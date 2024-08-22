package com.infuq.consumer.listener.redis;

import com.alibaba.fastjson.JSONObject;
import com.infuq.common.model.TaskBO;
import com.infuq.service.download.AsyncDownloadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * 通过Redis方式监听导出消息
 *
 */
@Slf4j
public class RedisDownloadListener implements MessageListener {

    private AsyncDownloadService asyncDownloadService;

    public RedisDownloadListener() {
    }

    public RedisDownloadListener(AsyncDownloadService asyncDownloadService) {
        this.asyncDownloadService = asyncDownloadService;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {

        log.info("监听器:" + this.hashCode() + ",消费Channel:" + message.getChannel());

        String msg = new String(message.getBody());
        String jsonStr = StringEscapeUtils.unescapeJava(msg.substring(1, msg.length() - 1));
        TaskBO task = JSONObject.parseObject(jsonStr, TaskBO.class);

        asyncDownloadService.handleExport(task);

    }


}
