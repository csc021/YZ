package com.tf.sc.dto.request;

import lombok.Data;

@Data
public class ParcelQueryRequest {
    private String trackingNo;
    private String pickupCode;
    private Long stationId;
    private Long shelfId;
    private Long carrierId;
    private String recipientPhone;
    private String recipientName;
    private Integer status;
    private Integer pickupRequested;
    private String inboundStartTime;
    private String inboundEndTime;
    private Long pageNum = 1L;
    private Long pageSize = 10L;
}
