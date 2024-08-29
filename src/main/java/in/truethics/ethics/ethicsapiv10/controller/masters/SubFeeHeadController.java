package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.SubFeeHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SubFeeHeadController {
    @Autowired
    private SubFeeHeadService subFeeHeadService;

    @PostMapping(path = "/createSubFeeHead")
    public Object createSubFeeHead(HttpServletRequest request) {
        return subFeeHeadService.createSubFeeHead(request);
    }

    /* Get all paymentHeads of institue */
    @GetMapping(path = "/getAllSubFeeHeads")
    public Object getAllSubFeeHeads(HttpServletRequest request) {
        JsonObject result = subFeeHeadService.getAllSubFeeHeads(request);
        return result.toString();
    }

    /*Update paymentHeads*/
    @PostMapping(path = "/updateSubFeeHead")
    public Object updateSubFeeHead(HttpServletRequest request) {
        return subFeeHeadService.updateSubFeeHead(request);
    }

    /*get MotherTongue by Id*/
    @PostMapping(path = "/getSubFeeHeadById")
    public Object getSubFeeHeadById(HttpServletRequest request) {
        JsonObject result = subFeeHeadService.getSubFeeHeadById(request);
        return result.toString();
    }

    @GetMapping(path = "/getSubFeeHeads")
    public Object getSubFeeHeads(HttpServletRequest request) {
        JsonObject result = subFeeHeadService.getSubFeeHeads(request);
        return result.toString();
    }

    @PostMapping(path = "/getSubFeeHeadsByFeeHead")
    public Object getSubFeeHeadsByFeeHead(HttpServletRequest request) {
        JsonObject result = subFeeHeadService.getSubFeeHeadsByFeeHead(request);
        return result.toString();
    }
}
