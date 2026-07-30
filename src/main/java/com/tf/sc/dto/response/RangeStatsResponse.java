package com.tf.sc.dto.response;

import lombok.Data;

@Data
public class RangeStatsResponse {
    private long inboundCount;
    private long outboundCount;
    private long pendingCount;
    private long retainedCount;
}
