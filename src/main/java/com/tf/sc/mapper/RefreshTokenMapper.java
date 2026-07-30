package com.tf.sc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tf.sc.entity.RefreshToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshTokenMapper extends BaseMapper<RefreshToken> {
    RefreshToken selectValidTokenByUserId(@Param("userId") Long userId);

    int deleteAllTokenByUserId(@Param("userId") Long userId);

    int deleteExpiredToken();
}
