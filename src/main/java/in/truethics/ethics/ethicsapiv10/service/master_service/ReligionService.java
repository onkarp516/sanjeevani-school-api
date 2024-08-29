package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.school_master.Religion;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.ReligionRepository;
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
public class ReligionService {
    private static final Logger religionLogger = LoggerFactory.getLogger(ReligionService.class);
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private ReligionRepository religionRepository;

    public Object createReligion(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Religion rgn = new Religion();
            rgn.setReligionName(request.getParameter("religionName"));
            rgn.setCreatedBy(users.getId());
            rgn.setStatus(true);
            try {

                Religion religion = religionRepository.save(rgn);
                responseObject.setResponse(religion.getId());
                responseObject.setMessage("Religion created successfully");
                responseObject.setResponseStatus(HttpStatus.OK.value());
            } catch (DataIntegrityViolationException e) {
                religionLogger.error("createReligion -> failed to create religion " + e.getMessage());
                responseObject.setMessage("Already Exist! Duplicate data not allowed");
                responseObject.setResponseStatus(HttpStatus.BAD_REQUEST.value());
            } catch (Exception e1) {
                religionLogger.error("createReligion -> Religion creation error " + e1.getMessage());
                e1.printStackTrace();
                responseObject.setMessage("Failed to create religion");
                responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e1) {
            religionLogger.error("createReligion -> Religion creation error " + e1.getMessage());
            e1.printStackTrace();
            responseObject.setMessage("Failed to create religion");
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return responseObject;
    }


    /* get Religion by id */
    public ResponseMessage getReligion(HttpServletRequest request) {
        ResponseMessage response = new ResponseMessage();
        try {
            Long religionId = Long.parseLong(request.getParameter("id"));
            Religion rgn = religionRepository.findByIdAndStatus(religionId, true);
            if (rgn != null) {
                response.setResponse(rgn);
                response.setResponseStatus(HttpStatus.OK.value());
            } else {
                response.setMessage("Data not found");
                response.setResponseStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e1) {
            religionLogger.error("getReligion -> Religion creation error " + e1.getMessage());
            e1.printStackTrace();
            response.setMessage("Failed to load data");
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /* Get  all Religions */
    public JsonObject getAllReligions(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            List<Religion> list = religionRepository.findByCreatedByAndStatus(users.getId(), true);
            JsonArray result = new JsonArray();
            if (list.size() > 0) {
                for (Religion mGroup : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mGroup.getId());
                    response.addProperty("religionName", mGroup.getReligionName());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e1) {
            religionLogger.error("getAllReligions -> Religion creation error " + e1.getMessage());
            e1.printStackTrace();
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }


    /*Update  the  Standard*/
    public ResponseMessage updateReligion(HttpServletRequest request) {
        ResponseMessage response = new ResponseMessage();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Religion mrgn = religionRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (mrgn != null) {
                mrgn.setReligionName(request.getParameter("religionName"));
                mrgn.setUpdatedBy(users.getId());
                mrgn.setUpdatedAt(LocalDateTime.now());
                try {
                    religionRepository.save(mrgn);
                    response.setMessage("Religion updated successfully");
                    response.setResponseStatus(HttpStatus.OK.value());
                } catch (Exception e1) {
                    religionLogger.error("updateReligion -> Religion update error " + e1.getMessage());
                    e1.printStackTrace();
                    response.setMessage("Failed to update religion");
                    response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } else {
                response.setMessage("Data not found");
                response.setResponseStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e1) {
            religionLogger.error("updateReligion -> Religion update error " + e1.getMessage());
            e1.printStackTrace();
            response.setMessage("Failed to update religion");
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    public JsonObject getReligionsList() {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            List<Religion> list = religionRepository.findByStatus(true);
            if (list.size() > 0) {
                for (Religion mGroup : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mGroup.getId());
                    response.addProperty("religionName", mGroup.getReligionName());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e1) {
            religionLogger.error("getReligionsList -> failed load data " + e1.getMessage());
            e1.printStackTrace();
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }
}
