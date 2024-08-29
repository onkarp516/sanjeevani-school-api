package in.truethics.ethics.ethicsapiv10.service.tranx_service.purchase;

import com.google.gson.*;
import in.truethics.ethics.ethicsapiv10.common.CustomArrayUtilities;
import in.truethics.ethics.ethicsapiv10.common.GenerateDates;
import in.truethics.ethics.ethicsapiv10.common.GenerateFiscalYear;
import in.truethics.ethics.ethicsapiv10.model.inventory.InventorySerialNumberSummary;
import in.truethics.ethics.ethicsapiv10.model.inventory.PackingMaster;
import in.truethics.ethics.ethicsapiv10.model.inventory.Product;
import in.truethics.ethics.ethicsapiv10.model.inventory.Units;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.tranx.debit_note.TranxDebitNoteDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.debit_note.TranxDebitNoteNewReferenceMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.*;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.inventory_repository.*;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerBalanceSummaryRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerTransactionDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.TransactionStatusRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.TransactionTypeMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.debitnote_repository.TranxDebitNoteDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.debitnote_repository.TranxDebitNoteNewReferenceRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository.*;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TranxPurInvoiceService {

    private static final Logger purInvoiceLogger = LoggerFactory.getLogger(TranxPurInvoiceService.class);
    @PersistenceContext
    EntityManager entityManager;
    List<Long> dbList = new ArrayList<>(); // for saving all ledgers Id against Purchase invoice from DB
    List<Long> mInputList = new ArrayList<>(); // input all ledgers Id against Purchase invoice from request
    @Autowired
    private TransactionTypeMasterRepository tranxRepository;
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private GenerateFiscalYear generateFiscalYear;
    @Autowired
    private LedgerMasterRepository ledgerMasterRepository;
    @Autowired
    private LedgerBalanceSummaryRepository balanceSummaryRepository;
    @Autowired
    private LedgerTransactionDetailsRepository transactionDetailsRepository;
    @Autowired
    private TranxPurInvoiceRepository tranxPurInvoiceRepository;
    @Autowired
    private TranxPurOrderRepository tranxPurOrderRepository;
    @Autowired
    private TranxPurOrderDetailsRepository tranxPurOrderDetailsRepository;
    @Autowired
    private TranxPurChallanRepository tranxPurChallanRepository;
    @Autowired
    private TranxPurChallanDetailsRepository tranxPurChallanDetailsRepository;
    @Autowired
    private PurInvoiceDutiesTaxesRepository purInvoiceDutiesTaxesRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PurchaseInvoiceDetailsRepository invoiceDetailsRepository;
    @Autowired
    private PurchaseInvoiceProductSrNumberRepository serialNumberRepository;
    /* @Autowired
     private InventoryDTO inventoryDTO;*/
    @Autowired
    private InventorySerialNumberSummaryRepository inventorySerialNumberSummaryRepository;
    @Autowired
    private PurInvoiceAdditionalChargesRepository purInvoiceAdditionalChargesRepository;
    @Autowired
    private ProductUnitRepository productUnitRepository;
    @Autowired
    private TransactionStatusRepository transactionStatusRepository;
    /* @Autowired
     private PaymentNewReferenceRepository paymentNewReferenceRepository;*/
    @Autowired
    private TranxDebitNoteNewReferenceRepository tranxDebitNoteNewReferenceRepository;
    @Autowired
    private TranxDebitNoteDetailsRepository tranxDebitNoteDetailsRepository;
    @Autowired
    private UnitsRepository unitsRepository;
    @Autowired
    private TranxPurInvoiceDetailsUnitsRepository tranxPurInvoiceUnitsRepository;
    @Autowired
    private PackingMasterRepository packingMasterRepository;

    public Object insertPurchaseInvoices(HttpServletRequest request) {
        TranxPurInvoice mPurchaseTranx = null;
        Map<String, String[]> paramMap = request.getParameterMap();
        ResponseMessage responseMessage = new ResponseMessage();
        /* Validations with Serial Numbers */
        Boolean flag = validateSerialNumbers(request.getParameter("row"));
        if (flag) {
            responseMessage.setMessage("One of the Serial number of Product already mapped with Purchase");
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        } else {
            mPurchaseTranx = saveIntoPurchaseInvoice(request);
            if (mPurchaseTranx != null) {
                insertIntoLedgerTranxDetails(mPurchaseTranx, request);// Accounting Postings
                responseMessage.setMessage("purchase invoice created successfully");
                responseMessage.setResponseStatus(HttpStatus.OK.value());
            } else {
                responseMessage.setMessage("Error in purchase invoice creation");
                responseMessage.setResponseStatus(HttpStatus.FORBIDDEN.value());
            }
        }
        return responseMessage;
    }

    private void insertIntoLedgerTranxDetails(TranxPurInvoice mPurchaseTranx, HttpServletRequest request) {
        /* start of ledger trasaction details */
        TransactionTypeMaster tranxType = tranxRepository.findByTransactionNameIgnoreCase("purchase");
//        generateTransactions.insertIntoTranxsDetails(mPurchaseTranx,tranxType);
        try {

            insertIntoTranxDetailSC(mPurchaseTranx, tranxType);// for Sundry Creditors : cr
            insertIntoTranxDetailPA(mPurchaseTranx, tranxType); // for Purchase Accounts : dr
            insertIntoTranxDetailPD(mPurchaseTranx, tranxType); // for Purchase Discounts : cr
            insertIntoTranxDetailRO(mPurchaseTranx, tranxType); // for Round Off : cr or dr
            insertDB(mPurchaseTranx, "AC", tranxType); // for Additional Charges : dr
            insertDB(mPurchaseTranx, "DT", tranxType); // for Duties and Taxes : dr
            /* end of ledger transaction details */
        } catch (Exception e) {
            purInvoiceLogger.error("Exception->PurchaseInvoiceService(class)->insertIntoLedgerTranxDetails(method) : " + e.getMessage());
            System.out.println("Posting Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /* Insertion into Transaction Details Table of Sundry Creditors Ledgers for Purchase Invoice */
    public void insertIntoTranxDetailSC(TranxPurInvoice mPurchaseTranx,
                                        TransactionTypeMaster tranxType) {
        try {
            transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(mPurchaseTranx.getSundryCreditors().
                            getFoundations().getId(), mPurchaseTranx.getSundryCreditors().getPrinciples().getId(),
                    mPurchaseTranx.getSundryCreditors().getPrincipleGroups().getId(),
                    mPurchaseTranx.getAssociateGroups() != null ? mPurchaseTranx.getAssociateGroups().getId() : null,
                    tranxType.getId(), mPurchaseTranx.getSundryCreditors().getBalancingMethod().getId(),
                    mPurchaseTranx.getBranch() != null ? mPurchaseTranx.getBranch().getId() : null,
                    mPurchaseTranx.getOutlet().getId(), "pending", 0.0, mPurchaseTranx.getBalance(),
                    mPurchaseTranx.getTransactionDate(), null, mPurchaseTranx.getId(), tranxType.getTransactionName(),
                    mPurchaseTranx.getSundryCreditors().getUnderPrefix(), mPurchaseTranx.getFinancialYear(),
                    mPurchaseTranx.getCreatedBy(), mPurchaseTranx.getSundryCreditors().getId(), mPurchaseTranx.getVendorInvoiceNo());
        } catch (Exception e) {
            purInvoiceLogger.error("Exception->PurchaseInvoiceService(class)->insertIntoTranxDetailSC(method) :" + e.getMessage());
            e.printStackTrace();
            System.out.println("Store Procedure Error " + e.getMessage());
        }
    }

    /* Insertion into Transaction Details Table of Purchase Accounts Ledgers for Purchase Invoice*/
    public void insertIntoTranxDetailPA(TranxPurInvoice mPurchaseTranx, TransactionTypeMaster tranxType) {
        try {
            transactionDetailsRepository.insertIntoLegerTranxDetailsPosting
                    (mPurchaseTranx.getPurchaseAccountLedger().getFoundations().getId(),
                            mPurchaseTranx.getPurchaseAccountLedger().getPrinciples().getId(),
                            mPurchaseTranx.getPurchaseAccountLedger().getPrincipleGroups() != null ?
                                    mPurchaseTranx.getPurchaseAccountLedger().getPrincipleGroups().getId() : null,
                            mPurchaseTranx.getAssociateGroups() != null ?
                                    mPurchaseTranx.getAssociateGroups().getId() : null, tranxType.getId(), null,
                            mPurchaseTranx.getBranch() != null ? mPurchaseTranx.getBranch().getId() : null,
                            mPurchaseTranx.getOutlet().getId(), "pending",
                            mPurchaseTranx.getTotalBaseAmount() * -1, 0.0,
                            mPurchaseTranx.getTransactionDate(), null, mPurchaseTranx.getId(),
                            tranxType.getTransactionName(),
                            mPurchaseTranx.getPurchaseAccountLedger().getUnderPrefix(),
                            mPurchaseTranx.getFinancialYear(), mPurchaseTranx.getCreatedBy(),
                            mPurchaseTranx.getPurchaseAccountLedger().getId(), mPurchaseTranx.getVendorInvoiceNo());
        } catch (Exception e) {
            purInvoiceLogger.error("Exception->PurchaseInvoiceService(class)->insertIntoTranxDetailPA(method) :" + e.getMessage());
            e.printStackTrace();
        }
    }

    /* Insertion into Transaction Details Table of Purchase Discount Ledgers for Purchase Invoice*/
    public void insertIntoTranxDetailPD(TranxPurInvoice mPurchaseTranx, TransactionTypeMaster tranxType) {
        try {
            if (mPurchaseTranx.getPurchaseDiscountLedger() != null) {
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                        mPurchaseTranx.getPurchaseDiscountLedger().getPrinciples().
                                getFoundations().getId(),
                        mPurchaseTranx.getPurchaseDiscountLedger().getPrinciples().getId(),
                        mPurchaseTranx.getPurchaseDiscountLedger().getPrincipleGroups() != null ?
                                mPurchaseTranx.getPurchaseDiscountLedger().getPrincipleGroups().getId() : null,
                        mPurchaseTranx.getAssociateGroups() != null ?
                                mPurchaseTranx.getAssociateGroups().getId() : null, tranxType.getId(), null,
                        mPurchaseTranx.getBranch() != null ? mPurchaseTranx.getBranch().getId() : null,
                        mPurchaseTranx.getOutlet().getId(), "pending", 0.0, mPurchaseTranx.getTotalPurchaseDiscountAmt(),
                        mPurchaseTranx.getTransactionDate(), null, mPurchaseTranx.getId(), tranxType.getTransactionName(),
                        mPurchaseTranx.getPurchaseDiscountLedger().getUnderPrefix(), mPurchaseTranx.getFinancialYear(),
                        mPurchaseTranx.getCreatedBy(), mPurchaseTranx.getPurchaseDiscountLedger().getId(), mPurchaseTranx.getVendorInvoiceNo());
            }
        } catch (Exception e) {
            purInvoiceLogger.error("Exception->PurchaseInvoiceService(class)->insertIntoTranxDetailPD(method) :" + e.getMessage());
            e.printStackTrace();
        }
    }

    /* Insertion into Transaction Details Table of Purchase RoundOff Ledgers for Purchase Invoice*/
    private void insertIntoTranxDetailRO(TranxPurInvoice mPurchaseTranx,
                                         TransactionTypeMaster tranxType) {
        try {
            if (mPurchaseTranx.getRoundOff() >= 0) {
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                        mPurchaseTranx.getPurchaseRoundOff().getPrinciples().
                                getFoundations().getId(),
                        mPurchaseTranx.getPurchaseRoundOff().getPrinciples().getId(),
                        mPurchaseTranx.getPurchaseRoundOff().getPrincipleGroups() != null ?
                                mPurchaseTranx.getPurchaseRoundOff().getPrincipleGroups().getId() : null,
                        mPurchaseTranx.getAssociateGroups() != null ?
                                mPurchaseTranx.getAssociateGroups().getId() : null, tranxType.getId(), null,
                        mPurchaseTranx.getBranch() != null ? mPurchaseTranx.getBranch().getId() : null,
                        mPurchaseTranx.getOutlet().getId(), "pending", mPurchaseTranx.getRoundOff() * -1,
                        0.0, mPurchaseTranx.getTransactionDate(), null, mPurchaseTranx.getId(),
                        tranxType.getTransactionName(), mPurchaseTranx.getPurchaseRoundOff().getUnderPrefix(),
                        mPurchaseTranx.getFinancialYear(), mPurchaseTranx.getCreatedBy(),
                        mPurchaseTranx.getPurchaseRoundOff().getId(), mPurchaseTranx.getVendorInvoiceNo());
            } else if (mPurchaseTranx.getRoundOff() < 0) {
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                        mPurchaseTranx.getPurchaseRoundOff().getPrinciples().
                                getFoundations().getId(), mPurchaseTranx.getPurchaseRoundOff().getPrinciples().getId(),
                        mPurchaseTranx.getPurchaseRoundOff().getPrincipleGroups() != null ?
                                mPurchaseTranx.getPurchaseRoundOff().getPrincipleGroups().getId() : null,
                        mPurchaseTranx.getAssociateGroups() != null ?
                                mPurchaseTranx.getAssociateGroups().getId() :
                                null, tranxType.getId(), null,
                        mPurchaseTranx.getBranch() != null ? mPurchaseTranx.getBranch().getId() : null,
                        mPurchaseTranx.getOutlet().getId(), "pending", 0.0,
                        Math.abs(mPurchaseTranx.getRoundOff()),
                        mPurchaseTranx.getTransactionDate(), null, mPurchaseTranx.getId(),
                        tranxType.getTransactionName(), mPurchaseTranx.getPurchaseRoundOff().getUnderPrefix(),
                        mPurchaseTranx.getFinancialYear(),
                        mPurchaseTranx.getCreatedBy(), mPurchaseTranx.getPurchaseRoundOff().getId(), mPurchaseTranx.getVendorInvoiceNo());
            }
        } catch (Exception e) {
            purInvoiceLogger.error("Exception->PurchaseInvoiceService(class)->insertIntoTranxDetailRO(method) :" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertDB(TranxPurInvoice mPurchaseTranx, String ledgerName, TransactionTypeMaster tranxType) {

        /* Purchase Duties Taxes */
        if (ledgerName.equalsIgnoreCase("DT")) {
            List<TranxPurInvoiceDutiesTaxes> list = new ArrayList<>();
            list = purInvoiceDutiesTaxesRepository.findByPurchaseTransactionAndStatus(
                    mPurchaseTranx, true);
            if (list.size() > 0) {
                for (TranxPurInvoiceDutiesTaxes mDuties : list) {
                    insertFromDutiesTaxes(mDuties, mPurchaseTranx, tranxType);
                }
            }
        } else if (ledgerName.equalsIgnoreCase("AC")) {
            /* Purchase Additional Charges */
            List<TranxPurInvoiceAdditionalCharges> list = new ArrayList<>();
            list = purInvoiceAdditionalChargesRepository.findByPurchaseTransaction(mPurchaseTranx);
            if (list.size() > 0) {
                for (TranxPurInvoiceAdditionalCharges mAdditinoalCharges : list) {
                    insertFromAdditionalCharges(mAdditinoalCharges,
                            mPurchaseTranx, tranxType);
                }
            }
        }
    }

    public void insertFromDutiesTaxes(TranxPurInvoiceDutiesTaxes mDuties,
                                      TranxPurInvoice mPurchaseTranx, TransactionTypeMaster tranxType) {
        try {
            transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                    mDuties.getDutiesTaxes().getPrinciples().getFoundations().getId(),
                    mDuties.getDutiesTaxes().getPrinciples().getId(),
                    mDuties.getDutiesTaxes().getPrincipleGroups() != null ?
                            mDuties.getDutiesTaxes().getPrincipleGroups().getId() : null,
                    mPurchaseTranx.getAssociateGroups() != null ?
                            mPurchaseTranx.getAssociateGroups().getId() : null,
                    tranxType.getId(), null,
                    mPurchaseTranx.getBranch() != null ? mPurchaseTranx.getBranch().getId() : null,
                    mPurchaseTranx.getOutlet().getId(), "pending", mDuties.getAmount() * -1,
                    0.0, mPurchaseTranx.getTransactionDate(), null,
                    mPurchaseTranx.getId(), tranxType.getTransactionName(),
                    mDuties.getDutiesTaxes().getUnderPrefix(),
                    mPurchaseTranx.getFinancialYear(), mPurchaseTranx.getCreatedBy(),
                    mDuties.getDutiesTaxes().getId(), mPurchaseTranx.getVendorInvoiceNo());
        } catch (Exception e) {
            purInvoiceLogger.error("Exception->PurchaseInvoiceService(class)->insertFromDutiesTaxes(method) :" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertFromAdditionalCharges(TranxPurInvoiceAdditionalCharges mAdditinoalCharges,
                                            TranxPurInvoice mPurchaseTranx, TransactionTypeMaster tranxType) {
        try {
            transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                    mAdditinoalCharges.getAdditionalCharges().getPrinciples().getFoundations().getId(),
                    mAdditinoalCharges.getAdditionalCharges().getPrinciples().getId(),
                    mAdditinoalCharges.getAdditionalCharges().getPrincipleGroups() != null ?
                            mAdditinoalCharges.getAdditionalCharges().getPrincipleGroups().getId() : null,
                    mPurchaseTranx.getAssociateGroups() != null ?
                            mPurchaseTranx.getAssociateGroups().getId() : null,
                    tranxType.getId(), null,
                    mPurchaseTranx.getBranch() != null ? mPurchaseTranx.getBranch().getId() : null,
                    mPurchaseTranx.getOutlet().getId(), "pending", mAdditinoalCharges.getAmount() * -1,
                    0.0, mPurchaseTranx.getTransactionDate(), null,
                    mPurchaseTranx.getId(), tranxType.getTransactionName(),
                    mAdditinoalCharges.getAdditionalCharges().getUnderPrefix(),
                    mPurchaseTranx.getFinancialYear(), mPurchaseTranx.getCreatedBy(),
                    mAdditinoalCharges.getAdditionalCharges().getId(), mPurchaseTranx.getVendorInvoiceNo());
        } catch (Exception e) {
            purInvoiceLogger.error("Exception->PurchaseInvoiceService(class)->insertFromAdditionalCharges(method) :" + e.getMessage());
            e.printStackTrace();
        }
    }

    public Boolean validateSerialNumbers(String jsonStr) {
        Boolean status = false;
        InventorySerialNumberSummary inventorySerials = null;
        JsonParser parser = new JsonParser();
        JsonElement purDetailsJson = parser.parse(jsonStr);
        JsonArray array = purDetailsJson.getAsJsonArray();
        for (JsonElement mList : array) {
            JsonObject object = mList.getAsJsonObject();
            JsonArray jsonArray = object.getAsJsonArray("serialNo");
            for (JsonElement jsonElement : jsonArray) {
                JsonObject json = jsonElement.getAsJsonObject();
                String srno = json.get("no").getAsString();
                inventorySerials = inventorySerialNumberSummaryRepository.findBySerialNoAndTranxAction(
                        srno, "STOCKIN");
                if (inventorySerials != null) {
                    status = true;
                    break;
                }
            }
        }
        return status;
    }

    /******* save into  Purchase Invoice *******/

    public TranxPurInvoice saveIntoPurchaseInvoice(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        TranxPurInvoice mPurchaseTranx = null;
        TransactionTypeMaster tranxType = null;
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Branch branch = null;

        Outlet outlet = users.getOutlet();
        TranxPurInvoice invoiceTranx = new TranxPurInvoice();
        if (users.getBranch() != null) {
            branch = users.getBranch();
            invoiceTranx.setBranch(branch);
        }
        invoiceTranx.setOutlet(outlet);
        tranxType = tranxRepository.findByTransactionNameIgnoreCase("purchase");
        if (paramMap.containsKey("invoice_date")) {
            LocalDate date = LocalDate.parse(request.getParameter("invoice_date"));
            invoiceTranx.setInvoiceDate(date);
            /* fiscal year mapping */
            FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(date);
            if (fiscalYear != null) {
                invoiceTranx.setFinancialYear(fiscalYear.getFiscalYear());
                invoiceTranx.setFiscalYear(fiscalYear);
            }
        }
        /* End of fiscal year mapping */
        if (paramMap.containsKey("reference_po_ids"))
            invoiceTranx.setPoId(request.getParameter("reference_po_ids"));
        /*if (!request.getParameter("reference_po_ids").equalsIgnoreCase("")) {
            System.out.println("refernece Po Id:" + request.getParameter("reference_po_ids"));
            setClosePO(request.getParameter("reference_po_ids"));
        }
*/
        if (paramMap.containsKey("reference_pc_ids"))
            invoiceTranx.setPcId(request.getParameter("reference_pc_ids"));
       /* if (!request.getParameter("reference_pc_ids").equalsIgnoreCase("")) {
            System.out.println("refernece Pc Id:" + request.getParameter("reference_pc_ids"));
            setClosePC(request.getParameter("reference_pc_ids"));
        }*/
        if (paramMap.containsKey("invoice_no"))
            invoiceTranx.setVendorInvoiceNo(request.getParameter("invoice_no"));
        LedgerMaster purchaseAccount = ledgerMasterRepository.findByIdAndOutletIdAndStatus(Long.parseLong(
                request.getParameter("purchase_id")), users.getOutlet().getId(), true);
        invoiceTranx.setPurchaseAccountLedger(purchaseAccount);
        // Long discountLedgerId = Long.parseLong(request.getParameter("purchase_disc_ledger"));
        if (paramMap.containsKey("purchase_disc_ledger")) {
            LedgerMaster discountLedger = ledgerMasterRepository.findByIdAndOutletIdAndStatus(Long.parseLong(
                    request.getParameter("purchase_disc_ledger")), users.getOutlet().getId(), true);
            if (discountLedger != null) {
                invoiceTranx.setPurchaseDiscountLedger(discountLedger);
            }
        }
        LedgerMaster sundryCreditors = ledgerMasterRepository.findByIdAndOutletIdAndStatus(
                Long.parseLong(request.getParameter("supplier_code_id")),
                users.getOutlet().getId(), true);
        invoiceTranx.setSundryCreditors(sundryCreditors);
        LocalDate mDate = LocalDate.parse(request.getParameter("transaction_date"));
        invoiceTranx.setTransactionDate(mDate);
        invoiceTranx.setTotalBaseAmount(Double.parseDouble(request.getParameter("total_base_amt")));
        LedgerMaster roundoff = ledgerMasterRepository.findByOutletIdAndLedgerNameIgnoreCase(users.getOutlet().getId(), "Round off");
        invoiceTranx.setRoundOff(Double.parseDouble(request.getParameter("roundoff")));
        invoiceTranx.setPurchaseRoundOff(roundoff);
        invoiceTranx.setTotalAmount(Double.parseDouble(request.getParameter("totalamt")));
        invoiceTranx.setBalance(Double.parseDouble(request.getParameter("totalamt")));
        Boolean taxFlag = Boolean.parseBoolean(request.getParameter("taxFlag"));
        /* if true : cgst and sgst i.e intra state */
        if (taxFlag) {
            invoiceTranx.setTotalcgst(Double.parseDouble(request.getParameter("totalcgst")));
            invoiceTranx.setTotalsgst(Double.parseDouble(request.getParameter("totalsgst")));
            invoiceTranx.setTotaligst(0.0);
        }
        /* if false : igst i.e inter state */
        else {
            invoiceTranx.setTotalcgst(0.0);
            invoiceTranx.setTotalsgst(0.0);
            invoiceTranx.setTotaligst(Double.parseDouble(request.getParameter("totaligst")));
        }
        invoiceTranx.setTotalqty(Long.parseLong(request.getParameter("totalqty")));
        if (paramMap.containsKey("tcs"))
            invoiceTranx.setTcs(Double.parseDouble(request.getParameter("tcs")));
        invoiceTranx.setTaxableAmount(Double.parseDouble(request.getParameter("taxable_amount")));
        invoiceTranx.setPurchaseDiscountPer(Double.parseDouble(
                request.getParameter("purchase_discount")));
        invoiceTranx.setPurchaseDiscountAmount(Double.parseDouble(
                request.getParameter("purchase_discount_amt")));
        invoiceTranx.setTotalPurchaseDiscountAmt(Double.parseDouble(request.getParameter("total_purchase_discount_amt")));
        invoiceTranx.setSrno(Long.parseLong(request.getParameter("purchase_sr_no")));
        invoiceTranx.setCreatedBy(users.getId());
        invoiceTranx.setAdditionalChargesTotal(Double.parseDouble(request.getParameter(
                "additionalChargesTotal")));
        invoiceTranx.setStatus(true);
        invoiceTranx.setOperations("insert");
        if (paramMap.containsKey("narration"))
            invoiceTranx.setNarration(request.getParameter("narration"));
        else
            invoiceTranx.setNarration("NA");

        if (paramMap.containsKey("reference"))
            invoiceTranx.setReference(request.getParameter("reference"));
        else
            invoiceTranx.setReference("NA");

        if (paramMap.containsKey("transport"))
            invoiceTranx.setTransportName(request.getParameter("transport"));
        else
            invoiceTranx.setTransportName("NA");

        try {
            mPurchaseTranx = tranxPurInvoiceRepository.save(invoiceTranx);
            if (mPurchaseTranx != null) {
                /* adjust debit note bill against purchase invoice */
                if (Boolean.parseBoolean(request.getParameter("debitNoteReference"))) {
                    String jsonStr = request.getParameter("bills");
                    JsonParser parser = new JsonParser();
                    JsonElement debitNoteBills = parser.parse(jsonStr);
                    JsonArray debitNotes = debitNoteBills.getAsJsonArray();
                    Double totalBalance = 0.0;
                    for (JsonElement mBill : debitNotes) {
                        TranxDebitNoteNewReferenceMaster tranxDebitNoteNewReference = null;
                        JsonObject mDebitNote = mBill.getAsJsonObject();
                        totalBalance += mDebitNote.get("debitNotePaidAmt").getAsDouble();
                        tranxDebitNoteNewReference = tranxDebitNoteNewReferenceRepository.findByIdAndStatus(
                                mDebitNote.get("debitNoteId").getAsLong(), true);
                        TransactionStatus transactionStatus = transactionStatusRepository.findByStatusNameAndStatus(
                                "closed", true);
                        tranxDebitNoteNewReference.setTotalAmount(mDebitNote.get("debitNotePaidAmt").getAsDouble());
                        if (mDebitNote.get("debitNoteRemaningAmt").getAsDouble() == 0) {
                            tranxDebitNoteNewReference.setTransactionStatus(transactionStatus);
                        }
                        tranxDebitNoteNewReference.setBalance(mDebitNote.get("debitNoteRemaningAmt").getAsDouble());
                   /*     tranxDebitNoteNewReference.setAdjustedSource(mDebitNote.get("source").getAsString());
                        tranxDebitNoteNewReference.setAdjustedPurInvoiceId(mDebitNote.get("debitNoteId").getAsLong());*/
                        TranxDebitNoteNewReferenceMaster newReferenceMaster =
                                tranxDebitNoteNewReferenceRepository.save(tranxDebitNoteNewReference);
                        /* Adding int Debit Note Details */
                        TranxDebitNoteDetails mDetails = new TranxDebitNoteDetails();
                        mDetails.setBranch(newReferenceMaster.getBranch());
                        mDetails.setOutlet(newReferenceMaster.getOutlet());
                        mDetails.setSundryCreditor(newReferenceMaster.getSundryCreditor());
                        mDetails.setTotalAmount(newReferenceMaster.getTotalAmount());
                        mDetails.setPaidAmt(mDebitNote.get("debitNotePaidAmt").getAsDouble());
                        mDetails.setAdjustedId(mPurchaseTranx.getId());
                        mDetails.setAdjustedSource("purchase_invoice");
                        mDetails.setOperations("adjust");
                        mDetails.setTranxDebitNoteMaster(newReferenceMaster);
                        mDetails.setStatus(true);
                        mDetails.setAdjustmentStatus(newReferenceMaster.getAdjustmentStatus());
                        // immediate
                        tranxDebitNoteDetailsRepository.save(mDetails);
                    }
                    mPurchaseTranx.setBalance(mPurchaseTranx.getTotalAmount() - totalBalance);
                    try {
                        tranxPurInvoiceRepository.save(mPurchaseTranx);
                    } catch (Exception e) {
                        purInvoiceLogger.error("Error while creating adjustment of debit note bill against purchase invoice" + e.getMessage());
                    }
                }
                /* Save into Duties and Taxes */
                String taxStr = request.getParameter("taxCalculation");
                //  JsonObject duties_taxes = new JsonObject(taxStr);
                JsonObject duties_taxes = new Gson().fromJson(taxStr, JsonObject.class);
                saveIntoPurchaseDutiesTaxes(duties_taxes, mPurchaseTranx, taxFlag);
                /* save into Additional Charges  */
                String strJson = request.getParameter("additionalCharges");
                JsonParser parser = new JsonParser();
                JsonElement tradeElement = parser.parse(strJson);
                JsonArray additionalCharges = tradeElement.getAsJsonArray();
                saveIntoPurchaseAdditionalCharges(additionalCharges, mPurchaseTranx, tranxType.getId(),
                        users.getOutlet().getId());
                /* save into Purchase Invoice Details */
                String jsonStr = request.getParameter("row");
                JsonElement purDetailsJson = parser.parse(jsonStr);
                JsonArray array = purDetailsJson.getAsJsonArray();
                String referenceObject = request.getParameter("refObject");
                saveIntoPurchaseInvoiceDetails(array, mPurchaseTranx,
                        branch, outlet, users.getId(), tranxType, referenceObject);
            }
        } catch (DataIntegrityViolationException e) {
            purInvoiceLogger.error("Error in saveIntoPurchaseInvoice" + e.getMessage());
            System.out.println("Exception:" + e.getMessage());

        } catch (Exception e1) {
            purInvoiceLogger.error("Error in saveIntoPurchaseInvoice" + e1.getMessage());
            System.out.println("Exception:" + e1.getMessage());
        }
        return mPurchaseTranx;
    }

    public void setClosePO(String poIds) {
        Boolean flag = false;
        String[] idList;
        idList = poIds.split(",");
        for (String mId : idList) {
            TranxPurOrder tranxPurOrder = tranxPurOrderRepository.findByIdAndStatus(Long.parseLong(mId), true);
            if (tranxPurOrder != null) {
                tranxPurOrder.setStatus(false);
                tranxPurOrderRepository.save(tranxPurOrder);
            }
        }
    }

    public void setClosePC(String poIds) {
        Boolean flag = false;
        String[] idList;
        idList = poIds.split(",");
        for (String mId : idList) {
            TranxPurChallan tranxPurChallan = tranxPurChallanRepository.findByIdAndStatus(Long.parseLong(mId), true);
            if (tranxPurChallan != null) {
                tranxPurChallan.setStatus(false);
                tranxPurChallanRepository.save(tranxPurChallan);
            }
        }
    }
    /* End of creation of Purchase Invoice */

    /****** Save into Duties and Taxes ******/
    public void saveIntoPurchaseDutiesTaxes(JsonObject duties_taxes, TranxPurInvoice mPurchaseTranx,
                                            Boolean taxFlag) {
        List<TranxPurInvoiceDutiesTaxes> purchaseDutiesTaxes = new ArrayList<>();
        if (taxFlag) {
            JsonArray cgstList = duties_taxes.getAsJsonArray("cgst");
            //JsonArray cgstList = null;
            JsonArray sgstList = duties_taxes.getAsJsonArray("sgst");
            /* this is for Cgst creation */
            if (cgstList.size() > 0) {
                for (JsonElement mList : cgstList) {
                    TranxPurInvoiceDutiesTaxes taxes = new TranxPurInvoiceDutiesTaxes();
                    JsonObject cgstObject = mList.getAsJsonObject();
                    //   JsonObject cgstObject = cgstList.getJSONObject(i);
                    LedgerMaster dutiesTaxes = null;
                    String inputGst = cgstObject.get("gst").getAsString();
                    String ledgerName = "INPUT CGST " + inputGst;
                    dutiesTaxes = ledgerMasterRepository.findByOutletIdAndLedgerNameIgnoreCase(
                            mPurchaseTranx.getOutlet().getId(), ledgerName);
                    if (dutiesTaxes != null) {
                        //   dutiesTaxesLedger.setDutiesTaxes(dutiesTaxes);
                        taxes.setDutiesTaxes(dutiesTaxes);
                    }
                    taxes.setAmount(Double.parseDouble(cgstObject.get("amt").getAsString()));
                    taxes.setPurchaseTransaction(mPurchaseTranx);
                    taxes.setSundryCreditors(mPurchaseTranx.getSundryCreditors());
                    taxes.setIntra(taxFlag);
                    taxes.setStatus(true);
                    purchaseDutiesTaxes.add(taxes);
                }
            }
            /* this is for Sgst creation */
            if (sgstList.size() > 0) {
                for (JsonElement mList : sgstList) {
                    TranxPurInvoiceDutiesTaxes taxes = new TranxPurInvoiceDutiesTaxes();
                    JsonObject sgstObject = mList.getAsJsonObject();
                    LedgerMaster dutiesTaxes = null;
                    String inputGst = sgstObject.get("gst").getAsString();
                    String ledgerName = "INPUT SGST " + inputGst;
                    dutiesTaxes = ledgerMasterRepository.findByOutletIdAndLedgerNameIgnoreCase(
                            mPurchaseTranx.getOutlet().getId(), ledgerName);
                    if (dutiesTaxes != null) {
                        taxes.setDutiesTaxes(dutiesTaxes);
                    }
                    taxes.setAmount(Double.parseDouble(sgstObject.get("amt").getAsString()));
                    taxes.setPurchaseTransaction(mPurchaseTranx);
                    taxes.setSundryCreditors(mPurchaseTranx.getSundryCreditors());
                    taxes.setIntra(taxFlag);
                    taxes.setStatus(true);
                    purchaseDutiesTaxes.add(taxes);
                }
            }
        } else {
            JsonArray igstList = duties_taxes.getAsJsonArray("igst");
            /* this is for Igst creation */
            if (igstList.size() > 0) {
                for (JsonElement mList : igstList) {
                    TranxPurInvoiceDutiesTaxes taxes = new TranxPurInvoiceDutiesTaxes();
                    JsonObject igstObject = mList.getAsJsonObject();
                    LedgerMaster dutiesTaxes = null;
                    String inputGst = igstObject.get("gst").getAsString();
                    String ledgerName = "INPUT IGST " + inputGst;
                    dutiesTaxes = ledgerMasterRepository.findByOutletIdAndLedgerNameIgnoreCase(
                            mPurchaseTranx.getOutlet().getId(), ledgerName);
                    if (dutiesTaxes != null) {
                        taxes.setDutiesTaxes(dutiesTaxes);
                    }
                    taxes.setAmount(Double.parseDouble(igstObject.get("amt").getAsString()));
                    taxes.setPurchaseTransaction(mPurchaseTranx);
                    taxes.setSundryCreditors(mPurchaseTranx.getSundryCreditors());
                    taxes.setIntra(taxFlag);
                    taxes.setStatus(true);
                    purchaseDutiesTaxes.add(taxes);
                }
            }
        }
        try {
            /* save all Duties and Taxes into purchase Invoice Duties taxes table */
            purInvoiceDutiesTaxesRepository.saveAll(purchaseDutiesTaxes);
        } catch (DataIntegrityViolationException e1) {
            System.out.println(e1.getMessage());
            e1.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    /* End of Purchase Duties and Taxes Ledger */

    /****    Save Into Purchase Additional Charges    *****/
    public void saveIntoPurchaseAdditionalCharges(JsonArray additionalCharges,
                                                  TranxPurInvoice mPurchaseTranx, Long tranxId, Long outletId) {
        List<TranxPurInvoiceAdditionalCharges> chargesList = new ArrayList<>();
        if (mPurchaseTranx.getAdditionalChargesTotal() > 0) {
            for (JsonElement mList : additionalCharges) {
                TranxPurInvoiceAdditionalCharges charges = new TranxPurInvoiceAdditionalCharges();
                JsonObject object = mList.getAsJsonObject();
                Double amount = object.get("amt").getAsDouble();
                Long ledgerId = object.get("ledger").getAsLong();
                LedgerMaster addcharges = ledgerMasterRepository.findByIdAndOutletIdAndStatus(ledgerId, outletId, true);
                charges.setAmount(amount);
                charges.setAdditionalCharges(addcharges);
                charges.setPurchaseTransaction(mPurchaseTranx);
                charges.setStatus(true);
                charges.setOperation("inserted");
                charges.setCreatedBy(mPurchaseTranx.getCreatedBy());
                chargesList.add(charges);

            }
        }
        try {
            purInvoiceAdditionalChargesRepository.saveAll(chargesList);
        } catch (DataIntegrityViolationException e1) {
            System.out.println(e1.getMessage());
            e1.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    /* End of Purchase Additional Charges */

    /****** save into Purchase Invoice Details ******/
    public void saveIntoPurchaseInvoiceDetails(JsonArray array, TranxPurInvoice mPurchaseTranx,
                                               Branch branch, Outlet outlet,
                                               Long userId, TransactionTypeMaster tranxType, String referenceObject) {
        /* Purchase Product Details Start here */
        // Map<String, String[]> paramMap = request.getParameterMap();
        try {
            List<TranxPurInvoiceDetails> row = new ArrayList<>();
            List<TranxPurchaseInvoiceProductSrNumber> newSerialNumbers = new ArrayList<>();
            for (JsonElement mList : array) {
                JsonObject object = mList.getAsJsonObject();
                TranxPurInvoiceDetails mDetails = new TranxPurInvoiceDetails();
                mDetails.setPurchaseTransaction(mPurchaseTranx);
                mDetails.setCreatedBy(mPurchaseTranx.getCreatedBy());
                Product mProduct = productRepository.findByIdAndStatus(object.get("product_id").getAsLong(),
                        true);
                mDetails.setProduct(mProduct);
                if (!object.get("reference_id").equals("")) {
                    mDetails.setReferenceId(object.get("reference_id").getAsString());
                    mDetails.setReferenceType(object.get("reference_type").getAsString());
                }
                mDetails.setBase_amt(object.get("base_amt").getAsDouble());
                mDetails.setDiscountAmount(object.get("dis_amt").getAsDouble());
                mDetails.setDiscountPer(object.get("dis_per").getAsDouble());
                mDetails.setDiscountPerCal(object.get("dis_per_cal").getAsDouble());
                mDetails.setDiscountAmountCal(object.get("dis_amt_cal").getAsDouble());
                mDetails.setTotalAmount(object.get("total_amt").getAsDouble());
                mDetails.setIgst(object.get("igst").getAsDouble());
                mDetails.setSgst(object.get("sgst").getAsDouble());
                mDetails.setCgst(object.get("cgst").getAsDouble());
                mDetails.setTotalIgst(object.get("total_igst").getAsDouble());
                mDetails.setTotalSgst(object.get("total_sgst").getAsDouble());
                mDetails.setTotalCgst(object.get("total_cgst").getAsDouble());
                mDetails.setFinalAmount(object.get("final_amt").getAsDouble());
                if (object.has("qtyH"))
                    mDetails.setQtyHigh(object.get("qtyH").getAsDouble());
                if (object.has("rateH"))
                    mDetails.setRateHigh(object.get("rateH").getAsDouble());
                mDetails.setStatus(true);
                if (object.has("qtyM"))
                    mDetails.setQtyMedium(object.get("qtyM").getAsDouble());
                if (object.has("rateM"))
                    mDetails.setRateMedium(object.get("rateM").getAsDouble());
                if (object.has("qtyL"))
                    mDetails.setQtyLow(object.get("qtyL").getAsDouble());
                if (object.has("rateL"))
                    mDetails.setRateLow(object.get("rateL").getAsDouble());
                if (object.has("base_amt_H"))
                    mDetails.setBaseAmtHigh(object.get("base_amt_H").getAsDouble());
                if (object.has("base_amt_M"))
                    mDetails.setBaseAmtMedium(object.get("base_amt_M").getAsDouble());
                if (object.has("base_amt_L"))
                    mDetails.setBaseAmtLow(object.get("base_amt_L").getAsDouble());
                if (!object.get("packageId").getAsString().equalsIgnoreCase("")) {
                    PackingMaster packingMaster = packingMasterRepository.findByIdAndStatus(object.get("packageId").getAsLong(), true);
                    if (packingMaster != null)
                        mDetails.setPackingMaster(packingMaster);
                }
                row.add(mDetails);
                TranxPurInvoiceDetails newDetails = invoiceDetailsRepository.save(mDetails);
                /* inserting into TranxPurchaseInvoiceDetailsUnits */
                JsonArray unitDetails = object.getAsJsonArray("units");
                for (JsonElement mUnits : unitDetails) {
                    JsonObject mObject = mUnits.getAsJsonObject();
                    TranxPurInvoiceDetailsUnits invoiceUnits = new TranxPurInvoiceDetailsUnits();
                    invoiceUnits.setPurchaseTransaction(newDetails.getPurchaseTransaction());
                    invoiceUnits.setPurInvoiceDetails(newDetails);
                    invoiceUnits.setProduct(newDetails.getProduct());
                    Units unit = unitsRepository.findByIdAndStatus(mObject.get("unit_id").getAsLong(), true);
                    invoiceUnits.setUnits(unit);
                    invoiceUnits.setUnitConversions(mObject.get("unit_conv").getAsDouble());
                    invoiceUnits.setQty(mObject.get("qty").getAsDouble());
                    invoiceUnits.setRate(mObject.get("rate").getAsDouble());
                    invoiceUnits.setBaseAmt(mObject.get("base_amt").getAsDouble());
                    invoiceUnits.setStatus(true);
                    invoiceUnits.setCreatedBy(newDetails.getCreatedBy());
                    tranxPurInvoiceUnitsRepository.save(invoiceUnits);
                }
                /* End of inserting into TranxPurchaseInvoiceDetailsUnits */

                /* closing of purchase orders while converting into purchase invoice using its qnt */
                TranxPurOrder tranxPurOrder = null;
                JsonArray referenceArray = new JsonArray();
                JsonParser parser = new JsonParser();
                if (referenceObject != null) {
                    JsonElement purDetailsJson = parser.parse(referenceObject);
                    referenceArray = purDetailsJson.getAsJsonArray();
                }
                if (object.get("reference_type").getAsString().equalsIgnoreCase("purchase order")) {
                    if (referenceArray != null && referenceArray.size() != 0) {
                        for (JsonElement jsonElement : referenceArray) {
                            Long referenceId = jsonElement.getAsJsonObject().get("refId").getAsLong();
                            JsonArray prdList = jsonElement.getAsJsonObject().getAsJsonArray("prdList");
                            boolean flag_status = false;
                            for (JsonElement mPrdList : prdList) {
                                Long prdId = mPrdList.getAsJsonObject().get("product_id").getAsLong();
                                double qty = mPrdList.getAsJsonObject().get("qtyH").getAsDouble();
                                TranxPurOrderDetails orderDetails = tranxPurOrderDetailsRepository.
                                        findByTranxPurOrderIdAndProductIdAndStatus(referenceId, prdId, true);
                                if (orderDetails != null) {
                                    if (qty != orderDetails.getQtyHigh().doubleValue()) {
                                        flag_status = true;
                                        orderDetails.setQtyHigh(qty);//push data into History table before update(reminding)
                                        tranxPurOrderDetailsRepository.save(orderDetails);
                                    }
                                }
                            }
                            tranxPurOrder = tranxPurOrderRepository.findByIdAndStatus(
                                    referenceId, true);
                            if (tranxPurOrder != null) {
                                if (flag_status) {
                                    TransactionStatus transactionStatus = transactionStatusRepository.
                                            findByStatusNameAndStatus("opened", true);
                                    tranxPurOrder.setTransactionStatus(transactionStatus);
                                    tranxPurOrderRepository.save(tranxPurOrder);
                                } else {
                                    TransactionStatus transactionStatus = transactionStatusRepository.
                                            findByStatusNameAndStatus("closed", true);
                                    tranxPurOrder.setTransactionStatus(transactionStatus);
                                    tranxPurOrderRepository.save(tranxPurOrder);
                                }
                            }
                        }
                    }
                }
            /* End of  closing of purchase orders while converting into purchase invoice
            using its qnt */

                /* closing of purchase challan while converting into purchase invoice using its qnt */
                else {
                    TranxPurChallan tranxPurChallan = null;
                    if (referenceArray != null && referenceArray.size() != 0) {
                        for (JsonElement jsonElement : referenceArray) {
                            Long referenceId = jsonElement.getAsJsonObject().get("refId").getAsLong();
                            JsonArray prdList = jsonElement.getAsJsonObject().getAsJsonArray("prdList");
                            boolean flag_status = false;
                            for (JsonElement mPrdList : prdList) {
                                Long prdId = mPrdList.getAsJsonObject().get("product_id").getAsLong();
                                double qty = mPrdList.getAsJsonObject().get("qtyH").getAsDouble();
                                TranxPurChallanDetails challanDetails = tranxPurChallanDetailsRepository.
                                        findByTranxPurChallanIdAndProductIdAndStatus(referenceId, prdId, true);
                                if (challanDetails != null) {
                                    if (qty != challanDetails.getQtyHigh().doubleValue()) {
                                        flag_status = true;
                                        challanDetails.setQtyHigh(qty);//push data into History table before update(reminding)
                                        tranxPurChallanDetailsRepository.save(challanDetails);
                                    }
                                }
                            }
                            tranxPurChallan = tranxPurChallanRepository.findByIdAndStatus(
                                    referenceId, true);
                            if (tranxPurChallan != null) {
                                if (flag_status) {
                                    TransactionStatus transactionStatus = transactionStatusRepository.
                                            findByStatusNameAndStatus("opened", true);
                                    tranxPurChallan.setTransactionStatus(transactionStatus);
                                    tranxPurChallanRepository.save(tranxPurChallan);
                                } else {
                                    TransactionStatus transactionStatus = transactionStatusRepository.
                                            findByStatusNameAndStatus("closed", true);
                                    tranxPurChallan.setTransactionStatus(transactionStatus);
                                    tranxPurChallanRepository.save(tranxPurChallan);
                                }
                            }
                        }
                    }
                }
            /* End of  closing of purchase challan while converting into purchase invoice
            using its qnt */
                JsonArray jsonArray = object.getAsJsonArray("serialNo");
                if (jsonArray != null && jsonArray.size() > 0) {
                    List<TranxPurchaseInvoiceProductSrNumber> serialNumbers = new ArrayList<>();
                    for (JsonElement jsonElement : jsonArray) {
                        JsonObject json = jsonElement.getAsJsonObject();
                        TranxPurchaseInvoiceProductSrNumber productSerialNumber = new TranxPurchaseInvoiceProductSrNumber();
                        productSerialNumber.setProduct(mProduct);
                        productSerialNumber.setSerialNo(json.get("no").getAsString());
                        productSerialNumber.setPurchaseTransaction(mPurchaseTranx);
                        productSerialNumber.setTransactionStatus("purchase");
                        productSerialNumber.setStatus(true);
                        productSerialNumber.setCreatedBy(userId);
                        productSerialNumber.setOperations("inserted");
                        productSerialNumber.setCreatedBy(mPurchaseTranx.getCreatedBy());
                        productSerialNumber.setTransactionTypeMaster(tranxType);
                        productSerialNumber.setBranch(mPurchaseTranx.getBranch());
                        productSerialNumber.setOutlet(mPurchaseTranx.getOutlet());
                        productSerialNumber.setTransactionTypeMaster(tranxType);
                        serialNumbers.add(productSerialNumber);
                    }

                    newSerialNumbers = serialNumberRepository.saveAll(serialNumbers);

                }
                /*inventoryDTO.insertIntoInventoryTranxDetails(newDetails.getProduct(),
                        newDetails.getPurchaseTransaction().getTransactionDate(),
                        tranxType.getTransactionName(), newDetails.getQtyHigh(), newDetails.getQtyMedium(),
                        newDetails.getQtyLow(), "STOCKIN", newDetails.getPurchaseTransaction().getId(),
                        newDetails.getPurchaseTransaction().getFinancialYear(),
                        newDetails.getPurchaseTransaction().getInvoiceDate(),
                        newDetails.getPurchaseTransaction().getBranch(), newDetails.getPurchaseTransaction().getOutlet());
                if (newSerialNumbers.size() > 0) {
                    inventoryDTO.saveIntoSerialTranxSummaryDetails(
                            newSerialNumbers, tranxType.getTransactionName());
                }*/
            }
        } catch (Exception e) {
            purInvoiceLogger.error("Exception in DetailsL:" + e.getMessage());
        }
    }
    /* End of Purchase Invoice Details */

    public JsonObject purchaseLastRecord(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Long count = tranxPurInvoiceRepository.findLastRecord(users.getOutlet().getId());
        String serailNo = String.format("%05d", count + 1);// 5 digit serial number
       /* String companyName = users.getOutlet().getCompanyName();
        companyName = companyName.substring(0, 3);*/ // fetching first 3 digits from company names
        /* getting Start and End year from fiscal Year */
        String startYear = generateFiscalYear.getStartYear();
        String endYear = generateFiscalYear.getEndYear();
        //first 3 digits of Current month
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
       /* String piCode = companyName.toUpperCase() + "-" + startYear + endYear
                + "-" + "PI" + currentMonth + "-" + serailNo;*/
        String piCode = "PI" + currentMonth + serailNo;
        JsonObject result = new JsonObject();
        result.addProperty("message", "success");
        result.addProperty("responseStatus", HttpStatus.OK.value());
        result.addProperty("count", count + 1);
        result.addProperty("serialNo", piCode);
        return result;
    }

    /* find all purchase invoices outletwise */
    public JsonObject purchaseList(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();

        List<TranxPurInvoice> purInvoice = tranxPurInvoiceRepository.findByOutletIdAndStatusOrderByIdDesc(
                users.getOutlet().getId(), true);
        for (TranxPurInvoice invoices : purInvoice) {
            JsonObject response = new JsonObject();
            response.addProperty("id", invoices.getId());
            response.addProperty("invoice_no", invoices.getVendorInvoiceNo());
            response.addProperty("invoice_date", invoices.getInvoiceDate().toString());
            response.addProperty("transaction_date", invoices.getTransactionDate().toString());
            response.addProperty("purchase_serial_number", invoices.getSrno());
            response.addProperty("total_amount", invoices.getTotalAmount());
            response.addProperty("sundry_creditor_name",
                    invoices.getSundryCreditors().getLedgerName());
            response.addProperty("sundry_creditor_id", invoices.getSundryCreditors().getId());
            response.addProperty("purchase_account_name",
                    invoices.getPurchaseAccountLedger().getLedgerName());
            result.add(response);
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);
        return output;
    }

    public JsonObject getPurchaseInvoiceById(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<TranxPurInvoiceDetails> list = new ArrayList<>();
        JsonArray units = new JsonArray();
        List<TranxPurchaseInvoiceProductSrNumber> serialNumbers = new ArrayList<>();
        List<TranxPurInvoiceAdditionalCharges> additionalCharges = new ArrayList<>();
        JsonObject finalResult = new JsonObject();
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            TranxPurInvoice purchaseInvoice = tranxPurInvoiceRepository.findByIdAndOutletIdAndStatus(
                    id, users.getOutlet().getId(), true);
            list = invoiceDetailsRepository.findByPurchaseTransactionIdAndStatus(id, true);
            serialNumbers = serialNumberRepository.findByPurchaseTransactionId(purchaseInvoice.getId());
            additionalCharges = purInvoiceAdditionalChargesRepository.findByPurchaseTransactionIdAndStatus(
                    purchaseInvoice.getId(), true);
            finalResult.addProperty("tcs", purchaseInvoice.getTcs());
            finalResult.addProperty("narration", purchaseInvoice.getNarration() != null ? purchaseInvoice.getNarration() : "");
            finalResult.addProperty("discountLedgerId", purchaseInvoice.getPurchaseDiscountLedger() != null ?
                    purchaseInvoice.getPurchaseDiscountLedger().getId() : 0);
            finalResult.addProperty("discountInAmt", purchaseInvoice.getPurchaseDiscountAmount());
            finalResult.addProperty("discountInPer", purchaseInvoice.getPurchaseDiscountPer());
            JsonObject result = new JsonObject();
            /* Purchase Invoice Data */
            result.addProperty("id", purchaseInvoice.getId());
            result.addProperty("invoice_dt", purchaseInvoice.getInvoiceDate().toString());
            result.addProperty("invoice_no", purchaseInvoice.getVendorInvoiceNo());
            result.addProperty("purchase_sr_no", purchaseInvoice.getSrno());
            result.addProperty("purchase_account_ledger_id", purchaseInvoice.getPurchaseAccountLedger().getId());
            result.addProperty("supplierId", purchaseInvoice.getSundryCreditors().getId());
            result.addProperty("transaction_dt", purchaseInvoice.getTransactionDate().toString());
            result.addProperty("additional_charges_total", purchaseInvoice.getAdditionalChargesTotal());
            /* End of Purchase Invoice Data */

            /* Purchase Invoice Details */
            JsonArray row = new JsonArray();
            if (list.size() > 0) {
                for (TranxPurInvoiceDetails mDetails : list) {
                    JsonObject prDetails = new JsonObject();
                    prDetails.addProperty("details_id", mDetails.getId());
                    prDetails.addProperty("product_id", mDetails.getProduct().getId());
                    prDetails.addProperty("qtyH", mDetails.getQtyHigh());
                    prDetails.addProperty("qtyM", mDetails.getQtyMedium());
                    prDetails.addProperty("qtyL", mDetails.getQtyLow());
                    prDetails.addProperty("rateH", mDetails.getRateHigh());
                    prDetails.addProperty("rateM", mDetails.getRateMedium());
                    prDetails.addProperty("rateL", mDetails.getRateLow());
                    prDetails.addProperty("base_amt_H", mDetails.getBaseAmtHigh());
                    prDetails.addProperty("base_amt_M", mDetails.getBaseAmtMedium());
                    prDetails.addProperty("base_amt_L", mDetails.getBaseAmtLow());
                    prDetails.addProperty("base_amt", mDetails.getBase_amt());
                    prDetails.addProperty("dis_amt", mDetails.getDiscountAmount());
                    prDetails.addProperty("dis_amt_cal", mDetails.getDiscountAmountCal());
                    prDetails.addProperty("dis_per", mDetails.getDiscountPer());
                    prDetails.addProperty("dis_per_cal", mDetails.getDiscountPerCal());
                    prDetails.addProperty("reference_id", mDetails.getReferenceId());
                    prDetails.addProperty("reference_type", mDetails.getReferenceType());
                    prDetails.addProperty("igst", mDetails.getIgst());
                    prDetails.addProperty("package_id", mDetails.getPackingMaster() != null
                            ? mDetails.getPackingMaster().getId().toString() : "");
                    JsonArray serialNo = new JsonArray();
                    if (serialNumbers.size() > 0) {
                        for (TranxPurchaseInvoiceProductSrNumber mProductSerials : serialNumbers) {
                            JsonObject jsonSerailNo = new JsonObject();
                            jsonSerailNo.addProperty("product_id", mDetails.getProduct().getId());
                            jsonSerailNo.addProperty("serial_no", mProductSerials.getSerialNo());
                            serialNo.add(jsonSerailNo);
                        }
                        prDetails.add("serialNo", serialNo);
                    }
                    /* getting Units of Purcase Invoice*/
                    List<TranxPurInvoiceDetailsUnits> unitDetails = tranxPurInvoiceUnitsRepository.findByPurInvoiceDetailsIdAndStatus(
                            mDetails.getId(), true);
                    unitDetails.forEach(mUnit -> {
                        JsonObject mObject = new JsonObject();
                        mObject.addProperty("qty", mUnit.getQty());
                        mObject.addProperty("rate", mUnit.getRate());
                        mObject.addProperty("base_amt", mUnit.getBaseAmt());
                        mObject.addProperty("unit_conv", mUnit.getUnitConversions());
                        mObject.addProperty("unit_id", mUnit.getUnits().getId());
                        units.add(mObject);
                    });
                    prDetails.add("units", units);
                    row.add(prDetails);


                }
            }
            System.out.println("Row  " + row);
            /* End of Purchase Invoice Details */

            /* Purchase Additional Charges */
            JsonArray jsonAdditionalList = new JsonArray();
            if (additionalCharges.size() > 0) {
                for (TranxPurInvoiceAdditionalCharges mAdditionalCharges : additionalCharges) {
                    JsonObject json_charges = new JsonObject();
                    json_charges.addProperty("additional_charges_details_id", mAdditionalCharges.getId());
                    json_charges.addProperty("ledger_id", mAdditionalCharges.getAdditionalCharges() != null ?
                            mAdditionalCharges.getAdditionalCharges().getId() : 0);
                    json_charges.addProperty("amt", mAdditionalCharges.getAmount());
                    jsonAdditionalList.add(json_charges);
                }
            }
            /* End of Purchase Additional Charges */
            finalResult.addProperty("message", "success");
            finalResult.addProperty("responseStatus", HttpStatus.OK.value());
            finalResult.add("invoice_data", result);
            finalResult.add("row", row);
            finalResult.add("additional_charges", jsonAdditionalList);

        } catch (DataIntegrityViolationException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            finalResult.addProperty("message", "error");
            finalResult.addProperty("responseStatus", HttpStatus.CONFLICT.value());
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
            e1.printStackTrace();
            finalResult.addProperty("message", "error");
            finalResult.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
        }
        return finalResult;
    }

    public Object editPurchaseInvoice(HttpServletRequest request) {
        return saveIntoPurchaseInvoiceEdit(request, "edit");
    }

    private ResponseMessage saveIntoPurchaseInvoiceEdit(HttpServletRequest request, String key) {
        ResponseMessage responseMessage = new ResponseMessage();
        TransactionTypeMaster tranxType = null;
        TranxPurInvoice invoiceTranx = null;
        LedgerMaster discountLedger = null;
        LedgerMaster sundryCreditors = null;
        LedgerMaster purchaseAccount = null;
        LedgerMaster roundoff = null;
        TranxPurInvoice mPurchaseTranx = null;

        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        tranxType = tranxRepository.findByTransactionNameIgnoreCase("purchase");
        invoiceTranx = tranxPurInvoiceRepository.findByIdAndOutletIdAndStatus(Long.parseLong(
                request.getParameter("id")), users.getOutlet().getId(), true);
        dbList = transactionDetailsRepository.findByTransactionId(invoiceTranx.getId(), tranxType.getId());
        invoiceTranx.setOperations("updated");
        insertIntoPurchaseInvoiceHistory(invoiceTranx);
        Boolean taxFlag;
        LocalDate date = LocalDate.parse(request.getParameter("invoice_dt"));
        invoiceTranx.setInvoiceDate(date);
        /* fiscal year mapping */
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(date);
        if (fiscalYear != null) {
            invoiceTranx.setFinancialYear(fiscalYear.getFiscalYear());
        }
        /* End of fiscal year mapping */
        invoiceTranx.setVendorInvoiceNo(request.getParameter("invoice_no"));
        purchaseAccount = ledgerMasterRepository.findByIdAndOutletIdAndStatus(Long.parseLong(
                request.getParameter("purchase_id")), users.getOutlet().getId(), true);

        /* calling store procedure for updating opening and closing  of Purchase Account  */
        if (purchaseAccount.getId() != null && invoiceTranx.getId() != null && tranxType.getId() != null) {
            Boolean isContains = dbList.contains(purchaseAccount.getId());
            mInputList.add(purchaseAccount.getId());
            if (isContains) {
                /* edit ledger tranx if same ledger is modified */
                transactionDetailsRepository.ledgerPostingEdit(purchaseAccount.getId(),
                        invoiceTranx.getId(), tranxType.getId(), "DR", Double.parseDouble(
                                request.getParameter("total_base_amt")) * -1);
            } else {
                /* insert ledger tranx if ledger is changed */
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(purchaseAccount.getFoundations().getId(),
                        purchaseAccount.getPrinciples().getId(),
                        purchaseAccount.getPrincipleGroups() != null ?
                                purchaseAccount.getPrincipleGroups().getId() : null,
                        invoiceTranx.getAssociateGroups() != null ?
                                invoiceTranx.getAssociateGroups().getId() : null, tranxType.getId(), null,
                        invoiceTranx.getBranch() != null ? invoiceTranx.getBranch().getId() : null,
                        invoiceTranx.getOutlet().getId(), "pending",
                        Double.parseDouble(
                                request.getParameter("total_base_amt")) * -1, 0.0,
                        invoiceTranx.getTransactionDate(), null, invoiceTranx.getId(),
                        tranxType.getTransactionName(),
                        purchaseAccount.getUnderPrefix(),
                        invoiceTranx.getFinancialYear(), invoiceTranx.getCreatedBy(),
                        purchaseAccount.getId(), invoiceTranx.getVendorInvoiceNo());

            }
        }/* end of calling store procedure for Purchase Account updating opening and closing */

        Long discountLedgerId = Long.parseLong(request.getParameter("purchase_disc_ledger"));
        if (discountLedgerId > 0) {
            discountLedger = ledgerMasterRepository.findByIdAndOutletIdAndStatus(Long.parseLong(
                    request.getParameter("purchase_disc_ledger")), users.getOutlet().getId(), true);
            if (discountLedger != null) {
                invoiceTranx.setPurchaseDiscountLedger(discountLedger);

                /* calling store procedure for updating opening and closing of Purchase Discount  */
                if (discountLedger.getId() != null && invoiceTranx.getId() != null
                        && tranxType.getId() != null) {
                    Boolean isContains = dbList.contains(discountLedger.getId());
                    mInputList.add(discountLedger.getId());
                    if (isContains) {
                        transactionDetailsRepository.ledgerPostingEdit(discountLedger.getId(),
                                invoiceTranx.getId(),
                                tranxType.getId(), "CR", Double.parseDouble(
                                        request.getParameter("total_purchase_discount_amt")));
                    } else {
                        /* insert ledger tranx if ledger is changed */
                        transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                                discountLedger.getPrinciples().getFoundations().getId(),
                                discountLedger.getPrinciples().getId(),
                                discountLedger.getPrincipleGroups() != null ?
                                        discountLedger.getPrincipleGroups().getId() : null,
                                invoiceTranx.getAssociateGroups() != null ?
                                        invoiceTranx.getAssociateGroups().getId() : null, tranxType.getId(), null,
                                invoiceTranx.getBranch() != null ? invoiceTranx.getBranch().getId() : null,
                                invoiceTranx.getOutlet().getId(), "pending", 0.0, Double.parseDouble(
                                        request.getParameter("total_purchase_discount_amt")),
                                invoiceTranx.getTransactionDate(), null, invoiceTranx.getId(), tranxType.getTransactionName(),
                                discountLedger.getUnderPrefix(), invoiceTranx.getFinancialYear(),
                                invoiceTranx.getCreatedBy(), discountLedger.getId(), mPurchaseTranx.getVendorInvoiceNo());
                    }
                }
                /* end of calling store procedure for updating opening and closing of Purchase Discount */
            }
        }
        sundryCreditors = ledgerMasterRepository.findByIdAndOutletIdAndStatus(
                Long.parseLong(request.getParameter("supplier_code_id")),
                users.getOutlet().getId(), true);
        /* calling store procedure for updating opening and closing of Sundry Creditors  */
        if (sundryCreditors.getId() != null && invoiceTranx.getId() != null && tranxType.getId() != null) {

            Boolean isContains = dbList.contains(sundryCreditors.getId());
            mInputList.add(sundryCreditors.getId());
            if (isContains) {
                transactionDetailsRepository.ledgerPostingEdit(sundryCreditors.getId(), invoiceTranx.getId(),
                        tranxType.getId(), "CR", Double.parseDouble(
                                request.getParameter("totalamt")));
            } else {
                /* insert ledger tranx if ledger is changed */
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(sundryCreditors.
                                getFoundations().getId(), sundryCreditors.getPrinciples().getId(),
                        sundryCreditors.getPrincipleGroups().getId(),
                        invoiceTranx.getAssociateGroups() != null ? invoiceTranx.getAssociateGroups().getId() : null,
                        tranxType.getId(), sundryCreditors.getBalancingMethod().getId(),
                        invoiceTranx.getBranch() != null ? invoiceTranx.getBranch().getId() : null,
                        invoiceTranx.getOutlet().getId(), "pending", 0.0, Double.parseDouble(
                                request.getParameter("totalamt")),
                        invoiceTranx.getTransactionDate(), null, invoiceTranx.getId(), tranxType.getTransactionName(),
                        sundryCreditors.getUnderPrefix(), invoiceTranx.getFinancialYear(),
                        invoiceTranx.getCreatedBy(), sundryCreditors.getId(), invoiceTranx.getVendorInvoiceNo());
            }
        }  /* end of calling store procedure for updating opening and closing of Sundry Creditors  */

        invoiceTranx.setPurchaseAccountLedger(purchaseAccount);
        invoiceTranx.setSundryCreditors(sundryCreditors);
        LocalDate mDate = LocalDate.parse(request.getParameter("transaction_dt"));
        invoiceTranx.setTransactionDate(mDate);
        invoiceTranx.setTotalBaseAmount(Double.parseDouble(request.getParameter("total_base_amt")));
        roundoff = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletId("Round off", users.getOutlet().getId());
        invoiceTranx.setRoundOff(Double.parseDouble(request.getParameter("roundoff")));
        invoiceTranx.setPurchaseRoundOff(roundoff);
        Boolean isContains = dbList.contains(roundoff.getId());
        String crdr = "";
        Double rf = Double.parseDouble(request.getParameter("roundoff"));
        /* inserting into Round off  JSON Object */
        if (isContains) {
            if (rf >= 0) {
                crdr = "DR";
            } else if (Double.parseDouble(request.getParameter("roundoff")) < 0) {
                crdr = "CR";
            }
            transactionDetailsRepository.ledgerPostingEdit(roundoff.getId(), invoiceTranx.getId(),
                    tranxType.getId(), crdr, rf * -1);
        } else {
            if (rf >= 0) {
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                        roundoff.getPrinciples().getFoundations().getId(),
                        roundoff.getPrinciples().getId(), roundoff.getPrincipleGroups() != null ?
                                roundoff.getPrincipleGroups().getId() : null,
                        invoiceTranx.getAssociateGroups() != null ?
                                invoiceTranx.getAssociateGroups().getId() : null,
                        tranxType.getId(), null,
                        invoiceTranx.getBranch() != null ? invoiceTranx.getBranch().getId() : null,
                        invoiceTranx.getOutlet().getId(), "pending", rf * -1,
                        0.0, invoiceTranx.getTransactionDate(), null, invoiceTranx.getId(),
                        tranxType.getTransactionName(), roundoff.getUnderPrefix(),
                        invoiceTranx.getFinancialYear(), invoiceTranx.getCreatedBy(),
                        roundoff.getId(), invoiceTranx.getVendorInvoiceNo());
            } else {
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                        roundoff.getPrinciples().getFoundations().getId(), roundoff.getPrinciples().getId(),
                        roundoff.getPrincipleGroups() != null ?
                                roundoff.getPrincipleGroups().getId() : null,
                        invoiceTranx.getAssociateGroups() != null ?
                                invoiceTranx.getAssociateGroups().getId() :
                                null, tranxType.getId(), null,
                        invoiceTranx.getBranch() != null ? invoiceTranx.getBranch().getId() : null,
                        invoiceTranx.getOutlet().getId(), "pending", 0.0,
                        rf * -1,
                        invoiceTranx.getTransactionDate(), null, invoiceTranx.getId(),
                        tranxType.getTransactionName(), roundoff.getUnderPrefix(),
                        invoiceTranx.getFinancialYear(),
                        invoiceTranx.getCreatedBy(), roundoff.getId(), invoiceTranx.getVendorInvoiceNo());
            }
        }
        //  procList.put(mRoundOffObj);
        /* end of inserting into Sundry Creditors JSON Object */

        invoiceTranx.setTotalAmount(Double.parseDouble(request.getParameter("totalamt")));
        taxFlag = Boolean.parseBoolean(request.getParameter("taxFlag"));
        /* if true : cgst and sgst i.e intra state */
        if (taxFlag) {
            invoiceTranx.setTotalcgst(Double.parseDouble(request.getParameter("totalcgst")));
            invoiceTranx.setTotalsgst(Double.parseDouble(request.getParameter("totalsgst")));
            invoiceTranx.setTotaligst(0.0);
        }
        /* if false : igst i.e inter state */
        else {
            invoiceTranx.setTotalcgst(0.0);
            invoiceTranx.setTotalsgst(0.0);
            invoiceTranx.setTotaligst(Double.parseDouble(request.getParameter("totaligst")));
        }
        invoiceTranx.setTotalqty(Long.parseLong(request.getParameter("totalqty")));
        invoiceTranx.setTcs(Double.parseDouble(request.getParameter("tcs")));
        invoiceTranx.setTaxableAmount(Double.parseDouble(request.getParameter("taxable_amount")));
        invoiceTranx.setPurchaseDiscountPer(Double.parseDouble(
                request.getParameter("purchase_discount")));
        invoiceTranx.setPurchaseDiscountAmount(Double.parseDouble(
                request.getParameter("purchase_discount_amt")));
        invoiceTranx.setTotalPurchaseDiscountAmt(Double.parseDouble(
                request.getParameter("total_purchase_discount_amt")));
        invoiceTranx.setSrno(Long.parseLong(request.getParameter("purchase_sr_no")));
        invoiceTranx.setCreatedBy(users.getId());
        invoiceTranx.setAdditionalChargesTotal(Double.parseDouble(
                request.getParameter("additionalChargesTotal")));
        invoiceTranx.setStatus(true);
        invoiceTranx.setNarration(request.getParameter("narration"));
        try {
            mPurchaseTranx = tranxPurInvoiceRepository.save(invoiceTranx);
            if (mPurchaseTranx != null) {

                /**** for PurchaseInvoice Details Edit ****/
                String jsonStr = request.getParameter("row");
                JsonParser parser = new JsonParser();
                JsonElement purDetailsJson = parser.parse(jsonStr);
                JsonArray array = purDetailsJson.getAsJsonArray();
                //  JsonArray array = new JsonArray(jsonStr);
                String rowsDeleted = request.getParameter("rowDelDetailsIds");
                saveIntoPurchaseInvoiceDetailsEdit(array, mPurchaseTranx,
                        users.getBranch() != null ? users.getBranch() : null, users.getOutlet(), users.getId(), rowsDeleted, tranxType);
                /**** end of PurchaseInvoice DetailsEdit ****/

                /**** for Purchase Duties and Texes Edit ****/
                String taxStr = request.getParameter("taxCalculation");
                JsonObject duties_taxes = new Gson().fromJson(taxStr, JsonObject.class);
                saveIntoPurchaseDutiesTaxesEdit(duties_taxes, mPurchaseTranx, taxFlag,
                        tranxType, users.getOutlet().getId(), users.getId());
                /**** end of Purchase Duties and Texes Edit ****/

                /**** Additional Charges in Update start ***/
                saveHistoryPurchaseAdditionalCharges(mPurchaseTranx);
                String strJson = request.getParameter("additionalCharges");
                JsonElement purAddChargesJson = parser.parse(strJson);
                JsonArray additionalCharges = purAddChargesJson.getAsJsonArray();
                if (additionalCharges.size() > 0) {
                    saveIntoPurchaseAdditionalChargesEdit(additionalCharges, mPurchaseTranx,
                            tranxType, users.getOutlet().getId());
                }
                /**** Additional Charges in Update End ***/
                /* Remove all ledgers from DB if we found new input ledger id's while updating */
                for (Long mDblist : dbList) {
                    if (!mInputList.contains(mDblist)) {
                        transactionDetailsRepository.ledgerPostingRemove(mDblist, invoiceTranx.getId(), tranxType.getId());
                    }
                }
            }
            responseMessage.setMessage("success");
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        } catch (DataIntegrityViolationException e1) {
            System.out.println("Exception:" + e1.getMessage());
            responseMessage.setMessage("error");
            responseMessage.setResponseStatus(HttpStatus.FORBIDDEN.value());
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
            responseMessage.setMessage("error");
            responseMessage.setResponseStatus(HttpStatus.FORBIDDEN.value());
        }
        return responseMessage;
    }

    /* for Purchase Edit */
    public void saveIntoPurchaseInvoiceDetailsEdit(JsonArray array, TranxPurInvoice mPurchaseTranx,
                                                   Branch branch, Outlet outlet, Long userId,
                                                   String rowsDeleted, TransactionTypeMaster tranxType) {
        List<TranxPurInvoiceDetails> row =
                new ArrayList<>();
        row = invoiceDetailsRepository.findByPurchaseTransactionIdAndStatus(mPurchaseTranx.getId(), true);
        insertIntoPurchaseInvoiceDetailsHistory(row);

        /* Purchase Product Details Start here */
        row.clear();
        for (JsonElement mList : array) {
            TranxPurInvoiceDetails mDetails = null;
            JsonObject object = mList.getAsJsonObject();
            Long detailsId = object.get("details_id").getAsLong();
            if (detailsId != 0) {
                mDetails = invoiceDetailsRepository.findByIdAndStatus(detailsId, true);
                if (mDetails != null) {
                    mDetails.setOperations("updated");
                    // Boolean flag = insertIntoPurchaseInvoiceDetailsHistory(mDetails);
                } else {
                    mDetails = new TranxPurInvoiceDetails();
                    mDetails.setPurchaseTransaction(mPurchaseTranx);
                    mDetails.setOperations("inserted");
                }
            } else {
                mDetails = new TranxPurInvoiceDetails();
                mDetails.setPurchaseTransaction(mPurchaseTranx);
                mDetails.setOperations("inserted");
            }
            Product mProduct = productRepository.findByIdAndStatus(
                    object.get("product_id").getAsLong(), true);
            mDetails.setProduct(mProduct);
            mDetails.setBase_amt(object.get("base_amt").getAsDouble());
            if (!object.get("dis_amt").getAsString().equals("")) {
                mDetails.setDiscountAmount(object.get("dis_amt").getAsDouble());
            } else {
                mDetails.setDiscountAmount((double) 0);
            }
            if (!object.get("dis_per").getAsString().equals("")) {

                mDetails.setDiscountPer(object.get("dis_per").getAsDouble());
            } else {
                mDetails.setDiscountPer((double) 0);
            }
            if (!object.get("dis_per_cal").getAsString().equals("")) {
                mDetails.setDiscountAmountCal(object.get("dis_per_cal").getAsDouble());
            } else {
                mDetails.setDiscountAmountCal((double) 0);
            }
            if (!object.get("dis_amt_cal").getAsString().equals("")) {

                mDetails.setDiscountAmountCal(object.get("dis_amt_cal").getAsDouble());
            } else {
                mDetails.setDiscountAmountCal((double) 0);
            }
            mDetails.setTotalAmount(object.get("total_amt").getAsDouble());
            mDetails.setIgst(object.get("igst").getAsDouble());
            mDetails.setSgst(object.get("sgst").getAsDouble());
            mDetails.setCgst(object.get("cgst").getAsDouble());
            mDetails.setTotalIgst(object.get("total_igst").getAsDouble());
            mDetails.setTotalSgst(object.get("total_sgst").getAsDouble());
            mDetails.setTotalCgst(object.get("total_cgst").getAsDouble());
            mDetails.setFinalAmount(object.get("final_amt").getAsDouble());
            mDetails.setQtyHigh(object.get("qtyH").getAsDouble());
            mDetails.setRateHigh(object.get("rateH").getAsDouble());
            mDetails.setStatus(true);
            if (!object.get("qtyM").getAsString().equals("")) {
                mDetails.setQtyMedium(object.get("qtyM").getAsDouble());
            } else {
                mDetails.setQtyMedium((double) 0);
            }
            if (!object.get("rateM").getAsString().equals("")) {
                mDetails.setRateMedium(object.get("rateM").getAsDouble());
            } else {
                mDetails.setRateMedium((double) 0);
            }
            if (!object.get("qtyL").getAsString().equals("")) {
                mDetails.setQtyLow(object.get("qtyL").getAsDouble());
            } else {
                mDetails.setQtyLow((double) 0);
            }
            if (!object.get("rateL").getAsString().equals("")) {
                mDetails.setRateLow(object.get("rateL").getAsDouble());
            } else {
                mDetails.setRateLow((double) 0);
            }
            mDetails.setBaseAmtHigh(object.get("base_amt_H").getAsDouble());
            if (!object.get("base_amt_M").getAsString().equals("")) {
                mDetails.setBaseAmtMedium(object.get("base_amt_M").getAsDouble());
            } else {
                mDetails.setBaseAmtMedium((double) 0);
            }
            if (!object.get("base_amt_L").getAsString().equals("")) {
                mDetails.setBaseAmtLow(object.get("base_amt_L").getAsDouble());
            } else {
                mDetails.setBaseAmtLow((double) 0);
            }
            row.add(mDetails);
            TranxPurInvoiceDetails newDetails = invoiceDetailsRepository.save(mDetails);
            JsonArray jsonArray = object.getAsJsonArray("serialNo");
            if (jsonArray.size() > 0) {
                List<TranxPurchaseInvoiceProductSrNumber> serialNumbers = new ArrayList<>();
                for (JsonElement mArray : jsonArray) {
                    JsonObject json = mArray.getAsJsonObject();
                    TranxPurchaseInvoiceProductSrNumber productSerialNumber =
                            new TranxPurchaseInvoiceProductSrNumber();
                    productSerialNumber.setBranch(branch);
                    productSerialNumber.setOutlet(outlet);
                    productSerialNumber.setProduct(mProduct);
                    productSerialNumber.setSerialNo(json.get("no").getAsString());
                    productSerialNumber.setPurchaseTransaction(mPurchaseTranx);
                    productSerialNumber.setTransactionStatus("purchase");
                    productSerialNumber.setTransactionTypeMaster(tranxType);
                    productSerialNumber.setStatus(true);
                    productSerialNumber.setUpdatedBy(userId);
                    serialNumbers.add(productSerialNumber);
                }
                try {
                    serialNumberRepository.saveAll(serialNumbers);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        /* if product is deleted from details table from front end, when user edit the purchase */
        try {
            //     List<PurchaseInvoiceDetails> list = invoiceDetailsRepository.saveAll(row);
            JsonParser parser = new JsonParser();
            JsonElement purDetailsJson = parser.parse(rowsDeleted);
            JsonArray deletedArrays = purDetailsJson.getAsJsonArray();
            if (deletedArrays.size() > 0) {
                TranxPurInvoiceDetails mDeletedInvoices = null;
                for (JsonElement element : deletedArrays) {
                    JsonObject deletedRowsId = element.getAsJsonObject();
                    mDeletedInvoices = invoiceDetailsRepository.findByIdAndStatus(
                            deletedRowsId.get("del_id").getAsLong(), true);
                    if (mDeletedInvoices != null) {
                        mDeletedInvoices.setStatus(false);
                        mDeletedInvoices.setOperations("removed");
                        invoiceDetailsRepository.save(mDeletedInvoices);
                                /*System.out.println("\nPurchase invoice edited successfully");
                                responseMessage.setMessage("purchase invoice edited successfully");
                                responseMessage.setResponseStatus(HttpStatus.OK.value());*/
                    }
                }
            }

        } catch (DataIntegrityViolationException de) {
            System.out.println("Exception:" + de.getMessage());

        } catch (Exception ex) {
            System.out.println("Exception:" + ex.getMessage());
        }
    }

    /* for Purchase Details Edit */
    private void insertIntoPurchaseInvoiceDetailsHistory(List<TranxPurInvoiceDetails> row) {
        for (TranxPurInvoiceDetails mRow : row) {
            mRow.setStatus(false);
            mRow.setOperations("updated");
            invoiceDetailsRepository.save(mRow);
        }
    }

    /* for Purchase Invoice Edit */
    public void saveIntoPurchaseDutiesTaxesEdit(JsonObject duties_taxes,
                                                TranxPurInvoice invoiceTranx, Boolean taxFlag, TransactionTypeMaster tranxType, Long outletId, Long userId) {
        /* Purchase Duties and Taxes */
        List<TranxPurInvoiceDutiesTaxes> purchaseDutiesTaxes = new ArrayList<>();
        /* purchaseDutiesTaxes = purInvoiceDutiesTaxesRepository.findByPurchaseTransactionIdAndStatus(
                        mPurchaseTranx.getId(), true);*/
        /* getting duties_taxes_ledger_id  */
        List<Long> db_dutiesLedgerIds = purInvoiceDutiesTaxesRepository.findByDutiesAndTaxesId(
                invoiceTranx.getId());
        List<Long> input_dutiesLedgerIds = getInputLedgerIds(taxFlag, duties_taxes, outletId);
        List<Long> travelArray = CustomArrayUtilities.getTwoArrayMergeUnique(db_dutiesLedgerIds,
                input_dutiesLedgerIds);
//                System.out.println("travelArray"+travelArray);
        List<Long> travelledArray = new ArrayList();
        if (travelArray.size() > 0) {
            //Updation into Purchase Duties and Taxes
            if (db_dutiesLedgerIds.size() > 0) {
                //insert old records in history
                purchaseDutiesTaxes = purInvoiceDutiesTaxesRepository.
                        findByPurchaseTransactionAndStatus(invoiceTranx, true);
                insertIntoDutiesAndTaxesHistory(purchaseDutiesTaxes);
            }
            if (taxFlag) {
                JsonArray cgstList = duties_taxes.getAsJsonArray("cgst");
                JsonArray sgstList = duties_taxes.getAsJsonArray("sgst");
                /* this is for Cgst creation */
                if (cgstList.size() > 0) {
                    for (JsonElement mCgst : cgstList) {
                        TranxPurInvoiceDutiesTaxes taxes = new TranxPurInvoiceDutiesTaxes();
                        JsonObject cgstObject = mCgst.getAsJsonObject();
                        LedgerMaster dutiesTaxes = null;
                        String inputGst = cgstObject.get("gst").getAsString();
                        String ledgerName = "INPUT CGST " + inputGst;
                        dutiesTaxes = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletId(
                                ledgerName, outletId);
                        Double amt = cgstObject.get("amt").getAsDouble();
                        if (dutiesTaxes != null) {
                            //   dutiesTaxesLedger.setDutiesTaxes(dutiesTaxes);
                            taxes.setDutiesTaxes(dutiesTaxes);
                            travelledArray.add(dutiesTaxes.getId());
                            Boolean isContains = dbList.contains(dutiesTaxes.getId());
                            mInputList.add(dutiesTaxes.getId());
                            if (isContains) {
                                transactionDetailsRepository.ledgerPostingEdit(dutiesTaxes.getId(),
                                        invoiceTranx.getId(), tranxType.getId(), "DR", amt * -1);
                            } else {
                                /* insert */
                                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                                        dutiesTaxes.getPrinciples().getFoundations().getId(),
                                        dutiesTaxes.getPrinciples().getId(),
                                        dutiesTaxes.getPrincipleGroups() != null ?
                                                dutiesTaxes.getPrincipleGroups().getId() : null,
                                        invoiceTranx.getAssociateGroups() != null ?
                                                invoiceTranx.getAssociateGroups().getId() : null,
                                        tranxType.getId(), null,
                                        invoiceTranx.getBranch() != null ? invoiceTranx.getBranch().getId() : null,
                                        invoiceTranx.getOutlet().getId(), "pending", amt * -1,
                                        0.0, invoiceTranx.getTransactionDate(), null,
                                        invoiceTranx.getId(), tranxType.getTransactionName(),
                                        dutiesTaxes.getUnderPrefix(),
                                        invoiceTranx.getFinancialYear(), invoiceTranx.getCreatedBy(),
                                        dutiesTaxes.getId(), invoiceTranx.getVendorInvoiceNo());
                            }
                        }
                        taxes.setAmount(amt);
                        taxes.setStatus(true);
                        taxes.setPurchaseTransaction(invoiceTranx);
                        taxes.setSundryCreditors(invoiceTranx.getSundryCreditors());
                        taxes.setIntra(taxFlag);
                        purchaseDutiesTaxes.add(taxes);
                    }
                }
                /* this is for Sgst creation */
                if (sgstList.size() > 0) {
                    for (JsonElement mSgst : sgstList) {
                        TranxPurInvoiceDutiesTaxes taxes = new TranxPurInvoiceDutiesTaxes();
                        JsonObject sgstObject = mSgst.getAsJsonObject();
                        LedgerMaster dutiesTaxes = null;
                        String inputGst = sgstObject.get("gst").getAsString();
                        String ledgerName = "INPUT SGST " + inputGst;
                        Double amt = sgstObject.get("amt").getAsDouble();
                        dutiesTaxes = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletId(
                                ledgerName, outletId);
                        if (dutiesTaxes != null) {
                            taxes.setDutiesTaxes(dutiesTaxes);
                            travelledArray.add(dutiesTaxes.getId());
                            Boolean isContains = dbList.contains(dutiesTaxes.getId());
                            mInputList.add(dutiesTaxes.getId());
                            if (isContains) {
                                transactionDetailsRepository.ledgerPostingEdit(dutiesTaxes.getId(),
                                        invoiceTranx.getId(),
                                        tranxType.getId(), "DR", amt * -1);
                            } else {
                                /* insert */
                                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                                        dutiesTaxes.getPrinciples().getFoundations().getId(),
                                        dutiesTaxes.getPrinciples().getId(),
                                        dutiesTaxes.getPrincipleGroups() != null ?
                                                dutiesTaxes.getPrincipleGroups().getId() : null,
                                        invoiceTranx.getAssociateGroups() != null ?
                                                invoiceTranx.getAssociateGroups().getId() : null,
                                        tranxType.getId(), null,
                                        invoiceTranx.getBranch() != null ? invoiceTranx.getBranch().getId() : null,
                                        invoiceTranx.getOutlet().getId(), "pending", amt * -1,
                                        0.0, invoiceTranx.getTransactionDate(), null,
                                        invoiceTranx.getId(), tranxType.getTransactionName(),
                                        dutiesTaxes.getUnderPrefix(),
                                        invoiceTranx.getFinancialYear(), invoiceTranx.getCreatedBy(),
                                        dutiesTaxes.getId(), invoiceTranx.getVendorInvoiceNo());
                            }

                        }
                        taxes.setAmount(amt);
                        taxes.setPurchaseTransaction(invoiceTranx);
                        taxes.setSundryCreditors(invoiceTranx.getSundryCreditors());
                        taxes.setIntra(taxFlag);
                        taxes.setStatus(true);
                        purchaseDutiesTaxes.add(taxes);
                    }
                }
            } else {
                JsonArray igstList = duties_taxes.getAsJsonArray("igst");
                /* this is for Igst creation */
                if (igstList.size() > 0) {
                    for (JsonElement mIgst : igstList) {
                        TranxPurInvoiceDutiesTaxes taxes = new TranxPurInvoiceDutiesTaxes();
                        JsonObject igstObject = mIgst.getAsJsonObject();
                        LedgerMaster dutiesTaxes = null;
                        String inputGst = igstObject.get("gst").getAsString();
                        String ledgerName = "INPUT IGST " + inputGst;
                        Double amt = igstObject.get("amt").getAsDouble();
                        dutiesTaxes = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletId(
                                ledgerName, outletId);
                        if (dutiesTaxes != null) {
                            taxes.setDutiesTaxes(dutiesTaxes);
                            travelledArray.add(dutiesTaxes.getId());
                            Boolean isContains = dbList.contains(dutiesTaxes.getId());
                            mInputList.add(dutiesTaxes.getId());
                            if (isContains) {
                                transactionDetailsRepository.ledgerPostingEdit(dutiesTaxes.getId(),
                                        invoiceTranx.getId(),
                                        tranxType.getId(), "DR", amt * -1);
                            } else {
                                /* insert */
                                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                                        dutiesTaxes.getPrinciples().getFoundations().getId(),
                                        dutiesTaxes.getPrinciples().getId(),
                                        dutiesTaxes.getPrincipleGroups() != null ?
                                                dutiesTaxes.getPrincipleGroups().getId() : null,
                                        invoiceTranx.getAssociateGroups() != null ?
                                                invoiceTranx.getAssociateGroups().getId() : null,
                                        tranxType.getId(), null,
                                        invoiceTranx.getBranch() != null ? invoiceTranx.getBranch().getId() : null,
                                        invoiceTranx.getOutlet().getId(), "pending", amt * -1,
                                        0.0, invoiceTranx.getTransactionDate(), null,
                                        invoiceTranx.getId(), tranxType.getTransactionName(),
                                        dutiesTaxes.getUnderPrefix(),
                                        invoiceTranx.getFinancialYear(), invoiceTranx.getCreatedBy(),
                                        dutiesTaxes.getId(), invoiceTranx.getVendorInvoiceNo());
                            }
                        }
                        taxes.setAmount(amt);
                        taxes.setPurchaseTransaction(invoiceTranx);
                        taxes.setSundryCreditors(invoiceTranx.getSundryCreditors());
                        taxes.setIntra(taxFlag);
                        taxes.setStatus(true);
                        purchaseDutiesTaxes.add(taxes);
                    }
                }
            }
        } else {
            //Insertion into Purchase Duties and Taxes
            if (taxFlag) {
                JsonArray cgstList = duties_taxes.getAsJsonArray("cgst");
                JsonArray sgstList = duties_taxes.getAsJsonArray("sgst");
                /* this is for Cgst creation */
                if (cgstList.size() > 0) {
                    for (JsonElement mCgst : cgstList) {
                        TranxPurInvoiceDutiesTaxes taxes = new TranxPurInvoiceDutiesTaxes();
                        JsonObject cgstObject = mCgst.getAsJsonObject();
                        LedgerMaster dutiesTaxes = null;
                        String inputGst = cgstObject.get("gst").getAsString();
                        String ledgerName = "INPUT CGST " + inputGst;
                        Double amt = cgstObject.get("amt").getAsDouble();
                        dutiesTaxes = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletId(
                                ledgerName, outletId);
                        if (dutiesTaxes != null) {
                            //   dutiesTaxesLedger.setDutiesTaxes(dutiesTaxes);
                            taxes.setDutiesTaxes(dutiesTaxes);
                        }
                        taxes.setAmount(amt);
                        taxes.setPurchaseTransaction(invoiceTranx);
                        taxes.setSundryCreditors(invoiceTranx.getSundryCreditors());
                        taxes.setIntra(taxFlag);
                        purchaseDutiesTaxes.add(taxes);
                    }
                }
                /* this is for Sgst creation */
                if (sgstList.size() > 0) {
                    for (JsonElement mSgst : sgstList) {
                        TranxPurInvoiceDutiesTaxes taxes = new TranxPurInvoiceDutiesTaxes();
                        JsonObject sgstObject = mSgst.getAsJsonObject();
                        LedgerMaster dutiesTaxes = null;
                        String inputGst = sgstObject.get("gst").getAsString();
                        String ledgerName = "INPUT SGST " + inputGst;
                        Double amt = sgstObject.get("amt").getAsDouble();
                        dutiesTaxes = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletId(
                                ledgerName, outletId);
                        if (dutiesTaxes != null) {
                            taxes.setDutiesTaxes(dutiesTaxes);
                        }
                        taxes.setAmount(amt);
                        taxes.setPurchaseTransaction(invoiceTranx);
                        taxes.setSundryCreditors(invoiceTranx.getSundryCreditors());
                        taxes.setIntra(taxFlag);
                        purchaseDutiesTaxes.add(taxes);
                    }
                }
            } else {
                JsonArray igstList = duties_taxes.getAsJsonArray("igst");
                /* this is for Igst creation */
                if (igstList.size() > 0) {
                    for (JsonElement mIgst : igstList) {
                        TranxPurInvoiceDutiesTaxes taxes = new TranxPurInvoiceDutiesTaxes();
                        JsonObject igstObject = igstList.getAsJsonObject();
                        LedgerMaster dutiesTaxes = null;
                        String inputGst = igstObject.get("gst").getAsString();
                        String ledgerName = "INPUT IGST " + inputGst;
                        Double amt = igstObject.get("amt").getAsDouble();
                        dutiesTaxes = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletId(
                                ledgerName, outletId);
                        if (dutiesTaxes != null) {
                            taxes.setDutiesTaxes(dutiesTaxes);
                        }
                        taxes.setAmount(amt);
                        taxes.setPurchaseTransaction(invoiceTranx);
                        taxes.setSundryCreditors(invoiceTranx.getSundryCreditors());
                        taxes.setIntra(taxFlag);
                        purchaseDutiesTaxes.add(taxes);
                    }
                }
            }
        }
        purInvoiceDutiesTaxesRepository.saveAll(purchaseDutiesTaxes);
    }

    private List<Long> getInputLedgerIds(Boolean taxFlag, JsonObject duties_taxes, Long outletId) {
        List<Long> returnLedgerIds = new ArrayList<>();
        if (taxFlag) {
            JsonArray cgstList = duties_taxes.getAsJsonArray("cgst");
            JsonArray sgstList = duties_taxes.getAsJsonArray("sgst");
            /* this is for Cgst creation */
            if (cgstList.size() > 0) {
                for (JsonElement mCgst : cgstList) {
                    TranxPurInvoiceDutiesTaxes taxes = new TranxPurInvoiceDutiesTaxes();
                    JsonObject cgstObject = mCgst.getAsJsonObject();
                    LedgerMaster dutiesTaxes = null;
                    String inputGst = cgstObject.get("gst").getAsString();
                    String ledgerName = "INPUT CGST " + inputGst;
                    dutiesTaxes = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletId(
                            ledgerName, outletId);
                    if (dutiesTaxes != null) {
                        //   dutiesTaxesLedger.setDutiesTaxes(dutiesTaxes);
                        returnLedgerIds.add(dutiesTaxes.getId());
                    }
                }
            }
            /* this is for Sgst creation */
            if (sgstList.size() > 0) {
                for (JsonElement mSgst : sgstList) {
                    TranxPurInvoiceDutiesTaxes taxes = new TranxPurInvoiceDutiesTaxes();
                    JsonObject sgstObject = mSgst.getAsJsonObject();
                    LedgerMaster dutiesTaxes = null;
                    String inputGst = sgstObject.get("gst").getAsString();
                    String ledgerName = "INPUT SGST " + inputGst;
                    dutiesTaxes = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletId(
                            ledgerName, outletId);
                    if (dutiesTaxes != null) {
                        returnLedgerIds.add(dutiesTaxes.getId());
                    }
                }
            }
        } else {
            JsonArray igstList = duties_taxes.getAsJsonArray("igst");
            /* this is for Igst creation */
            if (igstList.size() > 0) {
                for (JsonElement mIgst : igstList) {
                    JsonObject igstObject = mIgst.getAsJsonObject();
                    LedgerMaster dutiesTaxes = null;
                    String inputGst = igstObject.get("gst").getAsString();
                    String ledgerName = "INPUT IGST " + inputGst;
                    dutiesTaxes = ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletId(
                            ledgerName, outletId);
                    if (dutiesTaxes != null) {
                        returnLedgerIds.add(dutiesTaxes.getId());
                    }
                }
            }
        }
        return returnLedgerIds;
    }

    /**** Save Into Purchase Additional Charges Edit *****/
    public void saveIntoPurchaseAdditionalChargesEdit(JsonArray additionalCharges,
                                                      TranxPurInvoice mPurchaseTranx,
                                                      TransactionTypeMaster tranxTypeMater, Long outletId) {

        List<TranxPurInvoiceAdditionalCharges> chargesList = new ArrayList<>();
        for (JsonElement mAddCharges : additionalCharges) {
            JsonObject object = mAddCharges.getAsJsonObject();
            Double amount = object.get("amt").getAsDouble();
            Long ledgerId = object.get("ledger").getAsLong();
            Long detailsId = object.get("additional_charges_details_id").getAsLong();
            LedgerMaster addcharges = null;
            TranxPurInvoiceAdditionalCharges charges = null;
            if (detailsId != 0) {
                charges = purInvoiceAdditionalChargesRepository.findByIdAndStatus(detailsId, true);
            } else {
                charges = new TranxPurInvoiceAdditionalCharges();
            }
            addcharges = ledgerMasterRepository.findByIdAndOutletIdAndStatus(
                    ledgerId, outletId, true);
            charges.setAmount(amount);
            charges.setAdditionalCharges(addcharges);
            charges.setPurchaseTransaction(mPurchaseTranx);
            charges.setStatus(true);
            charges.setOperation("updated");
            chargesList.add(charges);
            Boolean isContains = dbList.contains(addcharges.getId());
            mInputList.add(addcharges.getId());
            if (isContains) {
                transactionDetailsRepository.ledgerPostingEdit(addcharges.getId(),
                        mPurchaseTranx.getId(),
                        tranxTypeMater.getId(), "DR", amount * -1);
            } else {
                /* insert */
                transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                        addcharges.getPrinciples().getFoundations().getId(),
                        addcharges.getPrinciples().getId(),
                        addcharges.getPrincipleGroups() != null ?
                                addcharges.getPrincipleGroups().getId() : null,
                        mPurchaseTranx.getAssociateGroups() != null ?
                                mPurchaseTranx.getAssociateGroups().getId() : null,
                        tranxTypeMater.getId(), null,
                        mPurchaseTranx.getBranch() != null ? mPurchaseTranx.getBranch().getId() : null,
                        mPurchaseTranx.getOutlet().getId(), "pending", amount * -1,
                        0.0, mPurchaseTranx.getTransactionDate(), null,
                        mPurchaseTranx.getId(), tranxTypeMater.getTransactionName(),
                        addcharges.getUnderPrefix(),
                        mPurchaseTranx.getFinancialYear(), mPurchaseTranx.getCreatedBy(),
                        addcharges.getId(), mPurchaseTranx.getVendorInvoiceNo());
            }
        }

        try {
            purInvoiceAdditionalChargesRepository.saveAll(chargesList);
        } catch (DataIntegrityViolationException e1) {
            System.out.println(e1.getMessage());
            e1.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /* End of Purchase Additional Charges Edit */
    public void insertIntoPurchaseInvoiceHistory(TranxPurInvoice invoiceTranx) {
        invoiceTranx.setStatus(false);
        tranxPurInvoiceRepository.save(invoiceTranx);
    }

    private void insertIntoDutiesAndTaxesHistory(List<TranxPurInvoiceDutiesTaxes> purchaseDutiesTaxes) {
        for (TranxPurInvoiceDutiesTaxes mList : purchaseDutiesTaxes) {
            mList.setStatus(false);
            purInvoiceDutiesTaxesRepository.save(mList);
        }
    }

    private void saveHistoryPurchaseAdditionalCharges(TranxPurInvoice mPurchaseTranx) {
        List<TranxPurInvoiceAdditionalCharges> purInvoiceAdditionalCharges = new ArrayList<>();
        purInvoiceAdditionalCharges = purInvoiceAdditionalChargesRepository.
                findByPurchaseTransactionIdAndStatus(mPurchaseTranx.getId(), true);
        if (purInvoiceAdditionalCharges.size() > 0) {
            for (TranxPurInvoiceAdditionalCharges mList : purInvoiceAdditionalCharges) {
                mList.setStatus(false);
                mList.setOperation("updated");
                purInvoiceAdditionalChargesRepository.save(mList);
            }
        }
    }

    /* find all Purchase Invoices of Sundry Creditors/Suppliers wise , for Purchase Returns */
    public JsonObject purchaseListSupplierWise(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Map<String, String[]> paramMap = request.getParameterMap();
        JsonArray result = new JsonArray();
        List<TranxPurInvoice> purInvoice = new ArrayList<>();
        if (paramMap.containsKey("dateFrom") && paramMap.containsKey("dateTo")) {
            purInvoice = tranxPurInvoiceRepository.findBySuppliersWithDates(
                    users.getOutlet().getId(), true,
                    Long.parseLong(request.getParameter("sundry_creditor_id")),
                    request.getParameter("dateFrom"), request.getParameter("dateTo"));
        } else {
            System.out.println("Creditor Id:" + request.getParameter("sundry_creditor_id"));
            purInvoice = tranxPurInvoiceRepository.findByOutletIdAndStatusAndSundryCreditorsId(
                    users.getOutlet().getId(), true,
                    Long.parseLong(request.getParameter("sundry_creditor_id")));
        }
        if (purInvoice.size() > 0) {
            for (TranxPurInvoice invoices : purInvoice) {
                JsonObject response = new JsonObject();
                response.addProperty("id", invoices.getId());
                response.addProperty("invoice_no", invoices.getVendorInvoiceNo());
                response.addProperty("invoice_date", invoices.getInvoiceDate().toString());
                response.addProperty("transaction_date", invoices.getTransactionDate().toString());
                response.addProperty("purchase_serial_number", invoices.getSrno());
                response.addProperty("total_amount", Math.abs(invoices.getTotalAmount()));
                if (invoices.getTotalAmount() > 0) {
                    response.addProperty("balance_type", "CR");
                } else {
                    response.addProperty("balance_type", "DR");
                }

                response.addProperty("sundry_creditor_name",
                        invoices.getSundryCreditors().getLedgerName());
                response.addProperty("sundry_creditor_id", invoices.getSundryCreditors().getId());
                result.add(response);
            }
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);
        return output;
    }

}
