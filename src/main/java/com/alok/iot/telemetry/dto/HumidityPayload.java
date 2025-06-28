package com.alok.iot.telemetry.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@Data
public class HumidityPayload extends Payload {
    private double humidity;
    private String unit;

    public HumidityPayload(String deviceId, Long epocheTime, double humidity, String unit) {
        super(deviceId, epocheTime);
        this.humidity = humidity;
        this.unit = unit;
    }
}
