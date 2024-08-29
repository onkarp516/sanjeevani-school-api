package in.truethics.ethics.ethicsapiv10.service.tranx_service.journal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.truethics.ethics.ethicsapiv10.common.GenerateDates;
import in.truethics.ethics.ethicsapiv10.common.GenerateFiscalYear;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalMaster;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerTransactionDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.BranchRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.TransactionTypeMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.FeesTransactionDetailRepository;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.FeesTransactionSummaryRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.journal_repository.TranxJournalDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.journal_repository.TranxJournalMasterRepository;
import in.truethics.ethics.ethicsapiv10.service.master_service.BranchService;
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
public class TranxJournalService {
    private static final Logger journalLogger = LoggerFactory.getLogger(BranchService.class);
    @Autowired
    private TranxJournalMasterRepository tranxJournalMasterRepository;
    @Autowired
    private JwtTokenUtil jwtRequestFilter;

    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private LedgerMasterRepository ledgerMasterRepository;
    @Autowired
    private FeesTransactionDetailRepository feesTransactionDetailRepository;

    @Autowired
    private FeesTransactionSummaryRepository feesTransactionSummaryRepository;
    @Autowired
    private LedgerTransactionDetailsRepository transactionDetailsRepository;
    @Autowired
    private TransactionTypeMasterRepository tranxRepository;
    @Autowired
    private GenerateFiscalYear generateFiscalYear;
    @Autowired

    private TranxJournalDetailsRepository tranxJournalDetailsRepository;

    public JsonObject journalLastRecord(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Long count = tranxJournalMasterRepository.findLastRecord(users.getOutlet().getId(), users.getBranch().getId());
        String serailNo = String.format("%05d", count + 1);// 5 digit serial number
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
        String csCode = "JRNL" + currentMonth + serailNo;
        JsonObject result = new JsonObject();
        result.addProperty("message", "success");
        result.addProperty("responseStatus", HttpStatus.OK.value());
        result.addProperty("count", count + 1);
        result.addProperty("journalNo", csCode);
        return result;
    }


    public JsonObject getledgerDetails(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        List<LedgerMaster> ledgerMaster = ledgerMasterRepository.findledgers(users.getOutlet().getId());
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

    public JsonObject createJournal(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Map<String, String[]> paramMap = request.getParameterMap();
        JsonObject response = new JsonObject();

        TranxJournalMaster journalMaster = new TranxJournalMaster();
        Branch branch = null;
        if (users.getBranch() != null)
            branch = users.getBranch();
        Outlet outlet = users.getOutlet();
        journalMaster.setBranch(branch);
        journalMaster.setOutlet(outlet);
        journalMaster.setStatus(true);
        LocalDate tranxDate = LocalDate.parse(request.getParameter("transaction_dt"));
        /* fiscal year mapping */
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(tranxDate);
        if (fiscalYear != null) {
            journalMaster.setFiscalYear(fiscalYear);
            journalMaster.setFinancialYear(fiscalYear.getFiscalYear());
        }

        journalMaster.setTranscationDate(tranxDate);
        journalMaster.setJournalSrNo(Long.parseLong(request.getParameter("voucher_journal_sr_no")));
        journalMaster.setJournalNo(request.getParameter("voucher_journal_no"));
        journalMaster.setTotalAmt(Double.parseDouble(request.getParameter("total_amt")));
        if (paramMap.containsKey("narration"))
            journalMaster.setNarrations(request.getParameter("narration"));
        else {
            journalMaster.setNarrations("NA");
        }
        journalMaster.setCreatedBy(users.getId());
        TranxJournalMaster tranxJournalMaster = tranxJournalMasterRepository.save(journalMaster);

        try {
            double total_amt = 0.0;
            String jsonStr = request.getParameter("rows");
            JsonParser parser = new JsonParser();
            JsonArray row = parser.parse(jsonStr).getAsJsonArray();
            for (int i = 0; i < row.size(); i++) {
                JsonObject journalRow = row.get(i).getAsJsonObject();
                TranxJournalDetails tranxJournalDetails = new TranxJournalDetails();
                LedgerMaster ledgerMaster = null;

                tranxJournalDetails.setBranch(branch);
                tranxJournalDetails.setOutlet(outlet);
                tranxJournalDetails.setStatus(true);
                ledgerMaster = ledgerMasterRepository.findByIdAndStatus(journalRow.get("perticulars").getAsJsonObject().get("id").getAsLong(), true);
                if (ledgerMaster != null)
                    tranxJournalDetails.setLedgerMaster(ledgerMaster);
                tranxJournalDetails.setTranxJournalMaster(tranxJournalMaster);
                tranxJournalDetails.setType(journalRow.get("type").getAsString());
//                tranxContraDetails.setLedgerName(contraRow.get("perticulars").getAsJsonObject().get("ledger_name").getAsString());
                total_amt = journalRow.get("paid_amt").getAsDouble();

                JsonObject perticulars = journalRow.get("perticulars").getAsJsonObject();
                tranxJournalDetails.setLedgerType(ledgerMaster.getSlugName());
                tranxJournalDetails.setCreatedBy(users.getId());
                TranxJournalDetails mContra = tranxJournalDetailsRepository.save(tranxJournalDetails);
                insertIntoPostings(mContra, total_amt);
            }
            response.addProperty("message", "journal created successfully");
            response.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            journalLogger.error("Error in journal Creation :->" + e.getMessage());
            response.addProperty("message", "Error in Contra creation");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    public JsonObject addReceiptNo(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("authorization").substring(7));
        JsonObject response = new JsonObject();
//        long studentId = Long.valueOf(request.getParameter("studentId"));

        try {
//            long studentId = 289;
            long studentId = 0;
            String receiptNo = null;
            double paidAmt = 0;
            LocalDate transactionDate = null;
            List<Object[]> list = feesTransactionDetailRepository.findByTransactionDateWithYearAndStandards(users.getOutlet().getId(), users.getBranch().getId());
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    Object[] feeObj = list.get(i);

                    LedgerMaster ledgerMaster = ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(
                            (Long.parseLong(feeObj[2].toString())), users.getOutlet().getId(), users.getBranch().getId(), true);

                    paidAmt = Double.parseDouble(feeObj[0].toString());
                    System.out.println("Paid Amount->" + paidAmt);

                    transactionDate = LocalDate.parse(feeObj[1].toString());
                    System.out.println("transaction Date->" + transactionDate);
                    receiptNo = feeObj[3].toString();
                    studentId = Long.parseLong(feeObj[2].toString());

                    double paidAmountExceptHostel = 0;
                    double paidAmountExceptHostelWithConc = 0;
                    double concessionAmt = Double.parseDouble(feeObj[8].toString());
                    double hostelFee = 0;
                    String hostelFeeExist = feesTransactionDetailRepository.getHostelFee(receiptNo);
                    if (hostelFeeExist != null) {
                        hostelFee = Double.parseDouble(hostelFeeExist);
                    }
                    paidAmountExceptHostel = paidAmt - hostelFee;  // 58500-30000 = 28500
                    paidAmountExceptHostelWithConc = paidAmountExceptHostel + concessionAmt;  // 28500+3000 = 31500

                    if (ledgerMaster != null) {
                        List<TranxJournalDetails> tranxJournalDetails = tranxJournalDetailsRepository.findByLedgerMasterIdAndOutletId
                                (ledgerMaster.getId(), users.getOutlet().getId());
                        System.out.println("Student Ledger->" + ledgerMaster.getId());
                        if (tranxJournalDetails.size() > 0) {
                            for (TranxJournalDetails tranxJournalDetails1 : tranxJournalDetails) {
                                System.out.println("JournalDetail's Paid Amount->" + tranxJournalDetails1.getPaidAmount());
                                System.out.println("JournalDetail's TransactionDate->" + tranxJournalDetails1.getTranxJournalMaster().getTranscationDate());
                                if (paidAmountExceptHostel == tranxJournalDetails1.getPaidAmount() && transactionDate.equals(tranxJournalDetails1.getTranxJournalMaster().getTranscationDate())) {
                                    System.out.println("paidAmountExceptHostel " + paidAmountExceptHostel);
                                    TranxJournalMaster journalMaster = tranxJournalDetails1.getTranxJournalMaster();
                                    journalMaster.setFeeReceiptNo(receiptNo);
                                    System.out.println("ReceiptNo->" + receiptNo);
                                    tranxJournalMasterRepository.save(journalMaster);
                                    response.addProperty("message", "Receipt No Added Successfully");
                                    response.addProperty("responseStatus", HttpStatus.OK.value());
                                } else if (paidAmountExceptHostelWithConc == tranxJournalDetails1.getPaidAmount() && transactionDate.equals(tranxJournalDetails1.getTranxJournalMaster().getTranscationDate())) {
                                    System.out.println("paidAmountExceptHostelWithConc " + paidAmountExceptHostelWithConc);
                                    TranxJournalMaster journalMaster = tranxJournalDetails1.getTranxJournalMaster();
                                    journalMaster.setFeeReceiptNo(receiptNo);
                                    System.out.println("ReceiptNo->" + receiptNo);
                                    tranxJournalMasterRepository.save(journalMaster);
                                    response.addProperty("message", "Receipt No Added Successfully");
                                    response.addProperty("responseStatus", HttpStatus.OK.value());
                                } else {
                                    System.out.println("Failed to match Paidamount,TransactionDate");
                                }
                            }
                        }

                        if (hostelFeeExist != null) {
                            Branch hostelBranch = branchRepository.getHostelBranchRecord(users.getOutlet().getId(), true);
                            if (hostelBranch != null) {
                                LedgerMaster ledgerMaster1 = ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(
                                        studentId, users.getOutlet().getId(), hostelBranch.getId(), true);
                                if (ledgerMaster1 != null) {
                                    List<TranxJournalDetails> thostelist = tranxJournalDetailsRepository.findByLedgerMasterIdAndOutletIdAndBranchId(
                                            ledgerMaster1.getId(), users.getOutlet().getId(), hostelBranch.getId());
                                    if (thostelist.size() > 0) {
                                        for (TranxJournalDetails tranxJournalDetails2 : thostelist) {
                                            System.out.println("Journal HostelPaid Amt->" + tranxJournalDetails2.getPaidAmount());
                                            System.out.println("Journal HostelPaid trDate->" + tranxJournalDetails2.getTranxJournalMaster().getTranscationDate());
                                            if (hostelFee == tranxJournalDetails2.getPaidAmount() && transactionDate.equals(tranxJournalDetails2.getTranxJournalMaster().getTranscationDate())) {
                                                System.out.println("hostelFee " + hostelFee);
                                                TranxJournalMaster journalMaster = tranxJournalDetails2.getTranxJournalMaster();
                                                journalMaster.setFeeReceiptNo(receiptNo);
                                                System.out.println("ReceiptNo->" + receiptNo);
                                                tranxJournalMasterRepository.save(journalMaster);
                                                response.addProperty("message", "Receipt No Added Successfully");
                                                response.addProperty("responseStatus", HttpStatus.OK.value());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        System.out.println("Data not Found in TranxjournalDetails");

                    }
                    System.out.println("Ledger Not Found");

                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }


    private void insertIntoPostings(TranxJournalDetails mjournal, double total_amt) {
        TransactionTypeMaster tranxType = tranxRepository.findByTransactionNameIgnoreCase("journal");
        try {
            if (mjournal.getType().equalsIgnoreCase("dr")) {
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(mjournal.getLedgerMaster().
                                getFoundations().getId(), mjournal.getLedgerMaster().getPrinciples() != null ?
                                mjournal.getLedgerMaster().getPrinciples().getId() : null,
                        mjournal.getLedgerMaster().getPrincipleGroups() != null ?
                                mjournal.getLedgerMaster().getPrincipleGroups().getId() : null,
                        null,
                        tranxType.getId(), mjournal.getLedgerMaster().getBalancingMethod() != null ?
                                mjournal.getLedgerMaster().getBalancingMethod().getId() : null,
                        mjournal.getBranch() != null ?
                                mjournal.getBranch().getId() : null,
                        mjournal.getOutlet().getId(), "NA", total_amt * -1, 0.0,
                        mjournal.getTranxJournalMaster().getTranscationDate(), null, mjournal.getId(), tranxType.getTransactionName(),
                        mjournal.getLedgerMaster().getUnderPrefix(), mjournal.getTranxJournalMaster().getFinancialYear(),
                        mjournal.getCreatedBy(), mjournal.getLedgerMaster().getId(), mjournal.getTranxJournalMaster().getJournalNo());

            } else {
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(mjournal.getLedgerMaster().
                                getFoundations().getId(), mjournal.getLedgerMaster().getPrinciples() != null ?
                                mjournal.getLedgerMaster().getPrinciples().getId() : null,
                        mjournal.getLedgerMaster().getPrincipleGroups() != null ?
                                mjournal.getLedgerMaster().getPrincipleGroups().getId() : null,
                        null,
                        tranxType.getId(), mjournal.getLedgerMaster().getBalancingMethod() != null ?
                                mjournal.getLedgerMaster().getBalancingMethod().getId() : null,
                        mjournal.getBranch() != null ?
                                mjournal.getBranch().getId() : null,
                        mjournal.getOutlet().getId(), "NA", 0.0, total_amt,
                        mjournal.getTranxJournalMaster().getTranscationDate(), null, mjournal.getId(), tranxType.getTransactionName(),
                        mjournal.getLedgerMaster().getUnderPrefix(), mjournal.getTranxJournalMaster().getFinancialYear(),
                        mjournal.getCreatedBy(), mjournal.getLedgerMaster().getId(), mjournal.getTranxJournalMaster().getJournalNo());
            }
        } catch (Exception e) {
            e.printStackTrace();
            journalLogger.error("Error in journal Postings :->" + e.getMessage());
        }
    }


    public JsonObject journalListbyOutlet(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<TranxJournalMaster> journal = new ArrayList<>();

        if (users.getBranch() != null)
            journal = tranxJournalMasterRepository.
                    findByOutletIdAndBranchIdAndStatusOrderByIdDesc(users.getOutlet().getId(), users.getBranch().getId(), true);
        else
            journal = tranxJournalMasterRepository.
                    findByOutletIdAndStatusOrderByIdDesc(users.getOutlet().getId(), true);

        for (TranxJournalMaster vouchers : journal) {
            JsonObject response = new JsonObject();
            response.addProperty("id", vouchers.getId());
            response.addProperty("journal_code", vouchers.getJournalNo());
            response.addProperty("transaction_dt", vouchers.getTranscationDate().toString());
            response.addProperty("journal_sr_no", vouchers.getJournalSrNo());
            response.addProperty("narration", vouchers.getNarrations());
//            response.addProperty("ledger_name", vouchers.getContraSrNo());

            response.addProperty("total_amount", vouchers.getTotalAmt());


            result.add(response);
        }

        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);
        return output;
    }

    public JsonObject getJournalDetails(HttpServletRequest request) {
        JsonObject finalObject = new JsonObject();
        JsonArray mJournalArray = new JsonArray();
        JsonObject mResponse = new JsonObject();
        JsonObject res = new JsonObject();
        Users user = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Long id = Long.valueOf(request.getParameter("id"));
        try {
            List<TranxJournalDetails> journalDetails = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(
                    id, user.getOutlet().getId(), user.getBranch().getId(), true, "DR");

            for (TranxJournalDetails journalDetails1 : journalDetails) {
                JsonObject jsonObject = new JsonObject();
                if (journalDetails1.getType().equalsIgnoreCase("CR")) {
                    jsonObject.addProperty("credit", journalDetails1.getPaidAmount());
                } else {
                    jsonObject.addProperty("debit", journalDetails1.getPaidAmount());
                }
                jsonObject.addProperty("tranxType", journalDetails1.getType());
                jsonObject.addProperty("particular", journalDetails1.getLedgerMaster().getLedgerName());
                jsonObject.addProperty("narration", journalDetails1.getTranxJournalMaster().getNarrations());
                mJournalArray.add(jsonObject);
            }

            List<TranxJournalDetails> journalDetails1 = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(
                    id, user.getOutlet().getId(), user.getBranch().getId(), true, "CR");
            for (TranxJournalDetails tranxJournalDetails : journalDetails1) {
                JsonObject jsonObject1 = new JsonObject();
                if (tranxJournalDetails.getType().equalsIgnoreCase("CR")) {
                    jsonObject1.addProperty("credit", tranxJournalDetails.getPaidAmount());
                } else {
                    jsonObject1.addProperty("debit", tranxJournalDetails.getPaidAmount());
                }
                jsonObject1.addProperty("tranxType", tranxJournalDetails.getType());
                jsonObject1.addProperty("particular", tranxJournalDetails.getLedgerMaster().getLedgerName());
                jsonObject1.addProperty("narration", tranxJournalDetails.getTranxJournalMaster().getNarrations());
                mJournalArray.add(jsonObject1);
            }

            finalObject.addProperty("message", "success");
            finalObject.addProperty("responseStatus", HttpStatus.OK.value());
            finalObject.add("data", mJournalArray);
        } catch (Exception e) {
            finalObject.addProperty("message", "Failed to Load Data 1");
            finalObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());

        }
        return finalObject;

    }


}
