package com.tf.sc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tf.sc.entity.Zone;
import com.tf.sc.mapper.ZoneMapper;
import com.tf.sc.service.ZoneService;
import com.tf.sc.utils.DateUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneServiceImpl extends ServiceImpl<ZoneMapper, Zone> implements ZoneService {

    @Override
    public List<Zone> getByStationId(Long stationId) {
        return list(new LambdaQueryWrapper<Zone>()
                .eq(Zone::getStationId, stationId)
                .orderByAsc(Zone::getCode));
    }

    @Override
    public List<Zone> getNormalByStationId(Long stationId) {
        return list(new LambdaQueryWrapper<Zone>()
                .eq(Zone::getStationId, stationId)
                .eq(Zone::getStatus, 1)
                .orderByAsc(Zone::getCode));
    }

    @Override
    public boolean createZone(Zone zone) {
        zone.setStatus(1);
        zone.setCreatedAt(DateUtil.nowStr());
        return save(zone);
    }

    @Override
    public boolean updateZone(Zone zone) {
        Zone existing = getById(zone.getId());
        if (existing == null) return false;
        if (zone.getCode() != null) existing.setCode(zone.getCode());
        if (zone.getName() != null) existing.setName(zone.getName());
        if (zone.getTempMin() != null) existing.setTempMin(zone.getTempMin());
        if (zone.getTempMax() != null) existing.setTempMax(zone.getTempMax());
        if (zone.getHumidityMin() != null) existing.setHumidityMin(zone.getHumidityMin());
        if (zone.getHumidityMax() != null) existing.setHumidityMax(zone.getHumidityMax());
        if (zone.getShelfId() != null) existing.setShelfId(zone.getShelfId());
        if (zone.getStationId() != null) existing.setStationId(zone.getStationId());
        return updateById(existing);
    }

    @Override
    public boolean deleteZone(Long id) {
        return removeById(id);
    }

    @Override
    public boolean enableZone(Long id) {
        Zone zone = getById(id);
        if (zone == null) return false;
        zone.setStatus(1);
        return updateById(zone);
    }

    @Override
    public boolean disableZone(Long id) {
        Zone zone = getById(id);
        if (zone == null) return false;
        zone.setStatus(0);
        return updateById(zone);
    }
}