package com.tf.sc.controller;

import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Result;
import com.tf.sc.dto.request.AuditRequest;
import com.tf.sc.dto.response.AuditListResponse;
import com.tf.sc.entity.User;
import com.tf.sc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequireRole({"2"})
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public Result<List<AuditListResponse>> users() {
        List<AuditListResponse> responses = userService.findAll().stream()
                .map(this::toAuditResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    @GetMapping("/audits")
    public Result<List<AuditListResponse>> pendingAudits() {
        List<AuditListResponse> responses = userService.findAll().stream()
                .filter(user -> Integer.valueOf(0).equals(user.getAuditStatus())
                        || Integer.valueOf(3).equals(user.getAuditStatus()))
                .map(this::toAuditResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    @GetMapping("/audits/history")
    public Result<List<AuditListResponse>> auditHistory() {
        List<AuditListResponse> responses = userService.findAll().stream()
                .filter(user -> Integer.valueOf(1).equals(user.getAuditStatus())
                        || Integer.valueOf(2).equals(user.getAuditStatus()))
                .map(this::toAuditResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    @PostMapping("/audit")
    public Result<Boolean> audit(@RequestBody AuditRequest request) {
        boolean success = userService.auditUser(request.getUserId(), request.getAuditStatus(), request.getRejectReason());
        return success ? Result.success(true) : Result.error("Audit failed");
    }

    @PostMapping("/users/{id}/lock")
    public Result<Boolean> lock(@PathVariable Long id) {
        boolean success = userService.lockUser(id);
        return success ? Result.success(true) : Result.error("Lock failed");
    }

    @PostMapping("/users/{id}/unlock")
    public Result<Boolean> unlock(@PathVariable Long id) {
        boolean success = userService.unlockUser(id);
        return success ? Result.success(true) : Result.error("Unlock failed");
    }

    private AuditListResponse toAuditResponse(User user) {
        AuditListResponse response = new AuditListResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setPhone(user.getPhone());
        response.setNickname(user.getNickname());
        response.setRole(user.getRole());
        response.setAuditStatus(user.getAuditStatus());
        response.setRejectReason(user.getRejectReason());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
