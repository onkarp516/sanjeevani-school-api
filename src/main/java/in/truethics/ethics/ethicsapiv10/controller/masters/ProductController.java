package in.truethics.ethics.ethicsapiv10.controller.masters;


import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping(path = "/create_product")
    public ResponseEntity<?> createProduct(MultipartHttpServletRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    /* Get Product by id for edit */
    @PostMapping(path = "/get_product_edit")
    public ResponseEntity<?> getProductById(HttpServletRequest request) {
        return ResponseEntity.ok(productService.getProductById(request));
    }

    /* get all products of outlet (second use) */
    @GetMapping(path = "/getAllProduct")
    public ResponseEntity<?> getAllProduct(HttpServletRequest request) {
        return ResponseEntity.ok(productService.getAllProduct(request));
    }

    @PostMapping(path = "/update_product")
    public ResponseEntity<?> updateProduct(HttpServletRequest request) {
        return ResponseEntity.ok(productService.updateProduct(request));
    }

    /* Get Product by outletid (one use) */
    @GetMapping(path = "/get_product")
    public Object getProduct(HttpServletRequest request) {
        JsonObject result = new JsonObject();
        result = productService.getProduct(request);
        return result.toString();
    }

    /* Get All Products by outletwise */
    @GetMapping(path = "/get_all_product")
    public Object getProductsOfOutlet(HttpServletRequest request) {
        JsonObject result = new JsonObject();
        result = productService.getProductsOfOutlet(request);
        return result.toString();
    }

    /* Get all packings and its all units of products */
    @PostMapping(path = "/get_all_product_units_packings")
    public Object getUnitsPackings(HttpServletRequest request) {
        JsonObject result = new JsonObject();
        result = productService.getUnitsPackings(request);
        return result.toString();
    }
}