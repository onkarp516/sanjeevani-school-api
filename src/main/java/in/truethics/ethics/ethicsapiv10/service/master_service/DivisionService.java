package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.school_master.Division;
import in.truethics.ethics.ethicsapiv10.model.school_master.Standard;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.BranchRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.DivisionRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.StandardRepository;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DivisionService {
    private static final Logger divisionLogger = LoggerFactory.getLogger(DivisionService.class);
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private DivisionRepository divisionRepository;
    @Autowired
    private BranchRepository branchRepository;

    public JsonObject createDivision(HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        try {
            Division div = new Division();
            div.setDivisionName(request.getParameter("divName"));
            Standard std = standardRepository.findByIdAndStatus(Long.parseLong(request.getParameter("standardId")), true);
            div.setStandard(std);
            if (request.getParameterMap().containsKey("branchId")) {
                Long branchId = Long.valueOf(request.getParameter("branchId"));
                Branch branch = branchRepository.findByIdAndStatus(branchId, true);
                div.setBranch(branch);
            } else if (users.getBranch() != null) {
                div.setBranch(users.getBranch());
            }
            div.setOutlet(users.getOutlet());
            div.setCreatedBy(users.getId());
            div.setStatus(true);
            try {
                Division mSubgroup = divisionRepository.save(div);
                responseObject.addProperty("message", "Division created successfully");
                responseObject.addProperty("responseStatus", HttpStatus.OK.value());
            } catch (Exception e) {
                e.printStackTrace();
                divisionLogger.error("createDivision -> Failed to create division " + e.getMessage());
                responseObject.addProperty("message", "Failed to create division");
                responseObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            divisionLogger.error("createDivision -> Failed to create division " + e.getMessage());
            responseObject.addProperty("message", "Failed to create division");
            responseObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return responseObject;
    }

    /* get all divisions of standards*/
    public JsonObject getAllDivisions(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            List<Division> list = new ArrayList<>();
            if (users.getBranch() != null) {
                list = divisionRepository.findByOutletIdAndBranchIdAndStatus(users.getOutlet().getId(), users.getBranch().getId(), true);
            } else {
                list = divisionRepository.findByOutletIdAndStatus(users.getOutlet().getId(), true);

            }
            for (Division division : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", division.getId());
                response.addProperty("divisionName", division.getDivisionName());
                response.addProperty("standardId", division.getStandard().getId());
                response.addProperty("standardName", division.getStandard().getStandardName());
                response.addProperty("branchId", division.getBranch().getId());
                response.addProperty("branchName", division.getBranch().getBranchName());
                response.addProperty("outletId", division.getOutlet().getId());
                response.addProperty("outletName", division.getOutlet().getCompanyName());
                result.add(response);
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            divisionLogger.error("getAllDivisions -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.OK.value());
        }
        return res;
    }

    public JsonObject updateDivision(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Division div = divisionRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (div != null) {
                div.setDivisionName(request.getParameter("divName"));
                if (request.getParameterMap().containsKey("branchId")) {
                    Long branchId = Long.valueOf(request.getParameter("branchId"));
                    Branch branch = branchRepository.findByIdAndStatus(branchId, true);
                    div.setBranch(branch);
                } else if (users.getBranch() != null) {
                    div.setBranch(users.getBranch());
                }
                Standard std = standardRepository.findByIdAndStatus(Long.parseLong(request.getParameter("standardId")), true);
                div.setStandard(std);
                div.setUpdatedBy(users.getId());
                div.setUpdatedAt(LocalDateTime.now());
                try {
                    divisionRepository.save(div);
                    response.addProperty("message", "Division updated successfully");
                    response.addProperty("responseStatus", HttpStatus.OK.value());
                } catch (Exception e) {
                    e.printStackTrace();
                    divisionLogger.error("updateDivision -> failed to update division " + e.getMessage());
                    response.addProperty("message", "Failed to update division");
                    response.addProperty("responseStatus", HttpStatus.OK.value());
                }
            } else {
                response.addProperty("message", "Data not found");
                response.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            divisionLogger.error("updateDivision -> failed to update division " + e.getMessage());
            response.addProperty("message", "Failed to update division");
            response.addProperty("responseStatus", HttpStatus.OK.value());
        }
        return response;
    }

    /* Get all divisions by branch */
    public JsonObject getDivisionsByStandard(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Long standardId = Long.valueOf(request.getParameter("standardId"));
            List<Division> list = divisionRepository.findByStandardIdAndStatus(standardId, true);
            for (Division mdiv : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mdiv.getId());
                response.addProperty("standardId", mdiv.getStandard().getId());
                response.addProperty("standardName", mdiv.getStandard().getStandardName());
                response.addProperty("divName", mdiv.getDivisionName());
                response.addProperty("branchName", mdiv.getBranch().getBranchName());
                response.addProperty("branchId", mdiv.getBranch().getId());
                result.add(response);
            }
            res.addProperty("message", "success");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        } catch (Exception e) {
            e.printStackTrace();
            divisionLogger.error("getDivisionsByStandard -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }


    public JsonObject getDivisionById(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        try {
            Division div = divisionRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (div != null) {
                response.addProperty("id", div.getId());
                response.addProperty("divName", div.getDivisionName());
                response.addProperty("branchId", div.getBranch().getId());
                response.addProperty("branchName", div.getBranch().getBranchName());
                response.addProperty("standardId", div.getStandard().getId());
                response.addProperty("standardName", div.getStandard().getStandardName());

                result.add("responseObject", response);
                result.addProperty("responseStatus", HttpStatus.OK.value());
            } else {
                result.addProperty("message", "Data not found");
                result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            divisionLogger.error("getDivisionsByStandard -> failed to load data " + e.getMessage());
            result.addProperty("message", "Failed to load data");
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }
}
