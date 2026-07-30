package com.tf.sc.dto.request;

import lombok.Data;

@Data
public class ParcelInboundRequest {
    private String trackingNo;
    private Long stationId;
    private Long shelfId;
    private Integer shelfFloor;
    private Long carrierId;
    private String recipientPhone;
    private Long operatorId;
    private Long parcelTypeId;
    private Long zoneId;
}
