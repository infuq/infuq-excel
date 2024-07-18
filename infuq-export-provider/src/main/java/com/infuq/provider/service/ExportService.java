package com.infuq.provider.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.mq.http.MQProducer;
import com.aliyun.mq.http.model.TopicMessage;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import com.infuq.common.constants.CommonConstant;
import com.infuq.common.enums.ExportTypeEnum;
import com.infuq.common.enums.FileStatus;
import com.infuq.common.enums.SuffixType;
import com.infuq.common.model.ExportRecord;
import com.infuq.common.model.ExportTaskDTO;
import com.infuq.common.req.StoreCustomerOrderReq;
import com.infuq.provider.mapper.ExportRecordMapper;
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

//    @Autowired
//    private MQProducer producer;

    @Autowired
    private Producer producer;
    @Autowired
    private ExportRecordMapper exportRecordMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate redisTemplate;

    public void exportStoreCustomerOrder(StoreCustomerOrderReq req) {

        req.setUserId(154539762039238656L);

        ExportRecord record = ExportRecord.builder()
                .userId(154539762039238656L)
                .fileName("订货单信息-202405301324413413271.xlsx")
                .fileStatus(FileStatus.CREATE_SUCCESS.getCode())
                .fileTypeDesc("订货单信息")
                .fileSuffix(SuffixType.Xlsx.getFileType())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .requestBody(JSON.toJSONString(req))
                .exportType(ExportTypeEnum.CUSTOMER_ORDER_INFO.getValue())
                .enterpriseId(154539761477201920L)
                .build();

        // 1.向数据库插入导出记录
        exportRecordMapper.insert(record);

        // 2.发送MQ消息
        /* HTTP 协议
        TopicMessage message;
        message = new TopicMessage("HTTP SDK".getBytes(), "EXPORT");
        message.getProperties().put("address", "hz");
        message.setMessageKey("123456qwerty");

        TopicMessage response = producer.publishMessage(message);

        log.info(" Send mq message success. Topic is: TEST, msgId is: " + response.getMessageId()
                + ", bodyMD5 is: " + response.getMessageBodyMD5());

        */


        ExportTaskDTO exportTaskDTO = ExportTaskDTO.builder()
                .exportRecordId(record.getExportRecordId())
                .build();

        // TCP 协议
        Message message = new Message();
        message.setTopic(req.getTopic());
        message.setTag(req.getTag());
        message.setKey(record.getExportRecordId().toString());
        message.setBody(JSON.toJSONString(exportTaskDTO).getBytes());
//        SendResult response = producer.send(message);
//        log.info("msgId is: " + response.getMessageId());


        redisTemplate.convertAndSend(CommonConstant.REDIS_EXPORT_CHANNEL, JSONObject.toJSONString(exportTaskDTO));

    }


}
