package com.tf.sc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tf.sc.entity.Parcel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParcelMapper extends BaseMapper<Parcel> {
    List<Parcel> selectParcelCondition(@Param("trackingNo") String trackingNo,
                                       @Param("recipientPhone") String recipientPhone,
                                       @Param("stationId") Long stationId,
                                       @Param("carrierId") Long carrierId,
                                       @Param("status") Integer status);

    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
}
