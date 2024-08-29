package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.DivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DivisionController {
    @Autowired
    private DivisionService divisionService;

    @PostMapping(path = "/createDivision")
    public Object createDivision(HttpServletRequest request) {
        JsonObject result = divisionService.createDivision(request);
        return result.toString();
    }

    /* get all divisions of standard */
    @GetMapping(path = "/getAllDivisions")
    public Object getAllDivisions(HttpServletRequest request) {
        JsonObject result = divisionService.getAllDivisions(request);
        return result.toString();
    }

    /* update casts by id*/
    @PostMapping(path = "/updateDivision")
    public Object updateDivision(HttpServletRequest request) {
        JsonObject result = divisionService.updateDivision(request);
        return result.toString();
    }

    /* Get all divisions of  branchs */
    @PostMapping(path = "/getDivisionsByStandard")
    public Object getDivisionsByStandard(HttpServletRequest request) {
        JsonObject result = divisionService.getDivisionsByStandard(request);
        return result.toString();
    }

    /* get branch by Id */
    @PostMapping(path = "/getDivisionById")
    public Object getDivisionById(HttpServletRequest request) {
        JsonObject result = divisionService.getDivisionById(request);
        return result.toString();
    }

}
