package com.tf.sc.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ParcelPrintResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String trackingNo;
    private String pickupCode;
    private String recipientPhone;
    private Long stationId;
    private Long carrierId;
    private String inboundTime;
}
