package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.inventory.PackingMaster;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.inventory_repository.PackingMasterRepository;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class PackingMasterService {
    private static final Logger packingLogger = LoggerFactory.getLogger(PackingMasterService.class);
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private PackingMasterRepository repository;

    public JsonObject createPackaging(HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        try {
            PackingMaster packingMaster = new PackingMaster();
            packingMaster.setPackName(request.getParameter("packing_name"));
            if (users.getBranch() != null)
                packingMaster.setBranch(users.getBranch());
            packingMaster.setOutlet(users.getOutlet());
            packingMaster.setStatus(true);
            jsonObject.addProperty("message", "success");
            jsonObject.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            packingLogger.error("Exception in Create Package:" + e.getMessage());
            jsonObject.addProperty("message", "error");
            jsonObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return jsonObject;
    }


    public JsonObject getPackagings(HttpServletRequest request) {
        JsonArray array = new JsonArray();
        JsonObject res = new JsonObject();
        List<PackingMaster> list = new ArrayList<>();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        list = repository.findByOutletIdAndStatus(users.getOutlet().getId(), true);
        for (PackingMaster mPack : list) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", mPack.getId());
            jsonObject.addProperty("name", mPack.getPackName());
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
