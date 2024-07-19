package com.infuq.provider.controller;


import com.infuq.common.req.StoreCustomerOrderReq;
import com.infuq.provider.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("export")
public class ExportController {

    @Autowired
    private ExportService exportService;


    @GetMapping("exportStoreCustomerOrder")
    public void exportStoreCustomerOrder(@RequestParam("topic") String topic, @RequestParam("tag") String tag) {
        StoreCustomerOrderReq req = new StoreCustomerOrderReq();
        req.setTopic(topic);
        req.setTag(tag);
        exportService.exportStoreCustomerOrder(req);
    }


}
