package com.infuq.core.download.easypoi;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import com.aliyun.oss.OSS;
import com.infuq.common.req.Pager;
import com.infuq.config.OssConfig;
import com.infuq.core.download.easyexcel.WriteExcelFinishCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.OutputStream;

/**
 * 下载Excel
 */
@Service
@Slf4j
public class ExcelDownloadService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private OSS oss;
    @Resource
    private OssConfig ossConfig;


    /**
     * 将下载的数据写入到OutputStream
     *
     */
    public <T extends Pager> void download(IExcelExportServer downloader, T condition, Class<?> headClazz, OutputStream outputStream, WriteExcelFinishCallback callback) {


        ExportParams exportParams = new ExportParams(null, null, ExcelType.XSSF);
        exportParams.setDataHandler(null);
        exportParams.setDictHandler(null);

        Workbook workbook = ExcelExportUtil.exportBigExcel(exportParams, headClazz, downloader, condition);

        try {
            workbook.write(outputStream);
            workbook.close();

            // 回调
            callback.doFinish();
        } catch (Exception e) {

        }
    }




}

