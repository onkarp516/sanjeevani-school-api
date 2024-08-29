package in.truethics.ethics.ethicsapiv10.controller.users;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.common.CommonAccessPermissions;
import in.truethics.ethics.ethicsapiv10.common.PasswordEncoders;
import in.truethics.ethics.ethicsapiv10.model.access_permissions.SystemAccessPermissions;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.access_permission_repository.SystemAccessPermissionsRepository;
import in.truethics.ethics.ethicsapiv10.repository.access_permission_repository.SystemActionMappingRepository;
import in.truethics.ethics.ethicsapiv10.repository.user_repository.UsersRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.service.user_service.UserService;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    public static long ACCESS_VALIDITY = 24 * 60 * 60;
    public static long TOKEN_VALIDITY = 20 * 60 * 60;
    private final String SECRET_KEY = "SECRET_KEY";
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenUtil jwtUtil;
    @Autowired
    UserService userService;
    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private SystemActionMappingRepository systemActionMappingRepository;
    @Autowired
    private SystemAccessPermissionsRepository systemAccessPermissionsRepository;
    @Autowired
    private CommonAccessPermissions accessPermissions;
    @Autowired
    private PasswordEncoders bcryptEncoder;

    /* Registration of  SuperAdmin */
    @PostMapping(path = "/register_superAdmin")
    public ResponseEntity<?> createSuperAdmin(HttpServletRequest request) {
        return ResponseEntity.ok(userService.createSuperAdmin(request));
    }

    /* Registration of Users including Admin */
    @PostMapping(path = "/register_user")
    public Object createUser(HttpServletRequest request) {
        JsonObject jsonObject = userService.addUser(request);
        return jsonObject.toString();
    }

    //
//    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
//    public ResponseEntity<?> createAuthenticateToken(@RequestBody Map<String, String> request,
//                                                     HttpServletRequest req) {
//        ResponseMessage responseMessage = new ResponseMessage();
//        String username = request.get("usercode");
//        String password = request.get("password");
//        try {
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(username, password);
//            authenticationManager.authenticate(authenticationToken);
//            Users users = userService.findUserWithPassword(username, password);
//            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
//            JWTCreator.Builder jwtBuilder = JWT.create();
//            String access_token = "";
//            jwtBuilder.withSubject(users.getUsercode());
//            jwtBuilder.withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_VALIDITY * 1000));
//            jwtBuilder.withIssuer(req.getRequestURI());
//            jwtBuilder.withClaim("userId", users.getId());
//            jwtBuilder.withClaim("isSuperAdmin", users.getIsSuperAdmin());
//            jwtBuilder.withClaim("userRole", users.getUserRole());
//            jwtBuilder.withClaim("userCode", users.getUsercode());
//            if (users.getUserRole() != null && users.getUserRole().equalsIgnoreCase("BADMIN")) {
//                jwtBuilder.withClaim("branchId", users.getBranch().getId());
//                jwtBuilder.withClaim("branchName", users.getBranch().getBranchName());
//                jwtBuilder.withClaim("companyId", users.getOutlet().getId());
//                jwtBuilder.withClaim("CompanyName", users.getOutlet().getCompanyName());
//                jwtBuilder.withClaim("state", users.getOutlet().getState().getStateCode());
//            }
//            if (users.getUserRole() != null && users.getUserRole().equalsIgnoreCase("CADMIN")) {
//                jwtBuilder.withClaim("companyId", users.getOutlet().getId());
//                jwtBuilder.withClaim("CompanyName", users.getOutlet().getCompanyName());
//                jwtBuilder.withClaim("state", users.getOutlet().getState().getStateCode());
//            }
//            if (users.getUserRole() != null && users.getUserRole().equalsIgnoreCase("USER")) {
//                jwtBuilder.withClaim("branchId", users.getBranch() != null ? users.getBranch().getId().toString() : "");
//                jwtBuilder.withClaim("branchName", users.getBranch() != null ? users.getBranch().getBranchName() : "");
//                jwtBuilder.withClaim("companyId", users.getOutlet().getId());
//                jwtBuilder.withClaim("CompanyName", users.getOutlet().getCompanyName());
//                jwtBuilder.withClaim("state", users.getOutlet().getState().getStateCode());
//            }
//
//            jwtBuilder.withClaim("status", "OK");
//            /* getting User Permissions */
//            JsonObject finalResult = new JsonObject();
//            JsonArray userPermissions = new JsonArray();
//            JsonArray permissions = new JsonArray();
//            JsonArray masterModules = new JsonArray();
//            List<SystemAccessPermissions> list = systemAccessPermissionsRepository.findByUsersIdAndStatus(users.getId(), true);
//            /*
//             * Print elements using the forEach
//             */
//            for (SystemAccessPermissions mapping : list) {
//                JsonObject mObject = new JsonObject();
//                mObject.addProperty("id", mapping.getId());
//                mObject.addProperty("action_mapping_id", mapping.getSystemActionMapping().getId());
//                mObject.addProperty("action_mapping_name", mapping.getSystemActionMapping().getName());
//                mObject.addProperty("action_mapping_slug", mapping.getSystemActionMapping().getSlug());
//                String[] actions = mapping.getUserActionsId().split(",");
//                permissions = accessPermissions.getActions(actions);
//                masterModules = accessPermissions.getParentMasters(mapping.getSystemActionMapping().getSystemMasterModules().getParentModuleId());
//                mObject.add("actions", permissions);
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("id", mapping.getSystemActionMapping().getSystemMasterModules().getId());
//                jsonObject.addProperty("name", mapping.getSystemActionMapping().getSystemMasterModules().getName());
//                jsonObject.addProperty("slug", mapping.getSystemActionMapping().getSystemMasterModules().getSlug());
//                masterModules.add(jsonObject);
//                mObject.add("parent_modules", masterModules);
//                userPermissions.add(mObject);
//            }
//            finalResult.add("userActions", userPermissions);
//
//            /* end of User Permissions */
//            //     jwtBuilder.withClaim("permission", "" + finalResult);
//            access_token = jwtBuilder.sign(algorithm);
//            JWTCreator.Builder builder = JWT.create();
//            String refresh_token = "";
//            builder.withSubject(users.getUsercode());
//            builder.withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000));
//            builder.withIssuer(req.getRequestURI().toString());
//            refresh_token = builder.sign(algorithm);
//            Map<String, String> tokens = new HashMap<>();
//            tokens.put("access_token", access_token);
//            tokens.put("refresh_token", refresh_token);
//            responseMessage.setMessage("Login Successfully");
//            responseMessage.setResponseObject(tokens);
//            responseMessage.setResponseStatus(HttpStatus.OK.value());
//            responseMessage.setData(finalResult.toString());
//        } catch (BadCredentialsException be) {
//            be.printStackTrace();
//            System.out.println("Exception " + be.getMessage());
//            responseMessage.setMessage("Incorrect username or password");
//            responseMessage.setResponseStatus(HttpStatus.UNAUTHORIZED.value());
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Exception " + e.getMessage());
//            responseMessage.setMessage("Incorrect username or password");
//            responseMessage.setResponseStatus(HttpStatus.UNAUTHORIZED.value());
//        }
//        //   responseMessage.setData(jwtToken);
//        return ResponseEntity.ok(responseMessage);
//    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticateToken(@RequestBody Map<String, String> request,
                                                     HttpServletRequest req) {
        ResponseMessage responseMessage = new ResponseMessage();
        String username = request.get("usercode");
        String password = request.get("password");
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authenticationToken);
            Users users = userService.findUserWithPassword(username, password);
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
            JWTCreator.Builder jwtBuilder = JWT.create();
            String access_token = "";
            jwtBuilder.withSubject(users.getUsercode());
            jwtBuilder.withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_VALIDITY * 1000));
            jwtBuilder.withIssuer(req.getRequestURI().toString());
            jwtBuilder.withClaim("userId", users.getId());
            jwtBuilder.withClaim("isSuperAdmin", users.getIsSuperAdmin());
            jwtBuilder.withClaim("userRole", users.getUserRole());
            jwtBuilder.withClaim("userCode", users.getUsercode());
            jwtBuilder.withClaim("fullName", users.getFullName());
            if (users.getOutlet() != null)
                jwtBuilder.withClaim("gstType", users.getOutlet().getGstTypeMaster().getGstType());
            if (users.getUserRole() != null && users.getUserRole().equalsIgnoreCase("BADMIN")) {
                jwtBuilder.withClaim("branchId", users.getBranch().getId());
                jwtBuilder.withClaim("branchName", users.getBranch().getBranchName());
                jwtBuilder.withClaim("companyId", users.getOutlet().getId());
                jwtBuilder.withClaim("CompanyName", users.getOutlet().getCompanyName());
                jwtBuilder.withClaim("state", users.getOutlet().getState().getStateCode());
            } else if (users.getUserRole() != null && users.getUserRole().equalsIgnoreCase("CADMIN")) {
                jwtBuilder.withClaim("companyId", users.getOutlet().getId());
                jwtBuilder.withClaim("CompanyName", users.getOutlet().getCompanyName());
                jwtBuilder.withClaim("state", users.getOutlet().getState().getStateCode());
            } else if (users.getUserRole() != null && users.getUserRole().equalsIgnoreCase("USER")) {
                jwtBuilder.withClaim("branchId", users.getBranch() != null ? users.getBranch().getId().toString() : "");
                jwtBuilder.withClaim("branchName", users.getBranch() != null ? users.getBranch().getBranchName() : "");
                jwtBuilder.withClaim("companyId", users.getOutlet().getId());
                jwtBuilder.withClaim("CompanyName", users.getOutlet().getCompanyName());
                jwtBuilder.withClaim("state", users.getOutlet().getState().getStateCode());
            }

            jwtBuilder.withClaim("status", "OK");
            /* getting User Permissions */
            JsonObject finalResult = new JsonObject();
            JsonArray userPermissions = new JsonArray();
            JsonArray permissions = new JsonArray();
            JsonArray masterModules = new JsonArray();
            List<SystemAccessPermissions> list = systemAccessPermissionsRepository.findByUsersIdAndStatus(users.getId(), true);
            /*
             * Print elements using the forEach
             */
            for (SystemAccessPermissions mapping : list) {
                JsonObject mObject = new JsonObject();
                mObject.addProperty("id", mapping.getId());
                mObject.addProperty("action_mapping_id", mapping.getSystemActionMapping().getId());
                mObject.addProperty("action_mapping_name", mapping.getSystemActionMapping().getSystemMasterModules().getName());
                mObject.addProperty("action_mapping_slug", mapping.getSystemActionMapping().getSystemMasterModules().getSlug());
                String[] actions = mapping.getUserActionsId().split(",");
                permissions = accessPermissions.getActions(actions);
                masterModules = accessPermissions.getParentMasters(mapping.getSystemActionMapping().getSystemMasterModules().getParentModuleId());
                mObject.add("actions", permissions);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", mapping.getSystemActionMapping().getSystemMasterModules().getId());
                jsonObject.addProperty("name", mapping.getSystemActionMapping().getSystemMasterModules().getName());
                jsonObject.addProperty("slug", mapping.getSystemActionMapping().getSystemMasterModules().getSlug());
                masterModules.add(jsonObject);
                mObject.add("parent_modules", masterModules);
                userPermissions.add(mObject);
            }
            finalResult.add("userActions", userPermissions);

            /* end of User Permissions */
            //     jwtBuilder.withClaim("permission", "" + finalResult);
            access_token = jwtBuilder.sign(algorithm);
            JWTCreator.Builder builder = JWT.create();
            String refresh_token = "";
            builder.withSubject(users.getUsercode());
            builder.withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000));
            builder.withIssuer(req.getRequestURI().toString());
            refresh_token = builder.sign(algorithm);
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", access_token);
            tokens.put("refresh_token", refresh_token);
            tokens.put("current_fiscal_year","2022-2023");
            responseMessage.setMessage("Login Successfully");
            responseMessage.setResponseObject(tokens);
            responseMessage.setResponseStatus(HttpStatus.OK.value());
            responseMessage.setData(finalResult.toString());
            // responseMessage.setData(bcryptEncoder.encrypt(users.getPermissions()));
        } catch (BadCredentialsException be) {
            be.printStackTrace();
            System.out.println("Exception " + be.getMessage());
            responseMessage.setMessage("Incorrect username or password");
            responseMessage.setResponseStatus(HttpStatus.UNAUTHORIZED.value());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());
            responseMessage.setMessage("Incorrect username or password");
            responseMessage.setResponseStatus(HttpStatus.UNAUTHORIZED.value());
        }
        //   responseMessage.setData(jwtToken);
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping(path = "/getUsers")
    public Object getUsers() {
        JsonObject res = userService.getUsers();
        return res.toString();
    }

    /* get user by id */
    @PostMapping(path = "/get_user_by_id")
    public Object getUsersById(HttpServletRequest requestParam) {
        JsonObject response = userService.getUsersById(requestParam.getParameter("id"));
        return response.toString();
    }

    // update Users
    @PostMapping(path = "/updateUser")
    public ResponseEntity<?> updateUser(HttpServletRequest request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    @PostMapping(path = "/get_c_admin_users_old")
    public Object getUsersOfCompanyOld(HttpServletRequest httpServletRequest, @RequestBody Map<String, String> request) {
        JsonObject res = userService.getUsersOfCompany(httpServletRequest, request.get("userRole"), request.get("currentUserRole"));
        return res.toString();
    }

    @GetMapping(path = "/get_c_admin_users")
    public Object getUsersOfCompany(HttpServletRequest httpServletRequest) {
        JsonObject res = userService.getUsersOfCompanyNew(httpServletRequest);
        return res.toString();
    }

    @GetMapping(path = "/get_b_admin_users")
    public Object getUsersOfBranch(HttpServletRequest httpServletRequest) {
        JsonObject res = userService.getUsersOfBrnahcNew(httpServletRequest);
        return res.toString();
    }

    /* call to this api if expired Token */
    @GetMapping(path = "/get_mesg_for_token_expired")
    public Object getMesgForTokenExpired(HttpServletRequest request) {
        JsonObject result = userService.getMesgForTokenExpired(request);
        return result.toString();
    }

    /* call to this api if expired Token */
    @PostMapping(path = "/set_user_perissions")
    public Object setUserPermissions(HttpServletRequest request) {
        JsonObject result = userService.setUserPermissions(request);
        return result.toString();
    }

    /*** get access permissions of User *****/
    @PostMapping(path = "/get_user_permissions")
    public Object getUserPermissions(HttpServletRequest request) {
        JsonObject jsonObject = userService.getUserPermissions(request);
        return jsonObject.toString();
    }

    @GetMapping(path = "/getOAdminUsers")
    public Object getOAdminUsers(HttpServletRequest httpServletRequest) {
        JsonObject res = userService.getOAdminUsers(httpServletRequest);
        return res.toString();
    }
}
