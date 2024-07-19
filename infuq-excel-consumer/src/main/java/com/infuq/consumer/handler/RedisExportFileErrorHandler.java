package com.infuq.consumer.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ErrorHandler;

@Slf4j
public class RedisExportFileErrorHandler implements ErrorHandler {


    @Override
    public void handleError(Throwable t) {
        log.error("导出文件错误,ErrMessage:{}", t.getMessage(), t);
    }


}
