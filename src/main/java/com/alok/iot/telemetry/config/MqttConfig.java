package com.alok.iot.telemetry.config;

import com.alok.iot.telemetry.dto.DeviceStatusPayload;
import com.alok.iot.telemetry.dto.Payload;
import com.alok.iot.telemetry.properties.IotProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Configuration
public class MqttConfig {

    private final IotProperties iotProperties;
    private final IMqttMessageListener statusMessageListener;
    private final IMqttMessageListener humidityMessageListener;
    private final IMqttMessageListener temperatureMessageListener;

    public MqttConfig(IotProperties iotProperties, IMqttMessageListener statusMessageListener, IMqttMessageListener humidityMessageListener, IMqttMessageListener temperatureMessageListener) {
        this.iotProperties = iotProperties;
        this.statusMessageListener = statusMessageListener;
        this.humidityMessageListener = humidityMessageListener;
        this.temperatureMessageListener = temperatureMessageListener;
    }

    /**
     * Creates an MQTT client bean that connects to the specified broker.
     *
     * @return an instance of IMqttClient
     * @throws MqttException if there is an error connecting to the broker
     */
    @Bean
    public IMqttClient mqttClient() throws MqttException, UnknownHostException {
        var brokerProperties = iotProperties.getMqtt().getBroker();
        String clientId = brokerProperties.getClientId();
        String brokerUrl = String.format("ssl://%s:%d",
                brokerProperties.getHost(),
                brokerProperties.getPort());

        IMqttClient client = new MqttClient(brokerUrl, clientId);
        log.info("Connecting to MQTT broker at: {}", brokerUrl);

        client.connect(createMqttConnectOptions(brokerProperties));
        log.info("Connected to MQTT broker with client ID: {}", clientId);

        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                log.info("Connection complete: {}-{}", b, s);
                try {
                    topicSubscribe(client);
                } catch (MqttException e) {
                    log.error("Failed to subscribe to topics after reconnection: {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                log.error("Connection lost: {}", cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                log.info("Message arrived on topic {}: {}", topic, new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                log.info("Delivery complete for token: {}", token.getMessageId());
            }
        });
        topicSubscribe(client);
        publish(client, brokerProperties.getStatusTopic(), new DeviceStatusPayload(brokerProperties.getClientId(), System.currentTimeMillis(), "online", Inet4Address.getLocalHost().getHostAddress()));

        return client;
    }

    public void publish(final IMqttClient mqttClient, final String topic, final Payload payload) throws MqttException {
        MqttMessage message = new MqttMessage();
        message.setQos(1);
        // AWS IoT Core doesn't support retained=true
        message.setRetained(false);
        message.setPayload(payload.toString().getBytes(StandardCharsets.UTF_8));

        mqttClient.publish( topic, message );
    }

    private MqttConnectOptions createMqttConnectOptions(IotProperties.Mqtt.Broker brokerProperties) throws UnknownHostException {
        MqttConnectOptions options = new MqttConnectOptions();

        options.setAutomaticReconnect(brokerProperties.isAutoReconnect());
        options.setCleanSession(brokerProperties.isCleanState());
        options.setConnectionTimeout(brokerProperties.getConnectionTimeout());
        options.setKeepAliveInterval(brokerProperties.getKeepAlive());
        options.setWill(brokerProperties.getStatusTopic(),
                new DeviceStatusPayload(brokerProperties.getClientId(),
                        System.currentTimeMillis(), "offline", Inet4Address.getLocalHost().getHostAddress())
        .toString().getBytes(StandardCharsets.UTF_8), 1, false);

        try {
            options.setSocketFactory(createSSLSocketFactory());
        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }

        return options;
    }

    private void topicSubscribe(IMqttClient mqttClient) throws MqttException {
        iotProperties.getMqtt().getDevices().forEach((deviceId, device) -> {
            try {
                if (device.getStatusTopic() != null) {
                    log.info("Subscribing to status topic: {}", device.getStatusTopic());
                    mqttClient.subscribe(device.getStatusTopic(), statusMessageListener);
                }
                if (device.getSensors() != null && device.getSensors().get("humidity") != null) {
                    log.info("Subscribing to humidity topic: {}", device.getSensors().get("humidity").getTopic());
                    mqttClient.subscribe(device.getSensors().get("humidity").getTopic(), humidityMessageListener);
                }
                if (device.getSensors() != null && device.getSensors().get("temperature") != null) {
                    log.info("Subscribing to temperature topic: {}", device.getSensors().get("temperature").getTopic());
                    mqttClient.subscribe(device.getSensors().get("temperature").getTopic(), temperatureMessageListener);
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Creates an SSL socket factory for secure MQTT connections.
     *
     * @return an SSLSocketFactory instance
     */
    private javax.net.ssl.SSLSocketFactory createSSLSocketFactory() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException {
        // Implement the logic to create and return an SSLSocketFactory
        // This typically involves loading the keystore and truststore
        // from the properties and configuring SSL context.
        // For simplicity, this method is left unimplemented here.

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(loadTrustStore().get());
        TrustManager[] trustManagers = tmf.getTrustManagers();

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(getKeyStore().get(), iotProperties.getMqtt().getBroker().getSecurity().getClientKeystorePassword().toCharArray()); // Assuming no client certificate is used
        KeyManager keyManagers[] = kmf.getKeyManagers();

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(keyManagers, trustManagers, null);
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        return sslSocketFactory;
    }


    private Optional<KeyStore> loadTrustStore() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            var file = new FileInputStream(iotProperties.getMqtt().getBroker().getSecurity().getCaCertPath());
            trustStore.setCertificateEntry("Custom CA", CertificateFactory.getInstance("X509")
                    .generateCertificate(file));

            return Optional.of(trustStore);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return Optional.empty();
    }

    private Optional<KeyStore> getKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance(iotProperties.getMqtt().getBroker().getSecurity().getClientKeystoreType());
            try (FileInputStream keyStoreFile = new FileInputStream(iotProperties.getMqtt().getBroker().getSecurity().getClientKeystorePath())) {
                keyStore.load(keyStoreFile, iotProperties.getMqtt().getBroker().getSecurity().getClientKeystorePassword().toCharArray());
            }
            return Optional.of(keyStore);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return Optional.empty();
    }

}