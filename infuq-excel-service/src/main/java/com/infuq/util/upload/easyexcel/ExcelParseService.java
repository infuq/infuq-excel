package com.infuq.util.upload.easyexcel;

import com.alibaba.excel.EasyExcelFactory;
import com.infuq.common.rsp.ParseExcelRsp;
import com.infuq.util.upload.ExcelParser;
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
public class ExcelParseService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    public <T> ParseExcelRsp parse(InputStream in, ExcelParser<T> parser) {

        List<T> failList = new LinkedList<>();
        List<Future<ParseExcelRsp>> asyncParseRetList = new ArrayList<>();

        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String start = dateTimeFormatter.format(new Date());
        String batchNo = "EASY_EXCEL_UPLOAD:" + start;

        // 多线程解析
        EasyExcelFactory.read(in, parser.head(), new ExcelReadListener<T>(failList, asyncParseRetList, batchNo, parser, redisTemplate)).sheet(0).doRead();

        int success = 0;
        int fail = failList.size();

        // 主线程等待
        for (Future<ParseExcelRsp> f : asyncParseRetList) {
            try {
                ParseExcelRsp obj = f.get();
                if (obj.getSuccessSize() != null) {
                    success = obj.getSuccessSize() + success;
                }
                if (obj.getFailSize() != null) {
                    fail = obj.getFailSize() + fail;
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
