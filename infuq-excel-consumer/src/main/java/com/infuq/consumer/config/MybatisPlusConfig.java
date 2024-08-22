package com.infuq.consumer.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.infuq.mapper")
public class MybatisPlusConfig {


}
