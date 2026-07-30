package com.tf.sc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tf.sc.entity.Carrier;
import com.tf.sc.mapper.CarrierMapper;
import com.tf.sc.service.CarrierService;
import org.springframework.stereotype.Service;

@Service
public class CarrierServiceImpl extends ServiceImpl<CarrierMapper, Carrier> implements CarrierService {
}