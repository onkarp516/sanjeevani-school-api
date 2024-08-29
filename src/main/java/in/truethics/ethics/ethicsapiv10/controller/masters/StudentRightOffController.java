package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.StudentRightOffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class StudentRightOffController {

    @Autowired
    private StudentRightOffService studentRightOffService;

    @PostMapping(path="/getStudentRightOffList")
    public Object getStudentRightOffList(HttpServletRequest request)
    {
        JsonObject jobject= studentRightOffService.getStudentRightOffList(request);
        return jobject.toString();
    }

}
