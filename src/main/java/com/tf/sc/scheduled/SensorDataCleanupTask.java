package com.tf.sc.scheduled;

import com.tf.sc.service.SensorDataService;
import com.tf.sc.service.SensorReadingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SensorDataCleanupTask {
    private static final Logger log = LoggerFactory.getLogger(SensorDataCleanupTask.class);

    @Autowired
    private SensorReadingService sensorReadingService;

    @Autowired
    private SensorDataService sensorDataService;

    /** 每天凌晨4点清理7天前的传感器历史数据 */
    @Scheduled(cron = "0 0 4 * * ?")
    public void run() {
        try {
            int deleted = sensorReadingService.cleanOldData(7);
            if (deleted > 0) {
                log.info("清理了 {} 条过期分区传感器数据 (sensor_reading, 7天前)", deleted);
            }
        } catch (Exception e) {
            log.error("清理分区传感器数据失败: {}", e.getMessage(), e);
        }

        try {
            int deleted = sensorDataService.cleanOldData(7);
            if (deleted > 0) {
                log.info("清理了 {} 条过期IoT传感器数据 (t_sensor_data, 7天前)", deleted);
            }
        } catch (Exception e) {
            log.error("清理IoT传感器数据失败: {}", e.getMessage(), e);
        }
    }
}
