package com.tf.sc.entity;

import lombok.Data;

@Data
public class Station {
    /** 主键id */
    private Long id;
    /** 驿站名称 */
    private String name;
    /** 省份 */
    private String province;
    /** 城市 */
    private String city;
    /** 区/县 */
    private String district;
    /** 详细地址 */
    private String address;
    private String brand;
    /** 站长ID */
    private Long managerId;
    /** 驿站状态：0-停用 1-正常 */
    private Integer status;
    /** 创建时间 */
    private String createdAt;
}
