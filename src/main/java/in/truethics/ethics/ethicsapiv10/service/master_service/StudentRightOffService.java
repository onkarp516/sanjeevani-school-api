package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.school_master.StudentAdmission;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.RightOffStudent;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.StudentAdmissionRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.StudentRegisterRepository;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.RightOffStudentRepository;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class StudentRightOffService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private StudentRegisterRepository studentRegisterRepository;

    @Autowired
    private StudentAdmissionRepository studentAdmissionRepository;
    @Autowired
    private RightOffStudentRepository rightOffStudentRepository;

    public JsonObject getStudentRightOffList(HttpServletRequest request) {
        JsonObject result = new JsonObject();
        JsonArray res = new JsonArray();
        Map<String, String[]> paramMap = request.getParameterMap();
        Long standardId = 0L;
        Integer studentType = 0;
        Long academicYearId = 0L;
        Users users=jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Long branchId = users.getBranch().getId();
        if (paramMap.containsKey("standardId")) standardId = Long.valueOf(request.getParameter("standardId"));
        if (paramMap.containsKey("studentType")) studentType = Integer.valueOf(request.getParameter("studentType"));
        if (paramMap.containsKey("academicYearId"))
            academicYearId = Long.valueOf(request.getParameter("academicYearId"));
        Long id = users.getOutlet().getId();
try{
        List<StudentAdmission> list = null;
        if (academicYearId != 0 && standardId != 0 && studentType != 0) {
            list = studentAdmissionRepository.findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndStudentTypeAndIsRightOffAndStatus (id, branchId, academicYearId, standardId, studentType, true, true);

        } else if (academicYearId != 0 && standardId != 0) {
            list = studentAdmissionRepository.findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndIsRightOffAndStatus(id, branchId, academicYearId, standardId,true, true);
        } else if (academicYearId != 0 && studentType != 0 ) {
            list = studentAdmissionRepository.findByOutletIdAndBranchIdAndAcademicYearIdAndStudentTypeAndIsRightOffAndStatus(id, branchId, academicYearId,studentType, true, true);
        } else if (standardId != 0 && studentType != 0) {
            list = studentAdmissionRepository.findByOutletIdAndBranchIdAndStandardIdAndStudentTypeAndIsRightOffAndStatus(id, branchId, standardId, studentType,true, true);
        } else if (academicYearId != 0) {
            list =  studentAdmissionRepository.findByOutletIdAndBranchIdAndAcademicYearIdAndIsRightOffAndStatus(id, branchId, academicYearId,true, true);
        } else if (standardId != 0) {
            list = studentAdmissionRepository.findByOutletIdAndBranchIdAndStandardIdAndIsRightOffAndStatus(id, branchId, standardId,true, true);
        } else if (studentType != 0) {
            list =  studentAdmissionRepository.findByOutletIdAndBranchIdAndStudentTypeAndIsRightOffAndStatus(id, branchId, studentType, true,true);
        } else {
            System.out.println("last else executing");
//                list = studentRegisterRepository.getDataByStatus(users.getOutlet().getId(), users.getBranch().getId(),academicyearid, true);
            list = studentAdmissionRepository.findByOutletIdAndBranchIdAndIsRightOffAndStatus(users.getOutlet().getId(), users.getBranch().getId(), true, true);
        }
            for(StudentAdmission studentAdmission:list)
            {
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("studentId", studentAdmission.getStudentRegister().getId());
                jsonObject.addProperty("studentName",studentAdmission.getStudentRegister().getFirstName());
                jsonObject.addProperty("middleName",studentAdmission.getStudentRegister().getMiddleName());
                jsonObject.addProperty("lastName",studentAdmission.getStudentRegister().getLastName());
                RightOffStudent rightOffStudent=rightOffStudentRepository.findByStudentIdAndStatus(studentAdmission.getStudentRegister().getId(),true);
                if(rightOffStudent!=null)
                {

                    jsonObject.addProperty("pendingFees",rightOffStudent.getRightOffAmt());
                    jsonObject.addProperty("terminationreason",rightOffStudent.getRightOffNote());

                }
                if(studentAdmission.getStudentRegister().getDateOfAdmission()!=null)
                {
                    jsonObject.addProperty("doa",studentAdmission.getStudentRegister().getDateOfAdmission().toString());
                }
                if(studentAdmission.getStudentRegister().getMobileNo()!=null)
                {

                    jsonObject.addProperty("mobileNo",studentAdmission.getStudentRegister().getMobileNo());
                }
                jsonObject.addProperty("academicYear",studentAdmission.getAcademicYear().getYear());
                jsonObject.addProperty("standardName",studentAdmission.getStandard().getStandardName());
                if(studentAdmission.getStudentType()==1)
                {

                    jsonObject.addProperty("studentType","Day Scholar");
                }else {
                    jsonObject.addProperty("studentType","Residential");
                }
                res.add(jsonObject);

            }




        System.out.println("list size " + list.size());

        result.add("responseObject", res);
        result.addProperty("responseStatus", HttpStatus.OK.value());

    } catch (Exception e) {
        e.printStackTrace();
        result.addProperty(",message", "Failed to load Data");
        result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
        return result;


}
}
