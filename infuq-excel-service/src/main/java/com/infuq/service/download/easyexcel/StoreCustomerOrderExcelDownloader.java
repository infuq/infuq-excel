package com.infuq.service.download.easyexcel;


import com.infuq.common.req.DownloadStoreCustomerOrderTemplateCondition;
import com.infuq.mapper.StoreCustomerOrderMapper;
import com.infuq.util.download.ExcelDownloader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 从数据库分页获取数据
 */
@Service
@Slf4j
public class StoreCustomerOrderExcelDownloader implements ExcelDownloader<DownloadStoreCustomerOrderTemplateCondition> {


    @Resource
    private StoreCustomerOrderMapper storeCustomerOrderMapper;

    @Override
    public List<Object> selectList(DownloadStoreCustomerOrderTemplateCondition condition) {
        return null;
    }
}

