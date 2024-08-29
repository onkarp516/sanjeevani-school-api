package in.truethics.ethics.ethicsapiv10.service.user_service;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.truethics.ethics.ethicsapiv10.common.CommonAccessPermissions;
import in.truethics.ethics.ethicsapiv10.common.PasswordEncoders;
import in.truethics.ethics.ethicsapiv10.model.access_permissions.SystemAccessPermissions;
import in.truethics.ethics.ethicsapiv10.model.access_permissions.SystemActionMapping;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import in.truethics.ethics.ethicsapiv10.model.user.UserRole;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.access_permission_repository.SystemAccessPermissionsRepository;
import in.truethics.ethics.ethicsapiv10.repository.access_permission_repository.SystemActionMappingRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.BranchRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.OutletRepository;
import in.truethics.ethics.ethicsapiv10.repository.user_repository.UserRoleRepository;
import in.truethics.ethics.ethicsapiv10.repository.user_repository.UsersRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    private static final Logger UserLogger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private OutletRepository outletRepository;
    @Autowired
    private PasswordEncoders bcryptEncoder;
    @Autowired
    private CommonAccessPermissions accessPermissions;
    @Autowired
    private SystemActionMappingRepository mappingRepository;
    @Autowired
    private SystemAccessPermissionsRepository accessPermissionsRepository;

    public ResponseMessage createSuperAdmin(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();
        Users users = new Users();
        Map<String, String[]> paramMap = request.getParameterMap();
        try {
            if (paramMap.containsKey("mobileNumber"))
                users.setMobileNumber(Long.valueOf(request.getParameter("mobileNumber")));
            else
                users.setMobileNumber(Long.valueOf(0));
            users.setFullName(request.getParameter("fullName"));
            if (paramMap.containsKey("email"))
                users.setEmail(request.getParameter("email"));
            else
                users.setEmail("NA");
            users.setGender(request.getParameter("gender"));
            users.setUsercode(request.getParameter("usercode"));
            users.setUsername(request.getParameter("usercode"));
            users.setUserRole(request.getParameter("userRole"));
            if (paramMap.containsKey("address"))
                users.setAddress(request.getParameter("address"));
            else
                users.setAddress("NA");
            users.setStatus(true);
            users.setPassword(bcryptEncoder.passwordEncoderNew().encode(request.getParameter("password")));
            users.setPlain_password(request.getParameter("password"));
            users.setIsSuperAdmin(true);
            users.setPermissions("all");

            userRepository.save(users);
            responseObject.setMessage("Super admin created successfully");
            responseObject.setResponseStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            UserLogger.error("Exception in addUser:" + e.getMessage());
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseObject.setMessage("Internal Server Error");
            e.printStackTrace();
            System.out.println("Exception:" + e.getMessage());
        }
        return responseObject;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = userRepository.findByUsername(username);
        if (users == null) {
            log.error("User not found In the database");
            throw new UsernameNotFoundException("UserController not found with username: " + username);

        } else {
            log.info("User found In the database: {}", username);
            return new org.springframework.security.core.userdetails.User(users.getUsercode(),
                    users.getPassword(), new ArrayList<>());
        }
    }

    public Users findUser(String usercode) throws UsernameNotFoundException {
        Users users = userRepository.findByUsername(usercode);
        if (users != null) {

        } else {
            throw new UsernameNotFoundException("User not found with username: " + usercode);
        }
        return users;

    }

    public Users findUserWithPassword(String usercode, String password) throws UsernameNotFoundException {
        Users users = userRepository.findByUsername(usercode);
        if (bcryptEncoder.passwordEncoderNew().matches(password, users.getPassword())) {
            return users;
        }
        return null;
    }

    public JsonObject addUser(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        //  ResponseMessage responseObject = new ResponseMessage();
        JsonObject responseObject = new JsonObject();
        Users users = new Users();
        Users user = null;
        try {
            if (paramMap.containsKey("mobileNumber")) {
                users.setMobileNumber(Long.valueOf(request.getParameter("mobileNumber")));
            } else {
                users.setMobileNumber(Long.valueOf(0));
            }
            if (paramMap.containsKey("fullName")) {
                users.setFullName(request.getParameter("fullName"));
            }
            if (paramMap.containsKey("email")) {
                users.setEmail(request.getParameter("email"));
            } else {
                users.setEmail("NA");
            }

            if (paramMap.containsKey("address")) {
                users.setAddress(request.getParameter("address"));
            } else {
                users.setAddress("NA");
            }
            if (request.getParameter("gender") != null) {
                users.setGender(request.getParameter("gender"));
            }
            UserRole userRole = userRoleRepository.findByIdAndStatus(Long.parseLong(request.getParameter("roleId")), true);
            if (userRole != null) {
                users.setRoleMaster(userRole);
            }
            users.setUsercode(request.getParameter("usercode"));
            users.setUsername(request.getParameter("usercode"));
            users.setUserRole(request.getParameter("userRole"));
            users.setStatus(true);
            users.setIsSuperAdmin(false);
            //  users.setPermissions(request.getParameter("permissions"));
            if (request.getHeader("Authorization") != null) {
                user = jwtRequestFilter.getUserDataFromToken(
                        request.getHeader("Authorization").substring(7));
                users.setCreatedBy(user.getId());
            }
            users.setPassword(bcryptEncoder.passwordEncoderNew().encode(
                    request.getParameter("password")));
            users.setPlain_password(request.getParameter("password"));
            if (paramMap.containsKey("companyId")) {
                Outlet mOutlet = outletRepository.findByIdAndStatus(Long.parseLong(request.getParameter("companyId")), true);
                users.setOutlet(mOutlet);
            } else {
                Users users1 = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
                users.setOutlet(users1.getOutlet());
            }
            if (paramMap.containsKey("branchId")) {
                Branch mBranch = branchRepository.findByIdAndStatus(Long.parseLong(request.getParameter("branchId")), true);
                users.setBranch(mBranch);
            }
            if (paramMap.containsKey("permissions"))
                users.setPermissions(request.getParameter("permissions"));
            Users newUser = userRepository.save(users);
            try {
//                if (request.getParameter("userRole").equalsIgnoreCase("BUSER")) {
                    /* Create Permissions */
                    String jsonStr = request.getParameter("user_permissions");
                    if (jsonStr != null) {
                        JsonArray userPermissions = new JsonParser().parse(jsonStr).getAsJsonArray();
                        for (int i = 0; i < userPermissions.size(); i++) {
                            JsonObject mObject = userPermissions.get(i).getAsJsonObject();
                            SystemAccessPermissions mPermissions = new SystemAccessPermissions();
                            mPermissions.setUsers(newUser);
                            SystemActionMapping mappings = mappingRepository.findByIdAndStatus(mObject.get("mapping_id").getAsLong(),
                                    true);
                            mPermissions.setSystemActionMapping(mappings);
                            mPermissions.setStatus(true);
                            mPermissions.setCreatedBy(user.getId());
                            JsonArray mActionsArray = mObject.get("actions").getAsJsonArray();
                            String actionsId = "";
                            for (int j = 0; j < mActionsArray.size(); j++) {
                                actionsId = actionsId + mActionsArray.get(j).getAsString();
                                if (j < mActionsArray.size() - 1) {
                                    actionsId = actionsId + ",";
                                }
                            }
                            mPermissions.setUserActionsId(actionsId);
                            accessPermissionsRepository.save(mPermissions);
                        }
                    }
//                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            responseObject.addProperty("message", "User added succussfully");
            responseObject.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (DataIntegrityViolationException e1) {
            UserLogger.error("Exception in addUser: " + e1.getMessage());
            System.out.println("DataIntegrityViolationException " + e1.getMessage());
            responseObject.addProperty("responseStatus", HttpStatus.CONFLICT.value());
            responseObject.addProperty("message", "Usercode already used");
            return responseObject;
        } catch (Exception e) {
            UserLogger.error("Exception in addUser: " + e.getMessage());
            responseObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseObject.addProperty("message", "Internal Server Error");
            e.printStackTrace();
            System.out.println("Exception:" + e.getMessage());
        }
        return responseObject;
    }

    //List of Users Service
    public JsonObject getUsers() {
        JsonObject res = new JsonObject();
        List<Users> list = userRepository.findAll();
        res = getUserData(list);
        return res;
    }

    //Get user By Id
    public JsonObject getUsersById(String id) {
        Users user = userRepository.findByIdAndStatus(Long.parseLong(id), true);
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        JsonArray user_permission = new JsonArray();
        if (user != null) {
            response.addProperty("id", user.getId());
            if (user.getOutlet() != null) {
                response.addProperty("companyName", user.getOutlet().getCompanyName());
                response.addProperty("companyId", user.getOutlet().getId());
            }
            if (user.getBranch() != null) {
                response.addProperty("branchName", user.getBranch().getBranchName());
                response.addProperty("branchId", user.getBranch().getId());
            }

            response.addProperty("email", user.getEmail());
            response.addProperty("gender", user.getGender());
            response.addProperty("usercode", user.getUsercode());
            response.addProperty("fullName", user.getFullName());
            response.addProperty("roleId", user.getRoleMaster().getId());
            response.addProperty("userRole", user.getUserRole());
            response.addProperty("password", user.getPlain_password());
            response.addProperty("mobileNumber", user.getMobileNumber() != 0 ?
                    user.getMobileNumber().toString() : "NA");

            /***** get User Permissions from access_permissions_tbl ****/
            List<SystemAccessPermissions> accessPermissions = new ArrayList<>();
            accessPermissions = accessPermissionsRepository.findByUsersIdAndStatus(user.getId(), true);
            for (SystemAccessPermissions mPermissions : accessPermissions) {
                JsonObject mObject = new JsonObject();
                mObject.addProperty("mapping_id", mPermissions.getSystemActionMapping().getId());
                JsonArray actions = new JsonArray();
                String actionsId = mPermissions.getUserActionsId();
                String[] actionsList = actionsId.split(",");
                Arrays.sort(actionsList);
                for (String actionId : actionsList) {
                    actions.add(actionId);
                }
                mObject.add("actions", actions);
                user_permission.add(mObject);
            }
            response.add("permissions", user_permission);

            result.addProperty("message", "success");
            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.add("responseObject", response);
        } else {
            result.addProperty("message", "error");
            result.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
        }
        return result;
    }

    public JsonObject getUserPermissions(HttpServletRequest request) {
        /* getting User Permissions */
        JsonObject finalResult = new JsonObject();
        JsonArray userPermissions = new JsonArray();
        JsonArray permissions = new JsonArray();
        JsonArray masterModules = new JsonArray();
        Long userId = Long.parseLong(request.getParameter("user_id"));
        List<SystemAccessPermissions> list = accessPermissionsRepository.findByUsersIdAndStatus(userId, true);
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
        return finalResult;
    }

    public Object updateUser(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        ResponseMessage responseObject = new ResponseMessage();
        Users users = userRepository.findByIdAndStatus(Long.parseLong(request.getParameter("id")),
                true);
        Users loginUser = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        if (users != null) {
            if (paramMap.containsKey("mobileNumber")) {
                users.setMobileNumber(Long.valueOf(request.getParameter("mobileNumber")));
            } else {
                users.setMobileNumber(Long.valueOf(0));
            }
            if (paramMap.containsKey("fullName")) {
                users.setFullName(request.getParameter("fullName"));
            }
            if (paramMap.containsKey("emailId")) {
                users.setEmail(request.getParameter("emailId"));
            }
            if (paramMap.containsKey("address")) {
                users.setAddress(request.getParameter("address"));
            } else {
                users.setAddress("NA");
            }
            if (request.getParameter("gender") != null) {
                users.setGender(request.getParameter("gender"));
            }
            users.setUsercode(request.getParameter("usercode"));
            users.setUsername(request.getParameter("usercode"));
            UserRole userRole = userRoleRepository.findByIdAndStatus(Long.parseLong(request.getParameter("roleId")), true);
            if (userRole != null) {
                users.setRoleMaster(userRole);
            }
            //users.setUserRole(request.getParameter("userRole"));
            users.setPermissions(request.getParameter("permissions"));
          /*  if (request.getHeader("Authorization") != null) {
                Users user = jwtRequestFilter.getUserDataFromToken(
                        request.getHeader("Authorization").substring(7));
                users.setCreatedBy(user.getId());
            }*/
//            users.setPassword(bcryptEncoder.passwordEncoderNew().encode(request.getParameter("password")));
//            users.setPlain_password(request.getParameter("password"));
            if (paramMap.containsKey("companyId")) {
                Outlet mOutlet = outletRepository.findByIdAndStatus(Long.parseLong(request.getParameter("companyId")), true);
                users.setOutlet(mOutlet);
            }
            if (paramMap.containsKey("branchId")) {
                Branch mBranch = branchRepository.findByIdAndStatus(Long.parseLong(request.getParameter("branchId")), true);
                users.setBranch(mBranch);
            }
            String del_user_perm = request.getParameter("del_user_permissions");
            JsonArray deleteUserPermission = new JsonParser().parse(del_user_perm).getAsJsonArray();
            for (int j = 0; j < deleteUserPermission.size(); j++) {
                Long moduleId = deleteUserPermission.get(j).getAsLong();
                //  SystemActionMapping delMapping = mappingRepository.findByIdAndStatus(moduleId, true);
                SystemAccessPermissions delPermissions = accessPermissionsRepository.findByUsersIdAndStatusAndSystemActionMappingId(
                        users.getId(), true, moduleId);
                delPermissions.setStatus(false);
                try {
                    accessPermissionsRepository.save(delPermissions);
                } catch (Exception e) {
                }
            }
            /* Update Permissions */
            String jsonStr = request.getParameter("user_permissions");
            JsonArray userPermissions = new JsonParser().parse(jsonStr).getAsJsonArray();
            for (int i = 0; i < userPermissions.size(); i++) {
                JsonObject mObject = userPermissions.get(i).getAsJsonObject();
                SystemActionMapping mappings = mappingRepository.findByIdAndStatus(mObject.get("mapping_id").getAsLong(),
                        true);
                SystemAccessPermissions mPermissions = accessPermissionsRepository.findByUsersIdAndStatusAndSystemActionMappingId(
                        users.getId(), true, mappings.getId());
                System.out.println("User Id:" + users.getId());
                if (mPermissions != null) {
                    JsonArray mActionsArray = mObject.get("actions").getAsJsonArray();
                    String actionsId = "";
                    for (int j = 0; j < mActionsArray.size(); j++) {
                        actionsId = actionsId + mActionsArray.get(j).getAsString();
                        if (j < mActionsArray.size() - 1) {
                            actionsId = actionsId + ",";
                        }
                    }
                    mPermissions.setUserActionsId(actionsId);
                    mPermissions.setUsers(users);
                    accessPermissionsRepository.save(mPermissions);
                } else {
                    /* Create Permissions */
                    SystemAccessPermissions mPermissions1 = new SystemAccessPermissions();
                    mPermissions1.setSystemActionMapping(mappings);
                    mPermissions1.setStatus(true);
                    // mPermissions1.setCreatedBy(users.getId());
                    mPermissions1.setCreatedBy(loginUser.getId());
                    JsonArray mActionsArray = mObject.get("actions").getAsJsonArray();
                    String actionsId = "";
                    for (int j = 0; j < mActionsArray.size(); j++) {
                        actionsId = actionsId + mActionsArray.get(j).getAsString();
                        if (j < mActionsArray.size() - 1) {
                            actionsId = actionsId + ",";
                        }
                    }
                    mPermissions1.setUserActionsId(actionsId);
                    mPermissions1.setUsers(users);

                    try {
                        accessPermissionsRepository.save(mPermissions1);
                    } catch (Exception e) {
                        System.out.println("Exception:" + e.getMessage());
                    }
                }
            }
            userRepository.save(users);
            responseObject.setMessage("User updated sucessfully");
            responseObject.setResponseStatus(HttpStatus.OK.value());
            /*else {
                responseObject.setResponseStatus(HttpStatus.FORBIDDEN.value());
                responseObject.setMessage("Not found");
            }*/
        } else {
            responseObject.setResponseStatus(HttpStatus.FORBIDDEN.value());
            responseObject.setMessage("Not found");
        }
        return responseObject;
    }

    /* Get all Branch Users of  Institute Admin */
    /*public JsonObject getBranchUsers(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Long branchId = users.getBranch().getId();
        List<Users> list = new ArrayList<>();
        JsonObject res = new JsonObject();
        list = userRepository.findByBranchIdAndStatus(branchId, true);
        res = getUserData(list);
        return res;
    }*/

    /* Get all outlet Users of Branch Admin */
    /*public JsonObject getOutletUsers(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Long outletId = users.getOutlet().getId();
        JsonObject res = new JsonObject();
        JsonArray result = new JsonArray();
        List<Users> list = userRepository.findByOutletIdAndStatus(outletId, true);
        res = getUserData(list);
        return res;
    }*/

    /* Get All users Rolewise of Super Admin */
    public JsonObject getUsersOfCompany(HttpServletRequest httpServletRequest, String userRole, String currentUserRole) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                httpServletRequest.getHeader("Authorization").substring(7));
        ResponseMessage responseMessage = new ResponseMessage();
        List<Users> list = new ArrayList<>();
        if (currentUserRole.equalsIgnoreCase("SADMIN"))
            list = userRepository.findByUserRoleAndStatus(userRole, true);
        else {
            list = userRepository.findByUserRoleAndCreatedByAndStatus(userRole, users.getId(), true);
        }
        JsonObject res = getUserData(list);
        return res;
    }


    public JsonObject getUserData(List<Users> list) {
        JsonObject res = new JsonObject();
        JsonArray result = new JsonArray();
        if (list.size() > 0) {
            for (Users mUser : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mUser.getId());
                if (mUser.getOutlet() != null)
                    response.addProperty("companyName", mUser.getOutlet().getCompanyName());
                response.addProperty("username", mUser.getUsername());
                response.addProperty("fullName", mUser.getFullName());
                response.addProperty("mobileNumber", mUser.getMobileNumber());
                response.addProperty("email", mUser.getEmail());
                response.addProperty("address", mUser.getAddress());
                response.addProperty("gender", mUser.getGender());
                response.addProperty("usercode", mUser.getUsercode());
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

    public JsonObject getMesgForTokenExpired(HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "Hello Token");
        jsonObject.addProperty("responseStatus", HttpStatus.OK.value());
        return jsonObject;
    }

    public JsonObject getUsersOfCompanyNew(HttpServletRequest httpServletRequest) {
        JsonObject res = new JsonObject();
        JsonArray result = new JsonArray();
        List<Users> list = new ArrayList<>();
        list = userRepository.findByUserRoleIgnoreCaseAndStatus("CADMIN", true);
        if (list.size() > 0) {
            for (Users mUser : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mUser.getId());
                if (mUser.getOutlet() != null)
                    response.addProperty("companyName", mUser.getOutlet().getCompanyName());
                response.addProperty("username", mUser.getUsername());
                response.addProperty("fullName", mUser.getFullName());
                response.addProperty("mobileNumber", mUser.getMobileNumber());
                response.addProperty("email", mUser.getEmail());
                response.addProperty("address", mUser.getAddress());
                response.addProperty("gender", mUser.getGender());
                response.addProperty("usercode", mUser.getUsercode());
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

    public JsonObject getUsersOfBrnahcNew(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        JsonArray result = new JsonArray();

        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));

        List<Users> list = new ArrayList<>();
        if (users.getBranch() == null)
            list = userRepository.findByOutletIdAndUserRoleIgnoreCaseAndStatus(users.getOutlet().getId(), "BADMIN", true);
        if (users.getBranch() != null)
            list = userRepository.findByBranchIdAndUserRoleIgnoreCaseAndStatus(users.getBranch().getId(), "BADMIN", true);
        if (list.size() > 0) {
            for (Users mUser : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mUser.getId());
                if (mUser.getOutlet() != null)
                    response.addProperty("branchName", mUser.getBranch().getBranchName());
                response.addProperty("username", mUser.getUsername());
                response.addProperty("fullName", mUser.getFullName());
                response.addProperty("mobileNumber", mUser.getMobileNumber());
                response.addProperty("email", mUser.getEmail());
                response.addProperty("address", mUser.getAddress());
                response.addProperty("gender", mUser.getGender());
                response.addProperty("usercode", mUser.getUsercode());

                result.add(response);
            }
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        } else {
            res.addProperty("message", "empty list");
            res.addProperty("responseStatus", HttpStatus.OK.value());
            res.add("responseObject", result);
        }
        return res;
    }


    public JsonObject setUserPermissions(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getParameter("user_id"));
        Users users = userRepository.findByIdAndStatus(userId, true);
        users.setPermissions(request.getParameter("permissions"));
        userRepository.save(users);
        JsonObject res = new JsonObject();
        res.addProperty("message", "success");
        res.addProperty("responseStatus", HttpStatus.OK.value());
        return res;
    }

    public JsonObject getOAdminUsers(HttpServletRequest httpServletRequest) {
        JsonObject res = new JsonObject();
        JsonArray result = new JsonArray();
        List<Users> list = new ArrayList<>();
        list = userRepository.findByUserRoleIgnoreCaseAndStatus("OADMIN", true);
        if (list.size() > 0) {
            for (Users mUser : list) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mUser.getId());
                if (mUser.getOutlet() != null)
                    response.addProperty("companyName", mUser.getOutlet().getCompanyName());
                response.addProperty("username", mUser.getUsername());
                response.addProperty("fullName", mUser.getFullName());
                response.addProperty("mobileNumber", mUser.getMobileNumber());
                response.addProperty("email", mUser.getEmail());
                response.addProperty("address", mUser.getAddress());
                response.addProperty("gender", mUser.getGender());
                response.addProperty("usercode", mUser.getUsercode());
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
}
