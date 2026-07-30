package com.tf.sc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Constants;
import com.tf.sc.common.Result;
import com.tf.sc.dto.request.MailOrderStatusRequest;
import com.tf.sc.dto.request.MailOrderSubmitRequest;
import com.tf.sc.dto.response.MailOrderStatsResponse;
import com.tf.sc.entity.MailOrder;
import com.tf.sc.entity.Station;
import com.tf.sc.entity.StationStaff;
import com.tf.sc.service.MailOrderService;
import com.tf.sc.service.StationService;
import com.tf.sc.service.StationStaffService;
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

import javax.servlet.http.HttpServletRequest;

@RequireRole({"0", "1", "2"})
@RestController
@RequestMapping("/mail-order")
public class MailOrderController {

    @Autowired
    private MailOrderService mailOrderService;

    @Autowired
    private StationStaffService stationStaffService;

    @Autowired
    private StationService stationService;

    @RequireRole({"0", "1", "2"})
    @PostMapping("/submit")
    public Result<MailOrder> submit(@RequestBody MailOrderSubmitRequest request) {
        Long userId = currentUserId();
        if (userId == null) {
            return Result.error(401, "Unauthorized");
        }
        // 自动解析当前快递员/站长所属驿站
        if (request.getStationId() == null) {
            request.setStationId(currentStationId());
        }
        if (request.getStationId() == null) {
            return Result.error("请选择驿站");
        }
        return Result.success(mailOrderService.submit(request, userId));
    }

    @RequireRole({"0"})
    @GetMapping("/list/my")
    public Result<Page<MailOrder>> listMy(@RequestParam(defaultValue = "1") Long pageNum,
                                          @RequestParam(defaultValue = "10") Long pageSize) {
        return Result.success(mailOrderService.listMy(currentUserId(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MailOrder> detail(@PathVariable Long id) {
        MailOrder order = mailOrderService.getById(id);
        if (order == null) {
            return Result.error("Mail order not found");
        }
        if (Integer.valueOf(Constants.ROLE_USER).equals(currentRole()) && !order.getUserId().equals(currentUserId())) {
            return Result.error(403, "Forbidden");
        }
        return Result.success(order);
    }

    @RequireRole({"1", "2"})
    @GetMapping("/list/station")
    public Result<Page<MailOrder>> listStation(@RequestParam(required = false) Long stationId,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(defaultValue = "1") Long pageNum,
                                               @RequestParam(defaultValue = "10") Long pageSize) {
        Long resolvedStationId = stationId == null ? currentStationId() : stationId;
        return Result.success(mailOrderService.listStation(resolvedStationId, status, pageNum, pageSize));
    }

    @RequireRole({"1", "2"})
    @PostMapping("/{id}/accept")
    public Result<Boolean> accept(@PathVariable Long id) {
        boolean success = mailOrderService.accept(id);
        return success ? Result.success(true) : Result.error("Accept mail order failed");
    }

    @RequireRole({"1", "2"})
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestBody MailOrderStatusRequest request) {
        boolean success = mailOrderService.updateStatus(id, request.getStatus());
        return success ? Result.success(true) : Result.error("Update mail order status failed");
    }

    @RequireRole({"1", "2"})
    @GetMapping("/stats/today")
    public Result<MailOrderStatsResponse> todayStats(@RequestParam(required = false) Long stationId) {
        Long resolvedStationId = stationId == null ? currentStationId() : stationId;
        return Result.success(mailOrderService.todayStats(resolvedStationId));
    }

    private Long currentStationId() {
        Long userId = currentUserId();
        if (userId == null) {
            return null;
        }
        Integer role = currentRole();
        if (Integer.valueOf(Constants.ROLE_COURIER).equals(role)) {
            StationStaff staff = stationStaffService.getStationByUserId(userId);
            return staff == null ? null : staff.getStationId();
        }
        if (Integer.valueOf(Constants.ROLE_STATION_MASTER).equals(role)) {
            Station station = stationService.getByManagerId(userId);
            return station == null ? null : station.getId();
        }
        return null;
    }

    private Long currentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        HttpServletRequest request = attributes.getRequest();
        Object value = request.getAttribute("userId");
        if (value instanceof Long) return (Long) value;
        String header = request.getHeader(Constants.AUTH_HEADER);
        if (header == null || !header.startsWith(Constants.JWT_PREFIX)) return null;
        try {
            return Long.valueOf(JwtUtil.parseSubject(header.substring(Constants.JWT_PREFIX.length())));
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private Integer currentRole() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        Object role = attributes.getRequest().getAttribute("role");
        return role instanceof Integer ? (Integer) role : null;
    }
}
