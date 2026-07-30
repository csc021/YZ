package com.tf.sc.dto.request;

import lombok.Data;

@Data
public class ParcelSelfPickupRequest {
    private String trackingNo;
    private String verificationCode;
}
