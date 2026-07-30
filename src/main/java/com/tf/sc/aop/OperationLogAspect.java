package com.tf.sc.aop;

import com.tf.sc.service.OperationLogService;
import com.tf.sc.utils.DateUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class OperationLogAspect {
    @Autowired
    private OperationLogService operationLogService;

    @Around("@annotation(com.tf.sc.annotation.OperationLog)")
    public Object record(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        com.tf.sc.annotation.OperationLog annotation = signature.getMethod().getAnnotation(com.tf.sc.annotation.OperationLog.class);
        com.tf.sc.entity.OperationLog log = new com.tf.sc.entity.OperationLog();
        log.setModule(joinPoint.getTarget().getClass().getSimpleName());
        log.setAction(signature.getMethod().getName());
        log.setDescription(annotation == null ? "" : annotation.value());
        log.setCreatedAt(DateUtil.nowStr());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            Object userId = attributes.getRequest().getAttribute("userId");
            if (userId instanceof Long) {
                log.setUserId((Long) userId);
            }
            log.setIp(attributes.getRequest().getRemoteAddr());
        }
        operationLogService.save(log);
        return result;
    }
}
