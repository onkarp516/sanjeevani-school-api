package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DocumentController {
    @Autowired
    private DocumentService service;

    @PostMapping(path = "/createDocument")
    public Object createDocument(HttpServletRequest request) {
        return service.createDocument(request);
    }

    /* Get all MotherTongue of branches */
    @GetMapping(path = "/getAllDocument")
    public Object getAllDocument(HttpServletRequest request) {
        JsonObject result = service.getAllDocument(request);
        return result.toString();
    }

    /*Update MotherTongue*/
    @PostMapping(path = "/updateDocument")
    public Object updateDocument(HttpServletRequest request) {
        JsonObject result = service.updateDocument(request);
        return result.toString();
    }


}

