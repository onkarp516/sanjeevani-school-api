package in.truethics.ethics.ethicsapiv10.service.master_service;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.*;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BranchService {
    private static final Logger branchLogger = LoggerFactory.getLogger(BranchService.class);
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private OutletRepository outletRepository;
    @Autowired
    private GstTypeMasterRepository gstMasterRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private CountryRepository countryRepository;

    public ResponseMessage createBranch(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        Branch branch = new Branch();
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            Outlet company = outletRepository.findByIdAndStatus(
                    Long.parseLong(request.getParameter("companyId")), true);
            branch.setOutlet(company);
            branch.setCompanyName(company.getCompanyName());
            branch.setCompanyCode(company.getCompanyCode());
            branch.setBranchName(request.getParameter("branchName"));
            if (paramMap.containsKey("branchCode"))
                branch.setBranchCode(request.getParameter("branchCode"));
            else
                branch.setBranchCode("NA");
            if (paramMap.containsKey("udiseNo"))
                branch.setUdiseNo(Long.valueOf(request.getParameter("udiseNo")));
            else
                branch.setUdiseNo(Long.valueOf(0));
            if (paramMap.containsKey("affiliationNo"))
                branch.setAffiliationNo(Long.valueOf(request.getParameter("affiliationNo")));
            else
                branch.setAffiliationNo(Long.valueOf(0));
            if (paramMap.containsKey("mobileNumber"))
                branch.setMobileNumber(Long.parseLong(request.getParameter("mobileNumber")));
            else
                branch.setMobileNumber(Long.valueOf(0));
            if (paramMap.containsKey("whatsappNumber"))
                branch.setWhatsappNumber(Long.parseLong(request.getParameter("whatsappNumber")));
            else
                branch.setWhatsappNumber(Long.valueOf(0));
            if (paramMap.containsKey("branchContactPerson"))
                branch.setBranchContactPerson(request.getParameter("branchContactPerson"));
            else
                branch.setBranchContactPerson("NA");
            Users users = jwtRequestFilter.getUserDataFromToken(
                    request.getHeader("Authorization").substring(7));
            branch.setCreatedBy(users.getId());
            branch.setStatus(true);
            if (paramMap.containsKey("registeredAddress"))
                branch.setRegisteredAddress(request.getParameter("registeredAddress"));
            else
                branch.setRegisteredAddress("NA");
            if (paramMap.containsKey("corporateAddress"))
                branch.setCorporateAddress(request.getParameter("corporateAddress"));
            else
                branch.setCorporateAddress("NA");
            if (paramMap.containsKey("pincode"))
                branch.setPincode(request.getParameter("pincode"));
            else
                branch.setPincode("NA");
            if (paramMap.containsKey("email"))
                branch.setEmail(request.getParameter("email"));
            else
                branch.setEmail("NA");
            Optional<State> state = stateRepository.findById(
                    Long.parseLong(request.getParameter("state_id")));
            Optional<Country> country = countryRepository.findById(Long.parseLong(
                    request.getParameter("country_id")));
            branch.setState(state.get());
            branch.setCountry(country.get());
            if (paramMap.containsKey("website"))
                branch.setWebsite(request.getParameter("website"));
            else
                branch.setWebsite("NA");
            if (request.getParameter("gstApplicable").equalsIgnoreCase("yes")) {
                branch.setGstApplicable(true);
                branch.setGstNumber(request.getParameter("gstIn"));
                Optional<GstTypeMaster> gstTypeMaster = gstMasterRepository.findById(
                        Long.parseLong(request.getParameter("gstType")));
                branch.setGstTypeMaster(gstTypeMaster.get());
                String stateCode = request.getParameter("gstIn").substring(0, 2);
                branch.setStateCode(stateCode);
                if (paramMap.containsKey("gstApplicableDate")) {
                    LocalDate date = LocalDate.parse(request.getParameter("gstApplicableDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    branch.setGstApplicableDate(date);
                }
            } else {
                branch.setGstApplicable(false);
                branch.setGstNumber("NA");
                branch.setStateCode(state.get().getStateCode());
                Optional<GstTypeMaster> gstTypeMaster = gstMasterRepository.findById(3L);
                branch.setGstTypeMaster(gstTypeMaster.get());
            }
            branch.setCurrency(request.getParameter("currency"));
            Branch branch1 = branchRepository.save(branch);

            if (branch1 != null) {
                branchRepository.createDefaultLedgers(branch1.getId(),
                        branch1.getOutlet().getId(), branch1.getOutlet().getCreatedBy());
                branchRepository.createDefaultFeesLedgers(branch1.getId(),
                        branch1.getOutlet().getId(), branch1.getOutlet().getCreatedBy());
                branchRepository.createDefaultDpSalaryLedgers(branch1.getId(),
                        branch1.getOutlet().getId(), branch1.getOutlet().getCreatedBy());
                branchRepository.createDefaultConcessionSalaryLedgers(branch1.getId(),
                        branch1.getOutlet().getId(), branch1.getOutlet().getCreatedBy());
            }
            responseMessage.setMessage("Branch created successfully");
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            branchLogger.error("Exception in Branch Creation:" + e.getMessage());
            responseMessage.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseMessage.setMessage("Failed to create branch");
            e.printStackTrace();
            System.out.println("Exception:" + e.getMessage());
        }
        return responseMessage;
    }

    public JsonObject getAllBranches(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        JsonObject res = new JsonObject();
        JsonArray result = new JsonArray();
        try {
            List<Branch> list = branchRepository.findByOutletIdAndStatus(users.getOutlet().getId(), true);
            if (list.size() > 0) {
                for (Branch mBranch : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mBranch.getId());
                    response.addProperty("companyId", mBranch.getOutlet().getId());
                    response.addProperty("companyName",
                            mBranch.getOutlet().getCompanyName());
                    response.addProperty("branchName", mBranch.getBranchName());
                    response.addProperty("branchCode", mBranch.getBranchCode());
                    response.addProperty("mobileNumber", mBranch.getMobileNumber());
                    response.addProperty("branchContactPerson", mBranch.getBranchContactPerson());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }

    /* get branch by id */
    public JsonObject getBranchById(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        Branch branch = branchRepository.findByIdAndStatus(Long.parseLong(request.getParameter("id")),
                true);
        if (branch != null) {
            response.addProperty("id", branch.getId());
            response.addProperty("companyId", branch.getOutlet().getId());
            response.addProperty("companyName",
                    branch.getOutlet().getCompanyName());
            response.addProperty("branchName", branch.getBranchName());
            response.addProperty("branchCode", branch.getBranchCode());
            response.addProperty("udiseNo", branch.getUdiseNo());
            response.addProperty("affiliationNo", branch.getAffiliationNo());
            response.addProperty("branchContactPerson", branch.getBranchContactPerson());
            response.addProperty("mobileNumber", branch.getMobileNumber());
            response.addProperty("registeredAddress", branch.getRegisteredAddress());
            response.addProperty("corporateAddress", branch.getCorporateAddress());
            response.addProperty("pincode", branch.getPincode());
            response.addProperty("whatsappNumber", branch.getWhatsappNumber());
            response.addProperty("country_id", branch.getCountry().getId());
            response.addProperty("state_id", branch.getState().getId());
            response.addProperty("email", branch.getEmail());
            response.addProperty("website", branch.getWebsite());
            response.addProperty("gstApplicable", branch.getGstApplicable());
            if (branch.getGstApplicable()) {
                response.addProperty("gstType", branch.getGstTypeMaster().getId());
                response.addProperty("gstIn", branch.getGstNumber());
                response.addProperty("gstTypeName", branch.getGstTypeMaster().getGstType());
                response.addProperty("gstApplicableDate", branch.getGstApplicableDate().toString());

            }
            response.addProperty("currency", branch.getCurrency());

            result.addProperty("message", "success");
            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.add("responseObject", response);
        } else {
            result.addProperty("message", "not found");
            result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return result;
    }

    public ResponseMessage updateBranch(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        ResponseMessage responseMessage = new ResponseMessage();
        Long id = Long.parseLong(request.getParameter("id"));
        Branch mBranch = branchRepository.findByIdAndStatus(id, true);
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        try {
            if (mBranch != null) {
                Outlet company = outletRepository.findByIdAndStatus(
                        Long.parseLong(request.getParameter("companyId")), true);
                if (company != null) {
                    mBranch.setOutlet(company);
                    mBranch.setCompanyName(company.getCompanyName());
                    mBranch.setCompanyCode(company.getCompanyCode());
                }
                mBranch.setBranchName(request.getParameter("branchName"));
                if (paramMap.containsKey("branchCode"))
                    mBranch.setBranchCode(request.getParameter("branchCode"));
                else
                    mBranch.setBranchCode("NA");
                if (paramMap.containsKey("udiseNo"))
                    mBranch.setUdiseNo(Long.valueOf(request.getParameter("udiseNo")));
                else
                    mBranch.setUdiseNo(Long.valueOf(0));
                if (paramMap.containsKey("affiliationNo"))
                    mBranch.setAffiliationNo(Long.valueOf(request.getParameter("affiliationNo")));
                else
                    mBranch.setAffiliationNo(Long.valueOf(0));
                if (paramMap.containsKey("mobileNumber"))
                    mBranch.setMobileNumber(Long.parseLong(request.getParameter("mobileNumber")));
                else
                    mBranch.setMobileNumber(Long.valueOf(0));
                if (paramMap.containsKey("whatsappNumber"))
                    mBranch.setWhatsappNumber(Long.parseLong(request.getParameter("whatsappNumber")));
                else
                    mBranch.setWhatsappNumber(Long.valueOf(0L));
                if (paramMap.containsKey("branchContactPerson"))
                    mBranch.setBranchContactPerson(request.getParameter("branchContactPerson"));
                else
                    mBranch.setBranchContactPerson("NA");

                if (paramMap.containsKey("registeredAddress"))
                    mBranch.setRegisteredAddress(request.getParameter("registeredAddress"));
                else
                    mBranch.setRegisteredAddress("NA");
                if (paramMap.containsKey("corporateAddress"))
                    mBranch.setCorporateAddress(request.getParameter("corporateAddress"));
                else
                    mBranch.setCorporateAddress("NA");
                if (paramMap.containsKey("pincode"))
                    mBranch.setPincode(request.getParameter("pincode"));
                else
                    mBranch.setPincode("NA");
                if (paramMap.containsKey("email"))
                    mBranch.setEmail(request.getParameter("email"));
                else
                    mBranch.setEmail("NA");
                Optional<State> state = stateRepository.findById(
                        Long.parseLong(request.getParameter("state_id")));
                Optional<Country> country = countryRepository.findById(Long.parseLong(
                        request.getParameter("country_id")));
                mBranch.setState(state.get());
                mBranch.setCountry(country.get());
                if (paramMap.containsKey("website"))
                    mBranch.setWebsite(request.getParameter("website"));
                else
                    mBranch.setWebsite("NA");
                if (request.getParameter("gstApplicable").equalsIgnoreCase("yes")) {
                    mBranch.setGstApplicable(true);
                    mBranch.setGstNumber(request.getParameter("gstIn"));
                    Optional<GstTypeMaster> gstTypeMaster = gstMasterRepository.findById(
                            Long.parseLong(request.getParameter("gstType")));
                    mBranch.setGstTypeMaster(gstTypeMaster.get());
                    String stateCode = request.getParameter("gstIn").substring(0, 2);
                    mBranch.setStateCode(stateCode);
                    if (paramMap.containsKey("gstApplicableDate")) {
                        LocalDate date = LocalDate.parse(request.getParameter("gstApplicableDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        mBranch.setGstApplicableDate(date);
                    }
                } else {
                    mBranch.setGstApplicable(false);
                    mBranch.setGstNumber("NA");
                    mBranch.setStateCode(state.get().getStateCode());
                    Optional<GstTypeMaster> gstTypeMaster = gstMasterRepository.findById(3L);
                    mBranch.setGstTypeMaster(gstTypeMaster.get());
                }
                mBranch.setCurrency(request.getParameter("currency"));
                branchRepository.save(mBranch);
                responseMessage.setMessage("Branch updated successfully");
                responseMessage.setResponseStatus(HttpStatus.OK.value());
            } else {
                responseMessage.setMessage("Error in branch updation");
                responseMessage.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e) {
            branchLogger.error("Exceptions in updateBranch:" + e.getMessage());
            e.printStackTrace();
        }
        return responseMessage;
    }


    public JsonObject getBranchesByOutlet(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        JsonArray result = new JsonArray();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Long outletId = Long.valueOf(request.getParameter("outletId"));
            List<Branch> list = branchRepository.findByOutletIdAndStatus(outletId, true);
            if (list.size() > 0) {
                for (Branch mBranch : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mBranch.getId());
                    response.addProperty("companyId", mBranch.getOutlet().getId());
                    response.addProperty("companyName",
                            mBranch.getOutlet().getCompanyName());
                    response.addProperty("branchName", mBranch.getBranchName());
                    response.addProperty("branchCode", mBranch.getBranchCode());
                    response.addProperty("mobileNumber", mBranch.getMobileNumber());
                    response.addProperty("branchContactPerson", mBranch.getBranchContactPerson());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }
}
