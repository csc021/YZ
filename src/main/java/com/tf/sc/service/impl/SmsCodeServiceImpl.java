package com.tf.sc.service.impl;

import com.tf.sc.entity.SmsCode;
import com.tf.sc.mapper.SmsCodeMapper;
import com.tf.sc.service.SmsCodeService;
import com.tf.sc.utils.DateUtil;
import com.tf.sc.utils.EmailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SmsCodeServiceImpl implements SmsCodeService {

    private static final Logger log = LoggerFactory.getLogger(SmsCodeServiceImpl.class);

    private static final int CODE_EXPIRE_MINUTES = 5; // 验证码5分钟有效
    private static final int CODE_LENGTH = 6;

    @Autowired
    private SmsCodeMapper smsCodeMapper;

    @Override
    @Transactional
    public boolean sendSmsCode(String email, Integer type) {
        String code = generateCode();
        String expireTime = DateUtil.format(LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES));

        SmsCode smsCode = new SmsCode();
        smsCode.setEmail(email);
        smsCode.setCode(code);
        smsCode.setType(type);
        smsCode.setIsUsed(0);
        smsCode.setExpireTime(expireTime);
        smsCode.setCreatedAt(DateUtil.nowStr());

        int result = smsCodeMapper.insert(smsCode);
        if (result > 0) {
            EmailUtil.send(email, code);
            log.info("验证码已生成: 邮箱={}, 验证码={}, 过期时间={}", email, code, expireTime);
            return true;
        }
        return false;
    }

    @Override
    public boolean verifySmsCode(String email, Integer type, String code) {
        SmsCode smsCode = smsCodeMapper.findByEmailTypeCode(email, type, code);
        if (smsCode == null) {
            log.warn("验证码不存在: 邮箱={}, 类型={}, 验证码={}", email, type, code);
            return false;
        }
        // 检查是否过期
        if (DateUtil.isBeforeNow(smsCode.getExpireTime())) {
            log.warn("验证码已过期: 邮箱={}, 过期时间={}", email, smsCode.getExpireTime());
            return false;
        }
        // 验证通过，标记为已使用（防止重复使用）
        smsCodeMapper.markAsUsed(smsCode.getId());
        return true;
    }

    @Override
    public String generateCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public int cleanExpiredCodes() {
        int count = smsCodeMapper.deleteExpired();
        log.info("清理过期验证码 {} 条", count);
        return count;
    }
}
