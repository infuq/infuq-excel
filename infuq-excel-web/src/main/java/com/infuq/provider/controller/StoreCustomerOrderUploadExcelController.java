package com.infuq.provider.controller;


import com.infuq.handler.StoreCustomerOrderUploadExcelParser;
import com.infuq.common.req.StoreCustomerOrderUploadReq;
import com.infuq.common.rsp.ParseExcelRsp;
import com.infuq.upload.EasyExcelUploadService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("storeCustomerOrder")
public class StoreCustomerOrderUploadExcelController {

    @Resource
    private EasyExcelUploadService easyExcelUploadService;
    @Resource
    private StoreCustomerOrderUploadExcelParser parser;

    /**
     * 上传
     */
    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ParseExcelRsp upload(@RequestPart("file") MultipartFile file) {
        try {
            // 采用EasyExcel处理
            return easyExcelUploadService.handleUpload(file.getInputStream(), StoreCustomerOrderUploadReq.class, parser);
        } catch (Exception e) {

        }
        return ParseExcelRsp.builder()
                .successSize(0)
                .failSize(0)
                .build();
    }


}
