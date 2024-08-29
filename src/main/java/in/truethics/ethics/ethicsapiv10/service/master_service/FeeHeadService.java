package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.common.GenerateDates;
import in.truethics.ethics.ethicsapiv10.common.GenerateFiscalYear;
import in.truethics.ethics.ethicsapiv10.common.LedgerCommonPostings;
import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerTransactionDetails;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesInvoice;
import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesInvoiceDetails;
import in.truethics.ethics.ethicsapiv10.model.school_master.*;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionDetail;
import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalMaster;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerTransactionDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerTransactionPostingsRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.*;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.FeesTransactionDetailRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.journal_repository.TranxJournalDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.journal_repository.TranxJournalMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository.TranxSalesInvoiceDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository.TranxSalesInvoiceRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeeHeadService {
    private static final Logger studentLogger = LoggerFactory.getLogger(StudentRegisterService.class);

    private static final Logger feeHeadLogger = LoggerFactory.getLogger(FeeHeadService.class);
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private PrincipleRepository principleRepository;
    @Autowired
    private PrincipleGroupsRepository principleGroupsRepository;
    @Autowired
    private FeeHeadRepository feeHeadRepository;
    @Autowired
    private LedgerMasterRepository ledgerMasterRepository;

    @Autowired
    private FeesTransactionDetailRepository feesTransactionDetailRepository;


    @Autowired
    private TransactionTypeMasterRepository transactionTypeMasterRepository;
    @Autowired
    private TranxSalesInvoiceRepository tranxSalesInvoiceRepository;

    @Autowired
    private AssociateGroupsRepository associateGroupsRepository;
    @Autowired
    private LedgerCommonPostings ledgerCommonPostings;


    @Autowired
    private GenerateFiscalYear generateFiscalYear;
    @Autowired
    private FeesDetailsRepository feesDetailsRepository;

    @Autowired
    private StudentAdmissionRepository studentAdmissionRepository;
    @Autowired
    private TranxSalesInvoiceDetailsRepository tranxSalesInvoiceDetailsRepository;
    @Autowired
    private LedgerTransactionPostingsRepository ledgerTransactionPostingsRepository;

    @Autowired
    private LedgerTransactionDetailsRepository ledgerTransactionDetailsRepository;

    @Autowired
    private TranxJournalDetailsRepository tranxJournalDetailsRepository;
    @Autowired
    private TranxJournalMasterRepository tranxJournalMasterRepository;

    public Object createFeeHead(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        FeeHead feeHead = new FeeHead();
        try {
            if (request.getParameterMap().containsKey("branchId")) {
                Long branchId = Long.valueOf(request.getParameter("branchId"));
                Branch branch = branchRepository.findByIdAndStatus(branchId, true);
                feeHead.setBranch(branch);
            } else if (users.getBranch() != null) {
                feeHead.setBranch(users.getBranch());
            }

            Long underBranchId = Long.valueOf(request.getParameter("underBranchId"));
            Branch branch = branchRepository.findByIdAndStatus(underBranchId, true);
            feeHead.setUnderBranch(branch);
            Long ledgerId = Long.valueOf(request.getParameter("ledgerId"));
            LedgerMaster ledgerMaster = ledgerMasterRepository.findByIdAndStatus(ledgerId, true);
            feeHead.setLedger(ledgerMaster);
            feeHead.setOutlet(users.getOutlet());
            feeHead.setFeeHeadName(request.getParameter("feeHeadName"));
            feeHead.setStudentType(Integer.valueOf(request.getParameter("studentType")));
            if(request.getParameter("isReceiptCurrentBranch").equals("current"))
            {
                feeHead.setIsReceiptCurrentBranch(true);
            }
            else {
                feeHead.setIsReceiptCurrentBranch(false);
            }



            feeHead.setCreatedBy(users.getId());
            feeHead.setStatus(true);
            try {
                feeHeadRepository.save(feeHead);
                responseObject.setMessage("Fee head created successfully");
                responseObject.setResponseStatus(HttpStatus.OK.value());
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                feeHeadLogger.error("createFeeHead -> failed to create fee head " + e.getMessage());
                responseObject.setMessage("Already exist! Duplicate data not allowed");
                responseObject.setResponseStatus(HttpStatus.CONFLICT.value());
            } catch (Exception e1) {
                e1.printStackTrace();
                feeHeadLogger.error("createFeeHead -> failed to create fee head " + e1.getMessage());
                responseObject.setMessage("Failed to create fee head");
                responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            feeHeadLogger.error("createFeeHead -> failed to create fee head " + e1.getMessage());
            responseObject.setMessage("Failed to create fee head");
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return responseObject;
    }

    /* Get  all heads of branch */
    public JsonObject getAllFeeHeads(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Long outletId = users.getOutlet().getId();

            List<FeeHead> list = new ArrayList<>();
            if (users.getBranch() != null) {
                list = feeHeadRepository.findByOutletIdAndBranchIdAndStatus(outletId, users.getBranch().getId(), true);
            } else {
                list = feeHeadRepository.findByOutletIdAndStatus(outletId, true);

            }
            if (list.size() > 0) {
                for (FeeHead mGroup : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mGroup.getId());
                    response.addProperty("feeHeadName", mGroup.getFeeHeadName());
//                    response.addProperty("studentType", mGroup.getStudentType());
                    if (mGroup.getStudentType() == 1) {
                        response.addProperty("studentType", "Day Scholar");
                    } else if (mGroup.getStudentType() == 2) {
                        response.addProperty("studentType", "Residential");
                    }
                    response.addProperty("outletName", mGroup.getOutlet().getCompanyName());
                    response.addProperty("branchId", mGroup.getBranch().getId());
                    response.addProperty("branchName", mGroup.getBranch().getBranchName());
                    response.addProperty("underBranchName", mGroup.getUnderBranch().getBranchName());
                    response.addProperty("ledgerName", mGroup.getLedger() != null ? mGroup.getLedger().getLedgerName() : "");
                    response.addProperty("isReceiptCurrentBranch", mGroup.getIsReceiptCurrentBranch() == true ? "Current Branch" : "Hostel Branch");


                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            feeHeadLogger.error("getAllFeeHead -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }

    public Object updateFeeHead(HttpServletRequest request) {
        ResponseMessage response = new ResponseMessage();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            FeeHead feeHead = feeHeadRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (feeHead != null) {
                feeHead.setFeeHeadName(request.getParameter("feeHeadName"));
                if (request.getParameterMap().containsKey("branchId")) {
                    Long branchId = Long.valueOf(request.getParameter("branchId"));
                    Branch branch = branchRepository.findByIdAndStatus(branchId, true);
                    feeHead.setBranch(branch);
                } else if (users.getBranch() != null) {
                    feeHead.setBranch(users.getBranch());
                }

                Long underBranchId = Long.valueOf(request.getParameter("underBranchId"));
                Branch branch = branchRepository.findByIdAndStatus(underBranchId, true);
                feeHead.setUnderBranch(branch);

                Long ledgerId = Long.valueOf(request.getParameter("ledgerId"));
                LedgerMaster ledgerMaster = ledgerMasterRepository.findByIdAndStatus(ledgerId, true);
                feeHead.setLedger(ledgerMaster);
                feeHead.setStudentType(Integer.valueOf(request.getParameter("studentType")));
             //   feeHead.setIsReceiptCurrentBranch(Boolean.valueOf(request.getParameter("isReceiptCurrentBranch")));
                if(request.getParameter("isReceiptCurrentBranch").equals("current"))
                {
                    feeHead.setIsReceiptCurrentBranch(true);
                }
                else {
                    feeHead.setIsReceiptCurrentBranch(false);
                }

                feeHead.setUpdatedBy(users.getId());
                try {
                    feeHeadRepository.save(feeHead);
                    response.setMessage("FeeHead updated successfully");
                    response.setResponseStatus(HttpStatus.OK.value());
                } catch (Exception e1) {
                    e1.printStackTrace();
                    feeHeadLogger.error("updateFeeHead -> failed to update fee head " + e1.getMessage());
                    response.setMessage("Failed to update fee head");
                    response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } else {
                response.setMessage("Data not found");
                response.setResponseStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            feeHeadLogger.error("updateFeeHead -> failed to update fee head " + e.getMessage());
            response.setMessage("Failed to update fee head");
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    public Object updateFeeHeadAccounting(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        ResponseMessage response = new ResponseMessage();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("authorization").substring(7));
        long oldfeesHeadId = Long.parseLong(request.getParameter("id"));
        boolean normalupdate = Boolean.parseBoolean(request.getParameter("normalupdate"));
        FeeHead feeHead = feeHeadRepository.findByIdAndStatus(oldfeesHeadId, true);
        FeeHead hostelfeeHead = null;
        Branch oldBranch = null;
        if (normalupdate == true) {
            LedgerMaster oldledger = feeHead.getLedger();
            oldBranch = feeHead.getBranch();
            Outlet oldOutlet = feeHead.getOutlet();
            Branch hostelBranch = null;
            if (request.getParameterMap().containsKey("branchId")) {
                long branchId = Long.parseLong(request.getParameter("branchId"));
                Branch branch = branchRepository.findByIdAndStatus(branchId, true);
                feeHead.setBranch(branch);
            } else if (users.getBranch() != null) {
                feeHead.setBranch(users.getBranch());
            }
            boolean updatehostelbranch = false;
            long underBranchId = Long.parseLong(request.getParameter("underBranchId"));
            Branch branch = branchRepository.findByIdAndStatus(underBranchId, true);
            feeHead.setUnderBranch(branch);
            if (branch.getBranchName().toLowerCase().contains("hostel")) {
                updatehostelbranch = true;
            }
            Long ledgerId = Long.valueOf(request.getParameter("ledgerId"));

            LedgerMaster ledgerMaster = ledgerMasterRepository.findByIdAndStatus(ledgerId, true);
//            hostelfeeHead=feeHeadRepository.findByLedgerMasterIdAndUnderBranchIdAndStatus(ledgerId, branch.getId(),true);
            feeHead.setLedger(ledgerMaster);
            feeHead.setStudentType(Integer.valueOf(request.getParameter("studentType")));
            feeHead.setIsReceiptCurrentBranch(Boolean.valueOf(request.getParameter("isReceiptCurrentBranch")));


        } else {
            if (feeHead != null) {

                LedgerMaster oldledger = feeHead.getLedger();
                oldBranch = feeHead.getBranch();
                Outlet oldOutlet = feeHead.getOutlet();
                Branch hostelBranch = null;
                if (request.getParameterMap().containsKey("branchId")) {
                    long branchId = Long.parseLong(request.getParameter("branchId"));
                    Branch branch = branchRepository.findByIdAndStatus(branchId, true);
                    feeHead.setBranch(branch);
                } else if (users.getBranch() != null) {
                    feeHead.setBranch(users.getBranch());
                }
                boolean updatehostelbranch = false;
                long underBranchId = Long.parseLong(request.getParameter("underBranchId"));
                Branch branch = branchRepository.findByIdAndStatus(underBranchId, true);
                feeHead.setUnderBranch(branch);
                if (branch.getBranchName().toLowerCase().contains("hostel")) {
                    updatehostelbranch = true;
                }
                Long ledgerId = Long.valueOf(request.getParameter("ledgerId"));

                LedgerMaster ledgerMaster = ledgerMasterRepository.findByIdAndStatus(ledgerId, true);
//            hostelfeeHead=feeHeadRepository.findByLedgerMasterIdAndUnderBranchIdAndStatus(ledgerId, branch.getId(),true);
                feeHead.setLedger(ledgerMaster);
                feeHead.setStudentType(Integer.valueOf(request.getParameter("studentType")));
                feeHead.setIsReceiptCurrentBranch(Boolean.valueOf(request.getParameter("isReceiptCurrentBranch")));


                if (updatehostelbranch == true) {

//                List<LedgerTransactionPostings> ledgerTransactionPostings = ledgerTransactionPostingsRepository.findByMessLedgerIdAndStatus(oldledger.getId(), oldBranch.getId());
//                if (ledgerTransactionPostings.size() > 0) {
//                    for (LedgerTransactionPostings mDetails : ledgerTransactionPostings) {
//
//
//                    }
//                }

                    List<LedgerTransactionDetails> ledgerTransactionDetails = ledgerTransactionDetailsRepository.findByLedgerMasterIdAndOutletIdAndBranchIdAndStatus(oldledger.getId(), oldOutlet.getId(), oldBranch.getId(), true);
                    System.out.println("Total No of Mess Students->" + ledgerTransactionDetails.size());
                    if (ledgerTransactionDetails.size() > 0) {
                        for (LedgerTransactionDetails ltDetails : ledgerTransactionDetails) {
                            System.out.println(" <>>>>>>>>>>>>>>>>>>>>>> ltDetails id " + ltDetails.getId());
                            long centralledgerId = 0;
                            long studentId = 0;
                            double messhostelFee = 0;
                            TranxSalesInvoice tranxSalesInvoice = null;
                            if (ltDetails.getTransactionType().getId() == 17)
                                tranxSalesInvoice = tranxSalesInvoiceRepository.findByIdAndStatus(ltDetails.getTransactionId(), true);
                            if (tranxSalesInvoice != null) {
//                            StudentRegister student = tranxSalesInvoice.getSundryDebtors().getStudentRegister();

                                centralledgerId = tranxSalesInvoice.getSundryDebtors().getId();
                                LedgerMaster lmaster = ledgerMasterRepository.findByIdAndStatus(centralledgerId, true);
                                if (lmaster != null) {
                                    studentId = lmaster.getStudentRegister().getId();
                                    hostelBranch = branchRepository.getHostelBranchRecord(users.getOutlet().getId(), true);

                                    LedgerMaster studenthosteledger = ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(studentId, users.getOutlet().getId(), hostelBranch.getId(), true);
                                    if (studenthosteledger != null) {
                                        TranxSalesInvoice sinvoice = tranxSalesInvoiceRepository.findBySundryDebtorsIdAndStatus(studenthosteledger.getId(), true);
                                        if (sinvoice != null) {
                                            messhostelFee = ltDetails.getCredit() + sinvoice.getTotalAmount();
                                            sinvoice.setTotalAmount(messhostelFee);
                                            tranxSalesInvoiceRepository.save(sinvoice);

                                            try {
                                                response.setMessage("FeeHead updated successfully");
                                                response.setResponseStatus(HttpStatus.OK.value());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                feeHeadLogger.error("updateFeeHead -> failed to update fee head " + e.getMessage());
                                                response.setMessage("Failed to update fee head");
                                                response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                                            }
                                        }
                                    } else {
                                        LedgerMaster hostelStudentLedger = createLedgerForStudentForHostel(hostelBranch, lmaster.getStudentRegister(), users);
                                        if (hostelStudentLedger == null) {
                                            System.out.println("ledger not created >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                                            response.setMessage("Failed to create ledger in hostel, please delete & add once again");
                                            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

                                        }
                                        messhostelFee = ltDetails.getCredit();
                                        StudentAdmission studentAdmission = studentAdmissionRepository.findByStudentRegisterIdAndStatus(studentId, true);
                                        System.out.println("Student against Hostel not Found Create Student Ledger & SalesInvoice >>>>>>>>>>>>>>>" + hostelStudentLedger.getId());
                                        createTranxSalesInvoiceForHostelMess(hostelBranch, hostelStudentLedger.getStudentRegister(), users, hostelStudentLedger.getId(), messhostelFee, studentAdmission, feeHead);

                                        response.setMessage("FeeHead updated successfully");
                                        response.setResponseStatus(HttpStatus.OK.value());
                                    }

                                }

                            } else {
                                System.out.println("Transaction Id Not found in Sales Invoice Table");
                            }
                        }
                    }
                    //for Payment Purpose

                    System.out.println("feeHead.getId()--->" + feeHead.getId());
                    List<FeesTransactionDetail> feesTransactionDetail = feesTransactionDetailRepository.findByFeeHeadIdAndStatus(feeHead.getId(), true);
                    System.out.println("No of Student who paid Mess Fee---->" + feesTransactionDetail.size());
                    for (FeesTransactionDetail mfeetranxDetails : feesTransactionDetail) {

                        //   List<TranxJournalMaster> tranxJournalMaster = tranxJournalMasterRepository.findByFeeReceiptNoAndStatus(mfeetranxDetails.getReceiptNo(), true);//suyash branch  and hostel
                        /***** suyash Branch****/
                        System.out.println("Receipt No:" + mfeetranxDetails.getReceiptNo());

                        TranxJournalMaster mSuyashBranch = tranxJournalMasterRepository.findByFeeReceiptNoAndStatusAndBranchId(mfeetranxDetails.getReceiptNo(), true, oldBranch.getId());
                        if (mSuyashBranch != null) {

                            mSuyashBranch.setTotalAmt(mSuyashBranch.getTotalAmt() - mfeetranxDetails.getPaidAmount());
                            try {
                                tranxJournalMasterRepository.save(mSuyashBranch);
                                response.setResponseStatus(HttpStatus.OK.value());

                                response.setMessage("Payment updated Successfully1");
                                System.out.println("Journal Master Created Successfully1");

                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                response.setMessage("Failed to update Payment1");
                                response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

                            }
                            List<TranxJournalDetails> msuyashDetails = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndStatus(mSuyashBranch.getId(), true);
                            for (TranxJournalDetails mDetails : msuyashDetails) {
                                //
                                System.out.println("mDetails.getLedgerMaster().getLedgerName().toLowerCase() "+mDetails.getLedgerMaster().getLedgerName().toLowerCase());
                                if (mDetails.getLedgerMaster().getLedgerName().toLowerCase().contains("concession")) {
                                    System.out.println("coneccsion amount dont include");
                                } else {
                                    //           System.out.println(mDetails.getLedgerMaster().getStudentRegister().getFirstName() + "" + mDetails.getLedgerMaster().getStudentRegister().getLastName());
                                    mDetails.setPaidAmount(mDetails.getPaidAmount() - mfeetranxDetails.getPaidAmount());
                                    try {
                                        tranxJournalDetailsRepository.save(mDetails);
                                        response.setResponseStatus(HttpStatus.OK.value());
                                        System.out.println("Journal Detail Created Successfully2");

                                        response.setMessage("Payment updated Successfully2");

                                    } catch (Exception e1) {
                                        System.out.println(e1.getMessage());
                                        response.setMessage("Failed to update Payment2");
                                        response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                                    }
                                }

                            } /***** End of suyash Branch****/
                        }

                        /***** Hostel Branch *****/

                        System.out.println("hostelBranch.getID--->"+hostelBranch.getId());
                        System.out.println("mfeetranxDetails.getReceiptNo()-->"+mfeetranxDetails.getReceiptNo());
                        TranxJournalMaster mJournalhostel = tranxJournalMasterRepository.findByFeeReceiptNoAndStatusAndBranchId(mfeetranxDetails.getReceiptNo(), true, hostelBranch.getId());
                        /***** if Exist in Journal Master *****/
                        TranxJournalMaster tranxJournalMasternarration = null;
                        if (mJournalhostel != null) {
                            System.out.println("mJournalhostel.getId-->"+mJournalhostel.getId());

                            mJournalhostel.setTotalAmt(mJournalhostel.getTotalAmt() + mfeetranxDetails.getPaidAmount());
                            try {
                                tranxJournalMasternarration = tranxJournalMasterRepository.save(mJournalhostel);
                                response.setResponseStatus(HttpStatus.OK.value());

                                response.setMessage("Payment updated Successfully3");
                                System.out.println("Journal Master Created Successfully3");

                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                response.setMessage("Failed to update Payment3");
                                response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                            }
                            List<TranxJournalDetails> mHostelDetails = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndStatus(mJournalhostel.getId(), true);
                            for (TranxJournalDetails mDetails : mHostelDetails) {


                                mDetails.setPaidAmount(mDetails.getPaidAmount() + mfeetranxDetails.getPaidAmount());
                                try {
                                    tranxJournalDetailsRepository.save(mDetails);
                                    response.setResponseStatus(HttpStatus.OK.value());

                                    response.setMessage("Payment updated Successfully4");
                                    System.out.println("Journal Detail Created Successfully4");


                                } catch (Exception e1) {
                                    System.out.println(e1.getMessage());
                                    response.setMessage("Failed to update Payment4");
                                    response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                                }
                            }
//                            /***** End of suyash Branch****/
                        }else {
//                            /***** if not available in Journal Master : Create new Journal Master and Details ****/
                            createJournalMaster(hostelBranch.getOutlet(), hostelBranch, mfeetranxDetails, users);
                        }
                    }
                }
            }
        }


        return response;
    }


    private void createJournalMaster(Outlet outlet, Branch hostelBranch, FeesTransactionDetail mfeetranxDetails, Users users) {
        TranxJournalMaster journalMaster1 = new TranxJournalMaster();
//                            Branch branch = hostelBranch;
        TransactionTypeMaster tranxType = transactionTypeMasterRepository.findByTransactionCodeIgnoreCase("JRNL");

        LedgerMaster studenthosteledger = ledgerMasterRepository.findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(mfeetranxDetails.getFeesTransactionSummary().getStudentRegister().getId(), users.getOutlet().getId(), hostelBranch.getId(), true);
        if (studenthosteledger != null) {
        } else {
            studenthosteledger = createLedgerForStudentForHostel(hostelBranch, mfeetranxDetails.getFeesTransactionSummary().getStudentRegister(), users);
        }
        Long count1 = tranxJournalMasterRepository.findLastRecord(outlet.getId(), hostelBranch.getId());
        String serailNo1 = String.format("%05d", count1 + 1);// 5 digit serial number
        GenerateDates generateDates = new GenerateDates();
        String currentMonth1 = generateDates.getCurrentMonth().substring(0, 3);
        String jtCode1 = "JRNL" + currentMonth1 + serailNo1;
        journalMaster1.setOutlet(outlet);
        journalMaster1.setBranch(hostelBranch);
        journalMaster1.setFeeReceiptNo(mfeetranxDetails.getReceiptNo());
        journalMaster1.setTranscationDate(mfeetranxDetails.getTransactionDate());
        journalMaster1.setJournalSrNo(count1 + 1);
        journalMaster1.setJournalNo(jtCode1);
//                        journalMaster1.setTotalAmt(currentPaidAmount);
        journalMaster1.setTotalAmt(mfeetranxDetails.getPaidAmount());
//                            journalMaster1.setNarrations("Journal entry from student to DP Acc:" + hostelStudent.getId());
//        journalMaster1.setNarrations("New Journal Created against Transfer from Mess to Hostel ");

        String studname = mfeetranxDetails.getFeesTransactionSummary().getStudentRegister().getFirstName() + " " + mfeetranxDetails.getFeesTransactionSummary().getStudentRegister().getLastName();

        journalMaster1.setNarrations("Being online fee received Mess to Hostel from " + studname + " vide R.No. " + mfeetranxDetails.getReceiptNo()
                + " " + mfeetranxDetails.getFeesTransactionSummary().getBranch().getBranchName() + " STD." + mfeetranxDetails.getFeesTransactionSummary().getStandard().getStandardName());
//        journalMaster1.setNarrations(tranxJournalMasternarration.getNarrations());

        journalMaster1.setCreatedBy(users.getId());
        journalMaster1.setStatus(true);
        /*fiscal year mapping*/
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(LocalDate.now());
        if (fiscalYear != null) {
            journalMaster1.setFiscalYear(fiscalYear);
            journalMaster1.setFinancialYear(fiscalYear.getFiscalYear());
        }
        TranxJournalMaster tranxJournalMaster1 = tranxJournalMasterRepository.save(journalMaster1);
        try {
            /**** Tranx Journal Details for Student A/C *****/
            TranxJournalDetails tranxJournalDetails1 = new TranxJournalDetails();
            tranxJournalDetails1.setBranch(hostelBranch);
            tranxJournalDetails1.setOutlet(outlet);
            tranxJournalDetails1.setStatus(true);
            tranxJournalDetails1.setLedgerMaster(studenthosteledger);
            tranxJournalDetails1.setTranxJournalMaster(tranxJournalMaster1);
            tranxJournalDetails1.setType("CR");
            tranxJournalDetails1.setLedgerType(studenthosteledger.getSlugName());
            tranxJournalDetails1.setCreatedBy(users.getId());
            tranxJournalDetails1.setPaidAmount(mfeetranxDetails.getPaidAmount());
            tranxJournalDetails1.setStatus(true);
            TranxJournalDetails journalDetails = tranxJournalDetailsRepository.save(tranxJournalDetails1);
            insertIntoSDJournal(journalDetails, tranxType); //Accounting postings of Student Account
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            /* *** Tranx Journal Details for DP A/C *****/
            TranxJournalDetails tranxJournalDetails2 = new TranxJournalDetails();
            LedgerMaster DpLedgerMaster = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletIdAndBranchIdAndStatus("DP A/C", outlet.getId(), hostelBranch.getId(), true);
            tranxJournalDetails2.setBranch(hostelBranch);
            tranxJournalDetails2.setOutlet(outlet);
            tranxJournalDetails2.setStatus(true);
            tranxJournalDetails2.setLedgerMaster(DpLedgerMaster);
            tranxJournalDetails2.setTranxJournalMaster(tranxJournalMaster1);
            tranxJournalDetails2.setType("DR");
            tranxJournalDetails2.setLedgerType(DpLedgerMaster.getSlugName());
            tranxJournalDetails2.setCreatedBy(users.getId());
            tranxJournalDetails2.setPaidAmount(mfeetranxDetails.getPaidAmount());
            tranxJournalDetails2.setStatus(true);
            TranxJournalDetails journalDetails = tranxJournalDetailsRepository.save(tranxJournalDetails2);
            insertIntoDPJournal(journalDetails, tranxType);//Accounting postings of DP Account

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void insertIntoDPJournal(TranxJournalDetails journalDetails, TransactionTypeMaster tranxType) {
        try {
            System.out.println("transction Name + tranxtypeId-->" + tranxType.getTransactionName() + tranxType.getId());

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

    private void insertIntoSDJournal(TranxJournalDetails journalDetails, TransactionTypeMaster tranxType) {
        try {
            System.out.println("transction Name + tranxtypeId-->" + tranxType.getTransactionName() + tranxType.getId());
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

    private LedgerMaster createLedgerForStudentForHostel(Branch hostelBranch, StudentRegister studentRegister, Users users) {
        LedgerMaster ledgerMaster = new LedgerMaster();
        PrincipleGroups groups = null;
        Principles principles = null;
        Foundations foundations = null;
        LedgerMaster mLedger = null;
        principles = principleRepository.findByIdAndStatus(1, true);
        foundations = principles.getFoundations();

        groups = principleGroupsRepository.findByIdAndStatus(1, true);
        if (groups != null) {
            ledgerMaster.setPrincipleGroups(groups);
            ledgerMaster.setPrinciples(principles);
            ledgerMaster.setUniqueCode(groups.getUniqueCode());
        } else {
            ledgerMaster.setPrinciples(principles);
            ledgerMaster.setUniqueCode(principles.getUniqueCode());
        }
        if (foundations != null) {
            ledgerMaster.setFoundations(foundations);
        }
        ledgerMaster.setIsPrivate(false);
        ledgerMaster.setIsDeleted(false); //isDelete : true means , we can delete this ledger
        ledgerMaster.setStatus(true);
        ledgerMaster.setIsDefaultLedger(false);

        ledgerMaster.setBranch(hostelBranch);
        ledgerMaster.setOutlet(users.getOutlet());
        ledgerMaster.setCreatedBy(users.getId());

        String studName = studentRegister.getFirstName();
        if (studentRegister.getFatherName() != null)
            studName = studName + " " + studentRegister.getFatherName();
        if (studentRegister.getLastName() != null)
            studName = studName + " " + studentRegister.getLastName();
        ledgerMaster.setLedgerName(studName);
        ledgerMaster.setSlugName("sundry_debtors");
        ledgerMaster.setStudentRegister(studentRegister);
        ledgerMaster.setUnderPrefix("PG#1");

        ledgerMaster.setTaxType("NA");
        ledgerMaster.setMailingName(studName);
        ledgerMaster.setOpeningBalType("Dr");

        if (studentRegister.getCurrentAddress() != null) {
            ledgerMaster.setAddress(studentRegister.getCurrentAddress());
        }
        ledgerMaster.setPincode(0L);
        if (studentRegister.getEmailId() != null) {
            ledgerMaster.setEmail(studentRegister.getEmailId());
        } else {
            ledgerMaster.setEmail("NA");
        }
        ledgerMaster.setMobile(studentRegister.getMobileNo());
        ledgerMaster.setTaxable(false);
        ledgerMaster.setBankName("NA");
        ledgerMaster.setAccountNumber("NA");
        ledgerMaster.setIfsc("NA");
        ledgerMaster.setBankBranch("NA");
        ledgerMaster.setOpeningBal(0.0);

        /* pune demo visit changes */
        ledgerMaster.setCreditDays(0);
        ledgerMaster.setApplicableFrom("NA");
        ledgerMaster.setFoodLicenseNo("NA");

        LedgerMaster ledgerMaster1 = ledgerMasterRepository.save(ledgerMaster);
        return ledgerMaster1;
    }

    private void createTranxSalesInvoiceForHostelMess(Branch hostelBranch, StudentRegister studentInfo, Users users, Long ledgerId, double hostelFee, StudentAdmission studentAdmission1, FeeHead feeHead) {
        TranxSalesInvoice mSalesTranx = null;
        TransactionTypeMaster tranxType = transactionTypeMasterRepository.findByTransactionCodeIgnoreCase("feestr");
        /* save into sales invoices  */
        Outlet outlet = users.getOutlet();
        TranxSalesInvoice invoiceTranx = new TranxSalesInvoice();
        invoiceTranx.setBranch(hostelBranch);
        invoiceTranx.setOutlet(outlet);
        LocalDate date = LocalDate.now();
        invoiceTranx.setBillDate(studentAdmission1.getDateOfAdmission() != null ? studentAdmission1.getDateOfAdmission() : date);
        /* fiscal year mapping */
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(date);
        if (fiscalYear != null) {
            invoiceTranx.setFiscalYear(fiscalYear);
            invoiceTranx.setFinancialYear(fiscalYear.getFiscalYear());
        }
        /* End of fiscal year mapping */

        Long count = tranxSalesInvoiceRepository.findLastRecord(users.getOutlet().getId(), hostelBranch.getId());
        String serailNo = String.format("%05d", count + 1);
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
        String siCode = "SI" + currentMonth + serailNo;
        invoiceTranx.setSalesSerialNumber(Long.parseLong(serailNo));
        invoiceTranx.setSalesInvoiceNo(siCode);
//        LedgerMaster feesAc = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletIdAndBranchIdAndStatus(
//        "Fees A/c", users.getOutlet().getId(), studentInfo.getBranch().getId(), true);
        AssociateGroups feesAc = associateGroupsRepository.findByAssociatesNameIgnoreCaseAndOutletIdAndBranchIdAndStatus
                ("Fees Account", users.getOutlet().getId(), hostelBranch.getId(), true);
        LedgerMaster sundryDebtors = ledgerMasterRepository.findByIdAndStatus(ledgerId, true);
        invoiceTranx.setFeesAct(feesAc);
        invoiceTranx.setSundryDebtors(sundryDebtors);
        invoiceTranx.setTotalBaseAmount(hostelFee);
        invoiceTranx.setTotalAmount(hostelFee);
        invoiceTranx.setBalance(hostelFee);

        String studName = studentInfo.getFirstName();
        if (studentInfo.getFatherName() != null)
            studName = studName + " " + studentInfo.getFatherName();
        if (studentInfo.getLastName() != null)
            studName = studName + " " + studentInfo.getLastName();
        invoiceTranx.setNarration(studName + " registered and student unique number is " + studentInfo.getStudentUniqueNo());
        invoiceTranx.setCreatedBy(users.getId());
        invoiceTranx.setStatus(true);
        invoiceTranx.setOperations("inserted");

        try {
            mSalesTranx = tranxSalesInvoiceRepository.save(invoiceTranx);
            /* Save into Sales Duties and Taxes */
        } catch (DataIntegrityViolationException e) {
            System.out.println("Exception:" + e.getMessage());
            // throw new Exception(e.getMessage());
        } catch (Exception e1) {
            System.out.println("Exception:" + e1.getMessage());
            // throw new Exception(e1.getMessage());
        }

        if (mSalesTranx != null) {
            /** Accounting Postings  **/
            insertIntoTranxDetailSD(mSalesTranx, tranxType); //for Sundry Debtors or Students : dr
          /*  ledgerCommonPostings.callToPostings(
                    mSalesTranx.getTotalAmount(),
                    mSalesTranx.getSundryDebtors(),tranxType,mSalesTranx.getSundryDebtors().getAssociateGroups(),mSalesTranx.getFiscalYear(),
                    mSalesTranx.getBranch(),mSalesTranx.getOutlet(),mSalesTranx.getBillDate(),mSalesTranx.getId(),mSalesTranx.getSalesInvoiceNo(),
                    "DR",true,"Sales Invoice","create");*/
//            insertIntoTranxDetailFA(mSalesTranx, tranxType, feesMaster); // for Fees Accounts : cr
            try {
                hostelFeeLedgerPosting(studentInfo, hostelFee, mSalesTranx, tranxType, hostelBranch, feeHead);
            } catch (Exception ex) {
                ex.printStackTrace();
                studentLogger.error("Exception in insertIntoTranxDetailFA ->" + ex.getMessage());
            }
        }
    }

    private void insertIntoTranxDetailSD(TranxSalesInvoice mSalesTranx, TransactionTypeMaster tranxType) {
        try {

//            ledgerTransactionDetailsRepository.insertIntoLegerTranxDetail(
            ledgerTransactionDetailsRepository.insertIntoLegerTranxDetailWithTranxDate(
                    mSalesTranx.getSundryDebtors().getFoundations().getId(),
                    mSalesTranx.getSundryDebtors().getPrinciples().getId(), mSalesTranx.getSundryDebtors().getPrincipleGroups().getId(),
                    null, tranxType.getId(), null, mSalesTranx.getBranch() != null ? mSalesTranx.getBranch().getId() : null,
                    mSalesTranx.getOutlet().getId(), "pending", mSalesTranx.getTotalAmount() * -1, 0.0,
                    mSalesTranx.getBillDate() != null ? mSalesTranx.getBillDate() : null, null, mSalesTranx.getId(),
                    tranxType.getTransactionName() != null ? tranxType.getTransactionName() : null,
                    mSalesTranx.getSundryDebtors() != null ? mSalesTranx.getSundryDebtors().getUnderPrefix() : null,
                    mSalesTranx.getFinancialYear() != null ? mSalesTranx.getFinancialYear() : null, mSalesTranx.getCreatedBy(),
                    mSalesTranx.getSundryDebtors() != null ? mSalesTranx.getSundryDebtors().getId() : null,
                    mSalesTranx.getSalesInvoiceNo() != null ? mSalesTranx.getSalesInvoiceNo() : null, true);

            ledgerCommonPostings.callToPostings(
                    mSalesTranx.getTotalAmount() * -1,
                    mSalesTranx.getSundryDebtors() != null ? mSalesTranx.getSundryDebtors() : null,
                    tranxType, mSalesTranx.getSundryDebtors().getAssociateGroups() != null ? mSalesTranx.getSundryDebtors().getAssociateGroups() : null,
                    mSalesTranx.getFiscalYear() != null ? mSalesTranx.getFiscalYear() : null,
                    mSalesTranx.getBranch() != null ? mSalesTranx.getBranch() : null, mSalesTranx.getOutlet(),
                    mSalesTranx.getBillDate() != null ? mSalesTranx.getBillDate() : null, mSalesTranx.getId(),
                    mSalesTranx.getSalesInvoiceNo() != null ? mSalesTranx.getSalesInvoiceNo() : null,
                    "CR", true, "17", "create");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insertIntoTranxDetailSD Store Procedure Error " + e.getMessage());
        }
    }/* End of Posting into Sundry Debtors */

    /* Posting into Sales Accounts : Fees Heads*/
    private void insertIntoTranxDetailFA(StudentRegister studentRegister, TranxSalesInvoice mSalesTranx, TransactionTypeMaster tranxType, FeesMaster feesMaster) {
        try {
            StudentAdmission studentAdmission = studentAdmissionRepository.findByStudentRegisterIdAndStatus(studentRegister.getId(), true);
            List<FeesDetails> feesDetailsList = feesDetailsRepository.findByFeesMasterIdAndStatusAndSubFeeHeadNull(feesMaster.getId(), true);
            for (FeesDetails feesDetails : feesDetailsList) {

                if (!feesDetails.getFeeHead().getFeeHeadName().toLowerCase().contains("hostel") && !feesDetails.getFeeHead().getFeeHeadName().toLowerCase().contains("bus")
                        && !feesDetails.getFeeHead().getFeeHeadName().toLowerCase().contains("vacation")) {
                    double fees = 0;
                    if (feesMaster.getStudentType() == 2 && (feesMaster.getStandard().getStandardName().equalsIgnoreCase("11")
                            || feesMaster.getStandard().getStandardName().equalsIgnoreCase("12")) && studentRegister.getGender().equalsIgnoreCase("male")) {
                        fees = feesDetails.getAmountForBoy();
                    } else if (feesMaster.getStudentType() == 2 && (feesMaster.getStandard().getStandardName().equalsIgnoreCase("11")
                            || feesMaster.getStandard().getStandardName().equalsIgnoreCase("12")) && studentRegister.getGender().equalsIgnoreCase("female")) {
                        fees = feesDetails.getAmountForGirl();
                    } else {
                        fees = feesDetails.getAmount();
                    }

                    LedgerTransactionDetails ledgerTransactionDetails = ledgerTransactionDetailsRepository.findByTransactionIdAndLedgerMasterIdAndStatus(
                            mSalesTranx.getId(), feesDetails.getFeeHead().getLedger().getId(), true);

                    if (ledgerTransactionDetails == null) {
//                        ledgerTransactionDetailsRepository.insertIntoLegerTranxDetail(
                        ledgerTransactionDetailsRepository.insertIntoLegerTranxDetailWithTranxDate(
                                mSalesTranx.getFeesAct().getFoundations().getId(),
                                mSalesTranx.getFeesAct().getPrinciples().getId(),
                                mSalesTranx.getFeesAct().getPrincipleGroups() != null ? mSalesTranx.getFeesAct().getPrincipleGroups().getId() : null,
                                null, tranxType.getId(), null,
                                mSalesTranx.getBranch() != null ? mSalesTranx.getBranch().getId() : null,
                                mSalesTranx.getOutlet().getId(), "pending", 0.0, fees,
                                mSalesTranx.getBillDate() != null ? mSalesTranx.getBillDate() : null, null,
                                mSalesTranx.getId(), tranxType.getTransactionName() != null ? tranxType.getTransactionName() : null,
                                mSalesTranx.getFeesAct() != null ? mSalesTranx.getFeesAct().getUnder_prefix() : null,
                                mSalesTranx.getFinancialYear() != null ? mSalesTranx.getFinancialYear() : null, mSalesTranx.getCreatedBy(),
                                feesDetails.getFeeHead().getLedger().getId(), mSalesTranx.getSalesInvoiceNo() != null ? mSalesTranx.getSalesInvoiceNo() : null, true);


                        ledgerCommonPostings.callToPostings(
                                fees,
                                feesDetails.getFeeHead().getLedger(), tranxType, feesDetails.getFeeHead().getLedger().getAssociateGroups(), mSalesTranx.getFiscalYear() != null ? mSalesTranx.getFiscalYear() : null,
                                mSalesTranx.getBranch(), mSalesTranx.getOutlet(), mSalesTranx.getBillDate(), mSalesTranx.getId(), mSalesTranx.getSalesInvoiceNo(),
                                "CR", true, tranxType.getTransactionName(), "create");
                    } else {
                        try {
                            ledgerTransactionDetailsRepository.ledgerPostingEdit(
                                    feesDetails.getFeeHead().getLedger().getId(), mSalesTranx.getId(), 17L, "CR", fees);
                        } catch (Exception e) {
                            e.printStackTrace();
                            studentLogger.error("Exception => while saving ledgerPostingEdit in student transport" + e);
                        }
                    }

                    /******* Insert into Sales Invoice Details against Fees Head *****/
                    TranxSalesInvoiceDetails invoiceDetails = new TranxSalesInvoiceDetails();
                    invoiceDetails.setAmount(fees);
                    if (mSalesTranx.getBranch() != null)
                        invoiceDetails.setBranch(mSalesTranx.getBranch());
                    invoiceDetails.setOutlet(mSalesTranx.getOutlet());
                    if (mSalesTranx.getFiscalYear() != null)
                        invoiceDetails.setFiscalYear(mSalesTranx.getFiscalYear());
                    invoiceDetails.setTranxSalesInvoice(mSalesTranx);
                    invoiceDetails.setFeeHead(feesDetails.getFeeHead());
                    invoiceDetails.setStatus(true);
                    try {
                        tranxSalesInvoiceDetailsRepository.save(invoiceDetails);
                    } catch (Exception ex) {
                        studentLogger.error("Exception in insertIntoTranxDetailFA ->" + ex.getMessage());
                    }
                } else if (feesDetails.getFeeHead().getFeeHeadName().toLowerCase().contains("vacation") && studentAdmission.getIsVacation()) { // if student applicable of vacation
                    double fees = 0;
                    if (feesMaster.getStudentType() == 2 && (feesMaster.getStandard().getStandardName().equalsIgnoreCase("11") ||
                            feesMaster.getStandard().getStandardName().equalsIgnoreCase("12")) && studentRegister.getGender().equalsIgnoreCase("male")) {
                        fees = feesDetails.getAmountForBoy();
                    } else if (feesMaster.getStudentType() == 2 && (feesMaster.getStandard().getStandardName().equalsIgnoreCase("11") ||
                            feesMaster.getStandard().getStandardName().equalsIgnoreCase("12")) && studentRegister.getGender().equalsIgnoreCase("female")) {
                        fees = feesDetails.getAmountForGirl();
                    } else {
                        fees = feesDetails.getAmount();
                    }

                    LedgerTransactionDetails ledgerTransactionDetails = ledgerTransactionDetailsRepository.findByTransactionIdAndLedgerMasterIdAndStatus(
                            mSalesTranx.getId(), feesDetails.getFeeHead().getLedger().getId(), true);

                    if (ledgerTransactionDetails == null) {
//                        ledgerTransactionDetailsRepository.insertIntoLegerTranxDetail(
                        ledgerTransactionDetailsRepository.insertIntoLegerTranxDetailWithTranxDate(
                                mSalesTranx.getFeesAct().getFoundations().getId(),
                                mSalesTranx.getFeesAct().getPrinciples().getId(),
                                mSalesTranx.getFeesAct().getPrincipleGroups() != null ? mSalesTranx.getFeesAct().getPrincipleGroups().getId() : null,
                                null, tranxType.getId(), null,
                                mSalesTranx.getBranch() != null ? mSalesTranx.getBranch().getId() : null,
                                mSalesTranx.getOutlet().getId(), "pending", 0.0, fees,
                                mSalesTranx.getBillDate() != null ? mSalesTranx.getBillDate() : null, null,
                                mSalesTranx.getId(), tranxType.getTransactionName() != null ? tranxType.getTransactionName() : null,
                                mSalesTranx.getFeesAct() != null ? mSalesTranx.getFeesAct().getUnder_prefix() : null,
                                mSalesTranx.getFinancialYear() != null ? mSalesTranx.getFinancialYear() : null, mSalesTranx.getCreatedBy(),
                                feesDetails.getFeeHead().getLedger().getId(), mSalesTranx.getSalesInvoiceNo() != null ? mSalesTranx.getSalesInvoiceNo() : null, true);

                    } else {
                        try {
                            ledgerTransactionDetailsRepository.ledgerPostingEdit(
                                    feesDetails.getFeeHead().getLedger().getId(), mSalesTranx.getId(), 17L, "CR", fees);
                        } catch (Exception e) {
                            e.printStackTrace();
                            studentLogger.error("Exception => while saving ledgerPostingEdit in student transport" + e);
                        }
                    }

                    /******* Insert into Sales Invoice Details against Fees Head *****/
                    TranxSalesInvoiceDetails invoiceDetails = new TranxSalesInvoiceDetails();
                    invoiceDetails.setAmount(fees);
                    if (mSalesTranx.getBranch() != null)
                        invoiceDetails.setBranch(mSalesTranx.getBranch());
                    invoiceDetails.setOutlet(mSalesTranx.getOutlet());
                    if (mSalesTranx.getFiscalYear() != null)
                        invoiceDetails.setFiscalYear(mSalesTranx.getFiscalYear());
                    invoiceDetails.setTranxSalesInvoice(mSalesTranx);
                    invoiceDetails.setFeeHead(feesDetails.getFeeHead());
                    invoiceDetails.setStatus(true);
                    try {
                        tranxSalesInvoiceDetailsRepository.save(invoiceDetails);
                    } catch (Exception ex) {
                        studentLogger.error("Exception in insertIntoTranxDetailFA ->" + ex.getMessage());
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in fee heads posting " + e.getMessage());
            studentLogger.error("Exception in insertIntoTranxDetailFA ->" + e.getMessage());
        }

    }

    public void hostelFeeLedgerPosting(StudentRegister studentInfo, double hostelfee, TranxSalesInvoice mSalesTranx, TransactionTypeMaster tranxType, Branch hostelBranch, FeeHead feeHead) {
//        String feesDetails = feesDetailsRepository.checkHostelFeeHeadData(feesMaster.getId());
//        if (feesDetails != null) {
//            String[] details = feesDetails.split(",");
//
//            FeeHead feeHead = feeHeadRepository.findByIdAndStatus(Long.parseLong(details[3]), true);
//            double fees = 0;
//            if (feesMaster.getStudentType() == 2 && (feesMaster.getStandard().getStandardName().equalsIgnoreCase("11") ||
//                    feesMaster.getStandard().getStandardName().equalsIgnoreCase("12"))
//                    && studentInfo.getGender().equalsIgnoreCase("male")) {
//                fees = Double.parseDouble(details[9]);
//            } else if (feesMaster.getStudentType() == 2 && (feesMaster.getStandard().getStandardName().equalsIgnoreCase("11") ||
//                    feesMaster.getStandard().getStandardName().equalsIgnoreCase("12"))
//                    && studentInfo.getGender().equalsIgnoreCase("female")) {
//                fees = Double.parseDouble(details[10]);
//            } else {
//                fees = Double.parseDouble(details[8]);
//            }
        try {
            ledgerTransactionDetailsRepository.insertIntoLegerTranxDetailWithTranxDate(
                    mSalesTranx.getFeesAct().getFoundations().getId(),
                    mSalesTranx.getFeesAct().getPrinciples().getId(),
                    mSalesTranx.getFeesAct().getPrincipleGroups() != null ? mSalesTranx.getFeesAct().getPrincipleGroups().getId() : null,
                    null, tranxType.getId(), null, hostelBranch.getId(),
                    mSalesTranx.getOutlet().getId(), "pending", 0.0, hostelfee,
                    mSalesTranx.getBillDate() != null ? mSalesTranx.getBillDate() : null, null,
                    mSalesTranx.getId(), tranxType.getTransactionName() != null ? tranxType.getTransactionName() : null,
                    mSalesTranx.getFeesAct() != null ? mSalesTranx.getFeesAct().getUnder_prefix() : null,
                    mSalesTranx.getFinancialYear() != null ? mSalesTranx.getFinancialYear() : null, mSalesTranx.getCreatedBy(),
                    feeHead.getLedger().getId(), mSalesTranx.getSalesInvoiceNo() != null ? mSalesTranx.getSalesInvoiceNo() : null, true);

//        System.out.println(hostelfee);
//        System.out.println(feeHead.getLedger().getId());
//        System.out.println(tranxType.getTransactionName());
//        System.out.println(feeHead.getLedger().getAssociateGroups());
//        System.out.println(mSalesTranx.getFiscalYear().getFiscalYear());
//        System.out.println(mSalesTranx.getBranch());
//        System.out.println(mSalesTranx.getId());
//        System.out.println(mSalesTranx.getSalesInvoiceNo());
            ledgerCommonPostings.callToPostings(
                    hostelfee,
                    feeHead.getLedger() != null ? feeHead.getLedger() : null, tranxType, feeHead.getLedger().getAssociateGroups() != null ? feeHead.getLedger().getAssociateGroups() : null,
                    mSalesTranx.getFiscalYear() != null ? mSalesTranx.getFiscalYear() : null,
                    mSalesTranx.getBranch() != null ? mSalesTranx.getBranch() : null, mSalesTranx.getOutlet(),
                    mSalesTranx.getBillDate() != null ? mSalesTranx.getBillDate() : null, mSalesTranx.getId(), mSalesTranx.getSalesInvoiceNo() != null ? mSalesTranx.getSalesInvoiceNo() : null,
                    "CR", true, tranxType.getTransactionName() != null ? tranxType.getTransactionName() : null, "create");


            /******* Insert into Sales Invoice Details against Fees Head *****/
            TranxSalesInvoiceDetails invoiceDetails = new TranxSalesInvoiceDetails();
            invoiceDetails.setAmount(hostelfee);
            if (mSalesTranx.getBranch() != null)
                invoiceDetails.setBranch(mSalesTranx.getBranch());
            invoiceDetails.setOutlet(mSalesTranx.getOutlet());
            if (mSalesTranx.getFiscalYear() != null)
                invoiceDetails.setFiscalYear(mSalesTranx.getFiscalYear());
            invoiceDetails.setTranxSalesInvoice(mSalesTranx);
            invoiceDetails.setFeeHead(feeHead);
            invoiceDetails.setStatus(true);
            try {
                tranxSalesInvoiceDetailsRepository.save(invoiceDetails);
            } catch (Exception ex) {
                ex.printStackTrace();
                studentLogger.error("Exception in insertIntoTranxDetailFA ->" + ex.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            studentLogger.error("Exception in callToPostings ->" + ex.getMessage());
        }
    }


    public JsonObject getFeeHeadById(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        try {
            FeeHead feeHead = feeHeadRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (feeHead != null) {
                response.addProperty("id", feeHead.getId());
                response.addProperty("feeHeadName", feeHead.getFeeHeadName());
                response.addProperty("studentType", feeHead.getStudentType());
                response.addProperty("branchId", feeHead.getBranch().getId());
                response.addProperty("branchName", feeHead.getBranch().getBranchName());
                response.addProperty("underBranchId", feeHead.getUnderBranch().getId());
                response.addProperty("underBranchName", feeHead.getUnderBranch().getBranchName());
                response.addProperty("ledgerId", feeHead.getLedger() != null ? feeHead.getLedger().getId() : 0);
                response.addProperty("ledgerName", feeHead.getLedger() != null ? feeHead.getLedger().getLedgerName() : "");
                response.addProperty("isReceiptCurrentBranch", feeHead.getIsReceiptCurrentBranch());

                result.addProperty("message", "success");
                result.addProperty("responseStatus", HttpStatus.OK.value());
                result.add("responseObject", response);
            } else {
                result.addProperty("message", "Data not found");
                result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            feeHeadLogger.error("updateFeeHead -> failed to update fee head " + e.getMessage());
            response.addProperty("message", "Failed to update fee head");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

    public JsonObject getFeeHeadsByBranch(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Long branchId = Long.valueOf(request.getParameter("branchId"));
            Integer studentType = Integer.valueOf(request.getParameter("studentType"));
            List<FeeHead> list = feeHeadRepository.findByBranchIdAndStudentTypeAndStatus(branchId, studentType, true);
            if (list.size() > 0) {
                for (FeeHead mGroup : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mGroup.getId());
                    response.addProperty("feeHeadName", mGroup.getFeeHeadName());
                    response.addProperty("isReceiptCurrentBranch", mGroup.getIsReceiptCurrentBranch());

                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            feeHeadLogger.error("getFeeHeadByBranch -> get fee heads by branch " + e.getMessage());
            res.addProperty("message", "empty list");
            res.addProperty("responseStatus", HttpStatus.OK.value());
        }
        return res;
    }

    public JsonObject getFeeHeads(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Long outletId = users.getOutlet().getId();
            List<FeeHead> list = feeHeadRepository.findByOutletIdAndStatus(outletId, true);
//            if (list.size() > 0)
            System.out.println("getFeeHeads --->>>>>>>>>>>>>>>> ");
            {
                for (FeeHead mGroup : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mGroup.getId());
                    response.addProperty("isReceiptCurrentBranch", mGroup.getIsReceiptCurrentBranch());

                    response.addProperty("feeHeadName", mGroup.getFeeHeadName());
//                    response.addProperty("studentType", mGroup.getStudentType());
                    if (mGroup.getStudentType() == 1) {
                        response.addProperty("studentType", "Day Scholar");
                    } else if (mGroup.getStudentType() == 2) {
                        response.addProperty("studentType", "Residential");
                    }
                    response.addProperty("outletName", mGroup.getOutlet().getCompanyName());
                    response.addProperty("branchId", mGroup.getBranch().getId());
                    response.addProperty("branchName", mGroup.getBranch().getBranchName());
                    response.addProperty("underBranchId", mGroup.getUnderBranch().getId());
                    response.addProperty("underBranchName", mGroup.getUnderBranch().getBranchName());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            feeHeadLogger.error("getAllFeeHead -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }

}



