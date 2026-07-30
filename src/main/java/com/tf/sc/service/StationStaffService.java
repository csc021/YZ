package com.tf.sc.service;

import com.tf.sc.dto.response.StationStaffDetailResponse;
import com.tf.sc.entity.StationStaff;

import java.util.List;

public interface StationStaffService {
    boolean addStaffToStation(Long stationId, Long userId);

    boolean removeStaffFromStation(Long id);

    List<StationStaff> getStaffByStationId(Long stationId);

    StationStaff getStationByUserId(Long userId);

    boolean isStaffInStation(Long stationId, Long userId);

    List<Long> getStaffUserIdsByStationId(Long stationId);

    List<StationStaffDetailResponse> getStaffDetails(Long stationId);
}
