package com.tf.sc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tf.sc.entity.RefreshToken;

public interface RefreshTokenService extends IService<RefreshToken> {
    RefreshToken findValidByUserId(Long userId);

    int removeAllByUserId(Long userId);

    int removeExpired();
}
