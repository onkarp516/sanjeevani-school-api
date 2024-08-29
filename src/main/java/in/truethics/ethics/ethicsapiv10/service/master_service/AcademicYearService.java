package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.school_master.AcademicYear;
import in.truethics.ethics.ethicsapiv10.model.school_master.StudentAdmission;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.AcademicYearRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.BranchRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.StudentAdmissionRepository;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class AcademicYearService {
    private static final Logger academicLogger = LoggerFactory.getLogger(AcademicYearService.class);
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private AcademicYearRepository academicYearRepository;
    @Autowired
    private StudentAdmissionRepository studentAdmissionRepository;

    public Object createAcademicYear(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        AcademicYear academicYear = new AcademicYear();
        String academicYearName=request.getParameter("academicYear");
        AcademicYear CheckAcademicYear=academicYearRepository.findByYearAndStatus(academicYearName,true);
        if(CheckAcademicYear==null) {


            try {
                academicYear.setOutlet(users.getOutlet());
                if (request.getParameterMap().containsKey("branchId")) {
                    Long branchId = Long.valueOf(request.getParameter("branchId"));
                    Branch branch = branchRepository.findByIdAndStatus(branchId, true);
                    academicYear.setBranch(branch);
                } else if (users.getBranch() != null) {
                    academicYear.setBranch(users.getBranch());
                }
                academicYear.setYear(academicYearName);
                academicYear.setCreatedBy(users.getId());
                academicYear.setStatus(true);
                try {
                    AcademicYear mstd = academicYearRepository.save(academicYear);
                    responseObject.setMessage("Academic Year created successfully");
                    responseObject.setResponseStatus(HttpStatus.OK.value());
                } catch (DataIntegrityViolationException e) {
                    academicLogger.error("createAcademicYear -> duplicate exceptoin " + e.getMessage());
                    responseObject.setMessage("Already Exist! Duplicate data not allowed");
                    responseObject.setResponseStatus(HttpStatus.CONFLICT.value());
                } catch (Exception e1) {
                    academicLogger.error("createAcademicYear -> Academic Year creation failed " + e1);
                    responseObject.setMessage("Failed to create academic year");
                    responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } catch (Exception e1) {
                academicLogger.error("createAcademicYear -> Academic Year creation failed " + e1);
                responseObject.setMessage("Failed to create academic year");
                responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }else {
            responseObject.setMessage("Already Exist! Duplicate data not allowed");
            responseObject.setResponseStatus(HttpStatus.CONFLICT.value());
        }
        return responseObject;
    }

    /* Get  all academic */
    public JsonObject getAllAcademicYear(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            List<AcademicYear> list = new ArrayList<>();
            if (users.getBranch() != null) {
                list = academicYearRepository.findByOutletIdAndBranchIdAndStatus(users.getOutlet().getId(), users.getBranch().getId(), true);
            } else {
                list = academicYearRepository.findByOutletIdAndStatus(users.getOutlet().getId(), true);

            }
            if (list.size() > 0) {
                for (AcademicYear mGroup : list) {
                    JsonObject object = new JsonObject();
                    object.addProperty("id", mGroup.getId());
                    object.addProperty("academicYear", mGroup.getYear());
                    object.addProperty("outletId", mGroup.getOutlet().getId());
                    object.addProperty("outletName", mGroup.getOutlet().getCompanyName());
                    if (mGroup.getBranch() != null) {
                        object.addProperty("branchId", mGroup.getBranch().getId());
                        object.addProperty("branchName", mGroup.getBranch().getBranchName());
                    }
                    result.add(object);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            academicLogger.error("getAllAcademicYear -> Academic Year creation failed " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }

    /*Update  the  academic*/
    public Object updateAcademicYear(HttpServletRequest request) {
        ResponseMessage response = new ResponseMessage();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            AcademicYear academicYear = academicYearRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (academicYear != null) {
                if (request.getParameterMap().containsKey("branchId")) {
                    Long branchId = Long.valueOf(request.getParameter("branchId"));
                    Branch branch = branchRepository.findByIdAndStatus(branchId, true);
                    academicYear.setBranch(branch);
                } else if (users.getBranch() != null) {
                    academicYear.setBranch(users.getBranch());
                }
                academicYear.setYear(request.getParameter("academicYear"));
                academicYear.setUpdatedBy(users.getId());
                academicYear.setUpdatedAt(LocalDateTime.now());
                try {
                    academicYearRepository.save(academicYear);
                    response.setMessage("Academic year updated successfully");
                    response.setResponseStatus(HttpStatus.OK.value());
                } catch (Exception e1) {
                    academicLogger.error("updateAcademicYear -> Academic Year update failed " + e1);
                    response.setMessage("Failed to update academic year");
                    response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } else {
                response.setMessage("Data not found");
                response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e) {
            academicLogger.error("updateAcademicYear -> Academic Year update failed " + e);
            response.setMessage("Failed to update academic year");
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    public JsonObject getAcademicYearById(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        try {
            AcademicYear academicYear = academicYearRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (academicYear != null) {
                response.addProperty("id", academicYear.getId());
                response.addProperty("academicYear", academicYear.getYear());
                if (academicYear.getBranch() != null) {
                    response.addProperty("branchId", academicYear.getBranch().getId());
                    response.addProperty("branchName", academicYear.getBranch().getBranchName());
                }
                result.addProperty("message", "success");
                result.addProperty("responseStatus", HttpStatus.OK.value());
                result.add("responseObject", response);
            } else {
                result.addProperty("message", "not found");
                result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            academicLogger.error("getAcademicYear -> find " + e.getMessage());
            result.addProperty("message", "Failed to update academic year");
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

    public JsonObject getAcademicByBranch(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            Long branchId = Long.valueOf(request.getParameter("branchId"));
            List<AcademicYear> list = academicYearRepository.findByBranchIdAndStatus(branchId, true);
            if (list.size() > 0) {
                for (AcademicYear mGroup : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mGroup.getId());
                    response.addProperty("academicYear", mGroup.getYear());
                    if (mGroup.getBranch() != null) {
                        response.addProperty("branchId", mGroup.getBranch().getId());
                        response.addProperty("branchName", mGroup.getBranch().getBranchName());
                    }
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            academicLogger.error("getAcademicByBranch -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }

    public JsonObject DeleteAcademicYear(HttpServletRequest request) {
        Users users=jwtRequestFilter.getUserDataFromToken(request.getHeader("authorization").substring(7));
        JsonObject result= new JsonObject();
        Long academicyearId= Long.valueOf(request.getParameter("academicYearId"));
        AcademicYear academicYear=academicYearRepository.findByIdAndStatus(academicyearId,true);
        if(academicYear!=null)
        {
            List<StudentAdmission> studentAdmission=studentAdmissionRepository.findByOutletIdAndBranchIdAndAcademicYearIdAndStatus(users.getOutlet().getId(),users.getBranch().getId(),academicyearId,true);
            if(studentAdmission.size()==0) {
                academicYearRepository.deleteAcademicYear(academicyearId);
                academicYearRepository.save(academicYear);
                result.addProperty("message", "AcademicYear deleted Successfully !");
                result.addProperty("responseStatus", HttpStatus.OK.value());
            }
            else {
                result.addProperty("message", "AcademicYear is aleardy in use for Admission");
                result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

        }
        return result;
    }
}

