package com.infuq.provider.producer;


import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Producer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.core.env.Environment;

import java.io.Serializable;

/**
 * 支持发送 ons 消息 和 rocketmq 消息
 */
public class MQProducer implements Serializable {

    private Environment environment;
    private Producer onsProducer;
    private DefaultMQProducer rocketProducer;
    private String topic;

    public MQProducer() { }
    public MQProducer(Environment environment, Producer onsProducer, DefaultMQProducer rocketProducer, String topic) {
        this.environment = environment;
        this.onsProducer = onsProducer;
        this.rocketProducer = rocketProducer;
        this.topic = topic;
    }



    public void send(Object body, String tag) throws Exception {

        String env = environment.getProperty("spring.profiles.active");
        if (env.equals("local")) {
            org.apache.rocketmq.common.message.Message message = new org.apache.rocketmq.common.message.Message(topic, tag, JSON.toJSONString(body).getBytes());
            org.apache.rocketmq.client.producer.SendResult response = rocketProducer.send(message);
            System.out.println("消息ID:" + response.getMsgId());
        } else {
            com.aliyun.openservices.ons.api.Message message = new com.aliyun.openservices.ons.api.Message(topic, tag, null, JSON.toJSONString(body).getBytes());
            com.aliyun.openservices.ons.api.SendResult response = onsProducer.send(message);
            System.out.println("消息ID:" + response.getMessageId());
        }

    }


}
