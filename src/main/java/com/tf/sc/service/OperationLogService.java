package com.tf.sc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tf.sc.entity.OperationLog;

import java.util.List;

public interface OperationLogService extends IService<OperationLog> {
    List<OperationLog> listByCondition(Long userId, String action, String startTime, String endTime);

    int clearBefore(String time);
}
