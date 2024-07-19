package com.infuq.common.req;

import lombok.Data;

@Data
public class StoreCustomerOrderReq {

    /**
     * 仓库
     */
    private String warehouseId;

    /**
     * 用户
     */
    private Long userId;

}
