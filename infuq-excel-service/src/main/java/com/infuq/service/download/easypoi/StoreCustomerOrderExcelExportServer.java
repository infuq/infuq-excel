package com.infuq.service.download.easypoi;


import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 从数据库分页获取数据
 */
@Service
@Slf4j
public class StoreCustomerOrderExcelExportServer implements IExcelExportServer {

    @Override
    public List<Object> selectListForExcelExport(Object queryParams, int page) {
        return null;
    }


}

