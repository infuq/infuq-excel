package com.infuq.util.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.infuq.util.easyexcel.convert.LocalDateStringConverter;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class EasyExcelUtil {


    /**
     *
     * @param sheetNo
     * @param sheetName
     * @param headClazz
     * @param data   分页数据
     * @return
     */
    public static void write(int sheetNo, String sheetName, Class<?> headClazz, List<?> data, OutputStream outputStream, WriteExcelFinishCallback callback) {

        SheetData sheetData = SheetData.builder()
                .sheetNo(sheetNo)
                .sheetName(sheetName)
                .headClazz(headClazz)
                .data(data)
                .build();

        List<SheetData> sheetDataList = new ArrayList<>();
        sheetDataList.add(sheetData);

        write(sheetDataList, outputStream, callback);

    }


    public static void write(List<SheetData> sheetDataList, OutputStream out, WriteExcelFinishCallback callback) {

        // 1.writer
        ExcelWriter writer = EasyExcel
                .write(out)
                //.withTemplate() 模板
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerConverter(new LocalDateStringConverter())
                //.inMemory(true) 是否在内存处理,默认会生成临时文件以节约内存.内存模式效率会更好,但是容易OOM
                //.excelType(ExcelTypeEnum.XLSX) 类型
                //.autoCloseStream(true) // 自动关闭写入的流
                .build();

        // 2.写入Sheet
        sheetDataList.forEach(sheetData -> {
            WriteSheet sheet = EasyExcel
                    .writerSheet(sheetData.getSheetNo())
                    .sheetName(sheetData.getSheetName())
                    .head(sheetData.getHeadClazz())
                    .build();
            writer.write(sheetData.getData(), sheet);
            //writer.fill();
        });

        callback.doFinish();

        // 3.
        writer.finish();
        writer.close();

    }



}
