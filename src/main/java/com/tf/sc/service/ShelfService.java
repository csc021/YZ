package com.tf.sc.service;

import com.tf.sc.entity.Shelf;
import java.util.List;

public interface ShelfService {

    // 创建货架
    boolean createShelf(Shelf shelf);

    // 更新货架
    boolean updateShelf(Shelf shelf);

    // 根据ID查询
    Shelf getById(Long id);

    // 根据驿站ID查询所有货架
    List<Shelf> getByStationId(Long stationId);

    // 查询驿站下正常状态的货架
    List<Shelf> getNormalByStationId(Long stationId);

    // 删除货架
    boolean deleteShelf(Long id);

    // 启用货架
    boolean enableShelf(Long id);

    // 停用货架
    boolean disableShelf(Long id);

    // 检查某个驿站下货架编码是否已存在
    boolean isCodeExist(Long stationId, String code);
}