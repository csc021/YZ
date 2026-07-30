package com.tf.sc.mapper;

import com.tf.sc.entity.Shelf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShelfMapper {

    // 插入货架
    int insert(Shelf shelf);

    // 更新货架
    int update(Shelf shelf);

    // 根据ID查询
    Shelf findById(@Param("id") Long id);

    // 根据驿站ID查询所有货架
    List<Shelf> findByStationId(@Param("stationId") Long stationId);

    // 根据驿站ID和编码查询
    Shelf findByStationIdAndCode(@Param("stationId") Long stationId, @Param("code") String code);

    // 查询驿站下正常状态的货架
    List<Shelf> findNormalByStationId(@Param("stationId") Long stationId);

    // 删除货架（物理删除）
    int deleteById(@Param("id") Long id);

    // 更新货架状态
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}