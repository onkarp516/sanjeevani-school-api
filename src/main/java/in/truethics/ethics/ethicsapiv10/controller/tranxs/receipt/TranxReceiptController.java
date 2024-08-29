package in.truethics.ethics.ethicsapiv10.controller.tranxs.receipt;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.tranx_service.receipt.TranxReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TranxReceiptController {

    @Autowired
    private TranxReceiptService service;

    /* Count purchase invoices */
    @GetMapping(path = "/get_receipt_invoice_last_records")
    public Object receiptLastRecord(HttpServletRequest request) {
        JsonObject result = service.receiptLastRecord(request);
        return result.toString();
    }

    /* get sundry debtors list and indirect incomes for receipt */
    @GetMapping(path = "/get_sundry_debtors_indirect_incomes")
    public Object getSundryDebtorsAndIndirectIncomes(HttpServletRequest request) {
        JsonObject result = service.getSundryDebtorsAndIndirectIncomes(request);
        return result.toString();
    }

    /* Sundry debtors pending Bills */

   @PostMapping(path = "/get_debtors_pending_bills")
    public Object getDebtorsPendingBills(HttpServletRequest request) {
        JsonObject array = service.getDebtorsPendingBills(request);
        return array.toString();
    }

    /* Create Receipt *//*
    @PostMapping(path = "/create_receipt")
    public Object createReceipt(HttpServletRequest request) {
        JsonObject array = service.createReceipt(request);
        return array.toString();
    }
*/
    /* Get List of receipts   */
    @GetMapping(path = "/get_receipt_list_by_outlet")
    public Object receiptListbyOutlet(HttpServletRequest request) {
        JsonObject array = service.receiptListbyOutlet(request);
        return array.toString();
    }

    @PostMapping(path = "get_receipt_details")
    public Object getReceiptDetails(HttpServletRequest request) {
        JsonObject object = service.getReceiptDetails(request);
        return object.toString();
    }


//    @GetMapping(path = "/get_receipt_list_by_outlet")
//    public void get_receipt_list_by_outlet(HttpServletRequest request){
//        System.out.println("called fun get_receipt_list_by_outlet");
//    }




}
