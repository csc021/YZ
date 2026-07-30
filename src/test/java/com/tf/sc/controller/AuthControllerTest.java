package com.tf.sc.controller;

import com.tf.sc.common.Result;
import com.tf.sc.common.ResultCode;
import com.tf.sc.dto.request.LoginRequest;
import com.tf.sc.dto.request.RegisterRequest;
import com.tf.sc.dto.response.LoginResponse;
import com.tf.sc.entity.User;
import com.tf.sc.service.RefreshTokenService;
import com.tf.sc.service.UserService;
import com.tf.sc.service.VerificationCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private VerificationCodeService verificationCodeService;

    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController();
        ReflectionTestUtils.setField(authController, "userService", userService);
        ReflectionTestUtils.setField(authController, "verificationCodeService", verificationCodeService);
        ReflectionTestUtils.setField(authController, "refreshTokenService", refreshTokenService);
    }

    @Test
    void registerRejectsInvalidPhoneBeforeCallingService() {
        RegisterRequest request = new RegisterRequest();
        request.setPhone("bad-phone");

        Result<?> result = authController.register(request);

        assertEquals(ResultCode.FAIL.getCode(), result.getCode());
        assertEquals("手机号格式不正确", result.getMessage());
        verifyNoInteractions(userService);
        verifyNoInteractions(refreshTokenService);
    }

    @Test
    void registerRejectsWhenCodeIsEmpty() {
        RegisterRequest request = new RegisterRequest();
        request.setPhone("13800138000");
        request.setEmail("test@test.com");

        Result<?> result = authController.register(request);

        assertEquals(ResultCode.FAIL.getCode(), result.getCode());
        assertEquals("验证码不能为空", result.getMessage());
    }

    @Test
    void loginReturnsUnauthorizedWhenCredentialsFail() {
        LoginRequest request = new LoginRequest();
        request.setPhone("13800138000");
        request.setPassword("bad");
        when(userService.login("13800138000", "bad")).thenReturn(null);

        Result<LoginResponse> result = authController.login(request);

        assertEquals(ResultCode.UNAUTHORIZED.getCode(), result.getCode());
        assertNull(result.getData());
        verify(refreshTokenService, never()).save(any());
    }

    @Test
    void loginReturnsTokenAndSavesRefreshToken() {
        LoginRequest request = new LoginRequest();
        request.setPhone("13800138000");
        request.setPassword("secret");
        User user = new User();
        user.setId(11L);
        user.setPhone("13800138000");
        user.setNickname("tester");
        user.setRole(0);
        when(userService.login("13800138000", "secret")).thenReturn(user);
        when(refreshTokenService.save(any())).thenReturn(true);

        Result<LoginResponse> result = authController.login(request);

        assertEquals(ResultCode.SUCCESS.getCode(), result.getCode());
        assertNotNull(result.getData());
        assertNotNull(result.getData().getToken());
        assertNotNull(result.getData().getRefreshToken());
        assertEquals("13800138000", result.getData().getUser().getPhone());
        verify(refreshTokenService).save(any());
    }

    @Test
    void logoutWithoutRefreshTokenIdIsSuccessfulNoop() {
        Result<Boolean> result = authController.logout(null);

        assertEquals(ResultCode.SUCCESS.getCode(), result.getCode());
        assertTrue(result.getData());
        verify(refreshTokenService, never()).removeById(any(java.io.Serializable.class));
    }
}
