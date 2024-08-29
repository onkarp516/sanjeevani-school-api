package in.truethics.ethics.ethicsapiv10.service.master_service;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerMasterRepository;
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
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OutletService {
    private static final Logger outletLogger = LoggerFactory.getLogger(OutletService.class);
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private OutletRepository outletRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private GstTypeMasterRepository gstMasterRepository;
    @Autowired
    private PrincipleRepository principleRepository;
    @Autowired
    private PrincipleGroupsRepository principleGroupsRepository;
    @Autowired
    private LedgerMasterRepository ledgerMasterRepository;
    @Autowired
    private BalancingMethodRepository balancingMethodRepository;

    public Object createOutlet(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        Outlet outlet = new Outlet();
        ResponseMessage responseObject = new ResponseMessage();
        if (validateDuplicateCompany(request.getParameter("companyName"))) {
            responseObject.setMessage("Company with this name is already exist");
            responseObject.setResponseStatus(HttpStatus.CONFLICT.value());
        } else {
            try {
                outlet.setCompanyName(request.getParameter("companyName"));
                if (paramMap.containsKey("companyCode"))
                    outlet.setCompanyCode(request.getParameter("companyCode"));
                else
                    outlet.setCompanyCode("NA");


                if (paramMap.containsKey("registeredAddress"))
                    outlet.setRegisteredAddress(request.getParameter("registeredAddress"));
                else
                    outlet.setRegisteredAddress("NA");
                if (paramMap.containsKey("corporateAddress"))
                    outlet.setCorporateAddress(request.getParameter("corporateAddress"));
                else
                    outlet.setCorporateAddress("NA");
                if (paramMap.containsKey("pincode"))
                    outlet.setPincode(request.getParameter("pincode"));
                else
                    outlet.setPincode("NA");
                if (paramMap.containsKey("mobileNumber"))
                    outlet.setMobileNumber(Long.parseLong(request.getParameter("mobileNumber")));
                else
                    outlet.setMobileNumber(0L);
                if (paramMap.containsKey("whatsappNumber"))
                    outlet.setWhatsappNumber(Long.parseLong(request.getParameter("whatsappNumber")));
                else
                    outlet.setWhatsappNumber(0L);
                if (paramMap.containsKey("email"))
                    outlet.setEmail(request.getParameter("email"));
                else
                    outlet.setEmail("NA");
                Optional<State> state = stateRepository.findById(
                        Long.parseLong(request.getParameter("state_id")));
                Optional<Country> country = countryRepository.findById(Long.parseLong(
                        request.getParameter("country_id")));
                outlet.setState(state.get());
                outlet.setCountry(country.get());
                if (paramMap.containsKey("website"))
                    outlet.setWebsite(request.getParameter("website"));
                else
                    outlet.setWebsite("NA");
                if (request.getParameter("gstApplicable").equalsIgnoreCase("yes")) {
                    outlet.setGstApplicable(true);
                    outlet.setGstNumber(request.getParameter("gstIn"));
                    Optional<GstTypeMaster> gstTypeMaster = gstMasterRepository.findById(
                            Long.parseLong(request.getParameter("gstType")));
                    outlet.setGstTypeMaster(gstTypeMaster.get());
                    String stateCode = request.getParameter("gstIn").substring(0, 2);
                    outlet.setStateCode(stateCode);
                    if (paramMap.containsKey("gstApplicableDate")) {
                        LocalDate date = LocalDate.parse(request.getParameter("gstApplicableDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        outlet.setGstApplicableDate(date);
                    }
                } else {
                    outlet.setGstApplicable(false);
                    outlet.setGstNumber("NA");
                    outlet.setStateCode(state.get().getStateCode());
                    Optional<GstTypeMaster> gstTypeMaster = gstMasterRepository.findById(3L);
                    outlet.setGstTypeMaster(gstTypeMaster.get());
                }
                outlet.setCurrency(request.getParameter("currency"));
                if (paramMap.containsKey("companydatapath"))
                    outlet.setCompanyDataPath("companydatapath");
                else
                    outlet.setCompanyDataPath("NA");
                if (paramMap.containsKey("bank_name"))
                    outlet.setBankName(request.getParameter("bank_name"));
                else {
                    outlet.setBankName("NA");
                }
                if (paramMap.containsKey("account_number"))
                    outlet.setAccountNumber(request.getParameter("account_number"));
                else
                    outlet.setAccountNumber("NA");
                if (paramMap.containsKey("ifsc"))
                    outlet.setIfsc(request.getParameter("ifsc"));
                else outlet.setIfsc("NA");
                if (paramMap.containsKey("bank_branch"))
                    outlet.setBankBranch(request.getParameter("bank_branch"));
                else outlet.setBankBranch("NA");
                Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
                outlet.setCreatedBy(users.getId());
                outlet.setStatus(true);
                Outlet mOutlet = outletRepository.save(outlet);
                if (mOutlet != null) {
                    outletRepository.createDefaultLedgers(null,
                            mOutlet.getId(), mOutlet.getCreatedBy());
                    if (mOutlet.getGstTypeMaster().getId() == 1L) {
                        outletRepository.createDefaultGST(null,
                                mOutlet.getId(), mOutlet.getCreatedBy());
                    } else {
                        outletRepository.createDefaultGSTUnRegistered(null,
                                mOutlet.getId(), mOutlet.getCreatedBy());
                    }
                }
                responseObject.setMessage("Company created successfully");
                responseObject.setResponseStatus(HttpStatus.OK.value());
                if (mOutlet.getBankName() != null)
                    createBankAccountLedger(mOutlet, users);

                createHostelAccountLedger(mOutlet, users);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception:" + e.getMessage());
                outletLogger.error("Exceptions in createOutlet:" + e.getMessage());
                responseObject.setMessage("Failed to create company");
                responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
        return responseObject;
    }

    /**** Creating Hostel Branch Ledger  ****/
    private void createHostelAccountLedger(Outlet mOutlet, Users users) {
        LedgerMaster ledgerMaster = new LedgerMaster();
        PrincipleGroups groups = null;
        Principles principles = null;
        Foundations foundations = null;
        LedgerMaster mLedger = null;
        principles = principleRepository.findByIdAndStatus(3L, true);
        foundations = principles.getFoundations();
        groups = principleGroupsRepository.findByIdAndStatus(1L, true);
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
        ledgerMaster.setIsDefaultLedger(true);

        BalancingMethod balancingMethod = balancingMethodRepository.findByIdAndStatus(2L, true);
        ledgerMaster.setBalancingMethod(balancingMethod);

        if (users.getBranch() != null)
            ledgerMaster.setBranch(users.getBranch());
        ledgerMaster.setOutlet(mOutlet);
        ledgerMaster.setCreatedBy(users.getId());
        ledgerMaster.setLedgerName("Hostel Ledger");
        ledgerMaster.setSlugName("sundry_debtors");
        ledgerMaster.setUnderPrefix("PG#1");
        ledgerMaster.setUniqueCode("SUDR");
        ledgerMaster.setTaxType("NA");
        ledgerMaster.setMailingName("Hostel Ledger");
        ledgerMaster.setOpeningBalType("Dr");
        ledgerMaster.setPincode(0L);
        ledgerMaster.setEmail("NA");
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
    }

    /**** Creating Bank Account Ledger  ****/
    private void createBankAccountLedger(Outlet mOutlet, Users users) {
        LedgerMaster ledgerMaster = new LedgerMaster();
        PrincipleGroups groups = null;
        Principles principles = null;
        Foundations foundations = null;
        LedgerMaster mLedger = null;
        principles = principleRepository.findByIdAndStatus(3L, true);
        foundations = principles.getFoundations();
        groups = principleGroupsRepository.findByIdAndStatus(2L, true);
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

        if (users.getBranch() != null)
            ledgerMaster.setBranch(users.getBranch());
        ledgerMaster.setOutlet(mOutlet);
        ledgerMaster.setCreatedBy(users.getId());
        ledgerMaster.setLedgerName(mOutlet.getBankName());
        ledgerMaster.setSlugName("bank_account");
        ledgerMaster.setUnderPrefix("PG#2");
        ledgerMaster.setUniqueCode("BAAC");
        ledgerMaster.setTaxType("NA");
        ledgerMaster.setMailingName(mOutlet.getBankName());
        ledgerMaster.setOpeningBalType("Dr");
        ledgerMaster.setPincode(0L);
        ledgerMaster.setEmail("NA");
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
    }

    private Boolean validateDuplicateCompany(String companyName) {
        Outlet mOutlet = outletRepository.findByCompanyNameIgnoreCaseAndStatus(companyName, true);
        Boolean flag;
        flag = mOutlet != null;
        return flag;
    }

    /* get Outlet by id */
    public JsonObject getOutletById(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject res = new JsonObject();
        try {
            Outlet mOutlet = outletRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (mOutlet != null) {
                response.addProperty("id", mOutlet.getId());
                response.addProperty("companyCode", mOutlet.getCompanyCode());
                response.addProperty("companyName", mOutlet.getCompanyName());
                response.addProperty("registeredAddress", mOutlet.getRegisteredAddress());
                response.addProperty("corporateAddress", mOutlet.getCorporateAddress());
                response.addProperty("pincode", mOutlet.getPincode());
                response.addProperty("mobileNumber", mOutlet.getMobileNumber());
                response.addProperty("whatsappNumber", mOutlet.getWhatsappNumber());
                response.addProperty("country_id", mOutlet.getCountry().getId());
                response.addProperty("state_id", mOutlet.getState().getId());
                response.addProperty("email", mOutlet.getEmail());
                response.addProperty("website", mOutlet.getWebsite());
                response.addProperty("gstApplicable", mOutlet.getGstApplicable());
                if (mOutlet.getGstApplicable()) {
                    response.addProperty("gstType", mOutlet.getGstTypeMaster().getId());
                    response.addProperty("gstIn", mOutlet.getGstNumber());
                    response.addProperty("gstTypeName", mOutlet.getGstTypeMaster().getGstType());
                    response.addProperty("gstApplicableDate", mOutlet.getGstApplicableDate().toString());

                }
                response.addProperty("currency", mOutlet.getCurrency());
                response.addProperty("bankName", mOutlet.getBankName());
                response.addProperty("accountNo", mOutlet.getAccountNumber());
                response.addProperty("ifsc", mOutlet.getIfsc());
                response.addProperty("branchName", mOutlet.getBankBranch());
                res.add("responseObject", response);
                res.addProperty("responseStatus", HttpStatus.OK.value());
            } else {
                res.add("responseObject", response);
                res.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception:" + e.getMessage());
            outletLogger.error("Exceptions in createOutlet:" + e.getMessage());
            res.addProperty("message", "Failed to create company");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }

    public JsonObject updateOutlet(HttpServletRequest request) throws ParseException {
        Map<String, String[]> paramMap = request.getParameterMap();
        JsonObject response = new JsonObject();
        try {
            Outlet mOutlet = outletRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            Optional<Country> country = countryRepository.findById(Long.parseLong(
                    request.getParameter("country_id")));
            Optional<State> state = stateRepository.findById(Long.parseLong(
                    request.getParameter("state_id")));
            if (mOutlet != null) {
                mOutlet.setCompanyName(request.getParameter("companyName"));
                if (paramMap.containsKey("companyCode")) {
                    mOutlet.setCompanyCode(request.getParameter("companyCode"));
                } else {
                    mOutlet.setCompanyCode("NA");
                }
                if (country != null)
                    mOutlet.setCountry(country.get());
                if (state != null)
                    mOutlet.setState(state.get());
                if (paramMap.containsKey("email"))
                    mOutlet.setEmail(request.getParameter("email"));
                else
                    mOutlet.setEmail("NA");
                if (request.getParameter("gstApplicable").equalsIgnoreCase("yes")) {
                    mOutlet.setGstApplicable(true);
                    mOutlet.setGstNumber(request.getParameter("gstIn"));
                    Optional<GstTypeMaster> gstTypeMaster = gstMasterRepository.findById(
                            Long.parseLong(request.getParameter("gstType")));
                    mOutlet.setGstTypeMaster(gstTypeMaster.get());
                    String stateCode = request.getParameter("gstIn").substring(0, 2);
                    mOutlet.setStateCode(stateCode);
                    if (paramMap.containsKey("gstApplicableDate")) {

                        LocalDate date = LocalDate.parse(request.getParameter("gstApplicableDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        mOutlet.setGstApplicableDate(date);
                    }
                } else {
                    mOutlet.setGstApplicable(false);
                    mOutlet.setGstNumber("NA");
                    mOutlet.setStateCode(state.get().getStateCode());
                    Optional<GstTypeMaster> gstTypeMaster = gstMasterRepository.findById(3L);
                    mOutlet.setGstTypeMaster(gstTypeMaster.get());
                }
                if (paramMap.containsKey("mobileNumber"))
                    mOutlet.setMobileNumber(Long.parseLong(request.getParameter("mobileNumber")));
                else
                    mOutlet.setMobileNumber(0L);
                if (paramMap.containsKey("registeredAddress"))
                    mOutlet.setRegisteredAddress(request.getParameter("registeredAddress"));
                else
                    mOutlet.setRegisteredAddress("NA");
                if (paramMap.containsKey("corporateAddress"))
                    mOutlet.setCorporateAddress(request.getParameter("corporateAddress"));
                else
                    mOutlet.setCorporateAddress("NA");
                if (paramMap.containsKey("pincode"))
                    mOutlet.setPincode(request.getParameter("pincode"));
                else
                    mOutlet.setPincode("NA");
                if (paramMap.containsKey("whatsappNumber"))
                    mOutlet.setWhatsappNumber(Long.parseLong(request.getParameter("whatsappNumber")));
                else
                    mOutlet.setWhatsappNumber(0L);
                if (paramMap.containsKey("website"))
                    mOutlet.setWebsite(request.getParameter("website"));
                else
                    mOutlet.setWebsite("NA");
                if (paramMap.containsKey("currency"))
                    mOutlet.setCurrency(request.getParameter("currency"));
                else
                    mOutlet.setCurrency("NA");


                if (paramMap.containsKey("bank_name"))
                    mOutlet.setBankName(request.getParameter("bank_name"));
                else {
                    mOutlet.setBankName("NA");
                }
                if (paramMap.containsKey("account_number"))
                    mOutlet.setAccountNumber(request.getParameter("account_number"));
                else
                    mOutlet.setAccountNumber("NA");
                if (paramMap.containsKey("ifsc"))
                    mOutlet.setIfsc(request.getParameter("ifsc"));
                else mOutlet.setIfsc("NA");
                if (paramMap.containsKey("bank_branch"))
                    mOutlet.setBankBranch(request.getParameter("bank_branch"));
                else mOutlet.setBankBranch("NA");

                Users users = jwtRequestFilter.getUserDataFromToken(
                        request.getHeader("Authorization").substring(7));
                outletRepository.save(mOutlet);
                response.addProperty("message", "Outlet updated successfully");
                response.addProperty("responseStatus", HttpStatus.OK.value());
            } else {
                response.addProperty("message", "Not found");
                response.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception:" + e.getMessage());
            outletLogger.error("updateOutlet -> failed to update:" + e.getMessage());
            response.addProperty("message", "Failed to update company");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    public JsonObject getOutletData(List<Outlet> outlets) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        if (outlets.size() > 0) {
            for (Outlet mOutlet : outlets) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mOutlet.getId());
                response.addProperty("companyCode", mOutlet.getCompanyCode());
                response.addProperty("registeredAddress", mOutlet.getRegisteredAddress());
                response.addProperty("corporateAddress", mOutlet.getCorporateAddress());
                response.addProperty("companyName", mOutlet.getCompanyName());
                response.addProperty("country", mOutlet.getCountry().getId());
                response.addProperty("state", mOutlet.getState().getStateCode());
                response.addProperty("email", mOutlet.getEmail());
                response.addProperty("gstApplicable", mOutlet.getGstApplicable());
                if (mOutlet.getGstApplicable()) {
                    response.addProperty("gstType", mOutlet.getGstTypeMaster().getId());
                    response.addProperty("gstTypeName", mOutlet.getGstTypeMaster().getGstType());
                    response.addProperty("gstApplicableDate", mOutlet.getGstApplicableDate() != null ?
                            mOutlet.getGstApplicableDate().toString() : "NA");
                    // response.addProperty("gstIn", mOutlet.getGstIn());
                }
                response.addProperty("mobile", mOutlet.getMobileNumber());
                result.add(response);
            }
        }
        res.addProperty("responseStatus", HttpStatus.OK.value());
        res.add("responseObject", result);

        return res;
    }

    public JsonObject getGstType() {
        JsonObject res = new JsonObject();
        JsonArray result = new JsonArray();
        List<GstTypeMaster> list = new ArrayList<>();
        list = gstMasterRepository.findAll();
        if (list.size() > 0) {
            for (GstTypeMaster mList : list) {
                if (mList.getId() != 3) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mList.getId());
                    response.addProperty("gstType", mList.getGstType());
                    result.add(response);
                }
            }
            res.addProperty("message", "success");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        } else {
            res.addProperty("message", "empty list");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        }
        return res;
    }

    public JsonObject getOutletsOfSuperAdmin(HttpServletRequest request) {
        List<Outlet> outlets = outletRepository.findByStatus(true);
        JsonObject res = getOutletData(outlets);
        return res;
    }

    public JsonObject listOfCompanies(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        try {
            List<Outlet> outlets = outletRepository.findByStatus(true);
            for (Outlet outlet : outlets) {
                JsonObject object = new JsonObject();

                object.addProperty("id", outlet.getId());
                object.addProperty("companyName", outlet.getCompanyName());
                object.addProperty("companyCode", outlet.getCompanyCode());

                jsonArray.add(object);
            }
            response.add("responseObject", jsonArray);
            response.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            outletLogger.error("listOfCompanies -> failed to load data " + e.getMessage());
            System.out.println("Exception:" + e.getMessage());
            response.addProperty("message", "Failed to load data");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }
}
