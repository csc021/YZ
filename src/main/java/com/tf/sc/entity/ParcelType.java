package com.tf.sc.entity;

import lombok.Data;

@Data
public class ParcelType {
    private Long id;
    private String name;
    private String icon;
    private Long defaultZoneId;
    private Integer sort;
    private String createdAt;
}