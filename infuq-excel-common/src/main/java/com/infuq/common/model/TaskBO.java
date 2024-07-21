package com.infuq.common.model;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class TaskBO {

    /**
     * 导出或导入的记录ID
     */
    private Long recordId;

}
