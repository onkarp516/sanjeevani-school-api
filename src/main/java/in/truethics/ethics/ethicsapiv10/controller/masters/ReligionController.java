package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.ReligionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ReligionController {
    @Autowired
    private ReligionService service;

    @PostMapping(path = "/createReligion")
    public Object createReligion(HttpServletRequest request) {
        return service.createReligion(request);
    }

    /* get religion by Id */
    @PostMapping(path = "/getReligionById")
    public Object getReligion(HttpServletRequest request) {
        return service.getReligion(request);
    }

    /* Get all Standards of branches */
    @GetMapping(path = "/getAllReligion")
    public Object getAllReligions(HttpServletRequest request) {
        JsonObject result = service.getAllReligions(request);
        return result.toString();
    }

    /* Get list religions */
    @GetMapping(path = "/getReligionList")
    public Object getReligionsList() {
        JsonObject result = service.getReligionsList();
        return result.toString();
    }

    /*Update Standards*/
    @PostMapping(path = "/updateReligion")
    public Object updateReligion(HttpServletRequest request) {
        return service.updateReligion(request);
    }
}

