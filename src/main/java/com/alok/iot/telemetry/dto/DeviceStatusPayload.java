package com.alok.iot.telemetry.dto;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DeviceStatusPayload extends Payload {
    private String status;
    private String lastSeen;
    private String ipAddress; // optional
}
