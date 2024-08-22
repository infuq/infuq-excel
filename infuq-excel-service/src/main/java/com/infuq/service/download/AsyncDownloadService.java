package com.infuq.service.download;

import com.infuq.common.enums.ExportFileStatus;
import com.infuq.common.model.RunningTaskBO;
import com.infuq.common.model.TaskBO;
import com.infuq.entity.DownloadRecord;
import com.infuq.handler.StoreCustomerOrderAsyncDownloadExcel;
import com.infuq.mapper.DownloadRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AsyncDownloadService {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private DownloadRecordMapper downloadRecordMapper;
    @Resource
    private StoreCustomerOrderAsyncDownloadExcel storeCustomerOrderAsyncDownloadExcel;

    private final Map<Long, RunningTaskBO> runningTaskMap = new ConcurrentHashMap<>();


    public void handleExport(TaskBO task) {

        Long recordId = task.getRecordId();

        // 1.查询导出记录
        DownloadRecord downloadRecord = downloadRecordMapper.selectById(recordId);
        if (downloadRecord == null) {
            log.error("导出记录不存在");
            return;
        }
        // 2.校验状态
        Integer fileStatus = downloadRecord.getFileStatus();
        if (fileStatus != null && fileStatus.compareTo(ExportFileStatus.WAIT_DOWNLOAD.getCode()) >= 0) {
            log.warn("导出任务已处理,记录ID:{}", recordId);
            return;
        }
        // 3.校验是否正在被处理
        if (runningTaskMap.containsKey(recordId)) {
            log.warn("导出任务正在处理中,记录ID:{}", recordId);
            return;
        }
        // 4.限制导出任务数量(并发数量20个)
        if (runningTaskMap.size() > 20) {
            throw new RuntimeException("导出服务繁忙,请稍后重试");
        }


        String key = "DOWNLOAD:" + recordId;
        RLock rLock = redissonClient.getLock(key);

        try {
            if (!rLock.tryLock(1, 30 * 60, TimeUnit.SECONDS)) {
                log.warn("短时间内同一个导出任务被重复处理,记录ID:{},获取锁失败", recordId);
                return;
            }
            log.info("加锁成功,记录ID:" + recordId);

            RunningTaskBO runningTask = new RunningTaskBO();
            runningTask.setRecordId(recordId);
            runningTask.setStartRunningTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            runningTask.setThreadName(Thread.currentThread().getName());
            runningTaskMap.put(recordId, runningTask);
            log.info("当前正在处理导出任务数共:{}个,任务记录ID:{}", runningTaskMap.size(), runningTaskMap.keySet());

            Integer businessType = downloadRecord.getBusinessType();
            // 根据 businessType 获取对应业务的导出策略类, 执行具体的导出业务


            String fileDownloadUrl = storeCustomerOrderAsyncDownloadExcel.download(downloadRecord.getRequestBody());

            downloadRecord.setFileStatus(ExportFileStatus.WAIT_DOWNLOAD.getCode());
            downloadRecord.setFileDownloadUrl(fileDownloadUrl);
            downloadRecord.setUpdateTime(LocalDateTime.now());
            // 更新导出记录
            downloadRecordMapper.updateById(downloadRecord);

        } catch (Exception e) {

            downloadRecord.setFileStatus(ExportFileStatus.EXPORT_ERROR.getCode());
            downloadRecord.setRemark(e.getMessage());

            // 更新导出记录
            downloadRecordMapper.updateById(downloadRecord);

        } finally {
            runningTaskMap.remove(recordId);

            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                // 释放锁
                log.info("释放锁,记录ID:" + recordId);
                rLock.unlock();
            }
        }

    }


}
