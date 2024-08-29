package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.AcademicYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AcademicYearController {
    @Autowired
    private AcademicYearService service;

    @PostMapping(path = "/create_AcademicYear")
    public Object createAcademicYear(HttpServletRequest request) {
        return service.createAcademicYear(request);
    }

    /* Get all academic */
    @GetMapping(path = "/getAllAcademicYear")
    public Object getAllAcademicYear(HttpServletRequest request) {
        JsonObject result = service.getAllAcademicYear(request);
        return result.toString();
    }

    /* Get all academic of branches */
    @PostMapping(path = "/getAcademicYearByBranch")
    public Object getAcademicByBranch(HttpServletRequest request) {
        JsonObject result = service.getAcademicByBranch(request);
        return result.toString();
    }

    /*Update academic*/
    @PostMapping(path = "/updateAcademicYear")
    public Object updateAcademicYear(HttpServletRequest request) {
        return service.updateAcademicYear(request);
    }

    /* get academic by Id */
    @PostMapping(path = "/getAcademicYearById")
    public Object getAcademicYearById(HttpServletRequest request) {
        JsonObject result = service.getAcademicYearById(request);
        return result.toString();
    }


    @PostMapping(path="/delete_academic_year")
    public Object deleteAcYear(HttpServletRequest request)
    {
        JsonObject result=service.DeleteAcademicYear(request);
                return result.toString();
    }

}
