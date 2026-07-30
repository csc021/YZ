package com.tf.sc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tf.sc.common.Result;
import com.tf.sc.common.ResultCode;
import com.tf.sc.dto.request.ParcelOutboundRequest;
import com.tf.sc.dto.request.ParcelQueryRequest;
import com.tf.sc.dto.response.TodayStatsResponse;
import com.tf.sc.entity.Parcel;
import com.tf.sc.service.ParcelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcelControllerTest {
    @Mock
    private ParcelService parcelService;

    private ParcelController parcelController;

    @BeforeEach
    void setUp() {
        parcelController = new ParcelController();
        ReflectionTestUtils.setField(parcelController, "parcelService", parcelService);
    }

    @Test
    void getByIdReturnsErrorWhenParcelMissing() {
        when(parcelService.getById(1L)).thenReturn(null);

        Result<Parcel> result = parcelController.getById(1L);

        assertEquals(ResultCode.FAIL.getCode(), result.getCode());
        assertEquals("包裹不存在", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void outboundReturnsErrorWhenServiceRejectsRequest() {
        ParcelOutboundRequest request = new ParcelOutboundRequest();
        request.setParcelId(1L);
        when(parcelService.outbound(request)).thenReturn(false);

        Result<Boolean> result = parcelController.outbound(request);

        assertEquals(ResultCode.FAIL.getCode(), result.getCode());
        assertEquals("出库失败，包裹不存在或取件信息不匹配", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void recipientParcelsBuildsQueryFromPathAndPagination() {
        Page<Parcel> page = new Page<>(2, 20);
        when(parcelService.query(any(ParcelQueryRequest.class))).thenReturn(page);

        Result<Page<Parcel>> result = parcelController.recipientParcels("13800138000", 2L, 20L);

        assertEquals(ResultCode.SUCCESS.getCode(), result.getCode());
        assertSame(page, result.getData());
        ArgumentCaptor<ParcelQueryRequest> captor = ArgumentCaptor.forClass(ParcelQueryRequest.class);
        verify(parcelService).query(captor.capture());
        assertEquals("13800138000", captor.getValue().getRecipientPhone());
        assertEquals(2L, captor.getValue().getPageNum());
        assertEquals(20L, captor.getValue().getPageSize());
    }

    @Test
    void todayStatsWrapsServiceResponse() {
        TodayStatsResponse stats = new TodayStatsResponse();
        stats.setInboundCount(3);
        when(parcelService.todayStats(6L)).thenReturn(stats);

        Result<TodayStatsResponse> result = parcelController.todayStats(6L);

        assertEquals(ResultCode.SUCCESS.getCode(), result.getCode());
        assertSame(stats, result.getData());
    }
}
