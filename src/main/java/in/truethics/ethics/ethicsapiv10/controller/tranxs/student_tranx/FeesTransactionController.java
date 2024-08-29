package in.truethics.ethics.ethicsapiv10.controller.tranxs.student_tranx;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.student_tranx.FeesTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class FeesTransactionController {

    @Autowired
    private FeesTransactionService feesTransactionService;

    @PostMapping(path = "/getStudentOutstanding")
    public Object getStudentOutstanding(HttpServletRequest request) {
        JsonObject jsonObject = feesTransactionService.getStudentOutstanding(request);
        return jsonObject.toString();
    }


    /*New URLS*/
    @PostMapping(path = "/createTransaction")
    public Object createTransaction(HttpServletRequest request) {
        JsonObject result = feesTransactionService.createTransaction(request);
        return result.toString();
    }
    @PostMapping(path = "/createTransactionForVidyalay")
    public Object createTransactionForVidyalays(HttpServletRequest request) {
        JsonObject result = feesTransactionService.createTransactionForVidyalay(request);
        return result.toString();
    }


    @GetMapping(path = "/updateTransactionDate")
    public Object updateTranxDate(HttpServletRequest request) {
        JsonObject result = feesTransactionService.updateTrasactionDate(request);
        return result.toString();
    }

    @GetMapping(path="/missingInvoiceInTranxPosting")
    public Object createSalesInvoice(HttpServletRequest request) {
        JsonObject result = feesTransactionService.missingInvoiceInTranxPosting(request);
        return result.toString();
    }

    @GetMapping(path = "/getTransactionList")
    public Object getTransactionList(HttpServletRequest request) {
        JsonObject result = feesTransactionService.getTransactionList(request);
        return result.toString();
    }

    @PostMapping(path = "/getTransactionListByStandard")
    public Object getTransactionListByStandard(HttpServletRequest request) {
        JsonObject result = feesTransactionService.getTransactionListByStandard(request);
        return result.toString();
    }


    @PostMapping(path = "/getTransactionDetailsById")
    public Object getTransactionDetailById(HttpServletRequest request) {
        JsonObject result = feesTransactionService.getTransactionDetailsById(request);
        return result.toString();
    }

    @PostMapping(path = "/getCollectionByDate")
    public Object getCollectionByDates(HttpServletRequest request) {
        JsonObject result = feesTransactionService.getCollectionByDate(request);
        return result.toString();
    }


    @PostMapping(path = "/deleteFeesData")
    public Object deleteFeestransactions(HttpServletRequest request) {
        JsonObject result = feesTransactionService.deleteFeesData(request);
        return result.toString();
    }

    @PostMapping(path = "/deleteFeesDataForVidyalay")
    public Object deleteFeestransactionsForVidyala(HttpServletRequest request) {
        JsonObject result = feesTransactionService.deleteFeesDataForVidyala(request);
        return result.toString();
    }

    @PostMapping(path = "/deleteJournalMaster")
    public Object deleteJournalMaster(HttpServletRequest request) {
        JsonObject result = feesTransactionService.deleteJournalMaster(request);
        return result.toString();
    }


    @PostMapping(path = "/getStudentPaidReceipts")
    public Object getStudentPaidReceipts(HttpServletRequest request) {
        return feesTransactionService.getStudentPaidReceipts(request).toString();
    }

    @PostMapping(path = "/cancelStudentAdmission")
    public Object cancelStudentAdmission(HttpServletRequest request) {
        return feesTransactionService.cancelStudentAdmission(request).toString();
    }

    @PostMapping(path = "/getDataForDashboard")
    public Object getDataforDash(HttpServletRequest request) {
        JsonObject res = feesTransactionService.getDataforDashboard(request);
        return res.toString();
    }


    @PostMapping(path = "/updateLedgerAccounting")
    public Object updateLedgerAccounting(HttpServletRequest request){
        return feesTransactionService.updateLedgerAccounting(request).toString();
    }

    @PostMapping(path = "/updateJournalNumbers")
    public Object updateJournalNumbers(HttpServletRequest request){
        return feesTransactionService.updateJournalNumbers(request).toString();
    }


    /*PRactice*/
    @PostMapping(path = "/getReceiptNo")
    public Object getReceiptNo(HttpServletRequest request){
        return feesTransactionService.getReceeiptNo(request).toString();
    }

    @PostMapping(path = "/create_receipt")
    public Object createReceipt(HttpServletRequest request) {
        JsonObject array = feesTransactionService.createReceipt(request);
        return array.toString();
    }
}
