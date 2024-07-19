package com.infuq.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.infuq.common.model.ExportTaskDTO;
import com.infuq.consumer.service.ExportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * 通过Redis方式监听导出消息
 *
 */
@Slf4j
public class RedisExportListener implements MessageListener {

    private ExportService exportService;

    public RedisExportListener() {
    }

    public RedisExportListener(ExportService exportService) {
        this.exportService = exportService;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {

        log.info("监听器:" + this.hashCode() + ",消费Channel:" + message.getChannel());

        String msg = new String(message.getBody());
        String jsonStr = StringEscapeUtils.unescapeJava(msg.substring(1, msg.length() - 1));
        ExportTaskDTO exportTaskDTO = JSONObject.parseObject(jsonStr, ExportTaskDTO.class);

        exportService.handleExport(exportTaskDTO);

    }


}
