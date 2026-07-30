package com.tf.sc.dto.request;

import lombok.Data;

@Data
public class SmsCodeRequest {
    private String email;
    private Integer type;
    private String code;
}
