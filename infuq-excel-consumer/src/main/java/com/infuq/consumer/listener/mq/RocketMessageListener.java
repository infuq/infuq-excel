package com.infuq.consumer.listener.mq;

import com.alibaba.fastjson.JSON;
import com.infuq.common.model.TaskBO;
import com.infuq.consumer.listener.mq.business.BusinessMQListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Slf4j
public class RocketMessageListener implements MessageListenerConcurrently {

    private final Map<String, BusinessMQListener> allListener;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageList, ConsumeConcurrentlyContext context) {

        messageList.forEach(message -> {
            byte[] body = message.getBody();
            String tag = message.getTags();

            BusinessMQListener<?> listener = allListener.get(tag);
            if (listener != null) {
                listener.handler(JSON.parseObject(body, TaskBO.class), null);
            }
        });

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

    }

}
