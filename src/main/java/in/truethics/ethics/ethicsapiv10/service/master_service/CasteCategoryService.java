package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.school_master.CasteCategory;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.CasteCategoryRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.CasteRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.ReligionRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.SubCasteRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CasteCategoryService {
    private static final Logger casteCategoryLogger = LoggerFactory.getLogger(CasteCategoryService.class);
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private CasteRepository casteRepository;
    @Autowired
    private CasteCategoryRepository categoryRepository;
    @Autowired
    private ReligionRepository religionRepository;
    @Autowired
    private SubCasteRepository subCasteRepository;

    public ResponseMessage createCasteCategory(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        CasteCategory category = new CasteCategory();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        try {
            category.setCategoryName(request.getParameter("categoryName"));
           /* Religion religion = religionRepository.findByIdAndStatus(Long.parseLong(request.getParameter("religionId")), true);
            if (religion != null) {
                category.setReligion(religion);
            }
            Caste caste = casteRepository.findByIdAndStatus(Long.parseLong(request.getParameter("casteId")), true);
            if (caste != null) {
                category.setCaste(caste);
            }
            SubCaste subCaste = subCasteRepository.findByIdAndStatus(Long.parseLong(request.getParameter("subCasteId")), true);
            if (subCaste != null) {
                category.setSubCaste(subCaste);
            }*/
            category.setCreatedBy(users.getId());
            category.setStatus(true);
            try {
                CasteCategory casteCategory = categoryRepository.save(category);
                responseObject.setResponse(casteCategory.getId());
                responseObject.setMessage("Caste category Created Successfully");
                responseObject.setResponseStatus(HttpStatus.OK.value());
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                casteCategoryLogger.error("createCasteCategory -> category creation error " + e.getMessage());
                responseObject.setMessage("Internal server Error");
                responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            } catch (Exception e1) {
                e1.printStackTrace();
                casteCategoryLogger.error("createCasteCategory -> category creation error " + e1.getMessage());
                responseObject.setMessage("Internal server Error");
                responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            casteCategoryLogger.error("createCasteCategory -> category creation error " + e.getMessage());
            responseObject.setMessage("Internal server Error");
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return responseObject;
    }


    /* get all category of cast */
    public JsonObject getCategoryBySubCaste(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Long subCasteId = Long.parseLong(request.getParameter("subCasteId"));
            List<CasteCategory> list = categoryRepository.findBySubCasteIdAndStatus(subCasteId, true);
            if (list.size() > 0) {
                for (CasteCategory mcategory : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mcategory.getId());
                    response.addProperty("categoryName", mcategory.getCategoryName());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            casteCategoryLogger.error("getCategoryBySubCaste -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }

    public ResponseMessage updateCategory(HttpServletRequest request) {
        ResponseMessage response = new ResponseMessage();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            CasteCategory category = categoryRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (category != null) {
                category.setCategoryName(request.getParameter("categoryName"));
                /*Religion religion = religionRepository.findByIdAndStatus(Long.parseLong(request.getParameter("religionId")), true);
                if (religion != null) {
                    category.setReligion(religion);
                }
                Caste caste = casteRepository.findByIdAndStatus(Long.parseLong(request.getParameter("casteId")), true);
                if (caste != null) {
                    category.setCaste(caste);
                }
                SubCaste subCaste = subCasteRepository.findByIdAndStatus(Long.parseLong(request.getParameter("subCasteId")), true);
                if (subCaste != null) {
                    category.setSubCaste(subCaste);
                }*/
                category.setUpdatedBy(users.getId());
                category.setUpdatedAt(LocalDateTime.now());
                try {
                    categoryRepository.save(category);
                    response.setMessage("Caste category updated successfully");
                    response.setResponseStatus(HttpStatus.OK.value());
                } catch (Exception e) {
                    e.printStackTrace();
                    casteCategoryLogger.error("updateCategory -> failed to update data " + e.getMessage());
                    response.setMessage("Failed to update caste");
                    response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } else {
                response.setMessage("Caste category not found");
                response.setResponseStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            casteCategoryLogger.error("updateCategory -> failed to update data " + e.getMessage());
            response.setMessage("Failed to update data");
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /* Get all Categories */
    public JsonObject getAllCategory(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            List<CasteCategory> list = categoryRepository.findByCreatedByAndStatus(users.getId(), true);
            if (list.size() > 0) {
                for (CasteCategory mctg : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mctg.getId());
                    response.addProperty("categoryName", mctg.getCategoryName());
                    /*response.addProperty("religionId", mctg.getReligion().getId());
                    response.addProperty("religionName", mctg.getReligion().getReligionName());
                    response.addProperty("casteId", mctg.getCaste().getId());
                    response.addProperty("casteName", mctg.getCaste().getCasteName());
                    response.addProperty("subCasteId", mctg.getSubCaste().getId());
                    response.addProperty("subCasteName", mctg.getSubCaste().getSubCasteName());*/
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            casteCategoryLogger.error("getAllCategory -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }


    public JsonObject getCategoryById(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        try {
            CasteCategory category = categoryRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (category != null) {
                response.addProperty("id", category.getId());
                response.addProperty("categoryName", category.getCategoryName());
                /*response.addProperty("casteId", category.getCaste().getId());
                response.addProperty("casteName", category.getCaste().getCasteName());
                response.addProperty("religionId", category.getReligion().getId());
                response.addProperty("religionName", category.getReligion().getReligionName());
                response.addProperty("subCasteId", category.getSubCaste().getId());
                response.addProperty("subCasteName", category.getSubCaste().getSubCasteName());*/

                result.add("responseObject", response);
                result.addProperty("responseStatus", HttpStatus.OK.value());
            } else {
                result.addProperty("message", "Data not found");
                result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            casteCategoryLogger.error("getCategory -> failed to get data " + e.getMessage());
            result.addProperty("message", "Failed to get data");
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

    public JsonObject getCasteCategories(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            List<CasteCategory> list = categoryRepository.findByStatus(true);
            if (list.size() > 0) {
                for (CasteCategory mcategory : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mcategory.getId());
                    response.addProperty("categoryName", mcategory.getCategoryName());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            casteCategoryLogger.error("getCategoryBySubCaste -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }
}
