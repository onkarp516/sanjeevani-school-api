package in.truethics.ethics.ethicsapiv10.controller.masters;

import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.CasteCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CasteCategoryController {
    @Autowired
    private CasteCategoryService service;

    @PostMapping(path = "/createCasteCategory")
    public Object createCasteCategory(HttpServletRequest request) {
        return service.createCasteCategory(request);
    }

    /* get all category of casts */
    @PostMapping(path = "/getCategoryBySubCaste")
    public Object getCategoryBySubCaste(HttpServletRequest request) {
        JsonObject result = service.getCategoryBySubCaste(request);
        return result.toString();
    }

    /* get all category */
    @GetMapping(path = "/getCasteCategories")
    public Object getCasteCategories(HttpServletRequest request) {
        JsonObject result = service.getCasteCategories(request);
        return result.toString();
    }

    /* update category by id*/
    @PostMapping(path = "/updateCasteCategory")
    public Object updateCategory(HttpServletRequest request) {
        return service.updateCategory(request);
    }

    /* Get all category of  branchs */
    @GetMapping(path = "/getAllCasteCategory")
    public Object getAllCategory(HttpServletRequest request) {
        JsonObject result = service.getAllCategory(request);
        return result.toString();
    }

    /* get category by Id */
    @PostMapping(path = "/getCasteCategoryById")
    public Object getCategoryById(HttpServletRequest request) {
        JsonObject result = service.getCategoryById(request);
        return result.toString();
    }
}

