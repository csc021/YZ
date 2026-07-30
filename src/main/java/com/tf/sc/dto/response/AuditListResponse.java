package com.tf.sc.dto.response;

import lombok.Data;

@Data
public class AuditListResponse {
    private Long userId;
    private String username;
    private String phone;
    private String nickname;
    private Integer role;
    private Integer auditStatus;
    private String rejectReason;
    private String createdAt;
}
