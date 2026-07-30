package com.tf.sc.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String refreshToken;
    private UserInfoResponse user;
}
