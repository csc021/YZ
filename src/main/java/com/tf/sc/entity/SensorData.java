package com.tf.sc.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * IoT 温湿度传感器数据实体 — 对应 t_sensor_data 表
 */
@Data
public class SensorData {
    private Long id;
    private String deviceId;
    private BigDecimal temperature;
    private BigDecimal humidity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}