package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.FloorMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class FloorMasterController {
    @Autowired
    private FloorMasterService floorMasterService;

    /* Institute Creation by Super Admin */
    @PostMapping(path = "/create_floor")
    public Object createFloor(HttpServletRequest request) {
        JsonObject object = floorMasterService.createFloor(request);
        return object.toString();
    }

    /* get Floors by Super Admin */
    @PostMapping(path = "/get_floor")
    public Object getFloors(HttpServletRequest request) {
        JsonObject object = floorMasterService.getFloor(request);
        return object.toString();
    }

}
