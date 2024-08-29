package in.truethics.ethics.ethicsapiv10.service.tranx_service.purchase;

import com.google.gson.*;
import in.truethics.ethics.ethicsapiv10.common.GenerateDates;
import in.truethics.ethics.ethicsapiv10.common.GenerateFiscalYear;
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
import in.truethics.ethics.ethicsapiv10.service.master_service.BranchService;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TranxPurReturnsService {

    private static final Logger purchaseReturnLogger = LoggerFactory.getLogger(BranchService.class);
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
    /*   @Autowired
       private InventoryDTO inventoryDTO;*/
    @Autowired
    private InventorySerialNumberSummaryRepository inventorySerialNumberSummaryRepository;
    @Autowired
    private ProductUnitRepository productUnitRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private TranxPurReturnsRepository tranxPurReturnsRepository;
    @Autowired
    private TranxPurInvoiceRepository tranxPurInvoiceRepository;
    @Autowired
    private TranxPurReturnDutiesTaxesRepository tranxPurReturnDutiesTaxesRepository;
    @Autowired
    private TranxPurReturnAddChargesRepository tranxPurReturnAddChargesRepository;
    @Autowired
    private TranxPurReturnDetailsRepository tranxPurReturnDetailsRepository;
    @Autowired
    private TranxPurReturnProductSrNoRepository tranxPurReturnProdSrNoRepository;
    /* @Autowired
     private PaymentNewReferenceRepository paymentNewReferenceRepository;*/
    @Autowired
    private TranxPurChallanRepository tranxPurChallanRepository;
    @Autowired
    private PurchaseInvoiceDetailsRepository invoiceDetailsRepository;
    @Autowired
    private PurchaseInvoiceProductSrNumberRepository serialNumberRepository;
    @Autowired
    private PurInvoiceAdditionalChargesRepository purInvoiceAdditionalChargesRepository;
    @Autowired
    private TranxDebitNoteNewReferenceRepository tranxDebitNoteNewReferenceRepository;
    @Autowired
    private TransactionStatusRepository transactionStatusRepository;
    @Autowired
    private TranxDebitNoteDetailsRepository tranxDebitNoteDetailsRepository;

    @Autowired
    private PackingMasterRepository packingMasterRepository;
    @Autowired
    private UnitsRepository unitsRepository;
    @Autowired
    private TranxPurReturnDetailsUnitRepository tranxPurReturnDetailsUnitRepository;

    public Object createPurReturnsInvoices(HttpServletRequest request) {
        TranxPurReturnInvoice mPurchaseTranx = null;
        ResponseMessage responseMessage = new ResponseMessage();
        mPurchaseTranx = saveIntoPurchaseReturnsInvoice(request);
        if (mPurchaseTranx != null) {
            //
            //   insertIntoLedgerTranxDetails(mPurchaseTranx);// Accounting Postings
            /*** creating new reference while adjusting
             return amount into next purchase invoice bill ***/
            insertIntoNewReference(mPurchaseTranx, request);
            responseMessage.setMessage("purchase return invoice created successfully");
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        } else {
            responseMessage.setMessage("Error in purchase invoice creation");
            responseMessage.setResponseStatus(HttpStatus.FORBIDDEN.value());
        }
        return responseMessage;
    }

    /*** creating debit note (creating new reference automatically) while adjusting
     return amount ***/
    private void insertIntoNewReference(TranxPurReturnInvoice mPurchaseTranx,
                                        HttpServletRequest request) {

        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        TranxDebitNoteNewReferenceMaster tranxDebitNoteNewReference =
                new TranxDebitNoteNewReferenceMaster();
        Map<String, String[]> paramMap = request.getParameterMap();
        if (mPurchaseTranx.getBranch() != null)
            tranxDebitNoteNewReference.setBranch(mPurchaseTranx.getBranch());
        tranxDebitNoteNewReference.setOutlet(mPurchaseTranx.getOutlet());
        tranxDebitNoteNewReference.setSundryCreditor(mPurchaseTranx.getSundryCreditors());
        /* this parameter segregates whether debit note is from purchase invoice
        or purchase challan*/
        if (request.getParameter("source").equalsIgnoreCase("pur_invoice")) {

            tranxDebitNoteNewReference.setTranxPurInvoice(mPurchaseTranx.getTranxPurInvoice());
        } else {
            tranxDebitNoteNewReference.setTranxPurChallan(mPurchaseTranx.getTranxPurChallan());
        }
        tranxDebitNoteNewReference.setTranxPurReturnInvoice(mPurchaseTranx);
        TransactionStatus transactionStatus = transactionStatusRepository.findByStatusNameAndStatus(
                "opened", true);
        tranxDebitNoteNewReference.setTransactionStatus(transactionStatus);
        Long count = tranxDebitNoteNewReferenceRepository.findLastRecord(users.getOutlet().getId());
        //SQDEC00001
        String serailNo = String.format("%05d", count + 1);// 5 digit serial number
        //first 3 digits of Current month
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
        String dbtnCode = "DBTN" + currentMonth + serailNo;
        tranxDebitNoteNewReference.setDebitnoteNewReferenceNo(dbtnCode);
        tranxDebitNoteNewReference.setStatus(true);
        if (paramMap.containsKey("value")) {
            tranxDebitNoteNewReference.setAdjustmentStatus(request.getParameter("value"));
        } else {
            tranxDebitNoteNewReference.setAdjustmentStatus(request.getParameter("type"));
        }
        tranxDebitNoteNewReference.setRoundOff(mPurchaseTranx.getRoundOff());
        tranxDebitNoteNewReference.setTotalBaseAmount(mPurchaseTranx.getTotalBaseAmount());
        tranxDebitNoteNewReference.setTotalAmount(mPurchaseTranx.getTotalAmount());
        tranxDebitNoteNewReference.setTaxableAmount(mPurchaseTranx.getTaxableAmount());
        tranxDebitNoteNewReference.setTotalgst(mPurchaseTranx.getTotaligst());
        tranxDebitNoteNewReference.setPurchaseDiscountAmount(mPurchaseTranx.getPurchaseDiscountAmount());
        tranxDebitNoteNewReference.setPurchaseDiscountPer(mPurchaseTranx.getPurchaseDiscountPer());
        tranxDebitNoteNewReference.setTotalPurchaseDiscountAmt(mPurchaseTranx.getTotalPurchaseDiscountAmt());
        tranxDebitNoteNewReference.setAdditionalChargesTotal(mPurchaseTranx.getAdditionalChargesTotal());
        tranxDebitNoteNewReference.setFinancialYear(mPurchaseTranx.getFinancialYear());
        tranxDebitNoteNewReference.setBalance(mPurchaseTranx.getTotalAmount());
        if (request.getParameter("type").equalsIgnoreCase("credit")) {
            if (request.getParameter("value").equalsIgnoreCase("immediate")) {
                tranxDebitNoteNewReference.setBalance(mPurchaseTranx.getTranxPurInvoice().getBalance() - mPurchaseTranx.getTotalAmount());
                transactionStatus = transactionStatusRepository.findByStatusNameAndStatus(
                        "closed", true);
                if (tranxDebitNoteNewReference.getBalance() == 0.0)
                    tranxDebitNoteNewReference.setTransactionStatus(transactionStatus);
            }
        }
        try {
            TranxDebitNoteNewReferenceMaster newDebitNote =
                    tranxDebitNoteNewReferenceRepository.save(tranxDebitNoteNewReference);

            if (newDebitNote.getAdjustmentStatus().equalsIgnoreCase("immediate")) {
                TranxPurInvoice mInvoice = tranxPurInvoiceRepository.findByIdAndStatus(
                        mPurchaseTranx.getTranxPurInvoice().getId(), true);
                if (mInvoice != null) {
                    try {
                        mInvoice.setBalance(mPurchaseTranx.getTranxPurInvoice().getBalance() - mPurchaseTranx.getTotalAmount());
                        tranxPurInvoiceRepository.save(mInvoice);
                    } catch (Exception e) {
                        purchaseReturnLogger.error("Exception in Tranx Debit Note Creation:" + e.getMessage());
                    }
                }
               /* inserting into Tranx Debit note details table for maintaining adjustment
             of one debit note against multiple invoices or payments or receipt (master and child relation)*/
                TranxDebitNoteDetails mDetails = new TranxDebitNoteDetails();
                if (newDebitNote.getBranch() != null)
                    mDetails.setBranch(newDebitNote.getBranch());
                mDetails.setOutlet(newDebitNote.getOutlet());
                mDetails.setSundryCreditor(newDebitNote.getSundryCreditor());
                mDetails.setTotalAmount(newDebitNote.getTotalAmount());
                mDetails.setPaidAmt(newDebitNote.getTotalAmount());
                mDetails.setAdjustedId(newDebitNote.getTranxPurInvoice().getId());
                mDetails.setAdjustedSource("purchase_invoice");
                mDetails.setBalance(0.0);
                mDetails.setOperations("adjust");
                mDetails.setTranxDebitNoteMaster(newDebitNote);
                mDetails.setStatus(true);
                mDetails.setAdjustmentStatus(newDebitNote.getAdjustmentStatus());
                // immediate
                tranxDebitNoteDetailsRepository.save(mDetails);
                newDebitNote.setBalance(0.0);
              /*  transactionStatus = transactionStatusRepository.findByStatusNameAndStatus(
                        "closed", true);
                newDebitNote.setTransactionStatus(transactionStatus);
                tranxDebitNoteNewReferenceRepository.save(newDebitNote);*/
            }
            /* Accounting Postings */
            insertIntoLedgerTranxDetails(mPurchaseTranx);// Accounting Postings
        } catch (Exception e) {
            purchaseReturnLogger.error("Exception in Tranx Debit Note Creation:" + e.getMessage());
        }
    }

    private void insertIntoTranxDebitNoteDetails(TranxDebitNoteNewReferenceMaster newDebitNote) {
        TranxDebitNoteDetails mDetails = new TranxDebitNoteDetails();
        if (newDebitNote.getBranch() != null)
            mDetails.setBranch(newDebitNote.getBranch());
        mDetails.setOutlet(newDebitNote.getOutlet());
        mDetails.setSundryCreditor(newDebitNote.getSundryCreditor());
        mDetails.setTransactionStatus(newDebitNote.getTransactionStatus());
        mDetails.setTotalAmount(newDebitNote.getTotalAmount());
        mDetails.setBalance(newDebitNote.getTotalAmount());
        if (newDebitNote.getAdjustmentStatus().equalsIgnoreCase("immediate")) {
            mDetails.setAdjustedId(newDebitNote.getTranxPurInvoice().getId());
            mDetails.setAdjustedSource("purchase_invoice");
        } else {
            mDetails.setAdjustedSource("Not adjusted yet");
        }
        mDetails.setStatus(true);
        mDetails.setAdjustmentStatus(newDebitNote.getAdjustmentStatus());
        mDetails.setOperations("create");
        tranxDebitNoteDetailsRepository.save(mDetails);
    }

    /******* save into Purchase Return Invoice *******/
    private TranxPurReturnInvoice saveIntoPurchaseReturnsInvoice(HttpServletRequest request) {

        Map<String, String[]> paramMap = request.getParameterMap();
        TranxPurReturnInvoice mPurchaseTranx = null;
        TransactionTypeMaster tranxType = null;
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Branch branch = null;
        Outlet outlet = users.getOutlet();
        TranxPurReturnInvoice invoiceTranx = new TranxPurReturnInvoice();
        if (users.getBranch() != null) {
            branch = users.getBranch();
            invoiceTranx.setBranch(branch);
        }
        invoiceTranx.setOutlet(outlet);
        tranxType = tranxRepository.findByTransactionNameIgnoreCase("purchase return");
        LocalDate mDate = LocalDate.now();
        invoiceTranx.setTransactionDate(mDate);
        /* fiscal year mapping */
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(mDate);
        if (fiscalYear != null) {
            invoiceTranx.setFiscalYear(fiscalYear);
            invoiceTranx.setFinancialYear(fiscalYear.getFiscalYear());
        }
        /* End of fiscal year mapping */
        invoiceTranx.setPurRtnNo(request.getParameter("pur_return_invoice_no"));
        LedgerMaster purchaseAccount = ledgerMasterRepository.
                findByIdAndOutletIdAndStatus(Long.parseLong(
                        request.getParameter("purchase_account_id")), users.getOutlet().getId(), true);
        invoiceTranx.setPurchaseAccountLedger(purchaseAccount);
        // Long discountLedgerId = Long.parseLong(request.getParameter("purchase_disc_ledger"));
        if (!request.getParameter("purchase_disc_ledger").equalsIgnoreCase("")) {
            LedgerMaster discountLedger = ledgerMasterRepository.findByIdAndOutletIdAndStatus(Long.parseLong(
                    request.getParameter("purchase_disc_ledger")), users.getOutlet().getId(), true);
            if (discountLedger != null) {
                invoiceTranx.setPurchaseDiscountLedger(discountLedger);
            }
        }
       /* this parameter segregates whether pur return is from purchase invoice
        or purchase challan*/
        if (request.getParameter("source").equalsIgnoreCase("pur_invoice")) {
            if (paramMap.containsKey("pur_invoice_id")) {
                TranxPurInvoice tranxPurInvoice = tranxPurInvoiceRepository.findByIdAndStatus(
                        Long.parseLong(request.getParameter("pur_invoice_id")), true);
                invoiceTranx.setTranxPurInvoice(tranxPurInvoice);
            }
        } else {
            if (paramMap.containsKey("pur_challan_id")) {
                TranxPurChallan tranxPurChallan = tranxPurChallanRepository.findByIdAndStatus(
                        Long.parseLong(request.getParameter("pur_challan_id")), true);
                invoiceTranx.setTranxPurChallan(tranxPurChallan);
            }
        }
        LedgerMaster sundryCreditors = ledgerMasterRepository.findByIdAndOutletIdAndStatus(
                Long.parseLong(request.getParameter("supplier_code_id")),
                users.getOutlet().getId(), true);
        invoiceTranx.setSundryCreditors(sundryCreditors);
        invoiceTranx.setTotalBaseAmount(Double.parseDouble(request.getParameter("total_base_amt")));
        LedgerMaster roundoff = ledgerMasterRepository.findByOutletIdAndLedgerNameIgnoreCase(users.getOutlet().getId(), "Round off");
        invoiceTranx.setRoundOff(Double.parseDouble(request.getParameter("roundoff")));
        invoiceTranx.setPurchaseRoundOff(roundoff);
        invoiceTranx.setTotalAmount(Double.parseDouble(request.getParameter("totalamt")));
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
        invoiceTranx.setTcs(Double.parseDouble(request.getParameter("tcs")));
        invoiceTranx.setTaxableAmount(Double.parseDouble(request.getParameter("taxable_amount")));
        invoiceTranx.setPurchaseDiscountPer(Double.parseDouble(
                request.getParameter("purchase_discount")));
        invoiceTranx.setPurchaseDiscountAmount(Double.parseDouble(
                request.getParameter("purchase_discount_amt")));
        invoiceTranx.setTotalPurchaseDiscountAmt(Double.parseDouble(
                request.getParameter("total_purchase_discount_amt")));
        invoiceTranx.setPurReturnSrno(Long.parseLong(request.getParameter("purchase_return_sr_no")));
        invoiceTranx.setCreatedBy(users.getId());
        invoiceTranx.setAdditionalChargesTotal(Double.parseDouble(request.getParameter(
                "additionalChargesTotal")));
        invoiceTranx.setStatus(true);
        invoiceTranx.setOperations("insert");
        if (paramMap.containsKey("narration"))
            invoiceTranx.setNarration(request.getParameter("narration"));
        else
            invoiceTranx.setNarration("NA");
        try {
            mPurchaseTranx = tranxPurReturnsRepository.save(invoiceTranx);
            if (mPurchaseTranx != null) {
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
                saveIntoPurchaseAdditionalCharges(additionalCharges,
                        mPurchaseTranx, tranxType.getId(),
                        users.getOutlet().getId());
                /* save into Purchase Invoice Details */
                String jsonStr = request.getParameter("row");
                JsonElement purDetailsJson = parser.parse(jsonStr);
                JsonArray array = purDetailsJson.getAsJsonArray();
                saveIntoPurchaseInvoiceDetails(array, mPurchaseTranx,
                        branch, outlet, users.getId(), tranxType, request);
            }
        } catch (DataIntegrityViolationException e) {
            System.out.println("Exception:" + e.getMessage());

        } catch (Exception e1) {
            System.out.println("Exception:" + e1.getMessage());
        }
        return mPurchaseTranx;
    }

    /****** Save into Duties and Taxes ******/
    public void saveIntoPurchaseDutiesTaxes(JsonObject duties_taxes,
                                            TranxPurReturnInvoice mPurchaseTranx, Boolean taxFlag) {
        List<TranxPurReturnInvoiceDutiesTaxes> purchaseDutiesTaxes = new ArrayList<>();
        if (taxFlag) {
            JsonArray cgstList = duties_taxes.getAsJsonArray("cgst");
            JsonArray sgstList = duties_taxes.getAsJsonArray("sgst");
            /* this is for Cgst creation */
            if (cgstList.size() > 0) {
                for (JsonElement mList : cgstList) {
                    TranxPurReturnInvoiceDutiesTaxes taxes = new TranxPurReturnInvoiceDutiesTaxes();
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
                    taxes.setPurReturnInvoice(mPurchaseTranx);
                    taxes.setTranxPurInvoice(mPurchaseTranx.getTranxPurInvoice());
                    taxes.setSundryCreditors(mPurchaseTranx.getSundryCreditors());
                    taxes.setIntra(taxFlag);
                    taxes.setStatus(true);
                    purchaseDutiesTaxes.add(taxes);
                }
            }
            /* this is for Sgst creation */
            if (sgstList.size() > 0) {
                for (JsonElement mList : sgstList) {
                    TranxPurReturnInvoiceDutiesTaxes taxes = new TranxPurReturnInvoiceDutiesTaxes();
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
                    taxes.setPurReturnInvoice(mPurchaseTranx);
                    taxes.setTranxPurInvoice(mPurchaseTranx.getTranxPurInvoice());
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
                    TranxPurReturnInvoiceDutiesTaxes taxes = new TranxPurReturnInvoiceDutiesTaxes();
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
                    taxes.setPurReturnInvoice(mPurchaseTranx);
                    taxes.setTranxPurInvoice(mPurchaseTranx.getTranxPurInvoice());
                    taxes.setSundryCreditors(mPurchaseTranx.getSundryCreditors());
                    taxes.setIntra(taxFlag);
                    taxes.setStatus(true);
                    purchaseDutiesTaxes.add(taxes);
                }
            }
        }
        try {
            /* save all Duties and Taxes into purchase Invoice Duties taxes table */
            tranxPurReturnDutiesTaxesRepository.saveAll(purchaseDutiesTaxes);
        } catch (DataIntegrityViolationException e1) {
            System.out.println(e1.getMessage());
            e1.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }/* End of Purchase return Duties and Taxes Ledger */

    /**** Save Into Purchase Return Additional Charges    *****/
    public void saveIntoPurchaseAdditionalCharges(JsonArray additionalCharges,
                                                  TranxPurReturnInvoice mPurchaseTranx, Long tranxId, Long outletId) {
        List<TranxPurReturnInvoiceAddCharges> chargesList = new ArrayList<>();
        if (mPurchaseTranx.getAdditionalChargesTotal() > 0) {
            for (JsonElement mList : additionalCharges) {
                TranxPurReturnInvoiceAddCharges charges = new TranxPurReturnInvoiceAddCharges();
                JsonObject object = mList.getAsJsonObject();
                Double amount = object.get("amt").getAsDouble();
                Long ledgerId = object.get("ledger").getAsLong();
                LedgerMaster addcharges = ledgerMasterRepository.findByIdAndOutletIdAndStatus(ledgerId, outletId, true);
                charges.setAmount(amount);
                charges.setAdditionalCharges(addcharges);
                charges.setPurReturnInvoice(mPurchaseTranx);
                charges.setTranxPurInvoice(mPurchaseTranx.getTranxPurInvoice());
                charges.setStatus(true);
                charges.setOperation("inserted");
                chargesList.add(charges);
            }
        }
        try {
            tranxPurReturnAddChargesRepository.saveAll(chargesList);
        } catch (DataIntegrityViolationException e1) {
            System.out.println(e1.getMessage());
            e1.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }/* End of Purchase return Additional Charges */

    /****** save into Purchase Return Invoice Details ******/
    public void saveIntoPurchaseInvoiceDetails(JsonArray array, TranxPurReturnInvoice mPurchaseTranx,
                                               Branch branch, Outlet outlet,
                                               Long userId, TransactionTypeMaster tranxType, HttpServletRequest request) {
        /* Purchase Product Details Start here */
        Map<String, String[]> paramMap = request.getParameterMap();
        List<TranxPurReturnInvoiceDetails> row = new ArrayList<>();
        List<TranxPurReturnInvoiceProductSrNo> newSerialNumbers = new ArrayList<>();
        for (JsonElement mList : array) {
            JsonObject object = mList.getAsJsonObject();
            TranxPurReturnInvoiceDetails mDetails = new TranxPurReturnInvoiceDetails();
            mDetails.setPurReturnInvoice(mPurchaseTranx);
            mDetails.setTranxPurInvoice(mPurchaseTranx.getTranxPurInvoice());
            Product mProduct = productRepository.findByIdAndStatus(object.get("product_id").getAsLong(),
                    true);
            mDetails.setProduct(mProduct);
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
//            mDetails.setQtyHigh(object.get("qtyH").getAsDouble());
//            mDetails.setRateHigh(object.get("rateH").getAsDouble());
//            mDetails.setStatus(true);
//            mDetails.setQtyMedium(object.get("qtyM").getAsDouble());
//            mDetails.setRateMedium(object.get("rateM").getAsDouble());
//            mDetails.setQtyLow(object.get("qtyL").getAsDouble());
//            mDetails.setRateLow(object.get("rateL").getAsDouble());
//            mDetails.setBaseAmtHigh(object.get("base_amt_H").getAsDouble());
//            mDetails.setBaseAmtMedium(object.get("base_amt_M").getAsDouble());
//            mDetails.setBaseAmtLow(object.get("base_amt_L").getAsDouble());
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
            mDetails.setCreatedBy(userId);
            if (!object.get("packageId").getAsString().equalsIgnoreCase("")) {
                PackingMaster packingMaster = packingMasterRepository.findByIdAndStatus(object.get("packageId").getAsLong(), true);
                if (packingMaster != null)
                    mDetails.setPackingMaster(packingMaster);
            }
            row.add(mDetails);

//            TranxPurReturnInvoiceDetails newDetails = invoiceDetailsRepository.save(mDetails);
//            /* inserting into TranxPurchaseInvoiceDetailsUnits */
//            JsonArray unitDetails = object.getAsJsonArray("units");
//            for (JsonElement mUnits : unitDetails) {
//                JsonObject mObject = new JsonObject();
//                TranxPurInvoiceDetailsUnits invoiceUnits = new TranxPurInvoiceDetailsUnits();
//                invoiceUnits.setPurchaseTransaction(newDetails.getPurchaseTransaction());
//                invoiceUnits.setPurInvoiceDetails(newDetails);
//                invoiceUnits.setProduct(newDetails.getProduct());
//                Units unit = unitsRepository.findByIdAndStatus(mObject.get("unit_id").getAsLong(), true);
//                invoiceUnits.setUnits(unit);
//                invoiceUnits.setUnitConversions(mObject.get("unit_conv").getAsDouble());
//                invoiceUnits.setQty(mObject.get("qty").getAsDouble());
//                invoiceUnits.setRate(mObject.get("rate").getAsDouble());
//                invoiceUnits.setRate(mObject.get("base_amt").getAsDouble());
//                invoiceUnits.setStatus(true);
//                tranxPurInvoiceUnitsRepository.save(invoiceUnits);
//            }
            /* End of inserting into TranxPurchaseInvoiceDetailsUnits */

            TranxPurReturnInvoiceDetails newDetails = tranxPurReturnDetailsRepository.save(mDetails);
            /* inserting into TranxPurchaseInvoiceDetailsUnits */
            JsonArray unitDetails = object.getAsJsonArray("units");
            for (JsonElement mUnits : unitDetails) {
                JsonObject mObject = new JsonObject();
                TranxPurReturnDetailsUnits invoiceUnits = new TranxPurReturnDetailsUnits();
                invoiceUnits.setTranxPurReturnInvoice(newDetails.getPurReturnInvoice());
                invoiceUnits.setTranxPurReturnInvoiceDetails(newDetails);
                invoiceUnits.setProduct(newDetails.getProduct());
                Units unit = unitsRepository.findByIdAndStatus(mObject.get("unit_id").getAsLong(), true);
                invoiceUnits.setUnits(unit);
                invoiceUnits.setUnitConversions(mObject.get("unit_conv").getAsDouble());
                invoiceUnits.setQty(mObject.get("qty").getAsDouble());
                invoiceUnits.setRate(mObject.get("rate").getAsDouble());
                invoiceUnits.setRate(mObject.get("base_amt").getAsDouble());
                invoiceUnits.setStatus(true);
                invoiceUnits.setCreatedBy(userId);
                tranxPurReturnDetailsUnitRepository.save(invoiceUnits);
            }
            /* End of inserting into TranxPurchaseInvoiceDetailsUnits */


            JsonArray jsonArray = object.getAsJsonArray("serialNo");

            List<TranxPurReturnInvoiceProductSrNo> serialNumbers = new ArrayList<>();
            for (JsonElement jsonElement : jsonArray) {
                JsonObject json = jsonElement.getAsJsonObject();
                TranxPurReturnInvoiceProductSrNo productSerialNumber = new TranxPurReturnInvoiceProductSrNo();
                productSerialNumber.setProduct(mProduct);
                productSerialNumber.setSerialNo(json.get("no").getAsString());
                productSerialNumber.setPurReturnInvoice(mPurchaseTranx);
                productSerialNumber.setTranxPurInvoice(mPurchaseTranx.getTranxPurInvoice());
                productSerialNumber.setTransactionStatus("purchase");
                productSerialNumber.setStatus(true);
                productSerialNumber.setCreatedBy(userId);
                productSerialNumber.setOperations("inserted");
                if (mPurchaseTranx.getBranch() != null)
                    productSerialNumber.setBranch(mPurchaseTranx.getBranch());
                productSerialNumber.setOutlet(mPurchaseTranx.getOutlet());
                productSerialNumber.setTransactionTypeMaster(tranxType);
                serialNumbers.add(productSerialNumber);
            }
            try {
                newSerialNumbers = tranxPurReturnProdSrNoRepository.saveAll(serialNumbers);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            /*inventoryDTO.insertIntoInventoryTranxDetails(newDetails.getProduct(),
                    newDetails.getPurReturnInvoice().getTransactionDate(),
                    tranxType.getTransactionName(), newDetails.getQtyHigh(), newDetails.getQtyMedium(),
                    newDetails.getQtyLow(), "STOCKOUT", newDetails.getPurReturnInvoice().getId(),
                    newDetails.getPurReturnInvoice().getFinancialYear(),
                    newDetails.getPurReturnInvoice().getTransactionDate(),
                    newDetails.getPurReturnInvoice().getBranch(), newDetails.getPurReturnInvoice().getOutlet());*/
        }

        /*if (newSerialNumbers.size() > 0) {
            inventoryDTO.saveIntoSerialTranxSummaryDetailsPurReturn(
                    newSerialNumbers, tranxType.getTransactionName());
        }*/
    }/* End of Purchase return Details */

    /* Purchase Returns Last Records */
    public JsonObject purReturnsLastRecord(HttpServletRequest request) {

        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Long count = tranxPurReturnsRepository.findLastRecord(users.getOutlet().getId());
        //SQDEC00001
        String serailNo = String.format("%05d", count + 1);// 5 digit serial number
       /* String companyName = users.getOutlet().getCompanyName();
        companyName = companyName.substring(0, 3);*/ // fetching first 3 digits from company names
        /* getting Start and End year from fiscal Year */
    /*    String startYear = generateFiscalYear.getStartYear();
        String endYear = generateFiscalYear.getEndYear();*/
        //first 3 digits of Current month
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
     /*   String csCode = companyName.toUpperCase() + "-" + startYear + endYear
                + "-" + "PR" + currentMonth + "-" + serailNo;*/
        String csCode = "PR" + currentMonth + serailNo;
        JsonObject result = new JsonObject();
        result.addProperty("message", "success");
        result.addProperty("responseStatus", HttpStatus.OK.value());
        result.addProperty("count", count + 1);
        result.addProperty("purReturnNo", csCode);
        return result;
    }

    /* Postings of Ledgers while purchase returns*/
    private void insertIntoLedgerTranxDetails(TranxPurReturnInvoice mPurchaseTranx) {
        /* start of ledger trasaction details */
        TransactionTypeMaster tranxType = tranxRepository.findByTransactionNameIgnoreCase("purchase return");
//        generateTransactions.insertIntoTranxsDetails(mPurchaseTranx,tranxType);
        try {
            insertIntoTranxDetailSC(mPurchaseTranx, tranxType); // for Sundry Creditors : dr
            insertIntoTranxDetailPA(mPurchaseTranx, tranxType); // for Purchase Accounts : cr
            insertIntoTranxDetailPD(mPurchaseTranx, tranxType); // for Purchase Discounts : dr
            insertIntoTranxDetailRO(mPurchaseTranx, tranxType); // for Round Off : cr or dr
            insertDB(mPurchaseTranx, "AC", tranxType); // for Additional Charges : cr
            insertDB(mPurchaseTranx, "DT", tranxType); // for Duties and Taxes : cr
            /* end of ledger transaction details */
        } catch (Exception e) {
            System.out.println("Posting Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /* Insertion into Transaction Details Table of Sundry Creditors Ledgers for Purchase Invoice */
    public void insertIntoTranxDetailSC(TranxPurReturnInvoice mPurchaseTranx, TransactionTypeMaster tranxType) {
//        System.out.println(mPurchaseTranx);
        try {
            System.out.println("\nTotal Amount:" + mPurchaseTranx.getTotalAmount());

            transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(mPurchaseTranx.getSundryCreditors().
                            getFoundations().getId(), mPurchaseTranx.getSundryCreditors().getPrinciples().getId(),
                    mPurchaseTranx.getSundryCreditors().getPrincipleGroups().getId(),
                    mPurchaseTranx.getAssociateGroups() != null ? mPurchaseTranx.getAssociateGroups().getId() : null,
                    tranxType.getId(), mPurchaseTranx.getSundryCreditors().getBalancingMethod().getId(),
                    mPurchaseTranx.getBranch() != null ? mPurchaseTranx.getBranch().getId() : null,
                    mPurchaseTranx.getOutlet().getId(), "purchase return", mPurchaseTranx.getTotalAmount() * -1, 0.0,
                    mPurchaseTranx.getTransactionDate(), null, mPurchaseTranx.getId(), tranxType.getTransactionName(),
                    mPurchaseTranx.getSundryCreditors().getUnderPrefix(), mPurchaseTranx.getFinancialYear(),
                    mPurchaseTranx.getCreatedBy(), mPurchaseTranx.getSundryCreditors().getId(), mPurchaseTranx.getTranxPurInvoice().getVendorInvoiceNo());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Store Procedure Error " + e.getMessage());
        }
    }

    /* Insertion into Transaction Details Table of Purchase Accounts Ledgers for Purchase Invoice*/
    public void insertIntoTranxDetailPA(TranxPurReturnInvoice mPurchaseTranx, TransactionTypeMaster tranxType) {
        transactionDetailsRepository.insertIntoLegerTranxDetailsPosting
                (mPurchaseTranx.getPurchaseAccountLedger().getFoundations().getId(),
                        mPurchaseTranx.getPurchaseAccountLedger().getPrinciples().getId(),
                        mPurchaseTranx.getPurchaseAccountLedger().getPrincipleGroups() != null ?
                                mPurchaseTranx.getPurchaseAccountLedger().getPrincipleGroups().getId() : null,
                        mPurchaseTranx.getAssociateGroups() != null ?
                                mPurchaseTranx.getAssociateGroups().getId() : null, tranxType.getId(), null,
                        mPurchaseTranx.getBranch() != null ? mPurchaseTranx.getBranch().getId() : null,
                        mPurchaseTranx.getOutlet().getId(), "purchase returns ",
                        0.0, mPurchaseTranx.getTotalBaseAmount(),
                        mPurchaseTranx.getTransactionDate(), null, mPurchaseTranx.getId(),
                        tranxType.getTransactionName(),
                        mPurchaseTranx.getPurchaseAccountLedger().getUnderPrefix(),
                        mPurchaseTranx.getFinancialYear(), mPurchaseTranx.getCreatedBy(),
                        mPurchaseTranx.getPurchaseAccountLedger().getId(), mPurchaseTranx.getTranxPurInvoice().getVendorInvoiceNo());
    }

    /* Insertion into Transaction Details Table of Purchase Discount Ledgers for Purchase Invoice*/
    public void insertIntoTranxDetailPD(TranxPurReturnInvoice mPurchaseTranx, TransactionTypeMaster tranxType) {
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
                        mPurchaseTranx.getOutlet().getId(), "purchase returns", mPurchaseTranx.getTotalPurchaseDiscountAmt() * -1, 0.0,
                        mPurchaseTranx.getTransactionDate(), null, mPurchaseTranx.getId(), tranxType.getTransactionName(),
                        mPurchaseTranx.getPurchaseDiscountLedger().getUnderPrefix(), mPurchaseTranx.getFinancialYear(),
                        mPurchaseTranx.getCreatedBy(), mPurchaseTranx.getPurchaseDiscountLedger().getId(), mPurchaseTranx.getTranxPurInvoice().getVendorInvoiceNo());
            }
        } catch (Exception e) {
            System.out.println("Posting Discount Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /* Insertion into Transaction Details Table of Purchase RoundOff Ledgers for Purchase Invoice*/
    private void insertIntoTranxDetailRO(TranxPurReturnInvoice mPurchaseTranx,
                                         TransactionTypeMaster tranxType) {
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
                    mPurchaseTranx.getOutlet().getId(), "purchase returns",
                    0.0, mPurchaseTranx.getRoundOff(), mPurchaseTranx.getTransactionDate(), null, mPurchaseTranx.getId(),
                    tranxType.getTransactionName(), mPurchaseTranx.getPurchaseRoundOff().getUnderPrefix(),
                    mPurchaseTranx.getFinancialYear(), mPurchaseTranx.getCreatedBy(),
                    mPurchaseTranx.getPurchaseRoundOff().getId(), mPurchaseTranx.getTranxPurInvoice().getVendorInvoiceNo());
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
                    mPurchaseTranx.getOutlet().getId(), "purchase returns", Math.abs(mPurchaseTranx.getRoundOff()), 0.0,
                    mPurchaseTranx.getTransactionDate(), null, mPurchaseTranx.getId(),
                    tranxType.getTransactionName(), mPurchaseTranx.getPurchaseRoundOff().getUnderPrefix(),
                    mPurchaseTranx.getFinancialYear(),
                    mPurchaseTranx.getCreatedBy(), mPurchaseTranx.getPurchaseRoundOff().getId(), mPurchaseTranx.getTranxPurInvoice().getVendorInvoiceNo());
        }
    }

    public void insertDB(TranxPurReturnInvoice mPurchaseTranx, String ledgerName, TransactionTypeMaster tranxType) {

        /* Purchase Duties Taxes */
        if (ledgerName.equalsIgnoreCase("DT")) {
            List<TranxPurReturnInvoiceDutiesTaxes> list = new ArrayList<>();
            list = tranxPurReturnDutiesTaxesRepository.findByPurReturnInvoiceAndStatus(
                    mPurchaseTranx, true);
            if (list.size() > 0) {
                for (TranxPurReturnInvoiceDutiesTaxes mDuties : list) {
                    insertFromDutiesTaxes(mDuties, mPurchaseTranx, tranxType);
                }
            }
        } else if (ledgerName.equalsIgnoreCase("AC")) {
            /* Purchase Additional Charges */
            List<TranxPurReturnInvoiceAddCharges> list = new ArrayList<>();
            list = tranxPurReturnAddChargesRepository.findByPurReturnInvoiceIdAndStatus(mPurchaseTranx.getId(), true);
            if (list.size() > 0) {
                for (TranxPurReturnInvoiceAddCharges mAdditinoalCharges : list) {
                    insertFromAdditionalCharges(mAdditinoalCharges,
                            mPurchaseTranx, tranxType);
                }
            }
        }
    }

    public void insertFromDutiesTaxes(TranxPurReturnInvoiceDutiesTaxes mDuties,
                                      TranxPurReturnInvoice mPurchaseTranx, TransactionTypeMaster tranxType) {
        transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                mDuties.getDutiesTaxes().getPrinciples().getFoundations().getId(),
                mDuties.getDutiesTaxes().getPrinciples().getId(),
                mDuties.getDutiesTaxes().getPrincipleGroups() != null ?
                        mDuties.getDutiesTaxes().getPrincipleGroups().getId() : null,
                mPurchaseTranx.getAssociateGroups() != null ?
                        mPurchaseTranx.getAssociateGroups().getId() : null,
                tranxType.getId(), null,
                mPurchaseTranx.getBranch() != null ? mPurchaseTranx.getBranch().getId() : null,
                mPurchaseTranx.getOutlet().getId(), "pending", 0.0,
                mDuties.getAmount(), mPurchaseTranx.getTransactionDate(), null,
                mPurchaseTranx.getId(), tranxType.getTransactionName(),
                mDuties.getDutiesTaxes().getUnderPrefix(),
                mPurchaseTranx.getFinancialYear(), mPurchaseTranx.getCreatedBy(),
                mDuties.getDutiesTaxes().getId(), mPurchaseTranx.getTranxPurInvoice().getVendorInvoiceNo());
    }

    public void insertFromAdditionalCharges(TranxPurReturnInvoiceAddCharges mAdditinoalCharges,
                                            TranxPurReturnInvoice mPurchaseTranx, TransactionTypeMaster tranxType) {
        transactionDetailsRepository.insertIntoLegerTranxDetailsPosting(
                mAdditinoalCharges.getAdditionalCharges().getPrinciples().getFoundations().getId(),
                mAdditinoalCharges.getAdditionalCharges().getPrinciples().getId(),
                mAdditinoalCharges.getAdditionalCharges().getPrincipleGroups() != null ?
                        mAdditinoalCharges.getAdditionalCharges().getPrincipleGroups().getId() : null,
                mPurchaseTranx.getAssociateGroups() != null ?
                        mPurchaseTranx.getAssociateGroups().getId() : null,
                tranxType.getId(), null,
                mPurchaseTranx.getBranch() != null ? mPurchaseTranx.getBranch().getId() : null,
                mPurchaseTranx.getOutlet().getId(), "pending", 0.0,
                mAdditinoalCharges.getAmount(), mPurchaseTranx.getTransactionDate(), null,
                mPurchaseTranx.getId(), tranxType.getTransactionName(),
                mAdditinoalCharges.getAdditionalCharges().getUnderPrefix(),
                mPurchaseTranx.getFinancialYear(), mPurchaseTranx.getCreatedBy(),
                mAdditinoalCharges.getAdditionalCharges().getId(), mPurchaseTranx.getTranxPurInvoice().getVendorInvoiceNo());
    }


    /* find all Purchase Invoices and Purchase Challans of Sundry Creditors/Suppliers wise , for Purchase Returns */
    public JsonObject purchaseListSupplierWise(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Map<String, String[]> paramMap = request.getParameterMap();
        JsonArray result = new JsonArray();
        List<TranxPurInvoice> purInvoice = new ArrayList<>();
        List<TranxPurChallan> purChallans = new ArrayList<>();
        Long ledgerId = Long.parseLong(request.getParameter("sundry_creditor_id"));
        if (paramMap.containsKey("dateFrom") && paramMap.containsKey("dateTo")) {
            purInvoice = tranxPurInvoiceRepository.findBySuppliersWithDates(
                    users.getOutlet().getId(), true,
                    ledgerId, request.getParameter("dateFrom"), request.getParameter("dateTo"));
        } else {
            purInvoice = tranxPurInvoiceRepository.findByOutletIdAndStatusAndSundryCreditorsId(
                    users.getOutlet().getId(), true, ledgerId);
        }
        if (purInvoice != null && purInvoice.size() > 0) {
            for (TranxPurInvoice invoices : purInvoice) {
                JsonObject response = new JsonObject();
                response.addProperty("source", "pur_invoice");
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
                response.addProperty("sundry_creditor_id", ledgerId);
                result.add(response);
            }
        }
        /* challan list */
        if (paramMap.containsKey("dateFrom") && paramMap.containsKey("dateTo")) {
            purChallans = tranxPurChallanRepository.findBySuppliersWithDates(
                    users.getOutlet().getId(), true,
                    ledgerId, request.getParameter("dateFrom"), request.getParameter("dateTo"));
        } else {
            purChallans = tranxPurChallanRepository.
                    findBySundryCreditorsIdAndOutletIdAndTransactionStatusIdAndStatus(
                            ledgerId, users.getOutlet().getId(), 1L, true);
        }
        if (purChallans != null && purChallans.size() > 0) {
            for (TranxPurChallan invoices : purChallans) {
                JsonObject response = new JsonObject();
                response.addProperty("source", "pur_challan");
                response.addProperty("id", invoices.getId());
                response.addProperty("invoice_no", invoices.getVendorInvoiceNo());
                response.addProperty("invoice_date", invoices.getInvoiceDate().toString());
                response.addProperty("transaction_date", invoices.getTransactionDate().toString());
                response.addProperty("purchase_serial_number", invoices.getPurChallanSrno());
                response.addProperty("total_amount", Math.abs(invoices.getTotalAmount()));
                if (invoices.getTotalAmount() > 0) {
                    response.addProperty("balance_type", "CR");
                } else {
                    response.addProperty("balance_type", "DR");
                }
                response.addProperty("sundry_creditor_name",
                        invoices.getSundryCreditors().getLedgerName());
                response.addProperty("sundry_creditor_id", ledgerId);
                result.add(response);
            }
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);
        return output;
    }

    /* Purchase Returns:  find all products of selected purchase invoice bill of sundry creditor */
    public JsonObject productListPurInvoice(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
       /* JsonArray result = new JsonArray();
        TranxPurInvoice purInvoice = tranxPurInvoiceRepository.findByIdAndStatus(Long.parseLong(
                request.getParameter("pur_invoice_id")), true);
        List<TranxPurInvoiceDetails> productList = invoiceDetailsRepository.findByPurchaseTransactionIdAndStatus(
                Long.parseLong(request.getParameter("pur_invoice_id")), true);
        for (TranxPurInvoiceDetails prodList : productList) {
            JsonObject response = new JsonObject();
            response.addProperty("id", prodList.getId());
            response.addProperty("invoice_id", prodList.getPurchaseTransaction().getId());
            response.addProperty("product_id", prodList.getProduct().getId());
            response.addProperty("purchase_name",
                    prodList.getProduct().getProductName());
            List<ProductUnit> productUnits = productUnitRepository.
                    findByProductId(prodList.getProduct().getId());
            response.addProperty("High Unit",
                    prodList.getQtyHigh() != 0 ? productUnits.get(0).getUnitType() : "NA");
            response.addProperty("Medium Unit",
                    prodList.getQtyMedium() != 0 ? productUnits.get(1).getUnitType() : "NA");
            response.addProperty("Low Unit",
                    prodList.getQtyLow() != 0 ? productUnits.get(2).getUnitType() : "NA");
            response.addProperty("Total Price", purInvoice.getTotalAmount());

            result.add(response);
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);*/
        // return output;
        List<TranxPurInvoiceDetails> list = new ArrayList<>();
        List<TranxPurchaseInvoiceProductSrNumber> serialNumbers = new ArrayList<>();
        List<TranxPurInvoiceAdditionalCharges> additionalCharges = new ArrayList<>();
        JsonObject finalResult = new JsonObject();
        try {
            Long id = Long.parseLong(request.getParameter("pur_invoice_id"));
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
            result.addProperty("supplier_name", purchaseInvoice.getSundryCreditors().getLedgerName());
            result.addProperty("transaction_dt", purchaseInvoice.getTransactionDate().toString());
            /* End of Purchase Invoice Data */

            /* Purchase Invoice Details */
            JsonArray row = new JsonArray();
            if (list.size() > 0) {
                for (TranxPurInvoiceDetails mDetails : list) {
                    JsonObject prDetails = new JsonObject();
                    prDetails.addProperty("details_id", mDetails.getId());
                    prDetails.addProperty("product_name", mDetails.getProduct().getProductName());
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
                    row.add(prDetails);
                }
            }
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

    /* list of all selected products against purchase invoice bill for purchase returns */
    public JsonObject getInvoiceByIdWithProductsId(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<TranxPurInvoiceDetails> list = new ArrayList<>();
        List<TranxPurchaseInvoiceProductSrNumber> serialNumbers = new ArrayList<>();
        List<TranxPurInvoiceAdditionalCharges> additionalCharges = new ArrayList<>();
        JsonObject finalResult = new JsonObject();
        try {
            Long id = Long.parseLong(request.getParameter("invoice_id"));
            TranxPurInvoice purchaseInvoice = tranxPurInvoiceRepository.findByIdAndOutletIdAndStatus(
                    id, users.getOutlet().getId(), true);
            String str = request.getParameter("product_ids");
            list = invoiceDetailsRepository.findInvoiceByIdWithProductsId(id, true, str);
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
                    row.add(prDetails);
                }
            }
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

    public JsonObject purReturnsByOutlet(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        List<TranxPurReturnInvoice> purInvoice = tranxPurReturnsRepository.findByOutletIdAndStatusOrderByIdDesc(
                users.getOutlet().getId(), true);
        for (TranxPurReturnInvoice invoices : purInvoice) {
            JsonObject response = new JsonObject();
            response.addProperty("id", invoices.getId());
            response.addProperty("pur_return_no", invoices.getPurRtnNo());
            response.addProperty("transaction_date", invoices.getTransactionDate().toString());
            response.addProperty("purchase_return_serial_number", invoices.getPurReturnSrno());
            response.addProperty("total_amount", invoices.getTotalAmount());
            response.addProperty("sundry_creditor_name",
                    invoices.getSundryCreditors().getLedgerName());
            response.addProperty("sundry_creditor_id", invoices.getSundryCreditors().getId());
            response.addProperty("purchase_account_name",
                    invoices.getPurchaseAccountLedger().getLedgerName());
            if (invoices.getTranxPurInvoice() != null)
                response.addProperty("invoice_no", invoices.getTranxPurInvoice().getVendorInvoiceNo());
            if (invoices.getTranxPurChallan() != null)
                response.addProperty("invoice_no", invoices.getTranxPurChallan().getVendorInvoiceNo());
            result.add(response);
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);
        return output;
    }
}
