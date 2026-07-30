package com.tf.sc.utils;

public final class PhoneUtil {
    private PhoneUtil() {
    }

    public static boolean isValid(String phone) {
        return phone != null && phone.matches("^1\\d{10}$");
    }
}
