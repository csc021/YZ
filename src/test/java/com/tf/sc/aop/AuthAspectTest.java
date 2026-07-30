package com.tf.sc.aop;

import com.tf.sc.annotation.RequireRole;
import com.tf.sc.entity.User;
import com.tf.sc.exception.ForbiddenException;
import com.tf.sc.exception.UnauthorizedException;
import com.tf.sc.service.UserService;
import com.tf.sc.utils.JwtUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthAspectTest {
    @Mock
    private UserService userService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature signature;

    private AuthAspect authAspect;

    @BeforeEach
    void setUp() throws Exception {
        authAspect = new AuthAspect();
        ReflectionTestUtils.setField(authAspect, "userService", userService);
        Method method = SecuredMethod.class.getDeclaredMethod("execute");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void rawTokenIsNotTrustedWithoutValidatedRequestAttribute() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + JwtUtil.createToken("7", 2));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertThrows(UnauthorizedException.class, () -> authAspect.checkRole(joinPoint));
        verify(userService, never()).findById(7L);
    }

    @Test
    void authenticatedUserWithWrongRoleIsForbidden() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 7L);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        User user = new User();
        user.setId(7L);
        user.setRole(1);
        when(userService.findById(7L)).thenReturn(user);

        assertThrows(ForbiddenException.class, () -> authAspect.checkRole(joinPoint));
    }

    static class SecuredMethod {
        @RequireRole({"2"})
        public void execute() {
        }
    }
}
