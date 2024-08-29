package in.truethics.ethics.ethicsapiv10.service.inventory_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.inventory.InventorySummary;
import in.truethics.ethics.ethicsapiv10.model.inventory.InventorySummaryTransactionDetails;
import in.truethics.ethics.ethicsapiv10.repository.inventory_repository.InventorySummaryRepository;
import in.truethics.ethics.ethicsapiv10.repository.inventory_repository.InventoryTransactionDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventorySummaryRepository summaryRepository;

    @Autowired
    private InventoryTransactionDetailsRepository transactionDetailsRepository;


    /* Get Current Stocks of specific product */
    public JsonObject getProductCurrentStock(HttpServletRequest request) {
        JsonObject result = new JsonObject();
        try {
            InventorySummary summary = summaryRepository.findByProductId(Long.parseLong(
                    request.getParameter("product_id")));

            if (summary != null) {
                result.addProperty("product_id", summary.getProduct().getId());
                result.addProperty("product_name", summary.getProduct().getProductName());
                result.addProperty("current_stocks", summary.getClosingStock());
                result.addProperty("message", "success");
                result.addProperty("responseStatus", HttpStatus.OK.value());
            } else {
                result.addProperty("message", "error");
                result.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
            }
        } catch (Exception e) {
            result.addProperty("message", "error");
            result.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /* Get Stocks details of specific product */
    public JsonObject getInOut(HttpServletRequest request) {
        JsonObject result = new JsonObject();
        List<InventorySummaryTransactionDetails> tranxDetails = new ArrayList<>();
        try {
            tranxDetails = transactionDetailsRepository.findByProductId(
                    Long.parseLong(request.getParameter("product_id")));
            JsonArray jsonArray = new JsonArray();
            if (tranxDetails.size() > 0) {
                for (InventorySummaryTransactionDetails mDetails : tranxDetails) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("product_id", mDetails.getProduct().getId());
                    jsonObject.addProperty("product_name", mDetails.getProduct().getProductName());
                    jsonObject.addProperty("closing_stocks", mDetails.getClosingStock());
                    jsonObject.addProperty("opening_stocks", mDetails.getOpeningStock());
                    jsonObject.addProperty("in", mDetails.getStockIn());
                    // jsonObject.addProperty("tranx_date", mDetails.getTranxDate());
                    jsonArray.add(jsonObject);
                }
                result.addProperty("message", "success");
                result.addProperty("responseStatus", HttpStatus.OK.value());
                result.add("list", jsonArray);
            } else {
                result.addProperty("message", "empty list");
                result.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
            }
        } catch (Exception e) {
            result.addProperty("message", "error");
            result.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /* Get Current Stocks of all products */
    public JsonObject getStocks(HttpServletRequest request) {
        JsonObject result = new JsonObject();
        List<InventorySummary> inventoryDetails = new ArrayList<>();
        try {
            inventoryDetails = summaryRepository.findAll();
            JsonArray jsonArray = new JsonArray();
            if (inventoryDetails.size() > 0) {
                for (InventorySummary mDetails : inventoryDetails) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("product_id", mDetails.getProduct().getId());
                    jsonObject.addProperty("product_name", mDetails.getProduct().getProductName());
                    jsonObject.addProperty("closing_stocks", mDetails.getClosingStock());
                    jsonArray.add(jsonObject);
                }
                result.addProperty("message", "success");
                result.addProperty("responseStatus", HttpStatus.OK.value());
                result.add("list", jsonArray);
            } else {
                result.addProperty("message", "empty list");
                result.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
            }
        } catch (Exception e) {
            result.addProperty("message", "error");
            result.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
