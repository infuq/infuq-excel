package com.infuq.consumer.service;

import com.infuq.common.enums.FileStatus;
import com.infuq.common.model.ExportRecord;
import com.infuq.common.model.ExportTaskDTO;
import com.infuq.consumer.mapper.ExportRecordMapper;
import com.infuq.consumer.model.RunningExportTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ExportService {

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ExportRecordMapper exportRecordMapper;

    private final Map<Long, RunningExportTaskInfo> runningTaskMap = new ConcurrentHashMap<>();


    public void handleExport(ExportTaskDTO exportTaskDTO) {

        Long exportRecordId = exportTaskDTO.getExportRecordId();

        // 1.查询导出记录
        ExportRecord exportRecord = exportRecordMapper.selectById(exportRecordId);
        if (exportRecord == null) {
            log.error("导出记录不存在");
            return;
        }
        // 2.校验状态
        Integer fileStatus = exportRecord.getFileStatus();
        if (fileStatus != null && fileStatus.compareTo(FileStatus.WAIT_DOWNLOAD.getCode()) >= 0) {
            log.warn("导出任务已处理,记录ID:{}", exportRecordId);
            return;
        }
        // 3.校验是否正在被处理
        if (runningTaskMap.containsKey(exportRecordId)) {
            log.warn("导出任务正在处理中,记录ID:{}", exportRecordId);
            return;
        }
        // 4.限制导出任务数量(并发数量20个)
        if (runningTaskMap.size() > 20) {
            throw new RuntimeException("导出服务繁忙,请稍后重试");
        }


        String key = "export:" + exportRecordId;
        RLock rLock = redissonClient.getLock(key);

        try {
            if (!rLock.tryLock(1, 30 * 60, TimeUnit.SECONDS)) {
                log.warn("短时间内同一个导出任务被重复处理,记录ID:{},获取锁失败", exportRecordId);
                return;
            }
            log.info("加锁成功,记录ID:" + exportRecordId);

            RunningExportTaskInfo runningTask = new RunningExportTaskInfo();
            runningTask.setExportRecordId(exportRecordId);
            runningTask.setStartRunningTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            runningTask.setThreadName(Thread.currentThread().getName());
            runningTaskMap.put(exportRecordId, runningTask);
            log.info("当前正在处理导出任务数共:{}个,任务记录ID:{}", runningTaskMap.size(), runningTaskMap.keySet());

            String exportType = exportRecord.getExportType();
            // 根据 exportType 获取对应业务的导出策略类, 执行具体的导出业务

            //Thread.sleep(26 * 60 * 1000);

        } catch (Exception e) {

            exportRecord.setFileStatus(FileStatus.EXPORT_ERROR.getCode());
            exportRecord.setRemark(e.getMessage());

            // 更新导出记录
            exportRecordMapper.updateById(exportRecord);

        } finally {
            runningTaskMap.remove(exportRecordId);

            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                // 释放锁
                log.info("释放锁,记录ID:" + exportRecordId);
                rLock.unlock();
            }
        }

    }


}
