package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.Group;
import in.truethics.ethics.ethicsapiv10.model.master.Subgroup;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.GroupRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.SubgroupRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubgroupService {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    private SubgroupRepository subgroupRepository;

    public Object addSubgroup(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        Subgroup subgroup = new Subgroup();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        try {
            subgroup.setSubgroupName(request.getParameter("brandName"));
            Group group = groupRepository.findByIdAndStatus(Long.parseLong(request.getParameter("groupId")), true);
            subgroup.setBranch(users.getBranch());
            subgroup.setOutlet(users.getOutlet());
            subgroup.setGroup(group);
            subgroup.setCreatedBy(users.getId());
            subgroup.setUpdatedBy(users.getId());
            subgroup.setStatus(true);
            Subgroup mSubgroup = subgroupRepository.save(subgroup);
            responseObject.setMessage("Subgroup Created Successfully");
            responseObject.setResponseStatus(HttpStatus.OK.value());
            responseObject.setResponseObject(mSubgroup.getId().toString());
        } catch (Exception e) {
            responseObject.setMessage("Internal server Error");
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return responseObject;
    }

    /* get all subgroups of group */
    public JsonObject getAllSubGroups(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        Long groupId = Long.parseLong(request.getParameter("groupId"));
        List<Subgroup> list = new ArrayList<>();
        list = subgroupRepository.findByGroupIdAndStatus(groupId, true);
        if (list.size() > 0) {
            for (Subgroup mSubgroup : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mSubgroup.getId());
                response.addProperty("subgroupName", mSubgroup.getSubgroupName());
                result.add(response);
            }
            res.addProperty("message", "success");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);

        } else {
            res.addProperty("message", "empty list");
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        }
        return res;
    }

    /* Get all subgroups of outlets */
    public JsonObject getAllOutletSubGroups(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        Long outletId = users.getOutlet().getId();
        List<Subgroup> list = new ArrayList<>();
        list = subgroupRepository.findByOutletIdAndStatus(outletId, true);
        if (list.size() > 0) {
            for (Subgroup mGroup : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mGroup.getId());
                response.addProperty("groupId", mGroup.getGroup().getId());
                response.addProperty("groupName", mGroup.getGroup().getGroupName());
                response.addProperty("subgroupName", mGroup.getSubgroupName());
                result.add(response);
            }
            res.addProperty("message", "success");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);

        } else {
            res.add("responseObject", result);
            res.addProperty("message", "empty list");
            res.addProperty("responseStatus", HttpStatus.OK.value());
        }
        return res;
    }

    public JsonObject getSubGroupById(HttpServletRequest request) {
        Subgroup subgroup = subgroupRepository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        if (subgroup != null) {
            response.addProperty("id", subgroup.getId());
            response.addProperty("groupId", subgroup.getGroup().getId());
            response.addProperty("groupName", subgroup.getGroup().getGroupName());
            response.addProperty("subgroupName", subgroup.getSubgroupName());
            result.addProperty("message", "success");
            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.add("responseObject", response);
        } else {
            result.addProperty("message", "not found");
            result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return result;
    }

    public JsonObject updateSubgroup(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Subgroup subgroup = subgroupRepository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        JsonObject response = new JsonObject();
        if (subgroup != null) {
            subgroup.setSubgroupName(request.getParameter("brandName"));
            Group group = groupRepository.findByIdAndStatus(Long.parseLong(request.getParameter("groupId")), true);
            subgroup.setGroup(group);
            subgroup.setUpdatedBy(users.getId());
            subgroupRepository.save(subgroup);
            response.addProperty("message", "Subgroup updated successfully");
            response.addProperty("responseStatus", HttpStatus.OK.value());
        } else {
            response.addProperty("message", "Not found");
            response.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return response;
    }

}
