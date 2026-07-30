package com.tf.sc.service.impl;

import com.tf.sc.entity.Shelf;
import com.tf.sc.mapper.ShelfMapper;
import com.tf.sc.service.ShelfService;
import com.tf.sc.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShelfServiceImpl implements ShelfService {

    @Autowired
    private ShelfMapper shelfMapper;

    @Override
    @Transactional
    public boolean createShelf(Shelf shelf) {
        // 检查编码是否已存在
        if (isCodeExist(shelf.getStationId(), shelf.getCode())) {
            return false;
        }
        shelf.setStatus(1); // 默认正常
        shelf.setCreatedAt(DateUtil.nowStr());
        return shelfMapper.insert(shelf) > 0;
    }

    @Override
    public boolean updateShelf(Shelf shelf) {
        // 检查编码是否与其他货架冲突
        Shelf exist = shelfMapper.findByStationIdAndCode(shelf.getStationId(), shelf.getCode());
        if (exist != null && !exist.getId().equals(shelf.getId())) {
            return false;
        }
        return shelfMapper.update(shelf) > 0;
    }

    @Override
    public Shelf getById(Long id) {
        return shelfMapper.findById(id);
    }

    @Override
    public List<Shelf> getByStationId(Long stationId) {
        return shelfMapper.findByStationId(stationId);
    }

    @Override
    public List<Shelf> getNormalByStationId(Long stationId) {
        return shelfMapper.findNormalByStationId(stationId);
    }

    @Override
    @Transactional
    public boolean deleteShelf(Long id) {
        return shelfMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean enableShelf(Long id) {
        return shelfMapper.updateStatus(id, 1) > 0;
    }

    @Override
    @Transactional
    public boolean disableShelf(Long id) {
        return shelfMapper.updateStatus(id, 0) > 0;
    }

    @Override
    public boolean isCodeExist(Long stationId, String code) {
        return shelfMapper.findByStationIdAndCode(stationId, code) != null;
    }
}