package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.school_master.Standard;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.BranchRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.StandardRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class StandardService {
    private static final Logger standardLogger = LoggerFactory.getLogger(StandardService.class);
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private BranchRepository branchRepository;

    public Object createStandard(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        try {
            Standard std = new Standard();
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            std.setStandardName(request.getParameter("standardName"));
            std.setMaxFee(Double.valueOf(request.getParameter("maxFee")));
            std.setOutlet(users.getOutlet());
            if (request.getParameterMap().containsKey("branchId")) {
                Long branchId = Long.valueOf(request.getParameter("branchId"));
                Branch branch = branchRepository.findByIdAndStatus(branchId, true);
                std.setBranch(branch);
            } else if (users.getBranch() != null) {
                std.setBranch(users.getBranch());
            }
            std.setCreatedBy(users.getId());
            std.setStatus(true);
            try {
                Standard mstd = standardRepository.save(std);
                responseObject.setMessage("Standard created successfully");
                responseObject.setResponseStatus(HttpStatus.OK.value());
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                standardLogger.error("createStandard -> Failed to create standard " + e.getMessage());
                responseObject.setMessage("Failed to create standard");
                responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            } catch (Exception e1) {
                e1.printStackTrace();
                standardLogger.error("createStandard -> Failed to create standard " + e1.getMessage());
                responseObject.setMessage("Failed to create standard");
                responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            standardLogger.error("createStandard -> Failed to create standard " + e1.getMessage());
            responseObject.setMessage("Failed to create standard");
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return responseObject;
    }

    /*get Standard by id*/
    public JsonObject getStandardById(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        try {
            Standard standard = standardRepository.findByIdAndStatus(Long.parseLong(request.getParameter("id")), true);
            if (standard != null) {
                response.addProperty("id", standard.getId());
                response.addProperty("standardName", standard.getStandardName());
                response.addProperty("outletId", standard.getOutlet().getId());
                response.addProperty("outletName", standard.getOutlet().getCompanyName());
                response.addProperty("branchId", standard.getBranch().getId());
                response.addProperty("maxFee",standard.getMaxFee());
                response.addProperty("branchName", standard.getBranch().getBranchName());
                result.addProperty("responseStatus", HttpStatus.OK.value());
                result.add("responseObject", response);
            } else {
                result.addProperty("message", "Data not found");
                result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            standardLogger.error("getStandardById -> Failed to get standard " + e1.getMessage());
            result.addProperty("message", "Failed to load data");
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

    /*Get  all Standards */
    public JsonObject getAllStandards(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Long outletId = users.getOutlet().getId();
            List<Standard> list = new ArrayList<>();
            if (users.getBranch() != null) {
                list = standardRepository.findByOutletIdAndBranchIdAndStatus(outletId, users.getBranch().getId(), true);
            } else {
                list = standardRepository.findByOutletIdAndStatus(outletId, true);
            }
            for (Standard standard : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", standard.getId());
                response.addProperty("standardName", standard.getStandardName());
                response.addProperty("maxFee",standard.getMaxFee());
                response.addProperty("outletId", standard.getOutlet().getId());
                response.addProperty("outletName", standard.getOutlet().getCompanyName());
                response.addProperty("branchId", standard.getBranch().getId());
                response.addProperty("branchName", standard.getBranch().getBranchName());
                result.add(response);
            }
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        } catch (Exception e) {
            e.printStackTrace();
            standardLogger.error("getAllStandards -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }


    /*Update  the  Standard*/
    public JsonObject updateStandard(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Standard standard = standardRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (standard != null) {
                standard.setStandardName(request.getParameter("standardName"));
                standard.setMaxFee(Double.valueOf(request.getParameter("maxFee")));

                standard.setOutlet(users.getOutlet());
                if (request.getParameterMap().containsKey("branchId")) {
                    Long branchId = Long.valueOf(request.getParameter("branchId"));
                    Branch branch = branchRepository.findByIdAndStatus(branchId, true);
                    standard.setBranch(branch);
                } else if (users.getBranch() != null) {
                    standard.setBranch(users.getBranch());
                }
                standard.setUpdatedBy(users.getId());
                standardRepository.save(standard);
                response.addProperty("message", "Standard updated successfully");
                response.addProperty("responseStatus", HttpStatus.OK.value());
            } else {
                response.addProperty("message", "Data not found");
                response.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            standardLogger.error("updateStandard -> failed to update standard " + e.getMessage());
            response.addProperty("message", "Failed to update standard");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    public JsonObject getStandardsByBranch(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Long branchId = Long.valueOf(request.getParameter("branchId"));
            List<Object> list = getStandardList(branchId);
            for (int i = 0; i < list.size(); i++) {

                Standard standard = standardRepository.findByStandardNameAndBranchId(list.get(i).toString(), branchId);

                JsonObject response = new JsonObject();
                response.addProperty("id", standard.getId());
                response.addProperty("standardName", standard.getStandardName());
                response.addProperty("maxFee",standard.getMaxFee());
                result.add(response);
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            standardLogger.error("getStandardsByBranch -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }


    public List<Object> getStandardList(Long branchId) {
        List<Standard> list = standardRepository.findByBranchIdAndStatus(branchId, true);
        List<String> str = new ArrayList<>();
        List<Integer> intArr = new ArrayList<>();
        List<Object> slist = new ArrayList<>();
        for (Standard standard : list) {
            try {
                int stdName = Integer.parseInt(standard.getStandardName());
                intArr.add(stdName);
            } catch (NumberFormatException e) {
                System.out.println("Input String cannot be parsed to Integer.");
                str.add(standard.getStandardName());
            }
        }

        Collections.sort(str);
        Collections.sort(intArr);

        slist.addAll(str);
        slist.addAll(intArr);

        System.out.println("slist " + slist.size());
        System.out.println("slist " + slist);

        return slist;
/*
        for(Standard standard:list) {
            try {
                String stdName = standard.getStandardName();

            } catch ( NumberFormatException e) {
                System.out.println("Input Number Cannot be parsed To String");
                intArr.add(standard.getStandardName());
            }
        }*/
    }

    public JsonObject getMaxFeeStandard(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        try {
            Standard standard = standardRepository.findByIdAndStatus(Long.parseLong(request.getParameter("standardId")), true);
            if (standard != null) {
                response.addProperty("id", standard.getId());
                response.addProperty("maxFee",standard.getMaxFee());

                result.addProperty("responseStatus", HttpStatus.OK.value());
                result.add("responseObject", response);
            } else {
                result.addProperty("message", "Data not found");
                result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            standardLogger.error("getMaxFeeStandard -> Failed to getMaxFeeStandard" + e1.getMessage());
            result.addProperty("message", "Failed to load data");
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }
}
