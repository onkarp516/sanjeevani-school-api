package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.StandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class StandardController {
    @Autowired
    private StandardService standardService;

    @PostMapping(path = "/create_standard")
    public Object createStandard(HttpServletRequest request) {
        return standardService.createStandard(request);
    }

    /* get Standard by Id */
    @PostMapping(path = "/getStandardById")
    public Object getStandardById(HttpServletRequest request) {
        JsonObject result = standardService.getStandardById(request);
        return result.toString();
    }

    /* Get all Standards of institutes */
    @GetMapping(path = "/getAllStandards")
    public Object getAllStandards(HttpServletRequest request) {
        JsonObject result = standardService.getAllStandards(request);
        return result.toString();
    }

    /* Get all Standards of branches */
    @PostMapping(path = "/getStandardsByBranch")
    public Object getStandardsByBranch(HttpServletRequest request) {
        JsonObject result = standardService.getStandardsByBranch(request);
        return result.toString();
    }

    /*Update Standards*/
    @PostMapping(path = "/updateStandard")
    public Object updateStandard(HttpServletRequest request) {
        JsonObject result = standardService.updateStandard(request);
        return result.toString();
    }

    @PostMapping(path = "/getMaxFeeStandard")
    public Object getMaxFeeStandard(HttpServletRequest request) {
        JsonObject result = standardService.getMaxFeeStandard(request);
        return result.toString();
    }


  /*  @PostMapping(path = "/getStandardList")
    public Object getStandardList(HttpServletRequest request) {
        return standardService.getStandardList(request);
    }*/
}
