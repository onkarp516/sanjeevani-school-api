package in.truethics.ethics.ethicsapiv10.model.master;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.DocumentRepository;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {
    private static final Logger DocumentLogger = LoggerFactory.getLogger(DocumentService.class);
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private DocumentRepository documentsRepository;

    public Object createDocument(HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        Document dtc = new Document();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            dtc.setName(request.getParameter("documentName"));
            dtc.setIsRequired(Boolean.valueOf(request.getParameter("isRequired")));
            dtc.setCreatedBy(users.getId());
            dtc.setStatus(true);
            try {
                Document mdtc = documentsRepository.save(dtc);
                responseObject.addProperty("message", "Document created successfully");
                responseObject.addProperty("responseStatus", HttpStatus.OK.value());
            } catch (DataIntegrityViolationException e) {
                DocumentLogger.error("createDocument -> Duplicate document name " + e.getMessage());
                responseObject.addProperty("message", "Already exist! Duplicate data not allowed");
                responseObject.addProperty("responseStatus", HttpStatus.CONFLICT.value());
            } catch (Exception e1) {
                DocumentLogger.error("createDocument -> failed to create document " + e1.getMessage());
                responseObject.addProperty("message", "Failed to create document");
                responseObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e1) {
            DocumentLogger.error("createDocument -> failed to create document " + e1.getMessage());
            responseObject.addProperty("message", "Failed to create document");
            responseObject.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return responseObject;
    }

    /* Get  all documents of branch */
    public JsonObject getAllDocument(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        try {
            List<Document> list = documentsRepository.findByStatus(true);
            if (list.size() > 0) {
                for (Document mGroup : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mGroup.getId());
                    response.addProperty("documentName", mGroup.getName());
                    response.addProperty("isRequired", mGroup.getIsRequired());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            DocumentLogger.error("getAllDocument -> failed to load data " + e.getMessage());
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }

    /*Update  the  documents*/
    public JsonObject updateDocument(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Document mdtc = documentsRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (mdtc != null) {
                mdtc.setName(request.getParameter("documentName"));
                mdtc.setUpdatedBy(users.getId());
                mdtc.setUpdatedAt(LocalDateTime.now());
                documentsRepository.save(mdtc);
                response.addProperty("message", "Document updated successfully");
                response.addProperty("responseStatus", HttpStatus.OK.value());
            } else {
                response.addProperty("message", "Document not found");
                response.addProperty("responseStatus", HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            DocumentLogger.error("updateDocument -> failed to update document " + e.getMessage());
            response.addProperty("message", "Failed to update document");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }


}
