package com.infuq.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.infuq.common.req.DownloadStoreCustomerOrderTemplateReq;
import com.infuq.common.rsp.DownloadStoreCustomerOrderTemplateRsp;
import com.infuq.entity.StoreCustomerOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface StoreCustomerOrderMapper extends BaseMapper<StoreCustomerOrder> {

    List<DownloadStoreCustomerOrderTemplateRsp> downloadTemplate(@Param("condition") DownloadStoreCustomerOrderTemplateReq request);

}
