package com.tf.sc.entity;

import lombok.Data;

@Data
public class Parcel {
    /** 主键id */
    private Long id;
    /** 运单号 */
    private String trackingNo;
    /** 取件码 */
    private String pickupCode;
    /** 驿站ID */
    private Long stationId;
    /** 货架ID */
    private Long shelfId;
    /** 货架层号 */
    private Integer shelfFloor;
    /** 快递公司ID */
    private Long carrierId;
    /** 收件人手机号 */
    private String recipientPhone;
    /** 包裹状态：0-待取 1-已取 2-滞留 */
    private Integer status;
    /** 取件申请：0-未申请 1-已申请（用户提交申请后快递员方可出库） */
    private Integer pickupRequested;
    /** 入库时间 */
    private String inboundTime;
    /** 出库时间 */
    private String outboundTime;
    /** 出库操作人ID */
    private Long outboundBy;
    /** 入库操作人ID */
    private Long operatorId;
    /** 入库分区ID */
    private Long zoneId;
    /** 快递类型ID */
    private Long parcelTypeId;
    /** 入库时温度(°C) */
    private Double sensorTemp;
    /** 入库时湿度(%) */
    private Double sensorHumidity;
    /** 记录创建时间 */
    private String createdAt;
}