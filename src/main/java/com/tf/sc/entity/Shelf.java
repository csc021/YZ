package com.tf.sc.entity;

import lombok.Data;

@Data
public class Shelf {
    /** 主键id */
    private Long id;
    /** 驿站ID */
    private Long stationId;
    /** 货架编码 A/B/C */
    private String code;
    /** 层数 */
    private Integer floorCount;
    /** 状态：0-停用 1-正常 */
    private Integer status;
    /** 创建时间 */
    private String createdAt;
}