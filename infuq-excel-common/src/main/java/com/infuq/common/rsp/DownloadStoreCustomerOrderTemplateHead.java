package com.infuq.common.rsp;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DownloadStoreCustomerOrderTemplateHead {

    @ExcelProperty(value = "单号")
    private String customerOrderNo;

    @ExcelProperty(value = "联系人")
    private String linkman;

    @ExcelProperty(value = "电话")
    private String phone;

    @ExcelProperty(value = "地址")
    private String address;

    @ExcelProperty(value = "仓库")
    private String warehouseName;

    @ExcelProperty(value = "企业")
    private String enterpriseName;

    @ExcelProperty(value = "货主")
    private String goodsOwner;

    @ExcelProperty(value = "收货人")
    private String customerName;

    @ExcelProperty(value = "创建时间")
    private String createTime;

}
