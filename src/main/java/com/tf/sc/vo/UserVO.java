package com.tf.sc.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    private Integer role;
    private Integer auditStatus;
}
