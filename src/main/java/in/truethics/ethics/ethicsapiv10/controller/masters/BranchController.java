package in.truethics.ethics.ethicsapiv10.controller.masters;


import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
public class BranchController {
    @Autowired
    private BranchService branchService;

    @PostMapping(path = "/create_branch")
    public ResponseEntity<?> createBranch(HttpServletRequest request) {
        return ResponseEntity.ok(branchService.createBranch(request));
    }

    @PostMapping(path = "/update_branch")
    public Object updateBranch(HttpServletRequest request) {
        return ResponseEntity.ok(branchService.updateBranch(request));
    }

    /* get Branches of Super admin */
    @GetMapping(path = "/getAllBranches")
    public Object getAllBranches(HttpServletRequest request) {
        JsonObject res = branchService.getAllBranches(request);
        return res.toString();
    }

    /* get Branches From Outlet */
    @PostMapping(path = "/getBranchesByOutlet")
    public Object getBranchesByOutlet(HttpServletRequest request) {
        JsonObject res = branchService.getBranchesByOutlet(request);
        return res.toString();
    }

    /* get branch by id */
    @PostMapping(path = "/get_branch_by_id")
    public Object getBranchById(HttpServletRequest request) {
        JsonObject jsonObject = branchService.getBranchById(request);
        return jsonObject.toString();
    }
}
