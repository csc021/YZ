package com.tf.sc.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ParcelBatchPrintRequest {
    private List<String> trackingNos;
    private List<Long> parcelIds;
}
