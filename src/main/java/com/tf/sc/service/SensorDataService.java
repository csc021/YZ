package com.tf.sc.service;

import com.tf.sc.entity.SensorData;

import java.util.List;
import java.util.Map;

public interface SensorDataService {

    Map<String, Object> saveSensorData(SensorData sensorData);

    SensorData getLatestData(String deviceId);

    List<SensorData> getRecentData(String deviceId, int limit);

    List<Map<String, Object>> getDeviceList();

    List<Map<String, Object>> getOnlineDevices();

    Map<String, Object> getStats();

    /** 清理N天前的历史数据 */
    int cleanOldData(int days);
}