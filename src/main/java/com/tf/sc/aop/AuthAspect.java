package com.tf.sc.aop;

import com.tf.sc.annotation.RequireRole;
import com.tf.sc.entity.User;
import com.tf.sc.exception.ForbiddenException;
import com.tf.sc.exception.UnauthorizedException;
import com.tf.sc.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class AuthAspect {
    @Autowired
    private UserService userService;

    @Around("@annotation(com.tf.sc.annotation.RequireRole) || @within(com.tf.sc.annotation.RequireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        RequireRole requireRole = getRequireRole(joinPoint);
        if (requireRole == null || requireRole.value().length == 0) {
            return joinPoint.proceed();
        }
        Long userId = currentUserId();
        if (userId == null) {
            throw new UnauthorizedException("未登录");
        }
        User user = userService.findById(userId);
        if (user == null) {
            throw new UnauthorizedException("用户不存在");
        }
        boolean allowed = Arrays.stream(requireRole.value())
                .anyMatch(role -> role.equals(String.valueOf(user.getRole())));
        if (!allowed) {
            throw new ForbiddenException("无权限访问");
        }
        return joinPoint.proceed();
    }

    private RequireRole getRequireRole(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequireRole methodAnnotation = signature.getMethod().getAnnotation(RequireRole.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return joinPoint.getTarget().getClass().getAnnotation(RequireRole.class);
    }

    private Long currentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        Object value = request.getAttribute("userId");
        if (value instanceof Long) {
            return (Long) value;
        }
        return null;
    }
}
