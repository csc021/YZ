package com.tf.sc.dto.request;

import lombok.Data;

@Data
public class AuditRequest {
    private Long userId;
    private Integer auditStatus;
    private String rejectReason;
}
