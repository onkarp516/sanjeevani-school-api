package in.truethics.ethics.ethicsapiv10.service.tranx_service.sales;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.common.GenerateFiscalYear;
import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesInvoice;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerTransactionDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository.TranxSalesInvoiceRepository;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TranxSalesInvoiceService {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private TranxSalesInvoiceRepository salesTransactionRepository;
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private GenerateFiscalYear generateFiscalYear;
    @Autowired
    private LedgerMasterRepository ledgerMasterRepository;
    @Autowired
    private LedgerTransactionDetailsRepository transactionDetailsRepository;

    // Sales Invoice List
//    public JsonObject saleList(HttpServletRequest request) {
//        JsonArray result = new JsonArray();
//        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
//        List<TranxSalesInvoice> saleInvoice = new ArrayList<>();
//        if (users.getBranch() != null) {
//            saleInvoice = salesTransactionRepository.findByOutletIdAndBranchIdAndStatusOrderByIdDesc(users.getOutlet().getId(), users.getBranch().getId(),  true);
//        } else {
//            saleInvoice = salesTransactionRepository.findByOutletIdAndStatusOrderByIdDesc(users.getOutlet().getId(),  true);
//        }
//        for (TranxSalesInvoice invoices : saleInvoice) {
//            JsonObject response = new JsonObject();
//            response.addProperty("id", invoices.getId());
//            response.addProperty("invoice_no", invoices.getSalesInvoiceNo());
//            response.addProperty("invoice_date", invoices.getBillDate().toString());
//            response.addProperty("sale_serial_number", invoices.getSalesSerialNumber());
//            response.addProperty("total_amount", invoices.getTotalAmount());
//            response.addProperty("sundry_debtor_name", invoices.getSundryDebtors().getLedgerName());
//            response.addProperty("sundry_debtor_id", invoices.getSundryDebtors().getId());
////            response.addProperty("sale_account_name", invoices.getSalesAccountLedger().getLedgerName());
//            response.addProperty("narration", invoices.getNarration());
//
//            result.add(response);
//        }
//        JsonObject output = new JsonObject();
//        output.addProperty("message", "success");
//        output.addProperty("responseStatus", HttpStatus.OK.value());
//        output.add("data", result);
//        return output;
//    }
    // Sales Invoice List By Date From And To Or Current Date
    public JsonObject saleList(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        List<TranxSalesInvoice> saleInvoice = new ArrayList<>();
//        LedgerTransactionDetails ledgerTransactionDetails;
        Map<String, String[]> paramMap = request.getParameterMap();
        String endDate = null;
        LocalDate endDatep = null;
        String currentDate = null;
        LocalDate currentDatep = null;
        if (paramMap.containsKey("endDate") && paramMap.containsKey("currentDate")) {
            endDate = request.getParameter("endDate");
            endDatep = LocalDate.parse(endDate);
            currentDate = request.getParameter("currentDate");
            currentDatep = LocalDate.parse(currentDate);
        } else {

        }
        /***** start and end date Query ****/
        if (endDatep != null) {
            if (users.getBranch() != null) {
//                ledgerTransactionDetails = transactionDetailsRepository.findByOutletIdAndBranchIdAndStatusOrderByIdDesc(users.getOutlet().getId(), users.getBranch().getId(), true);
                saleInvoice = salesTransactionRepository.findByOutletIdAndBranchIdAndStatusAndBillDateBetweenOrderByIdDesc(users.getOutlet().getId(), users.getBranch().getId(), true, currentDatep, endDatep);
            } else {
                saleInvoice = salesTransactionRepository.findByOutletIdAndStatusAndBillDateBetweenOrderByIdDesc(users.getOutlet().getId(), true, currentDatep, endDatep);
//                ledgerTransactionDetails = transactionDetailsRepository.findByOutletIdAndStatusOrderByIdDesc(users.getOutlet().getId(), true);
            }
        }
        /***** On Load All List Load Query ****/
        else {
            if (users.getBranch() != null) {
//                ledgerTransactionDetails = transactionDetailsRepository.findByOutletIdAndBranchIdAndStatusOrderByIdDesc(users.getOutlet().getId(), users.getBranch().getId(), true);
                saleInvoice = salesTransactionRepository.findByOutletIdAndBranchIdAndStatusOrderByIdDesc(users.getOutlet().getId(), users.getBranch().getId(), true);
            } else {
//                ledgerTransactionDetails = transactionDetailsRepository.findByOutletIdAndStatusOrderByIdDesc(users.getOutlet().getId(), true);
                saleInvoice = salesTransactionRepository.findByOutletIdAndStatusOrderByIdDesc(users.getOutlet().getId(), true);
            }
        }
        for (TranxSalesInvoice invoices : saleInvoice) {

            JsonObject response = new JsonObject();
            response.addProperty("id", invoices.getId());
            response.addProperty("invoice_no", invoices.getSalesInvoiceNo());
            response.addProperty("invoice_date", invoices.getBillDate().toString());
            response.addProperty("sale_serial_number", invoices.getSalesSerialNumber());
            response.addProperty("total_amount", invoices.getTotalAmount());
            response.addProperty("sundry_debtor_name", invoices.getSundryDebtors().getLedgerName());
            response.addProperty("sd_id", invoices.getSundryDebtors().getId());

//            LedgerTransactionDetails ledgerTransactionDetails = transactionDetailsRepository.findTop1ByLedgerMasterIdAndStatusOrderByIdDesc(
//                    invoices.getSundryDebtors().getId(), true);
//

//            if (ledgerTransactionDetails != null) {
//                response.addProperty("debit", ledgerTransactionDetails.getDebit());
//                response.addProperty("credit", ledgerTransactionDetails.getCredit());
//            }
            response.addProperty("sundry_debtor_id", invoices.getSundryDebtors().getId());
//            response.addProperty("sale_account_name", invoices.getSalesAccountLedger().getLedgerName())

            result.add(response);
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);
        return output;
    }


    public JsonObject dateWiseTotalAmtBySalesAcc(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject res = new JsonObject();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        try {
            List<TranxSalesInvoice> saleInvoice = new ArrayList<>();
            Double salesactotalAmtCal = 0.0,directIncome=0.0,indirectIncome=0.0,directExpenses=0.0,indirectExpenses=0.0;
            LocalDate startDate = LocalDate.parse(request.getParameter("startDate"));
            LocalDate endDate = LocalDate.parse(request.getParameter("endDate"));
            if (users.getBranch() != null) {
                //Credit Side --> Sales Account Total Amount
                salesactotalAmtCal = salesTransactionRepository.findByDateWiseTotalAmountOuletAndBranchStatus(users.getOutlet().getId(),users.getBranch().getId(),true,startDate,endDate);
                //Credit Side --> Direct Income Total Amount
                directIncome=transactionDetailsRepository.findByDateWiseDirectIncomeTotalAmountOutletAndBranchStatus(users.getOutlet().getId(),users.getBranch().getId(),true,startDate,endDate);
                indirectIncome=transactionDetailsRepository.findByDateWiseDirectIncomeTotalAmountOutletAndBranchStatusInDIn(users.getOutlet().getId(),users.getBranch().getId(),true,startDate,endDate);
                directExpenses=transactionDetailsRepository.findByDateWiseDirectIncomeTotalAmountOutletAndBranchStatusDE(users.getOutlet().getId(),users.getBranch().getId(),true,startDate,endDate);
                indirectExpenses=transactionDetailsRepository.findByDateWiseDirectIncomeTotalAmountOutletAndBranchStatusINDE(users.getOutlet().getId(),users.getBranch().getId(),true,startDate,endDate);

            }
            else {
                //Credit Side --> Sales Account Total Ammount
                salesactotalAmtCal = salesTransactionRepository.findByDateWiseTotalAmountOuletAndStatus(users.getOutlet().getId(), true, startDate, endDate);
                //Credit Side --> Direct Income Total Amount
                directIncome=transactionDetailsRepository.findByDateWiseDirectIncomeTotalAmountOutletAndStatus(users.getOutlet().getId(), true, startDate, endDate);
                indirectIncome=transactionDetailsRepository.findByDateWiseDirectIncomeTotalAmountOutletAndStatusInDIn(users.getOutlet().getId(), true, startDate, endDate);
                directExpenses=transactionDetailsRepository.findByDateWiseDirectIncomeTotalAmountOutletAndStatusDE(users.getOutlet().getId(), true, startDate, endDate);
                indirectExpenses=transactionDetailsRepository.findByDateWiseDirectIncomeTotalAmountOutletAndStatusINDE(users.getOutlet().getId(), true, startDate, endDate);

            }
            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(8);
            res.addProperty("opening_stock",0);
            res.addProperty("purchase_account",0);
            res.addProperty("gross_profit",0);
            res.addProperty("gross_loss",0);
            res.addProperty("message", "Success");
            res.addProperty("sales_acc_amt",salesactotalAmtCal);
            res.addProperty("direct_income",df.format(directIncome));
            res.addProperty("in_direct_income",df.format(indirectIncome));
            res.addProperty("direct_expenses",df.format(directExpenses));
            res.addProperty("closing_stock",0);
            res.addProperty("gross_loss",0);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            res.addProperty("message", "Failed To Load Data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }


}
