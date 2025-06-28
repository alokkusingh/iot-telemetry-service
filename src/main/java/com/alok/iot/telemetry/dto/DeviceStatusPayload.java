package com.alok.iot.telemetry.dto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DeviceStatusPayload extends Payload {
    private String status;
    private String ipAddress; // optional

    public DeviceStatusPayload(String deviceId, Long epocheTime, String status, String ipAddress) {
        super(deviceId, epocheTime);
        this.status = status;
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "{" +
                "deviceId='" + super.getDeviceId() + '\'' +
                ", epocheTime='" + super.getEpochTime() + '\'' +
                ", status='" + status + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
