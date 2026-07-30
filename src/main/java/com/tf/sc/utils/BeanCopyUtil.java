package com.tf.sc.utils;

import org.springframework.beans.BeanUtils;

public final class BeanCopyUtil {
    private BeanCopyUtil() {
    }

    public static <T> T copy(Object source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }
}
