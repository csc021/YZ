package com.tf.sc.dto.response;

import lombok.Data;

@Data
public class ParcelResponse {
    private Long id;
    private String trackingNo;
    private String pickupCode;
    private Long stationId;
    private Long shelfId;
    private Integer shelfFloor;
    private Long carrierId;
    private String recipientPhone;
    private Integer status;
    private String inboundTime;
    private String outboundTime;
}
