package in.truethics.ethics.ethicsapiv10.service.tranx_service.receipt;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.truethics.ethics.ethicsapiv10.common.GenerateDates;
import in.truethics.ethics.ethicsapiv10.common.GenerateFiscalYear;
import in.truethics.ethics.ethicsapiv10.common.GenerateSlugs;
import in.truethics.ethics.ethicsapiv10.common.NumFormat;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesInvoice;
import in.truethics.ethics.ethicsapiv10.model.school_master.StudentTransport;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionDetail;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionSummary;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.StudentBusTransaction;
import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptPerticulars;
import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptPerticularsDetails;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerBalanceSummaryRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerTransactionDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.BranchRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.StudentTransportRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.TransactionStatusRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.TransactionTypeMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.FeesTransactionDetailRepository;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.FeesTransactionSummaryRepository;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.StudentBusTransactionRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.debitnote_repository.TranxDebitNoteNewReferenceRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository.TranxReceiptMasterRepositoty;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository.TranxReceiptPerticularsDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository.TranxReceiptPerticularsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository.TranxSalesInvoiceRepository;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service

public class TranxReceiptService {
    private static final Logger receiptLogger = LoggerFactory.getLogger(TranxReceiptService.class);
    @Autowired
    private TranxReceiptMasterRepositoty repository;
    @Autowired
    private TransactionTypeMasterRepository transactionTypeMasterRepository;
    @Autowired
    private StudentTransportRepository studentTransportRepository;

    @Autowired
    private StudentBusTransactionRepository studentBusTransactionRepository;

    @Autowired
    private FeesTransactionSummaryRepository feesTransactionSummaryRepository;
    @Autowired
    private FeesTransactionDetailRepository feesTransactionDetailRepository;

    @Autowired
    private NumFormat numFormat;
    @Autowired
    private TranxReceiptPerticularsRepository tranxReceiptPerticularsRepository;
    @Autowired
    private TranxReceiptPerticularsDetailsRepository tranxReceiptPerticularsDetailsRepository;
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private LedgerMasterRepository ledgerMasterRepository;
    @Autowired
    private GenerateSlugs generateSlugs;
    @Autowired
    private LedgerBalanceSummaryRepository ledgerBalanceSummaryRepository;
    @Autowired
    private GenerateFiscalYear generateFiscalYear;
    @Autowired
    private TransactionTypeMasterRepository tranxRepository;
    @Autowired
    private LedgerTransactionDetailsRepository transactionDetailsRepository;
    @Autowired
    private TransactionStatusRepository transactionStatusRepository;
    @Autowired
    private TranxDebitNoteNewReferenceRepository tranxDebitNoteNewReferenceRepository;

    @Autowired
    private TranxSalesInvoiceRepository tranxSalesInvoiceRepository;
    @Autowired
    private BranchRepository branchRepository;



    public JsonObject receiptLastRecord(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Long count = repository.findLastRecord(users.getOutlet().getId(),users.getBranch().getId());

        String serailNo = String.format("%05d", count + 1);// 5 digit serial number
        //first 3 digits of Current month
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
        String receiptCode = "RCPT" + currentMonth + serailNo;
        JsonObject result = new JsonObject();
        result.addProperty("message", "success");
        result.addProperty("responseStatus", HttpStatus.OK.value());
        result.addProperty("receipt_sr_no", count + 1);
        result.addProperty("receipt_code", receiptCode);
        return result;
    }

    public JsonObject getSundryDebtorsAndIndirectIncomes(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));

        JsonArray result = new JsonArray();
        JsonObject finalResult = new JsonObject();
        List<LedgerMaster> sundryDebtors = new ArrayList<>();
        List<LedgerMaster> sundryCreditors = new ArrayList<>();
        if (users.getBranch() == null) {
            sundryDebtors = ledgerMasterRepository.findByOutletIdAndPrincipleGroupsIdAndBranchIdIsNull(
                    users.getOutlet().getId(), 1L);
            sundryCreditors = ledgerMasterRepository.findByOutletIdAndPrincipleGroupsIdAndBranchIdIsNull(
                    users.getOutlet().getId(), 5L);
        }
        if (users.getBranch() != null) {
            sundryDebtors = ledgerMasterRepository.findByOutletIdAndBranchIdAndPrincipleGroupsId(
                    users.getOutlet().getId(), users.getBranch().getId(), 1L);
            sundryCreditors = ledgerMasterRepository.findByOutletIdAndBranchIdAndPrincipleGroupsId(
                    users.getOutlet().getId(), users.getBranch().getId(), 5L);
        }
        /* for Sundry Creditors List */
        if (sundryDebtors.size() > 0) {
            for (LedgerMaster mLedger : sundryDebtors) {
                JsonObject response = new JsonObject();
//                System.out.println("mLedger.getId() "+mLedger.getId());
                response.addProperty("id", mLedger.getId());
                response.addProperty("ledger_name", mLedger.getLedgerName());
                response.addProperty("balancing_method", mLedger.getBalancingMethod() != null ?
                        generateSlugs.getSlug(mLedger.getBalancingMethod().getBalancingMethod()) : "");
                response.addProperty("type", "SD");

                result.add(response);
            }
        }
        if (sundryCreditors.size() > 0) {
            for (LedgerMaster mLedger : sundryCreditors) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mLedger.getId());
                response.addProperty("ledger_name", mLedger.getLedgerName());
                response.addProperty("balancing_method", generateSlugs.getSlug(mLedger.getBalancingMethod().getBalancingMethod()));
                response.addProperty("type", "SC");

                result.add(response);
            }
        }
        List<LedgerMaster> indirectIncomes = new ArrayList<>();
        indirectIncomes = ledgerMasterRepository.findByOutletIdAndPrinciplesId(
                users.getOutlet().getId(), 9L);
        if (indirectIncomes.size() > 0) {
            for (LedgerMaster mLedger : indirectIncomes) {
                if (!mLedger.getLedgerName().equalsIgnoreCase("Purchase Discount")) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mLedger.getId());
                    response.addProperty("ledger_name", mLedger.getLedgerName());
                    response.addProperty("balancing_method", "NA");
                    response.addProperty("type", "IC");
                    result.add(response);
                }
            }
        }
        finalResult.addProperty("message", "success");
        finalResult.addProperty("responseStatus", HttpStatus.OK.value());
        finalResult.add("list", result);
        return finalResult;
    }

    /* Accounting Postings of Receipt Vouchers  */
    private void insertIntoPostings(TranxReceiptPerticulars mReceipt, double total_amt) {
        TransactionTypeMaster tranxType = tranxRepository.findByTransactionNameIgnoreCase("receipt");
        try {
            /* for Sundry Debtors  */
            if (mReceipt.getType().equalsIgnoreCase("cr")) {
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(mReceipt.getLedgerMaster().
                                getFoundations().getId(), mReceipt.getLedgerMaster().getPrinciples() != null ?
                                mReceipt.getLedgerMaster().getPrinciples().getId() : null,
                        mReceipt.getLedgerMaster().getPrincipleGroups() != null ?
                                mReceipt.getLedgerMaster().getPrincipleGroups().getId() : null,
                        null,
                        tranxType.getId(), mReceipt.getLedgerMaster().getBalancingMethod() != null ?
                                mReceipt.getLedgerMaster().getBalancingMethod().getId() : null,
                        mReceipt.getBranch() != null ? mReceipt.getBranch().getId() : null,
                        mReceipt.getOutlet().getId(), "NA", 0.0, total_amt,
                        mReceipt.getTranxReceiptMaster().getTranscationDate(), null, mReceipt.getId(), tranxType.getTransactionName(),
                        mReceipt.getLedgerMaster().getUnderPrefix(), mReceipt.getTranxReceiptMaster().getFinancialYear(),
                        mReceipt.getCreatedBy(), mReceipt.getLedgerMaster().getId(), mReceipt.getTranxReceiptMaster().getReceiptNo());

            } else {
                /* for Cash and Bank Account  */
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(mReceipt.getLedgerMaster().
                                getFoundations().getId(), mReceipt.getLedgerMaster().getPrinciples().getId(),
                        mReceipt.getLedgerMaster().getPrincipleGroups() != null ?
                                mReceipt.getLedgerMaster().getPrincipleGroups().getId() : null,
                        null,
                        tranxType.getId(), mReceipt.getLedgerMaster().getBalancingMethod() != null ?
                                mReceipt.getLedgerMaster().getBalancingMethod().getId() : null,
                        mReceipt.getBranch() != null ? mReceipt.getBranch().getId() : null,
                        mReceipt.getOutlet().getId(), "NA", total_amt * -1, 0.0,
                        mReceipt.getTranxReceiptMaster().getTranscationDate(), null, mReceipt.getId(), tranxType.getTransactionName(),
                        mReceipt.getLedgerMaster().getUnderPrefix(), mReceipt.getTranxReceiptMaster().getFinancialYear(),
                        mReceipt.getCreatedBy(), mReceipt.getLedgerMaster().getId(), mReceipt.getTranxReceiptMaster().getReceiptNo());
            }
        } catch (Exception e) {
            e.printStackTrace();
            receiptLogger.error("Error in Receipt Postings :->" + e.getMessage());
        }
    }

    public JsonObject receiptListbyOutlet(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<TranxReceiptMaster> payment = new ArrayList<>();

        if (users.getBranch() == null)
            payment = repository.findByOutletIdAndStatusAndBranchIdIsNullOrderByIdDesc(users.getOutlet().getId(), true);
        else
            payment = repository.findByOutletIdAndBranchIdAndStatusOrderByIdDesc(users.getOutlet().getId(), users.getBranch().getId(), true);
        for (TranxReceiptMaster invoices : payment) {
            JsonObject response = new JsonObject();
            System.out.println("invoices.getId() " + invoices.getId());
            response.addProperty("id", invoices.getId());
            response.addProperty("receipt_code", invoices.getReceiptNo());
            response.addProperty("transaction_dt", invoices.getTranscationDate() != null ?
                    invoices.getTranscationDate().toString() : "");
            response.addProperty("receipt_sr_no", invoices.getReceiptSrNo());
            response.addProperty("narration",invoices.getNarrations());
//            TranxReceiptPerticulars tranxReceiptPerticulars = tranxReceiptPerticularsRepository.
//                    findLedgerName(invoices.getId(), users.getOutlet().getId(), true);
            List<TranxReceiptPerticularsDetails> tranxReceiptPerticularsDetails=tranxReceiptPerticularsDetailsRepository.findByTranxReceiptMasterIdAndOutletIdAndStatusAndTypeIgnoreCase(
                    invoices.getId(), users.getOutlet().getId(),true,"CR");


                response.addProperty("ledger_name",invoices.getStudentLedger().getLedgerName());

            response.addProperty("total_amount", invoices.getTotalAmt());
//            response.addProperty("ledger_name", tranxReceiptPerticulars != null ?
//                    tranxReceiptPerticulars.getLedgerName() : "");
            result.add(response);
        }

        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);
        return output;
    }



    public JsonObject getReceiptDetails(HttpServletRequest request) {
        JsonObject finalObject = new JsonObject();
        JsonArray mReceiptArray = new JsonArray();
        JsonObject mResponse = new JsonObject();
        JsonObject res = new JsonObject();
        Users user = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Long id = Long.valueOf(request.getParameter("id"));
        try {

            List<TranxReceiptPerticularsDetails> tranxReceiptPerticularsDetails =tranxReceiptPerticularsDetailsRepository.findByTranxReceiptMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(
              id,user.getOutlet().getId(),user.getBranch().getId(),true, "DR");

            for (TranxReceiptPerticularsDetails crPerticularsDetails : tranxReceiptPerticularsDetails) {
                JsonObject jsonObject = new JsonObject();
                if (crPerticularsDetails.getType().equalsIgnoreCase("CR")) {
                    jsonObject.addProperty("credit", crPerticularsDetails.getPaidAmt());
                } else {
                    jsonObject.addProperty("debit", crPerticularsDetails.getPaidAmt());
                }
                jsonObject.addProperty("tranxType", crPerticularsDetails.getType());
                jsonObject.addProperty("particular", crPerticularsDetails.getLedgerMaster().getLedgerName());
                jsonObject.addProperty("narration", crPerticularsDetails.getTranxReceiptMaster().getNarrations());
                mReceiptArray.add(jsonObject);
            }

            List<TranxReceiptPerticularsDetails> tranxReceiptPerticularsDetails1 =tranxReceiptPerticularsDetailsRepository.findByTranxReceiptMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(
                    id,user.getOutlet().getId(),user.getBranch().getId(),true, "CR");
            for (TranxReceiptPerticularsDetails drPerticularsDetails : tranxReceiptPerticularsDetails1) {
                JsonObject jsonObject1 = new JsonObject();
                if (drPerticularsDetails.getType().equalsIgnoreCase("CR")) {
                    jsonObject1.addProperty("credit", drPerticularsDetails.getPaidAmt());
                } else {
                    jsonObject1.addProperty("debit", drPerticularsDetails.getPaidAmt());
                }
                jsonObject1.addProperty("tranxType", drPerticularsDetails.getType());
                jsonObject1.addProperty("particular", drPerticularsDetails.getLedgerMaster().getLedgerName());
                jsonObject1.addProperty("narration", drPerticularsDetails.getTranxReceiptMaster().getNarrations());
                mReceiptArray.add(jsonObject1);
            }

            finalObject.addProperty("message", "success");
            finalObject.addProperty("responseStatus", HttpStatus.OK.value());
            finalObject.add("data", mReceiptArray);
        } catch (Exception e) {
            finalObject.addProperty("message", "Failed to Load Data 1");
            finalObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());

        }
        return finalObject;
    }

    public JsonObject getDebtorsPendingBills(HttpServletRequest request) {
        JsonObject res=new JsonObject();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        long student_id= Long.parseLong(request.getParameter("student_id"));
        long academic_year_id= Long.parseLong(request.getParameter("academicYearId"));
        long standard_id= Long.parseLong(request.getParameter("standardId"));
        long division_id= Long.parseLong(request.getParameter("divisionId"));
        StudentTransport studentTransport=studentTransportRepository.findByStudentRegisterIdAndAcademicYearIdAndStandardIdAndDivisionIdAndIsBusFeePendingAndStatus(student_id,academic_year_id,standard_id,division_id,true,true);
        if(studentTransport!=null)
        {
            JsonObject result=new JsonObject();
            result.addProperty("start_date",studentTransport.getStartDate().toString());
            result.addProperty("end_date",studentTransport.getEndDate().toString());
            result.addProperty("bus_fee",studentTransport.getBus().getBusFee());
            result.addProperty("bus_stop_name",studentTransport.getBus().getBusStopName());
            result.addProperty("total_bus_fee",studentTransport.getBusFee());
            result.addProperty("total_month",studentTransport.getTotalMonth());
            result.addProperty("student_transport_id",studentTransport.getId());
            LedgerMaster ledgerMaster=ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(studentTransport.getStudentRegister().getId()
            ,users.getOutlet().getId(),users.getBranch().getId(),true);
            result.addProperty("ledger_id",ledgerMaster.getId());
            Integer remainingmonth= studentTransport.getTotalMonth()- studentTransport.getPaidMonth();
            Double paidamt=studentTransport.getPaidAmount();
            result.addProperty("remaining_month",remainingmonth);
            result.addProperty("paid_amt",paidamt);
            result.addProperty("outstanding",studentTransport.getBusFee()-paidamt);

            res.add("responseObject",result);
        }
        else {
            res.addProperty("message","Student Not Found in Transport");
            res.addProperty("responseStatus",HttpStatus.INTERNAL_SERVER_ERROR.value());
            return res;
        }
        res.addProperty("message","Student Found");
        res.addProperty("responseStatus",HttpStatus.OK.value());
        return res;
    }
}
