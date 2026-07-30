package com.tf.sc.entity;

import lombok.Data;

@Data
public class StationStaff {
    /** 主键id */
    private Long id;
    /** 驿站ID */
    private Long stationId;
    /** 员工用户ID */
    private Long userId;
    /** 创建时间 */
    private String createdAt;
}