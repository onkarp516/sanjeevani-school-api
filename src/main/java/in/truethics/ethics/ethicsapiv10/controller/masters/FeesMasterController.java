package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.FeesMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class FeesMasterController {
    @Autowired
    private FeesMasterService service;

    @PostMapping(path = "/createFeesMaster")
    public Object createFeesMaster(HttpServletRequest request) {
        JsonObject result = service.createFeesMaster(request);
        return result.toString();
    }

    @GetMapping(path = "/getFeesMasters")
    public Object getFeesMasters(HttpServletRequest request) {
        JsonObject result = service.getFeesMasters(request);
        return result.toString();
    }

    @PostMapping(path = "/getFeesMasterById")
    public Object getFeesMasterById(HttpServletRequest request) {
        JsonObject result = service.getFeesMasterById(request);
        return result.toString();
    }

    @PostMapping(path = "/updateFeesMaster")
    public Object updateFeesMaster(HttpServletRequest request) {
        JsonObject result = service.updateFeesMaster(request);
        return result.toString();
    }

    @PostMapping(path = "/getFeesMasterDetailsForInstallment")
    public Object getFeesMasterDetailsForInstallment(HttpServletRequest request) {
        JsonObject jsonObject = service.getFeesMasterDetailsForInstallment(request);
        return jsonObject.toString();
    }


    @PostMapping(path = "/getFeesMasterDetailsByStandard")
    public Object getFeesMasterDetailsByStandard(HttpServletRequest request) {
        JsonObject jsonObject = service.getFeesMasterDetailsByStandard(request);
        return jsonObject.toString();
    }

    @PostMapping(path = "/get_fees_master_format")
    public Object getFeesMasterformat(HttpServletRequest request) {
        JsonObject jsonObject = service.getFeesMasterformat(request);
        return jsonObject.toString();
    }
}
