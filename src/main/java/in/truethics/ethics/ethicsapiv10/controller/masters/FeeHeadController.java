package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.FeeHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class FeeHeadController {
    @Autowired
    private FeeHeadService service;

    @PostMapping(path = "/createFeeHead")
    public Object createFeeHead(HttpServletRequest request) {
        return service.createFeeHead(request);
    }

    /* Get all paymentHeads of institue */
    @GetMapping(path = "/getAllFeeHeads")
    public Object getAllFeeHeads(HttpServletRequest request) {
        JsonObject result = service.getAllFeeHeads(request);
        return result.toString();
    }

    @PostMapping(path = "/updateFeesHeadAccounting")
    public Object upddateFeeHeadAccounting(HttpServletRequest request) {
        return service.updateFeeHeadAccounting(request);
    }

    /* Get all paymentHeads of branches */
    @PostMapping(path = "/getFeeHeadsByBranch")
    public Object getFeeHeadsByBranch(HttpServletRequest request) {
        JsonObject result = service.getFeeHeadsByBranch(request);
        return result.toString();
    }

    /*Update paymentHeads*/
    @PostMapping(path = "/updateFeeHead")
    public Object updateFeeHead(HttpServletRequest request) {
        return service.updateFeeHead(request);
    }

    /* get MotherTongue by Id */
    @PostMapping(path = "/getFeeHeadById")
    public Object getFeeHeadById(HttpServletRequest request) {
        JsonObject result = service.getFeeHeadById(request);
        return result.toString();
    }

    @GetMapping(path = "/getFeeHeads")
    public Object getFeeHeads(HttpServletRequest request) {
        JsonObject result = service.getFeeHeads(request);
        return result.toString();
    }
}

