package com.tf.sc.entity;

import lombok.Data;

@Data
public class SmsCode {
    /** 主键id */
    private Long id;
    /** 邮箱 */
    private String email;
    /** 6位验证码 */
    private String code;
    /** 类型：1-注册 2-找回密码 */
    private Integer type;
    /** 是否已使用：0-未使用 1-已使用 */
    private Integer isUsed;
    /** 验证码过期时间 */
    private String expireTime;
    /** 记录创建时间 */
    private String createdAt;
}
