package com.infuq.handler;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.aliyun.oss.OSS;
import com.infuq.common.req.DownloadStoreCustomerOrderTemplateReq;
import com.infuq.common.rsp.DownloadStoreCustomerOrderTemplateRsp;
import com.infuq.config.OssConfig;
import com.infuq.mapper.StoreCustomerOrderMapper;
import com.infuq.util.easyexcel.WriteExcelFinishCallback;
import com.infuq.util.easyexcel.convert.LocalDateStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 下载Excel
 */
@Service
@Slf4j
public class StoreCustomerOrderAsyncDownloadExcel {

    @Resource
    private StoreCustomerOrderMapper storeCustomerOrderMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private OSS oss;
    @Resource
    private OssConfig ossConfig;

    //
    public String download(String requestBody) {


        DownloadStoreCustomerOrderTemplateReq request = new DownloadStoreCustomerOrderTemplateReq();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        download(request, outputStream, () -> {
            // 回调
        });

        byte[] byteArray = outputStream.toByteArray();

        String key = "download/2024-08-02/全部订货单下载模板.xlsx";
        oss.putObject(ossConfig.getBucketNames(), key, new ByteArrayInputStream(byteArray));

        return "https://" + ossConfig.getBucketNames() + "." + ossConfig.getEndpoint() + "/" + key;
    }


    public void download(DownloadStoreCustomerOrderTemplateReq request, OutputStream outputStream, WriteExcelFinishCallback callback) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("下载订货单模板数据");

        ExcelWriter writer = EasyExcel
                .write(outputStream)
                //.inMemory(true)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerConverter(new LocalDateStringConverter())
                .build();


        int sheetNo = 0;

        // 每个Sheet的行数上限
        int sheetMaxLine = 100000;
        int sheetLine = 0;

        // 当前页
        int curPage = 1;
        // 分页查询每页数量
        int rowCount = 10000;
        for (;;) {
            // 分页查询
            request.setOffset((curPage - 1) * rowCount);
            request.setRowCount(rowCount);
            List<DownloadStoreCustomerOrderTemplateRsp> dList = storeCustomerOrderMapper.downloadTemplate(request);
            if (CollectionUtils.isNotEmpty(dList)) {
                sheetLine = sheetLine + dList.size();

                // 达到每个Sheet的行数上限
                if (sheetLine >= sheetMaxLine) {

                    System.out.println("线程:" + Thread.currentThread().getName() + "查询第 " + curPage + " 页数据,每页 " + rowCount + " 条,写入第 " + (sheetNo + 1) + " 个Sheet.");
                    write(sheetNo, writer, dList);

                    // 切换到新的Sheet
                    sheetNo = sheetNo + 1;
                    sheetLine = 0;

                    // 下一页
                    curPage = curPage + 1;

                    continue;
                }

                // 还没有达到每个Sheet的行数上限
                System.out.println("线程:" + Thread.currentThread().getName() + "查询第 " + curPage + " 页数据,每页 " + rowCount + " 条,写入第 " + (sheetNo + 1) + " 个Sheet.");
                write(sheetNo, writer, dList);

            } else {
                // 没有数据了
                break;
            }

            // 下一页
            curPage = curPage + 1;
        }

        writer.finish();
        writer.close();

        // 回调
        callback.doFinish();


        stopWatch.stop();
        System.out.println("线程:"+Thread.currentThread().getName()+"解析下载耗时" + stopWatch.getLastTaskTimeMillis() + "毫秒");

    }


    private void write(int sheetNo, ExcelWriter writer, List<?> data) {
        WriteSheet sheet = EasyExcel
                .writerSheet(sheetNo)
                .sheetName("全部订货单下载模板" + ((sheetNo > 0) ? ("-" + sheetNo) : ""))
                .head(DownloadStoreCustomerOrderTemplateRsp.class)
                .build();
        writer.write(data, sheet);
    }









}

