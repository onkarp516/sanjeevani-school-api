package in.truethics.ethics.ethicsapiv10.controller.tranxs.purchase;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.tranx_service.purchase.TranxPurReturnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TranxPurReturnsController {
    @Autowired
    private TranxPurReturnsService service;

    /* create Tranx Purchase returns Invoice  */
    @PostMapping(path = "/create_purchase_returns_invoices")
    public ResponseEntity<?> createPurReturnsInvoices(HttpServletRequest request) {
        return ResponseEntity.ok(service.createPurReturnsInvoices(request));
    }

    /* get last records of Purchase Returns  */
    @GetMapping(path = "/get_last_pur_returns_record")
    public Object purReturnsLastRecord(HttpServletRequest request) {
        JsonObject result = service.purReturnsLastRecord(request);
        return result.toString();
    }

    /* get all purchase returns of Outlet */
    @GetMapping(path = "/get_pur_returns_by_outlet")
    public Object purReturnsByOutlet(HttpServletRequest request) {
        JsonObject result = service.purReturnsByOutlet(request);
        return result.toString();
    }

    /* find all Purchase Invoices And Purchase Challans of Sundry Creditors/Suppliers wise , for Purchase Returns */
    @PostMapping(path = "/list_pur_invoice_supplier_wise")
    public Object purchaseListSupplierWise(HttpServletRequest request) {
        JsonObject result = service.purchaseListSupplierWise(request);
        return result.toString();
    }

    /*............ Purchase Returns ................  */
    /* Purchase Returns:  find all products of selected purchase invoice bill of sundry creditor */
    @PostMapping(path = "/list_pur_invoice_product_list")
    public Object productListPurInvoice(HttpServletRequest request) {
        JsonObject result = service.productListPurInvoice(request);
        return result.toString();
    }

    /* list of all selected products against purchase invoice bill for purchase returns */
    @PostMapping(path = "/get_pur_invoice_by_id_with_pr_ids")
    public Object getInvoiceByIdWithProductsId(HttpServletRequest request) {
        JsonObject result = service.getInvoiceByIdWithProductsId(request);
        return result.toString();
    }
}
