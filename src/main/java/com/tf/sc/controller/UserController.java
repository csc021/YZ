package com.tf.sc.controller;

import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Constants;
import com.tf.sc.common.Result;
import com.tf.sc.dto.request.UserUpdateRequest;
import com.tf.sc.dto.response.UserInfoResponse;
import com.tf.sc.entity.User;
import com.tf.sc.service.UserService;
import com.tf.sc.utils.BeanCopyUtil;
import com.tf.sc.utils.FileUtil;
import com.tf.sc.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RequireRole({"0", "1", "2"})
@RestController
@RequestMapping("/users")
public class UserController {
    private static final Path UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath();

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public Result<UserInfoResponse> getById(@PathVariable Long id) {
        User user = userService.findById(id);
        return user == null ? Result.error("User not found") : Result.success(toUserInfo(user));
    }

    @GetMapping("/phone/{phone}")
    public Result<UserInfoResponse> getByPhone(@PathVariable String phone) {
        User user = userService.findByPhone(phone);
        return user == null ? Result.error("User not found") : Result.success(toUserInfo(user));
    }

    @GetMapping("/email/{email}")
    public Result<UserInfoResponse> getByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return user == null ? Result.error("User not found") : Result.success(toUserInfo(user));
    }

    @GetMapping("/list")
    public Result<List<UserInfoResponse>> list() {
        List<UserInfoResponse> users = userService.findAll().stream()
                .map(this::toUserInfo)
                .collect(Collectors.toList());
        return Result.success(users);
    }

    @PutMapping({"", "/update"})
    public Result<UserInfoResponse> update(@RequestBody UserUpdateRequest request) {
        Long currentUserId = currentUserId();
        if (request.getId() == null) {
            request.setId(currentUserId);
        }
        if (!isStationMaster() && (request.getId() == null || !request.getId().equals(currentUserId))) {
            return Result.error(403, "Forbidden");
        }
        User user = userService.findById(request.getId());
        if (user == null) {
            return Result.error("User not found");
        }
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getAvatar() != null) user.setAvatar(request.getAvatar());
        if (request.getProvince() != null) user.setProvince(request.getProvince());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getDistrict() != null) user.setDistrict(request.getDistrict());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getEmployeeNo() != null && isStationMaster()) user.setEmployeeNo(request.getEmployeeNo());
        boolean success = userService.updateUser(user);
        return success ? Result.success(toUserInfo(user)) : Result.error("Update failed");
    }

    @PostMapping("/avatar")
    public Result<String> avatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        Long userId = currentUserId();
        if (userId == null) {
            return Result.error(401, "Unauthorized");
        }
        if (file == null || file.isEmpty()) {
            return Result.error("File is required");
        }
        Files.createDirectories(UPLOAD_DIR);
        String fileName = FileUtil.fileName(file.getOriginalFilename());
        Path target = UPLOAD_DIR.resolve(fileName).normalize();
        if (!target.startsWith(UPLOAD_DIR)) {
            return Result.error("Invalid file path");
        }
        file.transferTo(target.toFile());
        String url = request.getContextPath() + "/files/" + fileName;
        boolean success = userService.updateAvatar(userId, url);
        return success ? Result.success(url) : Result.error("Upload avatar failed");
    }

    @PostMapping("/{id}/deletion")
    public Result<Boolean> requestDeletion(@PathVariable Long id) {
        Long currentUserId = currentUserId();
        if (!isStationMaster() && !id.equals(currentUserId)) {
            return Result.error(403, "Forbidden");
        }
        boolean success = userService.requestDeletion(id);
        return success ? Result.success(true) : Result.error("Deletion request failed");
    }

    @PostMapping("/apply-employee")
    public Result<Boolean> applyEmployee() {
        Long currentUserId = currentUserId();
        if (currentUserId == null) {
            return Result.error(401, "Unauthorized");
        }
        boolean success = userService.applyEmployee(currentUserId);
        return success ? Result.success(true) : Result.error("Apply courier failed");
    }

    @PostMapping("/cancel-employee")
    public Result<Boolean> cancelEmployee() {
        Long currentUserId = currentUserId();
        if (currentUserId == null) {
            return Result.error(401, "Unauthorized");
        }
        boolean success = userService.cancelEmployee(currentUserId);
        return success ? Result.success(true) : Result.error("Cancel courier failed");
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
        return Integer.valueOf(Constants.ROLE_STATION_MASTER).equals(role);
    }

    private UserInfoResponse toUserInfo(User user) {
        UserInfoResponse response = new UserInfoResponse();
        BeanCopyUtil.copy(user, response);
        return response;
    }
}
