package com.infuq.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导出记录
 */
@Builder
@Data
@TableName("export_record")
public class ExportRecord {

    /**
     * 记录ID
     */
    @TableId
    private Long exportRecordId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 类型
     */
    private String fileTypeDesc;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 状态
     */
    private Integer fileStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 文件下载地址
     */
    private String fileDownloadUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 请求参数
     */
    private String requestBody;

    /**
     * 导出类型值
     *
     */
    private String exportType;


}
