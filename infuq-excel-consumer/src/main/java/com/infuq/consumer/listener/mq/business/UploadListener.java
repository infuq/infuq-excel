package com.infuq.consumer.listener.mq.business;

import com.infuq.common.model.TaskBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Slf4j
@Service(value = "upload")
public class UploadListener implements BusinessMQListener<TaskBO> {

    @Override
    public void handler(TaskBO data, Properties userProperties) {


    }
}
