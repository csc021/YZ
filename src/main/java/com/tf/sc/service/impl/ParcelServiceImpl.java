package com.tf.sc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tf.sc.dto.request.ParcelInboundRequest;
import com.tf.sc.dto.request.ParcelOutboundRequest;
import com.tf.sc.dto.request.ParcelQueryRequest;
import com.tf.sc.dto.response.ParcelPrintResponse;
import com.tf.sc.dto.response.RangeStatsResponse;
import com.tf.sc.dto.response.TodayStatsResponse;
import com.tf.sc.entity.Parcel;
import com.tf.sc.entity.User;
import com.tf.sc.exception.BusinessException;
import com.tf.sc.mapper.ParcelMapper;
import com.tf.sc.entity.SensorReading;
import com.tf.sc.service.ParcelService;
import com.tf.sc.service.SensorReadingService;
import com.tf.sc.service.UserService;
import com.tf.sc.utils.BeanCopyUtil;
import com.tf.sc.utils.DateUtil;
import com.tf.sc.utils.PickupCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ParcelServiceImpl extends ServiceImpl<ParcelMapper, Parcel> implements ParcelService {

    @Autowired
    private UserService userService;

    @Autowired
    private SensorReadingService sensorReadingService;

    @Override
    @Transactional
    public Parcel inbound(ParcelInboundRequest request) {
        // 运单号唯一性校验：每个运单号仅可入库一次
        if (hasText(request.getTrackingNo()) && checkTrackingNoExists(request.getTrackingNo())) {
            throw new BusinessException("该运单号已入库，每个运单号仅可入库一次，请勿重复扫描");
        }
        Parcel parcel = new Parcel();
        BeanCopyUtil.copy(request, parcel);

        // 分区 + 物品类型
        parcel.setZoneId(request.getZoneId());
        parcel.setParcelTypeId(request.getParcelTypeId());

        // 记录入库时的传感器读数
        if (request.getZoneId() != null) {
            SensorReading latest = sensorReadingService.getLatestByZoneId(request.getZoneId());
            if (latest != null) {
                parcel.setSensorTemp(latest.getTemperature());
                parcel.setSensorHumidity(latest.getHumidity());
            }
        }

        parcel.setPickupCode(PickupCodeUtil.generate());
        parcel.setStatus(0);
        parcel.setInboundTime(DateUtil.nowStr());
        parcel.setCreatedAt(DateUtil.nowStr());
        save(parcel);
        return parcel;
    }

    @Override
    @Transactional
    public boolean outbound(ParcelOutboundRequest request) {
        Parcel parcel = null;
        if (request.getParcelId() != null) {
            parcel = getById(request.getParcelId());
        }
        if (parcel == null && hasText(request.getPickupCode())) {
            parcel = findPendingByCode(request.getPickupCode());
        }
        if (parcel == null && hasText(request.getTrackingNo())) {
            parcel = findPendingByTracking(request.getTrackingNo());
        }
        if (parcel == null && hasText(request.getRecipientPhone())) {
            parcel = findPendingByPhone(request.getRecipientPhone());
        }
        return completeOutbound(parcel, request.getOutboundBy());
    }

    @Override
    @Transactional
    public boolean outboundByCode(String pickupCode, Long outboundBy) {
        return completeOutbound(findPendingByCode(pickupCode), outboundBy);
    }

    @Override
    @Transactional
    public boolean outboundByTracking(String trackingNo, Long outboundBy) {
        return completeOutbound(findPendingByTracking(trackingNo), outboundBy);
    }

    @Override
    @Transactional
    public boolean outboundByPhone(String recipientPhone, Long outboundBy) {
        return completeOutbound(findPendingByPhone(recipientPhone), outboundBy);
    }

    @Override
    @Transactional
    public boolean selfPickup(Long parcelId, Long userId) {
        Parcel parcel = getById(parcelId);
        User user = userService.findById(userId);
        if (parcel == null || user == null || !Integer.valueOf(0).equals(parcel.getStatus())) {
            return false;
        }
        if (!parcel.getRecipientPhone().equals(user.getPhone())) {
            return false;
        }
        return completeOutbound(parcel, userId);
    }

    @Override
    public Page<Parcel> query(ParcelQueryRequest request) {
        if (request.getPageNum() == null || request.getPageNum() < 1) {
            request.setPageNum(1L);
        }
        if (request.getPageSize() == null || request.getPageSize() < 1) {
            request.setPageSize(10L);
        }
        LambdaQueryWrapper<Parcel> wrapper = buildQuery(request);
        wrapper.orderByDesc(Parcel::getCreatedAt);
        return page(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
    }

    @Override
    public List<ParcelPrintResponse> batchPrint(List<String> trackingNos, List<Long> parcelIds) {
        LambdaQueryWrapper<Parcel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Parcel::getStatus, 0);
        boolean hasIds = parcelIds != null && !parcelIds.isEmpty();
        boolean hasTrackingNos = trackingNos != null && !trackingNos.isEmpty();
        if (!hasIds && !hasTrackingNos) {
            return Collections.emptyList();
        }
        wrapper.and(w -> {
            if (hasIds) {
                w.in(Parcel::getId, parcelIds);
            }
            if (hasTrackingNos) {
                if (hasIds) {
                    w.or();
                }
                w.in(Parcel::getTrackingNo, trackingNos);
            }
        });
        List<ParcelPrintResponse> responses = new ArrayList<>();
        for (Parcel parcel : list(wrapper)) {
            ParcelPrintResponse response = new ParcelPrintResponse();
            response.setId(parcel.getId());
            response.setTrackingNo(parcel.getTrackingNo());
            response.setPickupCode(parcel.getPickupCode());
            response.setRecipientPhone(parcel.getRecipientPhone());
            response.setStationId(parcel.getStationId());
            response.setCarrierId(parcel.getCarrierId());
            response.setInboundTime(parcel.getInboundTime());
            responses.add(response);
        }
        return responses;
    }

    @Override
    public TodayStatsResponse todayStats(Long stationId) {
        String today = DateUtil.nowStr().substring(0, 10);
        TodayStatsResponse response = new TodayStatsResponse();
        response.setInboundCount(count(buildStationWrapper(stationId).likeRight(Parcel::getInboundTime, today)));
        response.setOutboundCount(count(buildStationWrapper(stationId).likeRight(Parcel::getOutboundTime, today)));
        response.setPendingCount(count(buildStationWrapper(stationId).eq(Parcel::getStatus, 0)));
        response.setRetainedCount(count(buildStationWrapper(stationId).eq(Parcel::getStatus, 2)));
        response.setExceptionCount(response.getRetainedCount());
        return response;
    }

    @Override
    public RangeStatsResponse rangeStats(Long stationId, String startTime, String endTime) {
        RangeStatsResponse response = new RangeStatsResponse();
        response.setInboundCount(count(applyRange(buildStationWrapper(stationId), Parcel::getInboundTime, startTime, endTime)));
        response.setOutboundCount(count(applyRange(buildStationWrapper(stationId), Parcel::getOutboundTime, startTime, endTime)));
        response.setPendingCount(count(buildStationWrapper(stationId).eq(Parcel::getStatus, 0)));
        response.setRetainedCount(count(buildStationWrapper(stationId).eq(Parcel::getStatus, 2)));
        return response;
    }

    @Override
    @Transactional
    public int markRetainedParcels(int retainedDays) {
        String deadline = DateUtil.format(LocalDateTime.now().minusDays(retainedDays));
        LambdaQueryWrapper<Parcel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Parcel::getStatus, 0).lt(Parcel::getInboundTime, deadline);
        long count = count(wrapper);
        if (count == 0) {
            return 0;
        }
        Parcel update = new Parcel();
        update.setStatus(2);
        update(update, wrapper);
        return Math.toIntExact(count);
    }

    @Override
    public boolean requestPickup(Long parcelId) {
        if (parcelId == null) return false;
        Parcel parcel = getById(parcelId);
        if (parcel == null || !Integer.valueOf(0).equals(parcel.getStatus())) {
            return false;
        }
        // 直接 SQL 更新，绕过 MyBatis-Plus 字段策略
        return baseMapper.requestPickup(parcelId) > 0;
    }

    @Override
    public boolean checkTrackingNoExists(String trackingNo) {
        if (!hasText(trackingNo)) {
            return false;
        }
        return count(new LambdaQueryWrapper<Parcel>()
                .eq(Parcel::getTrackingNo, trackingNo)) > 0;
    }

    private boolean completeOutbound(Parcel parcel, Long outboundBy) {
        if (parcel == null || !Integer.valueOf(0).equals(parcel.getStatus())) {
            return false;
        }
        parcel.setStatus(1);
        parcel.setOutboundBy(outboundBy);
        parcel.setOutboundTime(DateUtil.nowStr());
        return updateById(parcel);
    }

    private Parcel findPendingByCode(String pickupCode) {
        return getOne(new LambdaQueryWrapper<Parcel>()
                .eq(Parcel::getPickupCode, pickupCode)
                .eq(Parcel::getStatus, 0)
                .last("LIMIT 1"), false);
    }

    private Parcel findPendingByTracking(String trackingNo) {
        return getOne(new LambdaQueryWrapper<Parcel>()
                .eq(Parcel::getTrackingNo, trackingNo)
                .eq(Parcel::getStatus, 0)
                .last("LIMIT 1"), false);
    }

    private Parcel findPendingByPhone(String recipientPhone) {
        return getOne(new LambdaQueryWrapper<Parcel>()
                .eq(Parcel::getRecipientPhone, recipientPhone)
                .eq(Parcel::getStatus, 0)
                .orderByAsc(Parcel::getInboundTime)
                .last("LIMIT 1"), false);
    }

    private LambdaQueryWrapper<Parcel> buildQuery(ParcelQueryRequest request) {
        LambdaQueryWrapper<Parcel> wrapper = new LambdaQueryWrapper<>();
        if (hasText(request.getTrackingNo())) wrapper.like(Parcel::getTrackingNo, request.getTrackingNo());
        if (hasText(request.getPickupCode())) wrapper.eq(Parcel::getPickupCode, request.getPickupCode());
        if (request.getStationId() != null) wrapper.eq(Parcel::getStationId, request.getStationId());
        if (request.getShelfId() != null) wrapper.eq(Parcel::getShelfId, request.getShelfId());
        if (request.getCarrierId() != null) wrapper.eq(Parcel::getCarrierId, request.getCarrierId());
        if (hasText(request.getRecipientPhone())) wrapper.eq(Parcel::getRecipientPhone, request.getRecipientPhone());
        if (request.getStatus() != null) wrapper.eq(Parcel::getStatus, request.getStatus());
        if (request.getPickupRequested() != null) wrapper.eq(Parcel::getPickupRequested, request.getPickupRequested());
        if (hasText(request.getInboundStartTime())) wrapper.ge(Parcel::getInboundTime, request.getInboundStartTime());
        if (hasText(request.getInboundEndTime())) wrapper.le(Parcel::getInboundTime, request.getInboundEndTime());
        return wrapper;
    }

    private LambdaQueryWrapper<Parcel> buildStationWrapper(Long stationId) {
        LambdaQueryWrapper<Parcel> wrapper = new LambdaQueryWrapper<>();
        if (stationId != null) {
            wrapper.eq(Parcel::getStationId, stationId);
        }
        return wrapper;
    }

    private LambdaQueryWrapper<Parcel> applyRange(LambdaQueryWrapper<Parcel> wrapper,
                                                  com.baomidou.mybatisplus.core.toolkit.support.SFunction<Parcel, ?> column,
                                                  String startTime,
                                                  String endTime) {
        if (hasText(startTime)) wrapper.ge(column, startTime);
        if (hasText(endTime)) wrapper.le(column, endTime);
        return wrapper;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
