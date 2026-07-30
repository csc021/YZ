package com.tf.sc.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phone;
    private String email;
    private String password;
    private String nickname;
    private String avatar;
    private String province;
    private String city;
    private String district;
    private String address;
    /** 邮箱验证码 */
    private String code;
}
