package com.tf.sc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {
    private static final Logger log = LoggerFactory.getLogger(EmailUtil.class);

    private static JavaMailSender mailSender;

    private static String from;

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        EmailUtil.mailSender = mailSender;
    }

    @Value("${spring.mail.username:}")
    public void setFrom(String from) {
        EmailUtil.from = from;
    }

    public static void send(String to, String code) {
        if (mailSender == null) {
            log.warn("MailSender 未配置，模拟发送邮件: to={}, code={}", to, code);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("验证码 - 驿站管理系统");
            message.setText("您的验证码是：" + code + "，有效期5分钟，请勿泄露给他人。");
            mailSender.send(message);
            log.info("邮件验证码已发送: to={}, code={}", to, code);
        } catch (Exception e) {
            log.error("邮件发送失败: to={}, error={}", to, e.getMessage());
        }
    }
}
