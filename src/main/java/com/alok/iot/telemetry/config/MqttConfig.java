package com.alok.iot.telemetry.config;

import com.alok.iot.telemetry.properties.IotProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.*;
import java.security.cert.CertificateFactory;
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
    public IMqttClient mqttClient() throws MqttException {
        String clientId = iotProperties.getMqtt().getBroker().getClientId();
        String brokerUrl = String.format("ssl://%s:%d",
                iotProperties.getMqtt().getBroker().getHost(),
                iotProperties.getMqtt().getBroker().getPort());

        MqttConnectOptions options = new MqttConnectOptions();
        try {
            options.setSocketFactory(createSSLSocketFactory());
        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
        options.setServerURIs(new String[]{brokerUrl});
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        IMqttClient client = new MqttClient(brokerUrl, clientId);
        log.info("Connecting to MQTT broker at: {}", brokerUrl);
        client.connect(options);
        log.info("Connected to MQTT broker with client ID: {}", clientId);
        client.setCallback(new MqttCallback() {
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

        return client;
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