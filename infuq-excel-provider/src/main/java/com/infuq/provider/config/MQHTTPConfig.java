package com.infuq.provider.config;

import com.aliyun.mq.http.MQClient;
import com.aliyun.mq.http.MQProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@AllArgsConstructor
//@Configuration
public class MQHTTPConfig {

    private final Environment environment;

    @Bean
    public MQProducer producer() {
        MQClient mqClient = new MQClient(
                "http://1106705442396860.mqrest.cn-hangzhou.aliyuncs.com",
                "",
                ""
        );

        return mqClient.getProducer("MQ_INST_1106705442396860_BXXINmp6", "TEST");
    }


}

