package com.infuq.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 下载记录
 */
@Builder
@Data
@TableName("download_record")
public class DownloadRecord {

    /**
     * 记录ID
     */
    @TableId
    private Long recordId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 业务类型
     */
    private Integer businessType;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 状态
     */
    private Integer fileStatus;

    /**
     * 文件下载地址
     */
    private String fileDownloadUrl;

    /**
     * 请求参数
     */
    private String requestBody;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;




}
