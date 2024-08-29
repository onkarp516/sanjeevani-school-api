package in.truethics.ethics.ethicsapiv10.controller.tranxs.purchase;


import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.tranx_service.purchase.TranxPurChallanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TranxPurChallanController {

    @Autowired
    private TranxPurChallanService tranxPurChallanService;

    @PostMapping(path = "/create_po_challan_invoices")
    public ResponseEntity<?> createPOChallanInvoice(HttpServletRequest request) {
        return ResponseEntity.ok(tranxPurChallanService.insertPOChallanInvoice(request));
    }

    /* list of Purchase challans outletwise*/
    @GetMapping(path = "/list_po_challan_invoice")
    public Object poChallanInvoiceList(HttpServletRequest request) {
        JsonObject result = tranxPurChallanService.poChallanInvoiceList(request);
        return result.toString();
    }


    @PostMapping(path = "/get_po_challan_invoices_with_ids")
    public Object getPOChallanInvoiceWithIds(HttpServletRequest request) {
        JsonObject result = tranxPurChallanService.getPOChallanInvoiceWithIds(request);
        return result.toString();
    }

    /* Count pc invoices */
    @GetMapping(path = "/get_last_po_challan_record")
    public Object getChallanRecord(HttpServletRequest request) {
        JsonObject result = tranxPurChallanService.getChallanRecord(request);
        return result.toString();
    }

    /* Pending Purchase challan  */
    @PostMapping(path = "/pC_pending_challans")
    public Object pCPendingOrder(HttpServletRequest request) {
        JsonObject result = tranxPurChallanService.pCPendingOrder(request);
        return result.toString();
    }
}

