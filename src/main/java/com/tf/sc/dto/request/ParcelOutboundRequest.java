package com.tf.sc.dto.request;

import lombok.Data;

@Data
public class ParcelOutboundRequest {
    private Long parcelId;
    private String trackingNo;
    private String pickupCode;
    private String recipientPhone;
    private Long outboundBy;
}
