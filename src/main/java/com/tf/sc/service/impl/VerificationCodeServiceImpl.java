package com.tf.sc.service.impl;

import com.tf.sc.entity.VerificationCode;
import com.tf.sc.mapper.VerificationCodeMapper;
import com.tf.sc.service.VerificationCodeService;
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
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private static final Logger log = LoggerFactory.getLogger(VerificationCodeServiceImpl.class);

    private static final int CODE_EXPIRE_MINUTES = 5;
    private static final int CODE_LENGTH = 6;

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Override
    @Transactional
    public boolean sendVerificationCode(String email, Integer type) {
        String code = generateCode();
        String expireTime = DateUtil.format(LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES));

        VerificationCode vc = new VerificationCode();
        vc.setEmail(email);
        vc.setCode(code);
        vc.setType(type);
        vc.setIsUsed(0);
        vc.setExpireTime(expireTime);
        vc.setCreatedAt(DateUtil.nowStr());

        int result = verificationCodeMapper.insert(vc);
        if (result > 0) {
            EmailUtil.send(email, code);
            log.info("邮箱验证码已生成: 邮箱={}, 验证码={}, 过期时间={}", email, code, expireTime);
            return true;
        }
        return false;
    }

    @Override
    public boolean verifyCode(String email, Integer type, String code) {
        VerificationCode vc = verificationCodeMapper.findByEmailTypeCode(email, type, code);
        if (vc == null) {
            log.warn("验证码不存在: 邮箱={}, 类型={}, 验证码={}", email, type, code);
            return false;
        }
        if (DateUtil.isBeforeNow(vc.getExpireTime())) {
            log.warn("验证码已过期: 邮箱={}, 过期时间={}", email, vc.getExpireTime());
            return false;
        }
        verificationCodeMapper.markAsUsed(vc.getId());
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
        int count = verificationCodeMapper.deleteExpired();
        log.info("清理过期验证码 {} 条", count);
        return count;
    }
}
