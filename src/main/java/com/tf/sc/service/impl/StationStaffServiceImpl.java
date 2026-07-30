package com.tf.sc.service.impl;

import com.tf.sc.dto.response.StationStaffDetailResponse;
import com.tf.sc.entity.StationStaff;
import com.tf.sc.entity.User;
import com.tf.sc.mapper.StationStaffMapper;
import com.tf.sc.service.StationStaffService;
import com.tf.sc.service.UserService;
import com.tf.sc.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class StationStaffServiceImpl implements StationStaffService {

    @Autowired
    private StationStaffMapper stationStaffMapper;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public boolean addStaffToStation(Long stationId, Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return false;
        }
        // 普通用户（role=0）自动升级为快递员（role=1）
        if (Integer.valueOf(0).equals(user.getRole())) {
            user.setRole(1);
            user.setUpdatedAt(DateUtil.nowStr());
            userService.updateUser(user);
        } else if (!Integer.valueOf(1).equals(user.getRole())) {
            // 站长(2)和管理员(3)不能被添加为快递员
            return false;
        }
        // 检查该用户是否已被分配到其他驿站（跨驿站查重）
        StationStaff exist = stationStaffMapper.findByUserId(userId);
        if (exist != null) {
            return false;
        }
        StationStaff staff = new StationStaff();
        staff.setStationId(stationId);
        staff.setUserId(userId);
        staff.setCreatedAt(DateUtil.nowStr());
        return stationStaffMapper.insert(staff) > 0;
    }

    @Override
    @Transactional
    public boolean removeStaffFromStation(Long id) {
        return stationStaffMapper.deleteById(id) > 0;
    }

    @Override
    public List<StationStaff> getStaffByStationId(Long stationId) {
        return stationStaffMapper.findByStationId(stationId);
    }

    @Override
    public StationStaff getStationByUserId(Long userId) {
        return stationStaffMapper.findByUserId(userId);
    }

    @Override
    public boolean isStaffInStation(Long stationId, Long userId) {
        return stationStaffMapper.findByStationAndUser(stationId, userId) != null;
    }

    @Override
    public List<Long> getStaffUserIdsByStationId(Long stationId) {
        return stationStaffMapper.findUserIdsByStationId(stationId);
    }

    @Override
    public List<StationStaffDetailResponse> getStaffDetails(Long stationId) {
        List<StationStaff> staffList = stationId == null ? Collections.emptyList() : stationStaffMapper.findByStationId(stationId);
        List<StationStaffDetailResponse> responses = new ArrayList<>();
        for (StationStaff staff : staffList) {
            User user = userService.findById(staff.getUserId());
            StationStaffDetailResponse response = new StationStaffDetailResponse();
            response.setId(staff.getId());
            response.setStationId(staff.getStationId());
            response.setUserId(staff.getUserId());
            response.setCreatedAt(staff.getCreatedAt());
            if (user != null) {
                response.setUsername(user.getUsername());
                response.setNickname(user.getNickname());
                response.setPhone(user.getPhone());
                response.setEmail(user.getEmail());
                response.setAvatar(user.getAvatar());
                response.setEmployeeNo(user.getEmployeeNo());
                response.setRole(user.getRole());
                response.setAuditStatus(user.getAuditStatus());
            }
            responses.add(response);
        }
        return responses;
    }
}
