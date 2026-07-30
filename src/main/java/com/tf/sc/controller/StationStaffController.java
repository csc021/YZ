package com.tf.sc.controller;

import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Result;
import com.tf.sc.dto.response.StationStaffDetailResponse;
import com.tf.sc.entity.StationStaff;
import com.tf.sc.service.StationStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/check")
    public Result<Boolean> checkStaff(@RequestParam Long stationId, @RequestParam Long userId) {
        return Result.success(stationStaffService.isStaffInStation(stationId, userId));
    }
}
