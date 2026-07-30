package com.tf.sc.controller;

import com.tf.sc.common.Result;
import com.tf.sc.entity.SensorData;
import com.tf.sc.service.SensorDataService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * IoT 温湿度传感器控制器 — 对应 t_sensor_data 表
 * API 规范参考: E:\555\温度湿度相关\温度湿度相关\温度湿度接口.txt
 */
@RestController
@RequestMapping("/api/iot")
public class IotSensorController {

    @Resource
    private SensorDataService sensorDataService;

    /** POST /api/iot/sensor-data — 接收串口上报的温湿度数据 */
    @PostMapping("/sensor-data")
    public Result<Map<String, Object>> receiveSensorData(@RequestBody SensorData sensorData) {
        System.out.println("[IoT] 收到数据: deviceId=" + sensorData.getDeviceId()
                + " temp=" + sensorData.getTemperature() + " hum=" + sensorData.getHumidity());
        Map<String, Object> data = sensorDataService.saveSensorData(sensorData);
        return Result.success(data);
    }

    /** GET /api/iot/latest?deviceId= — 获取最新一条数据 */
    @GetMapping("/latest")
    public Result<SensorData> getLatest(@RequestParam(required = false, defaultValue = "rk2206_01") String deviceId) {
        SensorData data = sensorDataService.getLatestData(deviceId);
        return data != null ? Result.success(data) : Result.error("暂无数据");
    }

    /** GET /api/iot/recent?deviceId=&limit= — 获取最近N条数据 */
    @GetMapping("/recent")
    public Result<List<SensorData>> getRecent(@RequestParam(required = false, defaultValue = "rk2206_01") String deviceId,
                                               @RequestParam(required = false, defaultValue = "20") int limit) {
        return Result.success(sensorDataService.getRecentData(deviceId, limit));
    }

    /** GET /api/iot/device/list — 获取所有设备列表 */
    @GetMapping("/device/list")
    public Result<List<Map<String, Object>>> getDeviceList() {
        return Result.success(sensorDataService.getDeviceList());
    }

    /** GET /api/iot/device/online — 获取在线设备列表（最近5分钟有数据） */
    @GetMapping("/device/online")
    public Result<List<Map<String, Object>>> getOnlineDevices() {
        return Result.success(sensorDataService.getOnlineDevices());
    }

    /** GET /api/iot/stats — 获取统计数据 */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        return Result.success(sensorDataService.getStats());
    }
}
