package com.infuq.consumer.listener.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.infuq.common.model.TaskBO;
import com.infuq.consumer.listener.mq.business.BusinessMQListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@AllArgsConstructor
@Slf4j
public class OnsMessageListener implements com.aliyun.openservices.ons.api.MessageListener {

    private final Map<String, BusinessMQListener> allListener;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String tag = message.getTag();
        String key = message.getKey();
        log.info("MQ接收消息,message:{},tag:{},key:{}", JSON.toJSONString(message), tag, key);

        return consume(message, context, tag, key);
    }

    private Action consume(Message message, ConsumeContext context, String tag, String key) {
        BusinessMQListener<?> listener = allListener.get(tag);

        if (listener != null) {
            listener.handler(JSON.parseObject(message.getBody(), TaskBO.class), message.getUserProperties());
        }
        return Action.CommitMessage;
    }



}
