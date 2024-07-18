package com.infuq.common.enums;


public enum FileStatus {

    CREATE_SUCCESS(1, "创建成功"),
    EXPORTING(2, "正在生成"),
    EXPORT_ERROR(3, "生成失败"),
    WAIT_DOWNLOAD(4, "待下载"),
    DOWNLOADED(5, "已下载"),
    EXPIRE(6, "已过期");

    FileStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    private final int code;
    private final String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
