package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.school_master.FeeHead;
import in.truethics.ethics.ethicsapiv10.model.school_master.SubFeeHead;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.BranchRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.FeeHeadRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.SubFeeHeadRepository;
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
import java.util.List;

@Service
public class SubFeeHeadService {
    private static final Logger subFeeHeadLogger = LoggerFactory.getLogger(SubFeeHeadService.class);
    @Autowired
    private SubFeeHeadRepository subFeeHeadRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private FeeHeadRepository feeHeadRepository;

    public Object createSubFeeHead(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        SubFeeHead subFeeHead = new SubFeeHead();
        try {
            if (request.getParameterMap().containsKey("branchId")) {
                Long branchId = Long.valueOf(request.getParameter("branchId"));
                Branch branch = branchRepository.findByIdAndStatus(branchId, true);
                subFeeHead.setBranch(branch);
            } else if (users.getBranch() != null) {
                subFeeHead.setBranch(users.getBranch());
            }

            Long feeHeadId = Long.valueOf(request.getParameter("feeHeadId"));
            FeeHead feeHead = feeHeadRepository.findByIdAndStatus(feeHeadId, true);
            subFeeHead.setFeeHead(feeHead);
            subFeeHead.setOutlet(users.getOutlet());
            subFeeHead.setSubFeeHeadName(request.getParameter("subFeeHeadName"));
            subFeeHead.setStudentType(Integer.valueOf(request.getParameter("studentType")));
            subFeeHead.setCreatedBy(users.getId());
            subFeeHead.setStatus(true);
            try {
                subFeeHeadRepository.save(subFeeHead);
                responseObject.setMessage("Sub Fee head created successfully");
                responseObject.setResponseStatus(HttpStatus.OK.value());
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                subFeeHeadLogger.error("createSubFeeHead -> failed to create sub fee head " + e.getMessage());
                responseObject.setMessage("Already exist! Duplicate data not allowed");
                responseObject.setResponseStatus(HttpStatus.CONFLICT.value());
            } catch (Exception e1) {
                e1.printStackTrace();
                subFeeHeadLogger.error("createSubFeeHead -> failed to create sub fee head " + e1.getMessage());
                responseObject.setMessage("Failed to create fee head");
                responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            subFeeHeadLogger.error("createSubFeeHead -> failed to create sub fee head " + e1.getMessage());
            responseObject.setMessage("Failed to create fee head");
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return responseObject;
    }

    public JsonObject getAllSubFeeHeads(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Long outletId = users.getOutlet().getId();

            List<SubFeeHead> list = new ArrayList<>();
            if (users.getBranch() != null) {
                list = subFeeHeadRepository.findByOutletIdAndBranchIdAndStatus(outletId, users.getBranch().getId(), true);
            } else {
                list = subFeeHeadRepository.findByOutletIdAndStatus(outletId, true);

            }
            if (list.size() > 0) {
                for (SubFeeHead subFeeHead : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", subFeeHead.getId());
                    response.addProperty("subFeeHeadName", subFeeHead.getSubFeeHeadName());
                    response.addProperty("feeHeadName", subFeeHead.getFeeHead().getFeeHeadName());
                    response.addProperty("feeHeadId", subFeeHead.getFeeHead().getId());
                    response.addProperty("feeHeadName", subFeeHead.getFeeHead().getFeeHeadName());
                    response.addProperty("outletName", subFeeHead.getOutlet().getCompanyName());
                    response.addProperty("branchId", subFeeHead.getBranch().getId());
                    response.addProperty("branchName", subFeeHead.getBranch().getBranchName());
                    if (subFeeHead.getStudentType() == 1) {
                        response.addProperty("studentType", "Day Scholar");
                    } else if (subFeeHead.getStudentType() == 2) {
                        response.addProperty("studentType", "Residential");
                    }
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            subFeeHeadLogger.error("getAllSubFeeHeads -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }

    public Object updateSubFeeHead(HttpServletRequest request) {
        ResponseMessage response = new ResponseMessage();
        try {
            Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            SubFeeHead subFeeHead = subFeeHeadRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (subFeeHead != null) {
                subFeeHead.setSubFeeHeadName(request.getParameter("subFeeHeadName"));
                if (request.getParameterMap().containsKey("branchId")) {
                    Long branchId = Long.valueOf(request.getParameter("branchId"));
                    Branch branch = branchRepository.findByIdAndStatus(branchId, true);
                    subFeeHead.setBranch(branch);
                } else if (users.getBranch() != null) {
                    subFeeHead.setBranch(users.getBranch());
                }

                Long feeHeadId = Long.valueOf(request.getParameter("feeHeadId"));
                FeeHead feeHead = feeHeadRepository.findByIdAndStatus(feeHeadId, true);
                subFeeHead.setFeeHead(feeHead);
                subFeeHead.setStudentType(Integer.valueOf(request.getParameter("studentType")));
                subFeeHead.setUpdatedBy(users.getId());
                try {
                    feeHeadRepository.save(feeHead);
                    response.setMessage("Sub Fee Head updated successfully");
                    response.setResponseStatus(HttpStatus.OK.value());
                } catch (Exception e1) {
                    e1.printStackTrace();
                    subFeeHeadLogger.error("updateSubFeeHead -> failed to update sub fee head " + e1.getMessage());
                    response.setMessage("Failed to update fee head");
                    response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } else {
                response.setMessage("Data not found");
                response.setResponseStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            subFeeHeadLogger.error("updateSubFeeHead -> failed to update sub fee head " + e.getMessage());
            response.setMessage("Failed to update sub fee head");
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    public JsonObject getSubFeeHeadById(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        try {
            SubFeeHead subFeeHead = subFeeHeadRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (subFeeHead != null) {
                response.addProperty("id", subFeeHead.getId());
                response.addProperty("subFeeHeadName", subFeeHead.getSubFeeHeadName());
                response.addProperty("studentType", subFeeHead.getStudentType());
                response.addProperty("feeHeadId", subFeeHead.getFeeHead().getId());
                response.addProperty("feeHeadName", subFeeHead.getFeeHead().getFeeHeadName());
                response.addProperty("branchId", subFeeHead.getBranch().getId());
                response.addProperty("branchName", subFeeHead.getBranch().getBranchName());
                result.addProperty("responseStatus", HttpStatus.OK.value());
                result.add("responseObject", response);
            } else {
                result.addProperty("message", "Data not found");
                result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            subFeeHeadLogger.error("getSubFeeHeadById -> failed to load data " + e.getMessage());
            response.addProperty("message", "Failed to load data");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

    public JsonObject getSubFeeHeads(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Long outletId = users.getOutlet().getId();
            List<SubFeeHead> list = subFeeHeadRepository.findByOutletIdAndStatus(outletId, true);
            System.out.println("getFeeHeads --->>>>>>>>>>>>>>>> ");
            {
                for (SubFeeHead subFeeHead : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", subFeeHead.getId());
                    response.addProperty("subFeeHeadName", subFeeHead.getSubFeeHeadName());
                    if (subFeeHead.getStudentType() == 1) {
                        response.addProperty("studentType", "Day Scholar");
                    } else if (subFeeHead.getStudentType() == 2) {
                        response.addProperty("studentType", "Residential");
                    }
                    response.addProperty("outletName", subFeeHead.getOutlet().getCompanyName());
                    response.addProperty("feeHeadId", subFeeHead.getFeeHead().getId());
                    response.addProperty("feeHeadName", subFeeHead.getFeeHead().getFeeHeadName());
                    response.addProperty("branchId", subFeeHead.getBranch().getId());
                    response.addProperty("branchName", subFeeHead.getBranch().getBranchName());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            subFeeHeadLogger.error("getSubFeeHeads -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }

    public JsonObject getSubFeeHeadsByFeeHead(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Long feeHeadId = Long.valueOf(request.getParameter("feeHeadId"));
            List<SubFeeHead> list = subFeeHeadRepository.findByFeeHeadIdAndStatus(feeHeadId, true);

            for (SubFeeHead subFeeHead : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", subFeeHead.getId());
                response.addProperty("feeHeadId", subFeeHead.getFeeHead().getId());
                response.addProperty("subFeeHeadName", subFeeHead.getSubFeeHeadName());
                result.add(response);
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            subFeeHeadLogger.error("getSubFeeHeadsByFeeHead -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }
}
