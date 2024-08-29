package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.TaxMaster;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.TaxMasterRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TaxMasterService {
    private static final Logger taxMasterLogger = LoggerFactory.getLogger(TaxMasterService.class);
    @Autowired
    private TaxMasterRepository repository;
    @Autowired
    private JwtTokenUtil jwtRequestFilter;

    public JsonObject createTaxMaster(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        TaxMaster taxMaster = new TaxMaster();
        Map<String, String[]> paramMap = request.getParameterMap();
        ResponseMessage responseMessage = new ResponseMessage();
        JsonObject result = new JsonObject();
        try {
            if (users.getBranch() != null)
                taxMaster.setBranch(users.getBranch());
            taxMaster.setOutlet(users.getOutlet());
            taxMaster.setGst_per(request.getParameter("gst_per"));
            if (paramMap.containsKey("igst")) {
                taxMaster.setIgst(Double.parseDouble(request.getParameter("igst")));
            }
            if (paramMap.containsKey("cgst")) {
                taxMaster.setCgst(Double.parseDouble(request.getParameter("cgst")));
            }
            if (paramMap.containsKey("sgst")) {
                taxMaster.setSgst(Double.parseDouble(request.getParameter("sgst")));
            }
            taxMaster.setSratio(Double.parseDouble(request.getParameter("sratio")));
            LocalDate applicableDate = LocalDate.parse(request.getParameter("applicable_date"));
            taxMaster.setApplicableDate(applicableDate);
            taxMaster.setStatus(true);
            taxMaster.setCreatedBy(users.getId());
            TaxMaster taxM = repository.save(taxMaster);
            result.addProperty("message", "Tax Master created sucessfully");
            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.addProperty("responseObject", taxM.getId().toString());
        } catch (DataIntegrityViolationException e) {
            taxMasterLogger.error("Error in Tax Master Creation:" + e.getMessage());
            result.addProperty("message", "Error in Tax Master Creation ");
            result.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            e.getMessage();

        } catch (Exception e1) {
            taxMasterLogger.error("Error in Tax Master Creation:" + e1.getMessage());
            e1.getMessage();
        }
        return result;
    }

    public JsonObject getTaxMaster(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        List<TaxMaster> taxMasters = new ArrayList<>();
        taxMasters = repository.findByOutletIdAndStatus(users.getOutlet().getId(), true);
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        if (taxMasters.size() > 0) {
            for (TaxMaster mTaxMaster : taxMasters) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mTaxMaster.getId());
                response.addProperty("igst", mTaxMaster.getIgst());
                response.addProperty("cgst", mTaxMaster.getCgst());
                response.addProperty("sgst", mTaxMaster.getSgst());
                response.addProperty("gst_per", mTaxMaster.getGst_per());
                response.addProperty("ratio", mTaxMaster.getSratio());
                response.addProperty("applicable_date", mTaxMaster.getApplicableDate().toString());
                result.add(response);
            }
            res.addProperty("message", "success");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);

        } else {
            res.addProperty("message", "empty list");
            res.addProperty("responseStatus", HttpStatus.NO_CONTENT.value());
            res.add("responseObject", result);
        }
        return res;
    }

    public JsonObject getTaxMasterbyId(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        TaxMaster taxMasters = repository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        if (taxMasters != null) {
            response.addProperty("id", taxMasters.getId());
            response.addProperty("igst", taxMasters.getIgst());
            response.addProperty("cgst", taxMasters.getCgst());
            response.addProperty("sgst", taxMasters.getSgst());
            response.addProperty("gst_per", taxMasters.getGst_per());
            response.addProperty("ratio", taxMasters.getSratio());
            response.addProperty("applicable_date", taxMasters.getApplicableDate().toString());
            result.addProperty("message", "success");
            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.add("responseObject", response);
        } else {
            result.addProperty("message", "not found");
            result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return result;
    }

    public JsonObject updateTaxMaster(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        TaxMaster taxMaster = repository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        Map<String, String[]> paramMap = request.getParameterMap();
        JsonObject response = new JsonObject();
        try {

            taxMaster.setIgst(Double.parseDouble(request.getParameter("igst")));
            taxMaster.setCgst(Double.parseDouble(request.getParameter("cgst")));
            taxMaster.setSgst(Double.parseDouble(request.getParameter("sgst")));
            taxMaster.setApplicableDate(LocalDate.parse(request.getParameter("applicable_date")));
            taxMaster.setGst_per(request.getParameter("gst_per"));
            taxMaster.setSratio(Double.parseDouble(request.getParameter("sratio")));
            taxMaster.setCreatedBy(users.getId());
            repository.save(taxMaster);
            response.addProperty("message", "Tax Master updated sucessfully");
            response.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (DataIntegrityViolationException e) {
            response.addProperty("message", "error");
            response.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
            System.out.println(e.getMessage());
            e.getMessage();

        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
        return response;

    }
}
