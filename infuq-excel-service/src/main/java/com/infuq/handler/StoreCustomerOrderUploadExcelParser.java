package com.infuq.handler;

import com.alibaba.excel.util.StringUtils;
import com.infuq.common.req.StoreCustomerOrderUploadReq;
import com.infuq.common.rsp.ParseExcelRsp;
import com.infuq.util.easyexcel.ExcelParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 解析上传的Excel文件数据
 */
@Service
@Slf4j
public class StoreCustomerOrderUploadExcelParser implements ExcelParser<StoreCustomerOrderUploadReq> {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    static final ExecutorService EXCEL_DATA_2_DB = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ExcelData2RedisOrDB");
        }
    });


    // 解析阶段一 不涉及数据库层面的解析 只是单一的判断数据是否没有填写
    public void parse(StoreCustomerOrderUploadReq row, List<StoreCustomerOrderUploadReq> tmpList, List<StoreCustomerOrderUploadReq> failList) {

        if (StringUtils.isEmpty(row.getStoreName())) {
            row.setErrorMsg("门店名称不能为空");
            failList.add(row);
            return;
        }
        if (StringUtils.isEmpty(row.getStoreCustomerOrderNo())) {
            row.setErrorMsg("订单号");
            failList.add(row);
            return;
        }

        tmpList.add(row);
    }

    // 解析阶段二 涉及数据库层面的解析
    // 该线程需要处理 tmpList 里的数据
    public ParseExcelRsp parseInDB(List<StoreCustomerOrderUploadReq> tmpList, String batchNo, int batchLine) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("解析并落库");



        // TODO 批量查询,再次解析 tmpList 中哪些成功哪些失败
        tmpList.forEach(v -> {

            // 访问数据库
        });

        List<StoreCustomerOrderUploadReq> successList = new ArrayList<>(batchLine);
        List<StoreCustomerOrderUploadReq> failList = new ArrayList<>(batchLine);


        ParseExcelRsp ret = ParseExcelRsp.builder().build();

        // 将成功数据存入数据库
        if (!CollectionUtils.isEmpty(successList)) {

            EXCEL_DATA_2_DB.submit(new Runnable() {
                @Override
                public void run() {

                    // TODO 数据落库, 数据暂时不可见. 如果落库失败需要放入到Redis中
                }
            });

            ret.setSuccessSize(successList.size());
        }

        // 将失败数据存入缓存
        if (!CollectionUtils.isEmpty(failList)) {

            redisTemplate.opsForValue().increment(batchNo + ":FAILSIZE", failList.size());
            redisTemplate.expire(batchNo + ":FAILSIZE", 30, TimeUnit.MINUTES);

            EXCEL_DATA_2_DB.submit(new Runnable() {
                @Override
                public void run() {
                    // TODO 失败数据放入Redis,以便后面下载使用
                }
            });

            ret.setFailSize(failList.size());
        }

        stopWatch.stop();

        log.info("线程["+Thread.currentThread().getName()+"]解析" + tmpList.size() + "条数据数据完成,成功" + ret.getSuccessSize() + "条,失败" + ret.getFailSize() + "条,耗时" + stopWatch.getLastTaskTimeMillis() + "毫秒");


        return ret;
    }

}

