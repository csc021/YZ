package com.tf.sc.entity;

import lombok.Data;

@Data
public class OperationLog {
    /** 主键ID */
    private Long id;
    /** 操作人ID */
    private Long userId;
    /** 功能模块 */
    private String module;
    /** 操作类型 */
    private String action;
    /** 操作描述 */
    private String description;
    /** IP地址 */
    private String ip;
    /** 创建时间 */
    private String createdAt;
}