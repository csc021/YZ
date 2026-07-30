package com.tf.sc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.tf.sc.mapper")
@EnableScheduling
public class KsApplication {
    public static void main(String[] args) {
        SpringApplication.run(KsApplication.class, args);
    }
}
    