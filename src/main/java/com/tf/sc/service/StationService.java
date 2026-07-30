package com.tf.sc.service;

import com.tf.sc.entity.Station;

import java.util.List;

public interface StationService {
    boolean createStation(Station station);

    boolean updateStation(Station station);

    boolean updateBrand(Long stationId, String brand);

    Station getById(Long id);

    List<Station> getAllStations();

    Station getByManagerId(Long managerId);

    List<Station> getByStatus(Integer status);

    boolean deleteStation(Long id);

    boolean enableStation(Long id);

    boolean disableStation(Long id);
}
