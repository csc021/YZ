package com.tf.sc.dto.request;

import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String phone;
    private String employeeNo;

    public String code;
}
