package com.infuq.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.infuq.common.req.DownloadStoreCustomerOrderTemplateCondition;
import com.infuq.common.rsp.DownloadStoreCustomerOrderTemplateHead;
import com.infuq.entity.StoreCustomerOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface StoreCustomerOrderMapper extends BaseMapper<StoreCustomerOrder> {

    List<DownloadStoreCustomerOrderTemplateHead> downloadTemplate(@Param("condition") DownloadStoreCustomerOrderTemplateCondition request);

}
