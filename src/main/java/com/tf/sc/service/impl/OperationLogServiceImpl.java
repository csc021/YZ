package com.tf.sc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tf.sc.entity.OperationLog;
import com.tf.sc.mapper.OperationLogMapper;
import com.tf.sc.service.OperationLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    @Override
    public List<OperationLog> listByCondition(Long userId, String action, String startTime, String endTime) {
        return baseMapper.selectLogByCondition(userId, action, startTime, endTime);
    }

    @Override
    public int clearBefore(String time) {
        return baseMapper.clearLogBeforeTime(time);
    }
}
