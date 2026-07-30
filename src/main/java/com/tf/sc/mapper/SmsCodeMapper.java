package com.tf.sc.mapper;

import com.tf.sc.entity.SmsCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SmsCodeMapper {

    // 插入验证码
    int insert(SmsCode smsCode);

    // 根据邮箱和类型查询最新的有效验证码
    SmsCode findLatestByEmailAndType(@Param("email") String email, @Param("type") Integer type);

    // 根据邮箱、类型、验证码查询
    SmsCode findByEmailTypeCode(@Param("email") String email,
                                @Param("type") Integer type,
                                @Param("code") String code);

    // 标记验证码为已使用（逻辑删除或更新状态）
    int markAsUsed(@Param("id") Long id);

    // 删除已过期的验证码（定时任务用）
    int deleteExpired();

    // 根据邮箱删除所有验证码（用户注销时用）
    int deleteByEmail(@Param("email") String email);
}