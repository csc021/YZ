package com.tf.sc.scheduled;

import com.tf.sc.service.ParcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ParcelRetentionTask {
    @Autowired
    private ParcelService parcelService;

    @Scheduled(cron = "0 0 3 * * ?")
    public void run() {
        parcelService.markRetainedParcels(3);
    }
}
