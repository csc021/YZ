package com.tf.sc.controller;

import com.tf.sc.common.Result;
import com.tf.sc.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
public class VerificationCodeController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    /** 发送邮箱验证码 */
    @PostMapping("/send")
    public Result<Void> sendCode(@RequestParam String email, @RequestParam Integer type) {
        boolean success = verificationCodeService.sendVerificationCode(email, type);
        return success ? Result.success() : Result.error("验证码发送失败");
    }

    /** 校验邮箱验证码 */
    @PostMapping("/verify")
    public Result<Boolean> verifyCode(@RequestParam String email,
                                      @RequestParam Integer type,
                                      @RequestParam String code) {
        boolean valid = verificationCodeService.verifyCode(email, type, code);
        return valid ? Result.success(true) : Result.error("验证码错误或已过期");
    }
}
