package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.Category;
import in.truethics.ethics.ethicsapiv10.model.master.Subgroup;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.CategoryRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.GroupRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.SubgroupRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    SubgroupRepository subgroupRepository;
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private GroupRepository groupRepository;

    public Object createCategory(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        Category category = new Category();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        try {
            category.setCategoryName(request.getParameter("categoryName"));
            Subgroup subgroup = subgroupRepository.findByIdAndStatus(Long.parseLong(request.getParameter("brandId")), true);
            if (users.getBranch() != null)
                category.setBranch(users.getBranch());
            category.setOutlet(users.getOutlet());
            category.setSubgroup(subgroup);
            category.setCreatedBy(users.getId());
            category.setUpdatedBy(users.getId());
            category.setStatus(true);
            Category mCategory = categoryRepository.save(category);
            responseObject.setMessage("Category added succussfully");
            responseObject.setResponseStatus(HttpStatus.OK.value());
            responseObject.setResponseObject(mCategory.getId().toString());
        } catch (DataIntegrityViolationException e) {
            responseObject.setResponseStatus(HttpStatus.CONFLICT.value());
            responseObject.setMessage("Already Exist");
        } catch (Exception e) {
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseObject.setMessage("Internal Server Error");
        }
        return responseObject;
    }

    /* Get all categories of Subgroups */
    public JsonObject getAllCategory(HttpServletRequest request) {

        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        Long subgroupId = Long.parseLong(request.getParameter("subgroupId"));
        List<Category> list = new ArrayList<>();
        list = categoryRepository.findBySubgroupIdAndStatus(subgroupId, true);
        if (list.size() > 0) {
            for (Category mCategory : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mCategory.getId());
                response.addProperty("groupName", mCategory.getCategoryName());
                result.add(response);
            }

            res.addProperty("message", "success");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        } else {
            res.addProperty("message", "Empty list");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        }
        return res;
    }

    public JsonObject getCategory(HttpServletRequest request) {
        Category category = categoryRepository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        if (category != null) {
            response.addProperty("id", category.getId());
            response.addProperty("groupId", category.getSubgroup().getGroup().getId());
            response.addProperty("groupName", category.getSubgroup().getGroup().getGroupName());
            response.addProperty("subgroupId", category.getSubgroup().getId());
            response.addProperty("subgroupName", category.getSubgroup().getSubgroupName());
            response.addProperty("categoryName", category.getCategoryName());
            result.addProperty("message", "success");
            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.add("responseObject", response);
        } else {
            result.addProperty("message", "not found");
            result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return result;
    }

    public JsonObject updateCategory(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Category category = categoryRepository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        if (category != null) {
            category.setCategoryName(request.getParameter("categoryName"));
            Subgroup subgroup = subgroupRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("brandId")), true);
            if (subgroup != null)
                category.setSubgroup(subgroup);

            category.setUpdatedBy(users.getId());
            categoryRepository.save(category);
            response.addProperty("message", "Category update successfully");
            response.addProperty("responseStatus", HttpStatus.OK.value());
        } else {
            response.addProperty("message", "Not found");
            response.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return response;
    }

    /* Get  all Categories of Outlets */
    public JsonObject getAllOutletCategories(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        Long outletId = users.getOutlet().getId();
        List<Category> list = new ArrayList<>();
        list = categoryRepository.findByOutletIdAndStatus(outletId, true);
        if (list.size() > 0) {
            for (Category mGroup : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mGroup.getId());
                response.addProperty("groupId", mGroup.getSubgroup().getGroup().getId());
                response.addProperty("groupName", mGroup.getSubgroup().getGroup().getGroupName());
                response.addProperty("subgroupId", mGroup.getSubgroup().getId());
                response.addProperty("subgroupName", mGroup.getSubgroup().getSubgroupName());
                response.addProperty("categoryName", mGroup.getCategoryName());
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
}
