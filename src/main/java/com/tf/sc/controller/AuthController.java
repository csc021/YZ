package com.tf.sc.controller;

import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Constants;
import com.tf.sc.common.Result;
import com.tf.sc.dto.request.AdminLoginRequest;
import com.tf.sc.dto.request.AdminRegisterRequest;
import com.tf.sc.dto.request.LoginRequest;
import com.tf.sc.dto.request.PasswordChangeRequest;
import com.tf.sc.dto.request.PasswordResetRequest;
import com.tf.sc.dto.request.RefreshTokenRequest;
import com.tf.sc.dto.request.RegisterRequest;
import com.tf.sc.dto.response.LoginResponse;
import com.tf.sc.dto.response.UserInfoResponse;
import com.tf.sc.entity.RefreshToken;
import com.tf.sc.entity.User;
import com.tf.sc.service.RefreshTokenService;
import com.tf.sc.service.UserService;
import com.tf.sc.service.VerificationCodeService;
import com.tf.sc.utils.BeanCopyUtil;
import com.tf.sc.utils.DateUtil;
import com.tf.sc.utils.JwtUtil;
import com.tf.sc.utils.PhoneUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public Result<UserInfoResponse> register(@RequestBody RegisterRequest request) {
        if (!PhoneUtil.isValid(request.getPhone())) {
            return Result.error("手机号格式不正确");
        }
        if (!hasText(request.getEmail())) {
            return Result.error("邮箱不能为空");
        }
        if (!hasText(request.getCode())) {
            return Result.error("验证码不能为空");
        }
        if (!verificationCodeService.verifyCode(request.getEmail(), 1, request.getCode())) {
            return Result.error("验证码错误或已过期");
        }
        if (userService.findByPhone(request.getPhone()) != null) {
            return Result.error("手机号已注册");
        }
        if (userService.findByEmail(request.getEmail()) != null) {
            return Result.error("邮箱已注册");
        }
        User user = new User();
        BeanCopyUtil.copy(request, user);
        boolean success = userService.register(user);
        return success ? Result.success(toUserInfo(user)) : Result.error("注册失败");
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getPhone(), request.getPassword());
        if (user == null) {
            return Result.error(401, "手机号或密码错误，或账号已锁定");
        }
        return Result.success(toLoginResponse(user));
    }

    @PostMapping("/courier/login")
    public Result<LoginResponse> courierLogin(@RequestBody AdminLoginRequest request) {
        User user = userService.courierLogin(request.getUsername(), request.getPassword());
        if (user == null) {
            return Result.error(401, "用户名或密码错误");
        }
        return Result.success(toLoginResponse(user));
    }

    @PostMapping("/admin/login")
    public Result<LoginResponse> adminLogin(@RequestBody AdminLoginRequest request) {
        return courierLogin(request);
    }

//    @RequireRole({"2"})
    @PostMapping("/courier/register")
    public Result<UserInfoResponse> courierRegister(@RequestBody AdminRegisterRequest request) {

        // 站长登录后创建员工时前端会携带 token；公开自助注册仍需要邮箱验证码。
        if (!isStationMaster()) {
            if (!hasText(request.getCode())) {
                return Result.error("验证码不能为空");
            }
            if (!verificationCodeService.verifyCode(request.getEmail(), 3, request.getCode())) {
                return Result.error("验证码错误或已过期");
            }
        }

        return registerCourier(request);
    }

    @RequireRole({"2"})
    @PostMapping("/admin/register")
    public Result<UserInfoResponse> adminRegister(@RequestBody AdminRegisterRequest request) {
        return registerCourier(request);
    }

    @RequireRole({"0", "1", "2"})
    @PostMapping("/password/change")
    public Result<Boolean> changePassword(@RequestBody PasswordChangeRequest request) {
        Long userId = currentUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        boolean success = userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return success ? Result.success(true) : Result.error("原密码错误或用户不存在");
    }

    @PostMapping("/password/reset")
    public Result<Boolean> resetPassword(@RequestBody PasswordResetRequest request) {
        if (!verificationCodeService.verifyCode(request.getEmail(), 2, request.getCode())) {
            return Result.error("验证码错误或已过期");
        }
        User user = userService.findByEmail(request.getEmail());
        if (user == null) {
            return Result.error("该邮箱未注册");
        }
        boolean success = userService.resetPassword(user.getPhone(), request.getNewPassword());
        return success ? Result.success(true) : Result.error("重置失败");
    }

    @RequireRole({"0", "1", "2"})
    @PostMapping("/logout")
    public Result<Boolean> logout(@RequestParam(required = false) Long refreshTokenId) {
        if (refreshTokenId == null) {
            return Result.success(true);
        }
        return Result.success(refreshTokenService.removeById(refreshTokenId));
    }

    @PostMapping("/refresh")
    public Result<LoginResponse> refresh(@RequestBody RefreshTokenRequest request) {
        if (!hasText(request.getRefreshToken())) {
            return Result.error(401, "刷新令牌不能为空");
        }
        RefreshToken storedToken = refreshTokenService.getOne(
                Wrappers.<RefreshToken>lambdaQuery()
                        .eq(RefreshToken::getToken, request.getRefreshToken()));
        if (storedToken == null) {
            return Result.error(401, "刷新令牌无效");
        }
        if (DateUtil.isBeforeNow(storedToken.getExpireTime())) {
            return Result.error(401, "刷新令牌已过期，请重新登录");
        }
        User user = userService.findById(storedToken.getUserId());
        if (user == null) {
            return Result.error(401, "用户不存在");
        }
        // 删除旧 refresh token（轮转），防止重放攻击
        refreshTokenService.removeById(storedToken.getId());
        return Result.success(toLoginResponse(user));
    }

    private Result<UserInfoResponse> registerCourier(AdminRegisterRequest request) {
        if (!hasText(request.getUsername())) {
            return Result.error("用户名不能为空");
        }
        if (!hasText(request.getPassword())) {
            return Result.error("密码不能为空");
        }
        if (!hasText(request.getEmail())) {
            return Result.error("邮箱不能为空");
        }
        request.setEmail(request.getEmail().trim());
        if (!hasText(request.getEmployeeNo())) {
            // 未填工号则自动生成: KP + 时间戳后10位 + 4位随机数
            String ts = String.valueOf(System.currentTimeMillis());
            String suffix = String.format("%04d", (int)(Math.random() * 10000));
            request.setEmployeeNo("KP" + ts.substring(ts.length() - 10) + suffix);
        }
        if (userService.findByUsername(request.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        String phone = hasText(request.getPhone()) ? request.getPhone().trim() : request.getUsername().trim();
        if (!PhoneUtil.isValid(phone)) {
            return Result.error("手机号格式不正确");
        }
        if (userService.findByPhone(phone) != null) {
            return Result.error("手机号已注册");
        }
        if (userService.findByEmail(request.getEmail()) != null) {
            return Result.error("邮箱已注册");
        }
        User user = new User();
        BeanCopyUtil.copy(request, user);
        user.setPhone(phone);
        boolean success = userService.registerCourier(user);
        return success ? Result.success(toUserInfo(user)) : Result.error("注册失败");
    }

    private LoginResponse toLoginResponse(User user) {
        LoginResponse response = new LoginResponse();
        response.setToken(JwtUtil.createToken(String.valueOf(user.getId()), user.getRole()));
        response.setRefreshToken(createRefreshToken(user.getId()));
        response.setUser(toUserInfo(user));
        return response;
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
        String header = request.getHeader(Constants.AUTH_HEADER);
        if (header == null || !header.startsWith(Constants.JWT_PREFIX)) {
            return null;
        }
        try {
            return Long.valueOf(JwtUtil.parseSubject(header.substring(Constants.JWT_PREFIX.length())));
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private boolean isStationMaster() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }
        Object role = attributes.getRequest().getAttribute("role");
        if (Integer.valueOf(Constants.ROLE_STATION_MASTER).equals(role)) {
            return true;
        }
        String header = attributes.getRequest().getHeader(Constants.AUTH_HEADER);
        if (header == null || !header.startsWith(Constants.JWT_PREFIX)) {
            return false;
        }
        try {
            return Integer.valueOf(Constants.ROLE_STATION_MASTER)
                    .equals(JwtUtil.getRole(header.substring(Constants.JWT_PREFIX.length())));
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private String createRefreshToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(token);
        refreshToken.setExpireTime(DateUtil.format(LocalDateTime.now().plusDays(30)));
        refreshToken.setCreatedAt(DateUtil.nowStr());
        refreshTokenService.save(refreshToken);
        return token;
    }

    private UserInfoResponse toUserInfo(User user) {
        UserInfoResponse response = new UserInfoResponse();
        BeanCopyUtil.copy(user, response);
        return response;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
