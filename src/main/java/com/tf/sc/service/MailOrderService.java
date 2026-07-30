package com.tf.sc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tf.sc.dto.request.MailOrderSubmitRequest;
import com.tf.sc.dto.response.MailOrderStatsResponse;
import com.tf.sc.entity.MailOrder;

public interface MailOrderService extends IService<MailOrder> {
    MailOrder submit(MailOrderSubmitRequest request, Long userId);

    Page<MailOrder> listMy(Long userId, Long pageNum, Long pageSize);

    Page<MailOrder> listStation(Long stationId, Integer status, Long pageNum, Long pageSize);

    boolean accept(Long id);

    boolean updateStatus(Long id, Integer status);

    MailOrderStatsResponse todayStats(Long stationId);
}
