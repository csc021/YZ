package com.tf.sc.dto.response;

import lombok.Data;

@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String nickname;
    private String avatar;
    private Integer role;
    private Integer auditStatus;
    private String rejectReason;
    private String province;
    private String city;
    private String district;
    private String address;
    private String employeeNo;
    private Integer deletionStatus;
    private String createdAt;
    private String updatedAt;
}
