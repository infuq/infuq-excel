package com.infuq.common.enums;

public enum BusinessTypeEnum {


    STORE_CUSTOMER_ORDER(1, "订货单信息"),
    STORE_RETURN_ORDER(2, "退货单信息")

    ;

    BusinessTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private Integer value;
    private String desc;

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
