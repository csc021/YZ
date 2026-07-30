package com.tf.sc.mapper;

import com.tf.sc.entity.StationStaff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StationStaffMapper {
    int insert(StationStaff staff);

    int deleteById(@Param("id") Long id);

    int deleteByUserId(@Param("userId") Long userId);

    List<StationStaff> findByStationId(@Param("stationId") Long stationId);

    StationStaff findByUserId(@Param("userId") Long userId);

    StationStaff findByStationAndUser(@Param("stationId") Long stationId, @Param("userId") Long userId);

    List<Long> findUserIdsByStationId(@Param("stationId") Long stationId);
}
