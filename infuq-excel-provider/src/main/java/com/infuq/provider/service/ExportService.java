package com.infuq.provider.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.infuq.common.constants.CommonConstant;
import com.infuq.common.enums.ExportFileStatus;
import com.infuq.common.enums.ExportTypeEnum;
import com.infuq.common.enums.SuffixType;
import com.infuq.common.model.ExportTaskBO;
import com.infuq.common.req.StoreCustomerOrderReq;
import com.infuq.entity.ExportRecord;
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
public class ExportService {

    @Autowired
    private MQProducer producer;
    @Autowired
    private ExportRecordMapper exportRecordMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate redisTemplate;

    public void exportStoreCustomerOrder(StoreCustomerOrderReq req) throws Exception {

        Long userId = 154539762039238656L;
        Long enterpriseId = 154539761477201920L;
        req.setUserId(userId);

        ExportRecord record = ExportRecord.builder()
                .userId(userId)
                .fileName("订货单信息-202405301324413413271.xlsx")
                .fileStatus(ExportFileStatus.CREATE_SUCCESS.getCode())
                .fileTypeDesc("订货单信息")
                .fileSuffix(SuffixType.xlsx.getFileType())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .requestBody(JSON.toJSONString(req)) // 记录当前请求的数据
                .exportType(ExportTypeEnum.STORE_CUSTOMER_ORDER.getValue())
                .enterpriseId(enterpriseId)
                .build();

        // 1.向数据库插入导出记录
        exportRecordMapper.insert(record);


        ExportTaskBO exportTask = ExportTaskBO.builder()
                .exportRecordId(record.getExportRecordId())
                .build();

        // 2.方式一 发送MQ
        producer.send(exportTask, CommonConstant.MQ_EXPORT_CUSTOMER_CUSTOMER_ORDER);

        // 2.方式二 REDIS
        redisTemplate.convertAndSend(CommonConstant.REDIS_EXPORT_CHANNEL, JSONObject.toJSONString(exportTask));

    }


}
