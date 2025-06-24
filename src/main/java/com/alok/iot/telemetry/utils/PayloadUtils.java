package com.alok.iot.telemetry.utils;


import com.alok.iot.telemetry.dto.Payload;

public class PayloadUtils {

    private PayloadUtils() {}

    public static void validateDeviceId(String deviceIdFromTopic, Payload payload) {
        if (deviceIdFromTopic == null || deviceIdFromTopic.isEmpty()) {
            throw new IllegalArgumentException("Device ID cannot be null or empty");
        }

        if (!deviceIdFromTopic.equals(payload.getDeviceId())) {
            throw new IllegalArgumentException("Device ID from topic does not match device ID in payload");
        }
    }
}
