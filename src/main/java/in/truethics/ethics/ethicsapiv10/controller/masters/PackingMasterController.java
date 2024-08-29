package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.PackingMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PackingMasterController {
    @Autowired
    private PackingMasterService service;

    @PostMapping(path = "/create_packing")
    public Object createPackaging(HttpServletRequest request) {
        JsonObject object = service.createPackaging(request);
        return object.toString();
    }

    /* get packings of by outlet */
    @GetMapping(path = "/get_packings")
    public Object getPackagings(HttpServletRequest request) {
        JsonObject object = service.getPackagings(request);
        return object.toString();
    }

}
