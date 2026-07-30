package com.tf.sc.mapper;

import com.tf.sc.entity.SensorData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SensorDataMapper {

    int insert(SensorData sensorData);

    SensorData selectLatest(@Param("deviceId") String deviceId);

    List<SensorData> selectRecent(@Param("deviceId") String deviceId, @Param("limit") int limit);

    List<SensorData> selectAll();

    List<Map<String, Object>> selectDeviceList();

    List<Map<String, Object>> selectOnlineDevices(@Param("minutes") int minutes);

    Map<String, Object> selectStats();

    /** 删除指定时间之前的历史数据 */
    int deleteOlderThan(@Param("deadline") String deadline);
}