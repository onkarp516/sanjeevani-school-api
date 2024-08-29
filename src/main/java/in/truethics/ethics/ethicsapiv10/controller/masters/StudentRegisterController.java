package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.StudentRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@Slf4j
public class StudentRegisterController {
    @Autowired
    private StudentRegisterService service;

    @PostMapping(path = "/createStudent")
    public Object createStudent(MultipartHttpServletRequest request) {
        return service.createStudent(request);
    }

    @PostMapping(path = "/promoteStudent")
    public Object promoteStudent(HttpServletRequest request) {
        return service.promoteStudent(request);
    }

    @GetMapping(path = "/getStudentList")
    public Object getStudentList(HttpServletRequest request) {
        JsonObject result = service.getStudentList(request);
        return result.toString();
    }


    @PostMapping(path = "/getStudentListForPromotion")
    public Object getStudentListforPromotion(HttpServletRequest request) {
        JsonObject jsonObject = service.getStudentListForPromotion(request);
        return jsonObject.toString();
    }


    @PostMapping(path = "/getStudentListForTransaction")
    public Object getStudentListForTransaction(MultipartHttpServletRequest request) {
        JsonObject jsonObject = service.getStudentListForTransaction(request);
        return jsonObject.toString();
    }

    @PostMapping(path = "/get_student_Details_For_Bonafide")
    public Object getStudentDetailsForbonafide(MultipartHttpServletRequest request) {
        JsonObject jsonObject = service.getStudentDetailsForBonafide(request);
        return jsonObject.toString();
    }

    @PostMapping(path = "/saveStudentAdmission")
    public Object saveStudentAdmission(@RequestBody Map<String, String> jsonRequest, HttpServletRequest request) {
        return service.saveStudentAdmission(jsonRequest, request);
    }

    @PostMapping(path = "/getStudentRegisterById")
    public Object getStudentRegisterById(HttpServletRequest request) {
        JsonObject result = service.getStudentRegisterById(request);
        return result.toString();
    }

    @PostMapping(path = "/getStudentDataForPromotion")
    public Object getStudentDataForPromotion(HttpServletRequest request) {
        JsonObject result = service.getStudentDataforPromotions(request);
        return result.toString();
    }


    /*@GetMapping(path = "/getStudentList_for_Student_Admission")
    public Object GetStudentList(HttpServletRequest request) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        log.info("getStudentList_for_Student_Admission executing CRON The time is now {}", dateFormat.format(new Date()));
        return service.getStudentListForStudentAdmission(request);
    }*/

//    @Scheduled(cron = "0 0/10 * * * *")
   /* @Scheduled(cron = "1 * * * * *") // at every minute will executes
    public void demo() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            System.out.println("getStudentList_for_Student_Admission executing CRON The time is now {} " + dateFormat.format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception "+e.getMessage());
        }
    }*/

    @PostMapping(path = "/findStudent")
    public Object findStudent(HttpServletRequest request) {
        return service.findStudents(request);
    }


    @PostMapping(path = "/updateStudent")
    public Object updateStudent(MultipartHttpServletRequest request) {
        return service.updateStudent(request);
    }




    @PostMapping(path = "/createStudentPromotion")
    public Object createStudentPromotionOfStudent(HttpServletRequest request) {
        return service.createStudentPromotion(request);
    }




    @PostMapping(path = "/getStudentListasPerStandard")
    public Object GetStudentListAsPerStandard(HttpServletRequest request) {
        JsonObject result = service.getStudentListAsPerStandard(request);
        return result.toString();

    }

    @PostMapping(path = "/getStudentListByStandard")
    public Object getStudentListByStd(HttpServletRequest request) {
        JsonObject jsonObject = service.getStudentListByStandard(request);
        return jsonObject.toString();
    }

    @PostMapping(path = "/getStudentListforBusTransport")
    public Object getStudentListforBusTransport(HttpServletRequest request) {
        JsonObject result = service.getStudentListforBusTransport(request);
        return result.toString();
    }

    @PostMapping(path = "/deleteStudent")
    public Object deleteStudent(HttpServletRequest request) {
        JsonObject result = service.deleteStudent(request);
        return result.toString();

    }

    @PostMapping(path = "/exportExcelStudentData")
    public ResponseEntity<?> exportExcelStudentData(@RequestBody Map<String, String> request, HttpServletRequest req) throws IOException {
        String filename = "student_data_" + LocalDateTime.now() + ".xlsx";
        InputStreamResource file = new InputStreamResource(service.sheetLoad(request, req));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }


    @GetMapping(path = "/updateInvoiceDates")
    public Object updateInvoiceDates(HttpServletRequest request) {
        JsonObject result = service.updateInvoiceDates(request);
        return result.toString();

    }

    @GetMapping(path = "/rollbackInvoiceDates")
    public Object rollbackInvoiceDates(HttpServletRequest request) {
        JsonObject result = service.rollbackInvoiceDates(request);
        return result.toString();

    }
}
