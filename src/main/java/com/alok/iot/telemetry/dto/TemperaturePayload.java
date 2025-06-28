package com.alok.iot.telemetry.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@Data
public class TemperaturePayload extends Payload{
    private double temperature;
    private String unit;

    public TemperaturePayload(String deviceId, Long epocheTime, double temperature, String unit) {
        super(deviceId, epocheTime);
        this.temperature = temperature;
        this.unit = unit;
    }
}
