package com.tf.sc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tf.sc.entity.SensorReading;
import com.tf.sc.entity.Zone;
import com.tf.sc.mapper.SensorReadingMapper;
import com.tf.sc.service.SensorReadingService;
import com.tf.sc.service.ZoneService;
import com.tf.sc.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorReadingServiceImpl extends ServiceImpl<SensorReadingMapper, SensorReading> implements SensorReadingService {

    @Autowired
    private ZoneService zoneService;

    @Autowired
    private SensorReadingMapper sensorReadingMapper;

    @Override
    public SensorReading receive(String zoneCode, Long stationId, Double temperature, Double humidity) {
        // 根据 zoneCode + stationId 查找分区，匹配编码
        List<Zone> zones = zoneService.getByStationId(stationId);
        Zone matched = zones.stream()
                .filter(z -> z.getCode().equalsIgnoreCase(zoneCode))
                .findFirst()
                .orElse(null);

        SensorReading reading = new SensorReading();
        reading.setZoneId(matched != null ? matched.getId() : null);
        reading.setStationId(stationId);
        reading.setTemperature(temperature);
        reading.setHumidity(humidity);
        reading.setReadAt(DateUtil.nowStr());
        reading.setCreatedAt(DateUtil.nowStr());
        save(reading);
        return reading;
    }

    @Override
    public SensorReading getLatestByZoneId(Long zoneId) {
        return sensorReadingMapper.selectLatestByZoneId(zoneId);
    }

    @Override
    public List<SensorReading> getLatestByStationId(Long stationId) {
        return sensorReadingMapper.selectLatestByStationId(stationId);
    }

    @Override
    public int cleanOldData(int days) {
        String deadline = DateUtil.format(LocalDateTime.now().minusDays(days));
        return sensorReadingMapper.deleteOlderThan(deadline);
    }
}