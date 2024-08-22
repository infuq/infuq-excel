package com.infuq.core.upload;

import com.infuq.common.rsp.ParseExcelRsp;

import java.util.List;

/**
 * Excel解析器
 */
public interface ExcelParser<R> {

    Class<?> headClazz();

    // 解析阶段一 不涉及数据库层面的解析 只是单一的判断数据是否没有填写
    void parse(R row, List<R> tmpList, List<R> failList);

    // 解析阶段二 涉及数据库层面的解析
    // 该线程需要处理 tmpList 里的数据
    ParseExcelRsp parseInDB(List<R> tmpList, String batchNo, int batchLine);

}

