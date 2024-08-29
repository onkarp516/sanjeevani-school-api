package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.school_master.*;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionSummary;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.*;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.FeesTransactionSummaryRepository;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeesMasterService {
    private static final Logger feesMasterLogger = LoggerFactory.getLogger(FeesMasterService.class);
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
     @Autowired
   private FeesMasterRepository feesRepository;
    @Autowired
    private FeesDetailsRepository feesDetailsRepository;
    @Autowired
    private OutletRepository outletRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private FeeHeadRepository feeHeadRepository;
    @Autowired
    private AcademicYearRepository academicYearRepository;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private DivisionRepository divisionRepository;
    @Autowired
    private SubFeeHeadRepository subFeeHeadRepository;
    @Autowired
    private FeesMasterRepository feesMasterRepository;
    @Autowired
    private StudentRegisterRepository studentRegisterRepository;
    @Autowired
    private FeesTransactionSummaryRepository feesTransactionSummaryRepository;
    @Autowired
    private StudentTransportRepository studentTransportRepository;

    public JsonObject createFeesMaster(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        FeesMaster feesMaster = new FeesMaster();

        try {
            Users users = jwtRequestFilter.getUserDataFromToken(
                    request.getHeader("Authorization").substring(7));
            Branch branch = branchRepository.findByIdAndStatus(Long.parseLong(request.getParameter("branchId")), true);
            AcademicYear academicYear = academicYearRepository.findByIdAndStatus(Long.parseLong(request.getParameter("academicYearId")), true);
            Standard standard = standardRepository.findByIdAndStatus(Long.parseLong(request.getParameter("standardId")), true);
//            Division division = divisionRepository.findByIdAndStatus(Long.parseLong(request.getParameter("divisionId")), true);

            feesMaster.setOutlet(users.getOutlet());
            feesMaster.setBranch(branch);
            feesMaster.setAcademicYear(academicYear);
            feesMaster.setStandard(standard);
//            feesMaster.setDivision(division);
            feesMaster.setStatus(true);
            feesMaster.setNoOfInstallment(Integer.valueOf((request.getParameter("noOfInstallment"))));
            feesMaster.setMinimumAmount(Double.valueOf(request.getParameter("minimumAmount")));
            feesMaster.setStudentType(Integer.valueOf(request.getParameter("studentType")));

            if (request.getParameterMap().containsKey("studentGroup")) {
                feesMaster.setStudentGroup(Integer.valueOf(request.getParameter("studentGroup")));
            }

            try {
                FeesMaster fees = feesRepository.save(feesMaster);

                double totalAmount = 0;
                double totalBoysAmount = 0;
                double totalGirlsAmount = 0;

                String jsonStr = request.getParameter("row");
                JsonParser parser = new JsonParser();
                JsonArray row = parser.parse(jsonStr).getAsJsonArray();
                for (int i = 0; i < row.size(); i++) {
                    JsonObject feesRow = row.get(i).getAsJsonObject();

                    FeesDetails feesDetails = new FeesDetails();
                    feesDetails.setStatus(true);
                    feesDetails.setFeesMaster(fees);

                    if (feesRow.get("isSubHead").getAsInt() == 0) {
                        FeeHead feeHead = feeHeadRepository.findByIdAndStatus(feesRow.get("paymentHeadId").getAsLong(), true);
                        feesDetails.setFeeHead(feeHead);

                        totalAmount = totalAmount + +(!feesRow.get("amount").getAsString().equalsIgnoreCase("")
                                ? feesRow.get("amount").getAsDouble() : 0);
                        totalGirlsAmount = totalGirlsAmount + (!feesRow.get("amountForGirl").getAsString().equalsIgnoreCase("")
                                ? feesRow.get("amountForGirl").getAsDouble() : 0);
                        totalBoysAmount = totalBoysAmount + +(!feesRow.get("amountForBoy").getAsString().equalsIgnoreCase("")
                                ? feesRow.get("amountForBoy").getAsDouble() : 0);
                    } else {
                        FeeHead feeHead = feeHeadRepository.findByIdAndStatus(feesRow.get("feeHeadId").getAsLong(), true);
                        feesDetails.setFeeHead(feeHead);
                        SubFeeHead subFeeHead = subFeeHeadRepository.findByIdAndStatus(feesRow.get("paymentHeadId").getAsLong(), true);
                        feesDetails.setSubFeeHead(subFeeHead);
                    }
                    feesDetails.setOutlet(users.getOutlet());
                    feesDetails.setBranch(branch);

                  //  feesDetails.setPriority(feesRow.get("priority").getAsDouble());
                    if (!feesRow.get("amountForBoy").getAsString().equalsIgnoreCase("")) {
                        feesDetails.setAmountForBoy(feesRow.get("amountForBoy").getAsDouble());
                    }
                    if (!feesRow.get("amountForGirl").getAsString().equalsIgnoreCase("")) {
                        feesDetails.setAmountForGirl(feesRow.get("amountForGirl").getAsDouble());
                    }
                    if (!feesRow.get("amount").getAsString().equalsIgnoreCase("")) {
                        feesDetails.setAmount(feesRow.get("amount").getAsDouble());
                    }
                    feesDetailsRepository.save(feesDetails);
                }

                fees.setAmount(totalAmount);
                fees.setAmountForBoy(totalBoysAmount);
                fees.setAmountForGirl(totalGirlsAmount);
                feesMasterRepository.save(fees);

                response.addProperty("message", "FeesMaster created successfully");
                response.addProperty("responseStatus", HttpStatus.OK.value());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception " + e.getMessage());
                feesMasterLogger.error("createFeesMaster -> failed to create fees master " + e.getMessage());
                response.addProperty("message", "Failed to create fees master");
                response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());
            feesMasterLogger.error("createFeesMaster -> failed to create fees master " + e.getMessage());
            response.addProperty("message", "Failed to create fees master");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }


    public JsonObject getFeesMasters(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(
                    request.getHeader("Authorization").substring(7));
            JsonArray result = new JsonArray();
            Long outletId = users.getOutlet().getId();
            List<FeesMaster> list = new ArrayList<>();
            if (users.getBranch() != null) {
                list = feesRepository.findByOutletIdAndBranchIdAndStatus(outletId, users.getBranch().getId(), true);
            } else {
                list = feesRepository.findByOutletIdAndStatus(outletId, true);
            }
            for (FeesMaster master : list) {
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
                response.addProperty("id", master.getId());
                response.addProperty("outletName", master.getOutlet().getCompanyName());
                response.addProperty("branchName", master.getBranch().getBranchName());
                response.addProperty("academicYear", master.getAcademicYear().getYear());
                response.addProperty("standard", master.getStandard().getStandardName());
//                response.addProperty("division", master.getDivision().getDivisionName());totalBoysAmount
                response.addProperty("minimumAmount", master.getMinimumAmount());

                response.addProperty("amountForBoy", Precision.round(master.getAmountForBoy(), 2));
                response.addProperty("amountForGirl", Precision.round(master.getAmountForGirl(), 2));
                response.addProperty("amount", Precision.round(master.getAmount(), 2));

                /*String feesData = feesDetailsRepository.getTotalFeesByFeesMasterIdAndStatus(master.getId(), true);
                System.out.println("feesData " + feesData);
                if (feesData != null) {
                    String[] arr = feesData.split(",");
                }*/
                result.add(response);
            }
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());
            feesMasterLogger.error("getFeesMaster -> failed to load fees master " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.OK.value());
        }
        return res;
    }

    public JsonObject getFeesMasterById(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        try {

            FeesMaster feesMaster = feesRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (feesMaster != null) {
                response.addProperty("id", feesMaster.getId());

                response.addProperty("branchId", feesMaster.getBranch().getId());
                response.addProperty("branchName", feesMaster.getBranch().getBranchName());
                response.addProperty("academicYearId", feesMaster.getAcademicYear().getId());
                response.addProperty("academicyear", feesMaster.getAcademicYear().getYear());
                response.addProperty("standardId", feesMaster.getStandard().getId());
                response.addProperty("noOfInstallment", feesMaster.getNoOfInstallment());

                response.addProperty("divisionName", feesMaster.getStandard().getStandardName());
//                response.addProperty("divisionId", feesMaster.getDivision().getId());
//                response.addProperty("divisionName", feesMaster.getDivision().getDivisionName());
                response.addProperty("studentType", feesMaster.getStudentType());
                response.addProperty("studentGroup", feesMaster.getStudentGroup());
                response.addProperty("minimumAmount", feesMaster.getMinimumAmount());

                System.out.println("feesMaster.getId() " + feesMaster.getId());

                JsonArray rowdetails = new JsonArray();
                List<FeesDetails> feesDetailsList = feesDetailsRepository.findByFeesMasterIdAndStatus(feesMaster.getId(), true);
                /**** LAMBDA EXPRESSION *****/
                feesDetailsList.forEach(row -> {
                    JsonObject res = new JsonObject();

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("label", row.getFeeHead().getFeeHeadName());
                    jsonObject.addProperty("value", row.getFeeHead().getId());
                    res.add("paymentHeadId", jsonObject);
                    res.addProperty("priority", row.getPriority());

                    res.addProperty("feeHeadId", 0);
                    res.addProperty("isSubHead", 0);
                    if (row.getSubFeeHead() != null) {
                        JsonObject subfObject1 = new JsonObject();
                        subfObject1.addProperty("label", row.getSubFeeHead().getSubFeeHeadName());
                        subfObject1.addProperty("value", row.getSubFeeHead().getId());
                        subfObject1.addProperty("feeHeadId", row.getFeeHead().getId());
                        res.add("paymentHeadId", subfObject1);
                        res.addProperty("isSubHead", 1);
                    }
                    res.addProperty("amountForBoy", "");
                    if (row.getAmountForBoy() != null) {
                        res.addProperty("amountForBoy", row.getAmountForBoy());
                    }
                    res.addProperty("amountForGirl", "");
                    if (row.getAmountForGirl() != null) {
                        res.addProperty("amountForGirl", row.getAmountForGirl());
                    }
                    res.addProperty("amount", "");
                    if (row.getAmount() != null) {
                        res.addProperty("amount", row.getAmount());
                    }
                    rowdetails.add(res);
                });
                result.addProperty("message", "success");
                result.addProperty("responseStatus", HttpStatus.OK.value());
                result.add("data", response);
                result.add("row", rowdetails);
            } else {
                result.addProperty("message", "Data not found");
                result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            feesMasterLogger.error("updateFeeHead -> failed to update fee head " + e.getMessage());
            response.addProperty("message", "Failed to update fee head");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }


    public JsonObject updateFeesMaster(HttpServletRequest request) {
        JsonObject response = new JsonObject();

        try {
            Users users = jwtRequestFilter.getUserDataFromToken(
                    request.getHeader("Authorization").substring(7));
            FeesMaster feesMaster = feesRepository.findByIdAndStatus(Long.parseLong(request.getParameter("id")), true);

            if (feesMaster != null) {
                Branch branch = branchRepository.findByIdAndStatus(Long.parseLong(request.getParameter("branchId")), true);
                AcademicYear academicYear = academicYearRepository.findByIdAndStatus(Long.parseLong(request.getParameter("academicYearId")), true);
                Standard standard = standardRepository.findByIdAndStatus(Long.parseLong(request.getParameter("standardId")), true);

                feesMaster.setOutlet(users.getOutlet());
                feesMaster.setBranch(branch);
                feesMaster.setAcademicYear(academicYear);
                feesMaster.setStandard(standard);
                feesMaster.setStatus(true);
                feesMaster.setNoOfInstallment(Integer.valueOf((request.getParameter("noOfInstallment"))));
                feesMaster.setMinimumAmount(Double.valueOf(request.getParameter("minimumAmount")));
                feesMaster.setStudentType(Integer.valueOf(request.getParameter("studentType")));
                if (request.getParameterMap().containsKey("studentGroup")) {
                    feesMaster.setStudentGroup(Integer.valueOf(request.getParameter("studentGroup")));
                }

                try {
                    FeesMaster fees = feesRepository.save(feesMaster);

                    System.out.println("fees.getId() " + fees.getId());
                    feesDetailsRepository.deleteFeesDetails(fees.getId());

                    double totalAmount = 0;
                    double totalBoysAmount = 0;
                    double totalGirlsAmount = 0;

                    String jsonStr = request.getParameter("row");
                    JsonParser parser = new JsonParser();
                    JsonArray row = parser.parse(jsonStr).getAsJsonArray();
                    for (int i = 0; i < row.size(); i++) {
                        JsonObject feesRow = row.get(i).getAsJsonObject();

                        FeesDetails feesDetails = new FeesDetails();
                        feesDetails.setStatus(true);
                        feesDetails.setFeesMaster(fees);

                        if (feesRow.get("isSubHead").getAsInt() == 0) {
                            FeeHead feeHead = feeHeadRepository.findByIdAndStatus(feesRow.get("paymentHeadId").getAsLong(), true);
                            feesDetails.setFeeHead(feeHead);

                            totalAmount = totalAmount + +(!feesRow.get("amount").getAsString().equalsIgnoreCase("")
                                    ? feesRow.get("amount").getAsDouble() : 0);
                            totalGirlsAmount = totalGirlsAmount + (!feesRow.get("amountForGirl").getAsString().equalsIgnoreCase("")
                                    ? feesRow.get("amountForGirl").getAsDouble() : 0);
                            totalBoysAmount = totalBoysAmount + +(!feesRow.get("amountForBoy").getAsString().equalsIgnoreCase("")
                                    ? feesRow.get("amountForBoy").getAsDouble() : 0);
                        } else {
                            FeeHead feeHead = feeHeadRepository.findByIdAndStatus(feesRow.get("feeHeadId").getAsLong(), true);
                            feesDetails.setFeeHead(feeHead);
                            SubFeeHead subFeeHead = subFeeHeadRepository.findByIdAndStatus(feesRow.get("paymentHeadId").getAsLong(), true);
                            feesDetails.setSubFeeHead(subFeeHead);
                        }
                        feesDetails.setOutlet(users.getOutlet());
                        feesDetails.setBranch(branch);

                       // feesDetails.setPriority(feesRow.get("priority").getAsDouble());
                        if (!feesRow.get("amountForBoy").getAsString().equalsIgnoreCase("")) {
                            feesDetails.setAmountForBoy(feesRow.get("amountForBoy").getAsDouble());
                        }
                        if (!feesRow.get("amountForGirl").getAsString().equalsIgnoreCase("")) {
                            feesDetails.setAmountForGirl(feesRow.get("amountForGirl").getAsDouble());
                        }
                        if (!feesRow.get("amount").getAsString().equalsIgnoreCase("")) {
                            feesDetails.setAmount(feesRow.get("amount").getAsDouble());
                        }
                        feesDetailsRepository.save(feesDetails);
                    }

                    fees.setAmount(totalAmount);
                    fees.setAmountForBoy(totalBoysAmount);
                    fees.setAmountForGirl(totalGirlsAmount);
                    feesMasterRepository.save(fees);

                    response.addProperty("message", "FeesMaster updated successfully");
                    response.addProperty("responseStatus", HttpStatus.OK.value());

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Exception " + e.getMessage());
                    feesMasterLogger.error("updateFeesMaster -> failed to update fees master " + e.getMessage());
                    response.addProperty("message", "Failed to update fees master");
                    response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());
            feesMasterLogger.error("createFeesMaster -> failed to update fees master " + e.getMessage());
            response.addProperty("message", "Failed to update fees master");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }


    public JsonObject getFeesMasterDetailsForInstallment(HttpServletRequest request) {
        JsonObject responseMessage = new JsonObject();
        try {
            Long branchId = Long.valueOf(request.getParameter("branchId"));
            Long academicYearId = Long.valueOf(request.getParameter("academicYearId"));
            Long standardId = Long.valueOf(request.getParameter("standardId"));
            Integer studentType = Integer.valueOf(request.getParameter("studentType"));
            String studentGroup = request.getParameter("studentGroup");

            FeesMaster feesMaster = null;
            if (!studentGroup.equalsIgnoreCase("")) {
                feesMaster = feesMasterRepository.findByBranchIdAndStandardIdAndAcademicYearIdAndStudentTypeAndStudentGroupAndStatus(
                        branchId, standardId, academicYearId, studentType, Integer.parseInt(studentGroup), true);
            } else {
                feesMaster = feesMasterRepository.findByBranchIdAndStandardIdAndAcademicYearIdAndStudentTypeAndStatus(branchId, standardId, academicYearId, studentType, true);
            }
            JsonArray payheadsArray = new JsonArray();
            if (feesMaster != null) {
                List<FeesDetails> feesDetailsList = feesDetailsRepository.findByFeesMasterIdAndStatusAndSubFeeHeadNull(feesMaster.getId(), true);
                for (FeesDetails feesDetails : feesDetailsList) {

                    List<FeesDetails> feesDetails1 = feesDetailsRepository.findByFeeHeadIdAndFeesMasterIdAndStatusAndSubFeeHeadIsNotNull(feesDetails.getFeeHead().getId(), feesMaster.getId(), true);
                    System.out.println("feesDetails1 size " + feesDetails1.size());
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
                            payheadsArray.add(payheadObj);
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
                        payheadsArray.add(payheadObj);
                    }
                }

                String feesData = feesDetailsRepository.getTotalFeesByFeesMasterIdAndStatus(feesMaster.getId(), true);
                System.out.println("feesData " + feesData);
                if (feesData != null) {
                    String[] arr = feesData.split(",");
                    responseMessage.addProperty("amountForBoy", Precision.round(Double.parseDouble(arr[0]), 2));
                    responseMessage.addProperty("amountForGirl", Precision.round(Double.parseDouble(arr[1]), 2));
                    responseMessage.addProperty("amount", Precision.round(Double.parseDouble(arr[2]), 2));
                }

                responseMessage.add("responseObject", payheadsArray);
                responseMessage.addProperty("feesMasterId", feesMaster.getId());
                responseMessage.addProperty("noOfInstallments", feesMaster.getNoOfInstallment());
                responseMessage.addProperty("responseStatus", HttpStatus.OK.value());
            } else {
                responseMessage.addProperty("message", "No data found");
                responseMessage.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (
                Exception e) {
            feesMasterLogger.error("Exception " + e.getMessage());
            System.out.println("Exception " + e.getMessage());
            e.printStackTrace();
            responseMessage.addProperty("message", "No data found");
            responseMessage.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return responseMessage;
    }

    public JsonObject getFeesMasterDetailsByStandard(HttpServletRequest request) {
        JsonObject responseMessage = new JsonObject();
        try {
            Long branchId = Long.valueOf(request.getParameter("branchId"));
            Long academicYearId = Long.valueOf(request.getParameter("academicYearId"));
            Long standardId = Long.valueOf(request.getParameter("standardId"));
            Long studentId = Long.valueOf(request.getParameter("studentId"));
            Integer studentType = Integer.valueOf(request.getParameter("studentType"));
//            String studentGroup = request.getParameter("studentGroup");
//            Long feesMasterId = Long.valueOf(request.getParameter("feesMasterId"));

            Standard standard = standardRepository.findByIdAndStatus(standardId,true);
            StudentTransport studentTransport=studentTransportRepository.findByStudentRegisterIdAndStatus(studentId,true);

            StudentRegister student = studentRegisterRepository.findByIdAndStatus(studentId,true);
            responseMessage.addProperty("transactionId", 0);
           // if(Double.compare(standard.getMaxFee(), student.getMaxFee()) == 0){
                FeesMaster feesMaster = null;
                    feesMaster = feesMasterRepository.findByBranchIdAndStandardIdAndAcademicYearIdAndStudentTypeAndStatus(branchId, standardId, academicYearId, studentType, true);
                FeesTransactionSummary feesTransactionSummary = feesTransactionSummaryRepository.findByFeesMasterIdAndStudentRegisterId(feesMaster.getId(), studentId);
                if (feesTransactionSummary != null) {
                    responseMessage.addProperty("transactionId", feesTransactionSummary.getId());
                    responseMessage.addProperty("outstanding", feesTransactionSummary.getBalance());
                    responseMessage.addProperty("feesMasterId", feesMaster.getId());
                }
                JsonArray payheadsArray = new JsonArray();

            if (feesMaster != null) {
                    List<FeesDetails> feesDetailsList = feesDetailsRepository.findByFeesMasterIdAndStatusAndSubFeeHeadNull(feesMaster.getId(), true);

                    for (FeesDetails feesDetails : feesDetailsList) {
                        if(studentTransport == null && feesDetails.getFeeHead().getFeeHeadName().toLowerCase().contains("bus"))
                            continue;
                        JsonObject payheadObj = new JsonObject();
                        payheadObj.addProperty("feeHeadId", 0);
                        payheadObj.addProperty("payHeadId", feesDetails.getFeeHead().getId());
                        payheadObj.addProperty("payHeadName", feesDetails.getFeeHead().getFeeHeadName());
                        payheadObj.addProperty("priority", feesDetails.getPriority());
                        payheadObj.addProperty("isSubHead", 0);
                        if(studentTransport != null && feesDetails.getFeeHead().getFeeHeadName().toLowerCase().contains("bus")){
                            payheadObj.addProperty("amount",studentTransport.getBus().getBusFee());
                        }
                        else
                            payheadObj.addProperty("amount", feesDetails.getAmount());
                        payheadObj.addProperty("boys", feesDetails.getAmountForBoy());
                        payheadObj.addProperty("girls", feesDetails.getAmountForGirl());
                        // payheadObj.addProperty("balance",0);
                        payheadsArray.add(payheadObj);
                    }

                    String feesData = feesDetailsRepository.getTotalFeesByFeesMasterIdAndStatus(feesMaster.getId(), true);
                    System.out.println("feesData " + feesData);
                    if (feesData != null) {
                        String[] arr = feesData.split(",");
                        responseMessage.addProperty("amountForBoy", Precision.round(Double.parseDouble(arr[0]), 2));
                        responseMessage.addProperty("amountForGirl", Precision.round(Double.parseDouble(arr[1]), 2));
                        responseMessage.addProperty("amount", Precision.round(Double.parseDouble(arr[2]), 2));
                    }

                    responseMessage.add("responseObject", payheadsArray);
                    responseMessage.addProperty("feesMasterId", feesMaster.getId());
                    responseMessage.addProperty("noOfInstallments", feesMaster.getNoOfInstallment());
                    responseMessage.addProperty("responseStatus", HttpStatus.OK.value());
                } else {
                    responseMessage.addProperty("message", "No data found");
                    responseMessage.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
                }

//            if(studentTransport!=null) {
//                    //boolean isFound = payheadObj.get("payHeadName").getAsString().toLowerCase().contains("bus"); // true
//                    System.out.println(studentTransport.getBus().getBusStopName());
//                if (feesMaster != null) {
//                    FeeHead feeHead=feeHeadRepository.findByFeeHeadNameAndStatus("Bus Fee",true);
//                    List<FeesDetails> feesDetailsList = feesDetailsRepository.findByFeesMasterIdAndFeeHeadIdAndStatusAndSubFeeHeadNull(feesMaster.getId(),feeHead.getId(), true);
//
//                    for (FeesDetails feesDetails : feesDetailsList) {
//                        JsonObject payheadObj = new JsonObject();
//                        payheadObj.addProperty("feeHeadId", 0);
//                        payheadObj.addProperty("payHeadId", feesDetails.getFeeHead().getId());
//                        payheadObj.addProperty("payHeadName", feesDetails.getFeeHead().getFeeHeadName());
//                        payheadObj.addProperty("priority", feesDetails.getPriority());
//                        payheadObj.addProperty("isSubHead", 0);
//                        payheadObj.addProperty("amount", feesDetails.getAmount());
//                        payheadObj.addProperty("boys", feesDetails.getAmountForBoy());
//                        payheadObj.addProperty("girls", feesDetails.getAmountForGirl());
//                        // payheadObj.addProperty("balance",0);
//                        payheadsArray.add(payheadObj);
//                    }
//
//                    String feesData = feesDetailsRepository.getTotalFeesByFeesMasterIdAndStatus(feesMaster.getId(), true);
//                    System.out.println("feesData " + feesData);
//                    if (feesData != null) {
//                        String[] arr = feesData.split(",");
//                        responseMessage.addProperty("amountForBoy", Precision.round(Double.parseDouble(arr[0]), 2));
//                        responseMessage.addProperty("amountForGirl", Precision.round(Double.parseDouble(arr[1]), 2));
//                        responseMessage.addProperty("amount", Precision.round(Double.parseDouble(arr[2]), 2));
//                    }
//
//                    responseMessage.add("responseObject", payheadsArray);
//                    responseMessage.addProperty("feesMasterId", feesMaster.getId());
//                    responseMessage.addProperty("noOfInstallments", feesMaster.getNoOfInstallment());
//                    responseMessage.addProperty("responseStatus", HttpStatus.OK.value());
//                } else {
//                    responseMessage.addProperty("message", "No data found");
//                    responseMessage.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
//                }
//
//
//                }
//                else {
//                System.out.println("Bus is not assigned");
//
//                if (feesMaster != null) {
//                    List<FeesDetails> feesDetailsList = feesDetailsRepository.findByFeesMasterIdAndStatusAndSubFeeHeadNull(feesMaster.getId(), true);
//
//                    for (FeesDetails feesDetails : feesDetailsList) {
//                        JsonObject payheadObj = new JsonObject();
//                        payheadObj.addProperty("feeHeadId", 0);
//                        payheadObj.addProperty("payHeadId", feesDetails.getFeeHead().getId());
//                        payheadObj.addProperty("payHeadName", feesDetails.getFeeHead().getFeeHeadName());
//                        payheadObj.addProperty("priority", feesDetails.getPriority());
//                        payheadObj.addProperty("isSubHead", 0);
//                        payheadObj.addProperty("amount", feesDetails.getAmount());
//                        payheadObj.addProperty("boys", feesDetails.getAmountForBoy());
//                        payheadObj.addProperty("girls", feesDetails.getAmountForGirl());
//                        // payheadObj.addProperty("balance",0);
//                        payheadsArray.add(payheadObj);
//                    }
//
//                    String feesData = feesDetailsRepository.getTotalFeesByFeesMasterIdAndStatus(feesMaster.getId(), true);
//                    System.out.println("feesData " + feesData);
//                    if (feesData != null) {
//                        String[] arr = feesData.split(",");
//                        responseMessage.addProperty("amountForBoy", Precision.round(Double.parseDouble(arr[0]), 2));
//                        responseMessage.addProperty("amountForGirl", Precision.round(Double.parseDouble(arr[1]), 2));
//                        responseMessage.addProperty("amount", Precision.round(Double.parseDouble(arr[2]), 2));
//                    }
//
//                    responseMessage.add("responseObject", payheadsArray);
//                    responseMessage.addProperty("feesMasterId", feesMaster.getId());
//                    responseMessage.addProperty("noOfInstallments", feesMaster.getNoOfInstallment());
//                    responseMessage.addProperty("responseStatus", HttpStatus.OK.value());
//                } else {
//                    responseMessage.addProperty("message", "No data found");
//                    responseMessage.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
//                }
//            }
//
        } catch (
                Exception e) {
            feesMasterLogger.error("Exception " + e.getMessage());
            System.out.println("Exception " + e.getMessage());
            e.printStackTrace();
            responseMessage.addProperty("message", "No data found");
            responseMessage.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return responseMessage;
    }

    public JsonObject getFeesMasterformat(HttpServletRequest request) {
        Users users=jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        JsonObject result=new JsonObject();
        Long standardId= Long.valueOf(request.getParameter("standardId"));
        Long academicYearId= Long.valueOf(request.getParameter("academicYearId"));
        FeesMaster feesMaster=feesMasterRepository.findByBranchIdAndStandardIdAndAcademicYearIdAndStatus(users.getBranch().getId(),standardId,academicYearId,true);
        if(feesMaster!=null)
        {
        List<FeesDetails> feesDetails=feesDetailsRepository.findByFeesMasterIdAndStatus(feesMaster.getId(),true);
        JsonArray jsonArray=new JsonArray();
        for(FeesDetails flist: feesDetails)
        {
            JsonObject payheadObj=new JsonObject();

            payheadObj.addProperty("payHeadId", flist.getFeeHead().getId());
            payheadObj.addProperty("payHeadName", flist.getFeeHead().getFeeHeadName());
            payheadObj.addProperty("priority", flist.getPriority());
            payheadObj.addProperty("payheadamt",flist.getAmount());
            jsonArray.add(payheadObj);
        }
        result.addProperty("standardId",feesMaster.getStandard().getId());
        result.addProperty("maxFee",feesMaster.getAmount());
            result.addProperty("responseStatus",HttpStatus.OK.value());
        result.add("responseObject",jsonArray);
        result.addProperty("message","success");



        }


return result;

    }
}

