package com.infuq.upload;

import com.alibaba.excel.EasyExcelFactory;
import com.infuq.common.rsp.ParseExcelRsp;
import com.infuq.util.easyexcel.ExcelParser;
import com.infuq.util.easyexcel.ExcelReadListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

@Service
@Slf4j
public class EasyExcelUploadService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    public <T> ParseExcelRsp handleUpload(InputStream in, Class<T> clazz, ExcelParser<T> parser) {

        List<T> failList = new LinkedList<>();
        List<Future<ParseExcelRsp>> parseRetList = new ArrayList<>();

        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String start = dateTimeFormatter.format(new Date());
        String batchNo = "EASY_EXCEL_UPLOAD:" + start;


        // 多线程解析
        EasyExcelFactory.read(in, clazz, new ExcelReadListener<T>(failList, parseRetList, batchNo, parser, redisTemplate))
                .sheet(0)
                .doRead();

        int success = 0;
        int fail = failList.size();

        // 主线程等待
        for (Future<ParseExcelRsp> f : parseRetList) {
            try {
                ParseExcelRsp obj = f.get();
                if (obj.getSuccessSize() != null) {
                    int _success = obj.getSuccessSize();
                    success = _success + success;
                }
                if (obj.getFailSize() != null) {
                    int _fail = obj.getFailSize();
                    fail = _fail + fail;
                }
            } catch (Exception e) {
                log.error("异常", e);
            }
        }

        return ParseExcelRsp.builder()
                .batchNo(batchNo)
                .successSize(success)
                .failSize(fail)
                .build();

    }

}
