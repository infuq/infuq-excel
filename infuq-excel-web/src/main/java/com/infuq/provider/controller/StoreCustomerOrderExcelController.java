package com.infuq.provider.controller;


import com.infuq.common.req.DownloadStoreCustomerOrderTemplateCondition;
import com.infuq.common.rsp.DownloadStoreCustomerOrderTemplateHead;
import com.infuq.common.rsp.ParseExcelRsp;
import com.infuq.provider.service.StoreCustomerOrderDownloadService;
import com.infuq.service.download.easyexcel.StoreCustomerOrderExcelDownloader;
import com.infuq.service.upload.easyexcel.StoreCustomerOrderExcelParser;
import com.infuq.util.download.easyexcel.ExcelDownloadService;
import com.infuq.util.upload.easyexcel.ExcelParseService;
import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;

@RestController
@RequestMapping("storeCustomerOrderExcel")
public class StoreCustomerOrderExcelController {

    @Resource
    private ExcelParseService parseService;
    @Resource
    private StoreCustomerOrderExcelParser parser; // 上传文件使用的解析器

    @Resource
    private ExcelDownloadService downloadService;
    @Resource
    private StoreCustomerOrderExcelDownloader downloader; // 下载文件使用的下载器

    @Resource
    private StoreCustomerOrderDownloadService storeCustomerOrderDownloadService;
    @Resource
    private ExcelDownloadService storeCustomerOrderAsyncDownloadExcel;

    /**
     * 异步下载数据
     */
    @GetMapping("asyncDownloadTemplate")
    public void asyncDownloadTemplate() throws Exception {
        DownloadStoreCustomerOrderTemplateCondition req = new DownloadStoreCustomerOrderTemplateCondition();
        storeCustomerOrderDownloadService.asyncDownloadTemplate(req);
    }

    /**
     * 同步下载数据
     */
    @GetMapping("downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) throws Exception {

        StopWatch watcher = new StopWatch();
        watcher.start("下载订货单模板数据");

        OutputStream outputStream = response.getOutputStream();

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("全部订货单下载模板.xlsx", "UTF-8"));


        DownloadStoreCustomerOrderTemplateCondition condition = new DownloadStoreCustomerOrderTemplateCondition();

        downloadService.download(downloader, condition, DownloadStoreCustomerOrderTemplateHead.class, outputStream, () -> {
            // 回调
        });


        outputStream.flush();
        outputStream.close();

        watcher.stop();
        System.out.println("线程:"+Thread.currentThread().getName()+"解析下载耗时" + watcher.getLastTaskTimeMillis() + "毫秒");



    }



    /**
     * 上传
     */
    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ParseExcelRsp upload(@RequestPart("file") MultipartFile file) {
        try {
            // 采用EasyExcel处理, 不同的业务使用不同的parser
            return parseService.parse(file.getInputStream(), parser);
        } catch (Exception e) {

        }
        return ParseExcelRsp.builder()
                .successSize(0)
                .failSize(0)
                .build();
    }


}
