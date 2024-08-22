package com.infuq.common.req;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreCustomerOrderHead {

    @ExcelProperty(value = "门店名称")
    private String storeName;

    @ExcelProperty(value = "订单号")
    private String storeCustomerOrderNo;

    @ExcelProperty(value = "错误原因")
    private String errorCause;


}
