package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.school_master.Caste;
import in.truethics.ethics.ethicsapiv10.model.school_master.Religion;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.CasteRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.ReligionRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CasteService {
    private static final Logger casteLogger = LoggerFactory.getLogger(CasteService.class);
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private ReligionRepository religionRepository;
    @Autowired
    private CasteRepository casteRepository;

    public Object createCaste(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Caste cast = new Caste();
            cast.setCasteName(request.getParameter("casteName"));
            Religion rgn = religionRepository.findByIdAndStatus(Long.parseLong(request.getParameter("religionId")), true);
            if (rgn != null) {
                cast.setReligion(rgn);
                cast.setCreatedBy(users.getId());
                cast.setStatus(true);
                try {
                    Religion religion = religionRepository.save(rgn);
                    Caste caste = casteRepository.save(cast);
//                    JsonObject reso=new JsonObject();
//                    reso.addProperty("casteId",cast.getId());
                    responseObject.setResponse(cast.getId());
//                    responseObject.setResponse(reso);
                    responseObject.setMessage("Caste Created Successfully");
                    responseObject.setResponseStatus(HttpStatus.OK.value());
                } catch (Exception e) {
                    casteLogger.error("createCaste -> Caste creation error " + e.getMessage());
                    responseObject.setMessage("Failed to create caste");
                    responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } else {
                responseObject.setMessage("Religion not found, Please try again!");
                responseObject.setResponseStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            casteLogger.error("createCaste -> Caste creation error " + e.getMessage());
            responseObject.setMessage("Failed to create caste");
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return responseObject;
    }

    /* get all cast of religion */
    public JsonObject getCastesByReligion(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        JsonArray result = new JsonArray();
        try {
            Long religionId = Long.parseLong(request.getParameter("religionId"));
            List<Caste> list = casteRepository.findByReligionIdAndStatus(religionId, true);
            if (list.size() > 0) {
                for (Caste mSubgroup : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mSubgroup.getId());
                    response.addProperty("casteName", mSubgroup.getCasteName());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            casteLogger.error("getAllCastsByReligion -> ");
            res.addProperty("message", "Failed to load data");
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());

        }
        return res;
    }

    public ResponseMessage updateCaste(HttpServletRequest request) {
        ResponseMessage response = new ResponseMessage();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Caste caste = casteRepository.findByIdAndStatus(Long.parseLong(request.getParameter("id")), true);
            if (caste != null) {
                caste.setCasteName(request.getParameter("casteName"));
                Religion rgn = religionRepository.findByIdAndStatus(Long.parseLong(request.getParameter("religionId")), true);
                if (rgn != null) {
                    caste.setReligion(rgn);
                    caste.setUpdatedBy(users.getId());
                    caste.setUpdatedAt(LocalDateTime.now());
                    try {
                        casteRepository.save(caste);
                        response.setMessage("Caste updated successfully");
                        response.setResponseStatus(HttpStatus.OK.value());
                    } catch (Exception e) {
                        casteLogger.error("updateCaste -> Caste update error " + e.getMessage());
                        response.setMessage("Failed to update caste");
                        response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    }
                } else {
                    response.setMessage("Religion not found, Please try again!");
                    response.setResponseStatus(HttpStatus.NOT_FOUND.value());
                }
            } else {
                response.setMessage("Caste not found, Please try again!");
                response.setResponseStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            casteLogger.error("updateCaste -> Caste update error " + e.getMessage());
            response.setMessage("Failed to create caste");
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /* Get all castes */
    public JsonObject getCastes(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            List<Caste> list = casteRepository.findByCreatedByAndStatus(users.getId(), true);
            if (list.size() > 0) {
                for (Caste mGroup : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mGroup.getId());
                    response.addProperty("religionId", mGroup.getReligion().getId());
                    response.addProperty("religionName", mGroup.getReligion().getReligionName());
                    response.addProperty("casteName", mGroup.getCasteName());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            casteLogger.error("getCastes -> get Castes error " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }

    public ResponseMessage getCasteById(HttpServletRequest request) {
        ResponseMessage response = new ResponseMessage();
        try {
            Caste caste = casteRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (caste != null) {
                response.setResponseObject(caste);
                response.setResponseStatus(HttpStatus.OK.value());
            } else {
                response.setMessage("Data not found");
                response.setResponseStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            casteLogger.error("getCasteById -> get getCasteById error " + e.getMessage());
            response.setMessage("Failed to load data");
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }
}