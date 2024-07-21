package com.infuq.upload;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSON;
import com.infuq.model.ExportRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@Slf4j
public class EasyExcelUploadService {


    public void handleUpload(InputStream in) {


        EasyExcelFactory.read(in, ExportRecordVO.class, new ReadListener<ExportRecordVO>() {

                    @Override
                    public void invoke(ExportRecordVO row, AnalysisContext analysisContext) {
                        log.info("行数据:" + JSON.toJSONString(row));
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                    }
                })
                .sheet(0)
                .doRead();


    }

}
