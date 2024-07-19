package com.infuq.consumer.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.infuq.consumer.mapper")
public class MybatisPlusConfig {


}
