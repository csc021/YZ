package com.tf.sc.mapper;

import com.tf.sc.entity.Station;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StationMapper {
    int insert(Station station);

    int update(Station station);

    int updateBrand(@Param("id") Long id, @Param("brand") String brand);

    Station findById(@Param("id") Long id);

    List<Station> findAll();

    Station findByManagerId(@Param("managerId") Long managerId);

    List<Station> findByStatus(@Param("status") Integer status);

    int deleteById(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
