package com.tf.sc.utils;

import java.util.concurrent.ThreadLocalRandom;

public final class PickupCodeUtil {
    private PickupCodeUtil() {
    }

    public static String generate() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    }
}
