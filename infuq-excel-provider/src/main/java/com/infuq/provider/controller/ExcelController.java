package com.infuq.provider.controller;


import com.infuq.common.req.StoreCustomerOrderReq;
import com.infuq.provider.service.ExportProviderService;
import com.infuq.provider.service.UploadProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("export")
public class ExcelController {

    @Autowired
    private ExportProviderService exportProviderService;
    @Autowired
    private UploadProviderService uploadProviderService;

    @GetMapping("checkConn")
    public String checkConn() {
        return "check success...";
    }

    /**
     * 导出
     */
    @GetMapping("exportStoreCustomerOrder")
    public void exportStoreCustomerOrder() throws Exception {
        StoreCustomerOrderReq req = new StoreCustomerOrderReq();
        exportProviderService.exportStoreCustomerOrder(req);
    }

    /**
     * 上传
     */
    @PostMapping("uploadExcel")
    public void uploadExcel(@RequestParam("file") MultipartFile file) {
        uploadProviderService.uploadExcel(file);
    }


}
