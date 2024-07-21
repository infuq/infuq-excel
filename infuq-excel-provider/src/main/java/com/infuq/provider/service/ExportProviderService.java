package com.infuq.provider.service;


import com.alibaba.fastjson.JSON;
import com.infuq.common.constants.SuffixTypeConstant;
import com.infuq.common.enums.BusinessTypeEnum;
import com.infuq.common.enums.ExportFileStatus;
import com.infuq.common.model.TaskBO;
import com.infuq.common.req.StoreCustomerOrderReq;
import com.infuq.entity.ExportRecord;
import com.infuq.export.EasyExcelExportService;
import com.infuq.mapper.ExportRecordMapper;
import com.infuq.provider.producer.MQProducer;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;


@Service
@Slf4j
public class ExportProviderService {

    @Autowired
    private MQProducer producer;
    @Autowired
    private ExportRecordMapper exportRecordMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private EasyExcelExportService easyExcelExportService;

    public void exportStoreCustomerOrder(StoreCustomerOrderReq req) throws Exception {

        Long userId = 154539762039238656L;
        Long enterpriseId = 154539761477201920L;
        req.setUserId(userId);

        ExportRecord record = ExportRecord.builder()
                .userId(userId)
                .enterpriseId(enterpriseId)
                .businessType(BusinessTypeEnum.STORE_CUSTOMER_ORDER.getValue())
                .fileName("订货单信息-202405301324413413271.xlsx")
                .fileSuffix(SuffixTypeConstant.XLSX)
                .fileStatus(ExportFileStatus.CREATE_SUCCESS.getCode())
                .requestBody(JSON.toJSONString(req)) // 记录当前请求的数据
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 1.向数据库插入导出记录
        exportRecordMapper.insert(record);


        TaskBO task = TaskBO.builder()
                .recordId(record.getRecordId())
                .build();

        // 2.方式一 发送MQ
        //producer.send(task, "export");

        // 2.方式二 REDIS
        //redisTemplate.convertAndSend(CommonConstant.REDIS_EXPORT_CHANNEL, JSONObject.toJSONString(task));


        easyExcelExportService.handleExport(task);


    }


}
