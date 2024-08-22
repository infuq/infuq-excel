package com.infuq.util.download.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.aliyun.oss.OSS;
import com.infuq.common.req.DownloadStoreCustomerOrderTemplateCondition;
import com.infuq.common.req.Pager;
import com.infuq.config.OssConfig;
import com.infuq.mapper.StoreCustomerOrderMapper;
import com.infuq.util.download.ExcelDownloader;
import com.infuq.util.download.WriteExcelFinishCallback;
import com.infuq.util.download.easyexcel.converter.LocalDateStringConverter;
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
public class ExcelDownloadService {

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


        DownloadStoreCustomerOrderTemplateCondition request = new DownloadStoreCustomerOrderTemplateCondition();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        download(request, outputStream, () -> {
//            // 回调
//        });

        byte[] byteArray = outputStream.toByteArray();

        String key = "download/2024-08-02/全部订货单下载模板.xlsx";
        oss.putObject(ossConfig.getBucketNames(), key, new ByteArrayInputStream(byteArray));

        return "https://" + ossConfig.getBucketNames() + "." + ossConfig.getEndpoint() + "/" + key;
    }


    public <T extends Pager> void download(ExcelDownloader<T> downloader, T condition, Class<?> headClazz, OutputStream outputStream, WriteExcelFinishCallback callback) {

        StopWatch watcher = new StopWatch();
        watcher.start("下载数据");

        ExcelWriter writer = EasyExcel
                .write(outputStream)
                //.withTemplate() 模板
                //.inMemory(true) 是否在内存处理,默认会生成临时文件以节约内存.内存模式效率会更好,但是容易OOM
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerConverter(new LocalDateStringConverter())
                //.excelType(ExcelTypeEnum.XLSX) 类型
                //.autoCloseStream(true) // 自动关闭写入的流
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
            condition.setOffset((curPage - 1) * rowCount);
            condition.setRowCount(rowCount);
            List<Object> dList = downloader.selectList(condition);

            if (CollectionUtils.isNotEmpty(dList)) {
                sheetLine = sheetLine + dList.size();

                // 达到每个Sheet的行数上限
                if (sheetLine >= sheetMaxLine) {

                    System.out.println("线程:" + Thread.currentThread().getName() + "查询第 " + curPage + " 页数据,每页 " + rowCount + " 条,写入第 " + (sheetNo + 1) + " 个Sheet.");
                    writeExcel(sheetNo, writer, dList, headClazz);

                    // 切换到新的Sheet
                    sheetNo = sheetNo + 1;
                    sheetLine = 0;

                    // 下一页
                    curPage = curPage + 1;

                    continue;
                }

                // 还没有达到每个Sheet的行数上限
                System.out.println("线程:" + Thread.currentThread().getName() + "查询第 " + curPage + " 页数据,每页 " + rowCount + " 条,写入第 " + (sheetNo + 1) + " 个Sheet.");
                writeExcel(sheetNo, writer, dList, headClazz);

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


        watcher.stop();
        System.out.println("线程:"+Thread.currentThread().getName()+"解析下载耗时" + watcher.getLastTaskTimeMillis() + "毫秒");

    }


    private void writeExcel(int sheetNo, ExcelWriter writer, List<?> data, Class<?> headClazz) {
        WriteSheet sheet = EasyExcel
                .writerSheet(sheetNo)
                .sheetName("全部订货单下载模板" + ((sheetNo > 0) ? ("-" + sheetNo) : ""))
                .head(headClazz)
                .build();
        writer.write(data, sheet);
    }









}

