package in.truethics.ethics.ethicsapiv10.service.master_service;

import org.springframework.stereotype.Service;

@Service
public class CompanyService {
 /*   @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private GstTypeMasterRepository gstMasterRepository;

    public Object createCompany(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        Company company = new Company();
        ResponseMessage responseObject = new ResponseMessage();
        company.setCompanyName(request.getParameter("companyName"));
        if (paramMap.containsKey("companyCode"))
            company.setCompanyCode(request.getParameter("companyCode"));
        else
            company.setCompanyCode("NA");
        if (paramMap.containsKey("registeredAddress"))
            company.setRegisteredAddress(request.getParameter("registeredAddress"));
        else
            company.setRegisteredAddress("NA");
        if (paramMap.containsKey("corporateAddress"))
            company.setCorporateAddress(request.getParameter("corporateAddress"));
        else
            company.setCorporateAddress("NA");
        if (paramMap.containsKey("pincode"))
            company.setPincode(request.getParameter("pincode"));
        else
            company.setPincode("NA");
        if (paramMap.containsKey("mobileNumber"))
            company.setMobileNumber(Long.parseLong(request.getParameter("mobileNumber")));
        else
            company.setMobileNumber(0L);
        if (paramMap.containsKey("whatsappNumber"))
            company.setWhatsappNumber(Long.parseLong(request.getParameter("whatsappNumber")));
        else
            company.setWhatsappNumber(0L);
        if (paramMap.containsKey("email"))
            company.setEmail(request.getParameter("email"));
        else
            company.setEmail("NA");
        Optional<State> state = stateRepository.findById(
                Long.parseLong(request.getParameter("state_id")));
        Optional<Country> country = countryRepository.findById(Long.parseLong(
                request.getParameter("country_id")));
        company.setState(state.get());
        company.setCountry(country.get());
        if (paramMap.containsKey("website"))
            company.setWebsite(request.getParameter("website"));
        else
            company.setWebsite("NA");
        if (request.getParameter("gstApplicable").equalsIgnoreCase("yes")) {
            company.setGstApplicable(true);
            company.setGstNumber(request.getParameter("gstIn"));
            Optional<GstTypeMaster> gstTypeMaster = gstMasterRepository.findById(
                    Long.parseLong(request.getParameter("gstType")));
            company.setGstTypeMaster(gstTypeMaster.get());
            String stateCode = request.getParameter("gstIn").substring(0, 2);
            company.setStateCode(stateCode);
            if (paramMap.containsKey("gstApplicableDate")) {
                LocalDate date = LocalDate.parse(request.getParameter("gstApplicableDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                company.setGstApplicableDate(date);
            }
        } else {
            company.setGstApplicable(false);
            company.setGstNumber("NA");
            company.setStateCode(state.get().getStateCode());
            Optional<GstTypeMaster> gstTypeMaster = gstMasterRepository.findById(3L);
            company.setGstTypeMaster(gstTypeMaster.get());
        }
        company.setCurrency(request.getParameter("currency"));
        if (paramMap.containsKey("companydatapath"))
            company.setCompanyDataPath("companydatapath");
        else
            company.setCompanyDataPath("NA");
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        company.setCreatedBy(users.getId());
        company.setUpdatedBy(users.getId());
        company.setStatus(true);
        try {
            Company mCompany = companyRepository.save(company);
            responseObject.setMessage("Company created successfully");
            responseObject.setResponseStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseObject.setMessage("Internal Server Error");
            e.printStackTrace();
            System.out.println("Exception:" + e.getMessage());
        }
        return responseObject;
    }

    public JsonObject updateCompany(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        Company mCompany = companyRepository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        JsonObject response = new JsonObject();

        Optional<Country> country = countryRepository.findById(Long.parseLong(
                request.getParameter("country_id")));
        Optional<State> state = stateRepository.findById(Long.parseLong(
                request.getParameter("state_id")));
        if (mCompany != null) {

            mCompany.setCompanyName(request.getParameter("companyName"));
            if (paramMap.containsKey("companyCode")) {
                mCompany.setCompanyCode(request.getParameter("companyCode"));
            } else {
                mCompany.setCompanyCode("NA");
            }
            if (country != null)
                mCompany.setCountry(country.get());
            if (state != null)
                mCompany.setState(state.get());
            if (paramMap.containsKey("email"))
                mCompany.setEmail(request.getParameter("email"));
            else
                mCompany.setEmail("NA");
            if (request.getParameter("gstApplicable").equalsIgnoreCase("yes")) {
                mCompany.setGstApplicable(true);
                mCompany.setGstNumber(request.getParameter("gstIn"));
                Optional<GstTypeMaster> gstTypeMaster = gstMasterRepository.findById(
                        Long.parseLong(request.getParameter("gstType")));
                mCompany.setGstTypeMaster(gstTypeMaster.get());
                String stateCode = request.getParameter("gstIn").substring(0, 2);
                mCompany.setStateCode(stateCode);
                if (paramMap.containsKey("gstApplicableDate")) {

                    LocalDate date = LocalDate.parse(request.getParameter("gstApplicableDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    mCompany.setGstApplicableDate(date);
                }
            } else {
                mCompany.setGstApplicable(false);
                mCompany.setGstNumber("NA");
                mCompany.setStateCode(state.get().getStateCode());
                Optional<GstTypeMaster> gstTypeMaster = gstMasterRepository.findById(3L);
                mCompany.setGstTypeMaster(gstTypeMaster.get());
            }
            if (paramMap.containsKey("mobileNumber"))
                mCompany.setMobileNumber(Long.parseLong(request.getParameter("mobileNumber")));
            else
                mCompany.setMobileNumber(0L);
            if (paramMap.containsKey("registeredAddress"))
                mCompany.setRegisteredAddress(request.getParameter("registeredAddress"));
            else
                mCompany.setRegisteredAddress("NA");
            if (paramMap.containsKey("corporateAddress"))
                mCompany.setCorporateAddress(request.getParameter("corporateAddress"));
            else
                mCompany.setCorporateAddress("NA");
            if (paramMap.containsKey("pincode"))
                mCompany.setPincode(request.getParameter("pincode"));
            else
                mCompany.setPincode("NA");
            if (paramMap.containsKey("whatsappNumber"))
                mCompany.setWhatsappNumber(Long.parseLong(request.getParameter("whatsappNumber")));
            else
                mCompany.setWhatsappNumber(0L);
            if (paramMap.containsKey("website"))
                mCompany.setWebsite(request.getParameter("website"));
            else
                mCompany.setWebsite("NA");
            if (paramMap.containsKey("currency"))
                mCompany.setCurrency(request.getParameter("currency"));
            else
                mCompany.setCurrency("NA");
            Users users = jwtRequestFilter.getUserDataFromToken(
                    request.getHeader("Authorization").substring(7));
            mCompany.setUpdatedBy(users.getId());
            companyRepository.save(mCompany);
            response.addProperty("message", "Company updated successfully");
            response.addProperty("responseStatus", HttpStatus.OK.value());
        } else {
            response.addProperty("message", "Not found");
            response.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return response;
    }

  *//*  public JsonObject getAllCompanies(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        List<Company> list = new ArrayList<>();
        if (users.getUserRole().equalsIgnoreCase("SADMIN")) {
            list = companyRepository.findAllByStatus(true);
        } else if (users.getUserRole().equalsIgnoreCase("IADMIN")) {
            list = companyRepository.findByInstituteIdAndCreatedByAndStatus(users.getInstitute().getId(),
                    users.getId(), true);
        } else {
            list = companyRepository.findByCreatedByAndStatus(users.getId(), true);
        }
        JsonObject res = new JsonObject();
        res = getCompanyData(list);
        return res;
    }*//*

    private JsonObject getCompanyData(List<Company> list) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        if (list.size() > 0) {
            for (Company mCompany : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mCompany.getId());
                response.addProperty("companyCode", mCompany.getCompanyCode());
                response.addProperty("registeredAddress", mCompany.getRegisteredAddress());
                response.addProperty("corporateAddress", mCompany.getCorporateAddress());
                response.addProperty("companyName", mCompany.getCompanyName());
                response.addProperty("country", mCompany.getCountry().getId());
                response.addProperty("state", mCompany.getState().getStateCode());
                response.addProperty("email", mCompany.getEmail());
                response.addProperty("gstApplicable", mCompany.getGstApplicable());
                if (mCompany.getGstApplicable()) {
                    response.addProperty("gstType", mCompany.getGstTypeMaster().getId());
                    response.addProperty("gstTypeName", mCompany.getGstTypeMaster().getGstType());
                    response.addProperty("gstApplicableDate", mCompany.getGstApplicableDate() != null ?
                            mCompany.getGstApplicableDate().toString() : "NA");
                }
                response.addProperty("mobile", mCompany.getMobileNumber());
                result.add(response);
                res.addProperty("message", "success");
                res.addProperty("responseStatus", HttpStatus.OK.value());
                res.add("responseObject", result);
            }
        } else {
            res.addProperty("message", "empty list");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        }
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

    public JsonObject getCompanyById(HttpServletRequest request) {
        Company mCompany = companyRepository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        JsonObject response = new JsonObject();
        JsonObject res = new JsonObject();
        if (mCompany != null) {
            response.addProperty("id", mCompany.getId());
            response.addProperty("companyCode", mCompany.getCompanyCode());
            response.addProperty("companyName", mCompany.getCompanyName());
            response.addProperty("registeredAddress", mCompany.getRegisteredAddress());
            response.addProperty("corporateAddress", mCompany.getCorporateAddress());
            response.addProperty("pincode", mCompany.getPincode());
            response.addProperty("mobileNumber", mCompany.getMobileNumber());
            response.addProperty("whatsappNumber", mCompany.getWhatsappNumber());
            response.addProperty("country_id", mCompany.getCountry().getId());
            response.addProperty("state_id", mCompany.getState().getId());
            response.addProperty("email", mCompany.getEmail());
            response.addProperty("website", mCompany.getWebsite());
            response.addProperty("gstApplicable", mCompany.getGstApplicable());
            if (mCompany.getGstApplicable()) {
                response.addProperty("gstType", mCompany.getGstTypeMaster().getId());
                response.addProperty("gstIn", mCompany.getGstNumber());
                response.addProperty("gstTypeName", mCompany.getGstTypeMaster().getGstType());
                response.addProperty("gstApplicableDate", mCompany.getGstApplicableDate().toString());
            }
            response.addProperty("currency", mCompany.getCurrency());
            res.addProperty("message", "success");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", response);
        } else {
            res.addProperty("message", "success");
            res.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            res.add("responseObject", response);
        }
        return res;
    }

    public JsonObject getCompaniesOfUsers(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Long userId = users.getId();
        List<Company> outlets = companyRepository.findByCreatedByAndStatus(userId, true);
        JsonObject res = new JsonObject();
        res = getCompanyData(outlets);
        return res;
    }*/
/*

    public JsonObject getGstType() {
    }*/
}
