package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.ledger_service.LedgerMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LedgerMasterController {
    @Autowired
    private LedgerMasterService service;

    /* create ledger Masters */
    @PostMapping(path = "/create_ledger_master")
    public ResponseEntity<?> createLedgerMaster(HttpServletRequest request) {
        return ResponseEntity.ok(service.createLedgerMaster(request));
    }

    /* Edit Ledger Master */
    @PostMapping(path = "/edit_ledger_master")
    public ResponseEntity<?> editLedgerMaster(HttpServletRequest request) {
        return ResponseEntity.ok(service.editLedgerMaster(request));
    }

    /* get Sundry Creditors Ledgers by outltwise */
    @GetMapping(path = "/get_sundry_creditors")
    public Object getSundryCreditors(HttpServletRequest request) {
        JsonObject result = service.getSundryCreditors(request);
        return result.toString();
    }

    /* get Sundry Debtors Ledgers by outlet id */
    @GetMapping(path = "/get_sundry_debtors")
    public Object getSundryDebtors(HttpServletRequest request) {
        JsonObject result = service.getSundryDebtors(request);
        return result.toString();
    }

    /* get Sundry Debtors Ledgers by id */
    @PostMapping(path = "/get_sundry_debtors_by_id")
    public Object getSundryDebtorsById(HttpServletRequest request) {
        JsonObject result = service.getSundryDebtorsById(request);
        return result.toString();
    }

    /* get Sundry Debtors Ledgers by id */
    @PostMapping(path = "/get_sundry_creditors_by_id")
    public Object getSundryCreditorsById(HttpServletRequest request) {
        JsonObject result = service.getSundryCreditorsById(request);
        return result.toString();
    }

    /* get Purchase Account by outletid */
    @GetMapping(path = "/get_purchase_accounts")
    public Object getPurchaseAccount(HttpServletRequest request) {
        JsonObject result = service.getPurchaseAccount(request);
        return result.toString();
    }

    /* get Sales Account by outletid */
    @GetMapping(path = "/get_sales_accounts")
    public Object getSalesAccount(HttpServletRequest request) {
        JsonObject result = service.getSalesAccount(request);
        return result.toString();
    }

    /* get All Indirect incomes by principleId(here principle id: 9 is for indirect incomes) */
    @GetMapping(path = "/get_indirect_incomes")
    public Object getIndirectIncomes(HttpServletRequest request) {
        JsonObject result = service.getIndirectIncomes(request);
        return result.toString();
    }

    /* get All Indirect expenses by principleId(here principle id: 12 is for indirect expenses) */
    @GetMapping(path = "/get_indirect_expenses")
    public Object getIndirectExpenses(HttpServletRequest request) {
        JsonObject result = service.getIndirectExpenses(request);
        return result.toString();
    }

    /* get All Ledgers of outlets with Dr and Cr */
    @GetMapping(path = "/get_all_ledgers")
    public Object getAllLedgers(HttpServletRequest request) {
        JsonObject result = service.getAllLedgers(request);
        return result.toString();
    }

    /* get Ledger Details on particular ID : ledger and its vouchers */
    @PostMapping(path = "/get_ledger_details")
    public Object getLedgerDetails(HttpServletRequest request) {
        JsonObject result = service.getLedgerDetails(request);
        return result.toString();
    }

    @GetMapping(path="/getbankdetails")
    public Object getbankdetail(HttpServletRequest request) {
        JsonObject result = service.getBankDetails(request);
        return  result.toString();
    }


    /* get Ledger Voucher on particular Transaction  ID : ledger and its vouchers  Details*/
    @PostMapping(path = "/get_ledger_voucher_details")
    public Object getLedgerVoucherDetails(HttpServletRequest request) {
        JsonObject result = service.getLedgerVoucherDetails(request);
        return result.toString();
    }


    /* get Ledger Voucher on particular Transaction  ID : ledger and its vouchers  Details*/
    @PostMapping(path = "/get_sales_invoice_details")
    public Object getSalesInvoiceDetails(HttpServletRequest request) {
        JsonObject result = service.getSalesInvoiceDetails(request);
        return result.toString();
    }

    /* Sundry creditors overdue for bil by bill */
    @PostMapping(path = "/get_creditors_total_amount_bill_by_bill")
    public Object getTotalAmountBillbyBillSC(HttpServletRequest request) {
        JsonObject array = service.getTotalAmountBillbyBill(request);
        return array.toString();
    }

    /* get total balance of each sundry creditors for Payment Vouchers  */
    @GetMapping(path = "/get_creditors_total_amount")
    public Object getTotalAmountSC(HttpServletRequest request) {
        JsonObject array = service.getTotalAmount(request, "sc");
        return array.toString();
    }

    /* get ledgers by id */
    @PostMapping(path = "/get_ledgers_by_id")
    public Object getLedgersById(HttpServletRequest request) {
        JsonObject result = service.getLedgersById(request);
        return result.toString();
    }
    /* get total balance of each sundry creditors for Payment Vouchers  */
  /*  @GetMapping(path = "/get_debtors_total_amount")
    public Object getTotalAmountSD(HttpServletRequest request) throws JSONException {
        JSONObject array = service.getTotalAmount(request, "sd");
        return array.toString();
    }*/

    /* Get Cash-In-Hand and Bank Account Ledger from ledger balance summary   */
    @GetMapping(path = "/get_cashAc_bank_account")
    public Object getCashAcBankAccount(HttpServletRequest request) {
        JsonObject object = service.getCashAcBankAccount(request);
        return object.toString();
    }

    /* get GST Details of ledgers by id */
    @PostMapping(path = "/get_gst_details")
    public Object getGstDetails(HttpServletRequest request) {
        JsonObject result = service.getGstDetails(request);
        return result.toString();
    }

    /* get Shipping Address Details of ledgers by id */
    @PostMapping(path = "/get_shipping_details")
    public Object getShippingDetails(HttpServletRequest request) {
        JsonObject result = service.getShippingDetails(request);
        return result.toString();
    }

    /* get Department Details of ledgers by id */
    @PostMapping(path = "/get_ledger_dept_details")
    public Object getDeptDetails(HttpServletRequest request) {
        JsonObject result = service.getDeptDetails(request);
        return result.toString();
    }

    /* get Billing address details of ledgers by id */
    @PostMapping(path = "/get_ledger_billing_details")
    public Object getBillingDetails(HttpServletRequest request) {
        JsonObject result = service.getBillingDetails(request);
        return result.toString();
    }


    /* Get Purchase and Payment Details of Sundry Creditors by id from Ledger Transaction Details Table */
   /* @PostMapping(path = "/get_tranx_details_sundry_creditor")
    public Object getTranxDetailsSundryCreditors(HttpServletRequest request) throws JSONException {
        JSONObject array = service.getTranxDetailsSundryCreditors(request);
        return array.toString();
    }*/

    /* Get Outstanding details of Sundry Creditors by id from Payment Transaction Details Table*/
    /*@PostMapping(path = "/get_payment_details_sundry_creditor")
    public Object getPaymentDetailsSundryCreditors(HttpServletRequest request) throws JSONException {
        JSONObject array = service.getPaymentDetailsSundryCreditors(request);
        return array.toString();
    }*/

    /* get sundry creditors, sundry debtors,cash account and  bank accounts*/
    @GetMapping(path = "/get_client_list_for_sale")
    public ResponseEntity<?> getClientList(HttpServletRequest request) {
        return ResponseEntity.ok(service.getClientList(request));
    }

    /*@PostMapping(path = "/DTGet_all_ledgers")
    public Object DTGetallledgers(@RequestBody Map<String, String> request, HttpServletRequest req) {
        return service.DTGetallledgers(request, req);
    }*/
    /* get all ledgers excepts cash account and bank accounts for payment and receipt */
    /*@GetMapping(path = "/get_ledgers_list")
    public Object getLedgersList(HttpServletRequest request) {
        JsonObject result = service.getLedgersList(request);
        return result.toString();
    }*/


    /* get All Ledgers of outlets foe Fee head */
    @GetMapping(path = "/getLedgersForList")
    public Object getLedgersForList(HttpServletRequest request) {
        JsonObject result = service.getLedgersForList(request);
        return result.toString();
    }

    /* get All Ledgers of branch foe Fee head */
    @PostMapping(path = "/getLedgersByBranch")
    public Object getLedgersByBranch(HttpServletRequest request) {
        JsonObject result = service.getLedgersByBranch(request);
        return result.toString();
    }

    /* get Sundry Debtors Ledgers by outlet id & branch id */
    @PostMapping(path = "/get_student_sundry_debtors")
    public Object getSundryDebtorsStudents(HttpServletRequest request) {
        JsonObject result = service.getSundryDebtorsStudents(request);
        return result.toString();
    }

    @PostMapping(path = "/get_ledger_tranx_details_report")
    public Object getLedgerTransactionsDetails(HttpServletRequest request) {
        return service.getLedgerTransactionsDetails(request).toString();
    }
}
