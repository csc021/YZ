package com.tf.sc.service;

import com.tf.sc.entity.SmsCode;

public interface SmsCodeService {

    /**
     * 发送验证码到邮箱
     * @param email 邮箱地址
     * @param type 1-注册 2-找回密码
     * @return true-发送成功
     */
    boolean sendSmsCode(String email, Integer type);

    /**
     * 校验验证码
     * @param email 邮箱地址
     * @param type 验证码类型
     * @param code 用户输入的验证码
     * @return true-验证通过
     */
    boolean verifySmsCode(String email, Integer type, String code);

    /**
     * 生成6位随机验证码
     */
    String generateCode();

    /**
     * 清理过期验证码（定时任务）
     */
    int cleanExpiredCodes();
}