package com.tf.sc.entity;

import lombok.Data;

@Data
public class MailOrder {
    private Long id;
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
    private Integer status;
    private String remark;
    private String createdAt;
    private String updatedAt;
}
