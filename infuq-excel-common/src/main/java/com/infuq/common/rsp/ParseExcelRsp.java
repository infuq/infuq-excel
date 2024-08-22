package com.infuq.common.rsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParseExcelRsp {

    private String batchNo;

    private Integer successSize;

    private Integer failSize;

}
