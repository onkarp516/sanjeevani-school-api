package in.truethics.ethics.ethicsapiv10.service.student_tranx;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.truethics.ethics.ethicsapiv10.common.GenerateDates;
import in.truethics.ethics.ethicsapiv10.common.GenerateFiscalYear;
import in.truethics.ethics.ethicsapiv10.common.LedgerCommonPostings;
import in.truethics.ethics.ethicsapiv10.common.NumFormat;
import in.truethics.ethics.ethicsapiv10.model.history_table.JournalDetailsHistory;
import in.truethics.ethics.ethicsapiv10.model.history_table.JournalMasterHistory;
import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerBalanceSummary;
import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerTransactionDetails;
import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerTransactionPostings;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesInvoice;
import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesReturnInvoice;
import in.truethics.ethics.ethicsapiv10.model.school_master.*;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionDetail;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionSummary;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.StudentBusTransaction;
import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptPerticularsDetails;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.history_repository.JournalDetailsHistoryRepository;
import in.truethics.ethics.ethicsapiv10.repository.history_repository.JournalMasterHistoryRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerBalanceSummaryRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerTransactionDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerTransactionPostingsRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.*;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.FeesTransactionDetailRepository;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.FeesTransactionSummaryRepository;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.FeesTransctionMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.StudentBusTransactionRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.contra_repository.TranxContraMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.journal_repository.TranxJournalDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.journal_repository.TranxJournalMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository.TranxReceiptMasterRepositoty;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository.TranxReceiptPerticularsDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository.TranxReceiptPerticularsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository.TranxSalesInvoiceDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository.TranxSalesInvoiceRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository.TranxSalesReturnRepository;
import in.truethics.ethics.ethicsapiv10.service.master_service.StudentRegisterService;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.apache.commons.math3.util.Precision;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FeesTransactionService {

    private static final Logger transactionLogger = LogManager.getLogger(FeesTransactionService.class);
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private FeesTransactionSummaryRepository transactionSummaryRepository;
    @Autowired
    private FeesDetailsRepository feesDetailsRepository;

    @Autowired
    private StudentTransportRepository studentTransportRepository;

    @Autowired
    private StudentBusTransactionRepository studentBusTransactionRepository;
    @Autowired
    private FeesTransactionDetailRepository feesTransactionDetailRepository;
    @Autowired
    private StudentRegisterRepository studentRegisterRepository;

    @Autowired
    LedgerTransactionPostingsRepository ledgerTransactionPostingsRepository;
    @Autowired
    private FeesMasterRepository feesMasterRepository;
    @Autowired
    private FeeHeadRepository feeHeadRepository;
    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private LedgerCommonPostings ledgerCommonPostings;

    @Autowired
    private TranxReceiptMasterRepositoty tranxReceiptMasterRepositoty;

    @Autowired
    private TranxReceiptPerticularsRepository tranxReceiptPerticularsRepository;

    @Autowired
    private JournalMasterHistoryRepository journalMasterHistoryRepository;
    @Autowired
    private JournalDetailsHistoryRepository journalDetailsHistoryRepository;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private AcademicYearRepository academicYearRepository;
    @Autowired
    private DivisionRepository divisionRepository;
    @Autowired
    private InstallmentMasterRepository installmentMasterRepository;
    @Autowired
    private SubFeeHeadRepository subFeeHeadRepository;
    @Autowired
    private InstallmentDetailsRepository installmentDetailsRepository;
    @Autowired
    private FeesTransctionMasterRepository feesTransctionMasterRepository;
    @Autowired
    private GenerateFiscalYear generateFiscalYear;
    @Autowired
    private LedgerMasterRepository ledgerMasterRepository;
    @Autowired
    private TranxReceiptMasterRepositoty receiptMasterRepositoty;
    @Autowired
    private NumFormat numFormat;
    @Autowired
    private TranxReceiptPerticularsRepository perticularsRepository;

    @Autowired
    private TranxReceiptPerticularsDetailsRepository tranxReceiptPerticularsDetailsRepository;
    @Autowired
    private TransactionTypeMasterRepository transactionTypeMasterRepository;
    @Autowired
    private LedgerTransactionDetailsRepository ledgerTransactionDetailsRepository;
    @Autowired
    private TranxSalesInvoiceRepository tranxSalesInvoiceRepository;
    @Autowired
    private StudentAdmissionRepository studentAdmissionRepository;
    @Autowired
    private TranxJournalMasterRepository tranxJournalMasterRepository;
    @Autowired
    private TranxJournalDetailsRepository tranxJournalDetailsRepository;
    @Autowired
    private TranxSalesReturnRepository tranxSalesReturnRepository;
    @Autowired
    private StudentRegisterService studentRegisterService;
    @Autowired
    private AssociateGroupsRepository associateGroupsRepository;

    @Autowired
    private TranxSalesInvoiceDetailsRepository tranxSalesInvoiceDetailsRepository;

    @Autowired
    private LedgerBalanceSummaryRepository ledgerBalanceSummaryRepository;
    @Autowired
    private TranxContraMasterRepository tranxContraMasterRepository;

    private static String NumberToWord(String number) {
        String twodigitword = "";
        String word = "";
        String[] HTLC = {"", "Hundred", "Thousand", "Lakh", "Crore"}; //H-hundread , T-Thousand, ..
        int[] split = {0, 2, 3, 5, 7, 11};
        String[] temp = new String[split.length];
        boolean addzero = true;
        int len1 = number.length();
        if (len1 > split[split.length - 1]) {
            System.out.println("Error. Maximum Allowed digits " + split[split.length - 1]);
            System.exit(0);
        }
        for (int l = 1; l < split.length; l++)
            if (number.length() == split[l]) {
                addzero = false;
                break;
            }
        if (addzero) number = "0" + number;
        int len = number.length();
        int j = 0;
        //spliting & putting numbers in temp array.
        while (split[j] < len) {
            int beg = len - split[j + 1];
            int end = beg + split[j + 1] - split[j];
            temp[j] = number.substring(beg, end);
            j = j + 1;
        }

        for (int k = 0; k < j; k++) {
            twodigitword = ConvertOnesTwos(temp[k]);
            if (k >= 1) {
                if (twodigitword.trim().length() != 0) word = twodigitword + " " + HTLC[k] + " " + word;
            } else word = twodigitword;
        }
        return (word);
    }

    private static String ConvertOnesTwos(String t) {
        final String[] ones = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        final String[] tens = {"", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        String word = "";
        int num = Integer.parseInt(t);
        if (num % 10 == 0) word = tens[num / 10] + " " + word;
        else if (num < 20) word = ones[num] + " " + word;
        else {
            word = tens[(num - (num % 10)) / 10] + word;
            word = word + " " + ones[num % 10];
        }
        return word;
    }


    public JsonObject getStudentOutstanding(HttpServletRequest request) {
        JsonObject responseMessage = new JsonObject();
        JsonObject result = new JsonObject();

        try {
//            Long instituteId = Long.valueOf(request.getParameter("instituteId"));
            Long branchId = Long.valueOf(request.getParameter("branchId"));
            Long academicYearId = Long.valueOf(request.getParameter("academicYearId"));
            Long standardId = Long.valueOf(request.getParameter("standardId"));
            Long divisionId = Long.valueOf(request.getParameter("divisionId"));
            Long studentId = Long.valueOf(request.getParameter("studentId"));
            Integer studentType = Integer.valueOf(request.getParameter("studentType"));

            FeesTransactionSummary transactionSummary = transactionSummaryRepository.findByStudentRegisterIdAndAcademicYearIdAndStandardIdAndDivisionIdAndStatus(studentId, academicYearId, standardId, divisionId, true);

            JsonArray payheadsArray = new JsonArray();
            if (transactionSummary != null) {
                result.addProperty("transactionId", transactionSummary.getId());
                result.addProperty("feesMasterId", transactionSummary.getFeesMaster().getId());
//                result.addProperty("outstandingAmount", transactionSummary.getOutStandingAmount());

                List<FeesDetails> feesDetailsList = feesDetailsRepository.findByFeesMasterIdAndStatus(transactionSummary.getFeesMaster().getId(), true);
                for (FeesDetails feesDetails : feesDetailsList) {
                    double fees = 0;
                    JsonObject payheadObj = new JsonObject();
                    payheadObj.addProperty("payHeadId", feesDetails.getFeeHead().getId());
                    payheadObj.addProperty("payHeadName", feesDetails.getFeeHead().getFeeHeadName());
                    payheadObj.addProperty("priority", feesDetails.getPriority());
                    /*payheadObj.addProperty("installment", feesDetails.getInstallment());
                    payheadObj.addProperty("installmentPer", feesDetails.getInstallmentPercentage());*/
                    payheadObj.addProperty("paid", 0);
                    payheadObj.addProperty("concession", 0);

                   /* FeesTransactionDetail transactionDetails1 = feesTransactionDetailRepository.findTop1ByFeeHeadsIdAndFeesTransactionSummaryIdOrderByIdDesc(feesDetails.getFeeHead().getId(), transactionSummary.getId());
                    payheadObj.addProperty("totalFees", transactionDetails1.getAmount());*/
//                    payheadObj.addProperty("balance", transactionDetails1.getOutStandingClosing());

//                    double totalFees = Precision.round(transactionDetails1.getAmount(), 2);
                   /* double balance = Precision.round(transactionDetails1.getOutStandingClosing(), 2);
                    double paid = totalFees - balance;
                    payheadObj.addProperty("paid", paid);*/

                    payheadsArray.add(payheadObj);
                }
                result.add("list", payheadsArray);

                JsonArray installmentArray = new JsonArray();
                List<Object[]> installmentList = installmentMasterRepository.findInstallmentsByFeesMasterId(transactionSummary.getFeesMaster().getId());
                for (int i = 0; i < installmentList.size(); i++) {
                    Object[] obj = installmentList.get(i);
                    installmentArray.add(obj[0].toString());
                }
                result.add("installments", installmentArray);

                JsonArray concessionArray = new JsonArray();
                List<Object[]> concessionList = installmentMasterRepository.findConcessionByFeesMasterId(transactionSummary.getFeesMaster().getId());
                for (int i = 0; i < concessionList.size(); i++) {
                    Object[] obj = concessionList.get(i);
                    concessionArray.add(obj[0].toString());
                }
                result.add("concessions", concessionArray);

                responseMessage.add("responseObject", result);
                responseMessage.addProperty("responseStatus", HttpStatus.OK.value());
            } else {
                result.addProperty("transactionId", 0);
                StudentRegister studentInfo = studentRegisterRepository.findByIdAndStatus(studentId, true);
                FeesMaster feesMaster = feesMasterRepository.findByBranchIdAndStandardIdAndDivisionIdAndAcademicYearIdAndStudentTypeAndStatus(branchId, standardId, divisionId, academicYearId, studentType, true);

                double outstandingAmount = 0;
                if (feesMaster != null) {
                    List<FeesDetails> feesDetailsList = feesDetailsRepository.findByFeesMasterIdAndStatus(feesMaster.getId(), true);
                    for (FeesDetails feesDetails : feesDetailsList) {
                        double fees = 0;
                        JsonObject payheadObj = new JsonObject();
                        payheadObj.addProperty("payHeadId", feesDetails.getFeeHead().getId());
                        payheadObj.addProperty("payHeadName", feesDetails.getFeeHead().getFeeHeadName());
                        payheadObj.addProperty("priority", feesDetails.getPriority());
                       /* payheadObj.addProperty("installment", feesDetails.getInstallment());
                        payheadObj.addProperty("installmentPer", feesDetails.getInstallmentPercentage());*/
                        payheadObj.addProperty("paid", 0);
                        payheadObj.addProperty("concession", 0);

                        fees = Precision.round(feesDetails.getAmount(), 2);
                        if (feesMaster.getStudentType() == 2 && (feesMaster.getStandard().getStandardName().equalsIgnoreCase("11")
                                || feesMaster.getStandard().getStandardName().equalsIgnoreCase("12"))) {

                            if (studentInfo.getGender().equalsIgnoreCase("male")) {
                                payheadObj.addProperty("totalFees", feesDetails.getAmountForBoy());
                                fees = Precision.round(feesDetails.getAmountForBoy(), 2);

                            } else if (studentInfo.getGender().equalsIgnoreCase("female")) {
                                payheadObj.addProperty("totalFees", feesDetails.getAmountForGirl());
                                fees = Precision.round(feesDetails.getAmountForGirl(), 2);
                            }
                        }
                        payheadObj.addProperty("balance", fees);
                        outstandingAmount = outstandingAmount + fees;
                        payheadsArray.add(payheadObj);
                    }
                    result.addProperty("feesMasterId", feesMaster.getId());
                    result.addProperty("outstandingAmount", outstandingAmount);
                    result.add("list", payheadsArray);

                    JsonArray installmentArray = new JsonArray();
                    List<Object[]> installmentList = installmentMasterRepository.findInstallmentsByFeesMasterId(feesMaster.getId());
                    for (int i = 0; i < installmentList.size(); i++) {
                        Object[] obj = installmentList.get(i);
                        installmentArray.add(obj[0].toString());
                    }
                    result.add("installments", installmentArray);

                    JsonArray concessionArray = new JsonArray();
                    List<Object[]> concessionList = installmentMasterRepository.findConcessionByFeesMasterId(feesMaster.getId());
                    for (int i = 0; i < concessionList.size(); i++) {
                        Object[] obj = concessionList.get(i);
                        concessionArray.add(obj[0].toString());
                    }
                    result.add("concessions", concessionArray);

                    responseMessage.add("responseObject", result);
                    responseMessage.addProperty("responseStatus", HttpStatus.OK.value());
                } else {
                    responseMessage.addProperty("message", "No data found");
                    responseMessage.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
                }
            }
        } catch (Exception e) {
            transactionLogger.error("Exception " + e.getMessage());
            System.out.println("Exception " + e.getMessage());
            e.printStackTrace();
        }
        return responseMessage;
    }

    public JsonObject createTransaction(HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        try {
            Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));

            int paymentMode = Integer.parseInt(request.getParameter("paymentMode"));
            Double paidAmt = Double.valueOf(request.getParameter("payable"));
            Double balance = Double.valueOf(request.getParameter("balance"));
            Double outstandingAmt= Double.valueOf(request.getParameter("outstanding"));
            String paymentNo = request.getParameter("paymentNo");
            Double discountAmt = Double.parseDouble(request.getParameter("discount"));
            Long studentInfoId = Long.valueOf(request.getParameter("studentId"));
//            StudentAdmission studentAdmission=studentAdmissionRepository.findByStudentIdAndStatus(studentInfoId,true);
           // StudentRegister studentInfo = studentRegisterRepository.findByIdAndStatus(studentInfoId, true);

            LocalDate trDate = LocalDate.parse(request.getParameter("transactionDate"));
            {
                Long transactionId = Long.valueOf(request.getParameter("transactionId"));
                if (transactionId > 0) {
                    FeesTransactionSummary savedTransactionSummary = transactionSummaryRepository.findById(transactionId).get();
                    if (savedTransactionSummary != null) {
//                        savedTransactionSummary.setPaidAmount(paidAmt);
                        savedTransactionSummary.setUpdatedBy(users.getId());
                        savedTransactionSummary.setUpdatedAt(LocalDateTime.now());
                        savedTransactionSummary.setStatus(true);
                        savedTransactionSummary.setDiscount(discountAmt);
//                        savedTransactionSummary.setStudentType(studentAdmission.getStudentType());


                        try {
                            double paidAmount = 0;
                            double currentPaidAmount = 0;

                            String lastReceiptNo = numFormat.receiptNumFormat(1);
                            FeesTransactionDetail lastRecord = feesTransactionDetailRepository.findTop1ByTransactionDateOrderByIdDesc(trDate);
                            if (lastRecord != null) {
                                String[] lastNo = lastRecord.getReceiptNo().split("-");
                                int lastReceiptNum = Integer.parseInt(lastNo[2]) + 1;
                                lastReceiptNo = numFormat.receiptNumFormat(lastReceiptNum);
                            }

                            String[] academicYr = savedTransactionSummary.getAcademicYear().getYear().split("-");
                            String month = numFormat.twoDigitFormat(trDate.getMonthValue());
                            String day = numFormat.twoDigitFormat(trDate.getDayOfMonth());

                            String receiptNo = academicYr[0].substring(2) + academicYr[1] + "-" + month + day + "-" + lastReceiptNo;
                            if (savedTransactionSummary.getBranch().getBranchCode() != null) {
                                receiptNo = savedTransactionSummary.getBranch().getBranchCode() + receiptNo;
                            }


                            receiptNo = getNewReceiptNo(receiptNo, trDate);


                            System.out.println("*********************************");
                            String jsonStr = request.getParameter("row");
                            JsonParser parser = new JsonParser();
                            FeesTransactionDetail fTranx = null;
                            JsonArray row = parser.parse(jsonStr).getAsJsonArray();
                            for (int i = 0; i < row.size(); i++) {
                                JsonObject installmentRpw = row.get(i).getAsJsonObject();
                                FeesTransactionDetail feesTransactionDetail = null;
                                double opening = 0;

                                FeeHead paymentHeads = null;
                                SubFeeHead subFeeHead = null;
                                paymentHeads = feeHeadRepository.findByIdAndStatus(installmentRpw.get("payHeadId").getAsLong(), true);
                                feesTransactionDetail = feesTransactionDetailRepository.findTop1ByFeesTransactionSummaryIdAndFeeHeadIdOrderByIdDesc(savedTransactionSummary.getId(), paymentHeads.getId());

                                if (feesTransactionDetail == null) {
                                    FeesTransactionDetail transactiondetails = new FeesTransactionDetail();
                                    transactiondetails.setFeesTransactionSummary(savedTransactionSummary);
                                    transactiondetails.setStatus(true);
                                    if (paymentHeads != null) {
                                        transactiondetails.setFeeHead(paymentHeads);
                                    }
                                    transactiondetails.setTransactionDate(trDate);
                                    transactiondetails.setReceiptNo(receiptNo);
                                    transactiondetails.setOpening(opening);
                                    transactiondetails.setAmount(installmentRpw.get("amount").getAsDouble());
                                    transactiondetails.setCreatedBy(users.getId());
                                    transactiondetails.setStatus(true);
                                    transactiondetails.setOutlet(savedTransactionSummary.getOutlet());
                                    transactiondetails.setBranch(savedTransactionSummary.getBranch());
                                    transactiondetails.setPaymentMode(paymentMode);
                                    transactiondetails.setConcessionAmount((double) 0);
                                    transactiondetails.setPaidAmount(installmentRpw.get("amount").getAsDouble());
                                    transactiondetails.setHeadFee(installmentRpw.get("amount").getAsDouble());
                                    transactiondetails.setSpecialConcessionAmount((double) 0);
                                    if (paymentMode != 0) {
                                        transactiondetails.setPaymentNo(paymentNo);
                                    }
                                    fTranx = feesTransactionDetailRepository.save(transactiondetails);
                                    paidAmount = paidAmount + installmentRpw.get("amount").getAsDouble();

                                } else {
                                    FeesTransactionDetail transactiondetails = new FeesTransactionDetail();
                                    transactiondetails.setFeesTransactionSummary(savedTransactionSummary);
                                    transactiondetails.setStatus(true);
                                    if (paymentHeads != null) {
                                        transactiondetails.setFeeHead(paymentHeads);
                                    }
                                    transactiondetails.setTransactionDate(trDate);
                                    transactiondetails.setReceiptNo(receiptNo);
                                    transactiondetails.setOpening(opening);
                                    transactiondetails.setAmount(installmentRpw.get("amount").getAsDouble());
                                    transactiondetails.setCreatedBy(users.getId());
                                    transactiondetails.setStatus(true);
                                    transactiondetails.setOutlet(savedTransactionSummary.getOutlet());
                                    transactiondetails.setBranch(savedTransactionSummary.getBranch());
                                    transactiondetails.setPaymentMode(paymentMode);
                                    if (paymentMode != 0) {
                                        transactiondetails.setPaymentNo(paymentNo);
                                    }
                                    transactiondetails.setConcessionAmount((double) 0);
                                    transactiondetails.setPaidAmount(installmentRpw.get("amount").getAsDouble());
                                    transactiondetails.setHeadFee(installmentRpw.get("amount").getAsDouble());
                                    transactiondetails.setSpecialConcessionAmount((double) 0);
                                    fTranx = feesTransactionDetailRepository.save(transactiondetails);
                                    paidAmount = paidAmount + installmentRpw.get("amount").getAsDouble();
                                }
                            }
                            savedTransactionSummary.setPaidAmount(savedTransactionSummary.getPaidAmount() + paidAmt);
                            savedTransactionSummary.setBalance(balance);
                            /******** Get Hostel fee by get hostel receipt under heads amount ********/
                            String hostelFeeExist = feesTransactionDetailRepository.getPaidHostelFeeByReceiptNo(receiptNo, false);
                            double hostelFee = 0;
                            if (hostelFeeExist != null) {
                                System.out.println("hostelFeeExist " + hostelFeeExist);
                                hostelFee = Double.parseDouble(hostelFeeExist);
                            }
                            double paidAmountExceptHostelFee = currentPaidAmount - hostelFee;
                            System.out.println("currentPaidAmount " + currentPaidAmount);
                            System.out.println("paidAmountExceptHostelFee " + paidAmountExceptHostelFee);
                            System.out.println("hostelFee " + hostelFee);

                            FeesTransactionSummary mSummary = transactionSummaryRepository.save(savedTransactionSummary);

                            String studName = savedTransactionSummary.getStudentRegister().getFirstName();
                            if (savedTransactionSummary.getStudentRegister().getFatherName() != null)
                                studName = studName + " " + savedTransactionSummary.getStudentRegister().getFatherName();
                            if (savedTransactionSummary.getStudentRegister().getLastName() != null)
                                studName = studName + " " + savedTransactionSummary.getStudentRegister().getLastName();

                            String narration = "Being cash deposited against fee from " + studName + " vide R.No. " + receiptNo
                                    + " " + savedTransactionSummary.getBranch().getBranchName() + " STD." + savedTransactionSummary.getStandard().getStandardName();
                            if (paymentMode != 0) {
                                narration = "Being online fee received from " + studName + " vide R.No. " + receiptNo
                                        + " " + savedTransactionSummary.getBranch().getBranchName() + " STD." + savedTransactionSummary.getStandard().getStandardName();
                            }
                            /*** Inserting into Transaction Reciept Master****/
                            TransactionTypeMaster tranxType = transactionTypeMasterRepository.findByTransactionCodeIgnoreCase("RCPT");
                            LedgerMaster mStudent = ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(
                                    mSummary.getStudentRegister().getId(), users.getOutlet().getId(), users.getBranch().getId(), true);

                            /**** Receipt Voucher *****/
                                System.out.println("narration >>>>>>>>>>>>>>>>>>>>>>>" + narration);
                                System.out.println("paidAmountExceptHostelFee >>>>>>>>>>>>>>>>>>>>>>>" + paidAmountExceptHostelFee);
                                TranxReceiptMaster receiptMaster = new TranxReceiptMaster();
                                Branch branch = null;
                                if (users.getBranch() != null) branch = users.getBranch();
                                Outlet outlet = users.getOutlet();
                                Long count = tranxReceiptMasterRepositoty.findLastRecord(outlet.getId(), branch.getId());
                                String serailNo = String.format("%05d", count + 1);// 5 digit serial number
                                GenerateDates generateDates = new GenerateDates();
                                String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
                                receiptMaster.setOutlet(outlet);
                                receiptMaster.setBranch(branch);
                                receiptMaster.setReceiptNo(fTranx.getReceiptNo());
                                receiptMaster.setTranscationDate(trDate);

                                receiptMaster.setReceiptSrNo(count + 1);
                                long studentId = fTranx.getFeesTransactionSummary().getStudentRegister().getId();
                                LedgerMaster studentLedger = ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(studentId, users.getOutlet().getId(), users.getBranch().getId(), true);
                                receiptMaster.setStudentLedger(studentLedger);
                                receiptMaster.setUnderBranch(users.getBranch());
                                receiptMaster.setTotalAmt(paidAmt+discountAmt); //outstanding
                                receiptMaster.setNarrations(narration);
                                receiptMaster.setCreatedBy(users.getId());
                                receiptMaster.setStatus(true);
                                /* fiscal year mapping*/
                                FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(trDate);
                                if (fiscalYear != null) {
                                    receiptMaster.setFiscalYear(fiscalYear);
                                    receiptMaster.setFinancialYear(fiscalYear.getFiscalYear());
                                }
                                TranxReceiptMaster tranxReceiptMaster = tranxReceiptMasterRepositoty.save(receiptMaster);
                                try {
                                    /**** Tranx Reciept Details for Student A/C *****/
                                    TranxReceiptPerticularsDetails tranxReceiptPerticularsDetails = new TranxReceiptPerticularsDetails();
                                    tranxReceiptPerticularsDetails.setBranch(branch);
                                    tranxReceiptPerticularsDetails.setOutlet(outlet);
                                    tranxReceiptPerticularsDetails.setStatus(true);
                                    tranxReceiptPerticularsDetails.setTransactionDate(trDate);
                                    tranxReceiptPerticularsDetails.setLedgerMaster(mStudent);
                                    tranxReceiptPerticularsDetails.setTranxReceiptMaster(tranxReceiptMaster);
                                    tranxReceiptPerticularsDetails.setType("CR");
                                    tranxReceiptPerticularsDetails.setCreatedBy(users.getId());
                                    tranxReceiptPerticularsDetails.setPaidAmt(paidAmt+discountAmt);///outstanding
                                    tranxReceiptPerticularsDetails.setStatus(true);
                                    TranxReceiptPerticularsDetails receiptPerticularsDetails4 = tranxReceiptPerticularsDetailsRepository.save(tranxReceiptPerticularsDetails);
                                    insertIntoSDReceipt(receiptPerticularsDetails4, tranxType); //Accounting postings of Student Account
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    /* *** Tranx Reciept Details for CASH OR BANK *****/
                                    TranxReceiptPerticularsDetails tranxReceiptPerticularsDetails2 = new TranxReceiptPerticularsDetails();
                                    LedgerMaster ledgerMaster = null;
                                    if (fTranx.getPaymentMode() == 0) {

                                        ledgerMaster = ledgerMasterRepository.findByUniqueCodeAndStatusAndOutletIdAndBranchId("CAIH", true, outlet.getId(), branch.getId());

                                    } else {
                                       /* long ledgerId = Long.parseLong(request.getParameter("ledgerId"));
                                        System.out.println("LedgerId-->" + ledgerId);*/
                                        ledgerMaster = ledgerMasterRepository.findByUniqueCodeAndStatusAndOutletIdAndBranchId("BAAC", true, outlet.getId(), branch.getId());
                                    }
                                    tranxReceiptPerticularsDetails2.setBranch(branch);
                                    tranxReceiptPerticularsDetails2.setOutlet(outlet);
                                    tranxReceiptPerticularsDetails2.setStatus(true);
                                    tranxReceiptPerticularsDetails2.setLedgerMaster(ledgerMaster);
                                    tranxReceiptPerticularsDetails2.setTranxReceiptMaster(tranxReceiptMaster);
                                    tranxReceiptPerticularsDetails2.setType("DR");
                                    tranxReceiptPerticularsDetails2.setTransactionDate(trDate);
                                    tranxReceiptPerticularsDetails2.setCreatedBy(users.getId());
                                    tranxReceiptPerticularsDetails2.setPaidAmt(paidAmt);
                                    tranxReceiptPerticularsDetails2.setStatus(true);
                                    TranxReceiptPerticularsDetails receiptPerticularsDetails = tranxReceiptPerticularsDetailsRepository.save(tranxReceiptPerticularsDetails2);
                                    insertIntoReceipt(receiptPerticularsDetails, tranxType);//Accounting postings of DP Account

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                /**** Tranx Receipt Details for Concession A/C *****/
                                if (discountAmt > 0) {
                                    try {
                                        /* *** Tranx Receipt Details for Concession A/C *****/
                                        TranxReceiptPerticularsDetails concessionReceiptPerticularsDetails = new TranxReceiptPerticularsDetails();
                                        LedgerMaster concessionLedgerMaster = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletIdAndBranchIdAndStatus("Concession A/C", outlet.getId(), branch.getId(), true);
                                        concessionReceiptPerticularsDetails.setBranch(branch);
                                        concessionReceiptPerticularsDetails.setOutlet(outlet);
                                        concessionReceiptPerticularsDetails.setStatus(true);
                                        concessionReceiptPerticularsDetails.setLedgerMaster(concessionLedgerMaster);
                                        concessionReceiptPerticularsDetails.setTranxReceiptMaster(tranxReceiptMaster);
                                        concessionReceiptPerticularsDetails.setType("DR");
                                        concessionReceiptPerticularsDetails.setTransactionDate(trDate);
                                        concessionReceiptPerticularsDetails.setCreatedBy(users.getId());
                                        concessionReceiptPerticularsDetails.setPaidAmt(discountAmt);
                                        concessionReceiptPerticularsDetails.setStatus(true);
                                        TranxReceiptPerticularsDetails concessionReceiptPerticularsDetails1 = tranxReceiptPerticularsDetailsRepository.save(concessionReceiptPerticularsDetails);
                                        insertIntoReceipt(concessionReceiptPerticularsDetails1, tranxType);//Accounting postings of Concession Account
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }



                            /**** Receipt Voucher For Hostel Branch  *****/
                            Branch hostelBranch = branchRepository.getHostelBranchRecord(users.getOutlet().getId(), true);


                            System.out.println("*************************");

                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Exception " + e.getMessage());
                            responseObject.addProperty("message", "Failed to save fees transaction");
                            responseObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());

                            return responseObject;
                        }
                        responseObject.addProperty("message", "Fees transaction saved successfully");
                        responseObject.addProperty("responseStatus", HttpStatus.OK.value());
                    }
                }
            }
        } catch (Exception e1) {
            System.out.println("Exception " + e1.getMessage());
            e1.printStackTrace();
            responseObject.addProperty("message", "Error");

            responseObject.addProperty("message", "Failed to save fees transaction");
            responseObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return responseObject;
    }



    public JsonObject createTransactionForVidyalay(HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        try {
            Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));

            int paymentMode = Integer.parseInt(request.getParameter("paymentMode"));
            int specialConcessionAmt = request.getParameter("specialConcessionAmt").equalsIgnoreCase("") ? 0 : Integer.parseInt(request.getParameter("specialConcessionAmt"));
            String paymentNo = request.getParameter("paymentNo");
//            String receiptNo=request.getParameter("receiptNo");

//            List<FeesTransactionDetail> feesTransactionDetails = feesTransactionDetailRepository.findByReceiptNo(receiptNo);
//            if(feesTransactionDetails.size()>0)
//            {
//                responseObject.addProperty("message","Receipt No Already Exist");
//                responseObject.addProperty("responseStatus",HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//                return responseObject;
//            }
//            else
            {
                boolean applyConcession = Boolean.parseBoolean(request.getParameter("applyConcession"));
                Long transactionId = Long.valueOf(request.getParameter("transactionId"));
                if (transactionId > 0) {
                    FeesTransactionSummary savedTransactionSummary = transactionSummaryRepository.findById(transactionId).get();
                    if (savedTransactionSummary != null) {

                        int concessionAmount = 0;
                        int concessionType = Integer.valueOf(request.getParameter("concessionNo"));
                        boolean isAppliedConcession = false;

                        if (Integer.valueOf(request.getParameter("concessionNo")) == 1) {
                            concessionAmount = 0;
                        } else if (Integer.valueOf(request.getParameter("concessionNo")) == 2) {
                            concessionAmount = 3000;
                        } else if (Integer.valueOf(request.getParameter("concessionNo")) == 4) {
                            concessionAmount = 2000;
                        } else if (Integer.valueOf(request.getParameter("concessionNo")) == 3) {
                            concessionAmount = 1000;
                        }

//                    if (savedTransactionSummary.getConcessionType() != null && savedTransactionSummary.getConcessionType() != concessionType) {
                        String dbConcessionNo = savedTransactionSummary.getConcessionType() != null ? savedTransactionSummary.getConcessionType().toString() : "";
                        if (!dbConcessionNo.equalsIgnoreCase(String.valueOf(concessionType)) && concessionType != 0) {
                            isAppliedConcession = true;
                        }
                        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< isAppliedConcession >>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + isAppliedConcession);

                        savedTransactionSummary.setConcessionType(Integer.valueOf(request.getParameter("concessionNo")));
                        if (savedTransactionSummary.getConcessionType() != 0) {
                            savedTransactionSummary.setIsManual(false);
                            /*if (savedTransactionSummary.getIsManual() != null && savedTransactionSummary.getIsManual() == true) {
                            }else {
                                savedTransactionSummary.setIsManual(true);
                            }*/
                        } else {
                            savedTransactionSummary.setIsManual(true);
                        }

                    /*savedTransactionSummary.setConcessionType(Integer.valueOf(request.getParameter("concessionNo")));
                    if (savedTransactionSummary.getConcessionType() == 0) {
                        if (savedTransactionSummary.getIsManual() != null && savedTransactionSummary.getIsManual()) {
                            savedTransactionSummary.setIsManual(false);
                        }
                        savedTransactionSummary.setIsManual(true);
                    } else if(isAppliedConcession){
                        savedTransactionSummary.setIsManual(false);
                    }*/
//                    savedTransactionSummary.setIsManual(concessionType == 0);

                        savedTransactionSummary.setUpdatedBy(users.getId());
                        savedTransactionSummary.setUpdatedAt(LocalDateTime.now());
                        savedTransactionSummary.setStatus(true);
                        try {
                            double paidAmount = 0;
                            double currentPaidAmount = 0;

                            String lastReceiptNo = numFormat.receiptNumFormat(1);
                            LocalDate trDate = LocalDate.parse(request.getParameter("transactionDate"));

//                        String receiptNo = request.getParameter("receiptNo").trim();
                            FeesTransactionDetail lastRecord = feesTransactionDetailRepository.findTop1ByTransactionDateOrderByIdDesc(trDate);
                            if (lastRecord != null) {
                                String[] lastNo = lastRecord.getReceiptNo().split("-");
                                int lastReceiptNum = Integer.parseInt(lastNo[2]) + 1;
                                lastReceiptNo = numFormat.receiptNumFormat(lastReceiptNum);
                            }

                            String[] academicYr = savedTransactionSummary.getAcademicYear().getYear().split("-");
                            String month = numFormat.twoDigitFormat(trDate.getMonthValue());
                            String day = numFormat.twoDigitFormat(trDate.getDayOfMonth());
//
                            String receiptNo = academicYr[0].substring(2) + academicYr[1] + "-" + month + day + "-" + lastReceiptNo;
                            if (savedTransactionSummary.getBranch().getBranchCode() != null) {
                                receiptNo = savedTransactionSummary.getBranch().getBranchCode() + receiptNo;
                            }

                            receiptNo = getNewReceiptNo(receiptNo, trDate);

                            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ receiptNo ^^^^^^^^^^^^^^^" + receiptNo);

                            /*responseObject.addProperty("message",receiptNo);
                            responseObject.addProperty("responseStatus",HttpStatus.OK.value());
                            return responseObject;*/

                            System.out.println("*********************************");
                            String jsonStr = request.getParameter("row");
                            JsonParser parser = new JsonParser();
                            FeesTransactionDetail fTranx = null;
                            JsonArray row = parser.parse(jsonStr).getAsJsonArray();
                            for (int i = 0; i < row.size(); i++) {
                                JsonObject installmentRpw = row.get(i).getAsJsonObject();

                                int installmentNo = installmentRpw.get("installmentNo").getAsInt();
                                JsonArray headsArr = installmentRpw.get("installmentData").getAsJsonArray();
                                for (int ij = 0; ij < headsArr.size(); ij++) {
                                    JsonObject headObj = headsArr.get(ij).getAsJsonObject();
//                                if (headObj.get("paid").getAsDouble() > 0) {
                                    if (headObj.get("cPaid").getAsDouble() > 0) {
                                        FeesTransactionDetail feesTransactionDetail = null;
                                        double opening = 0;

                                        FeeHead paymentHeads = null;
                                        SubFeeHead subFeeHead = null;
                                        if (headObj.get("isSubHead").getAsInt() == 0) {
                                            paymentHeads = feeHeadRepository.findByIdAndStatus(headObj.get("payHeadId").getAsLong(), true);
                                            feesTransactionDetail = feesTransactionDetailRepository.findTop1ByFeesTransactionSummaryIdAndFeeHeadIdAndInstallmentNoOrderByIdDesc(savedTransactionSummary.getId(), paymentHeads.getId(), installmentNo);
                                        } else {
                                            subFeeHead = subFeeHeadRepository.findByIdAndStatus(headObj.get("payHeadId").getAsLong(), true);
                                            feesTransactionDetail = feesTransactionDetailRepository.findTop1ByFeesTransactionSummaryIdAndSubFeeHeadIdAndInstallmentNoOrderByIdDesc(savedTransactionSummary.getId(), subFeeHead.getId(), installmentNo);
                                        }

                                        if (feesTransactionDetail == null) {
                                            FeesTransactionDetail transactiondetails = new FeesTransactionDetail();
                                            transactiondetails.setFeesTransactionSummary(savedTransactionSummary);
                                            transactiondetails.setStatus(true);
                                            if (paymentHeads != null) {
                                                transactiondetails.setFeeHead(paymentHeads);
                                            }
                                            if (subFeeHead != null) {
                                                transactiondetails.setSubFeeHead(subFeeHead);
                                                transactiondetails.setFeeHead(subFeeHead.getFeeHead());
                                            }
                                            transactiondetails.setTransactionDate(trDate);
                                            transactiondetails.setReceiptNo(receiptNo);
                                            transactiondetails.setInstallmentNo(installmentNo);
                                            transactiondetails.setHeadFee(headObj.get("totalFees").getAsDouble());
                                            transactiondetails.setOpening(opening);
                                            transactiondetails.setPaidAmount(headObj.get("cPaid").getAsDouble());
                                            transactiondetails.setAmount(headObj.get("paid").getAsDouble());
                                            transactiondetails.setBalance(headObj.get("balance").getAsDouble());
                                            transactiondetails.setConcessionAmount(headObj.get("concession").getAsDouble());
                                            transactiondetails.setSpecialConcessionAmount((double) 0);
                                            transactiondetails.setCreatedBy(users.getId());
                                            transactiondetails.setStatus(true);
                                            transactiondetails.setOutlet(savedTransactionSummary.getOutlet());
                                            transactiondetails.setBranch(savedTransactionSummary.getBranch());
                                            transactiondetails.setPaymentMode(paymentMode);
                                            if (paymentMode != 0) {
                                                transactiondetails.setPaymentNo(paymentNo);
                                            }
                                            fTranx = feesTransactionDetailRepository.save(transactiondetails);

                                            paidAmount = paidAmount + headObj.get("cPaid").getAsDouble();
                                        } else {
                                            // if payment head record already exists
                                            if (feesTransactionDetail.getBalance() > 0) { // if payment head record already exists but still balance > 0
                                                FeesTransactionDetail transactiondetails = new FeesTransactionDetail();
                                                transactiondetails.setFeesTransactionSummary(savedTransactionSummary);
                                                transactiondetails.setStatus(true);
                                                if (paymentHeads != null) {
                                                    transactiondetails.setFeeHead(paymentHeads);
                                                }
                                                if (subFeeHead != null) {
                                                    transactiondetails.setSubFeeHead(subFeeHead);
                                                    transactiondetails.setFeeHead(subFeeHead.getFeeHead());
                                                }
                                                transactiondetails.setTransactionDate(trDate);
                                                transactiondetails.setReceiptNo(receiptNo);

                                                transactiondetails.setInstallmentNo(installmentNo);
                                                transactiondetails.setHeadFee(headObj.get("totalFees").getAsDouble());
                                                transactiondetails.setOpening(opening);
                                                transactiondetails.setPaidAmount(headObj.get("cPaid").getAsDouble());
                                                transactiondetails.setAmount(headObj.get("paid").getAsDouble());
                                                transactiondetails.setBalance(headObj.get("balance").getAsDouble());
                                                transactiondetails.setConcessionAmount(headObj.get("concession").getAsDouble());
                                                transactiondetails.setSpecialConcessionAmount((double) 0);
                                                transactiondetails.setCreatedBy(users.getId());
                                                transactiondetails.setStatus(true);
                                                transactiondetails.setOutlet(savedTransactionSummary.getOutlet());
                                                transactiondetails.setBranch(savedTransactionSummary.getBranch());
                                                transactiondetails.setPaymentMode(paymentMode);
                                                if (paymentMode != 0) {
                                                    transactiondetails.setPaymentNo(paymentNo);
                                                }
                                                fTranx = feesTransactionDetailRepository.save(transactiondetails);

                                                paidAmount = paidAmount + headObj.get("cPaid").getAsDouble();
//                                            paidAmount = feesTransactionDetail.getBalance();
                                            } else if (applyConcession) {
                                                FeesTransactionDetail transactiondetails = new FeesTransactionDetail();
                                                transactiondetails.setFeesTransactionSummary(savedTransactionSummary);
                                                transactiondetails.setStatus(true);
                                                if (paymentHeads != null) {
                                                    transactiondetails.setFeeHead(paymentHeads);
                                                }
                                                if (subFeeHead != null) {
                                                    transactiondetails.setSubFeeHead(subFeeHead);
                                                    transactiondetails.setFeeHead(subFeeHead.getFeeHead());
                                                }
                                                transactiondetails.setTransactionDate(trDate);
                                                transactiondetails.setReceiptNo(receiptNo);
                                                transactiondetails.setInstallmentNo(installmentNo);
                                                transactiondetails.setHeadFee(headObj.get("totalFees").getAsDouble());
                                                transactiondetails.setOpening(opening);
                                                transactiondetails.setPaidAmount(headObj.get("cPaid").getAsDouble());
                                                transactiondetails.setAmount(headObj.get("paid").getAsDouble());
                                                transactiondetails.setBalance(headObj.get("balance").getAsDouble());
                                                transactiondetails.setConcessionAmount(headObj.get("concession").getAsDouble());
                                                transactiondetails.setSpecialConcessionAmount((double) 0);
                                                transactiondetails.setCreatedBy(users.getId());
                                                transactiondetails.setStatus(true);
                                                transactiondetails.setOutlet(savedTransactionSummary.getOutlet());
                                                transactiondetails.setBranch(savedTransactionSummary.getBranch());
                                                transactiondetails.setPaymentMode(paymentMode);
                                                if (paymentMode != 0) {
                                                    transactiondetails.setPaymentNo(paymentNo);
                                                }
                                                fTranx = feesTransactionDetailRepository.save(transactiondetails);

                                                paidAmount = paidAmount + headObj.get("cPaid").getAsDouble();
                                            }
                                        }
                                    }
                                }
                            }

                            double totalFees = savedTransactionSummary.getTotalFees();
                            currentPaidAmount = paidAmount;
                            paidAmount = paidAmount + savedTransactionSummary.getPaidAmount();

                            double balance = totalFees - paidAmount;
                            System.out.println("balance =====> " + balance);
                            System.out.println("concessionAmount =====> " + concessionAmount);
                            double newbalance = Precision.round(balance - Double.parseDouble(String.valueOf(concessionAmount)), 2);
                            System.out.println("newbalance =====> " + newbalance);

                            savedTransactionSummary.setConcessionAmount(concessionAmount);
                            savedTransactionSummary.setPaidAmount(paidAmount);
                            savedTransactionSummary.setBalance(newbalance);

                            /******** Get Hostel fee by searching hostel name ********/
//                        String hostelFeeExist = feesTransactionDetailRepository.getHostelFee(receiptNo);

                            /******** Get Hostel fee by get hostel receipt under heads amount ********/
                            String hostelFeeExist = feesTransactionDetailRepository.getPaidHostelFeeByReceiptNo(receiptNo, false);

                            double hostelFee = 0;

                            if (hostelFeeExist != null) {
                                System.out.println("hostelFeeExist " + hostelFeeExist);
                                hostelFee = Double.parseDouble(hostelFeeExist);
                            }
                            double paidAmountExceptHostelFee = currentPaidAmount - hostelFee;

                            System.out.println("current concessionAmount " + concessionAmount);
                            System.out.println("currentPaidAmount " + currentPaidAmount);
                            System.out.println("paidAmountExceptHostelFee " + paidAmountExceptHostelFee);
                            System.out.println("hostelFee " + hostelFee);

                            FeesTransactionSummary mSummary = transactionSummaryRepository.save(savedTransactionSummary);

                            String studName = savedTransactionSummary.getStudentRegister().getFirstName();
                            if (savedTransactionSummary.getStudentRegister().getFatherName() != null)
                                studName = studName + " " + savedTransactionSummary.getStudentRegister().getFatherName();
                            if (savedTransactionSummary.getStudentRegister().getLastName() != null)
                                studName = studName + " " + savedTransactionSummary.getStudentRegister().getLastName();

                            String narration = "Being cash deposited against fee from " + studName + " vide R.No. " + receiptNo
                                    + " " + savedTransactionSummary.getBranch().getBranchName() + " STD." + savedTransactionSummary.getStandard().getStandardName();
                            if (paymentMode != 0) {
                                narration = "Being online fee received from " + studName + " vide R.No. " + receiptNo
                                        + " " + savedTransactionSummary.getBranch().getBranchName() + " STD." + savedTransactionSummary.getStandard().getStandardName();
                            }

                            if (!isAppliedConcession)
                                concessionAmount = 0;
                            double stdAcct = paidAmountExceptHostelFee + concessionAmount;
                            double dpAcct = paidAmountExceptHostelFee;
                            System.out.println("stdAcct >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + stdAcct);
                            System.out.println("dpAcct >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + dpAcct);
                            System.out.println("concessionAmount >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + concessionAmount);

                            /*** Inserting into Transaction Journal Master****/
                            TransactionTypeMaster tranxType = transactionTypeMasterRepository.findByTransactionCodeIgnoreCase("RCPT");
                            LedgerMaster mStudent = ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(
                                    mSummary.getStudentRegister().getId(), users.getOutlet().getId(), users.getBranch().getId(), true);

                            /**** Journal Voucher For Central Branch  *****/
                            if (paidAmountExceptHostelFee > 0) {
                                System.out.println("narration >>>>>>>>>>>>>>>>>>>>>>>" + narration);
                                System.out.println("paidAmountExceptHostelFee >>>>>>>>>>>>>>>>>>>>>>>" + paidAmountExceptHostelFee);
                                TranxReceiptMaster receiptMaster = new TranxReceiptMaster();
                                Branch branch = null;
                                if (users.getBranch() != null) branch = users.getBranch();
                                Outlet outlet = users.getOutlet();
                                Long count = tranxReceiptMasterRepositoty.findLastRecord(outlet.getId(), branch.getId());
                                String serailNo = String.format("%05d", count + 1);// 5 digit serial number
                                GenerateDates generateDates = new GenerateDates();
                                String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
//                                String rtCode = "RCPT" + currentMonth + serailNo;
                                receiptMaster.setOutlet(outlet);
                                receiptMaster.setBranch(branch);
                                receiptMaster.setReceiptNo(fTranx.getReceiptNo());
                                receiptMaster.setTranscationDate(trDate);
//                                FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(trDate);
//                                if (fiscalYear != null) {
//                                    receiptMaster.setFiscalYear(fiscalYear);
//                                    receiptMaster.setFinancialYear(fiscalYear.getFiscalYear());
//                                }
//
                                receiptMaster.setReceiptSrNo(count + 1);
                                long studentId = fTranx.getFeesTransactionSummary().getStudentRegister().getId();
                                LedgerMaster studentLedger = ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(studentId, users.getOutlet().getId(), users.getBranch().getId(), true);
                                receiptMaster.setStudentLedger(studentLedger);
                                receiptMaster.setUnderBranch(users.getBranch());
//                        journalMaster.setTotalAmt(currentPaidAmount);
                                receiptMaster.setTotalAmt(stdAcct);
//                            journalMaster.setNarrations("Journal entry from student to DP Acc:" + mStudent.getId());
                                receiptMaster.setNarrations(narration);
                                receiptMaster.setCreatedBy(users.getId());
                                receiptMaster.setStatus(true);
                                /* fiscal year mapping*/
                                FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(trDate);
                                if (fiscalYear != null) {
                                    receiptMaster.setFiscalYear(fiscalYear);
                                    receiptMaster.setFinancialYear(fiscalYear.getFiscalYear());
                                }
                                TranxReceiptMaster tranxReceiptMaster = tranxReceiptMasterRepositoty.save(receiptMaster);
                                try {
                                    /**** Tranx Journal Details for Student A/C *****/
                                    TranxReceiptPerticularsDetails tranxReceiptPerticularsDetails = new TranxReceiptPerticularsDetails();
                                    tranxReceiptPerticularsDetails.setBranch(branch);
                                    tranxReceiptPerticularsDetails.setOutlet(outlet);
                                    tranxReceiptPerticularsDetails.setStatus(true);
                                    tranxReceiptPerticularsDetails.setTransactionDate(trDate);
                                    tranxReceiptPerticularsDetails.setLedgerMaster(mStudent);
                                    tranxReceiptPerticularsDetails.setTranxReceiptMaster(tranxReceiptMaster);
                                    tranxReceiptPerticularsDetails.setType("CR");
                                    tranxReceiptPerticularsDetails.setCreatedBy(users.getId());
                                    tranxReceiptPerticularsDetails.setPaidAmt(stdAcct);
                                    tranxReceiptPerticularsDetails.setStatus(true);
                                    TranxReceiptPerticularsDetails receiptPerticularsDetails4 = tranxReceiptPerticularsDetailsRepository.save(tranxReceiptPerticularsDetails);
                                    insertIntoSDReceipt(receiptPerticularsDetails4, tranxType); //Accounting postings of Student Account
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    /* *** Tranx Journal Details for CASH OR BANK *****/
                                    TranxReceiptPerticularsDetails tranxReceiptPerticularsDetails2 = new TranxReceiptPerticularsDetails();
                                    LedgerMaster DpLedgerMaster = null;
                                    if (fTranx.getPaymentMode() == 0) {

                                        DpLedgerMaster = ledgerMasterRepository.findByUniqueCodeAndStatusAndOutletIdAndBranchId("CAIH", true, outlet.getId(), branch.getId());

                                    } else {
                                        long ledgerId = Long.parseLong(request.getParameter("ledgerId"));
                                        System.out.println("LedgerId-->" + ledgerId);
                                        DpLedgerMaster = ledgerMasterRepository.findByIdAndOutletIdAndBranchIdAndStatus(ledgerId, outlet.getId(), branch.getId(), true);

                                    }
                                    tranxReceiptPerticularsDetails2.setBranch(branch);
                                    tranxReceiptPerticularsDetails2.setOutlet(outlet);
                                    tranxReceiptPerticularsDetails2.setStatus(true);
                                    tranxReceiptPerticularsDetails2.setLedgerMaster(DpLedgerMaster);
                                    tranxReceiptPerticularsDetails2.setTranxReceiptMaster(tranxReceiptMaster);
                                    tranxReceiptPerticularsDetails2.setType("DR");
                                    tranxReceiptPerticularsDetails2.setTransactionDate(trDate);
                                    tranxReceiptPerticularsDetails2.setCreatedBy(users.getId());
                                    tranxReceiptPerticularsDetails2.setPaidAmt(dpAcct);
                                    tranxReceiptPerticularsDetails2.setStatus(true);
                                    TranxReceiptPerticularsDetails receiptPerticularsDetails = tranxReceiptPerticularsDetailsRepository.save(tranxReceiptPerticularsDetails2);
                                    insertIntoReceipt(receiptPerticularsDetails, tranxType);//Accounting postings of DP Account

                                } catch (Exception e) {
                                    e.printStackTrace();

                                }

                                /**** Tranx Journal Details for Concession A/C *****/
                                if (isAppliedConcession && concessionAmount > 0) {
                                    try {
                                        /* *** Tranx Journal Details for Concession A/C *****/
                                        TranxReceiptPerticularsDetails concessionReceiptPerticularsDetails = new TranxReceiptPerticularsDetails();

                                        LedgerMaster concessionLedgerMaster = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletIdAndBranchIdAndStatus("Concession A/C", outlet.getId(), branch.getId(), true);
                                        concessionReceiptPerticularsDetails.setBranch(branch);
                                        concessionReceiptPerticularsDetails.setOutlet(outlet);
                                        concessionReceiptPerticularsDetails.setStatus(true);
                                        concessionReceiptPerticularsDetails.setLedgerMaster(concessionLedgerMaster);
                                        concessionReceiptPerticularsDetails.setTranxReceiptMaster(tranxReceiptMaster);
                                        concessionReceiptPerticularsDetails.setType("DR");
                                        concessionReceiptPerticularsDetails.setTransactionDate(trDate);
//                                        concessionReceiptPerticularsDetails.setLedgerType(concessionLedgerMaster.getSlugName());
                                        concessionReceiptPerticularsDetails.setCreatedBy(users.getId());
                                        concessionReceiptPerticularsDetails.setPaidAmt(Double.valueOf(concessionAmount));
                                        concessionReceiptPerticularsDetails.setStatus(true);
                                        TranxReceiptPerticularsDetails concessionReceiptPerticularsDetails1 = tranxReceiptPerticularsDetailsRepository.save(concessionReceiptPerticularsDetails);
                                        insertIntoReceipt(concessionReceiptPerticularsDetails1, tranxType);//Accounting postings of Concession Account

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            /**** Journal Voucher For Hostel Branch  *****/
//                            Branch hostelBranch = branchRepository.getHostelBranchRecord(users.getOutlet().getId(), true);

                            /**** Posting code ended here****/

                            System.out.println("*************************");

                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Exception " + e.getMessage());
                            responseObject.addProperty("message", "Failed to save fees transaction");
                            responseObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());

                            return responseObject;
                        }
                        responseObject.addProperty("message", "Fees transaction saved successfully");
                        responseObject.addProperty("responseStatus", HttpStatus.OK.value());
                    }
                } else {
                    FeesTransactionSummary transactionSummary = new FeesTransactionSummary();
                    transactionSummary.setOutlet(users.getOutlet());
                    Long branchId = Long.valueOf(request.getParameter("branchId"));
                    Branch branch = branchRepository.findByIdAndStatus(branchId, true);
                    transactionSummary.setBranch(branch);

                    Long standardId = Long.valueOf(request.getParameter("standardId"));
                    Standard standard = standardRepository.findByIdAndStatus(standardId, true);
                    transactionSummary.setStandard(standard);

                    Long academicYearId = Long.valueOf(request.getParameter("academicYearId"));
                    AcademicYear academicYear = academicYearRepository.findByIdAndStatus(academicYearId, true);
                    transactionSummary.setAcademicYear(academicYear);

                    Long divisionId = Long.valueOf(request.getParameter("divisionId"));
                    Division division = divisionRepository.findByIdAndStatus(divisionId, true);
                    transactionSummary.setDivision(division);

                    Long studentInfoId = Long.valueOf(request.getParameter("studentId"));
                    StudentRegister studentInfo = studentRegisterRepository.findByIdAndStatus(studentInfoId, true);
                    transactionSummary.setStudentRegister(studentInfo);

                    Long feesMasterId = Long.valueOf(request.getParameter("feesMasterId"));
                    FeesMaster feesMaster = feesMasterRepository.findByIdAndStatus(feesMasterId, true);
                    transactionSummary.setFeesMaster(feesMaster);

                    transactionSummary.setBalance(Double.valueOf(request.getParameter("balance")));
                    transactionSummary.setTotalFees(Double.valueOf(request.getParameter("totalFees")));
                    transactionSummary.setPaidAmount(Double.valueOf(request.getParameter("payable")));
                    transactionSummary.setStudentType(studentInfo.getStudentType());
                    transactionSummary.setStudentType(Integer.valueOf(request.getParameter("concessionNo")));

                    if (Integer.valueOf(request.getParameter("concessionNo")) == 2) {
                        transactionSummary.setConcessionAmount(1000);
                    } else if (Integer.valueOf(request.getParameter("concessionNo")) == 3) {
                        transactionSummary.setConcessionAmount(3000);
                    } else if (Integer.valueOf(request.getParameter("concessionNo")) == 4) {
                        transactionSummary.setConcessionAmount(2000);
                    }
                    transactionSummary.setCreatedBy(users.getId());
                    transactionSummary.setStudentType(studentInfo.getStudentType());
                    transactionSummary.setStudentGroup(studentInfo.getStudentGroup());
                    transactionSummary.setStatus(true);
                    try {
                        FeesTransactionSummary newTransactionSummary = transactionSummaryRepository.save(transactionSummary);

                    /*String lastReceiptNo = numFormat.receiptNumFormat(Integer.parseInt(lastReceipt[2]));
                    String month = numFormat.twoDigitFormat(LocalDate.now().getMonthValue());
                    String day = numFormat.twoDigitFormat(LocalDate.now().getDayOfMonth());*/

                        String jsonStr = request.getParameter("row");
                        JsonParser parser = new JsonParser();
                        JsonArray row = parser.parse(jsonStr).getAsJsonArray();
                        for (int i = 0; i < row.size(); i++) {
                            JsonObject installmentRpw = row.get(i).getAsJsonObject();

                            int installmentNo = installmentRpw.get("installmentNo").getAsInt();
                            JsonArray headsArr = installmentRpw.get("installmentDetails").getAsJsonArray();

                            for (int ij = 0; ij < headsArr.size(); ij++) {
                                JsonObject headObj = headsArr.get(ij).getAsJsonObject();

                                if (headObj.get("balance").getAsDouble() > 0) {
                                    FeesTransactionDetail transactiondetails = new FeesTransactionDetail();
                                    transactiondetails.setFeesTransactionSummary(newTransactionSummary);
                                    transactiondetails.setStatus(true);
                                    transactiondetails.setOutlet(users.getOutlet());
                                    transactiondetails.setBranch(branch);

                                    if (headObj.get("isSubHead").getAsInt() == 0) {
                                        FeeHead paymentHeads = feeHeadRepository.findByIdAndStatus(headObj.get("payHeadId").getAsLong(), true);
                                        transactiondetails.setFeeHead(paymentHeads);
                                    } else {
                                        SubFeeHead subFeeHead = subFeeHeadRepository.findByIdAndStatus(headObj.get("payHeadId").getAsLong(), true);
                                        transactiondetails.setSubFeeHead(subFeeHead);
                                        transactiondetails.setFeeHead(subFeeHead.getFeeHead());
                                    }
                                    transactiondetails.setInstallmentNo(headObj.get("installmentNo").getAsInt());
                                    transactiondetails.setAmount(headObj.get("totalFees").getAsDouble());
                                    transactiondetails.setBalance(headObj.get("balance").getAsDouble());
                                    transactiondetails.setCreatedBy(users.getId());
                                    transactiondetails.setStatus(true);
                                    feesTransactionDetailRepository.save(transactiondetails);
                                }
                            }
                        }
                        responseObject.addProperty("message", "Fees transaction saved successfully");
                        responseObject.addProperty("responseStatus", HttpStatus.OK.value());
                    } catch (DataIntegrityViolationException e) {
                        e.printStackTrace();
                        System.out.println("Exception " + e.getMessage());
                        responseObject.addProperty("message", "Internal Server Error");
                        responseObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
                    } catch (Exception e1) {
                        System.out.println("Exception " + e1.getMessage());
                        e1.printStackTrace();
                        responseObject.addProperty("message", "Error");
                    }
                }
            }
        } catch (Exception e1) {
            System.out.println("Exception " + e1.getMessage());
            e1.printStackTrace();
            responseObject.addProperty("message", "Error");

            responseObject.addProperty("message", "Failed to save fees transaction");
            responseObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return responseObject;
    }

    private String getNewReceiptNo(String lastReceiptNo, LocalDate trDate) {
        FeesTransactionDetail lastRecord = feesTransactionDetailRepository.findTop1ByReceiptNoOrderByIdDesc(lastReceiptNo);
        if (lastRecord != null) {
            String[] lastNo = lastRecord.getReceiptNo().split("-");
            int lastReceiptNum = Integer.parseInt(lastNo[2]) + 1;
            lastReceiptNo = numFormat.receiptNumFormat(lastReceiptNum);

            System.out.println("+++++++++++++++++++ Loop lastReceiptNo +++++++++++++" + lastReceiptNo);
//            SG2223-1017-001
            lastReceiptNo = lastNo[0] + "-" + lastNo[1] + "-" + lastReceiptNo;
            System.out.println("+++++++++++++++++++ Loop lastReceiptNo +++++++++++++" + lastReceiptNo);
            FeesTransactionDetail newlastRecord = feesTransactionDetailRepository.findTop1ByReceiptNoOrderByIdDesc(lastReceiptNo);
            if (newlastRecord != null) {
                lastReceiptNo = getNewReceiptNo(newlastRecord.getReceiptNo(), trDate);
            }
        }
        return lastReceiptNo;
    }

    /**** Accounting posting of DP A/C against Fees Transactions *****/
    private void insertIntoDPJournal(TranxJournalDetails journalDetails, TransactionTypeMaster tranxType) {
        try {
            ledgerTransactionDetailsRepository.insertIntoLegerTranxDetailWithTranxDate(journalDetails.getLedgerMaster().getFoundations().getId(),
                    journalDetails.getLedgerMaster().getPrinciples().getId(), journalDetails.getLedgerMaster().getPrincipleGroups() != null ?
                            journalDetails.getLedgerMaster().getPrincipleGroups().getId() : null, null, tranxType.getId(),
                    null, journalDetails.getBranch() != null ? journalDetails.getBranch().getId() : null,
                    journalDetails.getOutlet().getId(), "pending", journalDetails.getPaidAmount() * -1, 0.0,
                    journalDetails.getTranxJournalMaster().getTranscationDate(), null, journalDetails.getTranxJournalMaster().getId(),
                    tranxType.getTransactionName(), journalDetails.getLedgerMaster().getUnderPrefix(), journalDetails.getTranxJournalMaster().getFinancialYear(),
                    journalDetails.getCreatedBy(), journalDetails.getLedgerMaster().getId(), journalDetails.getTranxJournalMaster().getJournalNo(), true);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Store Procedure Error " + e.getMessage());
        }
    }

    private void insertIntoReceipt(TranxReceiptPerticularsDetails tranxReceiptPerticularsDetails, TransactionTypeMaster tranxType) {
        try {
           /* ledgerTransactionDetailsRepository.insertIntoLegerTranxDetailWithTranxDate(tranxReceiptPerticularsDetails.getLedgerMaster().getFoundations().getId(),
                    tranxReceiptPerticularsDetails.getLedgerMaster().getPrinciples().getId(), tranxReceiptPerticularsDetails.getLedgerMaster().getPrincipleGroups() != null ?
                            tranxReceiptPerticularsDetails.getLedgerMaster().getPrincipleGroups().getId() : null, null, tranxType.getId(),
                    null, tranxReceiptPerticularsDetails.getBranch() != null ? tranxReceiptPerticularsDetails.getBranch().getId() : null,
                    tranxReceiptPerticularsDetails.getOutlet().getId(), "pending", tranxReceiptPerticularsDetails.getPaidAmt() * -1, 0.0,
                    tranxReceiptPerticularsDetails.getTranxReceiptMaster().getTranscationDate(), null, tranxReceiptPerticularsDetails.getTranxReceiptMaster().getId(),
                    tranxType.getTransactionName(), tranxReceiptPerticularsDetails.getLedgerMaster().getUnderPrefix(), tranxReceiptPerticularsDetails.getTranxReceiptMaster().getFinancialYear(),
                    tranxReceiptPerticularsDetails.getCreatedBy(), tranxReceiptPerticularsDetails.getLedgerMaster().getId(), tranxReceiptPerticularsDetails.getTranxReceiptMaster().getReceiptNo(), true);

*/
            ledgerCommonPostings.callToPostings(tranxReceiptPerticularsDetails.getPaidAmt(), tranxReceiptPerticularsDetails.getLedgerMaster(),
                    tranxType, null, tranxReceiptPerticularsDetails.getTranxReceiptMaster().getFiscalYear() != null ? tranxReceiptPerticularsDetails.getTranxReceiptMaster().getFiscalYear() : null,
                    tranxReceiptPerticularsDetails.getBranch() != null ? tranxReceiptPerticularsDetails.getBranch() : null, tranxReceiptPerticularsDetails.getOutlet() != null ? tranxReceiptPerticularsDetails.getOutlet() : null,
                    tranxReceiptPerticularsDetails.getTransactionDate(), tranxReceiptPerticularsDetails.getTranxReceiptMaster().getId(), tranxReceiptPerticularsDetails.getTranxReceiptMaster().getReceiptNo(),
                    "DR", true, tranxReceiptPerticularsDetails.getType(), "insert");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Store Procedure Error " + e.getMessage());
        }
    }

    /**** Accounting posting of Student A/C against Fees Transactions *****/
    private void insertIntoSDJournal(TranxJournalDetails journalDetails, TransactionTypeMaster tranxType) {
        try {

            ledgerTransactionDetailsRepository.insertIntoLegerTranxDetailWithTranxDate(journalDetails.getLedgerMaster().getFoundations().getId(),
                    journalDetails.getLedgerMaster().getPrinciples().getId(), journalDetails.getLedgerMaster().getPrincipleGroups().getId(),
                    null, tranxType.getId(), null, journalDetails.getBranch() != null ? journalDetails.getBranch().getId() : null,
                    journalDetails.getOutlet().getId(), "pending", 0.0, journalDetails.getPaidAmount(),
                    journalDetails.getTranxJournalMaster().getTranscationDate(), null, journalDetails.getTranxJournalMaster().getId(),
                    tranxType.getTransactionName(), journalDetails.getLedgerMaster().getUnderPrefix(), journalDetails.getTranxJournalMaster().getFinancialYear(),
                    journalDetails.getCreatedBy(), journalDetails.getLedgerMaster().getId(), journalDetails.getTranxJournalMaster().getJournalNo(), true);

            /*ledgerTransactionDetailsRepository.insertIntoLegerTranxDetail(journalDetails.getLedgerMaster().getFoundations().getId(),
                    journalDetails.getLedgerMaster().getPrinciples().getId(), journalDetails.getLedgerMaster().getPrincipleGroups().getId(),
                    null, tranxType.getId(), null, journalDetails.getBranch() != null ? journalDetails.getBranch().getId() : null,
                    journalDetails.getOutlet().getId(), "pending", 0.0, journalDetails.getPaidAmount(),
                    journalDetails.getTranxJournalMaster().getTranscationDate(), null, journalDetails.getTranxJournalMaster().getId(),
                    tranxType.getTransactionName(), journalDetails.getLedgerMaster().getUnderPrefix(), journalDetails.getTranxJournalMaster().getFinancialYear(),
                    journalDetails.getCreatedBy(), journalDetails.getLedgerMaster().getId(), journalDetails.getTranxJournalMaster().getJournalNo(), true);*/

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Store Procedure Error " + e.getMessage());
        }
    }

    private void insertIntoSDReceipt(TranxReceiptPerticularsDetails tranxReceiptPerticularsDetails, TransactionTypeMaster tranxType) {
        try {

          /*  ledgerTransactionDetailsRepository.insertIntoLegerTranxDetailWithTranxDate(tranxReceiptPerticularsDetails.getLedgerMaster().getFoundations().getId(),
                    tranxReceiptPerticularsDetails.getLedgerMaster().getPrinciples().getId(), tranxReceiptPerticularsDetails.getLedgerMaster().getPrincipleGroups().getId(),
                    null, tranxType.getId(), null, tranxReceiptPerticularsDetails.getBranch() != null ? tranxReceiptPerticularsDetails.getBranch().getId() : null,
                    tranxReceiptPerticularsDetails.getOutlet().getId(), "pending", 0.0, tranxReceiptPerticularsDetails.getPaidAmt(),
                    tranxReceiptPerticularsDetails.getTranxReceiptMaster().getTranscationDate(), null, tranxReceiptPerticularsDetails.getTranxReceiptMaster().getId(),
                    tranxType.getTransactionName(), tranxReceiptPerticularsDetails.getLedgerMaster().getUnderPrefix(), tranxReceiptPerticularsDetails.getTranxReceiptMaster().getFinancialYear(),
                    tranxReceiptPerticularsDetails.getCreatedBy(), tranxReceiptPerticularsDetails.getLedgerMaster().getId(), tranxReceiptPerticularsDetails.getTranxReceiptMaster().getReceiptNo(), true);
*/

            ledgerCommonPostings.callToPostings(tranxReceiptPerticularsDetails.getPaidAmt(), tranxReceiptPerticularsDetails.getLedgerMaster(),
                    tranxType, null, tranxReceiptPerticularsDetails.getTranxReceiptMaster().getFiscalYear() != null ? tranxReceiptPerticularsDetails.getTranxReceiptMaster().getFiscalYear() : null,
                    tranxReceiptPerticularsDetails.getBranch() != null ? tranxReceiptPerticularsDetails.getBranch() : null, tranxReceiptPerticularsDetails.getOutlet() != null ? tranxReceiptPerticularsDetails.getOutlet() : null,
                    tranxReceiptPerticularsDetails.getTransactionDate(), tranxReceiptPerticularsDetails.getTranxReceiptMaster().getId(), tranxReceiptPerticularsDetails.getTranxReceiptMaster().getReceiptNo(),
                    "CR", true, tranxReceiptPerticularsDetails.getType(), "insert");


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Store Procedure Error " + e.getMessage());
        }
    }

    public JsonObject getTransactionList(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        try {
            Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            JsonArray result = new JsonArray();
            Long outletId = users.getOutlet().getId();
            List<FeesTransactionSummary> transactionSummaryList = new ArrayList<>();
            if (users.getBranch() != null) {
                transactionSummaryList = transactionSummaryRepository.findByOutletIdAndBranchIdAndStatus(outletId, users.getBranch().getId(), true);
            } else {
                transactionSummaryList = transactionSummaryRepository.findByOutletIdAndStatus(outletId, true);
            }
            for (FeesTransactionSummary transactionSummary : transactionSummaryList) {
                JsonObject transObject = new JsonObject();

                transObject.addProperty("id", transactionSummary.getId());
                transObject.addProperty("branchName", transactionSummary.getBranch().getBranchName());
                transObject.addProperty("studentName", transactionSummary.getStudentRegister().getFirstName() + " " + transactionSummary.getStudentRegister().getLastName());
                transObject.addProperty("academicYear", transactionSummary.getAcademicYear().getYear());
                transObject.addProperty("standard", transactionSummary.getStandard().getStandardName());
                transObject.addProperty("division", transactionSummary.getDivision() != null ? transactionSummary.getDivision().getDivisionName() : "");

                if (transactionSummary.getStudentType() == 1) {
                    transObject.addProperty("studentType", "Day Scholar");
                } else if (transactionSummary.getStudentType() == 2) {
                    transObject.addProperty("studentType", "Residential");
                }

                if (transactionSummary.getStudentGroup() != null && transactionSummary.getStudentGroup() == 1) {
                    transObject.addProperty("studentGroup", "PCM");
                } else if (transactionSummary.getStudentGroup() != null && transactionSummary.getStudentGroup() == 2) {
                    transObject.addProperty("studentGroup", "PCB");
                }
                transObject.addProperty("totalFees", transactionSummary.getTotalFees());
                transObject.addProperty("totalPaid", transactionSummary.getPaidAmount());
                transObject.addProperty("outstanding", transactionSummary.getBalance());

                result.add(transObject);
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
            return res;
        } catch (Exception e) {
            transactionLogger.error("Exception " + e);
            transactionLogger.error("Exception " + e.getMessage());

            e.printStackTrace();
            res.addProperty("message", "Failed to load data");

            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return res;
        }
    }

    public JsonObject getTransactionListByStandard(HttpServletRequest request) {

        JsonObject result = new JsonObject();
        JsonArray res = new JsonArray();
        Map<String, String[]> paramMap = request.getParameterMap();

        Long standardId = 0L;
        Integer studentType = 0;
        Long academicYearId = 0L;
        try {
            Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));

            Long branchId = Long.valueOf(request.getParameter("branchId"));

            if (paramMap.containsKey("standardId")) standardId = Long.valueOf(request.getParameter("standardId"));
            if (paramMap.containsKey("studentType")) studentType = Integer.valueOf(request.getParameter("studentType"));
            if (paramMap.containsKey("academicYearId"))
                academicYearId = Long.valueOf(request.getParameter("academicYearId"));


            List<FeesTransactionSummary> list = new ArrayList<>();
            if (academicYearId != 0 && standardId != 0 && studentType != 0) {
                list = transactionSummaryRepository.findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndStudentTypeAndStatusRawQuery(users.getOutlet().getId(), branchId, academicYearId, standardId, studentType, true);
            } else if (academicYearId != 0 && standardId != 0) {
                list = transactionSummaryRepository.findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndStatusRawQuery(users.getOutlet().getId(), branchId, academicYearId, standardId, true);

            } else if (academicYearId != 0 && studentType != 0) {
                list = transactionSummaryRepository.findByOutletIdAndBranchIdAndAcademicYearIdAndStudentTypeAndStatusRawQuery(users.getOutlet().getId(), branchId, academicYearId, studentType, true);

            } else if (standardId != 0 && studentType != 0) {
                list = transactionSummaryRepository.findByOutletIdAndBranchIdAndStandardIdAndStudentTypeAndStatusRawQuery(users.getOutlet().getId(), branchId, standardId, studentType, true);
            } else if (academicYearId != 0) {
                list = transactionSummaryRepository.findByOutletIdAndBranchIdAndAcademicYearIdAndStatusRawQuery(users.getOutlet().getId(), branchId, academicYearId, true);

            } else if (standardId != 0) {
                list = transactionSummaryRepository.findByOutletIdAndBranchIdAndStandardIdAndStatusRawQuery(users.getOutlet().getId(), branchId, standardId, true);

            } else if (studentType != 0) {
                list = transactionSummaryRepository.findByOutletIdAndBranchIdAndStudentTypeAndStatusRawQuery(users.getOutlet().getId(), branchId, studentType, true);

            } else {
                list = transactionSummaryRepository.findByOutletIdAndBranchIdAndStatusRawQuery(users.getOutlet().getId(), branchId, true);
            }
            for (FeesTransactionSummary transactionSummary : list) {
                JsonObject transObject = new JsonObject();

                System.out.println(("transactionSummary.getId() " + transactionSummary.getId()));
                transObject.addProperty("id", transactionSummary.getId());
                transObject.addProperty("branchName", transactionSummary.getBranch().getBranchName());

                transObject.addProperty("firstName", transactionSummary.getStudentRegister().getFirstName());
                transObject.addProperty("fatherName", transactionSummary.getStudentRegister().getFatherName());
                transObject.addProperty("lastName", transactionSummary.getStudentRegister().getLastName());
                transObject.addProperty("studentName", transactionSummary.getStudentRegister().getFirstName() + " " + transactionSummary.getStudentRegister().getLastName());
                transObject.addProperty("academicYear", transactionSummary.getAcademicYear().getYear());
                transObject.addProperty("standard", transactionSummary.getStandard().getStandardName());
                transObject.addProperty("discount",transactionSummary.getDiscount());
                transObject.addProperty("division", transactionSummary.getDivision() != null ? transactionSummary.getDivision().getDivisionName() : "");

                if (transactionSummary.getStudentType() == 1) {
                    transObject.addProperty("studentType", "Day Scholar");
                } else if (transactionSummary.getStudentType() == 2) {
                    transObject.addProperty("studentType", "Residential");
                }

                if (transactionSummary.getStudentGroup() != null && transactionSummary.getStudentGroup() == 1) {
                    transObject.addProperty("studentGroup", "PCM");
                } else if (transactionSummary.getStudentGroup() != null && transactionSummary.getStudentGroup() == 2) {
                    transObject.addProperty("studentGroup", "PCB");
                }
                /******** Get Hostel fee by searching hostel name ********/
//                double hostelFee = studentRegisterService.getHostelHeadFee(transactionSummary.getStudentRegister(), transactionSummary.getFeesMaster());

                /******** Get Hostel fee by get hostel receipt under heads amount ********/
//                double hostelFee = studentRegisterService.getHostelHeadFees(transactionSummary.getStudentRegister(), transactionSummary.getFeesMaster());
//                double totalFeeExceptHostel = transactionSummary.getTotalFees() - hostelFee;

                double paidHostelFee = 0;
                /******** Get Hostel fee by searching hostel name ********/
//                String hostelFeeExist = feesTransactionDetailRepository.getHostelFeePaid(transactionSummary.getId());

                /******** Get Hostel fee by get hostel receipt under heads amount ********/
              //  String hostelFeeExist = feesTransactionDetailRepository.getPaidHostelFeeHeadsByTransactionSummary(transactionSummary.getId(), false);
//                System.out.println("hostelFeeExist " + hostelFeeExist);
//                if (hostelFeeExist != null) {
//                    paidHostelFee = Double.parseDouble(hostelFeeExist);
//                }
                double paidFeeExceptHostel = transactionSummary.getPaidAmount() - paidHostelFee;

                transObject.addProperty("totalFees", transactionSummary.getTotalFees());
//                transObject.addProperty("totalFeeExceptHostel", totalFeeExceptHostel);
//                transObject.addProperty("hostelFee", hostelFee);
                transObject.addProperty("paidFeeExceptHostel", paidFeeExceptHostel);
                transObject.addProperty("paidHostelFee", paidHostelFee);
                transObject.addProperty("totalPaid", transactionSummary.getPaidAmount());
                transObject.addProperty("outstanding", transactionSummary.getBalance());
                transObject.addProperty("discount",transactionSummary.getDiscount());
                transObject.addProperty("concessionAmount", transactionSummary.getConcessionAmount());

                res.add(transObject);
            }


            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.add("responseObject", res);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.addProperty("message", "Failed to load data");
            return result;
        }


    }


    public JsonObject getTransactionDetailsById(HttpServletRequest request) {
        JsonObject result = new JsonObject();
        try {
            Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Long transactionId = Long.valueOf(request.getParameter("transactionId"));
            String lastReceiptNo = request.getParameter("lastReceiptNo");
//            SELECT receipt_no FROM `fees_transaction_detail_tbl` WHERE fees_transaction_summary_id=206 GROUP BY receipt_no ORDER BY id DESC LIMIT 1
            FeesTransactionSummary feesTransactionSummary = transactionSummaryRepository.findByIdAndStatus(transactionId, true);
            if (lastReceiptNo.equalsIgnoreCase("")) {
                lastReceiptNo = transactionSummaryRepository.findTransactionIdbyReceipt(transactionId);
            }

            JsonObject summaryObject = new JsonObject();

            if (feesTransactionSummary != null) {
                summaryObject.addProperty("id", feesTransactionSummary.getId());
                summaryObject.addProperty("studentName", feesTransactionSummary.getStudentRegister().getFirstName() + " " + feesTransactionSummary.getStudentRegister().getMiddleName() + " " + feesTransactionSummary.getStudentRegister().getLastName());
                summaryObject.addProperty("schoolName", feesTransactionSummary.getBranch().getBranchName());
                summaryObject.addProperty("standard", feesTransactionSummary.getStandard().getStandardName());
                summaryObject.addProperty("academicYear", feesTransactionSummary.getAcademicYear().getYear());
//                summaryObject.addProperty("fromMonth",feesTransactionSummary.getAcademicYear());
                summaryObject.addProperty("division", feesTransactionSummary.getDivision().getDivisionName());
            }

            double d = 0;
            double branchTotal = 0;
            double hostelBranchTotal = 0;


            summaryObject.addProperty("Hostel", "");
            JsonArray jsonArray = new JsonArray();
            JsonArray hostelJsonArray = new JsonArray();


//            OLD List<FeesTransactionDetail> feesTransactionDetails = feesTransactionDetailRepository.findByFeesTransactionSummaryIdAndReceiptNo(transactionId, lastReceiptNo);
//            List<Object[]> headList = feesTransactionDetailRepository.getFeeHeadsList(transactionId, lastReceiptNo);
            /******** Get Hostel fee by get hostel receipt under heads amount ********/

            List<Object[]> headList = feesTransactionDetailRepository.getFeeHeadsListByTransactionIdAndReceipt(transactionId, lastReceiptNo);


            /***** For Branch Heads ********/
            for (int i = 0; i < headList.size(); i++) {
                Object[] headObj = headList.get(i);
                JsonObject jsonObject = new JsonObject();
                System.out.println("headObj[10]-->" + headObj[6]);
//                if (Boolean.valueOf(headObj[6].toString()) == true) {
                    jsonObject.addProperty("particular", headObj[9].toString());
                    jsonObject.addProperty("paidAmount", Precision.round(Double.parseDouble(headObj[2].toString()), 0));
                    jsonObject.addProperty("headFeeAmount", Precision.round(Double.parseDouble(headObj[1].toString()), 0));

//                    if (Integer.parseInt(headObj[4].toString()) != 0) {
//                        SubFeeHead subFeeHead = subFeeHeadRepository.findByIdAndStatus(Long.parseLong(headObj[4].toString()), true);
//                        jsonObject.addProperty("particular", subFeeHead.getSubFeeHeadName());
//                    }
                    jsonArray.add(jsonObject);

                    branchTotal = branchTotal + Double.parseDouble(headObj[2].toString());
//                }

                summaryObject.addProperty("receiptNo", headObj[5].toString());
                summaryObject.addProperty("paymentNo", headObj[7].toString());

                if (Integer.parseInt(headObj[6].toString()) == 0) {
                    summaryObject.addProperty("paymentMode", "CASH");
                } else if (Integer.parseInt(headObj[6].toString()) == 1) {
                    summaryObject.addProperty("paymentMode", "CHEQUE");
                } else if (Integer.parseInt(headObj[6].toString()) == 2) {
                    summaryObject.addProperty("paymentMode", "ONLINE");
                }
                summaryObject.addProperty("transactionDate", headObj[8].toString());
            }


            /******* For Hostel Heads ********/
//            for (int i = 0; i < headList.size(); i++) {
//                Object[] headObj = headList.get(i);
//                JsonObject jsonObject = new JsonObject();
//                if (Boolean.valueOf(headObj[10].toString()) == false) {
//                    jsonObject.addProperty("particular", headObj[9].toString());
//                    jsonObject.addProperty("paidAmount", Precision.round(Double.parseDouble(headObj[2].toString()), 0));
//                    jsonObject.addProperty("headFeeAmount", Precision.round(Double.parseDouble(headObj[1].toString()), 0));
//
//                    if (Integer.parseInt(headObj[4].toString()) != 0) {
//                        SubFeeHead subFeeHead = subFeeHeadRepository.findByIdAndStatus(Long.parseLong(headObj[4].toString()), true);
//                        jsonObject.addProperty("particular", subFeeHead.getSubFeeHeadName());
//                    }
//                    hostelJsonArray.add(jsonObject);
//
//                    hostelBranchTotal = hostelBranchTotal + Double.parseDouble(headObj[2].toString());
//                }
//            }

            int hostelFee = (int) Precision.round(hostelBranchTotal, 0);
            System.out.println("amountString " + hostelFee);
            String hostelFeeInword = NumberToWord(String.valueOf(hostelFee));
            System.out.println("hostelFeeInword " + hostelFeeInword);
            summaryObject.addProperty("HostelFeeinword", hostelFeeInword.toUpperCase());
            summaryObject.addProperty("hostelamount", hostelFee);

            summaryObject.addProperty("paidAmount", Precision.round(branchTotal, 0));
            int amount = (int) Precision.round(branchTotal, 0);
            System.out.println("amountString " + amount);
            String branchFeeInWord = NumberToWord(String.valueOf(amount));
            System.out.println("branchFeeInWord " + branchFeeInWord);
            summaryObject.addProperty("inword", branchFeeInWord.toUpperCase());


            summaryObject.addProperty("hostelBranch", "");
            if (hostelJsonArray.size() > 0 && hostelFee > 0) {
                Branch branch = branchRepository.getBranchRecord();
                if (branch != null) {
                    summaryObject.addProperty("hostelBranch", branch.getBranchName());
                }
            }


            result.add("responseObject", summaryObject);
            result.add("particularList", jsonArray);
            result.add("hostelParticularList", hostelJsonArray);
            result.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exception " + e.getMessage());
            result.addProperty("message", "Failed to load Data");
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return result;

    }

    public JsonObject getCollectionByDate(HttpServletRequest request) {
        JsonObject result = new JsonObject();
        JsonArray res = new JsonArray();
        Map<String, String[]> paramMap = request.getParameterMap();
        try {
            Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            System.out.println("OutletId :-->" + users.getOutlet().getId());
            System.out.println("BranchId :-->" + users.getBranch().getId());

            String fromDate = request.getParameter("fromDate");
            String toDate = request.getParameter("toDate");
            String academicyear = request.getParameter("academicyearId");
            String standard = request.getParameter("standardId");

            String studentType = request.getParameter("studentType");
            List<Object[]> list = null;

            if (standard.contains("all") && studentType.equalsIgnoreCase("3")) {

                list = feesTransactionDetailRepository.findallTransactionByDateWise(users.getOutlet().getId(), users.getBranch().getId(),
                        academicyear, fromDate, toDate);


            } else if (standard.contains("all") && !studentType.equalsIgnoreCase("3")) {

                list = feesTransactionDetailRepository.findallStandardOnlyByDateWise(users.getOutlet().getId(), users.getBranch().getId(),
                        academicyear, fromDate, toDate, studentType);
            } else if (!standard.contains("all") && studentType.equalsIgnoreCase("3")) {

                list = feesTransactionDetailRepository.findallStudentTypeOnlyByDateWise(users.getOutlet().getId(), users.getBranch().getId(),
                        academicyear, fromDate, toDate, standard);
            } else {
                if (!request.getParameter("fromDate").equalsIgnoreCase("") && !request.getParameter("toDate").equalsIgnoreCase("")) {
                    if (!request.getParameter("academicyearId").equalsIgnoreCase("") && !request.getParameter("standardId").equalsIgnoreCase("")) {
                        list = feesTransactionDetailRepository.findByTransactionDateWithYearAndStandard(users.getOutlet().getId(), users.getBranch().getId(), academicyear, standard, fromDate, toDate, studentType);
                    } else if (!request.getParameter("academicyearId").equalsIgnoreCase("")) {
                        list = feesTransactionDetailRepository.findByTransactionDatewithAcademicYear(users.getOutlet().getId(), users.getBranch().getId(), academicyear, fromDate, toDate, studentType);
                    } else if (!request.getParameter("standardId").equalsIgnoreCase("")) {
                        list = feesTransactionDetailRepository.findByTransactionDateAndStandard(users.getOutlet().getId(), users.getBranch().getId(), standard, fromDate, toDate, studentType);
                    } else {
                        list = feesTransactionDetailRepository.findByTransactionDate(users.getOutlet().getId(), users.getBranch().getId(), LocalDate.parse(fromDate), LocalDate.parse(toDate), studentType);
                    }
                } else if (!request.getParameter("academicyearId").equalsIgnoreCase("") && !request.getParameter("standardId").equalsIgnoreCase("")) {
                    System.out.println("users.getOutlet().getId(), users.getBranch().getId(), Long.valueOf(academicyear), Long.valueOf(standard), LocalDate.now() " + users.getOutlet().getId() + " ->" + users.getBranch().getId() + " ->" + Long.valueOf(academicyear) + " ->" + Long.valueOf(standard) + " ->" + LocalDate.now());
                    list = feesTransactionDetailRepository.findByTransactionDateWithYearAndStandardNow(
                            users.getOutlet().getId(), users.getBranch().getId(), Long.valueOf(academicyear), Long.valueOf(standard), LocalDate.now(), studentType);
                } else if (!request.getParameter("academicyearId").equalsIgnoreCase("")) {
                    list = feesTransactionDetailRepository.findByTransactionDatewithAcademicYearNow(users.getOutlet().getId(), users.getBranch().getId(), academicyear, LocalDate.now(), studentType);
                } else if (!request.getParameter("standardId").equalsIgnoreCase("")) {
                    list = feesTransactionDetailRepository.findByTransactionDateAndStandardNow(users.getOutlet().getId(), users.getBranch().getId(), standard, LocalDate.now(), studentType);
                } else {
                    list = feesTransactionDetailRepository.findByTransactionDateNow(users.getOutlet().getId(), users.getBranch().getId(), LocalDate.now(), studentType);
                }
            }
            System.out.println("list size : " + list.size());
            if (list != null && list.size() > 0) {
                double d = 0;
                for (int i = 0; i < list.size(); i++) {


                    Object[] feesObj = list.get(i);
                    StudentRegister studentRegister = studentRegisterRepository.findByIdAndStatus(Long.parseLong(feesObj[2].toString()), true);
                    StudentAdmission studentAdmission = studentAdmissionRepository.findByStudentRegisterIdAndStatus(Long.parseLong(feesObj[2].toString()), true);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("paidAmount", Precision.round(Double.parseDouble(feesObj[0].toString()), 0));

//                    jsonObject.addProperty("transactionId",);
                    jsonObject.addProperty("transactionDate", feesObj[1].toString());
                    jsonObject.addProperty("studentName", studentRegister.getFirstName() + " " + studentRegister.getLastName());
                    jsonObject.addProperty("firstName", studentRegister.getFirstName());
                    jsonObject.addProperty("fatherName", studentRegister.getFatherName());
                    jsonObject.addProperty("middleName", studentRegister.getMiddleName());
                    jsonObject.addProperty("lastName", studentRegister.getLastName());
                    jsonObject.addProperty("studentId", studentRegister.getStudentId());
                    jsonObject.addProperty("standard", studentAdmission.getStandard().getStandardName());
                    jsonObject.addProperty("receiptNo", feesObj[3].toString());
                    jsonObject.addProperty("transactionId", feesObj[7].toString());
                    //  jsonObject.addProperty("concession_amount", feesObj[8].toString());
                    res.add(jsonObject);
                }
            } else {
                result.addProperty("message", "Failed To Load Date");
                result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            result.add("response", res);
            result.addProperty("responseStatus", HttpStatus.OK.value());


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exception " + e.getMessage());
            result.addProperty("message", "Failed To Load Data");
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());

        }
        return result;

    }

    public JsonObject deleteFeesData(HttpServletRequest request) {
        Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        JsonObject result = new JsonObject();
        try {
            Long transactionId = Long.valueOf(request.getParameter("transactionId"));
            String receiptNo = request.getParameter("receiptNo");
            boolean isConcessionTranx = false;
            double concessionAmount = 0;
//            double paidAmount = Double.parseDouble(request.getParameter("paidAmount"));
//            FeesTransactionSummary feesTransactionSummary = transactionSummaryRepository.findByIdAndStatus(transactionId, true);
            List<TranxJournalMaster> tranxJournalMastersList = tranxJournalMasterRepository.findByFeeReceiptNo(receiptNo);
//            List<TranxJournalMaster> tranxJournalMastersList = tranxJournalMasterRepository.findTop1ByTotalAmtAndStatusOrderByIdDesc(paidAmount, true);

            TransactionTypeMaster tranxType = transactionTypeMasterRepository.findByTransactionCodeIgnoreCase("JRNL");
            for (TranxJournalMaster tranxJournalMaster : tranxJournalMastersList) {
                System.out.println("tranxJournalMaster.getId() " + tranxJournalMaster.getId());
                List<TranxJournalDetails> tranxJournalDetailsList = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndStatus(
                        tranxJournalMaster.getId(), users.getOutlet().getId(), true);
                for (TranxJournalDetails tranxJournalDetails : tranxJournalDetailsList) {

                    if (tranxJournalDetails.getLedgerMaster().getLedgerName().toLowerCase().contains("concession")) {
                        isConcessionTranx = true;
                        concessionAmount = tranxJournalDetails.getPaidAmount();
                    }

                    if (tranxJournalDetails.getType().equalsIgnoreCase("DR")) {
                        System.out.println("TRANX DET ID " + tranxJournalDetails.getId() + "LDID " + tranxJournalDetails.getLedgerMaster().getId() + " TRID " + tranxJournalDetails.getTranxJournalMaster().getId()
                                + " trdate " + tranxJournalDetails.getTranxJournalMaster().getTranscationDate() + " paidAmt " + tranxJournalDetails.getPaidAmount() + " DR >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        ledgerTransactionDetailsRepository.ledgerPostingEditTranxDate(
                                tranxJournalDetails.getLedgerMaster().getId(), tranxJournalDetails.getTranxJournalMaster().getId(), tranxType.getId(), "DR",
                                0.0, tranxJournalDetails.getTranxJournalMaster().getTranscationDate());
                    } else if (tranxJournalDetails.getType().equalsIgnoreCase("CR")) {
                        System.out.println("TRANX DET ID " + tranxJournalDetails.getId() + "LDID " + tranxJournalDetails.getLedgerMaster().getId() + " TRID " + tranxJournalDetails.getTranxJournalMaster().getId()
                                + " trdate " + tranxJournalDetails.getTranxJournalMaster().getTranscationDate() + " paidAmt " + tranxJournalDetails.getPaidAmount() + " CR >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        ledgerTransactionDetailsRepository.ledgerPostingEditTranxDate(
                                tranxJournalDetails.getLedgerMaster().getId(), tranxJournalDetails.getTranxJournalMaster().getId(), tranxType.getId(), "CR",
                                0.0, tranxJournalDetails.getTranxJournalMaster().getTranscationDate());
                    }

                    JournalDetailsHistory journalDetailsHistory = new JournalDetailsHistory();
                    journalDetailsHistory.setTranxJournalDetailsId(tranxJournalDetails.getId());
                    journalDetailsHistory.setOutletId(tranxJournalDetails.getOutlet().getId());
                    journalDetailsHistory.setBranchId(tranxJournalDetails.getBranch().getId());
                    journalDetailsHistory.setStatus(true);
                    journalDetailsHistory.setPaidAmount(tranxJournalDetails.getPaidAmount());
                    journalDetailsHistory.setType(tranxJournalDetails.getType());
                    journalDetailsHistory.setCreatedBy(users.getId());
                    journalDetailsHistory.setJournalMasterId(tranxJournalDetails.getTranxJournalMaster().getId());
                    journalDetailsHistory.setLedgerType(tranxJournalDetails.getLedgerType());
                    journalDetailsHistory.setOperationType("delete");
                    journalDetailsHistoryRepository.save(journalDetailsHistory);


                    tranxJournalDetails.setStatus(false);
                    tranxJournalDetailsRepository.save(tranxJournalDetails);
                }
                ledgerTransactionDetailsRepository.updateToStatusZeroTranxLedgerDetailByJVIdAndTranxType(tranxJournalMaster.getId(), 10L);
                TranxJournalMaster tranxJournalMaster1 = tranxJournalMasterRepository.findByIdAndStatus(tranxJournalMaster.getId(), true);

                if (tranxJournalMaster1 != null) {
                    JournalMasterHistory journalMasterHistory = new JournalMasterHistory();
                    journalMasterHistory.setTranxJournalMasterId(tranxJournalMaster1.getId());
                    journalMasterHistory.setOperationType("delete");
                    journalMasterHistory.setFeeReceiptNo(tranxJournalMaster1.getFeeReceiptNo());
                    journalMasterHistory.setNarrations(tranxJournalMaster.getNarrations());
                    journalMasterHistory.setTranscationDate(tranxJournalMaster.getTranscationDate());
                    journalMasterHistory.setTotalAmt(tranxJournalMaster1.getTotalAmt());
                    journalMasterHistory.setStatus(true);
                    journalMasterHistory.setCreatedBy(users.getId());
                    journalMasterHistory.setJournalSrNo(tranxJournalMaster1.getJournalSrNo());
                    journalMasterHistory.setJournalNo(tranxJournalMaster1.getJournalNo());
                    journalMasterHistory.setFinancialYear(tranxJournalMaster1.getFinancialYear());
                    if (tranxJournalMaster1.getFiscalYear() != null) {
                        journalMasterHistory.setFiscalYearId(tranxJournalMaster1.getFiscalYear().getId());
                    }
                    journalMasterHistory.setFeeReceiptNo(tranxJournalMaster.getFeeReceiptNo());
                    journalMasterHistory.setFinancialYear(tranxJournalMaster.getFinancialYear());
                    journalMasterHistory.setTranscationDate(tranxJournalMaster1.getTranscationDate());
                    journalMasterHistoryRepository.save(journalMasterHistory);

                }
                tranxJournalMaster.setStatus(false);
                tranxJournalMasterRepository.save(tranxJournalMaster);


            }

            /************* Application Level Start*****************/
            double totalpAmt = 0;
//            double concessionAmt = 0;
            List<FeesTransactionDetail> list = feesTransactionDetailRepository.findByFeesTransactionSummaryIdAndReceiptNo(transactionId, receiptNo);
            for (FeesTransactionDetail feesTransactionDetail : list) {
                try {
                    totalpAmt = totalpAmt + feesTransactionDetail.getPaidAmount();
                    System.out.println("totalpAmt " + (totalpAmt));
                    Long tId = feesTransactionDetail.getId();
                    feesTransactionDetailRepository.DeleteColumnFeesTransaction(tId);

                } catch (Exception e) {
                    result.addProperty("message", "Failed to delete fees details!");
                    result.addProperty("responseStatus", HttpStatus.OK.value());
                    return result;
                }
            }
            System.out.println("totalpAmt " + (totalpAmt));
            FeesTransactionSummary tranxsummary = transactionSummaryRepository.findByIdAndStatus(transactionId, true);
            double prevpaid = tranxsummary.getPaidAmount();
            double prevbalance = tranxsummary.getBalance();
            System.out.println("paid " + (prevpaid - totalpAmt));
            System.out.println("balance " + (prevbalance + prevpaid));

            double netpaid = prevpaid - totalpAmt;
            double netbalance = prevbalance + totalpAmt;
            System.out.println("netpaid " + netpaid);
            System.out.println("netbalance " + netbalance);

            System.out.println(":isConcessionTranx ->" + isConcessionTranx);
            System.out.println(":concessionAmount ->" + concessionAmount);

            if (netpaid == 0) {
                tranxsummary.setConcessionType(null);
                tranxsummary.setConcessionAmount(null);
                tranxsummary.setIsManual(null);
            } else {


                /*if (tranxJournalMastersList.size() == 0 && tranxsummary.getConcessionType() == null || tranxsummary.getConcessionType() != 0) {
                    System.out.println(" ****************************************** executed appl condition ");
                    List<InstallmentMaster> installmentMasters = installmentMasterRepository.getListForConcessionCheck(tranxsummary.getFeesMaster().getId(), 1);
                    for (InstallmentMaster installmentMaster : installmentMasters) {
                        if (prevpaid >= installmentMaster.getFeesAmount()) {
                            System.out.println("prevpaid =>" + prevpaid + " installmentMaster.getFeesAmount()=>" + installmentMaster.getFeesAmount());
                            if (tranxsummary.getIsManual()) {
                                if (tranxsummary.getConcessionAmount() != null)
                                    concessionAmount = tranxsummary.getConcessionAmount();
                                tranxsummary.setConcessionType(0);
                                tranxsummary.setConcessionAmount(null);
                            } else {
                                tranxsummary.setConcessionType(null);
                                tranxsummary.setConcessionAmount(null);
                            }
                        }
                    }
                }*/
                if (tranxsummary.getConcessionType() != null && tranxsummary.getConcessionType() != 0 && isConcessionTranx == true) {
                    System.out.println(" ****************************************** executed accounting condition ");
                    tranxsummary.setConcessionAmount(0);
                    tranxsummary.setConcessionType(0);
                }
            }

            netbalance = netbalance + concessionAmount;
            tranxsummary.setPaidAmount(netpaid);
            tranxsummary.setBalance(netbalance);
            transactionSummaryRepository.save(tranxsummary);
            /************* Application Level End*****************/

            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.addProperty("message", "Fee Record Deleted SuccessFully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception" + e.getMessage());
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.addProperty("message", "Failed to Delete Data");
        }

        return result;
    }


    public Object getStudentPaidReceipts(HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();
        try {
            Long studentId = Long.valueOf(request.getParameter("studentId"));
            FeesTransactionSummary feesTransactionSummary = transactionSummaryRepository.findByStudentRegisterId(studentId);
            JsonArray receiptArray = new JsonArray();
            List<Object[]> receiptRows = feesTransactionDetailRepository.getReceiptRowsByFeesTransactionSummaryId(feesTransactionSummary.getId());
            for (int i = 0; i < receiptRows.size(); i++) {
                Object[] obj = receiptRows.get(i);

                JsonObject receiptObj = new JsonObject();
                receiptObj.addProperty("receiptNo", obj[0].toString());
                receiptObj.addProperty("transactionDate", obj[1].toString());
                receiptObj.addProperty("paidAmount", obj[2].toString());

                receiptArray.add(receiptObj);
            }

            jsonObject.add("response", receiptArray);
            jsonObject.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());
            transactionLogger.error("Exception -> getStudentPaidReceipts " + e);

            jsonObject.addProperty("message", "Failed to get data");
            jsonObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return jsonObject;
    }

    public Object cancelStudentAdmission(HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();
        Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Long studentId = Long.valueOf(request.getParameter("studentId"));
        StudentRegister studentRegister = studentRegisterRepository.findByIdAndStatus(studentId, true);
        StudentAdmission studentAdmission = studentAdmissionRepository.findTop1ByStudentRegisterIdAndStatusOrderByIdDesc(studentId, true);
        if (studentAdmission != null) {
            FeesTransactionSummary feesTransactionSummary = transactionSummaryRepository.findTop1ByStudentRegisterIdAndStatusOrderByIdDesc(studentId, true);

            double refundAmount = Double.parseDouble(request.getParameter("refundAmount"));
            double paidAmount = feesTransactionSummary.getPaidAmount();
            double balance = feesTransactionSummary.getBalance();

            studentAdmission.setReason(request.getParameter("reason"));
            studentAdmission.setStatus(false);
            studentAdmission.setPaidAmount(paidAmount);
            studentAdmission.setOutstanding(balance);

            double remainingPaidAmt = paidAmount - refundAmount;
            double branchAmount = 0;
            double hostelAmount = 0;
            List<Object[]> paidHeadsList = feesTransactionDetailRepository.getPaidHeadsListByTransactionId(feesTransactionSummary.getId());
            for (int i = 0; i < paidHeadsList.size(); i++) {
                Object[] headObj = paidHeadsList.get(i);

                Double headPaidAmount = Double.valueOf(headObj[0].toString());
                String headName = headObj[2].toString();

                if (headName.toLowerCase().contains("hostel")) {
                    System.out.println("hostel fees paid" + headPaidAmount);
                    hostelAmount = headPaidAmount;
                } else {
                    branchAmount = branchAmount + headPaidAmount;
                }
            }
            createSalesReturnInvoiceForStudent(users, studentRegister, branchAmount);

            if (hostelAmount > 0) {
                Branch hostelBranch = branchRepository.getHostelBranchRecord(users.getOutlet().getId(), true);
                if (hostelBranch != null) {
                    LedgerMaster hostelStudentLedger = ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(
                            studentRegister.getId(), users.getOutlet().getId(), hostelBranch.getId(), true);
                    if (hostelStudentLedger == null) {
                        System.out.println("ledger not created >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        jsonObject.addProperty("message", "Failed to create ledger in hostel, please delete & add once again");
                        jsonObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
                        return jsonObject;
                    }
                    createSalesReturnInvoiceForHostel(users, studentRegister, hostelBranch, hostelStudentLedger, hostelAmount);
                } else {
                    System.out.println("Hostel branch not exists");
                }
            }

            /**** Journal Voucher For Central Branch  *****/
            if (remainingPaidAmt > 0) {
                TransactionTypeMaster tranxType = transactionTypeMasterRepository.findByTransactionCodeIgnoreCase("JRNL");
                LedgerMaster mStudent = ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(
                        studentRegister.getId(), users.getOutlet().getId(), users.getBranch().getId(), true);

                TranxJournalMaster journalMaster = new TranxJournalMaster();
                Branch branch = null;
                if (users.getBranch() != null) branch = users.getBranch();
                Outlet outlet = users.getOutlet();
                Long count = tranxJournalMasterRepository.findLastRecord(outlet.getId(), branch.getId());
                String serailNo = String.format("%05d", count + 1);// 5 digit serial number
                GenerateDates generateDates = new GenerateDates();
                String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
                String jtCode = "JRNL" + currentMonth + serailNo;
                journalMaster.setOutlet(outlet);
                journalMaster.setBranch(branch);
                journalMaster.setTranscationDate(LocalDate.now());
                journalMaster.setJournalSrNo(count + 1);
                journalMaster.setJournalNo(jtCode);
//                        journalMaster.setTotalAmt(currentPaidAmount);
                journalMaster.setTotalAmt(remainingPaidAmt);
                journalMaster.setNarrations("Journal entry from student to Indirect Acc:" + studentRegister.getId());
                journalMaster.setCreatedBy(users.getId());
                journalMaster.setStatus(true);
                /*fiscal year mapping */
                FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(LocalDate.now());
                if (fiscalYear != null) {
                    journalMaster.setFiscalYear(fiscalYear);
                    journalMaster.setFinancialYear(fiscalYear.getFiscalYear());
                }
                TranxJournalMaster tranxJournalMaster = tranxJournalMasterRepository.save(journalMaster);
                try {
                    /**** Tranx Journal Details for Student A/C *****/
                    TranxJournalDetails tranxJournalDetails1 = new TranxJournalDetails();
                    tranxJournalDetails1.setBranch(branch);
                    tranxJournalDetails1.setOutlet(outlet);
                    tranxJournalDetails1.setStatus(true);
                    tranxJournalDetails1.setLedgerMaster(mStudent);
                    tranxJournalDetails1.setTranxJournalMaster(tranxJournalMaster);
                    tranxJournalDetails1.setType("CR");
                    tranxJournalDetails1.setLedgerType(mStudent.getSlugName());
                    tranxJournalDetails1.setCreatedBy(users.getId());
                    tranxJournalDetails1.setPaidAmount(remainingPaidAmt);
                    tranxJournalDetails1.setStatus(true);
                    TranxJournalDetails journalDetails = tranxJournalDetailsRepository.save(tranxJournalDetails1);
                    insertIntoSDJournal(journalDetails, tranxType); //Accounting postings of Student Account
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    /**** Tranx Journal Details for DP A/C *****/
                    TranxJournalDetails tranxJournalDetails2 = new TranxJournalDetails();
                    LedgerMaster DpLedgerMaster = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletIdAndBranchIdAndStatus("DP A/C", outlet.getId(), branch.getId(), true);
                    tranxJournalDetails2.setBranch(branch);
                    tranxJournalDetails2.setOutlet(outlet);
                    tranxJournalDetails2.setStatus(true);
                    tranxJournalDetails2.setLedgerMaster(DpLedgerMaster);
                    tranxJournalDetails2.setTranxJournalMaster(tranxJournalMaster);
                    tranxJournalDetails2.setType("DR");
                    tranxJournalDetails2.setLedgerType(DpLedgerMaster.getSlugName());
                    tranxJournalDetails2.setCreatedBy(users.getId());
                    tranxJournalDetails2.setPaidAmount(remainingPaidAmt);
                    tranxJournalDetails2.setStatus(true);
                    TranxJournalDetails journalDetails = tranxJournalDetailsRepository.save(tranxJournalDetails2);
                    insertIntoDPJournal(journalDetails, tranxType);//Accounting postings of DP Account

                } catch (Exception e) {
                    e.printStackTrace();
                            /*journalLogger.error("Error in createJournal :->" + e.getMessage());
                            response.addProperty("message", "Error in Contra creation");
                            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());*/
                }
            }

        }
        return jsonObject;
    }

    private void createSalesReturnInvoiceForHostel(Users users, StudentRegister studentRegister, Branch hostelBranch, LedgerMaster hostelStudentLedger, double hostelAmount) {
        TranxSalesReturnInvoice tranxSalesReturnInvoice = new TranxSalesReturnInvoice();
        tranxSalesReturnInvoice.setBranch(hostelBranch);
        tranxSalesReturnInvoice.setOutlet(users.getOutlet());
        tranxSalesReturnInvoice.setSundryDebtors(hostelStudentLedger);

        LocalDate date = LocalDate.now();
        tranxSalesReturnInvoice.setTransactionDate(date);
        /* fiscal year mapping */
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(date);
        if (fiscalYear != null) {
            tranxSalesReturnInvoice.setFiscalYear(fiscalYear);
            tranxSalesReturnInvoice.setFinancialYear(fiscalYear.getFiscalYear());
        }
        /* End of fiscal year mapping */


        Long count = tranxSalesReturnRepository.findLastRecord(users.getOutlet().getId());
        String serailNo = String.format("%05d", count + 1);
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
        String siCode = "SI" + currentMonth + serailNo;
        tranxSalesReturnInvoice.setSalesRtnSrNo(Long.parseLong(serailNo));
        tranxSalesReturnInvoice.setSalesReturnNo(siCode);
//        LedgerMaster feesAc = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletIdAndBranchIdAndStatus(
//                "Fees A/c", users.getOutlet().getId(), hostelBranch.getId(), true);
        AssociateGroups feesAc = associateGroupsRepository.findByAssociatesNameIgnoreCaseAndOutletIdAndBranchIdAndStatus
                ("Fees Account", users.getOutlet().getId(), hostelBranch.getId(), true);
        tranxSalesReturnInvoice.setFeesAct(feesAc);
        tranxSalesReturnInvoice.setTotalBaseAmount(hostelAmount);
        tranxSalesReturnInvoice.setTotalAmount(hostelAmount);
        tranxSalesReturnInvoice.setCreatedBy(users.getId());
        tranxSalesReturnInvoice.setStatus(true);
        tranxSalesReturnInvoice.setOperations("inserted");
        try {
            TranxSalesReturnInvoice mSalesInvoice = tranxSalesReturnRepository.save(tranxSalesReturnInvoice);
            /** Accounting Postings  **/
            if (mSalesInvoice != null) {
                TransactionTypeMaster tranxType = transactionTypeMasterRepository.findByTransactionCodeIgnoreCase("SLSRT");
                insertIntoTranxDetailSD(mSalesInvoice, tranxType); //for Sundry Debtors or Students : dr
                insertIntoTranxDetailFAForHostel(studentRegister, mSalesInvoice, tranxType); // for Fees Accounts : cr
            }

        } catch (Exception e) {
            transactionLogger.error("Error in createSalesReturnInvoiceForStudent-> " + e.getMessage());
        }

    }

    private void insertIntoTranxDetailFAForHostel(StudentRegister studentRegister, TranxSalesReturnInvoice mSalesTranx, TransactionTypeMaster tranxType) {
        FeesTransactionSummary feesTransactionSummary = transactionSummaryRepository.findTop1ByStudentRegisterIdAndStatusOrderByIdDesc(studentRegister.getId(), true);
        List<Object[]> paidHeadsList = feesTransactionDetailRepository.getPaidHeadsListByTransactionId(feesTransactionSummary.getId());
        for (int i = 0; i < paidHeadsList.size(); i++) {
            Object[] headObj = paidHeadsList.get(i);

            Double headPaidAmount = Double.valueOf(headObj[0].toString());
            String headName = headObj[2].toString();
            Long feeHeadId = Long.valueOf(headObj[1].toString());

            if (headName.toLowerCase().contains("hostel")) {

                FeeHead feeHead = feeHeadRepository.findByIdAndStatus(feeHeadId, true);
//                ledgerTransactionDetailsRepository.insertIntoLegerTranxDetail(
                ledgerTransactionDetailsRepository.insertIntoLegerTranxDetailWithTranxDate(
                        mSalesTranx.getFeesAccount().getFoundations().getId(),
                        mSalesTranx.getFeesAccount().getPrinciples().getId(),
                        mSalesTranx.getFeesAccount().getPrincipleGroups() != null ? mSalesTranx.getFeesAccount().getPrincipleGroups().getId() : null,
                        null, tranxType.getId(), null,
                        mSalesTranx.getBranch() != null ? mSalesTranx.getBranch().getId() : null,
                        mSalesTranx.getOutlet().getId(), "pending", headPaidAmount * -1, 0.0,
                        mSalesTranx.getTransactionDate() != null ? mSalesTranx.getTransactionDate() : null, null,
                        mSalesTranx.getId(), tranxType.getTransactionName() != null ? tranxType.getTransactionName() : null,
                        mSalesTranx.getFeesAccount() != null ? mSalesTranx.getFeesAccount().getUnderPrefix() : null,
                        mSalesTranx.getFinancialYear() != null ? mSalesTranx.getFinancialYear() : null, mSalesTranx.getCreatedBy(),
                        feeHead.getLedger().getId(), null, true);
            } else {
            }
        }
    }

    private void createSalesReturnInvoiceForStudent(Users users, StudentRegister studentRegister, double branchAmount) {

        LedgerMaster studentLedger = ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(
                studentRegister.getId(), users.getOutlet().getId(), users.getBranch().getId(), true);

        TranxSalesReturnInvoice tranxSalesReturnInvoice = new TranxSalesReturnInvoice();
        tranxSalesReturnInvoice.setBranch(users.getBranch());
        tranxSalesReturnInvoice.setOutlet(users.getOutlet());
        tranxSalesReturnInvoice.setSundryDebtors(studentLedger);

        LocalDate date = LocalDate.now();
        tranxSalesReturnInvoice.setTransactionDate(date);
        /* fiscal year mapping */
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(date);
        if (fiscalYear != null) {
            tranxSalesReturnInvoice.setFiscalYear(fiscalYear);
            tranxSalesReturnInvoice.setFinancialYear(fiscalYear.getFiscalYear());
        }
        /* End of fiscal year mapping */


        Long count = tranxSalesReturnRepository.findLastRecord(users.getOutlet().getId());
        String serailNo = String.format("%05d", count + 1);
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
        String siCode = "SI" + currentMonth + serailNo;
        tranxSalesReturnInvoice.setSalesRtnSrNo(Long.parseLong(serailNo));
        tranxSalesReturnInvoice.setSalesReturnNo(siCode);
//        LedgerMaster feesAc = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletIdAndBranchIdAndStatus(
//                "Fees A/c", users.getOutlet().getId(), studentRegister.getBranch().getId(), true);
        AssociateGroups feesAc = associateGroupsRepository.findByAssociatesNameIgnoreCaseAndOutletIdAndBranchIdAndStatus
                ("Fees Account", users.getOutlet().getId(), studentRegister.getBranch().getId(), true);
        tranxSalesReturnInvoice.setFeesAct(feesAc);
        tranxSalesReturnInvoice.setTotalBaseAmount(branchAmount);
        tranxSalesReturnInvoice.setTotalAmount(branchAmount);
        tranxSalesReturnInvoice.setCreatedBy(users.getId());
        tranxSalesReturnInvoice.setStatus(true);
        tranxSalesReturnInvoice.setOperations("inserted");
        try {
            TranxSalesReturnInvoice mSalesInvoice = tranxSalesReturnRepository.save(tranxSalesReturnInvoice);
            /** Accounting Postings  **/
            if (mSalesInvoice != null) {
                TransactionTypeMaster tranxType = transactionTypeMasterRepository.findByTransactionCodeIgnoreCase("SLSRT");
                insertIntoTranxDetailSD(mSalesInvoice, tranxType); //for Sundry Debtors or Students : dr
                insertIntoTranxDetailFA(studentRegister, mSalesInvoice, tranxType); // for Fees Accounts : cr
            }

        } catch (Exception e) {
            transactionLogger.error("Error in createSalesReturnInvoiceForStudent-> " + e.getMessage());
        }
    }

    private void insertIntoTranxDetailFA(StudentRegister studentRegister, TranxSalesReturnInvoice mSalesTranx, TransactionTypeMaster tranxType) {
        FeesTransactionSummary feesTransactionSummary = transactionSummaryRepository.findTop1ByStudentRegisterIdAndStatusOrderByIdDesc(studentRegister.getId(), true);
        List<Object[]> paidHeadsList = feesTransactionDetailRepository.getPaidHeadsListByTransactionId(feesTransactionSummary.getId());
        for (int i = 0; i < paidHeadsList.size(); i++) {
            Object[] headObj = paidHeadsList.get(i);

            Double headPaidAmount = Double.valueOf(headObj[0].toString());
            String headName = headObj[2].toString();
            Long feeHeadId = Long.valueOf(headObj[1].toString());

            if (headName.toLowerCase().contains("hostel")) {

            } else {
//                 Code need to develop

                FeeHead feeHead = feeHeadRepository.findByIdAndStatus(feeHeadId, true);
//                ledgerTransactionDetailsRepository.insertIntoLegerTranxDetail(
                ledgerTransactionDetailsRepository.insertIntoLegerTranxDetailWithTranxDate(
                        mSalesTranx.getFeesAccount().getFoundations().getId(),
                        mSalesTranx.getFeesAccount().getPrinciples().getId(),
                        mSalesTranx.getFeesAccount().getPrincipleGroups() != null ? mSalesTranx.getFeesAccount().getPrincipleGroups().getId() : null,
                        null, tranxType.getId(), null,
                        mSalesTranx.getBranch() != null ? mSalesTranx.getBranch().getId() : null,
                        mSalesTranx.getOutlet().getId(), "pending", headPaidAmount * -1, 0.0,
                        mSalesTranx.getTransactionDate() != null ? mSalesTranx.getTransactionDate() : null, null,
                        mSalesTranx.getId(), tranxType.getTransactionName() != null ? tranxType.getTransactionName() : null,
                        mSalesTranx.getFeesAccount() != null ? mSalesTranx.getFeesAccount().getUnderPrefix() : null,
                        mSalesTranx.getFinancialYear() != null ? mSalesTranx.getFinancialYear() : null, mSalesTranx.getCreatedBy(),
                        feeHead.getLedger().getId(), null, true);
            }
        }
    }

    //posting to sundry Debtors
    private void insertIntoTranxDetailSD(TranxSalesReturnInvoice mSalesTranx, TransactionTypeMaster tranxType) {
        try {
//            ledgerTransactionDetailsRepository.insertIntoLegerTranxDetail(
            ledgerTransactionDetailsRepository.insertIntoLegerTranxDetailWithTranxDate(
                    mSalesTranx.getSundryDebtors().getFoundations().getId(),
                    mSalesTranx.getSundryDebtors().getPrinciples().getId(),
                    mSalesTranx.getSundryDebtors().getPrincipleGroups().getId(),
                    null, tranxType.getId(), null,
                    mSalesTranx.getBranch() != null ? mSalesTranx.getBranch().getId() : null,
                    mSalesTranx.getOutlet().getId(), "pending", 0.0, mSalesTranx.getTotalAmount(),
                    mSalesTranx.getTransactionDate() != null ? mSalesTranx.getTransactionDate() : null,
                    null, mSalesTranx.getId(),
                    tranxType.getTransactionName() != null ? tranxType.getTransactionName() : null,
                    mSalesTranx.getSundryDebtors() != null ? mSalesTranx.getSundryDebtors().getUnderPrefix() : null,
                    mSalesTranx.getFinancialYear() != null ? mSalesTranx.getFinancialYear() : null,
                    mSalesTranx.getCreatedBy(),
                    mSalesTranx.getSundryDebtors() != null ? mSalesTranx.getSundryDebtors().getId() : null,
                    null, true);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Store Procedure Error " + e.getMessage());
        }
    }

    public JsonObject getDataforDashboard(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        JsonObject result = new JsonObject();
        JsonArray incomeexp = new JsonArray();
        Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        try {
            LocalDate currentDate = LocalDate.now();
            Double totalCollection = feesTransactionDetailRepository.CalculatePaidAmount();
            res.addProperty("totalCollection", totalCollection);
            Double totalDayCollection = feesTransactionDetailRepository.GetDailyCollection(currentDate);
            res.addProperty("totalDayCollection", totalDayCollection);
            Long totalRegisterdStudent = studentRegisterRepository.TotalCountOfRegisteredStudent();
            res.addProperty("registeredStudent", totalRegisterdStudent);
            Long totalAdmission = studentAdmissionRepository.TotalCountOfAdmission();
            res.addProperty("totalAdmission", totalAdmission);
            Long totalDayAdmission = studentAdmissionRepository.TotalDayAdmission(currentDate);
            res.addProperty("totalDayAdmission", totalDayAdmission);
            incomeexp.add(totalCollection);
            incomeexp.add(0);
            Calendar now = Calendar.getInstance();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            JsonArray jsonArray = new JsonArray();
            String[] days = new String[7];
            int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 1; //add 2 if your week start on monday
            now.add(Calendar.DAY_OF_MONTH, delta);
            for (int i = 0; i < 7; i++) {
                days[i] = format.format(now.getTime());
                now.add(Calendar.DAY_OF_MONTH, 1);
//                FeesTransactionDetail feesTransactionDetail1=feesTransactionDetailRepository.getCollectionByWeek(days,true);

            }

            System.out.println(Arrays.toString(days));
            for (int j = 0; j < days.length; j++) {
                JsonObject js = new JsonObject();
                System.out.println("Before format " + days[j]);
//                SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
//                String strDate=formatter.format(days[j]);
//                System.out.println("after Format "+strDate);

                String totalPaidAmt = feesTransactionDetailRepository.getCollectionByWeek(days[j]);

                js.addProperty("totalPaidAmount", totalPaidAmt);
                System.out.println(totalPaidAmt);
                jsonArray.add(totalPaidAmt);
            }
            result.add("incomeexp", incomeexp);
            result.add("WeekCollection", jsonArray);
            result.addProperty("message", "Success");
            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.add("responseObject", res);


        } catch (Exception e) {
            e.printStackTrace();
            result.addProperty("message", "Data Not Found !");
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return result;

    }

    public JsonObject deleteJournalMaster(HttpServletRequest request) {
        Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        JsonObject result = new JsonObject();
        try {
            Long jvMasterId = Long.valueOf(request.getParameter("jvMasterId"));

            TranxJournalMaster tranxJournalMaster = tranxJournalMasterRepository.findByIdAndStatus(jvMasterId, true);
            TransactionTypeMaster tranxType = transactionTypeMasterRepository.findByTransactionCodeIgnoreCase("JRNL");
            if (tranxJournalMaster != null) {
                System.out.println("tranxJournalMaster.getId() " + tranxJournalMaster.getId());
                List<TranxJournalDetails> tranxJournalDetailsList = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndStatus(
                        tranxJournalMaster.getId(), users.getOutlet().getId(), true);
                for (TranxJournalDetails tranxJournalDetails : tranxJournalDetailsList) {
                    if (tranxJournalDetails.getType().equalsIgnoreCase("DR")) {
                        System.out.println("TRANX DET ID " + tranxJournalDetails.getId() + " LDID " + tranxJournalDetails.getLedgerMaster().getId() + " TRID " + tranxJournalDetails.getTranxJournalMaster().getId()
                                + " trdate " + tranxJournalDetails.getTranxJournalMaster().getTranscationDate() + " paidAmt " + tranxJournalDetails.getPaidAmount() + " DR >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        try {
                            ledgerTransactionDetailsRepository.ledgerPostingEditTranxDate(
                                    tranxJournalDetails.getLedgerMaster().getId(), tranxJournalDetails.getTranxJournalMaster().getId(), tranxType.getId(), "DR",
                                    0.0, tranxJournalDetails.getTranxJournalMaster().getTranscationDate());
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Exception " + e.getMessage());
                        }
                    } else if (tranxJournalDetails.getType().equalsIgnoreCase("CR")) {
                        System.out.println("TRANX DET ID " + tranxJournalDetails.getId() + " LDID " + tranxJournalDetails.getLedgerMaster().getId() + " TRID " + tranxJournalDetails.getTranxJournalMaster().getId()
                                + " trdate " + tranxJournalDetails.getTranxJournalMaster().getTranscationDate() + " paidAmt " + tranxJournalDetails.getPaidAmount() + " CR >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        try {
                            ledgerTransactionDetailsRepository.ledgerPostingEditTranxDate(
                                    tranxJournalDetails.getLedgerMaster().getId(), tranxJournalDetails.getTranxJournalMaster().getId(), tranxType.getId(), "CR",
                                    0.0, tranxJournalDetails.getTranxJournalMaster().getTranscationDate());
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Exception " + e.getMessage());
                        }
                    }

                    tranxJournalDetails.setStatus(false);
                    tranxJournalDetailsRepository.save(tranxJournalDetails);
                }
                ledgerTransactionDetailsRepository.updateToStatusZeroTranxLedgerDetailByJVIdAndTranxType(tranxJournalMaster.getId(), 10L);

                tranxJournalMaster.setStatus(false);
                tranxJournalMasterRepository.save(tranxJournalMaster);
            }
            result.addProperty("message", "Journal Deleted SuccessFully!");
            result.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception" + e.getMessage());
            result.addProperty("message", "Failed to Delete Data");
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

    public JsonObject updateLedgerAccounting(HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();

        try {
            Long ledgerId = Long.valueOf(request.getParameter("ledgerId"));

            TransactionTypeMaster tranxType = transactionTypeMasterRepository.findByTransactionCodeIgnoreCase("JRNL");
            List<LedgerTransactionDetails> ledgerTransactionDetailsList = ledgerTransactionDetailsRepository.getLedgerDataInTranxDateAscWise(ledgerId, 10, true);

            boolean isFirst = true;
            LocalDate lastTranxDate = null;
            LocalDate tranxDate = null;
            LedgerTransactionDetails lastLedgerTransactionDetails = null;
            TranxSalesInvoice tranxSalesInvoice = null;
            double totalDeductAmount = 0;
            if (ledgerTransactionDetailsList.size() > 0) {

                lastTranxDate = ledgerTransactionDetailsList.get(0).getTransactionDate();
                System.out.println("first time lastTranxDate " + lastTranxDate);
                for (LedgerTransactionDetails ledgerTransactionDetails : ledgerTransactionDetailsList) {
                    tranxDate = ledgerTransactionDetails.getTransactionDate();

                    ledgerTransactionDetails.setStatus(false);
                    ledgerTransactionDetailsRepository.save(ledgerTransactionDetails);

                    System.out.println("ledgerTransactionDetails id:" + ledgerTransactionDetails.getId() + " <<<<<<<<<<<<<<<<<< isFirst >>>>>>>>>>>>>" + isFirst);

                    System.out.println(" OP-> " + ledgerTransactionDetails.getOpeningBal() + " DR-> " + ledgerTransactionDetails.getDebit() + " CR-> "
                            + ledgerTransactionDetails.getCredit() + " CL-> " + ledgerTransactionDetails.getClosingBal());

                    if (isFirst == true) {
                        LedgerTransactionDetails ledgerTransactionDetails1 = new LedgerTransactionDetails();
                        ledgerTransactionDetails1.setDebit(ledgerTransactionDetails.getDebit());
                        ledgerTransactionDetails1.setCredit(0.0);
                        ledgerTransactionDetails1.setOpeningBal(0.0);
                        ledgerTransactionDetails1.setClosingBal(ledgerTransactionDetails.getDebit());
                        ledgerTransactionDetails1.setTransactionDate(ledgerTransactionDetails.getTransactionDate());
                        ledgerTransactionDetails1.setTransactionId(ledgerTransactionDetails.getTransactionId());
                        ledgerTransactionDetails1.setStatus(true);
                        ledgerTransactionDetails1.setFoundations(ledgerTransactionDetails.getFoundations());
                        ledgerTransactionDetails1.setPrinciples(ledgerTransactionDetails.getPrinciples());
                        ledgerTransactionDetails1.setPrincipleGroups(ledgerTransactionDetails.getPrincipleGroups());
                        ledgerTransactionDetails1.setLedgerMaster(ledgerTransactionDetails.getLedgerMaster());
                        ledgerTransactionDetails1.setTransactionType(ledgerTransactionDetails.getTransactionType());
                        ledgerTransactionDetails1.setBalanceMethod(ledgerTransactionDetails.getBalanceMethod());
                        ledgerTransactionDetails1.setAssociateGroups(ledgerTransactionDetails.getAssociateGroups());
                        ledgerTransactionDetails1.setBranch(ledgerTransactionDetails.getBranch());
                        ledgerTransactionDetails1.setOutlet(ledgerTransactionDetails.getOutlet());
                        ledgerTransactionDetails1.setPaymentStatus(ledgerTransactionDetails.getPaymentStatus());
                        ledgerTransactionDetails1.setPaymentDate(ledgerTransactionDetails.getPaymentDate());
                        ledgerTransactionDetails1.setTransactionType(ledgerTransactionDetails.getTransactionType());
                        ledgerTransactionDetails1.setFinancialYear(ledgerTransactionDetails.getFinancialYear());
                        ledgerTransactionDetails1.setUnderPrefix(ledgerTransactionDetails.getUnderPrefix());
                        ledgerTransactionDetails1.setCreatedAt(LocalDateTime.now());

                        ledgerTransactionDetailsRepository.save(ledgerTransactionDetails1);


                        LedgerBalanceSummary ledgerBalanceSummary = ledgerBalanceSummaryRepository.findByLedgerMasterIdAndStatus(ledgerId, true);
                        ledgerBalanceSummary.setBalance(ledgerTransactionDetails.getDebit());
                        ledgerBalanceSummary.setClosingBal(ledgerTransactionDetails.getDebit());
                        ledgerBalanceSummary.setOpeningBal(0.0);
                        ledgerBalanceSummary.setCredit(0.0);
                        ledgerBalanceSummary.setDebit(ledgerTransactionDetails.getDebit());
                        ledgerBalanceSummaryRepository.save(ledgerBalanceSummary);

                        isFirst = false;
                    } else {

                        System.out.println("tranxDate " + tranxDate + " lastTranxDate " + lastTranxDate + " lastLedgerTransactionDetails.getTransactionDate() >>>" + lastLedgerTransactionDetails.getTransactionDate());
                        if (tranxDate.compareTo(lastTranxDate) > 0) {

                            Double lastClostingAmount = ledgerTransactionDetailsRepository.getDateClosingAmtByTranxDateAndLedger(
                                    lastLedgerTransactionDetails.getTransactionDate(), ledgerId, 10, true);
                            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< lastClostingAmount " + lastClostingAmount);
                            double lastCloseAmt = lastClostingAmount != null ? lastClostingAmount : 0;
                            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< lastCloseAmt " + lastCloseAmt + " debit " + ledgerTransactionDetails.getDebit() + " credit " + ledgerTransactionDetails.getCredit());
                            double newCloseAmt = lastCloseAmt + ledgerTransactionDetails.getDebit() - ledgerTransactionDetails.getCredit();
                            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< newCloseAmt " + newCloseAmt);

                            LedgerTransactionDetails ledgerTransactionDetails1 = new LedgerTransactionDetails();
                            ledgerTransactionDetails1.setDebit(ledgerTransactionDetails.getDebit());
                            ledgerTransactionDetails1.setCredit(0.0);
                            ledgerTransactionDetails1.setOpeningBal(lastCloseAmt);
                            ledgerTransactionDetails1.setClosingBal(newCloseAmt);
                            ledgerTransactionDetails1.setTransactionDate(ledgerTransactionDetails.getTransactionDate());
                            ledgerTransactionDetails1.setTransactionId(ledgerTransactionDetails.getTransactionId());
                            ledgerTransactionDetails1.setStatus(true);
                            ledgerTransactionDetails1.setFoundations(ledgerTransactionDetails.getFoundations());
                            ledgerTransactionDetails1.setPrinciples(ledgerTransactionDetails.getPrinciples());
                            ledgerTransactionDetails1.setPrincipleGroups(ledgerTransactionDetails.getPrincipleGroups());
                            ledgerTransactionDetails1.setLedgerMaster(ledgerTransactionDetails.getLedgerMaster());
                            ledgerTransactionDetails1.setTransactionType(ledgerTransactionDetails.getTransactionType());
                            ledgerTransactionDetails1.setBalanceMethod(ledgerTransactionDetails.getBalanceMethod());
                            ledgerTransactionDetails1.setAssociateGroups(ledgerTransactionDetails.getAssociateGroups());
                            ledgerTransactionDetails1.setBranch(ledgerTransactionDetails.getBranch());
                            ledgerTransactionDetails1.setOutlet(ledgerTransactionDetails.getOutlet());
                            ledgerTransactionDetails1.setPaymentStatus(ledgerTransactionDetails.getPaymentStatus());
                            ledgerTransactionDetails1.setPaymentDate(ledgerTransactionDetails.getPaymentDate());
                            ledgerTransactionDetails1.setTransactionType(ledgerTransactionDetails.getTransactionType());
                            ledgerTransactionDetails1.setFinancialYear(ledgerTransactionDetails.getFinancialYear());
                            ledgerTransactionDetails1.setUnderPrefix(ledgerTransactionDetails.getUnderPrefix());
                            ledgerTransactionDetails1.setCreatedAt(LocalDateTime.now());

                            ledgerTransactionDetailsRepository.save(ledgerTransactionDetails1);


                            LedgerBalanceSummary ledgerBalanceSummary = ledgerBalanceSummaryRepository.findByLedgerMasterIdAndStatus(ledgerId, true);
                            ledgerBalanceSummary.setBalance(newCloseAmt);
                            ledgerBalanceSummary.setClosingBal(newCloseAmt);
                            ledgerBalanceSummary.setOpeningBal(0.0);
                            ledgerBalanceSummary.setCredit(0.0);
                            ledgerBalanceSummary.setDebit(newCloseAmt);
                            ledgerBalanceSummaryRepository.save(ledgerBalanceSummary);

                            lastTranxDate = ledgerTransactionDetails.getTransactionDate();
                        } else {
                            try {
                                ledgerTransactionDetailsRepository.insertIntoLegerTranxDetailWithTranxDate(
                                        ledgerTransactionDetails.getFoundations().getId(),
                                        ledgerTransactionDetails.getPrinciples().getId(),
                                        ledgerTransactionDetails.getPrincipleGroups() != null ? ledgerTransactionDetails.getPrincipleGroups().getId() : null,
                                        null, tranxType.getId(), null, ledgerTransactionDetails.getBranch().getId(),
                                        ledgerTransactionDetails.getOutlet().getId(), "pending", ledgerTransactionDetails.getDebit(), 0.0,
                                        tranxDate != null ? tranxDate : null, null,
                                        ledgerTransactionDetails.getTransactionId(), tranxType.getTransactionName() != null ? tranxType.getTransactionName() : null,
                                        ledgerTransactionDetails.getUnderPrefix() != null ? ledgerTransactionDetails.getUnderPrefix() : null,
                                        ledgerTransactionDetails.getFinancialYear() != null ? ledgerTransactionDetails.getFinancialYear() : null, ledgerTransactionDetails.getCreatedBy(),
                                        ledgerTransactionDetails.getLedgerMaster().getId(), null, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    lastLedgerTransactionDetails = ledgerTransactionDetails;
                }
            } else {
                System.out.println("data not found in LedgerTransactiondetail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("updateLedgerAccounting Exception " + e.getMessage());
        }
        return jsonObject;
    }

    public Object getReceeiptNo(HttpServletRequest request) {
        JsonObject response = new JsonObject();

        String lastReceiptNo = numFormat.receiptNumFormat(1);
        LocalDate trDate = LocalDate.parse(request.getParameter("transactionDate"));

        Long transactionId = Long.valueOf(request.getParameter("transactionId"));
        FeesTransactionSummary savedTransactionSummary = transactionSummaryRepository.findById(transactionId).get();

//                        String receiptNo = request.getParameter("receiptNo").trim();
        FeesTransactionDetail lastRecord = feesTransactionDetailRepository.findTop1ByTransactionDateOrderByIdDesc(trDate);
        if (lastRecord != null) {
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ receiptNo ^^^^^^^^^^^^^^^" + lastRecord.getReceiptNo());
            String[] lastNo = lastRecord.getReceiptNo().split("-");
            int lastReceiptNum = Integer.parseInt(lastNo[2]) + 1;
            lastReceiptNo = numFormat.receiptNumFormat(lastReceiptNum);
        }

        String[] academicYr = savedTransactionSummary.getAcademicYear().getYear().split("-");
        String month = numFormat.twoDigitFormat(trDate.getMonthValue());
        String day = numFormat.twoDigitFormat(trDate.getDayOfMonth());

        String receiptNo = academicYr[0].substring(2) + academicYr[1] + "-" + month + day + "-" + lastReceiptNo;
        if (savedTransactionSummary.getBranch().getBranchCode() != null) {
            receiptNo = savedTransactionSummary.getBranch().getBranchCode() + receiptNo;
        }
        receiptNo = getNewReceiptNo(receiptNo, trDate);

        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ receiptNo ^^^^^^^^^^^^^^^" + receiptNo);

        response.addProperty("message", receiptNo);
        response.addProperty("responseStatus", HttpStatus.OK.value());

        return response;
    }

    public Object updateJournalNumbers(HttpServletRequest request) {
        JsonObject response = new JsonObject();

        List<TranxJournalMaster> tranxJournalMasterList = tranxJournalMasterRepository.findAll();
        System.out.println("tranxJournalMasterList " + tranxJournalMasterList.size());
        for (TranxJournalMaster tranxJournalMaster : tranxJournalMasterList) {
            if (tranxJournalMaster != null) {
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ tranxJournalMaster getID ^^^^^^^^^^^^^^^" + tranxJournalMaster.getId());

                Long count = tranxJournalMaster.getId();
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ tranxJournalMaster count ^^^^^^^^^^^^^^^" + count);
                String serailNo = String.format("%05d", count);// 5 digit serial number
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ tranxJournalMaster serailNo ^^^^^^^^^^^^^^^" + serailNo);
                String currentMonth = tranxJournalMaster.getCreatedAt().getMonth().name().substring(0, 3);
                String jtCode = "JRNL" + currentMonth + serailNo;
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<< jtCode ?>>>>>>>>>>>>>>>>>>>>" + jtCode);
                tranxJournalMaster.setJournalSrNo(count);
                tranxJournalMaster.setJournalNo(jtCode);

                try {
                    tranxJournalMasterRepository.save(tranxJournalMaster);
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> save data of " + tranxJournalMaster.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>  Failed to save data of " + tranxJournalMaster.getId());
                }
            }
        }
        return response;
    }

    public JsonObject updateTrasactionDate(HttpServletRequest request) {

        Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("authorization").substring(7));
        JsonObject result = new JsonObject();
        List<LedgerTransactionPostings> ledgerTransactionPostings = ledgerTransactionPostingsRepository.findDataWhichHavingTrnxDateFiscalNull(users.getOutlet().getId(), users.getBranch().getId(), true);
        if (ledgerTransactionPostings.size() > 0) {
            System.out.println("total Row which having FiscalYear null->" + ledgerTransactionPostings.size());
            long i = 1;
            for (LedgerTransactionPostings lPosting : ledgerTransactionPostings) {
                TranxReceiptMaster tranxReceiptMaster = tranxReceiptMasterRepositoty.findByIdAndStatus(lPosting.getTransactionId(), true);

                System.out.println("No:" + i);
                i++;
                TranxReceiptMaster tMaster = null;
                if (tranxReceiptMaster != null) {
                    lPosting.setTransactionDate(tranxReceiptMaster.getTranscationDate());
                    if (tranxReceiptMaster.getFinancialYear() == null) {
                        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(tranxReceiptMaster.getTranscationDate());
                        if (fiscalYear != null) {
                            tranxReceiptMaster.setFiscalYear(fiscalYear);
                            tranxReceiptMaster.setFinancialYear(fiscalYear.getFiscalYear());
                            tranxReceiptMasterRepositoty.save(tranxReceiptMaster);

                        }
                    } else {
                        System.out.println("finacial year alredy set");
                    }
                    lPosting.setFiscalYear(tranxReceiptMaster.getFiscalYear());
                    ledgerTransactionPostingsRepository.save(lPosting);

                    result.addProperty("responseStatus", HttpStatus.OK.value());
                    result.addProperty("responseMessage", "Date & Fiscal Year Added Successfully");

                }

            }
        }
        return result;
    }

    public JsonObject missingInvoiceInTranxPosting(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("authorization").substring(7));
        List<StudentRegister> studentRegister = studentRegisterRepository.findStudentsbyStatus(users.getOutlet().getId(), users.getBranch().getId(), true);

        if (studentRegister.size() > 0) {
            System.out.println("No of Student Registered->" + studentRegister.size());
            long i = 0;
            for (StudentRegister studentRegister1 : studentRegister) {
                LedgerMaster ledgerMaster = ledgerMasterRepository.findByStudentRegisterIdAndBranchIdAndStatus(studentRegister1.getId(), studentRegister1.getBranch().getId(), true);
                if (ledgerMaster != null) {
                    System.out.println("ledger Name-->" + ledgerMaster.getLedgerName());
                    Long tranxtypeid = 17L;
                    LedgerTransactionDetails ledgerTransactionDetails = ledgerTransactionDetailsRepository.findByLedgerMasterIdAndOutletIdAndBranchIdAndTransactionTypeIdAndStatus(ledgerMaster.getId(),
                            ledgerMaster.getOutlet().getId(), ledgerMaster.getBranch().getId(), tranxtypeid, true);
                    if (ledgerTransactionDetails != null) {
                        TranxSalesInvoice tranxSalesInvoice = tranxSalesInvoiceRepository.findByIdAndStatus(ledgerTransactionDetails.getTransactionId(), true);
                        if (tranxSalesInvoice != null) {

                            LedgerTransactionPostings ledgerTransactionPostings = ledgerTransactionPostingsRepository.findByInvoiceNoAndLedgerMasterIdAndTransactionIdAndLedgerTypeAndTranxTypeAndStatus(tranxSalesInvoice.getSalesInvoiceNo(),
                                    ledgerTransactionDetails.getLedgerMaster().getId(), tranxSalesInvoice.getId(), "DR", "17", true);


                            if (ledgerTransactionPostings == null) {
                                System.out.println("No:" + i);
                                i++;

                                ledgerCommonPostings.callToPostings(ledgerTransactionDetails.getDebit(), ledgerTransactionDetails.getLedgerMaster(),
                                        ledgerTransactionDetails.getTransactionType(), null, tranxSalesInvoice.getFiscalYear(),
                                        ledgerTransactionDetails.getBranch(), ledgerTransactionDetails.getOutlet(), ledgerTransactionDetails.getTransactionDate(),
                                        ledgerTransactionDetails.getTransactionId(), tranxSalesInvoice.getSalesInvoiceNo(), "DR", true, "17", "create");

                                List<LedgerTransactionDetails> ltranxForfees = ledgerTransactionDetailsRepository.findByTransactionIdAndTransactionTypeIdAndCredit(tranxSalesInvoice.getId(), tranxtypeid, true);
                                if (ltranxForfees.size() > 0) {
                                    for (LedgerTransactionDetails addltrnaxdetails : ltranxForfees) {
                                        ledgerCommonPostings.callToPostings(addltrnaxdetails.getCredit(), addltrnaxdetails.getLedgerMaster(), addltrnaxdetails.getTransactionType(),
                                                null, tranxSalesInvoice.getFiscalYear(), addltrnaxdetails.getBranch(), addltrnaxdetails.getOutlet(),
                                                addltrnaxdetails.getTransactionDate(), addltrnaxdetails.getTransactionId(), tranxSalesInvoice.getSalesInvoiceNo(),
                                                "CR", true, "17", "create");

                                    }
                                } else {
                                    System.out.println("Feess Paying rows not found");
                                }

                                res.addProperty("responseStatus", HttpStatus.OK.value());
                                res.addProperty("message", "SalesInvoice updated Successfully");


                            } else {
                                System.out.println("Sales Invoice of Student Found in LedgerTrnxPosting-->" + ledgerMaster.getLedgerName());
                            }
                        } else {
                            System.out.println("sales Invoice against student not found in SalesInvoice Table");
                        }
                    } else {
                        System.out.println("Sales Invoice not found in LedgerTranxDetail table");
                    }

                } else {
                    System.out.println("Ledger not Found against Student in ledgermaster table");
                }

            }
        } else {
            System.out.println("No Student Found");
        }
        return res;

    }

    public JsonObject deleteFeesDataForVidyala(HttpServletRequest request) {
        Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        JsonObject result = new JsonObject();
        try {
            Long transactionId = Long.valueOf(request.getParameter("transactionId"));
            String receiptNo = request.getParameter("receiptNo");
            boolean isConcessionTranx = false;
            double concessionAmount = 0;
            List<TranxReceiptMaster> tranxReceiptMastersList = tranxReceiptMasterRepositoty.findByReceiptNo(receiptNo);

            TransactionTypeMaster tranxType = transactionTypeMasterRepository.findByTransactionCodeIgnoreCase("RCPT");
            for (TranxReceiptMaster tranxReceiptMaster : tranxReceiptMastersList) {

                System.out.println("tranxReceiptMaster.getId() " + tranxReceiptMaster.getId());
                List<TranxReceiptPerticularsDetails> tranxReceiptPerticularsDetails = tranxReceiptPerticularsDetailsRepository.findByTranxReceiptMasterIdAndOutletIdAndBranchIdAndStatus(
                        tranxReceiptMaster.getId(), users.getOutlet().getId(), users.getBranch().getId(), true);
                for (TranxReceiptPerticularsDetails tranxReceiptPerticularsDetails1 : tranxReceiptPerticularsDetails) {
                    if (tranxReceiptPerticularsDetails1.getLedgerMaster().getLedgerName().toLowerCase().contains("concession")) {
                        isConcessionTranx = true;
                        concessionAmount = tranxReceiptPerticularsDetails1.getPaidAmt();
                    }

                    if (tranxReceiptPerticularsDetails1.getType().equalsIgnoreCase("DR")) {
                        System.out.println("TRANX DET ID " + tranxReceiptPerticularsDetails1.getId() + "LDID " + tranxReceiptPerticularsDetails1.getLedgerMaster().getId() + " TRID " + tranxReceiptPerticularsDetails1.getTranxReceiptMaster().getId()
                                + " trdate " + tranxReceiptPerticularsDetails1.getTranxReceiptMaster().getTranscationDate() + " paidAmt " + tranxReceiptPerticularsDetails1.getPaidAmt() + " DR >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        ledgerTransactionDetailsRepository.ledgerPostingEditTranxDate(
                                tranxReceiptPerticularsDetails1.getLedgerMaster().getId(), tranxReceiptPerticularsDetails1.getTranxReceiptMaster().getId(), tranxType.getId(), "DR",
                                0.0, tranxReceiptPerticularsDetails1.getTranxReceiptMaster().getTranscationDate());
                    } else if (tranxReceiptPerticularsDetails1.getType().equalsIgnoreCase("CR")) {
                        System.out.println("TRANX DET ID " + tranxReceiptPerticularsDetails1.getId() + "LDID " + tranxReceiptPerticularsDetails1.getLedgerMaster().getId() + " TRID " + tranxReceiptPerticularsDetails1.getTranxReceiptMaster().getId()
                                + " trdate " + tranxReceiptPerticularsDetails1.getTranxReceiptMaster().getTranscationDate() + " paidAmt " + tranxReceiptPerticularsDetails1.getPaidAmt() + " CR >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        ledgerTransactionDetailsRepository.ledgerPostingEditTranxDate(
                                tranxReceiptPerticularsDetails1.getLedgerMaster().getId(), tranxReceiptPerticularsDetails1.getTranxReceiptMaster().getId(), tranxType.getId(), "CR",
                                0.0, tranxReceiptPerticularsDetails1.getTranxReceiptMaster().getTranscationDate());
                    }

                    JournalDetailsHistory journalDetailsHistory = new JournalDetailsHistory();
                    journalDetailsHistory.setTranxJournalDetailsId(tranxReceiptPerticularsDetails1.getId());
                    journalDetailsHistory.setOutletId(tranxReceiptPerticularsDetails1.getOutlet().getId());
                    journalDetailsHistory.setBranchId(tranxReceiptPerticularsDetails1.getBranch().getId());
                    journalDetailsHistory.setStatus(true);
                    journalDetailsHistory.setPaidAmount(tranxReceiptPerticularsDetails1.getPaidAmt());
                    journalDetailsHistory.setType(tranxReceiptPerticularsDetails1.getType());
                    journalDetailsHistory.setCreatedBy(users.getId());
                    journalDetailsHistory.setJournalMasterId(tranxReceiptPerticularsDetails1.getTranxReceiptMaster().getId());
                    journalDetailsHistory.setLedgerType(tranxReceiptPerticularsDetails1.getType());
                    journalDetailsHistory.setOperationType("delete");
                    journalDetailsHistoryRepository.save(journalDetailsHistory);


                    tranxReceiptPerticularsDetails1.setStatus(false);
                    tranxReceiptPerticularsDetailsRepository.save(tranxReceiptPerticularsDetails1);
                }
                ledgerTransactionDetailsRepository.updateToStatusZeroTranxLedgerDetailByJVIdAndTranxType(tranxReceiptMaster.getId(), 5L);
//                    ledgerTransactionDetailsRepository.updateToStatusZeroTranxLedgerDetailByJVIdAndTranxType(tranxJournalMaster.getId(), 10L);

                TranxJournalMaster tranxJournalMaster1 = tranxJournalMasterRepository.findByIdAndStatus(tranxReceiptMaster.getId(), true);

                if (tranxJournalMaster1 != null) {
                    JournalMasterHistory journalMasterHistory = new JournalMasterHistory();
                    journalMasterHistory.setTranxJournalMasterId(tranxJournalMaster1.getId());
                    journalMasterHistory.setOperationType("delete");
                    journalMasterHistory.setFeeReceiptNo(tranxJournalMaster1.getFeeReceiptNo());
                    journalMasterHistory.setNarrations(tranxReceiptMaster.getNarrations());
                    journalMasterHistory.setTranscationDate(tranxReceiptMaster.getTranscationDate());
                    journalMasterHistory.setTotalAmt(tranxJournalMaster1.getTotalAmt());
                    journalMasterHistory.setStatus(true);
                    journalMasterHistory.setCreatedBy(users.getId());
                    journalMasterHistory.setJournalSrNo(tranxJournalMaster1.getJournalSrNo());
                    journalMasterHistory.setJournalNo(tranxJournalMaster1.getJournalNo());
                    journalMasterHistory.setFinancialYear(tranxJournalMaster1.getFinancialYear());
                    if (tranxJournalMaster1.getFiscalYear() != null) {
                        journalMasterHistory.setFiscalYearId(tranxJournalMaster1.getFiscalYear().getId());
                    }
                    journalMasterHistory.setFeeReceiptNo(tranxReceiptMaster.getReceiptNo());
                    journalMasterHistory.setFinancialYear(tranxReceiptMaster.getFinancialYear());
                    journalMasterHistory.setTranscationDate(tranxJournalMaster1.getTranscationDate());
                    journalMasterHistoryRepository.save(journalMasterHistory);

                }
                tranxReceiptMaster.setStatus(false);
                tranxReceiptMasterRepositoty.save(tranxReceiptMaster);


            }

            /************* Application Level Start*****************/
            double totalpAmt = 0;
//            double concessionAmt = 0;
            List<FeesTransactionDetail> list = feesTransactionDetailRepository.findByFeesTransactionSummaryIdAndReceiptNo(transactionId, receiptNo);
            for (FeesTransactionDetail feesTransactionDetail : list) {
                try {
                    totalpAmt = totalpAmt + feesTransactionDetail.getPaidAmount();
                    System.out.println("totalpAmt " + (totalpAmt));
                    Long tId = feesTransactionDetail.getId();
                    feesTransactionDetailRepository.DeleteColumnFeesTransaction(tId);

                } catch (Exception e) {
                    result.addProperty("message", "Failed to delete fees details!");
                    result.addProperty("responseStatus", HttpStatus.OK.value());
                    return result;
                }
            }
            System.out.println("totalpAmt " + (totalpAmt));
            FeesTransactionSummary tranxsummary = transactionSummaryRepository.findByIdAndStatus(transactionId, true);
            double prevpaid = tranxsummary.getPaidAmount();
            double prevbalance = tranxsummary.getBalance();
            System.out.println("paid " + (prevpaid - totalpAmt));
            System.out.println("balance " + (prevbalance + prevpaid));

            double netpaid = prevpaid - totalpAmt;
            double netbalance = prevbalance + totalpAmt;
            System.out.println("netpaid " + netpaid);
            System.out.println("netbalance " + netbalance);

            System.out.println(":isConcessionTranx ->" + isConcessionTranx);
            System.out.println(":concessionAmount ->" + concessionAmount);

            if (netpaid == 0) {
                tranxsummary.setConcessionType(null);
                tranxsummary.setConcessionAmount(null);
                tranxsummary.setIsManual(null);
            } else {

                if (tranxsummary.getConcessionType() != null && tranxsummary.getConcessionType() != 0 && isConcessionTranx == true) {
                    System.out.println(" ****************************************** executed accounting condition ");
                    tranxsummary.setConcessionAmount(0);
                    tranxsummary.setConcessionType(0);
                }
            }

            netbalance = netbalance + concessionAmount;
            tranxsummary.setPaidAmount(netpaid);
            tranxsummary.setBalance(netbalance);
            transactionSummaryRepository.save(tranxsummary);
            /************* Application Level End*****************/

            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.addProperty("message", "Fee Record Deleted SuccessFully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception" + e.getMessage());
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.addProperty("message", "Failed to Delete Data");
        }

        return result;
    }

    public JsonObject createReceipt(HttpServletRequest request) {
        Users users = jwtTokenUtil.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Map<String, String[]> paramMap = request.getParameterMap();
        JsonObject response = new JsonObject();
        StudentTransport studentTransport1=null;
        TranxReceiptMaster tranxReceipt = new TranxReceiptMaster();
        LedgerMaster ledgerMaster1=ledgerMasterRepository.findByIdAndStatus(Long.valueOf(request.getParameter("ledger_id")),true);
        LocalDate tranxDate = LocalDate.parse(request.getParameter("transaction_dt"));
        LocalDate payingFrom = LocalDate.parse(request.getParameter("payingFrom"));
        LocalDate payingTo = LocalDate.parse(request.getParameter("payingTo"));
        int payableMonth = Integer.valueOf(request.getParameter("payableMonth"));
        Double paidAmount = Double.valueOf(request.getParameter("paidAmount"));
        int paymentMode = Integer.parseInt(request.getParameter("paymentMode"));
        String paymentNo = request.getParameter("paymentNo");
        String paymentMethod="";
        String lastReceiptNo = numFormat.receiptNumFormat(1);
        FeesTransactionDetail lastRecord = feesTransactionDetailRepository.findTop1ByTransactionDateOrderByIdDesc(tranxDate);
        if (lastRecord != null) {
            String[] lastNo = lastRecord.getReceiptNo().split("-");
            int lastReceiptNum = Integer.parseInt(lastNo[2]) + 1;
            lastReceiptNo = numFormat.receiptNumFormat(lastReceiptNum);
        }

        if(paymentMode==0){
            paymentMethod="Cash";

        } else if (paymentMode==1) {
            paymentMethod="Cheque";
        }else {
            paymentMethod="Online";
        }

        StudentTransport studentTransport=studentTransportRepository.findByStudentRegisterIdAndIsBusFeePendingAndStatus(ledgerMaster1.getStudentRegister().getId(),true,true);
        if(studentTransport!=null) {
            Integer lastPaidMonth = Integer.valueOf(studentTransport.getPaidMonth());
            Double lastPaidAmount = studentTransport.getPaidAmount();
            studentTransport.setPaidAmount(lastPaidAmount + paidAmount);

//            studentTransport.setPaidAmount(lastPaidAmount+paidAmount);
            studentTransport.setPaidMonth(payableMonth+lastPaidMonth);

//            studentTransport.set
            studentTransport.setPaidMonth(payableMonth + lastPaidMonth);
            Double pendingFee = studentTransport.getBusFee() - studentTransport.getPaidAmount();
            if (pendingFee >= 0) {
                studentTransport.setIsBusFeePending(true);
                studentTransport.setOutstanding(pendingFee);

            } else {
                studentTransport.setIsBusFeePending(false);
                studentTransport.setOutstanding(pendingFee);

            }
            try {
                studentTransport1 = studentTransportRepository.save(studentTransport);
                System.out.println("student transport updated successfully");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error" + e.getMessage());
            }

            StudentBusTransaction studentBusTransaction = new StudentBusTransaction();
            studentBusTransaction.setBusFee(studentTransport1.getBusFee());
            studentBusTransaction.setAcademicYear(studentTransport1.getAcademicYear());
            studentBusTransaction.setPaidAmount(studentTransport1.getPaidAmount());
            studentBusTransaction.setStudentRegister(studentTransport1.getStudentRegister());
            studentBusTransaction.setPaidMonth(studentTransport1.getPaidMonth());
            studentBusTransaction.setPaidFrom(payingFrom);
            studentBusTransaction.setPaidTo(payingTo);
            studentBusTransaction.setStudentTransport(studentTransport1);
            studentBusTransaction.setTotalMonth(studentTransport1.getTotalMonth());
            studentBusTransaction.setStatus(true);
//        studentBusTransaction.setMonthName();
            try {
                studentBusTransactionRepository.save(studentBusTransaction);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error" + e.getMessage());
            }
            String studName = studentTransport1.getStudentRegister().getFirstName();
            if (studentTransport1.getStudentRegister().getFatherName() != null)
                studName = studName + " " + studentTransport1.getStudentRegister().getFatherName();
            if (studentTransport1.getStudentRegister().getLastName() != null)
                studName = studName + " " + studentTransport1.getStudentRegister().getLastName();

            String narration = "Being cash deposited against fee from " + studName+  " vide R.No. " + lastReceiptNo
                    + " " + studentTransport1.getBranch().getBranchName() + " STD." + studentTransport1.getStandard().getStandardName();
            if (paymentMode != 0) {
                narration = "Being online fee received from " + studName + " vide R.No. " + lastReceiptNo
                        + " " + studentTransport1.getBranch().getBranchName() + " STD." + studentTransport1.getStandard().getStandardName();
            }
            /*** Inserting into Transaction Reciept Master****/
            TransactionTypeMaster tranxType = transactionTypeMasterRepository.findByTransactionCodeIgnoreCase("RCPT");
            LedgerMaster mStudent = ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(
                    ledgerMaster1.getStudentRegister().getId(), users.getOutlet().getId(), users.getBranch().getId(), true);
            System.out.println("narration >>>>>>>>>>>>>>>>>>>>>>>" + narration);
            System.out.println("paidamt >>>>>>>>>>>>>>>>>>>>>>>" + paidAmount);
            TranxReceiptMaster receiptMaster = new TranxReceiptMaster();
            Branch branch = null;
            if (users.getBranch() != null) branch = users.getBranch();
            Outlet outlet = users.getOutlet();
            Long count = tranxReceiptMasterRepositoty.findLastRecord(outlet.getId(), branch.getId());
            String serailNo = String.format("%05d", count + 1);// 5 digit serial number
            GenerateDates generateDates = new GenerateDates();
            String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
            receiptMaster.setOutlet(outlet);
            receiptMaster.setBranch(branch);
            receiptMaster.setReceiptNo(lastReceiptNo);
            receiptMaster.setTranscationDate(tranxDate);

            receiptMaster.setReceiptSrNo(count + 1);
            long studentId = ledgerMaster1.getStudentRegister().getId();
            LedgerMaster studentLedger = ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(studentId, users.getOutlet().getId(), users.getBranch().getId(), true);
            receiptMaster.setStudentLedger(studentLedger);
            receiptMaster.setUnderBranch(users.getBranch());
            receiptMaster.setTotalAmt(paidAmount); //outstanding
            receiptMaster.setNarrations(narration);
            receiptMaster.setCreatedBy(users.getId());
            receiptMaster.setStatus(true);
            /* fiscal year mapping*/
            FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(tranxDate);
            if (fiscalYear != null) {
                receiptMaster.setFiscalYear(fiscalYear);
                receiptMaster.setFinancialYear(fiscalYear.getFiscalYear());
            }
            TranxReceiptMaster tranxReceiptMaster = tranxReceiptMasterRepositoty.save(receiptMaster);
            try {
                /**** Tranx Reciept Details for Student A/C *****/
                TranxReceiptPerticularsDetails tranxReceiptPerticularsDetails = new TranxReceiptPerticularsDetails();
                tranxReceiptPerticularsDetails.setBranch(branch);
                tranxReceiptPerticularsDetails.setOutlet(outlet);
                tranxReceiptPerticularsDetails.setStatus(true);
                tranxReceiptPerticularsDetails.setTransactionDate(tranxDate);
                tranxReceiptPerticularsDetails.setLedgerMaster(mStudent);
                tranxReceiptPerticularsDetails.setTranxReceiptMaster(tranxReceiptMaster);
                tranxReceiptPerticularsDetails.setType("CR");
                tranxReceiptPerticularsDetails.setCreatedBy(users.getId());
                tranxReceiptPerticularsDetails.setPaidAmt(paidAmount);///outstanding
                tranxReceiptPerticularsDetails.setStatus(true);
                TranxReceiptPerticularsDetails receiptPerticularsDetails4 = tranxReceiptPerticularsDetailsRepository.save(tranxReceiptPerticularsDetails);
                insertIntoSDReceipt(receiptPerticularsDetails4, tranxType); //Accounting postings of Student Account
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                /* *** Tranx Reciept Details for CASH OR BANK *****/
                TranxReceiptPerticularsDetails tranxReceiptPerticularsDetails2 = new TranxReceiptPerticularsDetails();
                LedgerMaster ledgerMaster = null;
                if (paymentMode == 0) {

                    ledgerMaster = ledgerMasterRepository.findByUniqueCodeAndStatusAndOutletIdAndBranchId("CAIH", true, outlet.getId(), branch.getId());

                } else {
                                       /* long ledgerId = Long.parseLong(request.getParameter("ledgerId"));
                                        System.out.println("LedgerId-->" + ledgerId);*/
                    ledgerMaster = ledgerMasterRepository.findByUniqueCodeAndStatusAndOutletIdAndBranchId("BAAC", true, outlet.getId(), branch.getId());
                }
                tranxReceiptPerticularsDetails2.setBranch(branch);
                tranxReceiptPerticularsDetails2.setOutlet(outlet);
                tranxReceiptPerticularsDetails2.setStatus(true);
                tranxReceiptPerticularsDetails2.setLedgerMaster(ledgerMaster);
                tranxReceiptPerticularsDetails2.setTranxReceiptMaster(tranxReceiptMaster);
                tranxReceiptPerticularsDetails2.setType("DR");
                tranxReceiptPerticularsDetails2.setTransactionDate(tranxDate);
                tranxReceiptPerticularsDetails2.setCreatedBy(users.getId());
                tranxReceiptPerticularsDetails2.setPaidAmt(paidAmount);
                tranxReceiptPerticularsDetails2.setStatus(true);
                TranxReceiptPerticularsDetails receiptPerticularsDetails = tranxReceiptPerticularsDetailsRepository.save(tranxReceiptPerticularsDetails2);
                insertIntoReceipt(receiptPerticularsDetails, tranxType);//Accounting postings of DP Account

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        return response;
    }
}
