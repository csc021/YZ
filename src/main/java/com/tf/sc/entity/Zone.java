package com.tf.sc.entity;

import lombok.Data;

@Data
public class Zone {
    private Long id;
    private Long stationId;
    private String code;
    private String name;
    private Double tempMin;
    private Double tempMax;
    private Double humidityMin;
    private Double humidityMax;
    private Long shelfId;
    private Integer status;
    private String createdAt;
}