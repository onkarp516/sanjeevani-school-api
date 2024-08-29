package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.MotherTongueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MotherTongueController {
    @Autowired
    private MotherTongueService service;

    @PostMapping(path = "/createMotherTongue")
    public Object createMotherTongue(HttpServletRequest request) {
        return service.createMotherTongue(request);
    }

    /* get MotherTongue by Id */
    @PostMapping(path = "/getMotherTongueById")
    public Object getMotherTongue(HttpServletRequest request) {
        return service.getMotherTongue(request);
    }

    /* Get all MotherTongue of branches */
    @GetMapping(path = "/getAllMotherTongue")
    public Object getAllMotherTongue(HttpServletRequest request) {
        JsonObject result = service.getAllMotherTongue(request);
        return result.toString();
    }

    /* Get all MotherTongue of branches */
    @GetMapping(path = "/getMotherTongueList")
    public Object getMotherTongueList() {
        JsonObject result = service.getMotherTongueList();
        return result.toString();
    }

    /*Update MotherTongue*/
    @PostMapping(path = "/updateMotherTongue")
    public Object updateMotherTongue(HttpServletRequest request) {
        return service.updateMotherTongue(request);
    }
}

