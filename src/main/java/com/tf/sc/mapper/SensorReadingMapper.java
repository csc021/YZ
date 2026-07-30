package com.tf.sc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tf.sc.entity.SensorReading;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SensorReadingMapper extends BaseMapper<SensorReading> {

    SensorReading selectLatestByZoneId(@Param("zoneId") Long zoneId);

    List<SensorReading> selectLatestByStationId(@Param("stationId") Long stationId);

    /** 删除7天前的历史数据 */
    int deleteOlderThan(@Param("deadline") String deadline);
}