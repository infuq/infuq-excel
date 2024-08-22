package com.infuq.common.req;

import lombok.Data;

@Data
public class DownloadStoreCustomerOrderTemplateCondition extends Pager {

    /**
     * 仓库
     */
    private String warehouseId;

    /**
     * 用户
     */
    private Long userId;

    /**
     * 企业
     */
    private Long enterpriseId;

}
