package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping(path = "/create_group")
    public ResponseEntity<?> createGroup(HttpServletRequest request) {
        return ResponseEntity.ok(groupService.addGroup(request));
    }

    /* Get all groups of Outlets */
    @GetMapping(path = "/get_outlet_groups")
    public Object getAllGroups(HttpServletRequest request) {
        JsonObject result = groupService.getAllGroups(request);
        return result.toString();
    }

    /* get Group by Id */
    @PostMapping(path = "/get_group")
    public Object getGroup(HttpServletRequest request) {
        JsonObject result = groupService.getGroup(request);
        return result.toString();
    }

    @PostMapping(path = "/update_group")
    public Object updateGroup(HttpServletRequest request) {
        JsonObject result = groupService.updateGroup(request);
        return result.toString();
    }

    @PostMapping(path = "/DTGroup")
    public Object DTGroup(@RequestBody Map<String, String> request, HttpServletRequest req) {
        return groupService.DTGroup(request, req);
    }
}
