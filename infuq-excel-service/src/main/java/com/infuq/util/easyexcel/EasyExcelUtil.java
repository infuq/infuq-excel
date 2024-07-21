package com.infuq.util.easyexcel;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.infuq.util.easyexcel.convert.LocalDateStringConverter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EasyExcelUtil {


    public static byte[] write(int sheetNo, String sheetName, Class<?> headClazz, List<?> data) {

        SheetData sheetData = SheetData.builder()
                .sheetNo(sheetNo)
                .sheetName(sheetName)
                .headClazz(headClazz)
                .data(data)
                .build();

        List<SheetData> sheetDataList = new ArrayList<>();
        sheetDataList.add(sheetData);

        return write(sheetDataList);

    }


    public static byte[] write(List<SheetData> sheetDataList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // 1.writer
        ExcelWriter writer = EasyExcelFactory
                .write(out)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerConverter(new LocalDateStringConverter())
                .build();

        // 2.写入Sheet
        sheetDataList.forEach(sheetData -> {
            WriteSheet sheet = EasyExcelFactory
                    .writerSheet(sheetData.getSheetNo())
                    .head(sheetData.getHeadClazz())
                    .sheetName(sheetData.getSheetName())
                    .build();
            writer.write(sheetData.getData(), sheet);
        });

        // 3.
        writer.finish();

        return out.toByteArray();
    }



}
