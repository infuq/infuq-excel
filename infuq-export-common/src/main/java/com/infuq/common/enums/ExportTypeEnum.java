package com.infuq.common.enums;

public enum ExportTypeEnum {


    CUSTOMER_ORDER_INFO("0001", "导出订货单信息")

    ;

    ExportTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private String value;
    private String desc;

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
