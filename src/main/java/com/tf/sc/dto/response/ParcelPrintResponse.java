package com.tf.sc.dto.response;

import lombok.Data;

@Data
public class ParcelPrintResponse {
    private Long id;
    private String trackingNo;
    private String pickupCode;
    private String recipientPhone;
    private Long stationId;
    private Long carrierId;
    private String inboundTime;
}
