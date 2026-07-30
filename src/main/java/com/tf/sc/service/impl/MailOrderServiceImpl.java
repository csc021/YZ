package com.tf.sc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tf.sc.dto.request.MailOrderSubmitRequest;
import com.tf.sc.dto.response.MailOrderStatsResponse;
import com.tf.sc.entity.MailOrder;
import com.tf.sc.mapper.MailOrderMapper;
import com.tf.sc.service.MailOrderService;
import com.tf.sc.utils.BeanCopyUtil;
import com.tf.sc.utils.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MailOrderServiceImpl extends ServiceImpl<MailOrderMapper, MailOrder> implements MailOrderService {

    @Override
    @Transactional
    public MailOrder submit(MailOrderSubmitRequest request, Long userId) {
        MailOrder order = new MailOrder();
        BeanCopyUtil.copy(request, order);
        order.setUserId(userId);
        order.setStatus(0);
        order.setCreatedAt(DateUtil.nowStr());
        order.setUpdatedAt(DateUtil.nowStr());
        save(order);
        return order;
    }

    @Override
    public Page<MailOrder> listMy(Long userId, Long pageNum, Long pageSize) {
        return page(new Page<>(normalizePageNum(pageNum), normalizePageSize(pageSize)),
                new LambdaQueryWrapper<MailOrder>()
                        .eq(MailOrder::getUserId, userId)
                        .orderByDesc(MailOrder::getCreatedAt));
    }

    @Override
    public Page<MailOrder> listStation(Long stationId, Integer status, Long pageNum, Long pageSize) {
        if (stationId == null) {
            return new Page<>(normalizePageNum(pageNum), normalizePageSize(pageSize));
        }
        LambdaQueryWrapper<MailOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailOrder::getStationId, stationId);
        if (status != null) {
            wrapper.eq(MailOrder::getStatus, status);
        }
        wrapper.orderByDesc(MailOrder::getCreatedAt);
        return page(new Page<>(normalizePageNum(pageNum), normalizePageSize(pageSize)), wrapper);
    }

    @Override
    @Transactional
    public boolean accept(Long id) {
        return updateStatus(id, 1);
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        if (status == null || status < 0 || status > 4) {
            return false;
        }
        MailOrder order = getById(id);
        if (order == null) {
            return false;
        }
        order.setStatus(status);
        order.setUpdatedAt(DateUtil.nowStr());
        return updateById(order);
    }

    @Override
    public MailOrderStatsResponse todayStats(Long stationId) {
        if (stationId == null) {
            return new MailOrderStatsResponse();
        }
        MailOrderStatsResponse response = new MailOrderStatsResponse();
        response.setSubmittedCount(count(todayWrapper(stationId).eq(MailOrder::getStatus, 0)));
        response.setAcceptedCount(count(todayWrapper(stationId).eq(MailOrder::getStatus, 1)));
        response.setShippingCount(count(todayWrapper(stationId).eq(MailOrder::getStatus, 2)));
        response.setDeliveredCount(count(todayWrapper(stationId).eq(MailOrder::getStatus, 3)));
        response.setExceptionCount(count(todayWrapper(stationId).eq(MailOrder::getStatus, 4)));
        return response;
    }

    private LambdaQueryWrapper<MailOrder> todayWrapper(Long stationId) {
        String today = DateUtil.nowStr().substring(0, 10);
        LambdaQueryWrapper<MailOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(MailOrder::getCreatedAt, today);
        wrapper.eq(MailOrder::getStationId, stationId);
        return wrapper;
    }

    private long normalizePageNum(Long pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private long normalizePageSize(Long pageSize) {
        return pageSize == null || pageSize < 1 ? 10 : pageSize;
    }
}
