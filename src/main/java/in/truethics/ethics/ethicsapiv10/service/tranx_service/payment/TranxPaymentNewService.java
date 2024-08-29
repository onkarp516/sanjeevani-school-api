package in.truethics.ethics.ethicsapiv10.service.tranx_service.payment;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.truethics.ethics.ethicsapiv10.common.GenerateDates;
import in.truethics.ethics.ethicsapiv10.common.GenerateFiscalYear;
import in.truethics.ethics.ethicsapiv10.common.GenerateSlugs;
import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerBalanceSummary;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.tranx.debit_note.TranxDebitNoteNewReferenceMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.payment.TranxPaymentMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.payment.TranxPaymentPerticulars;
import in.truethics.ethics.ethicsapiv10.model.tranx.payment.TranxPaymentPerticularsDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurInvoice;
import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptPerticulars;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerBalanceSummaryRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerTransactionDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.BranchRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.TransactionStatusRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.TransactionTypeMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.debitnote_repository.TranxDebitNoteNewReferenceRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.payment_repository.TranxPaymentMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.payment_repository.TranxPaymentPerticularsDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.payment_repository.TranxPaymentPerticularsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository.TranxPurInvoiceRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository.TranxReceiptMasterRepositoty;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository.TranxReceiptPerticularsRepository;
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

public class TranxPaymentNewService {

    private static final Logger paymentLogger = LoggerFactory.getLogger(TranxPaymentNewService.class);
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private TranxPurInvoiceRepository tranxPurInvoiceRepository;
    @Autowired
    private LedgerMasterRepository ledgerMasterRepository;
    @Autowired
    private LedgerBalanceSummaryRepository ledgerBalanceSummaryRepository;
    @Autowired
    private GenerateSlugs generateSlugs;
    @Autowired
    private TransactionTypeMasterRepository tranxRepository;
    @Autowired
    private LedgerTransactionDetailsRepository transactionDetailsRepository;
    @Autowired
    private GenerateFiscalYear generateFiscalYear;
    @Autowired
    private TranxDebitNoteNewReferenceRepository tranxDebitNoteNewReferenceRepository;
    @Autowired
    private TranxPaymentMasterRepository tranxPaymentMasterRepository;
    @Autowired
    private TranxPaymentPerticularsRepository tranxPaymentPerticularsRepository;
    @Autowired
    private TranxPaymentPerticularsDetailsRepository tranxPaymentPerticularsDetailsRepository;
    @Autowired
    private TransactionStatusRepository transactionStatusRepository;
    @Autowired
    private TranxReceiptMasterRepositoty receiptMasterRepositoty;
    @Autowired
    private TranxReceiptPerticularsRepository perticularsRepository;
    @Autowired
    private BranchRepository branchRepository;

    public JsonObject paymentLastRecord(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Long count = tranxPaymentMasterRepository.findLastRecord(users.getOutlet().getId());

        String serailNo = String.format("%05d", count + 1);// 5 digit serial number
        //first 3 digits of Current month
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
        String paymentCode = "PAYNT" + currentMonth + serailNo;
        JsonObject result = new JsonObject();
        result.addProperty("message", "success");
        result.addProperty("responseStatus", HttpStatus.OK.value());
        result.addProperty("payment_sr_no", count + 1);
        result.addProperty("payment_code", paymentCode);
        return result;
    }

    public JsonObject getSundryCreditorAndIndirectExpenses(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));

        JsonArray result = new JsonArray();
        JsonObject finalResult = new JsonObject();
        List<LedgerMaster> sundryCreditors = new ArrayList<>();
        List<LedgerMaster> sundryDebtors = new ArrayList<>();
        sundryCreditors = ledgerMasterRepository.findByOutletIdAndPrincipleGroupsId(
                users.getOutlet().getId(), 5L);
        sundryDebtors = ledgerMasterRepository.findByOutletIdAndPrincipleGroupsId(
                users.getOutlet().getId(), 1L);
        /* for Sundry Creditors List */
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
        /* for Sundry debtor List*/
        if (sundryDebtors.size() > 0) {
            for (LedgerMaster mLedger : sundryDebtors) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mLedger.getId());
                response.addProperty("ledger_name", mLedger.getLedgerName());
                response.addProperty("balancing_method", mLedger.getBalancingMethod() != null ?
                        generateSlugs.getSlug(mLedger.getBalancingMethod().getBalancingMethod()) : "");
                response.addProperty("type", "SD");

                result.add(response);
            }
        }
        List<LedgerMaster> indirectExpenses = new ArrayList<>();
        indirectExpenses = ledgerMasterRepository.findByOutletIdAndPrinciplesId(
                users.getOutlet().getId(), 12L);
        if (indirectExpenses.size() > 0) {
            for (LedgerMaster mLedger : indirectExpenses) {
                if (!mLedger.getLedgerName().equalsIgnoreCase("Round Off") &&
                        !mLedger.getLedgerName().equalsIgnoreCase("Sales Discount")) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mLedger.getId());
                    response.addProperty("ledger_name", mLedger.getLedgerName());
                    response.addProperty("balancing_method", "NA");
                    response.addProperty("type", "IE");
                    result.add(response);
                }
            }
        }
        finalResult.addProperty("message", "success");
        finalResult.addProperty("responseStatus", HttpStatus.OK.value());
        finalResult.add("list", result);
        return finalResult;
    }


    public JsonObject getCashAcBankAccountDetails(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        List<LedgerMaster> ledgerMaster = new ArrayList<>();
        Long branch_id = null;

        if (users.getBranch() != null) {
            System.out.println("branch is running");
            ledgerMaster = ledgerMasterRepository.findBankAccountCashAccountForBranch(users.getOutlet().getId(), users.getBranch().getId());
        } else {
            System.out.println("outlet is running");
            ledgerMaster = ledgerMasterRepository.findBankAccountCashAccount(users.getOutlet().getId());
        }

        JsonObject response = new JsonObject();
        for (LedgerMaster mLedger : ledgerMaster) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", mLedger.getId());
            jsonObject.addProperty("name", mLedger.getLedgerName());
            jsonObject.addProperty("type", mLedger.getSlugName());
            result.add(jsonObject);
        }
        if (result.size() > 0) {
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.addProperty("message", "success");
            response.add("list", result);
        } else {
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.addProperty("message", "empty list");
            response.add("list", result);
        }
        return response;
    }

    public JsonObject createPayments(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Map<String, String[]> paramMap = request.getParameterMap();
        JsonObject response = new JsonObject();
        TranxPaymentMaster tranxPayment = new TranxPaymentMaster();
        Branch branch = null;
        if (users.getBranch() != null) {
            branch = users.getBranch();
            tranxPayment.setBranch(branch);
        }
        Outlet outlet = users.getOutlet();
        tranxPayment.setOutlet(outlet);
        tranxPayment.setStatus(true);
        int inCash = 0;
//        int inCash = Integer.parseInt(request.getParameter("inCash")); // for accessing cash a/c or bank account ledger of corresponding a/c
        LocalDate tranxDate = LocalDate.parse(request.getParameter("transaction_dt"));
        tranxPayment.setTranscationDate(tranxDate);
        /*     fiscal year mapping  */
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(tranxDate);
        if (fiscalYear != null) {
            tranxPayment.setFiscalYear(fiscalYear);
            tranxPayment.setFinancialYear(fiscalYear.getFiscalYear());
        }

        tranxPayment.setPaymentSrNo(Long.parseLong(request.getParameter("payment_sr_no")));
        if (paramMap.containsKey("narration"))
            tranxPayment.setNarrations(request.getParameter("narration"));
        else {
            tranxPayment.setNarrations(request.getParameter("NA"));
        }
        tranxPayment.setPaymentNo(request.getParameter("payment_code"));
        tranxPayment.setTotalAmt(Double.parseDouble(request.getParameter("total_amt")));
        tranxPayment.setCreatedBy(users.getId());
        TranxPaymentMaster tranxPaymentMaster = tranxPaymentMasterRepository.save(tranxPayment);
        try {
            double total_amt = 0.0;
            String jsonStr = request.getParameter("row");
            JsonParser parser = new JsonParser();
            JsonArray row = parser.parse(jsonStr).getAsJsonArray();
            for (int i = 0; i < row.size(); i++) {
                /*Receipt Master */

                JsonObject paymentRow = row.get(i).getAsJsonObject();

                /*Payment Perticulars */
                TranxPaymentPerticulars tranxPaymentPerticulars = new TranxPaymentPerticulars();
                LedgerMaster ledgerMaster = null;
                tranxPaymentPerticulars.setBranch(branch);
                tranxPaymentPerticulars.setOutlet(outlet);
                tranxPaymentPerticulars.setStatus(true);
                ledgerMaster = ledgerMasterRepository.findByIdAndStatus(paymentRow.get("perticulars").getAsJsonObject().get("id").getAsLong(), true);
                if (ledgerMaster != null)
                    tranxPaymentPerticulars.setLedgerMaster(ledgerMaster);
                tranxPaymentPerticulars.setTranxPaymentMaster(tranxPaymentMaster);
                tranxPaymentPerticulars.setType(paymentRow.get("type").getAsString());
                tranxPaymentPerticulars.setLedgerType(paymentRow.get("perticulars").getAsJsonObject().get("type").getAsString());
                tranxPaymentPerticulars.setLedgerName(paymentRow.get("perticulars").getAsJsonObject().get("ledger_name").getAsString());
                if (paymentRow.get("type").getAsString().equalsIgnoreCase("dr")) {
                    tranxPaymentPerticulars.setDr(paymentRow.get("paid_amt").getAsDouble());
                }
                if (paymentRow.get("type").getAsString().equalsIgnoreCase("cr")) {
                    tranxPaymentPerticulars.setCr(paymentRow.get("paid_amt").getAsDouble());
                }
                if (paymentRow.has("bank_payment_no")) {
                    tranxPaymentPerticulars.setPaymentTranxNo(paymentRow.get("bank_payment_no").getAsString());
                }
                if (paymentRow.has("bank_payment_type")) {
                    tranxPaymentPerticulars.setPaymentMethod(paymentRow.get("bank_payment_type").getAsString());
                }
                tranxPaymentPerticulars.setCreatedBy(users.getId());
                TranxPaymentPerticulars mParticular = tranxPaymentPerticularsRepository.save(tranxPaymentPerticulars);
                total_amt = paymentRow.get("paid_amt").getAsDouble();

                /*Receipt Perticulars Details*/

                JsonObject perticulars = paymentRow.get("perticulars").getAsJsonObject();
                JsonArray billList = new JsonArray();
                if (perticulars.has("billids")) {
                    billList = perticulars.get("billids").getAsJsonArray();
                    if (billList != null && billList.size() > 0) {
                        for (int j = 0; j < billList.size(); j++) {
                            TranxPaymentPerticularsDetails tranxPymtDetails = new TranxPaymentPerticularsDetails();
                            JsonObject jsonBill = billList.get(j).getAsJsonObject();
                            TranxPurInvoice mPurInvoice = null;
                            tranxPymtDetails.setBranch(branch);
                            tranxPymtDetails.setOutlet(outlet);
                            tranxPymtDetails.setStatus(true);
                            if (ledgerMaster != null)
                                tranxPymtDetails.setLedgerMaster(ledgerMaster);
                            tranxPymtDetails.setTranxPaymentMaster(tranxPaymentMaster);
                            tranxPymtDetails.setTranxPaymentPerticulars(mParticular);
                            tranxPymtDetails.setStatus(true);
                            tranxPymtDetails.setCreatedBy(users.getId());
                            tranxPymtDetails.setTranxInvoiceId(jsonBill.get("invoice_id").getAsLong());
                            tranxPymtDetails.setType(jsonBill.get("source").getAsString());
                            tranxPymtDetails.setTotalAmt(jsonBill.get("amount").getAsDouble());
                            tranxPymtDetails.setPaidAmt(jsonBill.get("paid_amt").getAsDouble());
                            tranxPymtDetails.setTransactionDate(LocalDate.parse(jsonBill.get("invoice_date").getAsString()));
                            tranxPymtDetails.setTranxNo(jsonBill.get("invoice_no").getAsString());
                            if (jsonBill.get("source").getAsString().equalsIgnoreCase("pur_invoice")) {

                                mPurInvoice = tranxPurInvoiceRepository.findByIdAndStatus(jsonBill.get("invoice_id").getAsLong(), true);
                                if (jsonBill.has("remaining_amt")) {
                                    //tranxReceipt.setBalance(jsonBill.get("remaining_amt").getAsDouble());
                                    mPurInvoice.setBalance(jsonBill.get("remaining_amt").getAsDouble());
                                    tranxPurInvoiceRepository.save(mPurInvoice);
                                }
                            } else if (jsonBill.get("source").getAsString().equalsIgnoreCase("debit_note")) {
                                TranxDebitNoteNewReferenceMaster tranxDebitNoteNewReference =
                                        tranxDebitNoteNewReferenceRepository.findByIdAndStatus(jsonBill.get("invoice_id").getAsLong(), true);

                                if (jsonBill.has("remaining_amt")) {
                                    //tranxReceipt.setBalance(jsonBill.get("remaining_amt").getAsDouble());
                                    Double mbalance = jsonBill.get("remaining_amt").getAsDouble();
                                    tranxDebitNoteNewReference.setBalance(mbalance);
                                    if (mbalance == 0.0) {
                                        TransactionStatus transactionStatus = transactionStatusRepository.findByStatusNameAndStatus(
                                                "closed", true);
                                        tranxDebitNoteNewReference.setTransactionStatus(transactionStatus);
                                        tranxDebitNoteNewReferenceRepository.save(tranxDebitNoteNewReference);
                                    }
                                }
                            } else if (jsonBill.get("source").getAsString().equalsIgnoreCase("credit_note")) {
                               /* TranxCreditNoteNewReferenceMaster tranxCreditNoteNewReference =
                                        tranxCreditNoteNewReferenceRepository.findByIdAndStatus(jsonBill.get("invoice_id").getAsLong(), true);


                                if (jsonBill.has("remaining_amt")) {
                                    Double mbalance = jsonBill.get("remaining_amt").getAsDouble();
                                    tranxCreditNoteNewReference.setBalance(mbalance);
                                    if (mbalance == 0.0) {
                                        TransactionStatus transactionStatus = transactionStatusRepository.findByStatusNameAndStatus(
                                                "closed", true);
                                        tranxCreditNoteNewReference.setTransactionStatus(transactionStatus);
                                        tranxCreditNoteNewReferenceRepository.save(tranxCreditNoteNewReference);
                                    }
                                }*/
                            }
                            // save into tranxRptDetails
                            tranxPaymentPerticularsDetailsRepository.save(tranxPymtDetails);
                        }
                    }
                }

                TranxPaymentPerticulars mPayment = tranxPaymentPerticularsRepository.save(tranxPaymentPerticulars);
                insertIntoPostings(mPayment, total_amt);
            }
            /**** Receipt Transaction at corresposnding branch *****/
            if (request.getParameterMap().containsKey("branch_id") && !request.getParameter("branch_id").equalsIgnoreCase("")) {
                Branch brch = branchRepository.findByIdAndStatus(Long.parseLong(request.getParameter("branch_id")), true);

                /* For Journal Voucher New Scenario Return Payment Student Admission Cancellation*/


                /* For Receipt Voucher Scenario*/
                transactionReceiptForBranch(users, brch, total_amt, tranxDate, inCash);
            }

            response.addProperty("message", "Payment successfully done..");
            response.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            paymentLogger.error("Error in Payment Creation :->" + e.getMessage());
            response.addProperty("message", "Error in Payment creation");
            response.addProperty("responseStatus", HttpStatus.OK.value());
        }
        return response;
    }

    private void transactionReceiptForBranch(Users users, Branch branch, Double total_amt, LocalDate transactionDate, int inCash) {
        TranxReceiptMaster receiptMaster = new TranxReceiptMaster();
        receiptMaster.setOutlet(users.getOutlet());
        receiptMaster.setBranch(branch);
        TransactionTypeMaster tranxType = tranxRepository.findByTransactionNameIgnoreCase("receipt");
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(transactionDate);
        if (fiscalYear != null) {
            receiptMaster.setFiscalYear(fiscalYear);
            receiptMaster.setFinancialYear(fiscalYear.getFiscalYear());
        }
        receiptMaster.setTranscationDate(transactionDate);
        receiptMaster.setTotalAmt(total_amt);
        receiptMaster.setStatus(true);
        Long count = receiptMasterRepositoty.findLastRecord(users.getOutlet().getId(),users.getBranch().getId());
        String serailNo = String.format("%05d", count + 1);// 5 digit serial number
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);

        String rsCode = "RCPT" + currentMonth + serailNo;
        receiptMaster.setReceiptNo(rsCode);
        receiptMaster.setReceiptSrNo(count);
        TranxReceiptMaster mReceipt = receiptMasterRepositoty.save(receiptMaster);

        TranxReceiptPerticulars mParticular1 = new TranxReceiptPerticulars();
        LedgerMaster mBankAaccount = null;
        if (inCash == 1)
            mBankAaccount = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletIdAndBranchIdAndStatus("Cash A/c", users.getOutlet().getId(), branch.getId(), true);
        else
            mBankAaccount = ledgerMasterRepository.findByUniqueCodeAndStatusAndOutletIdAndBranchId("BAAC", true, users.getOutlet().getId(), branch.getId());
        if (mBankAaccount != null) {
            mParticular1.setLedgerMaster(mBankAaccount);
            mParticular1.setLedgerName(mBankAaccount.getLedgerName());
            mParticular1.setDr(total_amt);
            mParticular1.setTranxReceiptMaster(mReceipt);
            mParticular1.setOutlet(users.getOutlet());
            mParticular1.setBranch(branch);
            mParticular1.setTransactionDate(transactionDate);
            mParticular1.setStatus(true);
            mParticular1.setLedgerType("bank_account");
            //    mParticular1.setTranxInvoiceId(invoiceTranx.getId());
            mParticular1.setCreatedBy(users.getId());
            TranxReceiptPerticulars bankAccountParticuler = perticularsRepository.save(mParticular1);
            insertIntoBA(bankAccountParticuler, tranxType, branch, total_amt);//posting into Bank Account
        }
        TranxReceiptPerticulars mParticular2 = new TranxReceiptPerticulars();

        LedgerMaster DpLedgerMaster = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletIdAndBranchIdAndStatus(
                "DP A/C", users.getOutlet().getId(), branch.getId(), true);
        if (DpLedgerMaster != null) {
            mParticular2.setLedgerMaster(DpLedgerMaster);
            mParticular2.setCr(total_amt);
            mParticular2.setTranxReceiptMaster(mReceipt);
            mParticular2.setOutlet(users.getOutlet());
            mParticular2.setBranch(branch);
            mParticular2.setTransactionDate(transactionDate);
            mParticular1.setLedgerName(mBankAaccount.getLedgerName());
            mParticular1.setLedgerType("SD");
            mParticular2.setStatus(true);
//            mParticular2.setTranxInvoiceId(invoiceTranx.getId());
            mParticular2.setCreatedBy(users.getId());
            TranxReceiptPerticulars dpParticuler = perticularsRepository.save(mParticular2);
            insertIntoDP(dpParticuler, tranxType, branch, total_amt);
        }
    }

    private void insertIntoDP(TranxReceiptPerticulars dpParticuler, TransactionTypeMaster tranxType, Branch branch, Double totalAmt) {
        try {
            transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(dpParticuler.getLedgerMaster().getFoundations().getId(),
                    dpParticuler.getLedgerMaster().getPrinciples() != null ? dpParticuler.getLedgerMaster().getPrinciples().getId() : null,
                    dpParticuler.getLedgerMaster().getPrincipleGroups() != null ? dpParticuler.getLedgerMaster().getPrincipleGroups().getId() : null,
                    null, tranxType.getId(),
                    null, branch.getId(),
                    dpParticuler.getOutlet().getId(), "pending",
                    0.0, totalAmt, dpParticuler.getTranxReceiptMaster().getTranscationDate(),
                    null, dpParticuler.getId(), tranxType.getTransactionName(),
                    dpParticuler.getLedgerMaster().getUnderPrefix(), dpParticuler.getTranxReceiptMaster().getFinancialYear(),
                    dpParticuler.getCreatedBy(), dpParticuler.getLedgerMaster().getId(),
                    dpParticuler.getTranxReceiptMaster().getReceiptNo());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Store Procedure Error " + e.getMessage());
        }
    }

    private void insertIntoBA(TranxReceiptPerticulars dpParticuler, TransactionTypeMaster tranxType, Branch branch, Double totalAmt) {
        try {
            transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(dpParticuler.getLedgerMaster().getFoundations().getId(),
                    dpParticuler.getLedgerMaster().getPrinciples() != null ? dpParticuler.getLedgerMaster().getPrinciples().getId() : null,
                    dpParticuler.getLedgerMaster().getPrincipleGroups() != null ? dpParticuler.getLedgerMaster().getPrincipleGroups().getId() : null,
                    null, tranxType.getId(),
                    null, branch.getId(),
                    dpParticuler.getOutlet().getId(), "pending",
                    totalAmt * -1, 0.0, dpParticuler.getTranxReceiptMaster().getTranscationDate(),
                    null, dpParticuler.getId(), tranxType.getTransactionName(),
                    dpParticuler.getLedgerMaster().getUnderPrefix(), dpParticuler.getTranxReceiptMaster().getFinancialYear(),
                    dpParticuler.getCreatedBy(), dpParticuler.getLedgerMaster().getId(),
                    dpParticuler.getTranxReceiptMaster().getReceiptNo());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Store Procedure Error " + e.getMessage());
        }
    }

    /* Accounting Postings of Payment Vouchers  */
    private void insertIntoPostings(TranxPaymentPerticulars paymentRows, double total_amt) {
        TransactionTypeMaster tranxType = tranxRepository.findByTransactionNameIgnoreCase("payment");
        try {
            /* for Sundry Creditors  */
            if (paymentRows.getType().equalsIgnoreCase("dr")) {
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(paymentRows.getLedgerMaster().
                                getFoundations().getId(), paymentRows.getLedgerMaster().getPrinciples() != null ?
                                paymentRows.getLedgerMaster().getPrinciples().getId() : null,
                        paymentRows.getLedgerMaster().getPrincipleGroups() != null ?
                                paymentRows.getLedgerMaster().getPrincipleGroups().getId() : null,
                        null,
                        tranxType.getId(), paymentRows.getLedgerMaster().getBalancingMethod() != null ?
                                paymentRows.getLedgerMaster().getBalancingMethod().getId() : null,
                        paymentRows.getBranch() != null ?
                                paymentRows.getBranch().getId() : null,
                        paymentRows.getOutlet().getId(), "NA", total_amt * -1, 0.0,
                        paymentRows.getTranxPaymentMaster().getTranscationDate(), null, paymentRows.getId(), tranxType.getTransactionName(),
                        paymentRows.getLedgerMaster().getUnderPrefix(), paymentRows.getTranxPaymentMaster().getFinancialYear(),
                        paymentRows.getCreatedBy(), paymentRows.getLedgerMaster().getId(), paymentRows.getTranxPaymentMaster().getPaymentNo());

            } else {
                /* for Cash and Bank Account  */
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(paymentRows.getLedgerMaster().
                                getFoundations().getId(), paymentRows.getLedgerMaster().getPrinciples() != null ?
                                paymentRows.getLedgerMaster().getPrinciples().getId() : null,
                        paymentRows.getLedgerMaster().getPrincipleGroups() != null ?
                                paymentRows.getLedgerMaster().getPrincipleGroups().getId() : null,
                        null,
                        tranxType.getId(), paymentRows.getLedgerMaster().getBalancingMethod() != null ?
                                paymentRows.getLedgerMaster().getBalancingMethod().getId() : null,
                        paymentRows.getBranch() != null ?
                                paymentRows.getBranch().getId() : null,
                        paymentRows.getOutlet().getId(), "NA", 0.0, total_amt,
                        paymentRows.getTranxPaymentMaster().getTranscationDate(), null, paymentRows.getId(), tranxType.getTransactionName(),
                        paymentRows.getLedgerMaster().getUnderPrefix(), paymentRows.getTranxPaymentMaster().getFinancialYear(),
                        paymentRows.getCreatedBy(), paymentRows.getLedgerMaster().getId(), paymentRows.getTranxPaymentMaster().getPaymentNo());
            }
        } catch (Exception e) {
            e.printStackTrace();
            paymentLogger.error("Error in Payment Postings :->" + e.getMessage());
        }
    }

    public JsonObject getCreditorsPendingBillsNew(HttpServletRequest request) {

        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Long ledgerId = Long.parseLong(request.getParameter("ledger_id"));
        String type = request.getParameter("type");
        List<TranxPurInvoice> mInput = new ArrayList<>();
        List<TranxPurInvoice> purInvoice = new ArrayList<>();
        List<TranxDebitNoteNewReferenceMaster> list = new ArrayList<>();
//        List<TranxCreditNoteNewReferenceMaster> listcrd = new ArrayList<>();
        JsonArray result = new JsonArray();
        JsonObject finalResult = new JsonObject();
        try {
            /* start of SC of bill by bill */
            if (type.equalsIgnoreCase("SC")) {
                LedgerMaster ledgerMaster = ledgerMasterRepository.findByIdAndStatus(ledgerId, true);
                /* checking for Bill by bill (bill by bill id: 1) */
                if (ledgerMaster.getBalancingMethod().getId() == 1) {
                    /* find all purchase invoices against sundry creditor */
                    purInvoice = tranxPurInvoiceRepository.
                            findPendingBills(users.getOutlet().getId(), true, ledgerId);
                    if (purInvoice.size() > 0) {
                        for (TranxPurInvoice newPurInvoice : purInvoice) {
                            JsonObject response = new JsonObject();
                            response.addProperty("invoice_id", newPurInvoice.getId());
                            response.addProperty("amount", newPurInvoice.getBalance());
                            response.addProperty("total_amt", newPurInvoice.getTotalAmount());
                            response.addProperty("invoice_date", newPurInvoice.getInvoiceDate().toString());
                            response.addProperty("invoice_no", newPurInvoice.getVendorInvoiceNo());
                            response.addProperty("ledger_id", ledgerId);
                            response.addProperty("source", "pur_invoice");
                            result.add(response);
                        }
                    }
                } else {
                    /*  supplier :  on Account  */
                    LedgerBalanceSummary mBalanceSummary = ledgerBalanceSummaryRepository.findByLedgerMasterId(ledgerId);
                    if (mBalanceSummary != null && mBalanceSummary.getBalance() != 0) {
                        JsonObject response = new JsonObject();
                        response.addProperty("amount", mBalanceSummary.getBalance());
                        response.addProperty("ledger_id", ledgerId);
                        result.add(response);
                    }
                }
                list = tranxDebitNoteNewReferenceRepository.
                        findBySundryCreditorIdAndStatusAndTransactionStatusIdAndAdjustmentStatusAndOutletId(
                                ledgerId, true, 1L, "future", users.getOutlet().getId());
                if (list != null && list.size() > 0) {
                    for (TranxDebitNoteNewReferenceMaster mTranxDebitNote : list) {
                        if (mTranxDebitNote.getBalance() != 0.0) {
                            JsonObject data = new JsonObject();
                            data.addProperty("debit_note_id", mTranxDebitNote.getId());
                            data.addProperty("debit_note_no", mTranxDebitNote.getDebitnoteNewReferenceNo());
                            data.addProperty("debit_note_date", mTranxDebitNote.getCreatedAt().toString());
                            data.addProperty("Total_amt", mTranxDebitNote.getBalance());
                            data.addProperty("source", "debit_note");
                            result.add(data);
                        }
                    }
                }
            }

            if (type.equalsIgnoreCase("SD")) {
                /*listcrd = tranxCreditNoteNewReferenceRepository.
                        findBySundryDebtorsIdAndStatusAndTransactionStatusIdAndAdjustmentStatusAndOutletId(
                                ledgerId, true, 1L, "refund", users.getOutlet().getId());
                if (listcrd != null && listcrd.size() > 0) {
                    for (TranxCreditNoteNewReferenceMaster mTranxCreditNote : listcrd) {
                        if (mTranxCreditNote.getBalance() != 0.0) {
                            JsonObject data = new JsonObject();
                            data.addProperty("credit_note_id", mTranxCreditNote.getId());
                            data.addProperty("credit_note_no", mTranxCreditNote.getCreditnoteNewReferenceNo());
                            data.addProperty("credit_note_date", mTranxCreditNote.getCreatedAt().toString());
                            data.addProperty("Total_amt", mTranxCreditNote.getBalance());
                            data.addProperty("source", "credit_note");
                            result.add(data);
                        }
                    }
                }*/
            }

        } catch (Exception e) {
            paymentLogger.error("Exception in: get_creditors_pending_bills ->" + e.getMessage());
            System.out.println("Exception in: get_creditors_pending_bills ->" + e.getMessage());
            e.printStackTrace();
        }
        finalResult.addProperty("message", "success");
        finalResult.addProperty("responseStatus", HttpStatus.OK.value());
        finalResult.add("list", result);
        return finalResult;
    }

    public JsonObject paymentListbyOutlet(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<TranxPaymentMaster> payment = new ArrayList<>();

        if (users.getBranch() == null)
            payment = tranxPaymentMasterRepository.findByOutletIdAndStatusAndBranchIdIsNullOrderByIdDesc(users.getOutlet().getId(), true);
        else
            payment = tranxPaymentMasterRepository.findByOutletIdAndBranchIdAndStatusOrderByIdDesc(users.getOutlet().getId(), users.getBranch().getId(), true);

        for (TranxPaymentMaster invoices : payment) {
            JsonObject response = new JsonObject();
            response.addProperty("id", invoices.getId());
            response.addProperty("payment_code", invoices.getPaymentNo());
            response.addProperty("transaction_dt", invoices.getTranscationDate().toString());
            response.addProperty("payment_sr_no", invoices.getPaymentSrNo());
            TranxPaymentPerticulars tranxPaymentPerticulars = tranxPaymentPerticularsRepository.
                    findLedgerName(invoices.getId(), users.getOutlet().getId(), true);

            response.addProperty("total_amount", invoices.getTotalAmt());
            response.addProperty("ledger_name", tranxPaymentPerticulars != null ?
                    tranxPaymentPerticulars.getLedgerName() : "");


            result.add(response);
        }

        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);
        return output;
    }
}
