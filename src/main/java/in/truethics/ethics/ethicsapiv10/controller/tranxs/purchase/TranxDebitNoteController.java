package in.truethics.ethics.ethicsapiv10.controller.tranxs.purchase;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.tranx_service.purchase.TranxDebitNoteNewReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TranxDebitNoteController {

    @Autowired
    private TranxDebitNoteNewReferenceService service;

    /* get last records of voucher DebitNote   */
    @GetMapping(path = "/get_last_record_debitnote")
    public Object debitNoteLastRecord(HttpServletRequest request) {
        JsonObject result = service.debitNoteLastRecord(request);
        return result.toString();
    }

    /* list all opened debit notes of type = "futures" (only for Purchase invoice) */
    @PostMapping(path = "/list_tranx_debites_notes")
    public Object tranxDebitNoteList(HttpServletRequest request) {
        JsonObject result = service.tranxDebitNoteList(request);
        return result.toString();

    }

    @GetMapping(path = "/list_debit_notes")
    public Object debitListbyOutlet(HttpServletRequest request) {
        JsonObject result = service.debitListbyOutlet(request);
        return result.toString();

    }


    /* create debit note*/
    @PostMapping(path = "/create_debit")
    public Object createdebit(HttpServletRequest request) {
        JsonObject result = service.createdebit(request);
        return result.toString();

    }



    /* list all opened debit notes of type = "futures" (only for Purchase invoice) */
   /* @PostMapping(path = "/list_tranx_debites_notes_new")
    public Object tranxDebitNoteDetailsList(HttpServletRequest request) {
        JsonObject result = service.tranxDebitNoteDetailsList(request);
        return result.toString();

    }
*/
}
