package com.infuq.consumer.listener.mq.business;

import com.infuq.common.model.TaskBO;
import com.infuq.service.download.AsyncDownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Properties;

@Slf4j
@Service(value = "download")
public class DownloadListener implements BusinessMQListener<TaskBO> {

    @Resource
    private AsyncDownloadService asyncDownloadService;

    @Override
    public void handler(TaskBO task, Properties userProperties) {
        asyncDownloadService.handleExport(task);
    }
}
