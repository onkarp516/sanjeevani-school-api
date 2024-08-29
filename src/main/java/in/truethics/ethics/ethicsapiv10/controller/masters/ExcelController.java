package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.underscore.U;
import in.truethics.ethics.ethicsapiv10.model.school_master.Bus;
import in.truethics.ethics.ethicsapiv10.model.school_master.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@RestController
public class ExcelController {
    @Autowired
    ExcelService excelService;

    @PostMapping("/exportDailyCollectionSheet")
    public ResponseEntity<?> getAllCategoryOrdersReport(@RequestBody Map<String, String> jsonRequest) throws IOException {
        String fileName = "Daily_Collection" + LocalDateTime.now() + ".xlsx";
        InputStreamResource file = new InputStreamResource(excelService.exportDailyCollectionSheet(jsonRequest));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @PostMapping("/exportOutstandingListSheet")
    public ResponseEntity<?> getOutstandinglistReport(@RequestBody Map<String, String> jsonRequest) throws IOException {
        String fileName = "Outstanding_List" + LocalDateTime.now() + ".xlsx";
        InputStreamResource file = new InputStreamResource(excelService.exportOutstandingListSheet(jsonRequest));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @PostMapping("/exportFeesPaymentSheetForTally")
    public ResponseEntity<?> exportFeesPaymentSheetForTally(@RequestBody Map<String, String> jsonRequest, HttpServletRequest request) throws IOException {
        String fileName = "Outstanding_List" + LocalDateTime.now() + ".xlsx";
        InputStreamResource file = new InputStreamResource(excelService.exportFeesPaymentSheetForTally(jsonRequest, request));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }


    @PostMapping("/exportFeesPaymentSheetForTallyByReceipt")
    public ResponseEntity<?> exportFeesPaymentSheetForTallyByReceipt(@RequestBody Map<String, String> jsonRequest, HttpServletRequest request) throws IOException {
        String fileName = "Outstanding_List" + LocalDateTime.now() + ".xlsx";
        InputStreamResource file = new InputStreamResource(excelService.exportFeesPaymentSheetForTallyByReceipt(jsonRequest, request));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }


    @GetMapping("/exporFeesDataInXaml")
    public void exporFeesDataInXaml(HttpServletRequest request) throws IOException {
        try {
            String json = "{\"name\":\"JSON\",\"integer\":1,\"double\":2.0,\"boolean\":true,\"nested\":{\"id\":42},\"array\":[1,2,3]}";
            System.out.println(json);
            String xml = U.jsonToXml(json);
            System.out.println(xml);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("EXception "+e.getMessage());
        }
    }
}
