package com.infuq.core.download.easyexcel;

import java.util.List;

/**
 * Excel下载器
 */
public interface ExcelDownloader<R> {

    // 从数据库获取数据
    List<Object> selectList(R condition);

}

