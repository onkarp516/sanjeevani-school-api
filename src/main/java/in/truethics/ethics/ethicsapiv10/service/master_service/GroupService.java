package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.Group;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.GroupRepository;
import in.truethics.ethics.ethicsapiv10.response.GenericDatatable;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GroupService {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private GroupRepository groupRepository;

    public Object addGroup(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Group group = new Group();
        try {
            group.setGroupName(request.getParameter("groupName"));
            if (users.getBranch() != null)
                group.setBranch(users.getBranch());
            group.setOutlet(users.getOutlet());
            group.setCreatedBy(users.getId());
            group.setUpdatedBy(users.getId());
            group.setStatus(true);
            Group mGroup = groupRepository.save(group);
            responseObject.setMessage("Group added succussfully");
            responseObject.setResponseStatus(HttpStatus.OK.value());
            responseObject.setResponseObject(mGroup.getId().toString());
        } catch (DataIntegrityViolationException e) {
            responseObject.setMessage("Internal Server Error");
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (Exception e1) {
            responseObject.setMessage("Error");
        }
        return responseObject;
    }

    /* Get  all groups of Outlets */
    public JsonObject getAllGroups(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        Long outletId = users.getOutlet().getId();
        List<Group> list = new ArrayList<>();
        list = groupRepository.findByOutletIdAndStatus(outletId, true);
        if (list.size() > 0) {
            for (Group mGroup : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mGroup.getId());
                response.addProperty("groupName", mGroup.getGroupName());
                result.add(response);
            }
            res.addProperty("message", "success");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);

        } else {
            res.addProperty("message", "empty list");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        }
        return res;
    }

    /* get Group by id */
    public JsonObject getGroup(HttpServletRequest request) {
        Group group = groupRepository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        if (group != null) {
            response.addProperty("id", group.getId());
            response.addProperty("groupName", group.getGroupName());
            result.addProperty("message", "success");
            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.add("responseObject", response);
        } else {
            result.addProperty("message", "not found");
            result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return result;


    }

    public JsonObject updateGroup(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Group mGroup = groupRepository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        if (mGroup != null) {
            mGroup.setGroupName(request.getParameter("groupName"));
            mGroup.setUpdatedBy(users.getId());
            groupRepository.save(mGroup);
            response.addProperty("message", "Group updated succussfully");
            response.addProperty("responseStatus", HttpStatus.OK.value());
        } else {
            response.addProperty("message", "Not found");
            response.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return response;
    }

    public Object DTGroup(Map<String, String> request, HttpServletRequest req) {
        Users users = jwtRequestFilter.getUserDataFromToken(req.getHeader("Authorization").substring(7));
        Long outletId = users.getOutlet().getId();
        Integer from = Integer.parseInt(request.get("from"));
        Integer to = Integer.parseInt(request.get("to"));
        String searchText = request.get("searchText");

        GenericDatatable genericDatatable = new GenericDatatable();
        List<Group> groupList = new ArrayList<>();
        try {
            String query = "SELECT * FROM `group_tbl` WHERE group_tbl.outlet_id='" + outletId
                    + "' AND group_tbl.status=1";

            if (!searchText.equalsIgnoreCase("")) {
                query = query + " AND (group_name LIKE '%" + searchText + "%')";
            }

            String jsonToStr = request.get("sort");
            System.out.println(" sort " + jsonToStr);
            JsonObject jsonObject = new Gson().fromJson(jsonToStr, JsonObject.class);
            if (!jsonObject.get("colId").toString().equalsIgnoreCase("null") &&
                    jsonObject.get("colId").getAsString() != null) {
                System.out.println(" ORDER BY " + jsonObject.get("colId").getAsString());
                String sortBy = jsonObject.get("colId").getAsString();
                query = query + " ORDER BY " + sortBy;
                if (jsonObject.get("isAsc").getAsBoolean()) {
                    query = query + " ASC";
                } else {
                    query = query + " DESC";
                }
            } else {
                query = query + " ORDER BY group_name ASC";
            }
            String query1 = query;
            Integer endLimit = to - from;
            query = query + " LIMIT " + from + ", " + endLimit;
            System.out.println("query " + query);

            Query q = entityManager.createNativeQuery(query, Group.class);
            Query q1 = entityManager.createNativeQuery(query1, Group.class);

            groupList = q.getResultList();
            System.out.println("Limit total rows " + groupList.size());

            List<Group> groupArrayList = new ArrayList<>();
            groupArrayList = q1.getResultList();
            System.out.println("total rows " + groupArrayList.size());

            genericDatatable.setRows(groupList);
            genericDatatable.setTotalRows(groupArrayList.size());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());

            genericDatatable.setRows(groupList);
            genericDatatable.setTotalRows(0);
        }
        return genericDatatable;
    }
}
