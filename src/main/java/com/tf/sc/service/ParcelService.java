package com.tf.sc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tf.sc.dto.request.ParcelInboundRequest;
import com.tf.sc.dto.request.ParcelOutboundRequest;
import com.tf.sc.dto.request.ParcelQueryRequest;
import com.tf.sc.dto.response.ParcelPrintResponse;
import com.tf.sc.dto.response.RangeStatsResponse;
import com.tf.sc.dto.response.TodayStatsResponse;
import com.tf.sc.entity.Parcel;

import java.util.List;

public interface ParcelService extends IService<Parcel> {
    Parcel inbound(ParcelInboundRequest request);

    boolean outbound(ParcelOutboundRequest request);

    boolean outboundByCode(String pickupCode, Long outboundBy);

    boolean outboundByTracking(String trackingNo, Long outboundBy);

    boolean outboundByPhone(String recipientPhone, Long outboundBy);

    boolean selfPickup(Long parcelId, Long userId);

    boolean requestPickup(Long parcelId, Long userId);

    Page<Parcel> query(ParcelQueryRequest request);

    List<ParcelPrintResponse> batchPrint(List<String> trackingNos, List<Long> parcelIds);

    TodayStatsResponse todayStats(Long stationId);

    RangeStatsResponse rangeStats(Long stationId, String startTime, String endTime);

    int markRetainedParcels(int retainedDays);

    /**
     * 检查运单号是否已存在（精确匹配）
     * @param trackingNo 运单号
     * @return true=已存在 false=不存在
     */
    boolean checkTrackingNoExists(String trackingNo);
}
