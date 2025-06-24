package com.alok.iot.telemetry.dto;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class TemperaturePayload extends Payload{
    private double temperature;
    private String unit;
}
