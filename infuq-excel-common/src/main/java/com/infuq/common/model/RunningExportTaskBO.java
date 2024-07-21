package com.infuq.common.model;


import lombok.Data;

@Data
public class RunningExportTaskBO {

    /**
     * 导出记录ID
     */
    private Long exportRecordId;

    /**
     * 开始运行时间
     */
    private String startRunningTime;

    /**
     * 运行该任务的线程名称
     */
    private String threadName;

}
