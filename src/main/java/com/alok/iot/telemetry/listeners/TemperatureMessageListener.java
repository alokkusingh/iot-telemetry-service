package com.alok.iot.telemetry.listeners;

import com.alok.iot.telemetry.dto.HumidityPayload;
import com.alok.iot.telemetry.utils.PayloadUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TemperatureMessageListener implements IMqttMessageListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.debug("Message arrived on topic: {}", topic);
        log.debug("Message content: {}", new String(mqttMessage.getPayload()));

        var payload = objectMapper.readValue(mqttMessage.getPayload(), HumidityPayload.class);

        // example topic: "home/alok/telemetry/temperature/esp32-general-purpose-1"
        String deviceIdFromTopic = topic.split("/")[4];
        try {
            PayloadUtils.validateDeviceId(deviceIdFromTopic, payload);
        } catch (IllegalArgumentException e) {
            log.error("Device ID validation failed: {}", e.getMessage());
            return; // Exit if validation fails
        }
    }
}
