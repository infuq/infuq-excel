package com.infuq.common.req;


import lombok.Data;

@Data
public class StoreCustomerOrderReq {

    private String warehouseId;

    private Long userId;

    private String topic;

    private String tag;

}
