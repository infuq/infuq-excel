package com.infuq.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * 订货单
 */
@Builder
@Data
@TableName("store_customer_order")
public class StoreCustomerOrder {

    /**
     * 记录ID
     */
    @TableId
    private Long storeCustomerOrderId;




}
