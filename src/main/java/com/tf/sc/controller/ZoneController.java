package com.tf.sc.controller;

import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Result;
import com.tf.sc.entity.Zone;
import com.tf.sc.service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequireRole({"1", "2"})
@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    @Autowired
    private ZoneService zoneService;

    @GetMapping("/list")
    public Result<List<Zone>> list() {
        return Result.success(zoneService.list());
    }

    @GetMapping("/station/{stationId}")
    public Result<List<Zone>> getByStationId(@PathVariable Long stationId) {
        return Result.success(zoneService.getByStationId(stationId));
    }

    @GetMapping("/station/{stationId}/normal")
    public Result<List<Zone>> getNormalByStationId(@PathVariable Long stationId) {
        return Result.success(zoneService.getNormalByStationId(stationId));
    }

    @GetMapping("/{id}")
    public Result<Zone> getById(@PathVariable Long id) {
        Zone zone = zoneService.getById(id);
        return zone == null ? Result.error("分区不存在") : Result.success(zone);
    }

    @PostMapping("/create")
    public Result<Zone> create(@RequestBody Zone zone) {
        boolean success = zoneService.createZone(zone);
        return success ? Result.success(zone) : Result.error("创建分区失败");
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody Zone zone) {
        boolean success = zoneService.updateZone(zone);
        return success ? Result.success(true) : Result.error("更新分区失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = zoneService.deleteZone(id);
        return success ? Result.success(true) : Result.error("删除分区失败");
    }

    @PostMapping("/enable/{id}")
    public Result<Boolean> enable(@PathVariable Long id) {
        boolean success = zoneService.enableZone(id);
        return success ? Result.success(true) : Result.error("启用分区失败");
    }

    @PostMapping("/disable/{id}")
    public Result<Boolean> disable(@PathVariable Long id) {
        boolean success = zoneService.disableZone(id);
        return success ? Result.success(true) : Result.error("停用分区失败");
    }
}