package com.tf.sc.entity;

import lombok.Data;

@Data
public class RefreshToken {
    /** 主键id */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** Refresh Token */
    private String token;
    /** 过期时间 */
    private String expireTime;
    /** 创建时间 */
    private String createdAt;
}