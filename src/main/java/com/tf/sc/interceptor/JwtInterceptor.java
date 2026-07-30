package com.tf.sc.interceptor;

import com.tf.sc.common.Constants;
import com.tf.sc.utils.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader(Constants.AUTH_HEADER);
        if (header != null && header.startsWith(Constants.JWT_PREFIX)) {
            String token = header.substring(Constants.JWT_PREFIX.length());
            if (JwtUtil.isValid(token)) {
                request.setAttribute("userId", Long.valueOf(JwtUtil.getUserId(token)));
                request.setAttribute("role", JwtUtil.getRole(token));
                return true;
            }
        }
        // 未携带有效 token 也不在此拦截，由 AuthAspect 处理
        return true;
    }
}
