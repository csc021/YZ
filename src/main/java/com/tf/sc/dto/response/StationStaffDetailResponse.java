package com.tf.sc.dto.response;

import lombok.Data;

@Data
public class StationStaffDetailResponse {
    private Long id;
    private Long stationId;
    private Long userId;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private String employeeNo;
    private Integer role;
    private Integer auditStatus;
    private String createdAt;
}
