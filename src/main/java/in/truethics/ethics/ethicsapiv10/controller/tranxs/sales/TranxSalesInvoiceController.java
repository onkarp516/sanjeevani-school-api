package in.truethics.ethics.ethicsapiv10.controller.tranxs.sales;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.tranx_service.sales.TranxSalesInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TranxSalesInvoiceController {

    @Autowired
    private TranxSalesInvoiceService service;


    // For Sales Invoice List Only
//    @GetMapping(path = "/list_sale_invoice")
//    public Object saleList(HttpServletRequest request) {
//        JsonObject result = service.saleList(request);
//        return result.toString();
//    }
    // For Sales Invoice List From And To Date or Current Date
    @PostMapping(path = "/list_sale_invoice")
    public Object saleList(HttpServletRequest request) {
        JsonObject result = service.saleList(request);
        return result.toString();
    }
    @PostMapping(path = "/getProfitLossDetails")
    public Object dateWiseTotalAmtBySalesAcc(HttpServletRequest request) {
        JsonObject result = service.dateWiseTotalAmtBySalesAcc(request);
        return result.toString();
    }

}
