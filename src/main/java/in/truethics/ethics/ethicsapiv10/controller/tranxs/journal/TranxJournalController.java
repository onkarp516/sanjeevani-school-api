package in.truethics.ethics.ethicsapiv10.controller.tranxs.journal;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.tranx_service.journal.TranxJournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController

public class TranxJournalController {
    @Autowired
    private TranxJournalService service;

    /* get last records of voucher journal   */
    @GetMapping(path = "/get_last_record_journal")
    public Object journalLastRecord(HttpServletRequest request) {
        JsonObject result = service.journalLastRecord(request);
        return result.toString();
    }

    /* Create journal */
    @PostMapping(path = "/create_journal")
    public Object createJournal(HttpServletRequest request) {
        JsonObject array = service.createJournal(request);
        return array.toString();
    }

    @PostMapping(path = "/addReceiptNo")
    public Object addReceiptNo(HttpServletRequest request) {
        JsonObject object = service.addReceiptNo(request);
        return object.toString();
    }


    /* Get List of journal   */
    @GetMapping(path = "/get_journal_list_by_outlet")
    public Object journalListbyOutlet(HttpServletRequest request) {
        JsonObject object = service.journalListbyOutlet(request);
        return object.toString();
    }

    @PostMapping(path = "get_journal_details")
    public Object getJournalDetails(HttpServletRequest request) {
        JsonObject object = service.getJournalDetails(request);
        return object.toString();
    }


    /* Get  ledger details of journal   */
    @GetMapping(path = "/get_ledger_list_by_outlet")
    public Object getledgerDetails(HttpServletRequest request) {
        JsonObject object = service.getledgerDetails(request);
        return object.toString();
    }

}
