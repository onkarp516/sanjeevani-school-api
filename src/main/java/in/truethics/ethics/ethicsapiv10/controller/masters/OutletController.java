package in.truethics.ethics.ethicsapiv10.controller.masters;


import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.OutletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@RestController
public class OutletController {
    @Autowired
    private OutletService outletService;

    @PostMapping(path = "/create_company")
    public ResponseEntity<?> createOutlet(HttpServletRequest request) throws ParseException {
        return ResponseEntity.ok(outletService.createOutlet(request));
    }

    /* update Company by id */
    @PostMapping(path = "/update_company")
    public Object updateOutlet(HttpServletRequest request) throws ParseException {
        JsonObject object = outletService.updateOutlet(request);
        return object.toString();
    }

    /* get Company by super admin  */
    @PostMapping(path = "/get_company_by_id")
    public Object getOutletById(HttpServletRequest request) {
        JsonObject result = outletService.getOutletById(request);
        return result.toString();
    }

    /* get all companies of super admin */
    @GetMapping(path = "/get_companies_super_admin")
    public Object getOutletsOfSuperAdmin(HttpServletRequest request) {
        JsonObject result = outletService.getOutletsOfSuperAdmin(request);
        return result.toString();
    }

    /* get all companies of super admin for list */
    @GetMapping(path = "/listOfCompanies")
    public Object listOfCompanies(HttpServletRequest request) {
        JsonObject result = outletService.listOfCompanies(request);
        return result.toString();
    }

    /* Get GstTypemaster */
    @GetMapping(path = "/get_gst_type")
    public Object getGstType() {
        JsonObject res = outletService.getGstType();
        return res.toString();
    }
}
