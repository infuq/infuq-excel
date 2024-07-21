package com.infuq.common.enums;

public enum BusinessTypeEnum {


    STORE_CUSTOMER_ORDER("0001", "订货单信息"),
    STORE_RETURN_ORDER("0002", "退货单信息")

    ;

    BusinessTypeEnum(String value, String desc) {
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
