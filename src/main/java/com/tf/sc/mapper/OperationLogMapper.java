package com.tf.sc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tf.sc.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    List<OperationLog> selectLogByCondition(@Param("userId") Long userId,
                                            @Param("action") String action,
                                            @Param("startTime") String startTime,
                                            @Param("endTime") String endTime);

    int clearLogBeforeTime(@Param("time") String time);
}
