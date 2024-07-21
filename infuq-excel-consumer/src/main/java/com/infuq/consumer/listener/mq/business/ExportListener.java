package com.infuq.consumer.listener.mq.business;

import com.infuq.common.model.TaskBO;
import com.infuq.export.ExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Properties;

@Slf4j
@Service(value = "export")
public class ExportListener implements BusinessMQListener<TaskBO> {

    @Resource
    private ExportService exportService;

    @Override
    public void handler(TaskBO task, Properties userProperties) {
        exportService.handleExport(task);
    }
}
