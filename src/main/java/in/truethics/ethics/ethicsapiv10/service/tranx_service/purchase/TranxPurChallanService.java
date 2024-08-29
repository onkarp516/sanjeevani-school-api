package in.truethics.ethics.ethicsapiv10.service.tranx_service.purchase;

import com.google.gson.*;
import in.truethics.ethics.ethicsapiv10.common.GenerateCoreLogic;
import in.truethics.ethics.ethicsapiv10.common.GenerateDates;
import in.truethics.ethics.ethicsapiv10.common.GenerateFiscalYear;
import in.truethics.ethics.ethicsapiv10.common.NumFormat;
import in.truethics.ethics.ethicsapiv10.model.inventory.PackingMaster;
import in.truethics.ethics.ethicsapiv10.model.inventory.Product;
import in.truethics.ethics.ethicsapiv10.model.inventory.Units;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.*;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.inventory_repository.PackingMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.inventory_repository.ProductRepository;
import in.truethics.ethics.ethicsapiv10.repository.inventory_repository.ProductUnitRepository;
import in.truethics.ethics.ethicsapiv10.repository.inventory_repository.UnitsRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerBalanceSummaryRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerTransactionDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.FiscalYearRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.TransactionStatusRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.TransactionTypeMasterRepository;
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
public class TranxPurChallanService {

    private static final Logger purChallanLogger = LoggerFactory.getLogger(TranxPurChallanService.class);
    @PersistenceContext
    EntityManager entityManager;
    List<Long> dbList = new ArrayList<>(); // for saving all ledgers Id against Purchase invoice
    List<Long> mInputList = new ArrayList<>();
    @Autowired
    private TranxPurOrderRepository tranxPurOrderRepository;
    @Autowired
    private TranxPurchaseChallanProductSrNumberRepository tranxPurchaseChallanProductSrNumberRepository;
    @Autowired
    private TranxPurChallanRepository tranxPurChallanRepository;
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LedgerMasterRepository ledgerMasterRepository;
    @Autowired
    private LedgerBalanceSummaryRepository balanceSummaryRepository;
    @Autowired
    private LedgerTransactionDetailsRepository transactionDetailsRepository;
    @Autowired
    private TranxPurChallanDutiesTaxesRepository tranxPurChallanDutiesTaxesRepository;
    //    @Autowired
//    private TransactionTypeMasterDetailsRepository tranxMasterDetailsRepository;
    @Autowired
    private TranxPurChallanDetailsRepository tranxPurChallanDetailsRepository;
    @Autowired
    private TransactionTypeMasterRepository tranxRepository;
    @Autowired
    private GenerateCoreLogic generateCoreLogic;
    @Autowired
    private GenerateFiscalYear generateFiscalYear;
    @Autowired
    private FiscalYearRepository fiscalYearRepository;
    /* @Autowired
     private InventoryDTO inventoryDTO;*/
    @Autowired
    private TransactionStatusRepository transactionStatusRepository;
    @Autowired
    private ProductUnitRepository productUnitRepository;
    @Autowired
    private NumFormat numFormat;
    @Autowired
    private TranxPurOrderDetailsRepository tranxPurOrderDetailsRepository;
    @Autowired
    private PackingMasterRepository packingMasterRepository;
    @Autowired
    private TranxPurChallanDetailsUnitRepository tranxPurChallanDetailsUnitRepository;
    @Autowired
    private UnitsRepository unitsRepository;

    public Object insertPOChallanInvoice(HttpServletRequest request) {
        TranxPurChallan mPOChallanTranx = null;
        ResponseMessage responseMessage = new ResponseMessage();
        mPOChallanTranx = saveIntoPOChallanInvoice(request);
        if (mPOChallanTranx != null) {

            responseMessage.setMessage("Purchase challan created successfully");
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        } else {
            responseMessage.setMessage("Error in purchase invoice creation");
            responseMessage.setResponseStatus(HttpStatus.FORBIDDEN.value());
        }
        return responseMessage;
    }

    /*******  Save into Purchase Challan    *********/
    /******* @return *******/
    public TranxPurChallan saveIntoPOChallanInvoice(HttpServletRequest request) {
        TranxPurChallan mPOChallanTranx = null;
        TransactionTypeMaster tranxType = null;
        Map<String, String[]> paramMap = request.getParameterMap();
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Branch branch = null;
        Outlet outlet = users.getOutlet();
        TranxPurChallan tranxPurChallan = new TranxPurChallan();
        tranxPurChallan.setCreatedBy(users.getId());
        if (users.getBranch() != null) {
            branch = users.getBranch();
            tranxPurChallan.setBranch(branch);
        }
        tranxPurChallan.setOutlet(outlet);
        TransactionStatus transactionStatus = transactionStatusRepository.findByStatusNameAndStatus("opened", true);
        tranxPurChallan.setTransactionStatus(transactionStatus);
        if (paramMap.containsKey("reference_ids")) {
            //purchase order id mapped into reference ids
            tranxPurChallan.setOrderReference(request.getParameter("reference_ids"));
            //  type of reference e.g purchase order
            tranxPurChallan.setReferenceType(request.getParameter("reference_type"));
            // closing all references of purchase order ids
            //     setClosePO(request.getParameter("reference_ids"));
        }
        tranxType = tranxRepository.findByTransactionNameIgnoreCase("purchase challan");
        LocalDate date = LocalDate.parse(request.getParameter("pur_challan_date"));
        tranxPurChallan.setInvoiceDate(date);
        /* fiscal year mapping */
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(date);
        if (fiscalYear != null) {
            tranxPurChallan.setFiscalYear(fiscalYear);
            tranxPurChallan.setFinancialYear(fiscalYear.getFiscalYear());
        }
        /* End of fiscal year mapping */
        tranxPurChallan.setVendorInvoiceNo(request.getParameter("pur_challan_no"));
        tranxPurChallan.setPurChallanSrno(Long.parseLong(request.getParameter("pur_challan_sr_no")));
        LedgerMaster purchaseAccount = ledgerMasterRepository.findByIdAndStatus(Long.parseLong(
                request.getParameter("purchase_id")), true);

        LedgerMaster sundryCreditors = ledgerMasterRepository.findByIdAndStatus(
                Long.parseLong(request.getParameter("supplier_code_id")),
                true);
        tranxPurChallan.setPurchaseAccountLedger(purchaseAccount);
        tranxPurChallan.setSundryCreditors(sundryCreditors);
        LocalDate mDate = LocalDate.parse(request.getParameter("transaction_date"));
        tranxPurChallan.setTransactionDate(mDate);

        tranxPurChallan.setTotalBaseAmount(Double.parseDouble(request.getParameter("total_base_amt")));
        LedgerMaster roundoff = ledgerMasterRepository.findByOutletIdAndLedgerNameIgnoreCase(outlet.getId(), "Round off");
        tranxPurChallan.setRoundOff(Double.parseDouble(request.getParameter("roundoff")));
        tranxPurChallan.setPurchaseRoundOff(roundoff);

        tranxPurChallan.setTotalAmount(Double.parseDouble(request.getParameter("totalamt")));
        Boolean taxFlag = Boolean.parseBoolean(request.getParameter("taxFlag"));
        /* if true : cgst and sgst i.e intra state */
        if (taxFlag) {
            tranxPurChallan.setTotalcgst(Double.parseDouble(request.getParameter("totalcgst")));
            tranxPurChallan.setTotalsgst(Double.parseDouble(request.getParameter("totalsgst")));
            tranxPurChallan.setTotaligst(0.0);
        }
        /* if false : igst i.e inter state */
        else {
            tranxPurChallan.setTotalcgst(0.0);
            tranxPurChallan.setTotalsgst(0.0);
            tranxPurChallan.setTotaligst(Double.parseDouble(request.getParameter("totaligst")));
        }
        tranxPurChallan.setTotalqty(Long.parseLong(request.getParameter("totalqty")));
        if (paramMap.containsKey("tcs"))
            tranxPurChallan.setTcs(Double.parseDouble(request.getParameter("tcs")));
        else {
            tranxPurChallan.setTcs(0.0);
        }
        tranxPurChallan.setTaxableAmount(Double.parseDouble(request.getParameter("taxable_amount")));
        tranxPurChallan.setStatus(true);
        tranxPurChallan.setOperations("inserted");
        if (paramMap.containsKey("narration"))
            tranxPurChallan.setNarration(request.getParameter("narration"));
        else {
            tranxPurChallan.setNarration("NA");
        }
        try {
            mPOChallanTranx = tranxPurChallanRepository.save(tranxPurChallan);
            if (mPOChallanTranx != null) {
                /* Save into Duties and Taxes */
                String taxStr = request.getParameter("taxCalculation");
                JsonObject duties_taxes = new Gson().fromJson(taxStr, JsonObject.class);
                saveIntoPOChallanDutiesTaxes(duties_taxes, mPOChallanTranx, taxFlag);
                /* save into Additional Charges  */
                String jsonStr = request.getParameter("row");
                JsonParser parser = new JsonParser();
                JsonElement purDetailsJson = parser.parse(jsonStr);
                JsonArray array = purDetailsJson.getAsJsonArray();
                String referenceObject = request.getParameter("refObject");
                saveIntoPOChallanInvoiceDetails(array, mPOChallanTranx,
                        branch, outlet, users.getId(), tranxType, referenceObject);
            }
        } catch (DataIntegrityViolationException e) {
            purChallanLogger.error("DataIntegrityViolationException in saveIntoPOChallanInvoice:" + e.getMessage());
            System.out.println("Exception:" + e.getMessage());

        } catch (Exception e1) {
            purChallanLogger.error("Exception in saveIntoPOChallanInvoice:" + e1.getMessage());
            System.out.println("Exception:" + e1.getMessage());
        }
        return tranxPurChallan;
    }

    /* End of Purchase Invoice */
    /* Close All Purchase Orders */
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

    /****** Save into Duties and Taxes ******/
    public void saveIntoPOChallanDutiesTaxes(JsonObject duties_taxes, TranxPurChallan mPOChallanTranx,
                                             Boolean taxFlag) {
        List<TranxPurChallanDutiesTaxes> tranxPurChallanDutiesTaxes = new ArrayList<>();
        try {
            if (taxFlag) {
                JsonArray cgstList = duties_taxes.getAsJsonArray("cgst");
                JsonArray sgstList = duties_taxes.getAsJsonArray("sgst");
                /* this is for Cgst creation */
                if (cgstList.size() > 0) {
                    for (JsonElement mList : cgstList) {
                        TranxPurChallanDutiesTaxes tranxPurChallanDutiesTaxes1 = new TranxPurChallanDutiesTaxes();
                        JsonObject cgstObject = mList.getAsJsonObject();
                        LedgerMaster dutiesTaxes = null;
                        String inputGst = cgstObject.get("gst").getAsString();
                        String ledgerName = "INPUT CGST " + inputGst;
                        dutiesTaxes = ledgerMasterRepository.findByOutletIdAndLedgerNameIgnoreCase(mPOChallanTranx.getOutlet().getId(),
                                ledgerName);
                        if (dutiesTaxes != null) {
                            //   dutiesTaxesLedger.setDutiesTaxes(dutiesTaxes);
                            tranxPurChallanDutiesTaxes1.setDutiesTaxes(dutiesTaxes);
                        }
                        tranxPurChallanDutiesTaxes1.setAmount(Double.parseDouble(cgstObject.get("amt").getAsString()));
                        tranxPurChallanDutiesTaxes1.setTranxPurChallan(mPOChallanTranx);
                        tranxPurChallanDutiesTaxes1.setSundryCreditors(mPOChallanTranx.getSundryCreditors());
                        tranxPurChallanDutiesTaxes1.setIntra(taxFlag);
                        tranxPurChallanDutiesTaxes1.setStatus(true);
                        tranxPurChallanDutiesTaxes1.setCreatedBy(mPOChallanTranx.getCreatedBy());

                        tranxPurChallanDutiesTaxes.add(tranxPurChallanDutiesTaxes1);
                    }
                }
                /* this is for Sgst creation */
                if (sgstList.size() > 0) {
                    for (JsonElement mList : sgstList) {
                        TranxPurChallanDutiesTaxes taxes = new TranxPurChallanDutiesTaxes();
                        JsonObject sgstObject = mList.getAsJsonObject();
                        LedgerMaster dutiesTaxes = null;
                        String inputGst = sgstObject.get("gst").getAsString();
                        String ledgerName = "INPUT SGST " + inputGst;
                        dutiesTaxes = ledgerMasterRepository.findByOutletIdAndLedgerNameIgnoreCase(mPOChallanTranx.getOutlet().getId(),
                                ledgerName);
                        if (dutiesTaxes != null) {
                            taxes.setDutiesTaxes(dutiesTaxes);
                        }
                        taxes.setAmount(Double.parseDouble(sgstObject.get("amt").getAsString()));
                        taxes.setTranxPurChallan(mPOChallanTranx);
                        taxes.setSundryCreditors(mPOChallanTranx.getSundryCreditors());
                        taxes.setIntra(taxFlag);
                        taxes.setStatus(true);
                        taxes.setCreatedBy(mPOChallanTranx.getCreatedBy());

                        tranxPurChallanDutiesTaxes.add(taxes);
                    }
                }
            } else {
                JsonArray igstList = duties_taxes.getAsJsonArray("igst");
                /* this is for Igst creation */
                if (igstList.size() > 0) {
                    for (JsonElement mList : igstList) {
                        TranxPurChallanDutiesTaxes tranxPurChallanDutiesTaxes1 = new TranxPurChallanDutiesTaxes();
                        JsonObject igstObject = mList.getAsJsonObject();
                        LedgerMaster dutiesTaxes = null;
                        String inputGst = igstObject.get("gst").getAsString();
                        String ledgerName = "INPUT IGST " + inputGst;
                        dutiesTaxes = ledgerMasterRepository.findByOutletIdAndLedgerNameIgnoreCase(mPOChallanTranx.getOutlet().getId(),
                                ledgerName);
                        if (dutiesTaxes != null) {
                            tranxPurChallanDutiesTaxes1.setDutiesTaxes(dutiesTaxes);
                        }
                        tranxPurChallanDutiesTaxes1.setAmount(Double.parseDouble(igstObject.get("amt").getAsString()));
                        tranxPurChallanDutiesTaxes1.setTranxPurChallan(mPOChallanTranx);
                        tranxPurChallanDutiesTaxes1.setSundryCreditors(mPOChallanTranx.getSundryCreditors());
                        tranxPurChallanDutiesTaxes1.setIntra(taxFlag);
                        tranxPurChallanDutiesTaxes1.setStatus(true);
                        tranxPurChallanDutiesTaxes1.setCreatedBy(mPOChallanTranx.getCreatedBy());
                        tranxPurChallanDutiesTaxes.add(tranxPurChallanDutiesTaxes1);
                    }
                }
            }

            /* save all Duties and Taxes into purchase Invoice Duties taxes table */
            tranxPurChallanDutiesTaxesRepository.saveAll(tranxPurChallanDutiesTaxes);
        } catch (DataIntegrityViolationException e) {
            purChallanLogger.error("DataIntegrityViolationException in saveIntoPOChallanDutiesTaxes: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception e1) {
            purChallanLogger.error("Exception in saveIntoPOChallanDutiesTaxes: " + e1.getMessage());
            System.out.println(e1.getMessage());
            e1.printStackTrace();
        }
    }
    /* End of Purchase Duties and Taxes Ledger */


    /****** save into Purchase Invoice Details ******/
    public void saveIntoPOChallanInvoiceDetails(JsonArray array, TranxPurChallan mTranxPurChallan,
                                                Branch branch, Outlet outlet,
                                                Long userId, TransactionTypeMaster tranxType, String referenceObject) {

        /* Purchase Product Details Start here */
        try {
            List<TranxPurChallanDetails> row = new ArrayList<>();
            List<TranxPurchaseChallanProductSrNumber> newSerialNumbers = new ArrayList<>();
            for (JsonElement mList : array) {
                JsonObject object = mList.getAsJsonObject();
                TranxPurChallanDetails tranxPurChallanDetails = new TranxPurChallanDetails();
                tranxPurChallanDetails.setTranxPurChallan(mTranxPurChallan);
                tranxPurChallanDetails.setTransactionType(tranxType);
                Product mProduct = productRepository.findByIdAndStatus(object.get("product_id").getAsLong(),
                        true);
                tranxPurChallanDetails.setProduct(mProduct);
                tranxPurChallanDetails.setBase_amt(object.get("base_amt").getAsDouble());
                if (!object.get("dis_amt").getAsString().equals("")) {
                    System.out.println(object.get("dis_amt").getAsDouble());
                    tranxPurChallanDetails.setDiscountAmount(object.get("dis_amt").getAsDouble());
                } else {
                    tranxPurChallanDetails.setDiscountAmount((double) 0);
                }
                if (!object.get("dis_per").getAsString().equals("")) {

                    tranxPurChallanDetails.setDiscountPer(object.get("dis_per").getAsDouble());
                } else {
                    tranxPurChallanDetails.setDiscountPer((double) 0);
                }
                if (!object.get("dis_per_cal").getAsString().equals("")) {
                    tranxPurChallanDetails.setDiscountPerCal(object.get("dis_per_cal").getAsDouble());
                } else {
                    tranxPurChallanDetails.setDiscountPerCal((double) 0);
                }
                if (!object.get("dis_amt_cal").getAsString().equals("")) {

                    tranxPurChallanDetails.setDiscountAmountCal(object.get("dis_amt_cal").getAsDouble());
                } else {
                    tranxPurChallanDetails.setDiscountAmountCal((double) 0);
                }
                tranxPurChallanDetails.setTotalAmount(object.get("total_amt").getAsDouble());
                tranxPurChallanDetails.setIgst(object.get("igst").getAsDouble());
                tranxPurChallanDetails.setSgst(object.get("sgst").getAsDouble());
                tranxPurChallanDetails.setCgst(object.get("cgst").getAsDouble());
                tranxPurChallanDetails.setTotalIgst(object.get("total_igst").getAsDouble());
                tranxPurChallanDetails.setTotalSgst(object.get("total_sgst").getAsDouble());
                tranxPurChallanDetails.setTotalCgst(object.get("total_cgst").getAsDouble());
                tranxPurChallanDetails.setFinalAmount(object.get("final_amt").getAsDouble());
//                tranxPurChallanDetails.setQtyHigh(Double.parseDouble(object.get("qtyH").getAsString()));
//                tranxPurChallanDetails.setRateHigh(Double.parseDouble(object.get("rateH").getAsString()));
//                tranxPurChallanDetails.setStatus(true);
//                if (!object.get("qtyM").getAsString().equals("")) {
//                    tranxPurChallanDetails.setQtyMedium(Double.parseDouble(object.get("qtyM").getAsString()));
//                } else {
//                    tranxPurChallanDetails.setQtyMedium((double) 0);
//                }
//                if (!object.get("rateM").getAsString().equals("")) {
//                    tranxPurChallanDetails.setRateMedium(Double.parseDouble(object.get("rateM").getAsString()));
//                } else {
//                    tranxPurChallanDetails.setRateMedium((double) 0);
//                }
//                if (!object.get("qtyL").getAsString().equals("")) {
//                    tranxPurChallanDetails.setQtyLow(Double.parseDouble(object.get("qtyL").getAsString()));
//                } else {
//                    tranxPurChallanDetails.setQtyLow((double) 0);
//                }
//                if (!object.get("rateL").getAsString().equals("")) {
//                    tranxPurChallanDetails.setRateLow(Double.parseDouble(object.get("rateL").getAsString()));
//                } else {
//                    tranxPurChallanDetails.setRateLow((double) 0);
//                }
//                tranxPurChallanDetails.setBaseAmtHigh(Double.parseDouble(object.get("base_amt_H").getAsString()));
//                if (!object.get("base_amt_M").getAsString().equals("")) {
//                    tranxPurChallanDetails.setBaseAmtMedium(Double.parseDouble(object.get("base_amt_M").getAsString()));
//                } else {
//                    tranxPurChallanDetails.setBaseAmtMedium((double) 0);
//                }
//                if (!object.get("base_amt_L").getAsString().equals("")) {
//                    tranxPurChallanDetails.setBaseAmtLow(Double.parseDouble(object.get("base_amt_L").getAsString()));
//                } else {
//                    tranxPurChallanDetails.setBaseAmtLow((double) 0);
//                }
                if (object.has("qtyH"))
                    tranxPurChallanDetails.setQtyHigh(object.get("qtyH").getAsDouble());
                if (object.has("rateH"))
                    tranxPurChallanDetails.setRateHigh(object.get("rateH").getAsDouble());
                tranxPurChallanDetails.setStatus(true);
                tranxPurChallanDetails.setTranxPurChallan(mTranxPurChallan);
                if (object.has("qtyM"))
                    tranxPurChallanDetails.setQtyMedium(object.get("qtyM").getAsDouble());
                if (object.has("rateM"))
                    tranxPurChallanDetails.setRateMedium(object.get("rateM").getAsDouble());
                if (object.has("qtyL"))
                    tranxPurChallanDetails.setQtyLow(object.get("qtyL").getAsDouble());
                if (object.has("rateL"))
                    tranxPurChallanDetails.setRateLow(object.get("rateL").getAsDouble());
                if (object.has("base_amt_H"))
                    tranxPurChallanDetails.setBaseAmtHigh(object.get("base_amt_H").getAsDouble());
                if (object.has("base_amt_M"))
                    tranxPurChallanDetails.setBaseAmtMedium(object.get("base_amt_M").getAsDouble());
                if (object.has("base_amt_L"))
                    tranxPurChallanDetails.setBaseAmtLow(object.get("base_amt_L").getAsDouble());
                tranxPurChallanDetails.setCreatedBy(userId);
                if (!object.get("packageId").getAsString().equalsIgnoreCase("")) {
                    PackingMaster packingMaster = packingMasterRepository.findByIdAndStatus(object.get("packageId").getAsLong(), true);
                    if (packingMaster != null)
                        tranxPurChallanDetails.setPackingMaster(packingMaster);
                }
                row.add(tranxPurChallanDetails);
                TranxPurChallanDetails newChallanDetails = tranxPurChallanDetailsRepository.save(tranxPurChallanDetails);
                /* inserting into TranxPurchaseInvoiceDetailsUnits */
                JsonArray unitDetails = object.getAsJsonArray("units");
                for (JsonElement mUnits : unitDetails) {
                    JsonObject mObject = mUnits.getAsJsonObject();
                    TranxPurChallanDetailsUnits invoiceUnits = new TranxPurChallanDetailsUnits();
                    invoiceUnits.setTranxPurChallan(newChallanDetails.getTranxPurChallan());
                    invoiceUnits.setTranxPurChallanDetails(newChallanDetails);
                    invoiceUnits.setProduct(newChallanDetails.getProduct());
                    Units unit = unitsRepository.findByIdAndStatus(mObject.get("unit_id").getAsLong(), true);
                    invoiceUnits.setUnits(unit);
                    invoiceUnits.setUnitConversions(mObject.get("unit_conv").getAsDouble());
                    invoiceUnits.setQty(mObject.get("qty").getAsDouble());
                    invoiceUnits.setRate(mObject.get("rate").getAsDouble());
                    invoiceUnits.setBaseAmt(mObject.get("base_amt").getAsDouble());
                    invoiceUnits.setStatus(true);
                    invoiceUnits.setCreatedBy(userId);

                    tranxPurChallanDetailsUnitRepository.save(invoiceUnits);
                }
                /* End of inserting into TranxPurchaseInvoiceDetailsUnits */

                /* closing of purchase orders while converting into purchase challan using its qnt */
                TranxPurOrder tranxPurOrder = null;
                JsonArray referenceArray = new JsonArray();
                JsonParser parser = new JsonParser();
                if (referenceObject != null) {
                    JsonElement purDetailsJson = parser.parse(referenceObject);
                    referenceArray = purDetailsJson.getAsJsonArray();
                }
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
            /* End of  closing of purchase orders while converting into purchase challan
            using its qnt */

                row.add(tranxPurChallanDetails);
                TranxPurChallanDetails newDetails = tranxPurChallanDetailsRepository.save(tranxPurChallanDetails);
                JsonArray jsonArray = object.getAsJsonArray("serialNo");
                List<TranxPurchaseChallanProductSrNumber> serialNumbers = new ArrayList<>();
                if (jsonArray != null && jsonArray.size() > 0) {
                    for (JsonElement jsonElement : jsonArray) {
                        JsonObject json = jsonElement.getAsJsonObject();
                        TranxPurchaseChallanProductSrNumber tranxPurchaseChallanProductSrNumber = new TranxPurchaseChallanProductSrNumber();
                        tranxPurchaseChallanProductSrNumber.setBranch(branch);
                        tranxPurchaseChallanProductSrNumber.setOutlet(outlet);
                        tranxPurchaseChallanProductSrNumber.setProduct(mProduct);
                        tranxPurchaseChallanProductSrNumber.setSerialNo(json.get("no").getAsString());
                        tranxPurchaseChallanProductSrNumber.setTranxPurChallan(mTranxPurChallan);
                        tranxPurchaseChallanProductSrNumber.setTransactionStatus("purchase");
                        tranxPurchaseChallanProductSrNumber.setStatus(true);
                        tranxPurchaseChallanProductSrNumber.setCreatedBy(userId);
                        tranxPurchaseChallanProductSrNumber.setOperations("inserted");
                        // tranxPurchaseChallanProductSrNumber.setCreatedBy(mTranxPurChallan.getCreatedBy());
                        tranxPurchaseChallanProductSrNumber.setTransactionTypeMaster(tranxType);
                        serialNumbers.add(tranxPurchaseChallanProductSrNumber);
                    }
                    newSerialNumbers = tranxPurchaseChallanProductSrNumberRepository.saveAll(serialNumbers);
                    /*inventoryDTO.insertIntoInventoryTranxDetails(newDetails.getProduct(),
                            newDetails.getTranxPurChallan().getTransactionDate(),
                            "purchase challan", newDetails.getQtyHigh(), newDetails.getQtyMedium(),
                            newDetails.getQtyLow(), "STOCKIN", newDetails.getTranxPurChallan().getId(),
                            newDetails.getTranxPurChallan().getFinancialYear(),
                            newDetails.getTranxPurChallan().getInvoiceDate(), newDetails.getTranxPurChallan().getBranch(), newDetails.getTranxPurChallan().getOutlet());*/
                }
                /*if (newSerialNumbers.size() > 0) {
                    inventoryDTO.saveIntoSerialTranxSummaryDetailsPurChallan(
                            newSerialNumbers, tranxType.getTransactionName());
                }*/
            }
        } catch (Exception e) {
            purChallanLogger.error("Exception in saveIntoPOChallanInvoiceDetails: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /* list of Purchase challans outletwise*/
    public JsonObject poChallanInvoiceList(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<TranxPurChallan> tranxPurChallans = tranxPurChallanRepository.findByOutletIdAndStatusOrderByIdDesc(users.getOutlet().getId(), true);
        for (TranxPurChallan invoices : tranxPurChallans) {
            JsonObject response = new JsonObject();
            response.addProperty("id", invoices.getId());
            response.addProperty("invoice_no", invoices.getVendorInvoiceNo());
            response.addProperty("invoice_date", invoices.getInvoiceDate().toString());
            response.addProperty("transaction_date", invoices.getTransactionDate().toString());
            response.addProperty("total_amount", invoices.getTotalAmount());
            response.addProperty("sundry_creditor_name", invoices.getSundryCreditors().getLedgerName());
            response.addProperty("sundry_creditor_id", invoices.getSundryCreditors().getId());
            response.addProperty("purchase_account_name", invoices.getPurchaseAccountLedger().getLedgerName());
            response.addProperty("purchase_challan_status", invoices.getTransactionStatus().getStatusName());
            result.add(response);
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);
        return output;
    }


    public JsonObject getPOChallanInvoiceWithIds(HttpServletRequest request) {
        JsonObject output = new JsonObject();
        TransactionTypeMaster tranxType = tranxRepository.findByTransactionNameIgnoreCase("purchase challan");
        String str = request.getParameter("po_challan_ids");
        JsonParser parser = new JsonParser();
        JsonElement purDetailsJson = parser.parse(str);
        JsonArray jsonArray = purDetailsJson.getAsJsonArray();
        // JsonArray jsonArray = new JsonArray();
        JsonArray units = new JsonArray();
        JsonArray newList = new JsonArray();
        for (JsonElement mList : jsonArray) {
            JsonObject object = mList.getAsJsonObject();
            List<TranxPurChallanDetails> details = tranxPurChallanDetailsRepository.findByTranxPurChallanIdAndStatus(
                    object.get("id").getAsLong(), true);
            if (details.size() > 0) {
                for (TranxPurChallanDetails mDetails : details) {
                    JsonObject result = new JsonObject();
                    result.addProperty("id", mDetails.getId());
                    result.addProperty("reference_id", mDetails.getTranxPurChallan().getId());
                    result.addProperty("reference_type", tranxType.getTransactionName());
                    result.addProperty("product_id", mDetails.getProduct().getId());
                    result.addProperty("unitId", "");
                    result.addProperty("qtyH", mDetails.getQtyHigh());
                    result.addProperty("qtyM", mDetails.getQtyMedium().toString() != null ? mDetails.getQtyMedium().toString() : "");
                    result.addProperty("qtyL", mDetails.getQtyLow().toString() != null ? mDetails.getQtyLow().toString() : "");
                    Double gst_value = mDetails.getProduct().getProductHsn().getIgst();
                    /* In order to remove GST from base amount */
                   /* Double gstAmount = mDetails.getRateHigh() -
                            (mDetails.getRateHigh() * (100 / (100 + gst_value)));
                    Double rateHigh = mDetails.getRateHigh() - gstAmount;*/

                    /* End of : In order to remove GST from base amount */
                    Double rateHigh = mDetails.getRateHigh();
                    result.addProperty("rateH", numFormat.numFormat(rateHigh));
                    result.addProperty("rateM", mDetails.getRateMedium().toString() != null ?
                            mDetails.getRateMedium().toString() : "");
                    result.addProperty("rateL", mDetails.getRateLow().toString() != null ?
                            mDetails.getRateLow().toString() : "");
                    result.addProperty("base_amt_H", mDetails.getBaseAmtHigh());
                    result.addProperty("base_amt_M", mDetails.getBaseAmtMedium().toString() != null ?
                            mDetails.getBaseAmtMedium().toString() : "");
                    result.addProperty("base_amt_L", mDetails.getBaseAmtLow().toString() != null ?
                            mDetails.getBaseAmtLow().toString() : "");
                    result.addProperty("base_amt", "");
                    result.addProperty("dis_amt", "");
                    result.addProperty("dis_per", "");
                    result.addProperty("dis_per_cal", "");
                    result.addProperty("dis_amt_cal", "");
                    result.addProperty("total_amt", "");
                    result.addProperty("gst", "");
                    result.addProperty("igst", "");
                    result.addProperty("cgst", "");
                    result.addProperty("sgst", "");
                    result.addProperty("total_igst", "");
                    result.addProperty("total_cgst", "");
                    result.addProperty("total_sgst", "");
                    result.addProperty("final_amt", "");
                    result.addProperty("discount_proportional_cal", "0");
                    result.addProperty("additional_charges_proportional_cal", "0");
                    result.addProperty("package_id", mDetails.getPackingMaster() != null
                            ? mDetails.getPackingMaster().getId().toString() : "");
                    JsonArray serialNo = new JsonArray();
                    List<TranxPurchaseChallanProductSrNumber> serialNumbers = tranxPurchaseChallanProductSrNumberRepository.findByProductIdAndStatus(
                            mDetails.getProduct().getId(), true);
                    if (serialNumbers.size() > 0) {
                        for (TranxPurchaseChallanProductSrNumber mSerail : serialNumbers) {
                            JsonObject serailNObject = new JsonObject();
                            serailNObject.addProperty("no", mSerail.getSerialNo());
                            serialNo.add(serailNObject);
                        }
                        result.add("serialNo", serialNo);
                    }
                    /* getting Units of Purcase Challans*/
                    List<TranxPurChallanDetailsUnits> unitDetails = tranxPurChallanDetailsUnitRepository.findByTranxPurChallanDetailsIdAndStatus(
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
                    result.add("units", units);
                    newList.add(result);
                }
            }
        }
        if (newList.size() > 0) {
            output.addProperty("message", "success");
            output.addProperty("responseStatus", HttpStatus.OK.value());
            output.add("data", newList);
        } else {
            output.addProperty("message", "empty list");
            output.addProperty("responseStatus", HttpStatus.OK.value());
            output.addProperty("data", "");
        }
        return output;
    }

    public JsonObject getChallanRecord(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Long count = tranxPurChallanRepository.findLastRecord(users.getOutlet().getId());

        String serailNo = String.format("%05d", count + 1);// 5 digit serial number
        /*String companyName = users.getOutlet().getCompanyName();
        companyName = companyName.substring(0, 3);*/ // fetching first 3 digits from company names
        /* getting Start and End year from fiscal Year */
        String startYear = generateFiscalYear.getStartYear();
        String endYear = generateFiscalYear.getEndYear();
        //first 3 digits of Current month
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
       /* String pcCode = companyName.toUpperCase() + "-" + startYear + endYear
                + "-" + "PC" + currentMonth + "-" + serailNo;*/
        String pcCode = "PC" + currentMonth + serailNo;

        JsonObject result = new JsonObject();
        result.addProperty("message", "success");
        result.addProperty("responseStatus", HttpStatus.OK.value());
        result.addProperty("count", count + 1);
        result.addProperty("serialNo", pcCode);
        return result;
    }

    /* Pending Purchase challan  */
    public JsonObject pCPendingOrder(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        LedgerMaster sundryCreditors = ledgerMasterRepository.findByIdAndStatus(
                Long.parseLong(request.getParameter("supplier_code_id")),
                true);
        List<TranxPurChallan> tranxPurChallan = tranxPurChallanRepository.findBySundryCreditorsIdAndStatus(sundryCreditors.getId(), true);
        for (TranxPurChallan invoices : tranxPurChallan) {
            JsonObject response = new JsonObject();
            response.addProperty("id", invoices.getId());
            response.addProperty("invoice_no", invoices.getVendorInvoiceNo());
            response.addProperty("invoice_date", invoices.getInvoiceDate().toString());
            response.addProperty("transaction_date", invoices.getTransactionDate().toString());
            response.addProperty("total_amount", invoices.getTotalAmount());
            response.addProperty("sundry_creditor_name", invoices.getSundryCreditors().getLedgerName());
            response.addProperty("sundry_creditor_id", invoices.getSundryCreditors().getId());
            result.add(response);
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);
        return output;
    }

    /* Purchase Challans Count of last Records */
    public JsonObject pcLastRecord(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Long count = tranxPurChallanRepository.findLastRecord(users.getOutlet().getId());
        JsonObject result = new JsonObject();
        result.addProperty("message", "success");
        result.addProperty("responseStatus", HttpStatus.OK.value());
        result.addProperty("count", count + 1);
        return result;


    }

}
