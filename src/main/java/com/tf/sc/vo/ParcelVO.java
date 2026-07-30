package com.tf.sc.vo;

import lombok.Data;

@Data
public class ParcelVO {
    private Long id;
    private String trackingNo;
    private String pickupCode;
    private Long stationId;
    private Long shelfId;
    private Integer shelfFloor;
    private Long carrierId;
    private String recipientPhone;
    private Integer status;
}
