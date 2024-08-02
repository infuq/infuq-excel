package com.infuq.provider.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.infuq.common.req.DownloadStoreCustomerOrderTemplateReq;
import com.infuq.common.rsp.DownloadStoreCustomerOrderTemplateRsp;
import com.infuq.handler.StoreCustomerOrderAsyncDownloadExcel;
import com.infuq.provider.service.StoreCustomerOrderDownloadService;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("storeCustomerOrder")
public class StoreCustomerOrderDownloadExcelController {

    @Resource
    private StoreCustomerOrderDownloadService storeCustomerOrderDownloadService;
    @Resource
    private StoreCustomerOrderAsyncDownloadExcel storeCustomerOrderAsyncDownloadExcel;

    /**
     * 异步下载数据
     */
    @GetMapping("asyncDownloadTemplate")
    public void asyncDownloadTemplate() throws Exception {
        DownloadStoreCustomerOrderTemplateReq req = new DownloadStoreCustomerOrderTemplateReq();
        storeCustomerOrderDownloadService.asyncDownloadTemplate(req);
    }

    /**
     * 同步下载数据
     */
    @GetMapping("downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("下载订货单模板数据");

        OutputStream outputStream = response.getOutputStream();

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("全部订货单下载模板.xlsx", "UTF-8"));


        DownloadStoreCustomerOrderTemplateReq request = new DownloadStoreCustomerOrderTemplateReq();
        // 底层也是调用异步下载数据的逻辑
        storeCustomerOrderAsyncDownloadExcel.download(request, outputStream, () -> {
            // 回调
        });


        outputStream.flush();
        outputStream.close();

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
