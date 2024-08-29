package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.master.AssociateGroups;
import in.truethics.ethics.ethicsapiv10.model.master.LedgerFormParameter;
import in.truethics.ethics.ethicsapiv10.model.master.PrincipleGroups;
import in.truethics.ethics.ethicsapiv10.model.master.Principles;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerFormRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.AssociateGroupsRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.FoundationRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.PrincipleGroupsRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.PrincipleRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PrincipleGroupService {
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PrincipleGroupsRepository repository;
    @Autowired
    private PrincipleRepository principleRepository;
    @Autowired
    private LedgerFormRepository ledgerFormRepository;
    @Autowired
    private AssociateGroupsRepository associateGroupsRepository;
    @Autowired
    private PrincipleGroupsRepository groupsRepository;
    @Autowired
    private FoundationRepository foundationRepository;

    public Object createSubgroup(HttpServletRequest request) {
        PrincipleGroups subgroups = new PrincipleGroups();
        Principles principles = principleRepository.findByIdAndStatus(Long.parseLong(request.getParameter("principleId")),
                true);
        List<LedgerFormParameter> list = ledgerFormRepository.findAll();
        LedgerFormParameter ledgerFormParameter = ledgerFormRepository.findByFormName("Others");
        int cnt = 0;
        for (LedgerFormParameter mList : list) {
            if (mList.getFormName().equalsIgnoreCase(request.getParameter("subgroupName"))) {
                subgroups.setLedgerFormParameter(mList);
                cnt++;
                break;
            }
        }
        if (cnt == 0) {
            subgroups.setLedgerFormParameter(ledgerFormParameter);
        }
        subgroups.setGroupName(request.getParameter("subgroupName"));
        subgroups.setPrinciples(principles);
        subgroups.setStatus(true);
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            repository.save(subgroups);
            responseMessage.setMessage("Subprinciples Created");
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return responseMessage;
    }

    /*public JsonObject getAllAccountGroups(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        List<Principles> p_list = principleRepository.findAll();
        List<PrincipleGroups> pg_list = repository.findAll();
        List<AssociateGroups> ag_list = associateGroupsRepository.findByOutletId(users.getOutlet().getId());
        for (Principles mp : p_list) {
            JsonObject response = new JsonObject();
            response.addProperty("principle_id", mp.getId());
            response.addProperty("principle_name", mp.getPrincipleName());
            response.addProperty("ledger_form_parameter_id", mp.getLedgerFormParameter().getId());
            response.addProperty("ledger_form_parameter_slug", mp.getLedgerFormParameter().getSlugName());
            response.addProperty("sub_principle_id", "");
            response.addProperty("subprinciple_name", "");
            response.addProperty("unique_code", mp.getUniqueCode());
            response.addProperty("under_prefix", "P#" + mp.getId());
            response.addProperty("associates_id", "");
            response.addProperty("associates_name", "");
            result.add(response);
        }
        for (PrincipleGroups groups : pg_list) {
            JsonObject response = new JsonObject();
            response.addProperty("principle_id", groups.getPrinciples().getId());
            response.addProperty("principle_name", groups.getPrinciples().getPrincipleName());
            response.addProperty("sub_principle_id", groups.getId());
            response.addProperty("subprinciple_name", groups.getGroupName());
            response.addProperty("ledger_form_parameter_id", groups.getLedgerFormParameter().getId());
            response.addProperty("ledger_form_parameter_slug", groups.getLedgerFormParameter().getSlugName());
            response.addProperty("unique_code", groups.getUniqueCode());
            response.addProperty("under_prefix", "PG#" + groups.getId());
            response.addProperty("associates_id", "");
            response.addProperty("associates_name", "");
            result.add(response);
        }
        for (AssociateGroups associates : ag_list) {
            JsonObject response = new JsonObject();
            response.addProperty("associates_id", associates.getId());
            response.addProperty("associates_name", associates.getAssociatesName());
            response.addProperty("principle_id", associates.getPrinciples().getId());
            response.addProperty("principle_name", associates.getPrinciples().getPrincipleName());
            response.addProperty("sub_principle_id", associates.getPrincipleGroups() != null ? associates.getPrincipleGroups().getId().toString() : "");
            response.addProperty("subprinciple_name", associates.getPrincipleGroups() != null ? associates.getPrincipleGroups().getGroupName() : "");
            response.addProperty("ledger_form_parameter_id", associates.getLedgerFormParameter().getId());
            response.addProperty("ledger_form_parameter_slug", associates.getLedgerFormParameter().getSlugName());
            response.addProperty("under_prefix", "AG#" + associates.getId());
            result.add(response);
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("responseObject", result);
        return output;
        // return result;
    }
*/
    public Object createAssocitesgroup(HttpServletRequest request) {
        ResponseMessage responseMessage = new ResponseMessage();
        PrincipleGroups groups = null;
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        AssociateGroups associateGroups = new AssociateGroups();
        Principles principles = principleRepository.findByIdAndStatus
                (Long.parseLong(request.getParameter("principle_id")), true);
        if (request.getParameter("sub_principle_id") != null &&
                !request.getParameter("sub_principle_id").equalsIgnoreCase("")) {
            groups = groupsRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("sub_principle_id")), true);
        }
        associateGroups.setFoundations(principles.getFoundations());
        associateGroups.setPrinciples(principles);
        associateGroups.setOutlet(users.getOutlet());
        if (users.getBranch() != null)
            associateGroups.setBranch(users.getBranch());

        if (groups != null) {
            associateGroups.setPrincipleGroups(groups);
            associateGroups.setLedgerFormParameter(groups.getLedgerFormParameter());
        } else {
            associateGroups.setLedgerFormParameter(principles.getLedgerFormParameter());
        }
        associateGroups.setAssociatesName(request.getParameter("associates_group_name"));
        associateGroups.setUnder_prefix(request.getParameter("under_prefix"));
        String[] prefix = request.getParameter("under_prefix").split("#");
        associateGroups.setUnderId(Long.parseLong(prefix[1]));
        associateGroups.setStatus(true);
        try {
            associateGroupsRepository.save(associateGroups);
            responseMessage.setMessage("Account groups created");
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        } catch (DataIntegrityViolationException e) {
            System.out.println(e.getMessage());
            responseMessage.setMessage("Error in account groups creation");
            responseMessage.setResponseStatus(HttpStatus.CONFLICT.value());
            e.printStackTrace();
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
            responseMessage.setMessage("Error in account groups creation");
            responseMessage.setResponseStatus(HttpStatus.FORBIDDEN.value());
            e1.printStackTrace();
        }
        return responseMessage;
    }

    public Object editAssocitesgroup(HttpServletRequest request) {
        ResponseMessage responseMessage = new ResponseMessage();

        AssociateGroups associateGroups = associateGroupsRepository.findByIdAndStatus(
                Long.parseLong(request.getParameter("associates_id")), true);
        Map<String, String[]> paramMap = request.getParameterMap();
        Principles principles = null;
        PrincipleGroups groups = null;
        associateGroups.setAssociatesName(request.getParameter("associates_group_name"));
        if (paramMap.containsKey("principle_id")) {
            principles = principleRepository.findByIdAndStatus
                    (Long.parseLong(request.getParameter("principle_id")), true);
        }
        if (paramMap.containsKey("sub_principle_id")) {
            groups = groupsRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("sub_principle_id")), true);
        }
        associateGroups.setFoundations(principles.getFoundations());
        associateGroups.setPrinciples(principles);
        if (groups != null) {
            associateGroups.setPrincipleGroups(groups);
            associateGroups.setLedgerFormParameter(groups.getLedgerFormParameter());
        } else {
            associateGroups.setLedgerFormParameter(principles.getLedgerFormParameter());
        }
        associateGroups.setUnder_prefix(request.getParameter("under_prefix"));
        String[] prefix = request.getParameter("under_prefix").split("#");
        associateGroups.setUnderId(Long.parseLong(prefix[1]));
        try {
            associateGroupsRepository.save(associateGroups);
            responseMessage.setMessage("Account groups updated");
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        } catch (DataIntegrityViolationException e) {
            System.out.println(e.getMessage());
            responseMessage.setMessage("Error in account groups updation");
            responseMessage.setResponseStatus(HttpStatus.CONFLICT.value());
            e.printStackTrace();
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
            responseMessage.setMessage("Error in account groups updation");
            responseMessage.setResponseStatus(HttpStatus.FORBIDDEN.value());
            e1.printStackTrace();
        }
        return responseMessage;
    }

/*    public JsonObject getAssocitesgroup(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<AssociateGroups> ag_list = associateGroupsRepository.findByOutletIdOrderByIdDesc(users.getOutlet().getId());
        // JSONArray result = new JSONArray();
        JsonArray result = new JsonArray();
        for (AssociateGroups associates : ag_list) {
            //JSONObject response = new JSONObject();
            JsonObject response = new JsonObject();
            response.addProperty("associates_id", associates.getId());
            response.addProperty("associates_name", associates.getAssociatesName());
            response.addProperty("principle_id", associates.getPrinciples().getId());
            response.addProperty("principle_name",
                    associates.getPrinciples().getPrincipleName());

            response.addProperty("sub_principle_id",
                    associates.getPrincipleGroups() != null ?
                            associates.getPrincipleGroups().getId().toString() : "");
            response.addProperty("subprinciple_name",
                    associates.getPrincipleGroups() != null ?
                            associates.getPrincipleGroups().getGroupName() : "");
            response.addProperty("ledger_form_parameter_id",
                    associates.getLedgerFormParameter().getId());
            response.addProperty("ledger_form_parameter_slug",
                    associates.getLedgerFormParameter().getSlugName());
            response.addProperty("under_id", associates.getUnderId());
            response.addProperty("under_prefix", associates.getUnder_prefix());
            String prefix[] = associates.getUnder_prefix().split("#");
            response.addProperty("under_prefix_separator", prefix[0]);
            if (prefix[0].equalsIgnoreCase("P")) {
                response.addProperty("under_name",
                        associates.getPrinciples().getPrincipleName());
            } else if (prefix[0].equalsIgnoreCase("PG")) {
                response.addProperty("under_name",
                        associates.getPrincipleGroups().getGroupName());
            } else if (prefix[0].equalsIgnoreCase("AG")) {
                Long associateId = Long.parseLong(prefix[1]);
                String associateNames = associateGroupsRepository.findName(associateId);
                response.addProperty("under_name", associateNames);
            }
            result.add(response);
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("responseObject", result);
        return output;
    }*/

  /*  public Object DTAssociateGroups(Map<String, String> request, HttpServletRequest req) {
        Users users = jwtRequestFilter.getUserDataFromToken(req.getHeader("Authorization").substring(7));
        Long outletId = users.getOutlet().getId();
        Integer from = Integer.parseInt(request.get("from"));
        Integer to = Integer.parseInt(request.get("to"));
        String searchText = request.get("searchText");

        GenericDatatable genericDatatable = new GenericDatatable();
        List<AssociatesGroupsDTView> associatesGroupsDTViewList = new ArrayList<>();
        try {
            String query = "SELECT * FROM `associates_groups_dt_view` WHERE outlet_id='" + outletId + "'";

            if (!searchText.equalsIgnoreCase("")) {
                query = query + " AND (associates_name LIKE '%" + searchText + "%' OR  principle_name LIKE '%" +
                        searchText + "%' OR group_name LIKE '%" + searchText + "%' OR under LIKE '%" + searchText + " % ')";
            }

            String jsonToStr = request.get("sort");
            System.out.println(" sort " + jsonToStr);
            JsonObject jsonObject = new Gson().fromJson(jsonToStr, JsonObject.class);
            if (!jsonObject.get("colId").toString().equalsIgnoreCase("null") &&
                    jsonObject.get("colId").getAsString() != null) {
                System.out.println(" ORDER BY " + jsonObject.get("colId").getAsString());
                String sortBy = jsonObject.get("colId").getAsString();
                query = query + " ORDER BY " + sortBy;
                if (jsonObject.get("isAsc").getAsBoolean() == true) {
                    query = query + " ASC";
                } else {
                    query = query + " DESC";
                }
            } else {
                query = query + " ORDER BY associates_name ASC";
            }
            String query1 = query;
            Integer endLimit = to - from;
            query = query + " LIMIT " + from + ", " + endLimit;
            System.out.println("query " + query);

            Query q = entityManager.createNativeQuery(query, AssociatesGroupsDTView.class);
            Query q1 = entityManager.createNativeQuery(query1, AssociatesGroupsDTView.class);

            associatesGroupsDTViewList = q.getResultList();
            System.out.println("Limit total rows " + associatesGroupsDTViewList.size());

            List<AssociatesGroupsDTView> associatesGroupsDTViewArrayList = new ArrayList<>();
            associatesGroupsDTViewArrayList = q1.getResultList();
            System.out.println("total rows " + associatesGroupsDTViewArrayList.size());

            genericDatatable.setRows(associatesGroupsDTViewList);
            genericDatatable.setTotalRows(associatesGroupsDTViewArrayList.size());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());

            genericDatatable.setRows(associatesGroupsDTViewList);
            genericDatatable.setTotalRows(0);
        }
        return genericDatatable;
    }*/

    public Object get_associate_group(HttpServletRequest request) {
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            AssociateGroups associateGroups = associateGroupsRepository.findByIdAndStatus(
                    Long.parseLong(request.getParameter("id")), true);
            if (associateGroups != null) {
                responseMessage.setResponseObject(associateGroups);
                responseMessage.setResponseStatus(HttpStatus.OK.value());
            } else {
                responseMessage.setMessage("Associate group not found ");
                responseMessage.setResponseStatus(HttpStatus.NOT_FOUND.value());

            }
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
            e1.printStackTrace();
            responseMessage.setMessage("Failed to load associate group");
            responseMessage.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return responseMessage;
    }

    public JsonObject getAllAccountGroups(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        List<Principles> p_list = principleRepository.findAll();
        List<PrincipleGroups> pg_list = repository.findAll();
        List<AssociateGroups> ag_list = new ArrayList<>();

        if (users.getBranch() == null)
            ag_list = associateGroupsRepository.findByOutletId(users.getOutlet().getId());
        if (users.getBranch() != null)
            ag_list = associateGroupsRepository.findByOutletIdAndBranchIdOrderByIdDesc(users.getOutlet().getId(), users.getBranch().getId());
        for (Principles mp : p_list) {
            JsonObject response = new JsonObject();
            response.addProperty("principle_id", mp.getId());
            response.addProperty("principle_name", mp.getPrincipleName());
            response.addProperty("ledger_form_parameter_id", mp.getLedgerFormParameter().getId());
            response.addProperty("ledger_form_parameter_slug", mp.getLedgerFormParameter().getSlugName());
            response.addProperty("sub_principle_id", "");
            response.addProperty("subprinciple_name", "");
            response.addProperty("unique_code", mp.getUniqueCode());
            response.addProperty("under_prefix", "P#" + mp.getId());
            response.addProperty("associates_id", "");
            response.addProperty("associates_name", "");
            result.add(response);
        }
        for (PrincipleGroups groups : pg_list) {
            JsonObject response = new JsonObject();
            response.addProperty("principle_id", groups.getPrinciples().getId());
            response.addProperty("principle_name", groups.getPrinciples().getPrincipleName());
            response.addProperty("sub_principle_id", groups.getId());
            response.addProperty("subprinciple_name", groups.getGroupName());
            response.addProperty("ledger_form_parameter_id", groups.getLedgerFormParameter().getId());
            response.addProperty("ledger_form_parameter_slug", groups.getLedgerFormParameter().getSlugName());
            response.addProperty("unique_code", groups.getUniqueCode());
            response.addProperty("under_prefix", "PG#" + groups.getId());
            response.addProperty("associates_id", "");
            response.addProperty("associates_name", "");
            result.add(response);
        }
        for (AssociateGroups associates : ag_list) {
            JsonObject response = new JsonObject();
            response.addProperty("associates_id", associates.getId());
            response.addProperty("associates_name", associates.getAssociatesName());
            response.addProperty("principle_id", associates.getPrinciples().getId());
            response.addProperty("principle_name", associates.getPrinciples().getPrincipleName());
            response.addProperty("sub_principle_id", associates.getPrincipleGroups() != null ? associates.getPrincipleGroups().getId().toString() : "");
            response.addProperty("subprinciple_name", associates.getPrincipleGroups() != null ? associates.getPrincipleGroups().getGroupName() : "");
            response.addProperty("ledger_form_parameter_id", associates.getLedgerFormParameter().getId());
            response.addProperty("ledger_form_parameter_slug", associates.getLedgerFormParameter().getSlugName());
            response.addProperty("under_prefix", "AG#" + associates.getId());
            result.add(response);
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("responseObject", result);
        return output;
        // return result;
    }

    public JsonObject getAssocitesgroup(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<AssociateGroups> ag_list = new ArrayList<>();
        if (users.getBranch() == null)
            ag_list = associateGroupsRepository.findByOutletIdOrderByIdDesc(users.getOutlet().getId());
        if (users.getBranch() != null)
            ag_list = associateGroupsRepository.findByOutletIdAndBranchIdOrderByIdDesc(users.getOutlet().getId(), users.getBranch().getId());
        JsonArray result = new JsonArray();
        for (AssociateGroups associates : ag_list) {
            JsonObject response = new JsonObject();
            response.addProperty("associates_id", associates.getId());
            response.addProperty("associates_name", associates.getAssociatesName());
            response.addProperty("principle_id", associates.getPrinciples().getId());
            response.addProperty("principle_name",
                    associates.getPrinciples().getPrincipleName());

            response.addProperty("sub_principle_id",
                    associates.getPrincipleGroups() != null ?
                            associates.getPrincipleGroups().getId().toString() : "");
            response.addProperty("subprinciple_name",
                    associates.getPrincipleGroups() != null ?
                            associates.getPrincipleGroups().getGroupName() : "");
            response.addProperty("ledger_form_parameter_id",
                    associates.getLedgerFormParameter().getId());
            response.addProperty("ledger_form_parameter_slug",
                    associates.getLedgerFormParameter().getSlugName());
            response.addProperty("under_id", associates.getUnderId());
            response.addProperty("under_prefix", associates.getUnder_prefix());
            String[] prefix = associates.getUnder_prefix().split("#");
            response.addProperty("under_prefix_separator", prefix[0]);
            if (prefix[0].equalsIgnoreCase("P")) {
                response.addProperty("under_name",
                        associates.getPrinciples().getPrincipleName());
            } else if (prefix[0].equalsIgnoreCase("PG")) {
                response.addProperty("under_name",
                        associates.getPrincipleGroups().getGroupName());
            } else if (prefix[0].equalsIgnoreCase("AG")) {
                Long associateId = Long.parseLong(prefix[1]);
                String associateNames = associateGroupsRepository.findName(associateId);
                response.addProperty("under_name", associateNames);
            }
            result.add(response);
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("responseObject", result);
        return output;
    }
}
