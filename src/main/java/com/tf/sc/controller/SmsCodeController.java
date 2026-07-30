package com.tf.sc.controller;

import com.tf.sc.common.Result;
import com.tf.sc.service.SmsCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
public class SmsCodeController {

    @Autowired
    private SmsCodeService smsCodeService;

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/send")
    public Result<Void> sendCode(@RequestParam String email, @RequestParam Integer type) {
        // type: 1-注册, 2-找回密码, 3-快递员注册
        boolean success = smsCodeService.sendSmsCode(email, type);
        return success ? Result.success() : Result.error("验证码发送失败");
    }

    /**
     * 校验邮箱验证码
     */
    @PostMapping("/verify")
    public Result<Boolean> verifyCode(@RequestParam String email,
                                      @RequestParam Integer type,
                                      @RequestParam String code) {
        boolean valid = smsCodeService.verifySmsCode(email, type, code);
        return valid ? Result.success(true) : Result.error("验证码错误或已过期");
    }
}
