package in.truethics.ethics.ethicsapiv10.controller.masters;

import in.truethics.ethics.ethicsapiv10.service.master_service.StudentTransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class StudentTransportController {

    @Autowired
    StudentTransportService studentTransportService;


    @PostMapping(path = "/createStudentTransport")
    public Object createStudentTransport(HttpServletRequest request) {
        return studentTransportService.createStudentTransport(request).toString();
    }

    @PostMapping(path = "/getBusAllocatedStudentList")
    public Object getStudentListOfTransport(HttpServletRequest request) {
        return studentTransportService.getBusAllocatedStudentList(request).toString();
    }

    @PostMapping(path = "/getStudentBusForTransaction")
    public Object getStudentBusForTransaction(HttpServletRequest request) {
        return studentTransportService.getStudentBusForTransaction(request).toString();
    }
    @PostMapping(path = "/deleteStudentBus")
    public Object deleteStudentBusTransport(HttpServletRequest request) {
        return studentTransportService.deleteStudentBusTransport(request);

    }

    @PostMapping(path = "/exportExcelStudentTransportData")
    public ResponseEntity<?> exportExcelStudentTransportData(@RequestBody Map<String, String> request, HttpServletRequest req) throws IOException {
        String filename = "student_data_" + LocalDateTime.now() + ".xlsx";
        InputStreamResource file = new InputStreamResource(studentTransportService.sheetLoad(request, req));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
}
