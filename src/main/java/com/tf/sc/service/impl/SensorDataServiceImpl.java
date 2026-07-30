package com.tf.sc.service.impl;

import com.tf.sc.entity.SensorData;
import com.tf.sc.mapper.SensorDataMapper;
import com.tf.sc.service.SensorDataService;
import com.tf.sc.utils.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SensorDataServiceImpl implements SensorDataService {

    @Resource
    private SensorDataMapper sensorDataMapper;

    @Override
    public Map<String, Object> saveSensorData(SensorData sensorData) {
        if (sensorData.getDeviceId() == null || sensorData.getDeviceId().isEmpty()) {
            sensorData.setDeviceId("rk2206_01");
        }
        sensorDataMapper.insert(sensorData);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", sensorData.getId());
        result.put("deviceId", sensorData.getDeviceId());
        result.put("temperature", sensorData.getTemperature());
        result.put("humidity", sensorData.getHumidity());
        result.put("createTime", sensorData.getCreateTime());
        return result;
    }

    @Override
    public SensorData getLatestData(String deviceId) {
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = "rk2206_01";
        }
        return sensorDataMapper.selectLatest(deviceId);
    }

    @Override
    public List<SensorData> getRecentData(String deviceId, int limit) {
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = "rk2206_01";
        }
        if (limit <= 0 || limit > 100) {
            limit = 20;
        }
        return sensorDataMapper.selectRecent(deviceId, limit);
    }

    @Override
    public List<Map<String, Object>> getDeviceList() {
        return sensorDataMapper.selectDeviceList();
    }

    @Override
    public List<Map<String, Object>> getOnlineDevices() {
        return sensorDataMapper.selectOnlineDevices(5);
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = sensorDataMapper.selectStats();
        if (stats == null) {
            stats = new LinkedHashMap<>();
            stats.put("totalCount", 0);
            stats.put("deviceCount", 0);
            stats.put("todayCount", 0);
        }
        // 附加最新数据
        SensorData latest = sensorDataMapper.selectLatest("rk2206_01");
        if (latest != null) {
            Map<String, Object> latestMap = new LinkedHashMap<>();
            latestMap.put("temperature", latest.getTemperature());
            latestMap.put("humidity", latest.getHumidity());
            latestMap.put("time", latest.getCreateTime());
            stats.put("latestData", latestMap);
        }
        return stats;
    }

    @Override
    public int cleanOldData(int days) {
        String deadline = DateUtil.format(LocalDateTime.now().minusDays(days));
        return sensorDataMapper.deleteOlderThan(deadline);
    }
}