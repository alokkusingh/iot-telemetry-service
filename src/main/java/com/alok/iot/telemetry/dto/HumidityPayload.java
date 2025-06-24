package com.alok.iot.telemetry.dto;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class HumidityPayload extends Payload {
    private int humidity;
    private String unit;
}
