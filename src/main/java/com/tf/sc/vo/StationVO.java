package com.tf.sc.vo;

import lombok.Data;

@Data
public class StationVO {
    private Long id;
    private String name;
    private String province;
    private String city;
    private String district;
    private String address;
    private Long managerId;
    private Integer status;
}
