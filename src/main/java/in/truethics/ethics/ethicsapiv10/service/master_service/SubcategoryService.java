package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.Category;
import in.truethics.ethics.ethicsapiv10.model.master.Subcategory;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.CategoryRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.SubcategoryRepository;
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
public class SubcategoryService {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private SubcategoryRepository subcategoryRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public Object createSubcategory(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        Subcategory subcategory = new Subcategory();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        try {
            subcategory.setSubcategoryName(request.getParameter("subcategoryName"));
            Category category = categoryRepository.findByIdAndStatus(Long.parseLong(request.getParameter("categoryId")), true);
            if (users.getBranch() != null)
                subcategory.setBranch(users.getBranch());
            subcategory.setOutlet(users.getOutlet());
            subcategory.setCategory(category);
            subcategory.setCreatedBy(users.getId());
            subcategory.setUpdatedBy(users.getId());
            subcategory.setStatus(true);
            Subcategory mSubCategory = subcategoryRepository.save(subcategory);
            responseObject.setMessage("Subcategory created successfully");
            responseObject.setResponseStatus(HttpStatus.OK.value());
            responseObject.setResponseObject(mSubCategory.getId().toString());

        } catch (Exception e) {
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseObject.setMessage("Internal Server Error");
        }
        return responseObject;
    }

    /* Get all Subcategories of categories */
    public JsonObject getAllSubCategory(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        Long categoryId = Long.parseLong(request.getParameter("categoryId"));
        List<Subcategory> list = new ArrayList<>();
        list = subcategoryRepository.findByCategoryIdAndStatus(categoryId, true);
        if (list.size() > 0) {
            for (Subcategory mSubCategory : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mSubCategory.getId());
                response.addProperty("subCategoryName", mSubCategory.getSubcategoryName());
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

    /* Get  all SubCategories of Outlets */
    public JsonObject getAllOutletSubCategories(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();

        Long outletId = users.getOutlet().getId();
        List<Subcategory> list = new ArrayList<>();
        list = subcategoryRepository.findByOutletIdAndStatus(outletId, true);
        if (list.size() > 0) {
            for (Subcategory mGroup : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mGroup.getId());
                response.addProperty("groupId", mGroup.getCategory().getSubgroup().getGroup().getId());
                response.addProperty("groupName", mGroup.getCategory().getSubgroup().getGroup().getGroupName());
                response.addProperty("subgroupId", mGroup.getCategory().getSubgroup().getId());
                response.addProperty("subgroupName", mGroup.getCategory().getSubgroup().getSubgroupName());
                response.addProperty("categoryId", mGroup.getCategory().getId());
                response.addProperty("categoryName", mGroup.getCategory().getCategoryName());
                response.addProperty("subcategoryName", mGroup.getSubcategoryName());
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

    public JsonObject getSubCategory(HttpServletRequest request) {
        Subcategory subcategory = subcategoryRepository.findByIdAndStatus(Long.parseLong(request.getParameter("id")), true);
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        if (subcategory != null) {
            response.addProperty("id", subcategory.getId());
            response.addProperty("groupId", subcategory.getCategory().getSubgroup().getGroup().getId());
            response.addProperty("groupName", subcategory.getCategory().getSubgroup().getGroup().getGroupName());
            response.addProperty("subgroupId", subcategory.getCategory().getSubgroup().getId());
            response.addProperty("subgroupName", subcategory.getCategory().getSubgroup().getSubgroupName());
            response.addProperty("categoryId", subcategory.getCategory().getId());
            response.addProperty("categoryName", subcategory.getCategory().getCategoryName());
            response.addProperty("subcategoryName", subcategory.getSubcategoryName());
            result.addProperty("message", "success");
            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.add("responseObject", response);
        } else {
            result.addProperty("message", "not found");
            result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return result;
    }

    public JsonObject updateSubcategory(HttpServletRequest request) {
        Subcategory subcategory = subcategoryRepository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        JsonObject responseObject = new JsonObject();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        try {
            subcategory.setSubcategoryName(request.getParameter("subcategoryName"));
            Category category = categoryRepository.findByIdAndStatus(Long.parseLong(request.getParameter("categoryId")), true);
            subcategory.setCategory(category);
            subcategory.setUpdatedBy(users.getId());
            Subcategory mSubCategory = subcategoryRepository.save(subcategory);
            responseObject.addProperty("message", "Subcategory updated successfully");
            responseObject.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            responseObject.addProperty("message", "error");
            responseObject.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());

        }
        return responseObject;
    }

}
