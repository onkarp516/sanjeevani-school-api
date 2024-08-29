package in.truethics.ethics.ethicsapiv10.service.master_service;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.inventory.Units;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.inventory_repository.UnitsRepository;
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
public class UnitsService {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private UnitsRepository repository;
    @Autowired
    private JwtTokenUtil jwtRequestFilter;


    public Object createUnit(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Units units = new Units();
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            if (users.getBranch() != null)
                units.setBranch(users.getBranch());
            units.setOutlet(users.getOutlet());
            units.setUnitName(request.getParameter("unitName"));
            units.setUnitCode(request.getParameter("unitCode"));
            units.setStatus(true);
            repository.save(units);
            responseMessage.setMessage("unit created sucessfully");
            responseMessage.setResponseStatus(HttpStatus.OK.value());

        } catch (DataIntegrityViolationException e) {
            responseMessage.setMessage("error in unit creation");
            responseMessage.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (Exception e1) {
            e1.getMessage();
        }
        return responseMessage;
    }

    /* Get All units of outlet */
    public JsonObject getUnits(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        List<Units> unitsList = new ArrayList<>();
        unitsList = repository.findByOutletIdAndStatus(users.getOutlet().getId(), true);
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        if (unitsList.size() > 0) {
            for (Units mUnits : unitsList) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mUnits.getId());
                response.addProperty("unitName", mUnits.getUnitName());
                response.addProperty("unitCode", mUnits.getUnitCode());
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

    /* Get units by id */
    public JsonObject getUnitsById(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        Units mUnits = repository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        if (mUnits != null) {
            response.addProperty("id", mUnits.getId());
            response.addProperty("unitName", mUnits.getUnitName());
            response.addProperty("unitCode", mUnits.getUnitCode());
            result.addProperty("message", "success");
            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.add("responseObject", response);
        } else {
            result.addProperty("message", "not found");
            result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return result;
    }

    public JsonObject updateUnit(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Units units = repository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        JsonObject response = new JsonObject();
        try {
            units.setUnitName(request.getParameter("unitName"));
            units.setUnitCode(request.getParameter("unitCode"));
            repository.save(units);
            response.addProperty("message", "unit updated sucessfully");
            response.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (DataIntegrityViolationException e) {
            response.addProperty("message", "Error");
            response.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
            System.out.println(e.getMessage());
        } catch (Exception e1) {
            e1.getMessage();
            response.addProperty("message", "Error");
            response.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
            System.out.println(e1.getMessage());
        }
        return response;
    }

}
