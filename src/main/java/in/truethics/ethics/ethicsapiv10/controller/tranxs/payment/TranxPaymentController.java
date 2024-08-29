package in.truethics.ethics.ethicsapiv10.controller.tranxs.payment;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.tranx_service.payment.TranxPaymentNewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TranxPaymentController {


    @Autowired
    private TranxPaymentNewService service;

    /* Count purchase invoices */
    @GetMapping(path = "/get_payment_invoice_last_records")
    public Object paymentLastRecord(HttpServletRequest request) {
        JsonObject result = service.paymentLastRecord(request);
        return result.toString();
    }

    /* get sundry creditors list and indirect expenses for Payment */
    @GetMapping(path = "/get_sundry_creditors_indirect_expenses")
    public Object getSundryCreditorAndIndirectExpenses(HttpServletRequest request) {
        JsonObject result = service.getSundryCreditorAndIndirectExpenses(request);
        return result.toString();
    }

    /* Sundry creditors pending Bills */
   /* @PostMapping(path = "/get_creditors_pending_bills")
    public Object getCreditorsPendingBills(HttpServletRequest request) {
        JsonObject array = service.getCreditorsPendingBills(request);
        return array.toString();
    }*/
    @PostMapping(path = "/get_creditors_pending_bills")
    public Object getCreditorsPendingBillsNew(HttpServletRequest request) {
        JsonObject array = service.getCreditorsPendingBillsNew(request);
        return array.toString();
    }

    /* Get Cash-In-Hand and Bank Account Ledger for Payments   */
    @GetMapping(path = "/get_cashAc_bank_account_details")
    public Object getCashAcBankAccountDetails(HttpServletRequest request) {
        JsonObject object = service.getCashAcBankAccountDetails(request);
        return object.toString();
    }


    /* Get List of Payments   */
    @GetMapping(path = "/get_payment_list_by_outlet")
    public Object paymentListbyOutlet(HttpServletRequest request) {
        JsonObject object = service.paymentListbyOutlet(request);
        return object.toString();
    }


    /* Create Payments */
    @PostMapping(path = "/create_payments")
    public Object createPayments(HttpServletRequest request) {
        JsonObject array = service.createPayments(request);
        return array.toString();
    }
}

