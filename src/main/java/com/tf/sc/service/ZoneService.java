package com.tf.sc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tf.sc.entity.Zone;

import java.util.List;

public interface ZoneService extends IService<Zone> {
    List<Zone> getByStationId(Long stationId);
    List<Zone> getNormalByStationId(Long stationId);
    boolean createZone(Zone zone);
    boolean updateZone(Zone zone);
    boolean deleteZone(Long id);
    boolean enableZone(Long id);
    boolean disableZone(Long id);
}