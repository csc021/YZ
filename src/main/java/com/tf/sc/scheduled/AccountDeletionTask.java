package com.tf.sc.scheduled;

import com.tf.sc.entity.User;
import com.tf.sc.service.UserService;
import com.tf.sc.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AccountDeletionTask {
    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 30 3 * * ?")
    public void run() {
        String deadline = DateUtil.format(LocalDateTime.now().minusDays(7));
        for (User user : userService.findDeletionPending()) {
            if (Integer.valueOf(1).equals(user.getDeletionStatus())
                    && user.getDeletionTime() != null
                    && DateUtil.isBefore(user.getDeletionTime(), deadline)) {
                user.setDeletionStatus(2);
                user.setUpdatedAt(DateUtil.nowStr());
                userService.updateUser(user);
            }
        }
    }
}
