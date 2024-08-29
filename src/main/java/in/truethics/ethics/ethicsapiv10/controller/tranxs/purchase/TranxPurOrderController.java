package in.truethics.ethics.ethicsapiv10.controller.tranxs.purchase;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.tranx_service.purchase.TranxPurOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TranxPurOrderController {
    @Autowired
    private TranxPurOrderService tranxPurOrderService;

    /* creating purchase order */
    @PostMapping(path = "/create_po_invoices")
    public ResponseEntity<?> createPOInvoice(HttpServletRequest request) {
        return ResponseEntity.ok(tranxPurOrderService.insertPOInvoice(request));
    }

    /*edit purchase order */
    @PostMapping(path = "/edit_pur_order")
    public ResponseEntity<?> editPOInvoice(HttpServletRequest request) {
        return ResponseEntity.ok(tranxPurOrderService.editPOInvoice(request));
    }

    /* List of Purchase orders :outlet wise */
    @GetMapping(path = "/list_po_invoice")
    public Object poInvoiceList(HttpServletRequest request) {
        JsonObject result = tranxPurOrderService.poInvoiceList(request);
        return result.toString();
    }

    /* Count po invoices */
    @GetMapping(path = "/get_last_po_invoice_record")
    public Object purchaseLastRecord(HttpServletRequest request) {
        JsonObject result = tranxPurOrderService.poLastRecord(request);
        return result.toString();
    }

    @PostMapping(path = "/po_pending_order")
    public Object poPendingOrder(HttpServletRequest request) {
        JsonObject result = tranxPurOrderService.poPendingOrder(request);
        return result.toString();
    }

    /* Conversion to Purchase Order to challan or invoice */
    @PostMapping(path = "/get_po_invoices_with_ids")
    public Object getPOInvoiceWithIds(HttpServletRequest request) {
        JsonObject result = tranxPurOrderService.getPOInvoiceWithIds(request);
        return result.toString();
    }

    @GetMapping(path = "/get_po")
    public ResponseEntity<?> getAllPo() {
        return ResponseEntity.ok(tranxPurOrderService.getAllPo());
    }

    @PostMapping(path = "/get_purchase_order_by_id")
    public Object getPurchaseOrderById(HttpServletRequest request) {
        JsonObject result = tranxPurOrderService.getPurchaseOrderById(request);
        return result.toString();
    }

    /* validating for same sundry creditors while converting order into challan or invoices,throw exception if
       two different creditors selected for convertions  */
    @PostMapping(path = "/get_po_invoice_ids")
    public Object getPOInvoiceIds(HttpServletRequest request) {
        JsonObject result = tranxPurOrderService.getPOInvoiceIds(request);
        return result.toString();
    }
}
