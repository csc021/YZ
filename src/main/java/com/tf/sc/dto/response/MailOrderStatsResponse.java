package com.tf.sc.dto.response;

import lombok.Data;

@Data
public class MailOrderStatsResponse {
    private long submittedCount;
    private long acceptedCount;
    private long shippingCount;
    private long deliveredCount;
    private long exceptionCount;
}
