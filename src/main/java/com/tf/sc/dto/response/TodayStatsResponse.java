package com.tf.sc.dto.response;

import lombok.Data;

@Data
public class TodayStatsResponse {
    private long inboundCount;
    private long outboundCount;
    private long pendingCount;
    private long retainedCount;
    private long mailedCount;
    private long exceptionCount;
}
