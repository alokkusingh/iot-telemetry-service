package com.alok.iot.telemetry.listeners;

import com.alok.iot.telemetry.dto.DeviceStatusPayload;
import com.alok.iot.telemetry.utils.PayloadUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StatusMessageListener implements IMqttMessageListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.debug("Message arrived on topic: {}", topic);
        log.debug("Message content: {}", new String(mqttMessage.getPayload()));

        DeviceStatusPayload payload;
        try {
            payload = objectMapper.readValue(mqttMessage.getPayload(), DeviceStatusPayload.class);
        } catch (Exception e) {
            log.error("Failed to parse MQTT message payload: {}", e.getMessage());
            return; // Exit if parsing fails
        }

        // example topic: "home/alok/status/esp32-general-purpose-1"
        String deviceIdFromTopic = topic.split("/")[3];
        try {
            PayloadUtils.validateDeviceId(deviceIdFromTopic, payload);
        } catch (IllegalArgumentException e) {
            log.error("Device ID validation failed: {}", e.getMessage());
            return; // Exit if validation fails
        }
    }
}
