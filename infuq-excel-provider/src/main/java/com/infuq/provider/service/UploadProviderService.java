package com.infuq.provider.service;


import com.infuq.upload.EasyExcelUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


@Service
@Slf4j
public class UploadProviderService {

    @Resource
    private EasyExcelUploadService easyExcelUploadService;

    public void uploadExcel(MultipartFile file) {
        try {
            easyExcelUploadService.handleUpload(file.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
