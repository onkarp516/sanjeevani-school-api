package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.InstallmentMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class InstallmentMasterController {
    @Autowired
    private InstallmentMasterService installmentMasterService;

    @PostMapping(path = "/createInstallmentMaster")
    public Object createInstallmentMaster(HttpServletRequest request) {
        JsonObject result = installmentMasterService.createInstallmentMaster(request);
        return result.toString();
    }

    @PostMapping(path = "/updateInstallmentMaster")
    public Object updateFeesInstallmentMaster(HttpServletRequest request) {
        JsonObject result = installmentMasterService.updateInstallmentMaster(request);
        return result.toString();
    }

    @GetMapping(path = "/getInstallmentMasters")
    public Object getInstallmentMasters(HttpServletRequest request) {
        JsonObject result = installmentMasterService.getInstallmentMasters(request);
        return result.toString();
    }

    @PostMapping(path = "/getInstallments")
    public Object getInstallments(HttpServletRequest request) {
        JsonObject result = installmentMasterService.getInstallments(request);
        return result.toString();
    }

    @PostMapping(path = "/getConcessionsByInstallment")
    public Object getConcessionsByInstallment(HttpServletRequest request) {
        JsonObject result = installmentMasterService.getConcessionsByInstallment(request);
        return result.toString();
    }

    @PostMapping(path = "/getDetailsByInstallment")
    public Object getDetailsByInstallment(HttpServletRequest request) {
        JsonObject result = installmentMasterService.getDetailsByInstallment(request);
        return result.toString();
    }

    @PostMapping(path = "/getFeesInstallmentById")
    public Object getFeesInstallmentById(HttpServletRequest request) {
        JsonObject result = installmentMasterService.getFeesInstallmentById(request);
        return result.toString();
    }

    @PostMapping(path = "/getDetailsByInstallmentForManual")
    public Object getDetailsByInstallmentForManual(HttpServletRequest request) {
        JsonObject result = installmentMasterService.getDetailsByInstallmentForManual(request);
        return result.toString();
    }

    @PostMapping(path = "/getInstallmentMasterByFilter")
    public Object getInstallmentMasterByFilters(HttpServletRequest request) {
        JsonObject jsonObject = installmentMasterService.getInstallmentMasterByFilter(request);
        return jsonObject.toString();
    }

    @PostMapping(path = "/deleteFeesInstallmentMaster")
    public Object deleteFeesInstallmentMaster(HttpServletRequest request) {
        JsonObject jsonObject = installmentMasterService.deleteFeesInstallmentMaster(request);
        return jsonObject.toString();
    }
}
