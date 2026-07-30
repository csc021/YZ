package com.tf.sc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tf.sc.entity.Carrier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CarrierMapper extends BaseMapper<Carrier> {
    List<Carrier> selectCarrierByCondition(@Param("name") String name, @Param("code") String code);

    int deleteBatchByIds(@Param("ids") List<Long> ids);
}
