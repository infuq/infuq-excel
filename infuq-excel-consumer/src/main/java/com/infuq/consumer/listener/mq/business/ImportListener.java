package com.infuq.consumer.listener.mq.business;


import com.infuq.common.model.ExportTaskBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * 数据变化
 */
@Slf4j
@Service
public class ImportListener implements BusinessMQListener<ExportTaskBO> {

    @Override
    public void handler(ExportTaskBO data, Properties userProperties) {


    }
}
