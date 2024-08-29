package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.CasteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CasteController {
    @Autowired
    private CasteService service;

    @PostMapping(path = "/createCaste")
    public Object createCaste(HttpServletRequest request) {
        return service.createCaste(request);
    }

    /* get all castes of religion */
    @PostMapping(path = "/getCastesByReligion")
    public Object getCastesByReligion(HttpServletRequest request) {
        JsonObject result = service.getCastesByReligion(request);
        return result.toString();
    }

    /* update castes by id*/
    @PostMapping(path = "/updateCaste")
    public Object updateCaste(HttpServletRequest request) {
        return service.updateCaste(request);
    }

    /* Get all castes */
    @GetMapping(path = "/getAllCastes")
    public Object getAllCastes(HttpServletRequest request) {
        JsonObject result = service.getCastes(request);
        return result.toString();
    }

    /* get branch by Id */
    @PostMapping(path = "/getCasteById")
    public Object getCasteById(HttpServletRequest request) {
        return service.getCasteById(request);
    }
}

