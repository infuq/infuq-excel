package com.infuq.util.easyexcel;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SheetData {

    private int sheetNo;

    private Class<?> headClazz;

    private String sheetName;

    private List<?> data;

}

