package com.infuq.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


@Data
public class ExportRecordVO {

    @ExcelProperty("文件名")
    private String fileName;

    @ExcelProperty("文件后缀")
    private String fileSuffix;

}
