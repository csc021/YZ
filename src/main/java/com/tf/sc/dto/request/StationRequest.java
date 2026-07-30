package com.tf.sc.dto.request;

import lombok.Data;

@Data
public class StationRequest {
    private Long id;
    private String name;
    private String province;
    private String city;
    private String district;
    private String address;
    private Long managerId;
    private Integer status;
}
