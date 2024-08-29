package in.truethics.ethics.ethicsapiv10.service.master_service;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.inventory.ProductHsn;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.ProductHsnRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductHsnService {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private ProductHsnRepository repository;
    @Autowired
    private JwtTokenUtil jwtRequestFilter;

    public Object createHsn(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        ProductHsn productHsn = new ProductHsn();
        Map<String, String[]> paramMap = request.getParameterMap();
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            if (users.getBranch() != null)
                productHsn.setBranch(users.getBranch());
            productHsn.setOutlet(users.getOutlet());
            productHsn.setHsnNumber(request.getParameter("hsnNumber"));
            productHsn.setIgst(Double.parseDouble(request.getParameter("igst").equalsIgnoreCase("") ? String.valueOf(0.0) : request.getParameter("igst")));
            productHsn.setCgst(Double.parseDouble(request.getParameter("cgst").equalsIgnoreCase("") ? String.valueOf(0.0) : request.getParameter("cgst")));
            productHsn.setSgst(Double.parseDouble(request.getParameter("sgst").equalsIgnoreCase("") ? String.valueOf(0.0) : request.getParameter("sgst")));
            productHsn.setStatus(true);
            if (paramMap.containsKey("description")) {
                productHsn.setDescription(request.getParameter("description"));
            } else {
                productHsn.setDescription("NA");
            }
            productHsn.setType(request.getParameter("type"));
            ProductHsn mHsn = repository.save(productHsn);
            responseMessage.setMessage("HSN created sucessfully");
            responseMessage.setResponseObject(mHsn.getId().toString());
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        } catch (DataIntegrityViolationException e) {
            responseMessage.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseMessage.setMessage("Internal Server Error");
            e.getMessage();

        } catch (Exception e1) {
            e1.getMessage();
        }
        return responseMessage;
    }

    /* Get All Hsn of outlet */
    public JsonObject getHsn(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        List<ProductHsn> hsnList = new ArrayList<>();
        hsnList = repository.findByOutletIdAndStatus(users.getOutlet().getId(), true);
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        if (hsnList.size() > 0) {
            for (ProductHsn mHsn : hsnList) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mHsn.getId());
                response.addProperty("hsnno", mHsn.getHsnNumber());
                response.addProperty("hsndesc", mHsn.getDescription());
                /*response.addProperty("igst", mHsn.getIgst());
                response.addProperty("cgst", mHsn.getCgst());
                response.addProperty("sgst", mHsn.getSgst());*/
                response.addProperty("type", mHsn.getType());
                result.add(response);
            }
            res.addProperty("message", "success");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);

        } else {
            res.addProperty("message", "empty list");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        }
        return res;

    }

    public JsonObject getHsnbyId(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        ProductHsn mHsn = repository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        if (mHsn != null) {
            response.addProperty("id", mHsn.getId());
            response.addProperty("hsnno", mHsn.getHsnNumber());
            response.addProperty("hsndesc", mHsn.getDescription());
            /*response.addProperty("igst", mHsn.getIgst());
            response.addProperty("cgst", mHsn.getCgst());
            response.addProperty("sgst", mHsn.getSgst());*/
            response.addProperty("type", mHsn.getType());
            result.addProperty("message", "success");
            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.add("responseObject", response);
        } else {
            result.addProperty("message", "not found");
            result.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
        }
        return result;
    }

    public JsonObject updateHsn(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        ProductHsn productHsn = repository.findByIdAndStatus(Long.parseLong(
                request.getParameter("id")), true);
        Map<String, String[]> paramMap = request.getParameterMap();
        JsonObject response = new JsonObject();
        try {
            productHsn.setHsnNumber(request.getParameter("hsnNumber"));
            productHsn.setType(request.getParameter("type"));
            productHsn.setIgst(Double.parseDouble(request.getParameter("igst").equalsIgnoreCase("") ? String.valueOf(0.0) : request.getParameter("igst")));
            productHsn.setCgst(Double.parseDouble(request.getParameter("cgst").equalsIgnoreCase("") ? String.valueOf(0.0) : request.getParameter("cgst")));
            productHsn.setSgst(Double.parseDouble(request.getParameter("sgst").equalsIgnoreCase("") ? String.valueOf(0.0) : request.getParameter("sgst")));
            productHsn.setUpdatedBy(users.getId());
            if (paramMap.containsKey("description"))
                productHsn.setDescription(request.getParameter("description"));
            else {
                productHsn.setDescription("NA");
            }
            repository.save(productHsn);
            response.addProperty("message", "HSN updated sucessfully");
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
