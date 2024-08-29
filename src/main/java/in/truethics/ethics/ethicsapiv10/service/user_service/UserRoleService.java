package in.truethics.ethics.ethicsapiv10.service.user_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.truethics.ethics.ethicsapiv10.common.CommonAccessPermissions;
import in.truethics.ethics.ethicsapiv10.model.RoleAccessPermission;
import in.truethics.ethics.ethicsapiv10.model.access_permissions.SystemActionMapping;
import in.truethics.ethics.ethicsapiv10.model.access_permissions.SystemMasterModules;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import in.truethics.ethics.ethicsapiv10.model.user.UserRole;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.access_permission_repository.RoleAccessPermissionRepository;
import in.truethics.ethics.ethicsapiv10.repository.access_permission_repository.SystemAccessPermissionsRepository;
import in.truethics.ethics.ethicsapiv10.repository.access_permission_repository.SystemActionMappingRepository;
import in.truethics.ethics.ethicsapiv10.repository.access_permission_repository.SystemMasterModuleRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.OutletRepository;
import in.truethics.ethics.ethicsapiv10.repository.user_repository.UserRoleRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleAccessPermissionRepository roleAccessPermissionsRepository;
    @Autowired
    private SystemActionMappingRepository systemActionMappingRepository;

    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private OutletRepository outletRepository;

    @Autowired
    private CommonAccessPermissions accessPermissions;
    @Autowired
    private SystemMasterModuleRepository systemMasterModuleRepository;

    private static final Logger roleLogger = LogManager.getLogger(UserRoleService.class);
    @Autowired
    private SystemAccessPermissionsRepository systemAccessPermissionsRepository;


    public JsonObject createUserRole(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        //  ResponseMessage responseObject = new ResponseMessage();
        JsonObject responseObject = new JsonObject();
        UserRole userRole = new UserRole();
        Users user = null;
        try {
            userRole.setRoleName(request.getParameter("roleName"));
            userRole.setStatus(true);
            if (request.getHeader("Authorization") != null) {
                user = jwtRequestFilter.getUserDataFromToken(
                        request.getHeader("Authorization").substring(7));
                userRole.setCreatedBy(user.getId());
            }
            if (paramMap.containsKey("companyId")) {
                Outlet outlet = outletRepository.findByIdAndStatus(Long.parseLong(request.getParameter("outletId")), true);
                userRole.setOutlet(outlet);
            }
            UserRole newRole = userRoleRepository.save(userRole);
            try {
                /* Create Permissions */
                String jsonStr = request.getParameter("roles_permissions");
                if (jsonStr != null) {
                    JsonArray userPermissions = new JsonParser().parse(jsonStr).getAsJsonArray();
                    for (int i = 0; i < userPermissions.size(); i++) {
                        JsonObject mObject = userPermissions.get(i).getAsJsonObject();
                        RoleAccessPermission mPermissions = new RoleAccessPermission();
                        mPermissions.setUserRole(newRole);
                        SystemActionMapping mappings = systemActionMappingRepository.findByIdAndStatus(mObject.get("mapping_id").getAsLong(),
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
                        roleAccessPermissionsRepository.save(mPermissions);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                roleLogger.error("Exception in Role Master: " + e.getMessage());
                System.out.println(e.getMessage());
            }
            responseObject.addProperty("message", "Role master created successfully");
            responseObject.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            roleLogger.error("Exception in addUser: " + e1.getMessage());
            System.out.println("DataIntegrityViolationException " + e1.getMessage());
            responseObject.addProperty("responseStatus", HttpStatus.CONFLICT.value());
            responseObject.addProperty("message", "Usercode already used");
            return responseObject;
        } catch (Exception e) {
            e.printStackTrace();
            roleLogger.error("Exception in addUser: " + e.getMessage());
            responseObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseObject.addProperty("message", "Internal Server Error");
            e.printStackTrace();
            System.out.println("Exception:" + e.getMessage());
        }
        return responseObject;
    }


    public JsonObject getRolePermissionById(HttpServletRequest request) {

        UserRole role = userRoleRepository.findByIdAndStatus(Long.parseLong(request.getParameter("id")), true);
        JsonObject result = new JsonObject();
        result.addProperty("message", "success");
        result.addProperty("responseStatus", HttpStatus.OK.value());
        JsonArray role_permission = new JsonArray();
        if (role != null) {
            /***** get Role Permissions from access_permissions_tbl ****/
            List<RoleAccessPermission> accessPermissions = new ArrayList<>();
            accessPermissions = roleAccessPermissionsRepository.findByUserRoleIdAndStatus(role.getId(), true);

            for (RoleAccessPermission mPermissions : accessPermissions) {
                JsonObject masterObject = new JsonObject();
                JsonObject mObject = new JsonObject();
               /* SystemMasterModules parentModule = systemMasterModuleRepository.findByIdAndStatus(
                        mPermissions.getSystemActionMapping().getSystemMasterModules().getParentModuleId(), true);*/
                SystemMasterModules parentModule  = getChilds(mPermissions.getSystemActionMapping().getSystemMasterModules().getId());
                if (parentModule != null) {
                    masterObject.addProperty("id", parentModule.getId());
                    masterObject.addProperty("name", parentModule.getName());
                } /*else {
                    masterObject.addProperty("id", mPermissions.getSystemActionMapping().getSystemMasterModules().getId());
                    masterObject.addProperty("name", mPermissions.getSystemActionMapping().getSystemMasterModules().getName());
                }*/
                mObject.addProperty("id", mPermissions.getSystemActionMapping().getId());
                mObject.addProperty("name", mPermissions.getSystemActionMapping().getSystemMasterModules().getName());
              /*  if (mPermissions.getSystemActionMapping().getSystemMasterModules().getParentModuleId() != null) {
                    mObject.addProperty("id", parentModule.getId());
                    mObject.addProperty("name", parentModule.getName());
                } else {
                    mObject.addProperty("id", mPermissions.getSystemActionMapping().getSystemMasterModules().getId());
                    mObject.addProperty("name", mPermissions.getSystemActionMapping().getSystemMasterModules().getName());
                }*/
                JsonArray actions = new JsonArray();
                String actionsId = mPermissions.getUserActionsId();
                String[] actionsList = actionsId.split(",");
                Arrays.sort(actionsList);
                for (String actionId : actionsList) {
                    actions.add(actionId);
                }
                mObject.add("actions", actions);
                masterObject.add("level", mObject);
                role_permission.add(masterObject);
            }
            result.add("level", role_permission);
            result.addProperty("roleName", role.getRoleName());
        } else {
            result.addProperty("message", "error");
            result.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
        }
        return result;
    }


    public JsonObject getAllRolePermissionList() {
        JsonObject res = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        List<UserRole> userRole = userRoleRepository.findByStatus(true);
        if (userRole.size() > 0) {
            for (UserRole userRole1 : userRole) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", userRole1.getId());
                jsonObject.addProperty("roleName", userRole1.getRoleName());
                jsonArray.add(jsonObject);
            }
            res.add("responseObject", jsonArray);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        }

        return res;
    }

    public JsonObject getRoleById(HttpServletRequest request) {
        Long id = Long.valueOf(request.getParameter("id"));
        UserRole userRole = userRoleRepository.findByIdAndStatus(id, true);
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        JsonArray user_permission = new JsonArray();
        if (userRole != null) {
            response.addProperty("id", userRole.getId());
            response.addProperty("roleId", userRole.getId());
            response.addProperty("roleName", userRole.getRoleName());


            /***** get User Permissions from access_permissions_tbl ****/
            List<RoleAccessPermission> accessPermissions = new ArrayList<>();

            accessPermissions = roleAccessPermissionsRepository.findByUserRoleIdAndStatus(userRole.getId(), true);
            for (RoleAccessPermission mPermissions : accessPermissions) {
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

    public Object updateRole(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        ResponseMessage responseObject = new ResponseMessage();
        Users user = new Users();
        UserRole roleMaster = userRoleRepository.findByIdAndStatus(Long.parseLong(request.getParameter("id")),
                true);
        if (roleMaster != null) {
            if (paramMap.containsKey("roleName")) {
                roleMaster.setRoleName(request.getParameter("roleName"));
            }
            if (request.getHeader("Authorization") != null) {
                user = jwtRequestFilter.getUserDataFromToken(
                        request.getHeader("Authorization").substring(7));
                roleMaster.setCreatedBy(user.getId());
            }
            /* Update Permissions */
            String jsonStr = request.getParameter("role_permissions");
            JsonArray userPermissions = new JsonParser().parse(jsonStr).getAsJsonArray();
            for (int i = 0; i < userPermissions.size(); i++) {
                JsonObject mObject = userPermissions.get(i).getAsJsonObject();
                SystemActionMapping mappings = systemActionMappingRepository.findByIdAndStatus(mObject.get("mapping_id").getAsLong(),
                        true);
                RoleAccessPermission mPermissions = roleAccessPermissionsRepository.findByUserRoleIdAndStatusAndSystemActionMappingId(
                        roleMaster.getId(), true, mappings.getId());
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
                } else {
                    mPermissions = new RoleAccessPermission();
                    JsonArray mActionsArray = mObject.get("actions").getAsJsonArray();
                    String actionsId = "";
                    for (int j = 0; j < mActionsArray.size(); j++) {
                        actionsId = actionsId + mActionsArray.get(j).getAsString();
                        if (j < mActionsArray.size() - 1) {
                            actionsId = actionsId + ",";
                        }
                    }
                    mPermissions.setUserActionsId(actionsId);
                }
                mPermissions.setUserRole(roleMaster);
                mPermissions.setSystemActionMapping(mappings);
                mPermissions.setStatus(true);
                mPermissions.setCreatedBy(user.getId());

                roleAccessPermissionsRepository.save(mPermissions);
            }
            String del_user_perm = request.getParameter("del_role_permissions");
            JsonArray deleteUserPermission = new JsonParser().parse(del_user_perm).getAsJsonArray();
            for (int j = 0; j < deleteUserPermission.size(); j++) {
                Long moduleId = deleteUserPermission.get(j).getAsLong();
                //  SystemActionMapping delMapping = mappingRepository.findByIdAndStatus(moduleId, true);
                RoleAccessPermission delPermissions = roleAccessPermissionsRepository.findByUserRoleIdAndStatusAndSystemActionMappingId(
                        roleMaster.getId(), true, moduleId);
                delPermissions.setStatus(false);
                try {
                    roleAccessPermissionsRepository.save(delPermissions);
                } catch (Exception e) {
                }

            }
            userRoleRepository.save(roleMaster);
            responseObject.setMessage("User Role updated sucessfully");
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
    private SystemMasterModules getChilds(Long parentId) {
        SystemMasterModules modules = systemMasterModuleRepository.findByIdAndStatus(parentId, true);
        if (modules.getParentModuleId() == null) {
            return modules;
        } else {
            //   moduleSets.add(modules.getId());
            return getChilds(modules.getParentModuleId());
        }
    }

}
