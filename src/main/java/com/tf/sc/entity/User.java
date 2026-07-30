package com.tf.sc.entity;

import lombok.Data;

@Data
public class User {
    /** 主键id */
    private Long id;
    /** 用户名（管理员登录用） */
    private String username;
    /** 手机号 */
    private String phone;
    /** 邮箱 */
    private String email;
    /** BCrypt加密密码 */
    private String password;
    /** 昵称 */
    private String nickname;
    /** 头像URL */
    private String avatar;
    /** 角色：0-普通用户 1-快递员 2-站长 */
    private Integer role;
    /** 审核状态：0-待审核 1-已通过 2-已拒绝 */
    private Integer auditStatus;
    /** 拒绝理由 */
    private String rejectReason;
    /** 省份 */
    private String province;
    /** 城市 */
    private String city;
    /** 区/县 */
    private String district;
    /** 详细地址 */
    private String address;
    private String employeeNo;
    /** 密码错误次数 */
    private Integer loginFailCount;
    /** 锁定截止时间 */
    private String lockUntil;
    /** 注销状态：0-正常 1-冷静期 2-已删除 */
    private Integer deletionStatus;
    /** 申请注销时间 */
    private String deletionTime;
    /** 创建时间 */
    private String createdAt;
    /** 更新时间 */
    private String updatedAt;

}
