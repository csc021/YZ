package com.tf.sc.entity;

import lombok.Data;

@Data
public class SensorReading {
    private Long id;
    private Long zoneId;
    private Long stationId;
    private Double temperature;
    private Double humidity;
    private String readAt;
    private String createdAt;
}