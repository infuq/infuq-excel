package com.infuq.common.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Builder
@Data
public class ExportTaskDTO {

    /**
     * 记录ID
     */
    private Long exportRecordId;

}
