package com.infuq.util.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.infuq.common.rsp.ParseExcelRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class ExcelReadListener<T> implements ReadListener<T> {


    private final int batchLine = 200;
    private List<T> tmpList = new ArrayList<>(batchLine);
    private List<T> failList;

    // 异步结果
    private List<Future<ParseExcelRsp>> parseRetList;

    // 解析器
    private ExcelParser<T> parser;
    private RedisTemplate<String, Object> redisTemplate;
    private String batchNo;


    static final ThreadPoolExecutor PARSE_POOL = new ThreadPoolExecutor(4, 16,
            10,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));
    static final ExecutorService EXCEL_DATA_2_DB = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ExcelData2RedisOrDB");
        }
    });

    public ExcelReadListener() {}
    public ExcelReadListener(List<T> failList,
                             List<Future<ParseExcelRsp>> parseRetList,
                             String batchNo,
                             ExcelParser<T> parser,
                             RedisTemplate<String, Object> redisTemplate) {
        this.failList = failList;
        this.parseRetList = parseRetList;
        this.batchNo = batchNo;

        this.redisTemplate = redisTemplate;
        this.parser = parser;
    }

    @Override
    public void invoke(T row, AnalysisContext analysisContext) {

        // 解析阶段一 不涉及数据库层面的解析 只是单一的判断数据是否没有填写
        parser.parse(row, tmpList, failList);

        // 当解析条数达到 batchLine 条时,放在线程池里进行批量处理
        if (tmpList.size() == batchLine) {

            List<T> _tmpList = tmpList;

            // 创建一个新的 用于存放接下来解析的数据
            this.tmpList = new ArrayList<>(batchLine);

            // 解析阶段二 涉及数据库层面的解析
            this.doAsync(_tmpList);
        }

    }

    /**
     * Excel中所有数据解析完毕会调用此方法
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

        // 将最后解析的剩下数据tmpList进行数据库层面的解析
        // 解析阶段二 涉及数据库层面的解析
        this.doAsync(tmpList);

        // 将第一阶段解析出来的错误数据放入缓存
        if (!CollectionUtils.isEmpty(failList)) {

            EXCEL_DATA_2_DB.submit(new Runnable() {
                @Override
                public void run() {

                    // 两个动作放在一起
                    redisTemplate.opsForList().leftPushAll(batchNo + ":FAIL", failList);
                    redisTemplate.expire(batchNo + ":FAIL", 30, TimeUnit.MINUTES);
                }
            });

            redisTemplate.opsForValue().increment(batchNo + ":FAILSIZE", failList.size());
            redisTemplate.expire(batchNo + ":FAILSIZE", 30, TimeUnit.MINUTES);
        }

    }


    private void doAsync(List<T> tmpList) {

        CompletableFuture<ParseExcelRsp> completableFuture =
                CompletableFuture.supplyAsync(() -> parser.parseInDB(tmpList, batchNo, batchLine), PARSE_POOL).whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("线程["+Thread.currentThread().getName()+"]解析" + tmpList.size() + "条数据出现异常", throwable);
                    }
                });

        parseRetList.add(completableFuture);
    }

    @Override
    public boolean hasNext(AnalysisContext analysisContext) {
        return true;
    }


}

