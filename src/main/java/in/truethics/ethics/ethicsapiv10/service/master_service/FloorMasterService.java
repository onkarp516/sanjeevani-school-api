package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.FloorMaster;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.FloorMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class FloorMasterService {
    private static final Logger floorLogger = LoggerFactory.getLogger(FloorMasterService.class);
    @Autowired
    private FloorMasterRepository repository;

    public JsonObject createFloor(HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();
        try {
            FloorMaster floorMaster = new FloorMaster();
            floorMaster.setFloorName(request.getParameter("floor_name"));
            floorMaster.setStatus(true);
            jsonObject.addProperty("message", "success");
            jsonObject.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            floorLogger.error("Exception in CreateFloor:" + e.getMessage());
            jsonObject.addProperty("message", "error");
            jsonObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return jsonObject;
    }

    public JsonObject getFloor(HttpServletRequest request) {
        JsonArray array = new JsonArray();
        JsonObject res = new JsonObject();
        List<FloorMaster> list = new ArrayList<>();
        list = repository.findAllByStatus(true);
        for (FloorMaster mFloor : list) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", mFloor.getId());
            jsonObject.addProperty("name", mFloor.getFloorName());
            array.add(jsonObject);
        }
        if (list != null && list.size() > 0) {

            res.addProperty("message", "success");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("list", array);
        } else {
            res.addProperty("message", "empty list");
            res.addProperty("responseStatus", HttpStatus.NO_CONTENT.value());
            res.add("list", array);
        }
        return res;
    }
}
