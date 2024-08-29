package in.truethics.ethics.ethicsapiv10.controller.tranxs.contra;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.tranx_service.contra.TranxContraNewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TranxContraController {
    //    @Autowired
//    private TranxContraService service;
    @Autowired
    private TranxContraNewService service;

    /* get last records of voucher Contra   */
    @GetMapping(path = "/get_last_record_contra")
    public Object contraLastRecord(HttpServletRequest request) {
        JsonObject result = service.contraLastRecord(request);
        return result.toString();
    }

    /* Create Contra */
    @PostMapping(path = "/create_contra")
    public Object createContra(HttpServletRequest request) {
        JsonObject array = service.createContra(request);
        return array.toString();
    }


    /* Get List of contra   */
    @GetMapping(path = "/get_contra_list_by_outlet")
    public Object contraListbyOutlet(HttpServletRequest request) {
        JsonObject object = service.contraListbyOutlet(request);
        return object.toString();
    }

}
