package com.tf.sc.dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private Long id;
    private String email;
    private String nickname;
    private String avatar;
    private String province;
    private String city;
    private String district;
    private String address;
    private String employeeNo;
}
