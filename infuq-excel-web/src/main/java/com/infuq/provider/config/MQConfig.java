package com.infuq.provider.config;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.infuq.provider.producer.MQProducer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Data
@Component
@Configuration
public class MQConfig {

    @Value("${mq.name-server}")
    private String nameSrv;
    @Value("${mq.access-key}")
    private String accessKey;
    @Value("${mq.secret-key}")
    private String secretKey;
    @Value("${mq.producer.id}")
    private String id;
    @Value("${mq.producer.topic}")
    private String topic;

    @Profile(value ={"work", "prod"})
    @Bean
    public MQProducer onsProducer(Environment environment) {

        Properties properties = new Properties();

        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, "30000");
        properties.setProperty(PropertyKeyConst.AccessKey, accessKey);
        properties.setProperty(PropertyKeyConst.SecretKey, secretKey);
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, nameSrv);
        properties.setProperty(PropertyKeyConst.GROUP_ID, id);

        Producer producer = ONSFactory.createProducer(properties);
        producer.start();

        return new MQProducer(environment, producer, null, topic);
    }

    @Profile(value ={"local"})
    @Bean
    public MQProducer rocketProducer(Environment environment) {

        DefaultMQProducer producer = new DefaultMQProducer("producerGroupName");
        producer.setInstanceName("producer");
        producer.setRetryTimesWhenSendFailed(2);
        producer.setNamesrvAddr(nameSrv);
        try {
            producer.start();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new MQProducer(environment, null, producer, topic);
    }


}
