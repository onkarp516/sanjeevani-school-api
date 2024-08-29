package in.truethics.ethics.ethicsapiv10.service.tranx_service.purchase;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.truethics.ethics.ethicsapiv10.common.GenerateDates;
import in.truethics.ethics.ethicsapiv10.common.GenerateFiscalYear;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.tranx.debit_note.TranxDebitNoteDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.debit_note.TranxDebitNoteNewReferenceMaster;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerTransactionDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.TransactionTypeMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.debitnote_repository.TranxDebitNoteDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.debitnote_repository.TranxDebitNoteNewReferenceRepository;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TranxDebitNoteNewReferenceService {
    @Autowired
    private TranxDebitNoteNewReferenceRepository repository;
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private TranxDebitNoteDetailsRepository tranxDebitNoteDetailsRepository;
    @Autowired
    private GenerateFiscalYear generateFiscalYear;
    @Autowired
    private LedgerMasterRepository ledgerMasterRepository;
    @Autowired
    private TransactionTypeMasterRepository tranxRepository;
    @Autowired
    private LedgerTransactionDetailsRepository transactionDetailsRepository;

    public JsonObject tranxDebitNoteList(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject finalResult = new JsonObject();
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<TranxDebitNoteNewReferenceMaster> list = new ArrayList<>();
        Long sundryCreditorId = Long.parseLong(request.getParameter("sundry_creditor_id"));
        list = repository.findBySundryCreditorIdAndStatusAndTransactionStatusIdAndAdjustmentStatusAndOutletId(
                sundryCreditorId, true, 1L, "future", users.getOutlet().getId());
        if (list != null && list.size() > 0) {
            for (TranxDebitNoteNewReferenceMaster mTranxDebitNote : list) {
                if (mTranxDebitNote.getBalance() != 0.0) {
                    JsonObject data = new JsonObject();
                    data.addProperty("id", mTranxDebitNote.getId());
                    if (mTranxDebitNote.getTranxPurInvoice() != null) {
                        data.addProperty("source", "pur_invoice");
                        data.addProperty("invoice_id", mTranxDebitNote.getTranxPurInvoice().getId());
                    } else {
                        data.addProperty("source", "pur_challan");
                        data.addProperty("invoice_id", mTranxDebitNote.getTranxPurChallan().getId());
                    }
                    data.addProperty("debit_note_no", mTranxDebitNote.getDebitnoteNewReferenceNo());
                    data.addProperty("debit_note_date", mTranxDebitNote.getCreatedAt().toString());
                    data.addProperty("Total_amt", mTranxDebitNote.getBalance());
                    result.add(data);
                }
            }
            finalResult.addProperty("message", "success");
            finalResult.addProperty("responseStatus", HttpStatus.OK.value());
            finalResult.add("list", result);
        } else {
            finalResult.addProperty("message", "empty list");
            finalResult.addProperty("responseStatus", HttpStatus.NO_CONTENT.value());
            finalResult.add("list", result);
        }
        return finalResult;
    }

    public JsonObject tranxDebitNoteDetailsList(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject finalResult = new JsonObject();
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<TranxDebitNoteDetails> list = new ArrayList<>();
        Long sundryCreditorId = Long.parseLong(request.getParameter("sundry_creditor_id"));
        list = tranxDebitNoteDetailsRepository.findBySundryCreditorIdAndStatusAndTransactionStatusIdAndOutletId(
                sundryCreditorId, true, 1L, users.getOutlet().getId());
        if (list != null && list.size() > 0) {
            for (TranxDebitNoteDetails mTranxDebitNote : list) {

                JsonObject data = new JsonObject();
                data.addProperty("id", mTranxDebitNote.getId());
                if (mTranxDebitNote.getTranxDebitNoteMaster().getTranxPurInvoice() != null) {
                    data.addProperty("source", "pur_invoice");
                    data.addProperty("invoice_id", mTranxDebitNote.getTranxDebitNoteMaster().getTranxPurInvoice().getId());
                } else if (mTranxDebitNote.getTranxDebitNoteMaster().getTranxPurChallan() != null) {
                    data.addProperty("source", "pur_challan");
                    data.addProperty("invoice_id", mTranxDebitNote.getTranxDebitNoteMaster().getTranxPurChallan().getId());
                }
                data.addProperty("debit_note_no", mTranxDebitNote.getTranxDebitNoteMaster().getDebitnoteNewReferenceNo());
                data.addProperty("debit_note_date", mTranxDebitNote.getTranxDebitNoteMaster().getCreatedAt().toString());
                data.addProperty("Total_amt", mTranxDebitNote.getTotalAmount());
                result.add(data);
            }
            finalResult.addProperty("message", "success");
            finalResult.addProperty("responseStatus", HttpStatus.OK.value());
            finalResult.add("list", result);
        } else {
            finalResult.addProperty("message", "empty list");
            finalResult.addProperty("responseStatus", HttpStatus.NO_CONTENT.value());
            finalResult.add("list", result);
        }
        return finalResult;
    }

    public JsonObject debitNoteLastRecord(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Long count = repository.findLastRecord(users.getOutlet().getId());
        String serailNo = String.format("%05d", count + 1);// 5 digit serial number
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
        String debitNote = "DBTN" + currentMonth + serailNo;
        JsonObject result = new JsonObject();
        result.addProperty("message", "success");
        result.addProperty("responseStatus", HttpStatus.OK.value());
        result.addProperty("count", count + 1);
        result.addProperty("debitnoteNo", debitNote);
        return result;
    }

    public JsonObject debitListbyOutlet(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<TranxDebitNoteNewReferenceMaster> debitnote = repository.
                findByOutletIdAndStatusOrderByIdDesc(users.getOutlet().getId(), true);

        for (TranxDebitNoteNewReferenceMaster vouchers : debitnote) {
            JsonObject response = new JsonObject();
            response.addProperty("source", vouchers.getSource());
            response.addProperty("id", vouchers.getId());
            response.addProperty("debit_note_no", vouchers.getDebitnoteNewReferenceNo());
            response.addProperty("transaction_dt", vouchers.getTranscationDate().toString());
            //  response.addProperty("debit_sr_no", vouchers.getSrno());
//            response.addProperty("ledger_name", vouchers.getContraSrNo());

            response.addProperty("total_amount", vouchers.getTotalAmount());
           /* TranxDebitNoteNewReferenceMaster tranxDebitNoteNewReferenceMaster = tranxDebitNoteDetailsRepository.
                    findLedgerName(
                            vouchers.getId(),users.getOutlet().getId(), true);
            response.addProperty("ledger_name",tranxDebitNoteNewReferenceMaster.getSundryCreditor().getLedgerName());
*/
            result.add(response);
        }

        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);
        return output;
    }

    public JsonObject createdebit(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Map<String, String[]> paramMap = request.getParameterMap();
        JsonObject response = new JsonObject();

        TranxDebitNoteNewReferenceMaster debitMaster = new TranxDebitNoteNewReferenceMaster();
        Branch branch = null;
        if (users.getBranch() != null)
            branch = users.getBranch();
        Outlet outlet = users.getOutlet();
        debitMaster.setBranch(branch);
        debitMaster.setOutlet(outlet);
        debitMaster.setStatus(true);
        LocalDate tranxDate = LocalDate.parse(request.getParameter("transaction_dt"));
        /* fiscal year mapping */
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(tranxDate);
        if (fiscalYear != null) {
            debitMaster.setFiscalYear(fiscalYear);
            debitMaster.setFinancialYear(fiscalYear.getFiscalYear());
        }

        debitMaster.setTranscationDate(tranxDate);
        debitMaster.setSrno(Long.parseLong(request.getParameter("voucher_debit_sr_no")));
        debitMaster.setDebitnoteNewReferenceNo(request.getParameter("voucher_debit_no"));
        debitMaster.setTotalAmount(Double.parseDouble(request.getParameter("total_amt")));
        debitMaster.setSource("voucher");
        if (paramMap.containsKey("narration"))
            debitMaster.setNarrations(request.getParameter("narration"));
        else {
            debitMaster.setNarrations("NA");
        }
        debitMaster.setCreatedBy(users.getId());
        TranxDebitNoteNewReferenceMaster tranxdebitMaster = repository.save(debitMaster);

        try {
            double total_amt = 0.0;
            String jsonStr = request.getParameter("rows");
            JsonParser parser = new JsonParser();
            JsonArray row = parser.parse(jsonStr).getAsJsonArray();
            for (int i = 0; i < row.size(); i++) {
                JsonObject debitRow = row.get(i).getAsJsonObject();
                TranxDebitNoteDetails tranxDebitDetails = new TranxDebitNoteDetails();
                LedgerMaster ledgerMaster = null;

                tranxDebitDetails.setBranch(branch);
                tranxDebitDetails.setOutlet(outlet);
                tranxDebitDetails.setStatus(true);
                ledgerMaster = ledgerMasterRepository.findByIdAndStatus(debitRow.get("perticulars").getAsJsonObject().get("id").getAsLong(), true);
                if (ledgerMaster != null)
                    tranxDebitDetails.setLedgerMaster(ledgerMaster);
//                    tranxDebitDetails.setSundryCreditor(ledgerMaster);
                tranxDebitDetails.setTranxDebitNoteMaster(tranxdebitMaster);
                tranxDebitDetails.setType(debitRow.get("type").getAsString());
                tranxDebitDetails.setSource("voucher");
//                tranxContraDetails.setLedgerName(contraRow.get("perticulars").getAsJsonObject().get("ledger_name").getAsString());
                total_amt = debitRow.get("paid_amt").getAsDouble();

                JsonObject perticulars = debitRow.get("perticulars").getAsJsonObject();
                tranxDebitDetails.setLedgerType(ledgerMaster.getSlugName());
                tranxDebitDetails.setCreatedBy(users.getId());
                TranxDebitNoteDetails mdebit = tranxDebitNoteDetailsRepository.save(tranxDebitDetails);
                insertIntoPostings(mdebit, total_amt, tranxdebitMaster.getSource());
            }
            response.addProperty("message", "Debit note  created successfully");
            response.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            //  debitLogger.error("Error in debit Creation :->" + e.getMessage());
            response.addProperty("message", "Error in debit creation");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    private void insertIntoPostings(TranxDebitNoteDetails mdebit, double total_amt, String source) {
        TransactionTypeMaster tranxType = tranxRepository.findByTransactionNameIgnoreCase("debit note");
        try {
            if (mdebit.getType().equalsIgnoreCase("dr")) {
                if (source.equalsIgnoreCase("pur_invoice")) {

                    transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(mdebit.getSundryCreditor().
                                    getFoundations().getId(), mdebit.getSundryCreditor().getPrinciples() != null ?
                                    mdebit.getSundryCreditor().getPrinciples().getId() : null,
                            mdebit.getSundryCreditor().getPrincipleGroups() != null ?
                                    mdebit.getSundryCreditor().getPrincipleGroups().getId() : null,
                            null,
                            tranxType.getId(), mdebit.getSundryCreditor().getBalancingMethod() != null ?
                                    mdebit.getSundryCreditor().getBalancingMethod().getId() : null,
                            mdebit.getBranch() != null ?
                                    mdebit.getBranch().getId() : null,
                            mdebit.getOutlet().getId(), "NA", total_amt * -1, 0.0,
                            mdebit.getTranxDebitNoteMaster().getTranscationDate(), null, mdebit.getId(), tranxType.getTransactionName(),
                            mdebit.getSundryCreditor().getUnderPrefix(), mdebit.getTranxDebitNoteMaster().getFinancialYear(),
                            mdebit.getCreatedBy(), mdebit.getSundryCreditor().getId(), mdebit.getTranxDebitNoteMaster().getDebitnoteNewReferenceNo());
                } else {
                    transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(mdebit.getLedgerMaster().
                                    getFoundations().getId(), mdebit.getLedgerMaster().getPrinciples() != null ?
                                    mdebit.getLedgerMaster().getPrinciples().getId() : null,
                            mdebit.getLedgerMaster().getPrincipleGroups() != null ?
                                    mdebit.getLedgerMaster().getPrincipleGroups().getId() : null,
                            null,
                            tranxType.getId(), mdebit.getLedgerMaster().getBalancingMethod() != null ?
                                    mdebit.getLedgerMaster().getBalancingMethod().getId() : null,
                            mdebit.getBranch() != null ?
                                    mdebit.getBranch().getId() : null,
                            mdebit.getOutlet().getId(), "NA", total_amt * -1, 0.0,
                            mdebit.getTranxDebitNoteMaster().getTranscationDate(), null, mdebit.getId(), tranxType.getTransactionName(),
                            mdebit.getLedgerMaster().getUnderPrefix(), mdebit.getTranxDebitNoteMaster().getFinancialYear(),
                            mdebit.getCreatedBy(), mdebit.getLedgerMaster().getId(), mdebit.getTranxDebitNoteMaster().getDebitnoteNewReferenceNo());
                }

            } else {
                if (source.equalsIgnoreCase("voucher")) {
                    transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(mdebit.getLedgerMaster().
                                    getFoundations().getId(), mdebit.getLedgerMaster().getPrinciples() != null ?
                                    mdebit.getLedgerMaster().getPrinciples().getId() : null,
                            mdebit.getLedgerMaster().getPrincipleGroups() != null ?
                                    mdebit.getLedgerMaster().getPrincipleGroups().getId() : null,
                            null,
                            tranxType.getId(), mdebit.getLedgerMaster().getBalancingMethod() != null ?
                                    mdebit.getLedgerMaster().getBalancingMethod().getId() : null,

                            mdebit.getBranch() != null ?
                                    mdebit.getBranch().getId() : null,
                            mdebit.getOutlet().getId(), "NA", 0.0, total_amt,
                            mdebit.getTranxDebitNoteMaster().getTranscationDate(), null, mdebit.getId(), tranxType.getTransactionName(),
                            mdebit.getLedgerMaster().getUnderPrefix(), mdebit.getTranxDebitNoteMaster().getFinancialYear(),
                            mdebit.getCreatedBy(), mdebit.getLedgerMaster().getId(), mdebit.getTranxDebitNoteMaster().getDebitnoteNewReferenceNo());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // debitLogger.error("Error in debit Postings :->" + e.getMessage());
        }
    }
}

