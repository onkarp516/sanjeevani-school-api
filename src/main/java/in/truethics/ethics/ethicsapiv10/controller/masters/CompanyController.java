package in.truethics.ethics.ethicsapiv10.controller.masters;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyController {
   /* @Autowired
    private CompanyService companyService;

    @PostMapping(path = "/create_company")
    public ResponseEntity<?> createCompany(HttpServletRequest request) throws ParseException {
        return ResponseEntity.ok(companyService.createCompany(request));
    }

    *//* update Company by id *//*
    @PostMapping(path = "/update_company")
    public Object updateCompany(HttpServletRequest request) throws ParseException {
        JsonObject object = companyService.updateCompany(request);
        return object.toString();
    }

    *//* display all Companies of super admin and its users *//*
     *//*@GetMapping(path = "/get_all_company")
    public Object getAllCompanies(HttpServletRequest request) {
        JsonObject res = companyService.getAllCompanies(request);
        return res.toString();
    }*//*

     *//* get Company by super admin  *//*
    @PostMapping(path = "/get_company_by_id")
    public Object getCompanyById(HttpServletRequest request) {
        JsonObject result = companyService.getCompanyById(request);
        return result.toString();
    }

    *//* get All Companies of institute *//*
     *//*  @GetMapping(path = "/get_companies_by_institute")
    public Object getAllInstituteCompanies(HttpServletRequest request) {
        JsonObject result = companyService.getAllInstituteCompanies(request);
        return result.toString();
    }*//*

     *//* get all companies of users *//*
    @GetMapping(path = "/get_companies_by_users")
    public Object getCompaniesOfUsers(HttpServletRequest request) {
        JsonObject result = companyService.getCompaniesOfUsers(request);
        return result.toString();
    }

    *//* Get GstTypemaster *//*
    @GetMapping(path = "/get_gst_type")
    public Object getGstType() {
        JsonObject res = companyService.getGstType();
        return res.toString();
    }*/
}

