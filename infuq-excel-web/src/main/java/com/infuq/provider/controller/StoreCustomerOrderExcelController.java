package com.infuq.provider.controller;


import com.infuq.common.rsp.ParseExcelRsp;
import com.infuq.service.upload.StoreCustomerOrderExcelParser;
import com.infuq.util.upload.easyexcel.ExcelParseService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("storeCustomerOrderExcel")
public class StoreCustomerOrderExcelController {

    @Resource
    private ExcelParseService parseService;
    @Resource
    private StoreCustomerOrderExcelParser parser;

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
