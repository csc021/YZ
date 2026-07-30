package com.tf.sc.service.impl;

import com.tf.sc.common.Constants;
import com.tf.sc.entity.Station;
import com.tf.sc.entity.User;
import com.tf.sc.mapper.StationMapper;
import com.tf.sc.service.StationService;
import com.tf.sc.service.UserService;
import com.tf.sc.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private StationMapper stationMapper;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public boolean createStation(Station station) {
        User manager = station.getManagerId() == null ? null : userService.findById(station.getManagerId());
        if (manager == null || !Integer.valueOf(Constants.ROLE_STATION_MASTER).equals(manager.getRole())) {
            return false;
        }
        Station exist = stationMapper.findByManagerId(station.getManagerId());
        if (exist != null) {
            return false;
        }
        station.setStatus(1);
        station.setCreatedAt(DateUtil.nowStr());
        return stationMapper.insert(station) > 0;
    }

    @Override
    public boolean updateStation(Station station) {
        return stationMapper.update(station) > 0;
    }

    @Override
    public boolean updateBrand(Long stationId, String brand) {
        if (stationId == null || brand == null || brand.trim().isEmpty()) {
            return false;
        }
        if (!Arrays.asList(Constants.STATION_BRANDS).contains(brand)) {
            return false;
        }
        return stationMapper.updateBrand(stationId, brand) > 0;
    }

    @Override
    public Station getById(Long id) {
        return stationMapper.findById(id);
    }

    @Override
    public List<Station> getAllStations() {
        return stationMapper.findAll();
    }

    @Override
    public Station getByManagerId(Long managerId) {
        return stationMapper.findByManagerId(managerId);
    }

    @Override
    public List<Station> getByStatus(Integer status) {
        return stationMapper.findByStatus(status);
    }

    @Override
    @Transactional
    public boolean deleteStation(Long id) {
        return stationMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean enableStation(Long id) {
        return stationMapper.updateStatus(id, 1) > 0;
    }

    @Override
    @Transactional
    public boolean disableStation(Long id) {
        return stationMapper.updateStatus(id, 0) > 0;
    }
}
