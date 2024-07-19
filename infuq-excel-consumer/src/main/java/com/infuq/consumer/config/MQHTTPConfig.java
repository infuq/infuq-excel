package com.infuq.consumer.config;

import com.aliyun.mq.http.MQClient;
import com.aliyun.mq.http.MQConsumer;
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
    public MQConsumer consumer() {
        MQClient mqClient = new MQClient(
                "",
                "",
                ""
        );

        return mqClient.getConsumer("", "TEST", "", "EXPORT");
    }


}

