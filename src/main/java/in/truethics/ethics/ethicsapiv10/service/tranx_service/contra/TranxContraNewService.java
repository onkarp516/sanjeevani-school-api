package in.truethics.ethics.ethicsapiv10.service.tranx_service.contra;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.truethics.ethics.ethicsapiv10.common.GenerateDates;
import in.truethics.ethics.ethicsapiv10.common.GenerateFiscalYear;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.tranx.contra.TranxContraDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.contra.TranxContraMaster;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerTransactionDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.TransactionTypeMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.contra_repository.TranxContraDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.contra_repository.TranxContraMasterRepository;
import in.truethics.ethics.ethicsapiv10.service.master_service.BranchService;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class TranxContraNewService {

    private static final Logger contraLogger = LoggerFactory.getLogger(BranchService.class);
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private LedgerMasterRepository ledgerMasterRepository;
    @Autowired
    private LedgerTransactionDetailsRepository transactionDetailsRepository;
    @Autowired
    private TransactionTypeMasterRepository tranxRepository;
    @Autowired
    private GenerateFiscalYear generateFiscalYear;
    @Autowired
    private TranxContraMasterRepository tranxContaMasterRepository;
    @Autowired
    private TranxContraDetailsRepository tranxContraDetailsRepository;

    public JsonObject contraLastRecord(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Long count = tranxContaMasterRepository.findLastRecord(users.getOutlet().getId());
        String serailNo = String.format("%05d", count + 1);// 5 digit serial number
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
        String csCode = "CNTR" + currentMonth + serailNo;
        JsonObject result = new JsonObject();
        result.addProperty("message", "success");
        result.addProperty("responseStatus", HttpStatus.OK.value());
        result.addProperty("count", count + 1);
        result.addProperty("contraNo", csCode);
        return result;
    }


    public JsonObject createContra(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Map<String, String[]> paramMap = request.getParameterMap();
        JsonObject response = new JsonObject();

        TranxContraMaster contraMaster = new TranxContraMaster();
        Branch branch = null;
        if (users.getBranch() != null)
            branch = users.getBranch();
        Outlet outlet = users.getOutlet();
        contraMaster.setBranch(branch);
        contraMaster.setOutlet(outlet);
        contraMaster.setStatus(true);
        LocalDate tranxDate = LocalDate.parse(request.getParameter("transaction_dt"));
        /* fiscal year mapping */
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(tranxDate);
        if (fiscalYear != null) {
            contraMaster.setFiscalYear(fiscalYear);
            contraMaster.setFinancialYear(fiscalYear.getFiscalYear());
        }

        contraMaster.setTranscationDate(tranxDate);
        contraMaster.setContraSrNo(Long.parseLong(request.getParameter("voucher_contra_sr_no")));
        contraMaster.setContraNo(request.getParameter("voucher_contra_no"));
        contraMaster.setTotalAmt(Double.parseDouble(request.getParameter("total_amt")));
        if (paramMap.containsKey("narration"))
            contraMaster.setNarrations(request.getParameter("narration"));
        else {
            contraMaster.setNarrations("NA");
        }
        contraMaster.setCreatedBy(users.getId());
        TranxContraMaster tranxContraMaster = tranxContaMasterRepository.save(contraMaster);

        try {
            double total_amt = 0.0;
            String jsonStr = request.getParameter("rows");
            JsonParser parser = new JsonParser();
            JsonArray row = parser.parse(jsonStr).getAsJsonArray();
            for (int i = 0; i < row.size(); i++) {
                JsonObject contraRow = row.get(i).getAsJsonObject();
                TranxContraDetails tranxContraDetails = new TranxContraDetails();
                LedgerMaster ledgerMaster = null;

                tranxContraDetails.setBranch(branch);
                tranxContraDetails.setOutlet(outlet);
                tranxContraDetails.setStatus(true);
                ledgerMaster = ledgerMasterRepository.findByIdAndStatus(contraRow.get("perticulars").getAsJsonObject().get("id").getAsLong(), true);
                if (ledgerMaster != null)
                    tranxContraDetails.setLedgerMaster(ledgerMaster);
                tranxContraDetails.setTranxContraMaster(tranxContraMaster);
                tranxContraDetails.setType(contraRow.get("type").getAsString());
//                tranxContraDetails.setLedgerName(contraRow.get("perticulars").getAsJsonObject().get("ledger_name").getAsString());
                total_amt = contraRow.get("paid_amt").getAsDouble();
                if (contraRow.has("bank_payment_type"))
                    tranxContraDetails.setPayment_type(contraRow.get("bank_payment_type").getAsString());
                tranxContraDetails.setPaidAmount(total_amt);

                if (contraRow.has("bank_payment_no"))
                    tranxContraDetails.setBankPaymentNo(contraRow.get("bank_payment_no").getAsString());

                JsonObject perticulars = contraRow.get("perticulars").getAsJsonObject();

                //   ledgerMaster = ledgerMasterRepository.findByIdAndStatus(perticulars.get("id").getAsLong(), true);
                if (perticulars.get("type").getAsString().equalsIgnoreCase("bank_account"))
                    tranxContraDetails.setBankName(perticulars.get("label").getAsString());
                else {
                    tranxContraDetails.setBankName("Cash A/c");
                }

                tranxContraDetails.setLedgerType(ledgerMaster.getSlugName());
                tranxContraDetails.setCreatedBy(users.getId());
                TranxContraDetails mContra = tranxContraDetailsRepository.save(tranxContraDetails);
                insertIntoPostings(mContra, total_amt);
            }
            response.addProperty("message", "Contra created successfully");
            response.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            contraLogger.error("Error in Contra Creation :->" + e.getMessage());
            response.addProperty("message", "Error in Contra creation");
            response.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    private void insertIntoPostings(TranxContraDetails mContra, double total_amt) {
        TransactionTypeMaster tranxType = tranxRepository.findByTransactionNameIgnoreCase("contra");
        try {
            if (mContra.getType().equalsIgnoreCase("dr")) {
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(mContra.getLedgerMaster().
                                getFoundations().getId(), mContra.getLedgerMaster().getPrinciples() != null ?
                                mContra.getLedgerMaster().getPrinciples().getId() : null,
                        mContra.getLedgerMaster().getPrincipleGroups() != null ?
                                mContra.getLedgerMaster().getPrincipleGroups().getId() : null,
                        null,
                        tranxType.getId(), mContra.getLedgerMaster().getBalancingMethod() != null ?
                                mContra.getLedgerMaster().getBalancingMethod().getId() : null,
                        mContra.getBranch() != null ?
                                mContra.getBranch().getId() : null,
                        mContra.getOutlet().getId(), "NA", total_amt * -1, 0.0,
                        mContra.getTranxContraMaster().getTranscationDate(), null, mContra.getId(), tranxType.getTransactionName(),
                        mContra.getLedgerMaster().getUnderPrefix(), mContra.getTranxContraMaster().getFinancialYear(),
                        mContra.getCreatedBy(), mContra.getLedgerMaster().getId(), mContra.getTranxContraMaster().getContraNo());

            } else {
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(mContra.getLedgerMaster().
                                getFoundations().getId(), mContra.getLedgerMaster().getPrinciples() != null ?
                                mContra.getLedgerMaster().getPrinciples().getId() : null,
                        mContra.getLedgerMaster().getPrincipleGroups() != null ?
                                mContra.getLedgerMaster().getPrincipleGroups().getId() : null,
                        null,
                        tranxType.getId(), mContra.getLedgerMaster().getBalancingMethod() != null ?
                                mContra.getLedgerMaster().getBalancingMethod().getId() : null,
                        mContra.getBranch() != null ?
                                mContra.getBranch().getId() : null,
                        mContra.getOutlet().getId(), "NA", 0.0, total_amt,
                        mContra.getTranxContraMaster().getTranscationDate(), null, mContra.getId(), tranxType.getTransactionName(),
                        mContra.getLedgerMaster().getUnderPrefix(), mContra.getTranxContraMaster().getFinancialYear(),
                        mContra.getCreatedBy(), mContra.getLedgerMaster().getId(), mContra.getTranxContraMaster().getContraNo());
            }
        } catch (Exception e) {
            e.printStackTrace();
            contraLogger.error("Error in Contra Postings :->" + e.getMessage());
        }
    }

    public JsonObject contraListbyOutlet(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<TranxContraMaster> contra = tranxContaMasterRepository.
                findByOutletIdAndStatusOrderByIdDesc(users.getOutlet().getId(), true);

        for (TranxContraMaster vouchers : contra) {
            JsonObject response = new JsonObject();
            response.addProperty("id", vouchers.getId());
            response.addProperty("contra_code", vouchers.getContraNo());
            response.addProperty("transaction_dt", vouchers.getTranscationDate().toString());
            response.addProperty("contra_sr_no", vouchers.getContraSrNo());
//            response.addProperty("ledger_name", vouchers.getContraSrNo());

            response.addProperty("total_amount", vouchers.getTotalAmt());


            result.add(response);
        }

        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);
        return output;
    }
}
