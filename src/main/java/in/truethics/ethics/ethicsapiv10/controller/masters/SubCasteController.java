package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.SubCasteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SubCasteController {
    @Autowired
    private SubCasteService service;

    @PostMapping(path = "/create_subCast")
    public Object createSubCast(HttpServletRequest request) {
        return service.createSubCaste(request);
    }

    /* Get all subcasts of casts */
    @PostMapping(path = "/getSubCasteByCaste")
    public Object getSubCasteByCaste(HttpServletRequest request) {
        JsonObject result = service.getSubCasteByCaste(request);
        return result.toString();
    }

    /* Get  all subcasts of branch */
    @GetMapping(path = "/getSubCastes")
    public Object getSubCastes(HttpServletRequest request) {
        JsonObject result = service.getSubCastes(request);
        return result.toString();
    }

    @PostMapping(path = "/getSubCasteById")
    public Object getSubCasteById(HttpServletRequest request) {
        JsonObject result = service.getSubCasteById(request);
        return result.toString();
    }

    /*Update  SubCaste*/
    @PostMapping(path = "/updateSubCaste")
    public Object updateSubCaste(HttpServletRequest request) {
        return service.updateSubCaste(request);
    }
}

