package com.infuq.provider.controller;


import com.infuq.common.req.StoreCustomerOrderReq;
import com.infuq.provider.service.ExportProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("export")
public class ExcelController {

    @Autowired
    private ExportProviderService exportProviderService;

    @GetMapping("checkConn")
    public String checkConn() {
        return "check success...";
    }

    @GetMapping("exportStoreCustomerOrder")
    public void exportStoreCustomerOrder() throws Exception {
        StoreCustomerOrderReq req = new StoreCustomerOrderReq();
        exportProviderService.exportStoreCustomerOrder(req);
    }


}
