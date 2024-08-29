package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.school_master.Caste;
import in.truethics.ethics.ethicsapiv10.model.school_master.Religion;
import in.truethics.ethics.ethicsapiv10.model.school_master.SubCaste;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
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
public class SubCasteService {
    private static final Logger subCasteLogger = LoggerFactory.getLogger(SubCasteService.class);
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private ReligionRepository religionRepository;
    @Autowired
    private CasteRepository casteRepository;
    @Autowired
    private SubCasteRepository subCasteRepository;

    public Object createSubCaste(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        SubCaste subCaste = new SubCaste();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        try {
            subCaste.setSubCasteName(request.getParameter("subCasteName"));
            Religion religion = religionRepository.findByIdAndStatus(Long.parseLong(request.getParameter("religionId")), true);
            subCaste.setReligion(religion);
            Caste caste = casteRepository.findByIdAndStatus(Long.parseLong(request.getParameter("casteId")), true);
            subCaste.setCaste(caste);
            subCaste.setCreatedBy(users.getId());
            subCaste.setStatus(true);
            try {

                SubCaste subCaste1 = subCasteRepository.save(subCaste);
                responseObject.setResponse(subCaste1.getId());

                responseObject.setMessage("SubCaste created successfully");
                responseObject.setResponseStatus(HttpStatus.OK.value());
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                subCasteLogger.error("createSubCaste -> DataIntegrityViolationException failed to create " + e.getMessage());
                responseObject.setMessage("Already Exist! Duplicate data not allowed");
                responseObject.setResponseStatus(HttpStatus.CONFLICT.value());
            } catch (Exception e) {
                e.printStackTrace();
                subCasteLogger.error("createSubCaste -> failed to create " + e.getMessage());
                responseObject.setMessage("Failed to create sub caste");
                responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            subCasteLogger.error("createSubCaste -> failed to create " + e.getMessage());
            responseObject.setMessage("Failed to create sub caste");
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return responseObject;
    }

    /* Get all subcastes */
    public JsonObject getSubCasteByCaste(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonArray result = new JsonArray();
        try {
            Long casteId = Long.parseLong(request.getParameter("casteId"));
            List<SubCaste> list = subCasteRepository.findByCasteIdAndStatus(casteId, true);
            if (list.size() > 0) {
                for (SubCaste msubcast : list) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", msubcast.getId());
                    jsonObject.addProperty("SubCasteName", msubcast.getSubCasteName());
                    result.add(jsonObject);
                }
            }
            response.add("responseObject", result);
            response.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            subCasteLogger.error("getSubCasteByCaste -> failed to load data " + e.getMessage());
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("responseObject", result);
        }
        return response;
    }

    /* Get  all sub castes */
    public JsonObject getSubCastes(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            List<SubCaste> list = subCasteRepository.findByCreatedByAndStatus(users.getId(), true);
            if (list.size() > 0) {
                for (SubCaste msubcast : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", msubcast.getId());
                    response.addProperty("casteId", msubcast.getCaste().getId());
                    response.addProperty("casteName", msubcast.getCaste().getCasteName());
                    response.addProperty("religionId", msubcast.getReligion().getId());
                    response.addProperty("religionName", msubcast.getReligion().getReligionName());
                    response.addProperty("subCasteName", msubcast.getSubCasteName());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            subCasteLogger.error("getSubCastes -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }


    public JsonObject getSubCasteById(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        try {
            SubCaste subCaste = subCasteRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (subCaste != null) {
                response.addProperty("id", subCaste.getId());
                response.addProperty("id", subCaste.getId());
                response.addProperty("casteId", subCaste.getCaste().getId());
                response.addProperty("casteName", subCaste.getCaste().getCasteName());
                response.addProperty("religionId", subCaste.getReligion().getId());
                response.addProperty("religionName", subCaste.getReligion().getReligionName());
                response.addProperty("subCasteName", subCaste.getSubCasteName());

                result.add("responseObject", response);
                result.addProperty("responseStatus", HttpStatus.OK.value());
            } else {
                result.addProperty("message", "Data not found");
                result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            subCasteLogger.error("getSubCasteById -> failed to load data " + e.getMessage());
            result.addProperty("message", "Failed to load data");
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

    public Object updateSubCaste(HttpServletRequest request) {
        ResponseMessage response = new ResponseMessage();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            SubCaste subCaste = subCasteRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (subCaste != null) {
                subCaste.setSubCasteName(request.getParameter("subCasteName"));
                Caste caste = casteRepository.findByIdAndStatus(Long.parseLong(
                        request.getParameter("casteId")), true);
                if (caste != null)
                    subCaste.setCaste(caste);
                subCaste.setUpdatedBy(users.getId());
                subCaste.setUpdatedAt(LocalDateTime.now());
                try {
                    subCasteRepository.save(subCaste);
                    response.setMessage("Sub caste updated successfully");
                    response.setResponseStatus(HttpStatus.OK.value());
                } catch (Exception e) {
                    e.printStackTrace();
                    subCasteLogger.error("updateSubCaste -> failed to update " + e.getMessage());
                    response.setMessage("Failed to update sub caste");
                    response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } else {
                response.setMessage("Not found");
                response.setResponseStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            subCasteLogger.error("updateSubCaste -> failed to update " + e.getMessage());
            response.setMessage("Failed to update sub caste");
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

}


