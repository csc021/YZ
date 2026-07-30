package com.tf.sc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tf.sc.entity.SensorReading;

import java.util.List;

public interface SensorReadingService extends IService<SensorReading> {
    /** ESP32 上报温湿度数据 */
    SensorReading receive(String zoneCode, Long stationId, Double temperature, Double humidity);

    /** 获取某分区最新读数 */
    SensorReading getLatestByZoneId(Long zoneId);

    /** 获取驿站所有分区的最新读数 */
    List<SensorReading> getLatestByStationId(Long stationId);

    /** 清理7天前数据 */
    int cleanOldData(int days);
}