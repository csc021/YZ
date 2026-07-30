package com.tf.sc.controller;

import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Result;
import com.tf.sc.entity.SensorReading;
import com.tf.sc.service.SensorReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    @Autowired
    private SensorReadingService sensorReadingService;

    /**
     * ESP32 HTTP 上报温湿度数据
     * POST /api/sensors/report
     * Body: { zoneCode: "A", stationId: 1, temperature: 25.3, humidity: 55.1 }
     */
    @PostMapping("/report")
    public Result<SensorReading> report(@RequestBody Map<String, Object> body) {
        String zoneCode = (String) body.get("zoneCode");
        Long stationId = body.get("stationId") != null
                ? Long.valueOf(body.get("stationId").toString()) : null;
        Double temperature = body.get("temperature") != null
                ? Double.valueOf(body.get("temperature").toString()) : null;
        Double humidity = body.get("humidity") != null
                ? Double.valueOf(body.get("humidity").toString()) : null;

        if (zoneCode == null || stationId == null) {
            return Result.error("zoneCode 和 stationId 不能为空");
        }
        return Result.success(sensorReadingService.receive(zoneCode, stationId, temperature, humidity));
    }

    /** 获取驿站所有分区最新读数 */
    @RequireRole({"1", "2"})
    @GetMapping("/current")
    public Result<List<SensorReading>> current(@RequestParam Long stationId) {
        return Result.success(sensorReadingService.getLatestByStationId(stationId));
    }

    /** 获取某分区最新读数 */
    @RequireRole({"1", "2"})
    @GetMapping("/current/{zoneId}")
    public Result<SensorReading> currentByZone(@PathVariable Long zoneId) {
        SensorReading reading = sensorReadingService.getLatestByZoneId(zoneId);
        return reading != null ? Result.success(reading)
                : Result.success(null);
    }
}