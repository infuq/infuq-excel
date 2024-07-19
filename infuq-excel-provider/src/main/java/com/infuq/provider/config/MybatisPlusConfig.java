package com.infuq.provider.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.infuq.provider.mapper")
public class MybatisPlusConfig {


}
