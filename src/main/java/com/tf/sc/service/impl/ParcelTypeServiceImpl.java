package com.tf.sc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tf.sc.entity.ParcelType;
import com.tf.sc.mapper.ParcelTypeMapper;
import com.tf.sc.service.ParcelTypeService;
import org.springframework.stereotype.Service;

@Service
public class ParcelTypeServiceImpl extends ServiceImpl<ParcelTypeMapper, ParcelType> implements ParcelTypeService {
}