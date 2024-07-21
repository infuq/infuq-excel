package com.infuq.export;

import com.infuq.common.model.TaskBO;
import com.infuq.mapper.ExportRecordMapper;
import com.infuq.model.ExportRecordVO;
import com.infuq.util.easyexcel.EasyExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EasyExcelExportService {

    @Resource
    private ExportRecordMapper exportRecordMapper;


    public void handleExport(TaskBO task) {

//        Long recordId = task.getRecordId();
//        ExportRecord exportRecord = exportRecordMapper.selectById(recordId);

        // 构造数据
        List<ExportRecordVO> dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ExportRecordVO r = new ExportRecordVO();
            r.setFileName("全部订货单" + i);
            r.setFileSuffix(i % 2 == 0 ? "xlsx" :  "pdf");
            dataList.add(r);
        }

        // 写入Excel
        byte[] bytes = EasyExcelUtil.write(0, "全部订货单", ExportRecordVO.class, dataList);


        // 上传到OSS


        try {
            File file = new File("/Users/infuq/tmp/1.xlsx");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {

        }
    }


}
