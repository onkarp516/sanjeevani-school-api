package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.school_master.MotherTongue;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.MotherTongueRepository;
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
public class MotherTongueService {
    private static final Logger motherTongueLogger = LoggerFactory.getLogger(MotherTongueService.class);
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private MotherTongueRepository motherTongueRepository;

    public Object createMotherTongue(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            MotherTongue mtg = new MotherTongue();

            mtg.setName(request.getParameter("motherTongueName"));
            mtg.setCreatedBy(users.getId());
            mtg.setStatus(true);

            try {
                MotherTongue motherTongue = motherTongueRepository.save(mtg);
                responseObject.setResponse(motherTongue.getId());
                responseObject.setMessage("Mother Tongue created successfully");
                responseObject.setResponseStatus(HttpStatus.OK.value());
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                motherTongueLogger.error("createMotherTongue -> DataIntegrityViolationException " + e);
                responseObject.setMessage("Already Exist! Duplicate data not allowed");
                responseObject.setResponseStatus(HttpStatus.BAD_REQUEST.value());
            } catch (Exception e1) {
                motherTongueLogger.error("createMotherTongue -> MotherTongue creation error " + e1.getMessage());
                e1.printStackTrace();
                responseObject.setMessage("Failed to create mother tongue");
                responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            motherTongueLogger.error("createMotherTongue -> creation error " + e1.getMessage());
            responseObject.setMessage("Failed to create mother tongue");
            responseObject.setResponseStatus(HttpStatus.BAD_REQUEST.value());
        }
        return responseObject;
    }

    /* get MotherTongue by id */
    public ResponseMessage getMotherTongue(HttpServletRequest request) {
        ResponseMessage response = new ResponseMessage();
        try {
            MotherTongue mtg = motherTongueRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (mtg != null) {
                response.setResponseObject(mtg);
                response.setResponseStatus(HttpStatus.OK.value());
            } else {
                response.setMessage("Data not found");
                response.setResponseStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            motherTongueLogger.error("getMotherTongue -> failed to get data" + e.getMessage());
            response.setMessage("Failed to get data");
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /* Get  all MotherTongue */
    public JsonObject getAllMotherTongue(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            List<MotherTongue> list = motherTongueRepository.findByCreatedByAndStatus(users.getId(), true);
            if (list.size() > 0) {
                for (MotherTongue mGroup : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mGroup.getId());
                    response.addProperty("motherTongueName", mGroup.getName());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            motherTongueLogger.error("getAllMotherTongue -> failed to get data" + e.getMessage());
            res.addProperty("message", "Failed to get data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }


    /*Update  the  motherTongue*/
    public ResponseMessage updateMotherTongue(HttpServletRequest request) {
        ResponseMessage response = new ResponseMessage();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            MotherTongue mGroup = motherTongueRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (mGroup != null) {
                mGroup.setName(request.getParameter("motherTongueName"));
                mGroup.setUpdatedBy(users.getId());
                mGroup.setUpdatedAt(LocalDateTime.now());
                try {
                    motherTongueRepository.save(mGroup);
                    response.setMessage("Mother Tongue updated successfully");
                    response.setResponseStatus(HttpStatus.OK.value());
                } catch (Exception e) {
                    e.printStackTrace();
                    motherTongueLogger.error("updateMotherTongue -> failed to update data" + e.getMessage());
                    response.setMessage("Failed to update data");
                    response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } else {
                response.setMessage("Data Not found");
                response.setResponseStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            motherTongueLogger.error("updateMotherTongue -> failed to update data" + e.getMessage());
            response.setMessage("Failed to update data");
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    public JsonObject getMotherTongueList() {
        JsonObject res = new JsonObject();
        JsonArray result = new JsonArray();
        try {
            List<MotherTongue> list = motherTongueRepository.findByStatus(true);
            if (list.size() > 0) {
                for (MotherTongue mGroup : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mGroup.getId());
                    response.addProperty("motherTongueName", mGroup.getName());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            motherTongueLogger.error("getMotherTongueList -> failed to get data" + e.getMessage());
            res.addProperty("message", "Failed to get data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }
}
