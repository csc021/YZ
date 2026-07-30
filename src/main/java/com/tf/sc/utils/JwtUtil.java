package com.tf.sc.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class JwtUtil {

    private static final String SECRET = "c3RhdGlvbi1zZWNyZXQta2V5LWZvci1q d3Qtc2lnbmluZy0yMDI2";
    private static final long ACCESS_TOKEN_EXPIRE = 2 * 60 * 60 * 1000L; // 2 hours
    private static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.HS256;

    private JwtUtil() {
    }

    /**
     * 创建 JWT Token（HMAC-SHA256 签名，含签发时间与过期时间）
     *
     * @param userId 用户ID
     * @param role   用户角色
     * @return 签名的 JWT 字符串
     */
    public static String createToken(String userId, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE))
                .signWith(ALGORITHM, SECRET)
                .compact();
    }

    /**
     * 解析并验证 Token，返回 Claims；失败返回 null
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // token 过期 —— 仍然返回 claims 以便调用方自行判断
            return e.getClaims();
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * 从 Token 中获取用户 ID
     */
    public static String getUserId(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * 从 Token 中获取用户角色
     */
    public static Integer getRole(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        Object role = claims.get("role");
        return role instanceof Integer ? (Integer) role : null;
    }

    /**
     * Token 是否已过期
     */
    public static boolean isExpired(String token) {
        Claims claims = parseToken(token);
        return claims == null || claims.getExpiration().before(new Date());
    }

    /**
     * Token 是否有效（可解析且未过期）
     */
    public static boolean isValid(String token) {
        Claims claims = parseToken(token);
        return claims != null && !claims.getExpiration().before(new Date());
    }

    // ============ 兼容旧调用 ============

    /**
     * @deprecated 请使用 {@link #createToken(String, Integer)}
     */
    @Deprecated
    public static String createToken(String subject) {
        return createToken(subject, 0);
    }

    /**
     * @deprecated 请使用 {@link #getUserId(String)}
     */
    @Deprecated
    public static String parseSubject(String token) {
        return getUserId(token);
    }
}
