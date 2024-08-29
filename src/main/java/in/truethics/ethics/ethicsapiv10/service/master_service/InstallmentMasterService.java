package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.school_master.*;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionDetail;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionSummary;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.*;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.FeesTransactionDetailRepository;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.FeesTransactionSummaryRepository;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.apache.commons.math3.util.Precision;
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
public class InstallmentMasterService {
    private static final Logger installmentLogger = LoggerFactory.getLogger(InstallmentMasterService.class);
    @Autowired
    private InstallmentMasterRepository installmentMasterRepository;

    @Autowired
    private InstallmentDetailsRepository installmentDetailsRepository;
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private FeeHeadRepository feeHeadRepository;
    @Autowired
    private SubFeeHeadRepository subFeeHeadRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private FeesMasterRepository feesMasterRepository;
    @Autowired
    private InstallmentDetailsRepository detailsRepository;
    @Autowired
    private FeesDetailsRepository feesDetailsRepository;
    @Autowired
    private FeesTransactionSummaryRepository feesTransactionSummaryRepository;
    @Autowired
    private StudentRegisterRepository studentRegisterRepository;
    @Autowired
    private FeesTransactionDetailRepository feesTransactionDetailRepository;
    @Autowired
    private StudentAdmissionRepository studentAdmissionRepository;

    public JsonObject createInstallmentMaster(HttpServletRequest request) {
        JsonObject response = new JsonObject();

        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Branch branch = branchRepository.findByIdAndStatus(Long.parseLong(request.getParameter("branchId")), true);
            FeesMaster feesMaster = feesMasterRepository.findByIdAndStatus(Long.parseLong(request.getParameter("feesMasterId")), true);

            System.out.println("feesMaster " + feesMaster.getId());
            int noOfInstallments = Integer.valueOf(request.getParameter("noOfInstallments"));

            for (int j = 0; j < noOfInstallments; j++) {
                InstallmentMaster installmentMaster = new InstallmentMaster();
                installmentMaster.setOutlet(users.getOutlet());
                installmentMaster.setBranch(branch);
                installmentMaster.setStatus(true);
                String eDate = request.getParameter("expiryDate_" + (j + 1));
                System.out.println("eDate " + eDate);
                if (eDate != null && eDate.equalsIgnoreCase(""))
                    installmentMaster.setExpiryDate(LocalDate.parse(eDate));
                installmentMaster.setFeesMaster(feesMaster);
                installmentMaster.setConcessionType(Integer.valueOf(request.getParameter("concessionType")));
                installmentMaster.setStudentType(Integer.valueOf(request.getParameter("studentType")));

                if (request.getParameterMap().containsKey("studentGroup")) {
                    installmentMaster.setStudentGroup(Integer.valueOf(request.getParameter("studentGroup")));
                }
                installmentMaster.setInstallmentNo(Integer.valueOf(j + 1));
                InstallmentMaster installmentMaster1 = installmentMasterRepository.save(installmentMaster);

                double amount = 0;
                double boyAmount = 0;
                double girlAmount = 0;
                String jsonStr = request.getParameter("row");
                JsonParser parser = new JsonParser();
                JsonArray row = parser.parse(jsonStr).getAsJsonArray();
                System.out.println("J =>" + j);
                for (int i = 0; i < row.size(); i++) {
                    JsonObject feesRow = row.get(i).getAsJsonObject();

                    InstallmentDetails installmentDetails = new InstallmentDetails();
                    installmentDetails.setStatus(true);
                    installmentDetails.setInstallmentMaster(installmentMaster1);
                    installmentDetails.setPriority(feesRow.get("priority").getAsDouble());

                    if (feesRow.get("isSubHead").getAsInt() == 0) {
                        FeeHead feeHead = feeHeadRepository.findByIdAndStatus(feesRow.get("paymentHeadId").getAsLong(), true);
                        installmentDetails.setFeeHead(feeHead);
                    } else {
                        FeeHead feeHead = feeHeadRepository.findByIdAndStatus(feesRow.get("feeHeadId").getAsLong(), true);
                        installmentDetails.setFeeHead(feeHead);
                        SubFeeHead subFeeHead = subFeeHeadRepository.findByIdAndStatus(feesRow.get("paymentHeadId").getAsLong(), true);
                        installmentDetails.setSubFeeHead(subFeeHead);
                    }

                    if (feesMaster.getStudentType() == 2 && (feesMaster.getStandard().getStandardName().equalsIgnoreCase("11") ||
                            feesMaster.getStandard().getStandardName().equalsIgnoreCase("12"))) {
                        installmentDetails.setBoysAmount(feesRow.get("boys" + j).getAsDouble());
                        installmentDetails.setGirlsAmount(feesRow.get("girls" + j).getAsDouble());

                        boyAmount = boyAmount + feesRow.get("boys" + j).getAsDouble();
                        girlAmount = girlAmount + feesRow.get("girls" + j).getAsDouble();
                    } else {
                        installmentDetails.setAmount(feesRow.get("amt" + j).getAsDouble());
                        amount = amount + feesRow.get("amt" + j).getAsDouble();
                    }
                    installmentDetails.setOutlet(users.getOutlet());
                    installmentDetails.setBranch(branch);
                    detailsRepository.save(installmentDetails);
                }

                installmentMaster1.setFeesAmount(amount);
                installmentMaster1.setBoysAmount(boyAmount);
                installmentMaster1.setGirlsAmount(girlAmount);

                installmentMasterRepository.save(installmentMaster1);
            }
            response.addProperty("message", "Installment created successfully");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            /*} catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception " + e.getMessage());
                installmentLogger.error("createInstallmentMaster -> failed to create installment master " + e.getMessage());
                response.addProperty("message", "Failed to create installment master");
                response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());
            installmentLogger.error("createInstallmentMaster -> failed to create installment master " + e.getMessage());
            response.addProperty("message", "Failed to create installment master");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }


    public JsonObject getInstallmentMasters(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));

            JsonArray result = new JsonArray();
            Long outletId = users.getOutlet().getId();
            List<InstallmentMaster> list = new ArrayList<>();
            if (users.getBranch() != null) {
                list = installmentMasterRepository.getListByOutletIdAndBranchIdAndStatus(outletId, users.getBranch().getId(), true);
            } else {
                list = installmentMasterRepository.getListByOutletIdAndStatus(outletId, true);
            }
            for (InstallmentMaster master : list) {
                JsonObject response = new JsonObject();
                if (master.getFeesMaster().getStudentType() == 1) {
                    response.addProperty("studentType", "Day Scholar");
                } else if (master.getFeesMaster().getStudentType() == 2) {
                    response.addProperty("studentType", "Residential");
                }

                if (master.getFeesMaster().getStudentGroup() != null && master.getFeesMaster().getStudentGroup() == 1) {
                    response.addProperty("studentGroup", "PCM");
                } else if (master.getFeesMaster().getStudentGroup() != null && master.getFeesMaster().getStudentGroup() == 2) {
                    response.addProperty("studentGroup", "PCB");
                }

                if (master.getConcessionType() == 1) {
                    response.addProperty("concessionType", "No Concession");
                } else if (master.getConcessionType() == 2) {
                    response.addProperty("concessionType", "3000 Concession");
                } else if (master.getConcessionType() == 4) {
                    response.addProperty("concessionType", "2000 Concession");
                } else if (master.getConcessionType() == 3) {
                    response.addProperty("concessionType", "1000 Concession");
                }
                response.addProperty("id", master.getId());
                response.addProperty("feesMasterId", master.getFeesMaster().getId());
                response.addProperty("concType", master.getConcessionType());
                response.addProperty("outletName", master.getOutlet().getCompanyName());
                response.addProperty("branchName", master.getBranch().getBranchName());
                response.addProperty("academicYear", master.getFeesMaster().getAcademicYear().getYear());
                response.addProperty("standard", master.getFeesMaster().getStandard().getStandardName());
//                response.addProperty("division", master.getFeesMaster().getDivision().getDivisionName());
//                response.addProperty("installmentNo", master.getInstallmentNo());
               /* response.addProperty("totalResidentialAmount", master.getTotalResidentialAmount());
                response.addProperty("totalDayScholarAmount", master.getTotalDayScholarAmount());*/
                result.add(response);
            }
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());
            installmentLogger.error("getInstallmentMasters -> failed to load installment master " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.OK.value());
        }
        return res;
    }


    public JsonObject getInstallmentMasterByFilter(HttpServletRequest request) {

        JsonObject res = new JsonObject();
        JsonArray result = new JsonArray();
        Map<String, String[]> paramMap = request.getParameterMap();
        Long standardId = 0L;
        Long academicYearId = 0L;
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));

            Long branchId = users.getBranch().getId();
            if (paramMap.containsKey("standardId")) standardId = Long.valueOf(request.getParameter("standardId"));
            if (paramMap.containsKey("academicYearId"))
                academicYearId = Long.valueOf(request.getParameter("academicYearId"));
            Long id = users.getOutlet().getId();
            List<InstallmentMaster> list = new ArrayList<>();

            if (academicYearId != 0 && standardId != 0) {
                list = installmentMasterRepository.findInstallmentMasterByAcademicYearAndStandardAndStatus(id, branchId, academicYearId, standardId, true);


            } else if (academicYearId != 0) {
                list = installmentMasterRepository.findInstallmentMasterByAcademicYearStatus(id, branchId, academicYearId, true);
            } else if (standardId != 0) {
                list = installmentMasterRepository.findInstallmentMasterByStandardStatus(id, branchId, standardId, true);
            } else {
                System.out.println("last else executing");
                list = installmentMasterRepository.getListByOutletIdAndBranchIdAndStatus(id, branchId, true);

            }

            for (InstallmentMaster master : list) {
                JsonObject response = new JsonObject();
                if (master.getStudentType() == 1) {
                    response.addProperty("studentType", "Day Scholar");
                } else if (master.getStudentType() == 2) {
                    response.addProperty("studentType", "Residential");
                }

                if (master.getStudentGroup() != null && master.getStudentGroup() == 1) {
                    response.addProperty("studentGroup", "PCM");
                } else if (master.getStudentGroup() != null && master.getStudentGroup() == 2) {
                    response.addProperty("studentGroup", "PCB");
                }

                if (master.getConcessionType() == 1) {
                    response.addProperty("concessionType", "No Concession");
                } else if (master.getConcessionType() == 2) {
                    response.addProperty("concessionType", "3000 Concession");
                } else if (master.getConcessionType() == 4) {
                    response.addProperty("concessionType", "2000 Concession");
                } else if (master.getConcessionType() == 3) {
                    response.addProperty("concessionType", "1000 Concession");
                }
                response.addProperty("id", master.getId());
                response.addProperty("feesMasterId", master.getFeesMaster().getId());
                response.addProperty("concType", master.getConcessionType());
                response.addProperty("outletName", master.getOutlet().getCompanyName());
                response.addProperty("branchName", master.getBranch().getBranchName());
                response.addProperty("academicYear", master.getFeesMaster().getAcademicYear().getYear());
                response.addProperty("standard", master.getFeesMaster().getStandard().getStandardName());
//                response.addProperty("division", master.getFeesMaster().getDivision().getDivisionName());
//                response.addProperty("installmentNo", master.getInstallmentNo());
               /* response.addProperty("totalResidentialAmount", master.getTotalResidentialAmount());
                response.addProperty("totalDayScholarAmount", master.getTotalDayScholarAmount());*/
                result.add(response);
            }
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());
            installmentLogger.error("getInstallmentMasters -> failed to load installment master " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.OK.value());

        }

        return res;
    }


    public JsonObject getConcessionsByInstallment(HttpServletRequest request) {
        JsonObject responseMessage = new JsonObject();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Long studentInfoId = Long.valueOf(request.getParameter("studentId"));
        StudentRegister studentInfo = studentRegisterRepository.findByIdAndStatus(studentInfoId, true);
        Long feesMasterId = Long.valueOf(request.getParameter("feesMasterId"));
        FeesMaster feesMaster = feesMasterRepository.findByIdAndStatus(feesMasterId, true);

        FeesTransactionSummary transactionSummary = feesTransactionSummaryRepository.findByFeesMasterIdAndStudentRegisterIdAndOutletId(feesMasterId, studentInfoId, users.getOutlet().getId());
//        System.out.println("transactionSummary >>>>>>>>>>>>>>>>"+transactionSummary.getIsManual());
        int installmentNo = 0;
        long transactionId = 0;
        if (transactionSummary != null) {
            transactionId = transactionSummary.getId();
            JsonArray concessionArray = new JsonArray();
            if (transactionSummary.getPaidAmount() > 0) {
                System.out.println("fees paid in progress");

                FeesTransactionDetail feesTransactionDetail = feesTransactionDetailRepository.findTop1ByFeesTransactionSummaryIdOrderByIdDesc(transactionId);

                    if (feesMaster.getNoOfInstallment() >= feesTransactionDetail.getInstallmentNo()) {
                    InstallmentMaster installmentMaster = installmentMasterRepository.findTop1ByFeesMasterIdAndConcessionTypeAndFeesAmountGreaterThanEqualOrderByIdDesc(
                            feesMasterId, transactionSummary.getConcessionType(), transactionSummary.getBalance());
                    if (installmentMaster != null) {
                        installmentNo = installmentMaster.getInstallmentNo();
                    } else {
                        installmentNo = feesTransactionDetail.getInstallmentNo() + 1;
                    }
                    List<Object[]> concessionList = installmentMasterRepository.findConcessionByInstallment(installmentNo, feesMasterId);
                    for (int i = 0; i < concessionList.size(); i++) {
                        Object[] obj = concessionList.get(i);

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("concessionTypeValue", obj[0].toString());
                        if (obj[0].toString().equalsIgnoreCase("1")) {
                            jsonObject.addProperty("concessionType", "No Concession");
                        } else if (obj[0].toString().equalsIgnoreCase("2")) {
                            jsonObject.addProperty("concessionType", "3000 Concession");
                        } else if (obj[0].toString().equalsIgnoreCase("4")) {
                            jsonObject.addProperty("concessionType", "2000 Concession");
                        } else if (obj[0].toString().equalsIgnoreCase("3")) {
                            jsonObject.addProperty("concessionType", "1000 Concession");
                        }
                        concessionArray.add(jsonObject);
                    }
                } else {
                    installmentNo = feesTransactionDetail.getInstallmentNo();
                }
            } else {
                installmentNo = 1;
                List<Object[]> concessionList = installmentMasterRepository.findConcessionByInstallment(installmentNo, feesMasterId);
                for (int i = 0; i < concessionList.size(); i++) {
                    Object[] obj = concessionList.get(i);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("concessionTypeValue", obj[0].toString());
                    if (obj[0].toString().equalsIgnoreCase("1")) {
                        jsonObject.addProperty("concessionType", "No Concession");
                    } else if (obj[0].toString().equalsIgnoreCase("2")) {
                        jsonObject.addProperty("concessionType", "3000 Concession");
                    } else if (obj[0].toString().equalsIgnoreCase("4")) {
                        jsonObject.addProperty("concessionType", "2000 Concession");
                    } else if (obj[0].toString().equalsIgnoreCase("3")) {
                        jsonObject.addProperty("concessionType", "1000 Concession");
                    }
                    concessionArray.add(jsonObject);
                }
//                transactionId = 0;
            }

            JsonArray installArray = new JsonArray();
            for (int i = 0; i < feesMaster.getNoOfInstallment(); i++) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("label", (i + 1));
                jsonObject.addProperty("value", (i + 1));
                installArray.add(jsonObject);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("value", 0);
            jsonObject.addProperty("label", "Manual");
            installArray.add(jsonObject);

            responseMessage.addProperty("isManual", transactionSummary.getIsManual());
            responseMessage.addProperty("concessionType", transactionSummary.getConcessionType());
            responseMessage.addProperty("transactionId", transactionId);
            responseMessage.addProperty("installmentNo", installmentNo);
            responseMessage.add("installArray", installArray);
            responseMessage.add("responseObject", concessionArray);
            responseMessage.addProperty("responseStatus", HttpStatus.OK.value());
        } else {
            responseMessage.addProperty("message", "Data not found.");
            responseMessage.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return responseMessage;
    }

    public JsonObject getDetailsByInstallment(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Long feesMasterId = Long.valueOf(request.getParameter("feesMasterId"));
            Long studentId = Long.valueOf(request.getParameter("studentId"));
            Integer concessionType = Integer.valueOf(request.getParameter("concessionNo"));
            Integer installmentNo = Integer.valueOf(request.getParameter("installmentNo"));
            Long transactionId = Long.valueOf(request.getParameter("transactionId"));
            boolean applyConcession = Boolean.parseBoolean(request.getParameter("applyConcession"));

            FeesMaster feesMaster = feesMasterRepository.findByIdAndStatus(feesMasterId, true);

            StudentAdmission studentRegister = studentAdmissionRepository.findByStudentRegisterIdAndStatus(studentId, true);

            /*double studBusFee = 8000.0;
            double busInstall = studBusFee / 2;*/
            System.out.println("studentId =" + studentId + " feesMaster.getAcademicYear().getId() -> " + feesMaster.getAcademicYear().getId());
            String studBusFee = feesMasterRepository.getStudentBusFeeByStudentIdAndAcademic(studentId, feesMaster.getAcademicYear().getId());
            System.out.println("studBusFee =" + studBusFee);
//            double studBusFee = 8000.0;
            double busInstall = (studBusFee != null) ? Double.parseDouble(studBusFee) / 2 : 0;
            System.out.println(" busInstall -> " + busInstall);

            StudentAdmission studentAdmission = studentAdmissionRepository.findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndStudentRegisterIdAndStudentTypeAndStatus(
                    users.getOutlet().getId(), users.getBranch().getId(), feesMaster.getAcademicYear().getId(), feesMaster.getStandard().getId(),studentId, feesMaster.getStudentType(), true);


            JsonArray masterArray = new JsonArray();
//            OLD => List<InstallmentMaster> installmentMasterList = installmentMasterRepository.findByFeesMasterIdAndOutletId(feesMasterId, users.getOutlet().getId());
//          NEW =>
            double payable = 0;
            String sumOfInstallmentArray = installmentMasterRepository.getTotalInstallmentAmountByInstallmentNoAndConcessionNo(feesMasterId, concessionType, installmentNo);
            if (sumOfInstallmentArray != null) {
                String[] installmentSum = sumOfInstallmentArray.split(",");

                if (feesMaster.getStudentGroup() != null && feesMaster.getStudentGroup() == 1 &&
                        (feesMaster.getStandard().getStandardName().equalsIgnoreCase("11") ||
                                feesMaster.getStandard().getStandardName().equalsIgnoreCase("12"))) {
                    if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("male")) {
                        payable = Double.parseDouble(installmentSum[1]);
                    } else if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("female")) {
                        payable = Double.parseDouble(installmentSum[2]);
                    }
                } else {
                    payable = Double.parseDouble(installmentSum[0]);
                }
            }
            List<InstallmentMaster> installmentMasterList = installmentMasterRepository.findByConcessionTypeAndFeesMasterIdAndOutletId(concessionType, feesMasterId, users.getOutlet().getId());
            for (InstallmentMaster installmentMst : installmentMasterList) {

                if (installmentNo >= installmentMst.getInstallmentNo()) {
                    InstallmentMaster installmentMaster = installmentMasterRepository.findByFeesMasterIdAndConcessionTypeAndInstallmentNo(feesMasterId, concessionType, installmentMst.getInstallmentNo());

                    JsonObject masterObj = new JsonObject();
                    masterObj.addProperty("installmentNo", installmentMaster.getInstallmentNo());
                    if (installmentMaster != null && studentAdmission != null) {
                        List<InstallmentDetails> installmentDetails = detailsRepository.findByInstallmentMasterId(installmentMaster.getId());

                        JsonArray detailsArray = new JsonArray();
                        double totalFee = 0;
                        double totalPaid = 0;
                        double totalCPaid = 0;
                        double totalBalance = 0;
                        for (InstallmentDetails installmentDetails1 : installmentDetails) {
                            JsonObject payheadObj = new JsonObject();
                            FeesTransactionDetail feesTransactionDetail = null;
                            double headFee = 0;
                            double paid = 0;
                            double cPaid = 0;
                            double balance = 0;
                            String prevPaid = "";

                            if (installmentDetails1.getSubFeeHead() != null) {
                                payheadObj.addProperty("payHeadId", installmentDetails1.getSubFeeHead().getId());
                                payheadObj.addProperty("payHeadName", installmentDetails1.getSubFeeHead().getSubFeeHeadName());

                                FeesDetails feesDetails = feesDetailsRepository.findByFeesMasterIdAndSubFeeHeadId(installmentMaster.getFeesMaster().getId(), installmentDetails1.getSubFeeHead().getId());
                                payheadObj.addProperty("priority", feesDetails.getPriority());
                                payheadObj.addProperty("isSubHead", "1");

                                feesTransactionDetail = feesTransactionDetailRepository.findTop1ByFeesTransactionSummaryIdAndSubFeeHeadIdAndInstallmentNoOrderByIdDesc(transactionId, installmentDetails1.getSubFeeHead().getId(), installmentMaster.getInstallmentNo());
                            } else {
                                FeesDetails feesDetails = feesDetailsRepository.findByFeesMasterIdAndFeeHeadId(installmentMaster.getFeesMaster().getId(), installmentDetails1.getFeeHead().getId());
                                payheadObj.addProperty("payHeadId", installmentDetails1.getFeeHead().getId());
                                payheadObj.addProperty("payHeadName", installmentDetails1.getFeeHead().getFeeHeadName());
                                payheadObj.addProperty("priority", feesDetails.getPriority());
                                payheadObj.addProperty("isSubHead", "0");

                                feesTransactionDetail = feesTransactionDetailRepository.findTop1ByFeesTransactionSummaryIdAndFeeHeadIdAndInstallmentNoOrderByIdDesc(transactionId, installmentDetails1.getFeeHead().getId(), installmentMaster.getInstallmentNo());
                            }

                            if (feesTransactionDetail != null) { // if records exist in DB

                                if (feesTransactionDetail.getBalance() > 0) {

                                    System.out.println("feesTransactionDetail.id() ->>" + feesTransactionDetail.getId());
                                    System.out.println("feesTransactionDetail.getFeeHead() ->>" + feesTransactionDetail.getFeeHead().getFeeHeadName());

                                    /* If student set to particular bus route then it checks bus fee exist in fees master  */
//                                    System.out.println("feesTransactionDetail.getFeeHead() ->>" + feesTransactionDetail.getFeeHead().getFeeHeadName());
                                    boolean isFound = feesTransactionDetail.getFeeHead().getFeeHeadName().toLowerCase().contains("bus"); // true
                                    System.out.println("Bus isFound => " + isFound);
                                    if (isFound) {
                                        headFee = busInstall;
                                    } else {
                                        headFee = feesTransactionDetail.getHeadFee();
                                    }
                                    System.out.println("headFee => " + headFee);

                                    double prevBalance = feesTransactionDetail.getBalance();
//                                    prevBalance = headFee - feesTransactionDetail.getPaidAmount();
                                    prevBalance = headFee - feesTransactionDetail.getAmount();
                                    System.out.println("payable =>" + payable);
                                    if (payable > 0) {
                                        if (payable >= prevBalance) {
                                            paid = feesTransactionDetail.getAmount() + prevBalance;
                                            balance = 0;
                                            payable = payable - prevBalance;
                                            prevPaid = feesTransactionDetail.getAmount() + " + " + prevBalance;
                                            cPaid = prevBalance;
                                        } else {
                                            paid = feesTransactionDetail.getAmount() + payable;
                                            balance = prevBalance - payable;
                                            System.out.println("payable is less than 0 " + payable);
                                            prevPaid = feesTransactionDetail.getAmount() + " + " + payable;
                                            cPaid = payable;
                                            payable = 0;
                                        }
                                    } else {
                                        System.out.println("2 -> payable is less than 0 " + payable);
                                        paid = 0;
                                        balance = prevBalance;
                                        prevPaid = feesTransactionDetail.getAmount() + " + " + prevBalance;
//                                        cPaid = prevBalance;
                                        cPaid = 0;
                                    }
                                    System.out.println("prevPaid " + prevPaid);
                                } else if (feesTransactionDetail.getBalance() == 0) {

                                    double installmentAmt = installmentDetails1.getAmount();
                                    /* If student set to particular bus route then it checks bus fee exist in fees master  */
                                    System.out.println("feesTransactionDetail.getFeeHead() ->>" + feesTransactionDetail.getFeeHead().getFeeHeadName());
                                    boolean isFound = feesTransactionDetail.getFeeHead().getFeeHeadName().toLowerCase().contains("bus"); // true
                                    System.out.println("Bus isFound => " + isFound);
                                    if (isFound) {
                                        installmentAmt = busInstall;
                                    }

                                    if (installmentAmt > feesTransactionDetail.getHeadFee()) {
                                        headFee = installmentAmt;
                                        double prevBalance = installmentAmt
                                                - feesTransactionDetail.getAmount();
                                        if (payable > 0) {
                                            if (payable >= prevBalance) {
                                                paid = feesTransactionDetail.getAmount() + prevBalance;
                                                balance = 0;
                                                payable = payable - prevBalance;
                                                prevPaid = feesTransactionDetail.getAmount() + " + " + prevBalance;
                                                cPaid = prevBalance;
                                            } else {
                                                paid = feesTransactionDetail.getAmount() + payable;
                                                balance = prevBalance - payable;
                                                System.out.println("payable is less than 0 " + payable);
                                                prevPaid = feesTransactionDetail.getAmount() + " + " + payable;
                                                cPaid = payable;
                                                payable = 0;
                                            }
                                        } else {
                                            System.out.println("payable is less than 0 " + payable);
                                            paid = 0;
                                            balance = prevBalance;
                                            prevPaid = feesTransactionDetail.getAmount() + " + " + prevBalance;
                                            cPaid = prevBalance;
                                        }
                                        System.out.println("prevPaid " + prevPaid);
                                    } else {
                                        headFee = feesTransactionDetail.getHeadFee();
                                        paid = feesTransactionDetail.getAmount();
                                        balance = 0;

                                        prevPaid = feesTransactionDetail.getAmount() + "";
                                        cPaid = 0;
                                    }
                                } else {
                                    headFee = feesTransactionDetail.getHeadFee();
                                    paid = feesTransactionDetail.getAmount();
                                    balance = 0;

                                    prevPaid = feesTransactionDetail.getAmount() + "";
                                    cPaid = 0;
                                }


                                totalFee = totalFee + paid;
                                totalPaid = totalFee;
                                totalBalance = totalFee - totalPaid;
                                totalCPaid = totalCPaid + cPaid;
                                payheadObj.addProperty("totalFees", totalFee);
                                payheadObj.addProperty("prevPaid", prevPaid);
                                payheadObj.addProperty("paid", cPaid);
                                payheadObj.addProperty("cPaid", cPaid);
                                payheadObj.addProperty("balance", 0);
                                payheadObj.addProperty("installmentPer", "100");
                                payheadObj.addProperty("concession", 0);

                                detailsArray.add(payheadObj);
                            } else {

                                payheadObj.addProperty("paid", installmentDetails1.getAmount());
                                if (installmentMaster.getStudentType() == 2 && (feesMaster.getStandard().getStandardName().equalsIgnoreCase("11") ||
                                        feesMaster.getStandard().getStandardName().equalsIgnoreCase("12"))) {
                                    if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("male")) {
                                        payheadObj.addProperty("paid", installmentDetails1.getBoysAmount());
                                    } else if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("female")) {
                                        payheadObj.addProperty("paid", installmentDetails1.getGirlsAmount());
                                    }
                                }

                                boolean isFound = payheadObj.get("payHeadName").getAsString().toLowerCase().contains("bus"); // true
                                System.out.println("isFound => " + isFound);
                                if (isFound) {
                                    payheadObj.addProperty("paid", busInstall);
                                }


                                /* If student set to vacation applicable then it checks vacation fee exist in fees master  */
                                boolean isVacationFound = payheadObj.get("payHeadName").getAsString().toLowerCase().contains("vacation"); // true
                                System.out.println("feestrans is null ---- studentAdmission.getIsVacation() --------->>>>" + studentAdmission.getIsVacation() + " isVacationFound => " + isVacationFound);
                                if (isVacationFound) {
                                    if (studentAdmission.getIsVacation() == true && isVacationFound == true) {
                                        System.out.println("condition true");
                                        if (installmentMaster.getStudentType() == 2 && (feesMaster.getStandard().getStandardName().equalsIgnoreCase("11") ||
                                                feesMaster.getStandard().getStandardName().equalsIgnoreCase("12"))) {
                                            if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("male")) {
                                                headFee = installmentDetails1.getBoysAmount();
                                            } else if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("female")) {
                                                headFee = installmentDetails1.getGirlsAmount();
                                            }
                                        } else {
                                            headFee = installmentDetails1.getAmount();
                                        }

                                        payheadObj.addProperty("paid", headFee);
                                    } else {
                                        headFee = 0;
                                        System.out.println(" payable ===> " + payable);
                                        balance = payable;
                                        System.out.println(" balance ===> " + balance);
                                        paid = 0;
                                        System.out.println(" paid ===> " + paid);
                                        cPaid = paid;
                                        payheadObj.addProperty("paid", headFee);

                                        installmentNo = 2; // if vacation not exists then shows next installment data
                                    }
                                }


                                prevPaid = payheadObj.get("paid").getAsDouble() + "";

                                totalFee = totalFee + payheadObj.get("paid").getAsDouble();
                                totalPaid = totalFee;
                                totalBalance = totalFee - totalPaid;
                                totalCPaid = totalCPaid + payheadObj.get("paid").getAsDouble();
                                payheadObj.addProperty("totalFees", payheadObj.get("paid").getAsDouble());
                                payheadObj.addProperty("prevPaid", prevPaid);
                                payheadObj.addProperty("cPaid", payheadObj.get("paid").getAsDouble());
                                payheadObj.addProperty("balance", 0);
                                payheadObj.addProperty("installmentPer", "100");
                                payheadObj.addProperty("concession", 0);

                                detailsArray.add(payheadObj);
                            }
                        }
                        masterObj.addProperty("totalFee", totalFee);
                        masterObj.addProperty("totalPaid", totalPaid);
                        masterObj.addProperty("totalCPaid", totalCPaid);
                        masterObj.addProperty("totalBalance", totalBalance);
                        masterObj.add("installmentData", detailsArray);
                    }
                    masterArray.add(masterObj);
                }
            }

            FeesTransactionSummary transactionSummary = feesTransactionSummaryRepository.findByFeesMasterIdAndStudentRegisterId(feesMasterId, studentId);
            if (transactionSummary != null) {
                res.addProperty("outstanding", transactionSummary.getBalance());
            }
            res.add("responseObject", masterArray);
            res.addProperty("responseStatus", HttpStatus.OK.value());

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());
            installmentLogger.error("getDetailsByInstallment -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.OK.value());
        }
        return res;
    }

    public JsonObject getDetailsByInstallmentForManual(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Long transactionId = Long.valueOf(request.getParameter("transactionId"));
            Long feesMasterId = Long.valueOf(request.getParameter("feesMasterId"));
            Long studentId = Long.valueOf(request.getParameter("studentId"));
            Integer concessionType = Integer.valueOf(request.getParameter("concessionNo")) != 0 ? Integer.valueOf(request.getParameter("concessionNo"))
                    : Integer.valueOf(1);
            Integer installmentNo = Integer.valueOf(request.getParameter("installmentNo"));
//            Integer installmentNo = 2;
            double payable = Integer.valueOf(request.getParameter("payable"));
            boolean applyConcession = Boolean.parseBoolean(request.getParameter("applyConcession"));

            StudentAdmission studentRegister = studentAdmissionRepository.findByStudentRegisterIdAndStatus(studentId, true);

//            OLD =>
//            List<InstallmentMaster> installmentMasterList = installmentMasterRepository.findByFeesMasterIdAndOutletId(feesMasterId, users.getOutlet().getId());
            //          NEW =>
            FeesTransactionSummary feesTransactionSummary = feesTransactionSummaryRepository.findByFeesMasterIdAndStudentRegisterIdAndOutletId(feesMasterId, studentId, users.getOutlet().getId());
            double studentPaidAmount = feesTransactionSummary.getPaidAmount();
            System.out.println(" studentPaidAmount -> " + studentPaidAmount);

            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<< payable "+payable);
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<< feesTransactionSummary.getBalance() "+feesTransactionSummary.getBalance());
            /*if(payable == feesTransactionSummary.getBalance()) {
                payable += feesTransactionSummary.getConcessionAmount();
            }
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<< payable "+payable);*/

            System.out.println("studentId =" + studentId + " feesTransactionSummary.getAcademicYear().getId() -> " + feesTransactionSummary.getAcademicYear().getId());
            String studBusFee = feesMasterRepository.getStudentBusFeeByStudentIdAndAcademic(studentId, feesTransactionSummary.getAcademicYear().getId());
            System.out.println("studBusFee =" + studBusFee);
//            double studBusFee = 8000.0;
            double busInstall = (studBusFee != null) ? Double.parseDouble(studBusFee) / 2 : 0;
            System.out.println(" busInstall -> " + busInstall);

            StudentAdmission studentAdmission = studentAdmissionRepository.findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndDivisionIdAndStudentRegisterIdAndStudentTypeAndStatus(
                    users.getOutlet().getId(), users.getBranch().getId(), feesTransactionSummary.getAcademicYear().getId(), feesTransactionSummary.getStandard().getId(), feesTransactionSummary.getDivision().getId(),
                    studentId, feesTransactionSummary.getStudentType(), true);

            JsonArray masterArray = new JsonArray();
            if (feesTransactionSummary != null) {
                res.addProperty("outstanding", feesTransactionSummary.getBalance());
            }
            double installAmount = 0;
            List<InstallmentMaster> installmentMasterList = installmentMasterRepository.findByConcessionTypeAndFeesMasterIdAndOutletIdOrderByInstallmentNoAsc(
                    concessionType, feesMasterId, users.getOutlet().getId());
            for (InstallmentMaster installmentMaster : installmentMasterList) {

                if (installmentMaster != null && studentAdmission != null) {

                    installAmount += installAmount + installmentMaster.getFeesAmount();

                    if (installmentMaster.getStudentType() == 2 && (feesTransactionSummary.getStandard().getStandardName().equalsIgnoreCase("11") ||
                            feesTransactionSummary.getStandard().getStandardName().equalsIgnoreCase("12"))) {
                        if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("male")) {
                            installAmount += installAmount + installmentMaster.getBoysAmount();
                        } else if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("female")) {
                            installAmount += installAmount + installmentMaster.getGirlsAmount();
                        }
                    }

                    System.out.println("payable => " + payable + " installAmount => " + installAmount);

                    double totalFee = 0;
                    double totalPaid = 0;
                    double totalCPaid = 0;
                    double totalBalance = 0;

                    if (payable > 0 && totalPaid <= installAmount) {
                        JsonObject masterObj = new JsonObject();
                        masterObj.addProperty("installmentNo", installmentMaster.getInstallmentNo());
                        List<InstallmentDetails> installmentDetails = detailsRepository.findByInstallmentMasterId(installmentMaster.getId());

                        JsonArray detailsArray = new JsonArray();

                        for (InstallmentDetails installmentDetails1 : installmentDetails) {
                            JsonObject payheadObj = new JsonObject();

                            FeesTransactionDetail feesTransactionDetail = null;

                            double headFee = 0;
                            double paid = 0;
                            double cPaid = 0;
                            double balance = 0;
                            String prevPaid = "";
                            if (installmentDetails1.getSubFeeHead() != null) {
                                payheadObj.addProperty("payHeadId", installmentDetails1.getSubFeeHead().getId());
                                payheadObj.addProperty("payHeadName", installmentDetails1.getSubFeeHead().getSubFeeHeadName());

                                FeesDetails feesDetails = feesDetailsRepository.findByFeesMasterIdAndSubFeeHeadId(installmentMaster.getFeesMaster().getId(), installmentDetails1.getSubFeeHead().getId());
                                payheadObj.addProperty("priority", feesDetails.getPriority());
                                payheadObj.addProperty("isSubHead", "1");

                                feesTransactionDetail = feesTransactionDetailRepository.findTop1ByFeesTransactionSummaryIdAndSubFeeHeadIdAndInstallmentNoOrderByIdDesc(transactionId, installmentDetails1.getSubFeeHead().getId(), installmentMaster.getInstallmentNo());

                            } else {
                                FeesDetails feesDetails = feesDetailsRepository.findByFeesMasterIdAndFeeHeadId(installmentMaster.getFeesMaster().getId(), installmentDetails1.getFeeHead().getId());
                                payheadObj.addProperty("payHeadId", installmentDetails1.getFeeHead().getId());
                                payheadObj.addProperty("payHeadName", installmentDetails1.getFeeHead().getFeeHeadName());
                                payheadObj.addProperty("priority", feesDetails.getPriority());
                                payheadObj.addProperty("isSubHead", "0");

                                feesTransactionDetail = feesTransactionDetailRepository.findTop1ByFeesTransactionSummaryIdAndFeeHeadIdAndInstallmentNoOrderByIdDesc(transactionId, installmentDetails1.getFeeHead().getId(), installmentMaster.getInstallmentNo());
                            }

                            if (feesTransactionDetail != null) { // if records exist in DB

                                System.out.println("feesTransactionDetail.getId()() ->>" + feesTransactionDetail.getId());
                                if (feesTransactionDetail.getBalance() > 0) {

                                    System.out.println("feesTransactionDetail.getFeeHead() ->>" + feesTransactionDetail.getFeeHead().getFeeHeadName());

                                    /* If student set to particular bus route then it checks bus fee exist in fees master  */
                                    System.out.println("feesTransactionDetail.getFeeHead() ->>" + feesTransactionDetail.getFeeHead().getFeeHeadName());
                                    boolean isFound = feesTransactionDetail.getFeeHead().getFeeHeadName().toLowerCase().contains("bus"); // true
                                    System.out.println("Bus isFound => " + isFound);
                                    if (isFound) {
                                        headFee = busInstall;
                                    } else {
                                        headFee = feesTransactionDetail.getHeadFee();
                                    }
                                    System.out.println("headFee => " + headFee);

                                    double prevBalance = feesTransactionDetail.getBalance();
                                    System.out.println(" prevBalance :------------------------------" + prevBalance);
                                    if (applyConcession) {
                                        if (isFound) {
                                            headFee = busInstall;
                                        } else {
                                            headFee = installmentDetails1.getAmount();
                                        }
//                                        prevBalance = headFee - feesTransactionDetail.getPaidAmount();
                                        prevBalance = headFee - feesTransactionDetail.getAmount();
                                    }

                                    System.out.println("payable =>" + payable +" prevBalance :------------------------------" + prevBalance);
                                    if (payable > 0) {
                                        if (payable >= prevBalance) {
                                            paid = feesTransactionDetail.getAmount() + prevBalance;
                                            balance = 0;
                                            payable = payable - prevBalance;
                                            prevPaid = feesTransactionDetail.getAmount() + " + " + prevBalance;
                                            cPaid = prevBalance;
                                        } else {
                                            paid = feesTransactionDetail.getAmount() + payable;
                                            balance = prevBalance - payable;
                                            System.out.println("payable is less than 0 " + payable);
                                            prevPaid = feesTransactionDetail.getAmount() + " + " + payable;
                                            cPaid = payable;
                                            payable = 0;
                                        }
                                    } else {
                                        System.out.println("2 -> payable is less than 0 " + payable);
                                        paid = 0;
                                        balance = prevBalance;
                                        prevPaid = feesTransactionDetail.getAmount() + " + " + prevBalance;
//                                        cPaid = prevBalance;
                                        cPaid = 0;
                                    }
                                    System.out.println("prevPaid " + prevPaid);
                                }
                                else if (feesTransactionDetail.getBalance() == 0 && applyConcession) {

                                    double installmentAmt = installmentDetails1.getAmount();
                                    /* If student set to particular bus route then it checks bus fee exist in fees master  */
                                    System.out.println("feesTransactionDetail.getFeeHead() ->>" + feesTransactionDetail.getFeeHead().getFeeHeadName());
                                    boolean isFound = feesTransactionDetail.getFeeHead().getFeeHeadName().toLowerCase().contains("bus"); // true
                                    System.out.println("Bus isFound => " + isFound);
                                    if (isFound) {
                                        installmentAmt = busInstall;
                                    }

                                    if (installmentAmt > feesTransactionDetail.getHeadFee()) {
                                        headFee = installmentAmt;
                                        double prevBalance = installmentAmt
                                                - feesTransactionDetail.getAmount(); // 6000-3000 = 3000
                                        System.out.println(">>>>>>>>>>>>> payable "+payable +" prevBalance "+prevBalance);
                                        if (payable > 0) {
                                            if (payable >= prevBalance) {
                                                paid = feesTransactionDetail.getAmount() + prevBalance; // 3000+3000 = 6000
                                                balance = 0;
                                                payable = payable - prevBalance;
                                                prevPaid = feesTransactionDetail.getAmount() + " + " + prevBalance;
                                                cPaid = prevBalance;
                                            } else {
                                                paid = feesTransactionDetail.getAmount() + payable; // 6000+1000 = 4000
                                                balance = prevBalance - payable; // 3000-1000 = 2000
                                                System.out.println("payable is less than 0 " + payable);
                                                prevPaid = feesTransactionDetail.getAmount() + " + " + payable;
                                                cPaid = payable;
                                                payable = 0;
                                            }
                                        } else {
                                            System.out.println("payable is less than 0 " + payable);
                                            paid = 0;
                                            balance = prevBalance;
                                            prevPaid = feesTransactionDetail.getAmount() + " + " + prevBalance;
                                            cPaid = prevBalance;
                                        }
                                        System.out.println("prevPaid " + prevPaid);
                                    } else {
                                        headFee = feesTransactionDetail.getHeadFee();
                                        paid = feesTransactionDetail.getAmount();
                                        balance = 0;

                                        prevPaid = feesTransactionDetail.getAmount() + "";
                                        cPaid = 0;
                                    }
                                }
                                else {
                                    headFee = feesTransactionDetail.getHeadFee();
                                    paid = feesTransactionDetail.getAmount();
                                    balance = 0;

                                    prevPaid = feesTransactionDetail.getAmount() + "";
                                    cPaid = 0;
                                }
                            }
                            else {

                                /* If student set to particular bus route then it checks bus fee exist in fees master  */
                                boolean isFound = payheadObj.get("payHeadName").getAsString().toLowerCase().contains("bus"); // true
                                System.out.println("Bus isFound => " + isFound);
                                if (isFound) {
                                    headFee = busInstall;
                                    System.out.println(" payable ===> " + payable);
                                    balance = headFee - payable;
                                    System.out.println(" balance ===> " + balance);
                                    paid = payable;
                                    System.out.println(" paid ===> " + paid);
                                    cPaid = paid;
                                } else {
                                    if (installmentMaster.getStudentType() == 2 && (feesTransactionSummary.getStandard().getStandardName().equalsIgnoreCase("11") ||
                                            feesTransactionSummary.getStandard().getStandardName().equalsIgnoreCase("12"))) {
                                        if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("male")) {
                                            headFee = installmentDetails1.getBoysAmount();
                                        } else if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("female")) {
                                            headFee = installmentDetails1.getGirlsAmount();
                                        }
                                    } else {
                                        headFee = installmentDetails1.getAmount();
                                    }

                                }

                                /* If student set to vacation applicable then it checks vacation fee exist in fees master  */
                                boolean isVacationFound = payheadObj.get("payHeadName").getAsString().toLowerCase().contains("vacation"); // true
                                System.out.println("feestrans is null ---- studentAdmission.getIsVacation() --------->>>>" + studentAdmission.getIsVacation() + "isVacationFound => " + isVacationFound);
                                if (isVacationFound) {
                                    if (studentAdmission.getIsVacation() == true && isVacationFound == true) {
                                        System.out.println("condition true");
                                        if (installmentMaster.getStudentType() == 2 && (feesTransactionSummary.getStandard().getStandardName().equalsIgnoreCase("11") ||
                                                feesTransactionSummary.getStandard().getStandardName().equalsIgnoreCase("12"))) {
                                            if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("male")) {
                                                headFee = installmentDetails1.getBoysAmount();
                                            } else if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("female")) {
                                                headFee = installmentDetails1.getGirlsAmount();
                                            }
                                        } else {
                                            headFee = installmentDetails1.getAmount();
                                        }
                                    } else {
                                        headFee = 0;
                                        System.out.println(" payable ===> " + payable);
                                        balance = payable;
                                        System.out.println(" balance ===> " + balance);
                                        paid = 0;
                                        System.out.println(" paid ===> " + paid);
                                        cPaid = paid;
                                    }
                                }


                                System.out.println("headFee >>>>>>>>>>>>>>>>>>>>>>" + headFee);

                                if (payable > 0) {
                                    if (payable >= headFee) {
                                        paid = headFee;
                                        balance = 0;
                                        payable = payable - headFee;

                                        cPaid = paid;
                                    } else {
                                        paid = payable;
                                        balance = headFee - payable;
                                        payable = 0;
                                        cPaid = paid;
                                    }
                                } else {
                                    System.out.println("payable is less than 0 " + payable);
                                    paid = 0;
                                    balance = headFee;
                                    cPaid = paid;
                                }

                                prevPaid = paid + "";
                            }

                            System.out.println(" headFee ===> " + headFee);
                            totalFee = totalFee + headFee;
                            totalPaid = totalPaid + paid;
                            totalCPaid = totalCPaid + cPaid;
                            totalBalance = totalBalance + balance;
                            payheadObj.addProperty("totalFees", headFee);
                            payheadObj.addProperty("paid", paid);
                            payheadObj.addProperty("balance", balance);
                            payheadObj.addProperty("installmentPer", "100");
                            payheadObj.addProperty("concession", 0);
                            payheadObj.addProperty("prevPaid", prevPaid);
                            payheadObj.addProperty("cPaid", cPaid);

                            detailsArray.add(payheadObj);
                            studentPaidAmount = studentPaidAmount + cPaid;

                        }
                        masterObj.addProperty("totalFee", totalFee);
                        masterObj.addProperty("totalPaid", totalPaid);
                        masterObj.addProperty("totalCPaid", totalCPaid);
                        masterObj.addProperty("totalBalance", totalBalance);
                        masterObj.add("installmentData", detailsArray);

                        masterArray.add(masterObj);

                        System.out.println(" studentPaidAmount -> " + studentPaidAmount);
                    }
                }
            }


            System.out.println("studentPaidAmount " + studentPaidAmount);
            res.addProperty("applyConcession", false);
            if (feesTransactionSummary.getConcessionType() == null || feesTransactionSummary.getConcessionType() == 0) {
                List<InstallmentMaster> installmentMasters = installmentMasterRepository.getListForConcessionCheck(feesMasterId, 1);
                for (InstallmentMaster installmentMaster : installmentMasters) {
                    if (studentPaidAmount >= installmentMaster.getFeesAmount()) {
                        res.addProperty("applyConcession", true);
                    }
                }
            }

            /*FeesTransactionSummary transactionSummary = feesTransactionSummaryRepository.findByFeesMasterIdAndStudentRegisterId(feesMasterId, studentId);
            if (transactionSummary != null) {
                res.addProperty("outstanding", transactionSummary.getBalance());
            }*/
            res.add("responseObject", masterArray);
            res.addProperty("responseStatus", HttpStatus.OK.value());

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());
            installmentLogger.error("getDetailsByInstallment -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.OK.value());
        }
        return res;
    }

    public JsonObject getDetailsByInstallmentForManualbkp(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Long transactionId = Long.valueOf(request.getParameter("transactionId"));
            Long feesMasterId = Long.valueOf(request.getParameter("feesMasterId"));
            Long studentId = Long.valueOf(request.getParameter("studentId"));
            Integer concessionType = Integer.valueOf(1);
            Integer installmentNo = Integer.valueOf(request.getParameter("installmentNo"));
//            Integer installmentNo = 2;
            double payable = Integer.valueOf(request.getParameter("payable"));

            StudentAdmission studentRegister = studentAdmissionRepository.findByStudentRegisterIdAndStatus(studentId, true);

            JsonArray masterArray = new JsonArray();
//            OLD =>
//            List<InstallmentMaster> installmentMasterList = installmentMasterRepository.findByFeesMasterIdAndOutletId(feesMasterId, users.getOutlet().getId());
            //          NEW =>
            List<InstallmentMaster> installmentMasterList = installmentMasterRepository.findByConcessionTypeAndFeesMasterIdAndOutletId(concessionType, feesMasterId, users.getOutlet().getId());
            for (InstallmentMaster installmentMst : installmentMasterList) {

                if (installmentNo >= installmentMst.getInstallmentNo()) {
                    InstallmentMaster installmentMaster = installmentMasterRepository.findByFeesMasterIdAndConcessionTypeAndInstallmentNo(feesMasterId, concessionType, installmentMst.getInstallmentNo());

                    JsonObject masterObj = new JsonObject();
                    masterObj.addProperty("installmentNo", installmentMaster.getInstallmentNo());
                    if (installmentMaster != null) {
                        List<InstallmentDetails> installmentDetails = detailsRepository.findByInstallmentMasterId(installmentMaster.getId());

                        JsonArray detailsArray = new JsonArray();
                        double totalFee = 0;
                        double totalPaid = 0;
                        double totalBalance = 0;
                        for (InstallmentDetails installmentDetails1 : installmentDetails) {
                            JsonObject payheadObj = new JsonObject();

                            FeesTransactionDetail feesTransactionDetail = null;

                            double headFee = 0;
                            double paid = 0;
                            double cPaid = 0;
                            double balance = 0;
                            String prevPaid = "";
                            if (installmentDetails1.getSubFeeHead() != null) {
                                payheadObj.addProperty("payHeadId", installmentDetails1.getSubFeeHead().getId());
                                payheadObj.addProperty("payHeadName", installmentDetails1.getSubFeeHead().getSubFeeHeadName());

                                FeesDetails feesDetails = feesDetailsRepository.findByFeesMasterIdAndSubFeeHeadId(installmentMaster.getFeesMaster().getId(), installmentDetails1.getSubFeeHead().getId());
                                payheadObj.addProperty("priority", feesDetails.getPriority());
                                payheadObj.addProperty("isSubHead", "1");

                                feesTransactionDetail = feesTransactionDetailRepository.findTop1ByFeesTransactionSummaryIdAndSubFeeHeadIdAndInstallmentNoOrderByIdDesc(transactionId, installmentDetails1.getSubFeeHead().getId(), installmentMst.getInstallmentNo());

                            } else {
                                FeesDetails feesDetails = feesDetailsRepository.findByFeesMasterIdAndFeeHeadId(installmentMaster.getFeesMaster().getId(), installmentDetails1.getFeeHead().getId());
                                payheadObj.addProperty("payHeadId", installmentDetails1.getFeeHead().getId());
                                payheadObj.addProperty("payHeadName", installmentDetails1.getFeeHead().getFeeHeadName());
                                payheadObj.addProperty("priority", feesDetails.getPriority());
                                payheadObj.addProperty("isSubHead", "0");

                                feesTransactionDetail = feesTransactionDetailRepository.findTop1ByFeesTransactionSummaryIdAndFeeHeadIdAndInstallmentNoOrderByIdDesc(transactionId, installmentDetails1.getFeeHead().getId(), installmentMst.getInstallmentNo());
                            }

                            if (feesTransactionDetail != null) { // if records exist in DB

                                if (feesTransactionDetail.getBalance() > 0) {

                                    headFee = feesTransactionDetail.getHeadFee();
                                    double prevBalance = feesTransactionDetail.getBalance();
                                    if (payable > 0) {
//                                        if (payable >= headFee) {
                                        if (payable >= prevBalance) {
                                            paid = feesTransactionDetail.getAmount() + prevBalance;
                                            balance = 0;
                                            payable = payable - prevBalance;
                                            prevPaid = feesTransactionDetail.getAmount() + " + " + prevBalance;
                                            cPaid = prevBalance;
                                        } else {
                                            paid = feesTransactionDetail.getAmount() + payable;
                                            balance = prevBalance - payable;
                                            System.out.println("payable is less than 0 " + payable);
                                            prevPaid = feesTransactionDetail.getAmount() + " + " + payable;
                                            cPaid = payable;
                                            payable = 0;
                                        }
                                    } else {
                                        System.out.println("payable is less than 0 " + payable);
                                        paid = 0;
                                        balance = prevBalance;
                                        prevPaid = feesTransactionDetail.getAmount() + " + " + prevBalance;
                                        cPaid = prevBalance;
                                    }
                                    System.out.println("prevPaid " + prevPaid);
                                } else {
                                    headFee = feesTransactionDetail.getHeadFee();
                                    paid = feesTransactionDetail.getAmount();
                                    balance = 0;

                                    prevPaid = feesTransactionDetail.getAmount() + "";
                                    cPaid = 0;
                                }
                            } else {
                                if (installmentMaster.getStudentType() == 2 && (studentRegister.getStandard().getStandardName().equalsIgnoreCase("11") ||
                                        studentRegister.getStandard().getStandardName().equalsIgnoreCase("12"))) {
                                    if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("male")) {
                                        headFee = installmentDetails1.getBoysAmount();
                                    } else if (studentRegister.getStudentRegister().getGender().equalsIgnoreCase("female")) {
                                        headFee = installmentDetails1.getGirlsAmount();
                                    }
                                } else {
                                    headFee = installmentDetails1.getAmount();
                                }

                                if (payable > 0) {
                                    if (payable >= headFee) {
                                        paid = headFee;
                                        balance = 0;
                                        payable = payable - headFee;

                                        cPaid = paid;
                                    } else {
                                        paid = payable;
                                        balance = headFee - payable;
                                        payable = 0;
                                        cPaid = paid;
                                    }
                                } else {
                                    System.out.println("payable is less than 0 " + payable);
                                    paid = 0;
                                    balance = headFee;
                                    cPaid = paid;
                                }

                                prevPaid = paid + "";
                            }

                            totalFee = totalFee + headFee;
                            totalPaid = totalPaid + paid;
                            totalBalance = totalBalance + balance;
                            payheadObj.addProperty("totalFees", headFee);
                            payheadObj.addProperty("paid", paid);
                            payheadObj.addProperty("balance", balance);
                            payheadObj.addProperty("installmentPer", "100");
                            payheadObj.addProperty("concession", 0);
                            payheadObj.addProperty("prevPaid", prevPaid);
                            payheadObj.addProperty("cPaid", cPaid);

                            detailsArray.add(payheadObj);

                        }
                        masterObj.addProperty("totalFee", totalFee);
                        masterObj.addProperty("totalPaid", totalPaid);
                        masterObj.addProperty("totalBalance", totalBalance);
                        masterObj.add("installmentData", detailsArray);
                    }
                    masterArray.add(masterObj);
                }
            }

            FeesTransactionSummary transactionSummary = feesTransactionSummaryRepository.findByFeesMasterIdAndStudentRegisterId(feesMasterId, studentId);
            if (transactionSummary != null) {
                res.addProperty("outstanding", transactionSummary.getBalance());
            }
            res.add("responseObject", masterArray);
            res.addProperty("responseStatus", HttpStatus.OK.value());

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());
            installmentLogger.error("getDetailsByInstallment -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.OK.value());
        }
        return res;
    }

    public JsonObject getFeesInstallmentById(HttpServletRequest request) {
        JsonObject result = new JsonObject();

        JsonArray headArray = new JsonArray();
        try {
            Long installmentId = Long.valueOf(request.getParameter("id"));
            Long feesMasterId = Long.valueOf(request.getParameter("feesMasterId"));
            int concessionType = Integer.parseInt(request.getParameter("concessionType"));

            JsonObject response = new JsonObject();
            FeesMaster feesMaster = feesMasterRepository.findByIdAndStatus(feesMasterId, true);
            if (feesMaster != null) {
                response.addProperty("feesMasterId", feesMasterId);
                response.addProperty("concessionType", concessionType);
                response.addProperty("branchId", feesMaster.getBranch().getId());
                response.addProperty("academicYearId", feesMaster.getAcademicYear().getId());
                response.addProperty("standardId", feesMaster.getStandard().getId());
                response.addProperty("feesAmount", feesMaster.getAmount());
                response.addProperty("installmentNo", feesMaster.getNoOfInstallment());
                response.addProperty("studentType", feesMaster.getStudentType());
                response.addProperty("amountForBoy", feesMaster.getAmountForBoy());
                response.addProperty("amountForGirl", feesMaster.getAmountForGirl());
                response.addProperty("studentGroup", feesMaster.getStudentGroup());
            }

            int noOfInstallments = 0;
            List<FeesDetails> feesDetailsList = feesDetailsRepository.findByFeesMasterIdAndStatusAndSubFeeHeadNull(feesMasterId, true);
            for (FeesDetails feesDetails : feesDetailsList) {
                List<FeesDetails> feesDetails1 = feesDetailsRepository.findByFeeHeadIdAndFeesMasterIdAndStatusAndSubFeeHeadIsNotNull(feesDetails.getFeeHead().getId(), feesMasterId, true);
                if (feesDetails1.size() > 0) {
                    for (FeesDetails feesDetails2 : feesDetails1) {
                        JsonObject payheadObj = new JsonObject();
                        payheadObj.addProperty("feeHeadId", feesDetails2.getFeeHead().getId());
                        payheadObj.addProperty("payHeadId", feesDetails2.getSubFeeHead().getId());
                        payheadObj.addProperty("payHeadName", feesDetails2.getSubFeeHead().getSubFeeHeadName());
                        payheadObj.addProperty("priority", feesDetails2.getPriority());
                        payheadObj.addProperty("isSubHead", 1);
                        payheadObj.addProperty("amount", feesDetails2.getAmount());
                        payheadObj.addProperty("boys", feesDetails2.getAmountForBoy());
                        payheadObj.addProperty("girls", feesDetails2.getAmountForGirl());
                        headArray.add(payheadObj);

                        noOfInstallments = feesDetails.getFeesMaster().getNoOfInstallment();
                    }
                } else {
                    JsonObject payheadObj = new JsonObject();
                    payheadObj.addProperty("feeHeadId", 0);
                    payheadObj.addProperty("payHeadId", feesDetails.getFeeHead().getId());
                    payheadObj.addProperty("payHeadName", feesDetails.getFeeHead().getFeeHeadName());
                    payheadObj.addProperty("priority", feesDetails.getPriority());
                    payheadObj.addProperty("isSubHead", 0);
                    payheadObj.addProperty("amount", feesDetails.getAmount());
                    payheadObj.addProperty("boys", feesDetails.getAmountForBoy());
                    payheadObj.addProperty("girls", feesDetails.getAmountForGirl());
                    headArray.add(payheadObj);

                    noOfInstallments = feesDetails.getFeesMaster().getNoOfInstallment();
                }
            }

            List<InstallmentMaster> installmentMasterList = new ArrayList<>();
            for (int i = 0; i < noOfInstallments; i++) {
                int installno = (i + 1);
                InstallmentMaster installmentMaster = null;
                if (feesMaster.getStudentGroup() != null) {
                    installmentMaster = installmentMasterRepository.findByFeesMasterIdAndConcessionTypeAndInstallmentNoAndStudentGroup(
                            feesMasterId, concessionType, installno, feesMaster.getStudentGroup());
                } else {
                    installmentMaster = installmentMasterRepository.findByFeesMasterIdAndConcessionTypeAndInstallmentNo(feesMasterId, concessionType, installno);
                }
                if (installmentMaster != null) {
                    response.addProperty("expiryDate_" + i, installmentMaster.getExpiryDate() != null ? installmentMaster.getExpiryDate().toString() : "");
                    response.addProperty("instId_" + installno, installmentMaster.getId());
                    installmentMasterList.add(installmentMaster);
                }
            }

            for (JsonElement jsonElement : headArray) {
                JsonObject jsonObject1 = jsonElement.getAsJsonObject();
                for (InstallmentMaster installmentMaster : installmentMasterList) {
                    int index = (installmentMaster.getInstallmentNo() - 1);
                    if (jsonObject1.get("isSubHead").getAsInt() == 1) {
                        InstallmentDetails installmentDetails = installmentDetailsRepository.findByInstallmentMasterIdAndSubFeeHeadId(
                                installmentMaster.getId(), jsonObject1.get("payHeadId").getAsLong());

                        if (installmentDetails != null) {
                            jsonObject1.addProperty("amt" + index, installmentDetails.getAmount());
                            jsonObject1.addProperty("boys" + index, installmentDetails.getBoysAmount());
                            jsonObject1.addProperty("girls" + index, installmentDetails.getGirlsAmount());
                        }
                    } else if (jsonObject1.get("isSubHead").getAsInt() == 0) {
                        InstallmentDetails installmentDetails = installmentDetailsRepository.findByInstallmentMasterIdAndFeeHeadId(
                                installmentMaster.getId(), jsonObject1.get("payHeadId").getAsLong());

                        if (installmentDetails != null) {
                            jsonObject1.addProperty("amt" + index, installmentDetails.getAmount());
                            jsonObject1.addProperty("boys" + index, installmentDetails.getBoysAmount());
                            jsonObject1.addProperty("girls" + index, installmentDetails.getGirlsAmount());
                        }
                    }
                }
            }
            response.add("data", headArray);

            result.add("responseObject", response);
            result.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            result.addProperty("message", "Failed to update fee head");
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;

    }


    public JsonObject getInstallments(HttpServletRequest request) {
        JsonObject responseMessage = new JsonObject();
        try {
            Long branchId = Long.valueOf(request.getParameter("branchId"));
            Long academicYearId = Long.valueOf(request.getParameter("academicYearId"));
            Long standardId = Long.valueOf(request.getParameter("standardId"));
//            Long divisionId = Long.valueOf(request.getParameter("divisionId"));
            Long studentId = Long.valueOf(request.getParameter("studentId"));
            Integer studentType = Integer.valueOf(request.getParameter("studentType"));

            StudentAdmission studentAdmission = studentAdmissionRepository.findByStudentRegisterIdAndStatus(studentId, true);

//       FeesMaster feesMaster = feesMasterRepository.findByBranchIdAndStandardIdAndAcademicYearIdAndStudentTypeAndStatus(branchId, standardId, academicYearId, studentType, true);
            FeesMaster feesMaster = null;

//            if (studentAdmission.getStudentGroup() != null)
//                feesMaster = feesMasterRepository.findByBranchIdAndStandardIdAndAcademicYearIdAndStudentTypeAndStudentGroupAndStatus(
//                        studentAdmission.getBranch().getId(), studentAdmission.getStandard().getId(), studentAdmission.getAcademicYear().getId(),
//                        studentAdmission.getStudentType(), studentAdmission.getStudentGroup(), true);
//            else
                feesMaster = feesMasterRepository.findByBranchIdAndStandardIdAndAcademicYearIdAndStudentTypeAndStatus(
                        studentAdmission.getBranch().getId(), studentAdmission.getStandard().getId(), studentAdmission.getAcademicYear().getId(), studentAdmission.getStudentType(), true);

            if (feesMaster != null) {
                JsonArray installmentArray = new JsonArray();
//                List<Object[]> installmentList = installmentMasterRepository.findInstallmentsByFeesMasterId(feesMaster.getId());
//                for (int i = 0; i < installmentList.size(); i++) {
//                    Object[] obj = installmentList.get(i);
//                    installmentArray.add(obj[0].toString());
//                }

               // responseMessage.addProperty("outstanding", 0);

                String feesDetails = feesDetailsRepository.getTotalFeesByFeesMasterIdAndStatus(feesMaster.getId(), true);
                System.out.println("feesDetails " + feesDetails);
//                if (feesDetails != null) {
//                    String[] arr = feesDetails.split(",");
//                    responseMessage.addProperty("outstanding", Precision.round(Double.parseDouble(arr[2]), 2));
//                    responseMessage.addProperty("totalFees", Precision.round(Double.parseDouble(arr[2]), 2));
//
//
//                    if (feesMaster.getStudentType() == 2 && (studentAdmission.getStandard().getStandardName().equalsIgnoreCase("11") ||
//                            studentAdmission.getStandard().getStandardName().equalsIgnoreCase("12"))) {
//
//                        if (studentAdmission.getStudentRegister().getGender().equalsIgnoreCase("male")) {
//                            responseMessage.addProperty("outstanding", Precision.round(Double.parseDouble(arr[0]), 2));
//                            responseMessage.addProperty("totalFees", Precision.round(Double.parseDouble(arr[0]), 2));
//                        } else if (studentAdmission.getStudentRegister().getGender().equalsIgnoreCase("female")) {
//                            responseMessage.addProperty("outstanding", Precision.round(Double.parseDouble(arr[1]), 2));
//                            responseMessage.addProperty("totalFees", Precision.round(Double.parseDouble(arr[1]), 2));
//                        }
//                    }
//                }
                StudentRegister studentRegister=studentRegisterRepository.findByIdAndStatus(studentId,true);
                FeesTransactionSummary feesTransactionSummary = feesTransactionSummaryRepository.findByStudentRegisterIdAndStatus(studentId,true);
                if (feesTransactionSummary != null) {
                    responseMessage.addProperty("transactionId", feesTransactionSummary.getId());
                    responseMessage.addProperty("outstanding", feesTransactionSummary.getBalance());
                }
                else{
                   // responseMessage.addProperty("transactionId", feesTransactionSummary.getId());
                    responseMessage.addProperty("transactionId", 0);
                    responseMessage.addProperty("outstanding", studentRegister.getMaxFee());
                }

                //responseMessage.add("responseObject", installmentArray);
                responseMessage.addProperty("feesMasterId", feesMaster.getId());
                responseMessage.addProperty("responseStatus", HttpStatus.OK.value());
            } else {
                responseMessage.addProperty("message", "No data found");
                responseMessage.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            installmentLogger.error("Exception " + e.getMessage());
            System.out.println("Exception " + e.getMessage());
            e.printStackTrace();
        }
        return responseMessage;
    }

    public JsonObject updateInstallmentMaster(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Branch branch = branchRepository.findByIdAndStatus(Long.parseLong(request.getParameter("branchId")), true);
            FeesMaster feesMaster = feesMasterRepository.findByIdAndStatus(Long.parseLong(request.getParameter("feesMasterId")), true);

            int noOfInstallments = Integer.valueOf(request.getParameter("noOfInstallments"));

            for (int j = 0; j < noOfInstallments; j++) {
                Long installmentId = Long.valueOf(request.getParameter("instId_" + (j + 1)));
                InstallmentMaster installmentMaster = installmentMasterRepository.findByIdAndStatus(installmentId, true);
                if (installmentMaster != null) {
                    installmentMaster.setOutlet(users.getOutlet());
                    installmentMaster.setBranch(branch);
                    installmentMaster.setStatus(true);
                    String eDate = request.getParameter("expiryDate_" + (j + 1));
                    System.out.println("eDate " + eDate);
                    if (eDate != null && eDate.equalsIgnoreCase(""))
                        installmentMaster.setExpiryDate(LocalDate.parse(eDate));
                    installmentMaster.setFeesMaster(feesMaster);
                    installmentMaster.setConcessionType(Integer.valueOf(request.getParameter("concessionType")));
                    installmentMaster.setStudentType(Integer.valueOf(request.getParameter("studentType")));
                    installmentMaster.setStudentGroup(null);

                    if (request.getParameterMap().containsKey("studentGroup")) {
                        installmentMaster.setStudentGroup(Integer.valueOf(request.getParameter("studentGroup")));
                    }
                    installmentMaster.setInstallmentNo(Integer.valueOf(j + 1));
                    InstallmentMaster installmentMaster1 = installmentMasterRepository.save(installmentMaster);

                    if (installmentMaster1 != null) {
                        System.out.println("installmaster1.getId() " + installmentMaster1.getId());
                        installmentDetailsRepository.deleteDetailRecords(installmentMaster1.getId());

                        double amount = 0;
                        double boyAmount = 0;
                        double girlAmount = 0;
                        String jsonStr = request.getParameter("row");
                        JsonParser parser = new JsonParser();
                        JsonArray row = parser.parse(jsonStr).getAsJsonArray();
                        System.out.println("J =>" + j);
                        for (int i = 0; i < row.size(); i++) {
                            JsonObject feesRow = row.get(i).getAsJsonObject();
                            InstallmentDetails installmentDetails = new InstallmentDetails();
                            installmentDetails.setStatus(true);
                            installmentDetails.setInstallmentMaster(installmentMaster1);
                            installmentDetails.setPriority(feesRow.get("priority").getAsDouble());

                            if (feesRow.get("isSubHead").getAsInt() == 0) {
                                FeeHead feeHead = feeHeadRepository.findByIdAndStatus(feesRow.get("paymentHeadId").getAsLong(), true);
                                installmentDetails.setFeeHead(feeHead);
                            } else {
                                FeeHead feeHead = feeHeadRepository.findByIdAndStatus(feesRow.get("feeHeadId").getAsLong(), true);
                                installmentDetails.setFeeHead(feeHead);
                                SubFeeHead subFeeHead = subFeeHeadRepository.findByIdAndStatus(feesRow.get("paymentHeadId").getAsLong(), true);
                                installmentDetails.setSubFeeHead(subFeeHead);
                            }

                            if (feesMaster.getStudentType() == 2 && (feesMaster.getStandard().getStandardName().equalsIgnoreCase("11") ||
                                    feesMaster.getStandard().getStandardName().equalsIgnoreCase("12"))) {
                                installmentDetails.setBoysAmount(feesRow.get("boys" + j).getAsDouble());
                                installmentDetails.setGirlsAmount(feesRow.get("girls" + j).getAsDouble());

                                boyAmount = boyAmount + feesRow.get("boys" + j).getAsDouble();
                                girlAmount = girlAmount + feesRow.get("girls" + j).getAsDouble();
                            } else {
                                installmentDetails.setAmount(feesRow.get("amt" + j).getAsDouble());
                                amount = amount + feesRow.get("amt" + j).getAsDouble();
                            }
                            installmentDetails.setOutlet(users.getOutlet());
                            installmentDetails.setBranch(branch);
                            detailsRepository.save(installmentDetails);
                        }

                        installmentMaster1.setFeesAmount(amount);
                        installmentMaster1.setBoysAmount(boyAmount);
                        installmentMaster1.setGirlsAmount(girlAmount);

                        installmentMasterRepository.save(installmentMaster1);
                    }
                }
            }

            response.addProperty("message", "Installment  Master Updated SuccessFully");
            response.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());
            installmentLogger.error("updateInstallmentMaster -> failed to update installment master " + e.getMessage());
            response.addProperty("message", "Failed to update installment master");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());

        }
        return response;
    }


    public JsonObject deleteFeesInstallmentMaster(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        try {
            System.out.println("request.getParameter(\"id\") " + request.getParameter("id"));
            InstallmentMaster installmentMaster = installmentMasterRepository.findByIdAndStatus(Long.valueOf(request.getParameter("id")), true);
            if (installmentMaster != null) {
                detailsRepository.deleteDetailRecords(installmentMaster.getId());
                installmentMasterRepository.deleteMasterRecord(installmentMaster.getId());

                response.addProperty("response", "Deleted Installment Master");
                response.addProperty("responseStatus", HttpStatus.OK.value());
            } else {
                System.out.println("installment not exists");

                response.addProperty("response", "Installment Master not exists");
                response.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            System.out.println("deleteFeesInstallmentMaster " + e.getMessage());
            e.printStackTrace();
            installmentLogger.error("deleteFeesInstallmentMaster -> failed to delete installment master " + e.getMessage());


            response.addProperty("response", "Failed to delete Installment Master");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }
}
