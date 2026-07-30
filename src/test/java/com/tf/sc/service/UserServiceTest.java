package com.tf.sc.service;

import com.tf.sc.entity.User;
import com.tf.sc.mapper.UserMapper;
import com.tf.sc.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void registerInitializesUserAndEncryptsPassword() {
        User user = new User();
        user.setPhone("13800138000");
        user.setPassword("plain-password");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        boolean success = userService.register(user);

        assertTrue(success);
        assertTrue(encoder.matches("plain-password", user.getPassword()));
        assertEquals(0, user.getRole());
        assertEquals(1, user.getAuditStatus());
        assertEquals(0, user.getLoginFailCount());
        assertEquals(0, user.getDeletionStatus());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        verify(userMapper).insert(user);
    }

    @Test
    void loginReturnsUserAndResetsFailCountWhenPasswordMatches() {
        User user = new User();
        user.setId(7L);
        user.setPhone("13800138000");
        user.setPassword(encoder.encode("correct"));
        user.setLoginFailCount(3);
        when(userMapper.findByPhone("13800138000")).thenReturn(user);

        User result = userService.login("13800138000", "correct");

        assertSame(user, result);
        assertEquals(0, user.getLoginFailCount());
        verify(userMapper).updateLoginFailCount(7L, 0);
        verify(userMapper, never()).lockUser(any(), anyString());
    }

    @Test
    void loginLocksUserAfterFifthFailedPasswordAttempt() {
        User user = new User();
        user.setId(9L);
        user.setPassword(encoder.encode("correct"));
        user.setLoginFailCount(4);
        when(userMapper.findByPhone("13800138000")).thenReturn(user);

        User result = userService.login("13800138000", "wrong");

        assertNull(result);
        verify(userMapper).updateLoginFailCount(9L, 5);
        ArgumentCaptor<String> lockUntil = ArgumentCaptor.forClass(String.class);
        verify(userMapper).lockUser(eq(9L), lockUntil.capture());
        assertNotNull(lockUntil.getValue());
    }

    @Test
    void changePasswordRejectsWrongOldPassword() {
        User user = new User();
        user.setId(3L);
        user.setPassword(encoder.encode("old"));
        when(userMapper.findById(3L)).thenReturn(user);

        boolean success = userService.changePassword(3L, "bad", "new");

        assertFalse(success);
        verify(userMapper, never()).update(any(User.class));
    }
}
