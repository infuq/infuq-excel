package com.infuq.provider.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.infuq.common.constants.CommonConstant;
import com.infuq.common.constants.SuffixTypeConstant;
import com.infuq.common.enums.BusinessTypeEnum;
import com.infuq.common.enums.ExportFileStatus;
import com.infuq.common.model.TaskBO;
import com.infuq.common.req.DownloadStoreCustomerOrderTemplateCondition;
import com.infuq.common.rsp.DownloadStoreCustomerOrderTemplateHead;
import com.infuq.entity.DownloadRecord;
import com.infuq.mapper.DownloadRecordMapper;
import com.infuq.mapper.StoreCustomerOrderMapper;
import com.infuq.provider.producer.MQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class StoreCustomerOrderDownloadService {

    @Resource
    private StoreCustomerOrderMapper storeCustomerOrderMapper;
    @Resource
    private DownloadRecordMapper downloadRecordMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private MQProducer producer;


    public List<DownloadStoreCustomerOrderTemplateHead> downloadTemplate(DownloadStoreCustomerOrderTemplateCondition request) {
        return storeCustomerOrderMapper.downloadTemplate(request);
    }


    public void asyncDownloadTemplate(DownloadStoreCustomerOrderTemplateCondition req) throws Exception {

        Long userId = 154539762039238656L;
        Long enterpriseId = 154539761477201920L;
        req.setUserId(userId);

        DownloadRecord record = DownloadRecord.builder()
                .userId(userId)
                .enterpriseId(enterpriseId)
                .businessType(BusinessTypeEnum.STORE_CUSTOMER_ORDER.getValue())
                .fileName("全部订货单.xlsx")
                .fileSuffix(SuffixTypeConstant.XLSX)
                .fileStatus(ExportFileStatus.CREATE_SUCCESS.getCode())
                .requestBody(JSON.toJSONString(req)) // 记录当前请求的数据
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 1.向数据库插入导出记录
        downloadRecordMapper.insert(record);


        TaskBO task = TaskBO.builder()
                .recordId(record.getRecordId())
                .build();

        // 2.方式一 发送MQ
        //producer.send(task, "export");

        // 2.方式二 REDIS
        redisTemplate.convertAndSend(CommonConstant.REDIS_EXPORT_CHANNEL, JSONObject.toJSONString(task));


    }



}
