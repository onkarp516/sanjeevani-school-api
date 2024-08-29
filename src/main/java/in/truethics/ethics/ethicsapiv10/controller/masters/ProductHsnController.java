package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.ProductHsnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ProductHsnController {
    @Autowired
    ProductHsnService service;

    @PostMapping(path = "/create_hsn")
    public ResponseEntity<?> createHsn(HttpServletRequest request) {
        return ResponseEntity.ok(service.createHsn(request));
    }

    /* update Hsn by id */
    @PostMapping(path = "/update_hsn")
    public Object updateHsn(HttpServletRequest request) {
        JsonObject jsonObject = service.updateHsn(request);
        return jsonObject.toString();
    }

    /* Get All Hsn of outlet */
    @GetMapping(path = "/get_hsn_by_outlet")
    public Object getHsn(HttpServletRequest request) {
        JsonObject result = service.getHsn(request);
        return result.toString();
    }

    /* get Hsn by id */
    @PostMapping(path = "/get_hsn_by_id")
    public Object getHsnbyId(HttpServletRequest request) {
        JsonObject result = service.getHsnbyId(request);
        return result.toString();
    }

}
