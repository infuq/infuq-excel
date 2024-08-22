package com.infuq.consumer.config;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.google.common.base.Joiner;
import com.infuq.consumer.listener.mq.OnsMessageListener;
import com.infuq.consumer.listener.mq.RocketMessageListener;
import com.infuq.consumer.listener.mq.business.BusinessMQListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "mq.consumer")
public class MQConfig implements ApplicationContextAware {

    @Value("${mq.name-server}")
    private String nameSrv;
    @Value("${mq.access-key}")
    private String accessKey;
    @Value("${mq.secret-key}")
    private String secretKey;
    @Value("${mq.consumer.consumeThreadMin}")
    private int consumeThreadMin;
    @Value("${mq.consumer.consumeThreadMax}")
    private int consumeThreadMax;
    @Value("${mq.consumer.consumeMessageBatchMaxSize}")
    private int consumeMessageBatchMaxSize;

    private String id;
    private String topic;
    private List<String> tags;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Profile(value ={"work", "prod"})
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Consumer consumer() {

        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, "30000");
        properties.setProperty(PropertyKeyConst.AccessKey, accessKey);
        properties.setProperty(PropertyKeyConst.SecretKey, secretKey);
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, nameSrv);
        properties.setProperty(PropertyKeyConst.GROUP_ID, id);

        Map<String, BusinessMQListener> allListener = applicationContext.getBeansOfType(BusinessMQListener.class);

        Consumer consumer = ONSFactory.createConsumer(properties);
        consumer.subscribe(topic, Joiner.on("||").join(tags), new OnsMessageListener(allListener));
        return consumer;
    }


    @Profile(value ={"local"})
    @Bean
    public DefaultMQPushConsumer settingConsumerListener() {

        Map<String, BusinessMQListener> allListener = applicationContext.getBeansOfType(BusinessMQListener.class);

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(id);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.setNamesrvAddr(nameSrv);
        consumer.setConsumeThreadMin(consumeThreadMin);
        consumer.setConsumeThreadMax(consumeThreadMax);
        consumer.registerMessageListener(new RocketMessageListener(allListener));
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);

        try {
            consumer.subscribe(topic, Joiner.on("||").join(tags));
            consumer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return consumer;

    }


}
