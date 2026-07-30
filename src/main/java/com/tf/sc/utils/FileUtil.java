package com.tf.sc.utils;

import java.util.UUID;

public final class FileUtil {
    private FileUtil() {
    }

    public static String fileName(String originalName) {
        String suffix = "";
        if (originalName != null) {
            int index = originalName.lastIndexOf('.');
            if (index >= 0) {
                suffix = originalName.substring(index);
            }
        }
        return UUID.randomUUID().toString().replace("-", "") + suffix;
    }
}
