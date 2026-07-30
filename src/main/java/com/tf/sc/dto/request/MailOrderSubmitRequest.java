package com.tf.sc.dto.request;

import lombok.Data;

@Data
public class MailOrderSubmitRequest {
    private Long userId;
    private String senderName;
    private String senderPhone;
    private String senderProvince;
    private String senderCity;
    private String senderDistrict;
    private String senderAddress;
    private String receiverName;
    private String receiverPhone;
    private String receiverProvince;
    private String receiverCity;
    private String receiverDistrict;
    private String receiverAddress;
    private String itemName;
    private String itemType;
    private Double itemWeight;
    private Long carrierId;
    private Long stationId;
    private String remark;
}
