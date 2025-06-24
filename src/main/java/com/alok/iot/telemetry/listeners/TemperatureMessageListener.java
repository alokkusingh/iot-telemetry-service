package com.alok.iot.telemetry.listeners;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TemperatureMessageListener implements IMqttMessageListener {
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        log.debug("Message arrived on topic: {}", s);
        log.debug("Message content: {}", new String(mqttMessage.getPayload()));
    }
}
