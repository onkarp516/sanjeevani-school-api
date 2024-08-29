package in.truethics.ethics.ethicsapiv10.controller.tranxs.purchase;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.tranx_service.purchase.TranxPurInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TranxPurInvoiceController {
    @Autowired
    private TranxPurInvoiceService service;

    /* getting pending amount of sundry creditors againt purchase return(new reference)   */
   /* @PostMapping(path = "/get_outstanding_pur_return_amt")
    public Object getOutStandingPurchaseReturnAmt(HttpServletRequest request) {
        JsonObject result = service.getOutStandingPurchaseReturnAmt(request);
        return result.toString();
    }*/

    /* create Tranx Purchase Invoice  */
    @PostMapping(path = "/create_purchase_invoices")
    public ResponseEntity<?> createPurInvoices(HttpServletRequest request) {
        return ResponseEntity.ok(service.insertPurchaseInvoices(request));
    }

    /* edit functionality of Purchase  */
    @PostMapping(path = "/edit_purchase_invoices")
    public ResponseEntity<?> purchaseEdit(HttpServletRequest request) {
        return ResponseEntity.ok(service.editPurchaseInvoice(request));
    }

    /* Count purchase invoices */
    @GetMapping(path = "/get_last_invoice_record")
    public Object purchaseLastRecord(HttpServletRequest request) {
        JsonObject result = service.purchaseLastRecord(request);
        return result.toString();
    }

    /* find all purchase invoices outletwise */
    @GetMapping(path = "/list_purchase_invoice")
    public Object purchaseList(HttpServletRequest request) {
        JsonObject result = service.purchaseList(request);
        return result.toString();
    }

    /* get Tranx Purchase invoice by id */
    @PostMapping(path = "/get_purchase_invoice_by_id")
    public Object getPurchaseInvoiceById(HttpServletRequest request) {
        JsonObject result = service.getPurchaseInvoiceById(request);
        return result.toString();
    }
}
