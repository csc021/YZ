package com.tf.sc.controller;

import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Constants;
import com.tf.sc.common.Result;
import com.tf.sc.dto.response.StationStaffDetailResponse;
import com.tf.sc.entity.StationStaff;
import com.tf.sc.service.StationStaffService;
import com.tf.sc.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequireRole({"2"})
@RestController
@RequestMapping("/api/staff")
public class StationStaffController {

    @Autowired
    private StationStaffService stationStaffService;

    @PostMapping("/add")
    public Result<Boolean> addStaff(@RequestParam Long stationId, @RequestParam Long userId) {
        boolean success = stationStaffService.addStaffToStation(stationId, userId);
        return success ? Result.success(true) : Result.error("Add courier failed");
    }

    @DeleteMapping("/remove/{id}")
    public Result<Boolean> removeStaff(@PathVariable Long id) {
        boolean success = stationStaffService.removeStaffFromStation(id);
        return success ? Result.success(true) : Result.error("Remove courier failed");
    }

    @GetMapping("/station/{stationId}")
    public Result<List<StationStaff>> getStaffByStation(@PathVariable Long stationId) {
        return Result.success(stationStaffService.getStaffByStationId(stationId));
    }

    @GetMapping("/list/detail")
    public Result<List<StationStaffDetailResponse>> detailList(@RequestParam Long stationId) {
        return Result.success(stationStaffService.getStaffDetails(stationId));
    }

    @RequireRole({"1", "2"})
    @GetMapping("/user/{userId}")
    public Result<StationStaff> getStationByUser(@PathVariable Long userId) {
        StationStaff staff = stationStaffService.getStationByUserId(userId);
        return staff == null ? Result.error("Courier has no station") : Result.success(staff);
    }

    /** Resolve the logged-in courier's station without trusting a rounded JavaScript user id. */
    @RequireRole({"1"})
    @GetMapping("/current")
    public Result<StationStaff> getCurrentStation() {
        Long userId = currentUserId();
        if (userId == null) {
            return Result.error(401, "Unauthorized");
        }
        StationStaff staff = stationStaffService.getStationByUserId(userId);
        return staff == null ? Result.error("Courier has no station") : Result.success(staff);
    }

    @GetMapping("/check")
    public Result<Boolean> checkStaff(@RequestParam Long stationId, @RequestParam Long userId) {
        return Result.success(stationStaffService.isStaffInStation(stationId, userId));
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
}
