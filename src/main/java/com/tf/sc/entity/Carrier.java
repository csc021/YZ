package com.tf.sc.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Carrier {
    /** 主键id */
    private Long id;
    /** 快递公司名称 */
    private String name;
    /** 公司编码（唯一） */
    private String code;
    /** 排序 */
    private Integer sort;
    /** 创建时间 */
    private LocalDateTime createdAt;
}