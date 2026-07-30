package com.tf.sc.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ParcelBatchSelfPickupRequest {
    private List<Long> parcelIds;
    private String verificationCode;
}
