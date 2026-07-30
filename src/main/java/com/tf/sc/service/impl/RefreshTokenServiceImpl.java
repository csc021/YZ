package com.tf.sc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tf.sc.entity.RefreshToken;
import com.tf.sc.mapper.RefreshTokenMapper;
import com.tf.sc.service.RefreshTokenService;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl extends ServiceImpl<RefreshTokenMapper, RefreshToken> implements RefreshTokenService {
    @Override
    public RefreshToken findValidByUserId(Long userId) {
        return baseMapper.selectValidTokenByUserId(userId);
    }

    @Override
    public int removeAllByUserId(Long userId) {
        return baseMapper.deleteAllTokenByUserId(userId);
    }

    @Override
    public int removeExpired() {
        return baseMapper.deleteExpiredToken();
    }
}
