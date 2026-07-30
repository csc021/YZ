package com.tf.sc.common;

public final class Constants {
    public static final String AUTH_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final String REDIS_TOKEN_KEY = "ks:token:";
    public static final String REDIS_EMAIL_CODE_KEY = "ks:emailCode:";

    public static final int ROLE_USER = 0;
    public static final int ROLE_COURIER = 1;
    public static final int ROLE_STATION_MASTER = 2;

    public static final String[] STATION_BRANDS = {
            "cainiao", "jd", "jitu", "ems", "yto", "sto", "yunda", "sf", "zto"
    };

    private Constants() {
    }
}
