package com.tf.sc.mapper;

import com.tf.sc.entity.VerificationCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VerificationCodeMapper {

    int insert(VerificationCode verificationCode);

    VerificationCode findLatestByEmailAndType(@Param("email") String email, @Param("type") Integer type);

    VerificationCode findByEmailTypeCode(@Param("email") String email,
                                         @Param("type") Integer type,
                                         @Param("code") String code);

    int markAsUsed(@Param("id") Long id);

    int deleteExpired();

    int deleteByEmail(@Param("email") String email);
}
