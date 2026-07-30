package com.tf.sc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tf.sc.dto.request.ParcelInboundRequest;
import com.tf.sc.dto.request.ParcelOutboundRequest;
import com.tf.sc.dto.request.ParcelQueryRequest;
import com.tf.sc.entity.Parcel;
import com.tf.sc.mapper.ParcelMapper;
import com.tf.sc.service.impl.ParcelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcelServiceTest {
    @Spy
    @InjectMocks
    private ParcelServiceImpl parcelService;

    @Mock
    private ParcelMapper parcelMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(parcelService, "baseMapper", parcelMapper);
    }

    @Test
    void inboundInitializesParcelAndSavesIt() {
        doReturn(false).when(parcelService).checkTrackingNoExists(anyString());
        doReturn(true).when(parcelService).save(any(Parcel.class));
        ParcelInboundRequest request = new ParcelInboundRequest();
        request.setTrackingNo("YT123");
        request.setStationId(1L);
        request.setShelfId(2L);
        request.setShelfFloor(3);
        request.setCarrierId(4L);
        request.setRecipientPhone("13800138000");
        request.setOperatorId(5L);

        Parcel result = parcelService.inbound(request);

        assertEquals("YT123", result.getTrackingNo());
        assertEquals(0, result.getStatus());
        assertNotNull(result.getPickupCode());
        assertEquals(6, result.getPickupCode().length());
        assertNotNull(result.getInboundTime());
        assertNotNull(result.getCreatedAt());
        verify(parcelService).save(result);
    }

    @Test
    void outboundByIdMarksParcelAsPickedUp() {
        Parcel parcel = new Parcel();
        parcel.setId(10L);
        parcel.setStatus(0);
        parcel.setPickupRequested(1);
        ParcelOutboundRequest request = new ParcelOutboundRequest();
        request.setParcelId(10L);
        request.setOutboundBy(99L);
        doReturn(parcel).when(parcelService).getById(10L);
        when(parcelMapper.completeOutbound(eq(10L), eq(99L), anyString())).thenReturn(1);

        boolean success = parcelService.outbound(request);

        assertTrue(success);
        assertEquals(1, parcel.getStatus());
        assertEquals(99L, parcel.getOutboundBy());
        assertNotNull(parcel.getOutboundTime());
        verify(parcelMapper).completeOutbound(eq(10L), eq(99L), anyString());
    }

    @Test
    void outboundRejectsParcelWithoutPickupRequest() {
        Parcel parcel = new Parcel();
        parcel.setId(10L);
        parcel.setStatus(0);
        parcel.setPickupRequested(0);
        ParcelOutboundRequest request = new ParcelOutboundRequest();
        request.setParcelId(10L);
        request.setOutboundBy(99L);
        doReturn(parcel).when(parcelService).getById(10L);

        boolean success = parcelService.outbound(request);

        assertFalse(success);
        verifyNoInteractions(parcelMapper);
    }

    @Test
    void outboundReturnsFalseForAlreadyPickedUpParcel() {
        Parcel parcel = new Parcel();
        parcel.setId(10L);
        parcel.setStatus(1);
        ParcelOutboundRequest request = new ParcelOutboundRequest();
        request.setParcelId(10L);
        doReturn(parcel).when(parcelService).getById(10L);

        boolean success = parcelService.outbound(request);

        assertFalse(success);
        verifyNoInteractions(parcelMapper);
    }

    @Test
    void queryNormalizesInvalidPagination() {
        Page<Parcel> page = new Page<>(1, 10);
        doReturn(page).when(parcelService).page(any(Page.class), any(Wrapper.class));
        ParcelQueryRequest request = new ParcelQueryRequest();
        request.setPageNum(0L);
        request.setPageSize(-1L);

        Page<Parcel> result = parcelService.query(request);

        assertSame(page, result);
        assertEquals(1L, request.getPageNum());
        assertEquals(10L, request.getPageSize());
    }

    @Test
    void queryFiltersByPickupRequestState() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), "parcel-test"),
                Parcel.class);
        Page<Parcel> page = new Page<>(1, 10);
        doReturn(page).when(parcelService).page(any(Page.class), any(Wrapper.class));
        ParcelQueryRequest request = new ParcelQueryRequest();
        request.setPickupRequested(1);

        parcelService.query(request);

        ArgumentCaptor<Wrapper> wrapperCaptor = ArgumentCaptor.forClass(Wrapper.class);
        verify(parcelService).page(any(Page.class), wrapperCaptor.capture());
        LambdaQueryWrapper<Parcel> wrapper = (LambdaQueryWrapper<Parcel>) wrapperCaptor.getValue();
        assertTrue(wrapper.getSqlSegment().contains("pickup_requested"));
        assertTrue(wrapper.getParamNameValuePairs().containsValue(1));
    }
}
