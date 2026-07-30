package com.tf.sc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SmsUtil {
    private static final Logger log = LoggerFactory.getLogger(SmsUtil.class);

    private SmsUtil() {
    }

    public static void send(String phone, String code) {
        log.info("模拟发送短信: phone={}, code={}", phone, code);
    }
}
