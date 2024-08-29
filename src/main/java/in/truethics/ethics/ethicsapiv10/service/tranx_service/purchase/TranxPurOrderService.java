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
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurOrder;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurOrderDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurOrderDetailsUnits;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurOrderDutiesTaxes;
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
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository.TranxPurOrderDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository.TranxPurOrderDetailsUnitRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository.TranxPurOrderDutiesTaxesRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository.TranxPurOrderRepository;
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
public class TranxPurOrderService {

    private static final Logger purOrderLogger = LoggerFactory.getLogger(TranxPurOrderService.class);
    @PersistenceContext
    EntityManager entityManager;
    List<Long> dbList = new ArrayList<>(); // for saving all ledgers Id against Purchase invoice
    List<Long> mInputList = new ArrayList<>();
    @Autowired
    private TranxPurOrderRepository tranxPurOrderRepository;
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
    private TranxPurOrderDutiesTaxesRepository tranxPurOrderDutiesTaxesRepository;
    @Autowired
    private TranxPurOrderDetailsRepository tranxPurOrderDetailsRepository;
    @Autowired
    private TransactionTypeMasterRepository tranxRepository;
    @Autowired
    private GenerateCoreLogic generateCoreLogic;
    @Autowired
    private GenerateFiscalYear generateFiscalYear;
    @Autowired
    private FiscalYearRepository fiscalYearRepository;
    /*  @Autowired
      private InventoryDTO inventoryDTO;*/
    @Autowired
    private TransactionStatusRepository transactionStatusRepository;
    @Autowired
    private ProductUnitRepository productUnitRepository;
    @Autowired
    private NumFormat numFormat;
    @Autowired
    private UnitsRepository unitsRepository;
    @Autowired
    private TranxPurOrderDetailsUnitRepository tranxPurOrderDetailsUnitRepository;
    @Autowired
    private PackingMasterRepository packingMasterRepository;

    /* creating purchase order */
    public Object insertPOInvoice(HttpServletRequest request) {
        TranxPurOrder mPurchaseTranx = null;
        ResponseMessage responseMessage = new ResponseMessage();
        mPurchaseTranx = saveIntoPOInvoice(request);
        if (mPurchaseTranx != null) {
            //insertIntoLedgerTranxDetails(mPurchaseTranx);
            responseMessage.setMessage("purchase order created successfully");
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        } else {
            responseMessage.setMessage("Error in purchase order creation");
            responseMessage.setResponseStatus(HttpStatus.FORBIDDEN.value());
        }
        return responseMessage;
    }

    /******* Save into Purchase Order
     * @return*******/
    public TranxPurOrder saveIntoPOInvoice(HttpServletRequest request) {
        TranxPurOrder mPurchaseTranx = null;
        TransactionTypeMaster tranxType = null;
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Map<String, String[]> paramMap = request.getParameterMap();
        Branch branch = null;
        Outlet outlet = users.getOutlet();
        TranxPurOrder tranxPurOrder = new TranxPurOrder();
        if (users.getBranch() != null) {
            branch = users.getBranch();
            tranxPurOrder.setBranch(branch);
        }
        tranxPurOrder.setOutlet(outlet);
        TransactionStatus transactionStatus = transactionStatusRepository.findByStatusNameAndStatus("opened", true);
        tranxPurOrder.setTransactionStatus(transactionStatus);
        tranxPurOrder.setCreatedBy(users.getId());
        tranxPurOrder.setOrderReference(request.getParameter("pur_order_no"));
        tranxType = tranxRepository.findByTransactionNameIgnoreCase("purchase order");
        LocalDate mDate = LocalDate.parse(request.getParameter("pur_order_date"));
        tranxPurOrder.setInvoiceDate(mDate);
        tranxPurOrder.setTransactionDate(mDate);
        /* fiscal year mapping */
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(mDate);
        if (fiscalYear != null) {
            tranxPurOrder.setFiscalYear(fiscalYear);
            tranxPurOrder.setFinancialYear(fiscalYear.getFiscalYear());
        }
        /* End of fiscal year mapping */

        tranxPurOrder.setPurOrdSrno(Long.parseLong(request.getParameter("pur_order_sr_no")));
        tranxPurOrder.setVendorInvoiceNo(request.getParameter("pur_order_no"));
        if (paramMap.containsKey("transport_name"))
            tranxPurOrder.setTransportName(request.getParameter("transport_name"));
        else {
            tranxPurOrder.setTransportName("NA");
        }
        if (paramMap.containsKey("reference"))
            tranxPurOrder.setReference(request.getParameter("reference"));
        else {
            tranxPurOrder.setReference("NA");
        }
        LedgerMaster purchaseAccount = ledgerMasterRepository.findByIdAndStatus(Long.parseLong(
                request.getParameter("purchase_id")), true);
        LedgerMaster sundryCreditors = ledgerMasterRepository.findByIdAndStatus(
                Long.parseLong(request.getParameter("supplier_code_id")),
                true);
        tranxPurOrder.setPurchaseAccountLedger(purchaseAccount);
        tranxPurOrder.setSundryCreditors(sundryCreditors);

        tranxPurOrder.setTotalBaseAmount(Double.parseDouble(request.getParameter("total_base_amt")));
        LedgerMaster roundoff = ledgerMasterRepository.findByOutletIdAndLedgerNameIgnoreCase(outlet.getId(), "Round off");
        tranxPurOrder.setRoundOff(Double.parseDouble(request.getParameter("roundoff")));
        tranxPurOrder.setPurchaseRoundOff(roundoff);
        tranxPurOrder.setTotalAmount(Double.parseDouble(request.getParameter("totalamt")));
        Boolean taxFlag = Boolean.parseBoolean(request.getParameter("taxFlag"));
        /* if true : cgst and sgst i.e intra state */
        if (taxFlag) {
            tranxPurOrder.setTotalcgst(Double.parseDouble(request.getParameter("totalcgst")));
            tranxPurOrder.setTotalsgst(Double.parseDouble(request.getParameter("totalsgst")));
            tranxPurOrder.setTotaligst(0.0);
        }
        /* if false : igst i.e inter state */
        else {
            tranxPurOrder.setTotalcgst(0.0);
            tranxPurOrder.setTotalsgst(0.0);
            tranxPurOrder.setTotaligst(Double.parseDouble(request.getParameter("totaligst")));
        }
        if (paramMap.containsKey("totalqty"))
            tranxPurOrder.setTotalqty(Long.parseLong(request.getParameter("totalqty")));
        if (paramMap.containsKey("tcs"))
            tranxPurOrder.setTcs(Double.parseDouble(request.getParameter("tcs")));
        tranxPurOrder.setTaxableAmount(Double.parseDouble(request.getParameter("taxable_amount")));
        tranxPurOrder.setStatus(true);
        tranxPurOrder.setOperations("inserted");
        if (paramMap.containsKey("narration"))
            tranxPurOrder.setNarration(request.getParameter("narration"));
        else {
            tranxPurOrder.setNarration("NA");
        }
        try {
            mPurchaseTranx = tranxPurOrderRepository.save(tranxPurOrder);
            if (mPurchaseTranx != null) {
                /* Save into Duties and Taxes */
                String taxStr = request.getParameter("taxCalculation");
                // JsonObject duties_taxes = new JsonObject(taxStr);
                JsonObject duties_taxes = new Gson().fromJson(taxStr, JsonObject.class);
                saveIntoPOInvoiceDutiesTaxes(duties_taxes, mPurchaseTranx, taxFlag);
                /* save into Details  */
                String jsonStr = request.getParameter("row");
                JsonParser parser = new JsonParser();
                JsonElement purDetailsJson = parser.parse(jsonStr);
                JsonArray array = purDetailsJson.getAsJsonArray();
                //JsonArray array = new JsonArray(jsonStr);
                saveIntoPOInvoiceDetails(array, mPurchaseTranx,
                        branch, outlet, users.getId(), tranxType, "create");
            }
        } catch (DataIntegrityViolationException e) {
            System.out.println("Exception:" + e.getMessage());

        } catch (Exception e1) {

            System.out.println("Exception:" + e1.getMessage());
        }
        return mPurchaseTranx;
    }
    /* End of Purchase Invoice */

    /****** Save into Duties and Taxes ******/
    public void saveIntoPOInvoiceDutiesTaxes(
            JsonObject duties_taxes, TranxPurOrder mPurchaseTranx, Boolean taxFlag) {
        List<TranxPurOrderDutiesTaxes> tranxPurOrderDutiesTaxes = new ArrayList<>();
        if (taxFlag) {
            JsonArray cgstList = duties_taxes.getAsJsonArray("cgst");
            JsonArray sgstList = duties_taxes.getAsJsonArray("sgst");
            /* this is for Cgst creation */
            if (cgstList.size() > 0) {
                for (JsonElement mList : cgstList) {
                    TranxPurOrderDutiesTaxes tranxPurOrderDutiesTaxes1 = null;
                    tranxPurOrderDutiesTaxes1 = new TranxPurOrderDutiesTaxes();
                    JsonObject cgstObject = mList.getAsJsonObject();
                    // JsonObject cgstObject = cgstList.getAsJsonObject(i);
                    LedgerMaster dutiesTaxes = null;
                    String inputGst = cgstObject.get("gst").getAsString();
                    String ledgerName = "INPUT CGST " + inputGst;
                    dutiesTaxes = ledgerMasterRepository.findByOutletIdAndLedgerNameIgnoreCase(
                            mPurchaseTranx.getOutlet().getId(), ledgerName);
                    if (dutiesTaxes != null) {
                        //   dutiesTaxesLedger.setDutiesTaxes(dutiesTaxes);
                        tranxPurOrderDutiesTaxes1.setDutiesTaxes(dutiesTaxes);
                    }
                    tranxPurOrderDutiesTaxes1.setAmount(Double.parseDouble(cgstObject.get("amt").getAsString()));
                    tranxPurOrderDutiesTaxes1.setSundryCreditors(mPurchaseTranx.getSundryCreditors());
                    tranxPurOrderDutiesTaxes1.setTranxPurOrder(mPurchaseTranx);
                    tranxPurOrderDutiesTaxes1.setIntra(taxFlag);
                    tranxPurOrderDutiesTaxes1.setStatus(true);
                    tranxPurOrderDutiesTaxes1.setCreatedBy(mPurchaseTranx.getCreatedBy());
                    tranxPurOrderDutiesTaxes.add(tranxPurOrderDutiesTaxes1);
                }
            }
            /* this is for Sgst creation */
            if (sgstList.size() > 0) {
                for (JsonElement mList : sgstList) {
                    TranxPurOrderDutiesTaxes taxes = null;

                    taxes = new TranxPurOrderDutiesTaxes();

                    //TranxPurOrderDutiesTaxes taxes = new TranxPurOrderDutiesTaxes();
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
                    taxes.setTranxPurOrder(mPurchaseTranx);
                    taxes.setSundryCreditors(mPurchaseTranx.getSundryCreditors());
                    taxes.setIntra(taxFlag);
                    taxes.setStatus(true);
                    taxes.setCreatedBy(mPurchaseTranx.getCreatedBy());
                    tranxPurOrderDutiesTaxes.add(taxes);
                }
            }
        } else {
            JsonArray igstList = duties_taxes.getAsJsonArray("igst");
            /* this is for Igst creation */
            if (igstList.size() > 0) {
                for (JsonElement mList : igstList) {
                    TranxPurOrderDutiesTaxes taxes = null;

                    taxes = new TranxPurOrderDutiesTaxes();
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
                    taxes.setTranxPurOrder(mPurchaseTranx);
                    taxes.setSundryCreditors(mPurchaseTranx.getSundryCreditors());
                    taxes.setIntra(taxFlag);
                    taxes.setStatus(true);
                    taxes.setCreatedBy(mPurchaseTranx.getCreatedBy());
                    tranxPurOrderDutiesTaxes.add(taxes);
                }
            }
        }
        try {
            /* save all Duties and Taxes into purchase Invoice Duties taxes table */
            tranxPurOrderDutiesTaxesRepository.saveAll(tranxPurOrderDutiesTaxes);
        } catch (DataIntegrityViolationException e1) {
            System.out.println(e1.getMessage());
            e1.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    /* End of Purchase Duties and Taxes Ledger */


    /****** save into Purchase Invoice Details ******/
    public void saveIntoPOInvoiceDetails(JsonArray array, TranxPurOrder mTranxPurOrder,
                                         Branch branch, Outlet outlet,
                                         Long userId, TransactionTypeMaster tranxType, String key) {
        /* Purchase Product Details Start here */
        try {
            List<TranxPurOrderDetails> row = new ArrayList<>();
            for (JsonElement mList : array) {
                JsonObject object = mList.getAsJsonObject();
                TranxPurOrderDetails tranxPurOrderDetails = null;
                Long detailsId = object.get("details_id").getAsLong();
                if (detailsId != 0) {
                    tranxPurOrderDetails = tranxPurOrderDetailsRepository.findByIdAndStatus(detailsId, true);
                    if (tranxPurOrderDetails != null) {
                        tranxPurOrderDetails.setOperations("updated");
                        // Boolean flag = insertIntoPurchaseInvoiceDetailsHistory(tranxPurOrderDetails);
                    } else {
                        tranxPurOrderDetails = new TranxPurOrderDetails();
                        tranxPurOrderDetails.setTranxPurOrder(mTranxPurOrder);
                        tranxPurOrderDetails.setOperations("inserted");
                    }
                } else {
                    tranxPurOrderDetails = new TranxPurOrderDetails();
                    tranxPurOrderDetails.setTranxPurOrder(mTranxPurOrder);
                    tranxPurOrderDetails.setOperations("inserted");
                }
                tranxPurOrderDetails.setTranxPurOrder(mTranxPurOrder);
                Product mProduct = productRepository.findByIdAndStatus(object.get("product_id").getAsLong(),
                        true);
                tranxPurOrderDetails.setProduct(mProduct);
                tranxPurOrderDetails.setBase_amt(object.get("base_amt").getAsDouble());
                if (!object.get("dis_amt").getAsString().equals("")) {
                    System.out.println(object.get("dis_amt").getAsDouble());
                    tranxPurOrderDetails.setDiscountAmount(object.get("dis_amt").getAsDouble());
                } else {
                    tranxPurOrderDetails.setDiscountAmount((double) 0);
                }
                if (!object.get("dis_per").getAsString().equals("")) {


                    tranxPurOrderDetails.setDiscountPer(object.get("dis_per").getAsDouble());
                } else {
                    tranxPurOrderDetails.setDiscountPer((double) 0);
                }
                if (!object.get("dis_per_cal").getAsString().equals("")) {
                    tranxPurOrderDetails.setDiscountPerCal(object.get("dis_per_cal").getAsDouble());
                } else {
                    tranxPurOrderDetails.setDiscountPerCal((double) 0);
                }
                if (!object.get("dis_amt_cal").getAsString().equals("")) {

                    tranxPurOrderDetails.setDiscountAmountCal(object.get("dis_amt_cal").getAsDouble());
                } else {
                    tranxPurOrderDetails.setDiscountAmountCal((double) 0);
                }
                tranxPurOrderDetails.setTotalAmount(object.get("total_amt").getAsDouble());
                tranxPurOrderDetails.setIgst(object.get("igst").getAsDouble());
                tranxPurOrderDetails.setSgst(object.get("sgst").getAsDouble());
                tranxPurOrderDetails.setCgst(object.get("cgst").getAsDouble());
                tranxPurOrderDetails.setTotalIgst(object.get("total_igst").getAsDouble());
                tranxPurOrderDetails.setTotalSgst(object.get("total_sgst").getAsDouble());
                tranxPurOrderDetails.setTotalCgst(object.get("total_cgst").getAsDouble());
                tranxPurOrderDetails.setFinalAmount(object.get("final_amt").getAsDouble());
//                if (object.has("qtyH"))
//                    tranxPurOrderDetails.setQtyHigh(Double.parseDouble(object.get("qtyH").getAsString()));
//                tranxPurOrderDetails.setQtyHigh(object.get("qtyH").getAsDouble());
//                if (object.has("rateH"))
//                    tranxPurOrderDetails.setRateHigh(Double.parseDouble(object.get("rateH").getAsString()));
//                tranxPurOrderDetails.setStatus(true);
//                tranxPurOrderDetails.setTranxPurOrder(mTranxPurOrder);

                if (object.has("qtyH"))
                    tranxPurOrderDetails.setQtyHigh(object.get("qtyH").getAsDouble());
                if (object.has("rateH"))
                    tranxPurOrderDetails.setRateHigh(object.get("rateH").getAsDouble());
                tranxPurOrderDetails.setStatus(true);
                tranxPurOrderDetails.setTranxPurOrder(mTranxPurOrder);
                if (object.has("qtyM"))
                    tranxPurOrderDetails.setQtyMedium(object.get("qtyM").getAsDouble());
                if (object.has("rateM"))
                    tranxPurOrderDetails.setRateMedium(object.get("rateM").getAsDouble());
                if (object.has("qtyL"))
                    tranxPurOrderDetails.setQtyLow(object.get("qtyL").getAsDouble());
                if (object.has("rateL"))
                    tranxPurOrderDetails.setRateLow(object.get("rateL").getAsDouble());
                if (object.has("base_amt_H"))
                    tranxPurOrderDetails.setBaseAmtHigh(object.get("base_amt_H").getAsDouble());
                if (object.has("base_amt_M"))
                    tranxPurOrderDetails.setBaseAmtMedium(object.get("base_amt_M").getAsDouble());
                if (object.has("base_amt_L"))
                    tranxPurOrderDetails.setBaseAmtLow(object.get("base_amt_L").getAsDouble());
                tranxPurOrderDetails.setCreatedBy(userId);
                if (!object.get("packageId").getAsString().equalsIgnoreCase("")) {
                    PackingMaster packingMaster = packingMasterRepository.findByIdAndStatus(object.get("packageId").getAsLong(), true);
                    if (packingMaster != null)
                        tranxPurOrderDetails.setPackingMaster(packingMaster);
                }
                row.add(tranxPurOrderDetails);
                TranxPurOrderDetails newDetails = tranxPurOrderDetailsRepository.save(tranxPurOrderDetails);

                /* inserting into TranxPurchaseOrderDetailsUnits */
                JsonArray unitDetails = object.getAsJsonArray("units");
                for (JsonElement mUnits : unitDetails) {
                    JsonObject mObject = mUnits.getAsJsonObject();
                    TranxPurOrderDetailsUnits invoiceUnits = new TranxPurOrderDetailsUnits();
                    invoiceUnits.setTranxPurOrder(newDetails.getTranxPurOrder());
                    invoiceUnits.setTranxPurOrderDetails(newDetails);
                    invoiceUnits.setProduct(newDetails.getProduct());
                    Units unit = unitsRepository.findByIdAndStatus(mObject.get("unit_id").getAsLong(), true);
                    invoiceUnits.setUnits(unit);
                    invoiceUnits.setUnitConversions(mObject.get("unit_conv").getAsDouble());
                    invoiceUnits.setQty(mObject.get("qty").getAsDouble());
                    invoiceUnits.setRate(mObject.get("rate").getAsDouble());
                    invoiceUnits.setBaseAmt(mObject.get("base_amt").getAsDouble());
                    invoiceUnits.setStatus(true);
                    invoiceUnits.setCreatedBy(userId);
                    tranxPurOrderDetailsUnitRepository.save(invoiceUnits);
                }
                /* End of inserting into TranxPurchaseOrderDetailsUnits */

            }
        } catch (Exception e) {
            purOrderLogger.error("Exception in DetailsL:" + e.getMessage());
        }
    }

//    public  void saveIntoPOInvoiceDetails(JsonArray array, TranxPurInvoice mPurchaseTranx,
//                                          Branch branch, Outlet outlet,
//                                          Long userId, TransactionTypeMaster tranxType, String referenceObject){
//        try {
//            List<TranxPurOrderDetails> row = new ArrayList<>();
//
//            List<TranxPurchaseInvoiceProductSrNumber> newSerialNumbers = new ArrayList<>();
//            for (JsonElement mList : array) {
//                JsonObject object = mList.getAsJsonObject();
//                TranxPurInvoiceDetails mDetails = new TranxPurInvoiceDetails();
//                mDetails.setPurchaseTransaction(mPurchaseTranx);
//                Product mProduct = productRepository.findByIdAndStatus(object.get("product_id").getAsLong(),
//                        true);
//                mDetails.setProduct(mProduct);
//                if (!object.get("reference_id").equals("")) {
//                    mDetails.setReferenceId(object.get("reference_id").getAsString());
//                    mDetails.setReferenceType(object.get("reference_type").getAsString());
//                    // setClosePO(request.getParameter("reference_ids"));
//                }
//    }


    /* List of Purchase orders :outlet wise */
    public JsonObject poInvoiceList(HttpServletRequest request) {

        JsonArray result = new JsonArray();
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<TranxPurOrder> tranxPurOrders = tranxPurOrderRepository.findByOutletIdAndStatusOrderByIdDesc(users.getOutlet().getId(), true);
        for (TranxPurOrder invoices : tranxPurOrders) {
            JsonObject response = new JsonObject();
            response.addProperty("id", invoices.getId());
            response.addProperty("invoice_no", invoices.getVendorInvoiceNo());
            response.addProperty("invoice_date", invoices.getInvoiceDate().toString());
            response.addProperty("transaction_date", invoices.getTransactionDate().toString());
            //response.addProperty("order_serial_number", invoices.getTranxPurchaseOrderProductSrNumbers().toString());
            response.addProperty("total_amount", invoices.getTotalAmount());
            response.addProperty("sundry_creditor_name", invoices.getSundryCreditors().getLedgerName());
            response.addProperty("sundry_creditor_id", invoices.getSundryCreditors().getId());
            response.addProperty("purchase_order_status", invoices.getTransactionStatus().getStatusName());
            response.addProperty("purchase_account_name", invoices.getPurchaseAccountLedger().getLedgerName());
            // response.put("purchase_account_name", invoices.getPurchaseAccountLedger().getLedgerName());
            result.add(response);
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", result);
        return output;
    }

    public JsonObject poLastRecord(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Long count = tranxPurOrderRepository.findLastRecord(users.getOutlet().getId());

        String serailNo = String.format("%05d", count + 1);// 5 digit serial number
      /*  String companyName = users.getOutlet().getCompanyName();
        companyName = companyName.substring(0, 3);*/ // fetching first 3 digits from company names
        /* getting Start and End year from fiscal Year */
 /*       String startYear = generateFiscalYear.getStartYear();
        String endYear = generateFiscalYear.getEndYear();*/
        //first 3 digits of Current month
        GenerateDates generateDates = new GenerateDates();
        String currentMonth = generateDates.getCurrentMonth().substring(0, 3);
        /*String poCode = companyName.toUpperCase() + "-" + startYear + endYear
                + "-" + "PO" + currentMonth + "-" + serailNo;*/
        String poCode = "PO" + currentMonth + serailNo;

        JsonObject result = new JsonObject();
        result.addProperty("message", "success");
        result.addProperty("responseStatus", HttpStatus.OK.value());
        result.addProperty("count", count + 1);
        result.addProperty("serialNo", poCode);
        return result;
    }

    public Object getAllPo() {
        List<TranxPurOrder> list = tranxPurOrderRepository.findAllByStatus(true);
        return list;
    }


    public JsonObject getPOInvoiceWithIds(HttpServletRequest request) {
        JsonObject output = new JsonObject();
        String str = request.getParameter("purchase_order_id");
        JsonParser parser = new JsonParser();
        JsonElement purDetailsJson = parser.parse(str);
        TransactionTypeMaster tranxType = tranxRepository.findByTransactionNameIgnoreCase("purchase order");
        JsonArray jsonArray = purDetailsJson.getAsJsonArray();
        //JsonArray jsonArray = new JsonArray();
        JsonArray units = new JsonArray();
        JsonArray newList = new JsonArray();
        for (JsonElement mList : jsonArray) {
            JsonObject object = mList.getAsJsonObject();
            List<TranxPurOrderDetails> details = tranxPurOrderDetailsRepository.findByTranxPurOrderIdAndStatus(
                    object.get("id").getAsLong(), true);
            if (details.size() > 0) {
                for (TranxPurOrderDetails mDetails : details) {
                    JsonObject result = new JsonObject();
                    result.addProperty("id", mDetails.getId());
                    result.addProperty("reference_id", mDetails.getTranxPurOrder().getId());
                    result.addProperty("reference_type", tranxType.getTransactionName());
                    result.addProperty("product_id", mDetails.getProduct().getId());
                    result.addProperty("unitId", "");
                    result.addProperty("qtyH", mDetails.getQtyHigh());
                    result.addProperty("qtyM", mDetails.getQtyMedium().toString() != null ? mDetails.getQtyMedium().toString() : "");
                    result.addProperty("qtyL", mDetails.getQtyLow().toString() != null ? mDetails.getQtyLow().toString() : "");
                    Double gst_value = mDetails.getProduct().getProductHsn().getIgst();
                    //* In order to remove GST from base amount *//
                   /* Double gstAmount = mDetails.getRateHigh() -
                            (mDetails.getRateHigh() * (100 / (100 + gst_value)));
                    Double rateHigh = mDetails.getRateHigh() - gstAmount;*/
                    Double rateHigh = mDetails.getRateHigh();
                    //* End of : In order to remove GST from base amount *//
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
                    result.addProperty("package_id", mDetails.getPackingMaster() != null ? mDetails.getPackingMaster().getId().toString() : "");
                    JsonArray serialNo = new JsonArray();
                    /* getting Units of Purchase Orders */
                    List<TranxPurOrderDetailsUnits> unitDetails = tranxPurOrderDetailsUnitRepository.findByTranxPurOrderDetailsIdAndStatus(
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

    public JsonObject poPendingOrder(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        LedgerMaster sundryCreditors = ledgerMasterRepository.findByIdAndStatus(
                Long.parseLong(request.getParameter("supplier_code_id")),
                true);
        List<TranxPurOrder> tranxPurOrders = tranxPurOrderRepository.findBySundryCreditorsIdAndStatusAndTransactionStatusId(sundryCreditors.getId(), true, 1L);
        for (TranxPurOrder invoices : tranxPurOrders) {
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

    public JsonObject getPurchaseOrderById(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<TranxPurOrderDetails> list = new ArrayList<>();
        JsonObject finalResult = new JsonObject();
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            TranxPurOrder purchaseInvoice = tranxPurOrderRepository.findByIdAndOutletIdAndStatus(
                    id, users.getOutlet().getId(), true);
            list = tranxPurOrderDetailsRepository.findByTranxPurOrderIdAndStatus(id, true);
            finalResult.addProperty("narration", purchaseInvoice.getNarration() != null ? purchaseInvoice.getNarration() : "");
            JsonObject result = new JsonObject();
            /* Purchase Invoice Data */
            result.addProperty("id", purchaseInvoice.getId());
            result.addProperty("invoice_dt", purchaseInvoice.getInvoiceDate().toString());
            result.addProperty("purchase_order", purchaseInvoice.getVendorInvoiceNo());
            result.addProperty("purchase_id", purchaseInvoice.getPurchaseAccountLedger().getId());
            result.addProperty("po_sr_no", purchaseInvoice.getId());
            result.addProperty("po_date", purchaseInvoice.getTransactionDate().toString());
            result.addProperty("transport_name", purchaseInvoice.getTransportName());
            result.addProperty("reference", purchaseInvoice.getReference());
            result.addProperty("supplier_id", purchaseInvoice.getSundryCreditors().getId());
            result.addProperty("supplier_name", purchaseInvoice.getSundryCreditors().getLedgerName());

            /* End of Purchase ORDER Data */

            /* Purchase ORDER Details */
            JsonArray row = new JsonArray();
            if (list.size() > 0) {
                for (TranxPurOrderDetails mDetails : list) {
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
                    prDetails.addProperty("dis_per_cal", mDetails.getDiscountPerCal());
                    prDetails.addProperty("dis_per_cal", mDetails.getDiscountPerCal());

                    row.add(prDetails);
                }
            } /* End of Purchase Order Details */
            finalResult.addProperty("message", "success");
            finalResult.addProperty("responseStatus", HttpStatus.OK.value());
            finalResult.add("invoice_data", result);
            finalResult.add("row", row);

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

    public Object editPOInvoice(HttpServletRequest request) {
        TranxPurOrder mPurchaseTranx = null;
        ResponseMessage responseMessage = new ResponseMessage();
        mPurchaseTranx = saveIntoPOEdit(request);
        if (mPurchaseTranx != null) {
            //insertIntoLedgerTranxDetails(mPurchaseTranx);
            responseMessage.setMessage("purchase order created successfully");
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        } else {
            responseMessage.setMessage("Error in purchase order creation");
            responseMessage.setResponseStatus(HttpStatus.FORBIDDEN.value());
        }
        return responseMessage;
    }

    public TranxPurOrder saveIntoPOEdit(HttpServletRequest request) {
        TranxPurOrder mPurchaseTranx = null;
        TransactionTypeMaster tranxType = null;
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Branch branch = null;
        Outlet outlet = users.getOutlet();
        TranxPurOrder tranxPurOrder = new TranxPurOrder();
        tranxPurOrder = tranxPurOrderRepository.findByIdAndOutletIdAndStatus(Long.parseLong(
                request.getParameter("id")), users.getOutlet().getId(), true);
        if (users.getBranch() != null) {
            branch = users.getBranch();
            tranxPurOrder.setBranch(branch);
        }
        tranxPurOrder.setOutlet(outlet);
        tranxType = tranxRepository.findByTransactionNameIgnoreCase("purchase order");
       /* LocalDate date = LocalDate.now();
        tranxPurOrder.setTransactionDate(date);
        *//* fiscal year mapping *//*
        FiscalYear fiscalYear = generateFiscalYear.getFiscalYear(date);
        if (fiscalYear != null) {
            tranxPurOrder.setFinancialYear(fiscalYear.getFiscalYear());
        }*/
        /* End of fiscal year mapping */
        tranxPurOrder.setTransportName(request.getParameter("transport_name"));
        tranxPurOrder.setReference(request.getParameter("reference"));
        LedgerMaster purchaseAccount = ledgerMasterRepository.findByIdAndStatus(Long.parseLong(
                request.getParameter("purchase_id")), true);
        LedgerMaster sundryCreditors = ledgerMasterRepository.findByIdAndStatus(
                Long.parseLong(request.getParameter("supplier_code_id")),
                true);
        tranxPurOrder.setPurchaseAccountLedger(purchaseAccount);
        tranxPurOrder.setSundryCreditors(sundryCreditors);
        LocalDate mDate = LocalDate.parse(request.getParameter("pur_order_date"));//YMD format
        tranxPurOrder.setInvoiceDate(mDate);
        tranxPurOrder.setTotalBaseAmount(Double.parseDouble(request.getParameter("total_base_amt")));
        LedgerMaster roundoff = ledgerMasterRepository.findByOutletIdAndLedgerNameIgnoreCase(outlet.getId(), "Round off");
        tranxPurOrder.setRoundOff(Double.parseDouble(request.getParameter("roundoff")));
        tranxPurOrder.setPurchaseRoundOff(roundoff);

        tranxPurOrder.setTotalAmount(Double.parseDouble(request.getParameter("totalamt")));
        Boolean taxFlag = Boolean.parseBoolean(request.getParameter("taxFlag"));
        /* if true : cgst and sgst i.e intra state */
        if (taxFlag) {
            tranxPurOrder.setTotalcgst(Double.parseDouble(request.getParameter("totalcgst")));
            tranxPurOrder.setTotalsgst(Double.parseDouble(request.getParameter("totalsgst")));
            tranxPurOrder.setTotaligst(0.0);
        }
        /* if false : igst i.e inter state */
        else {
            tranxPurOrder.setTotalcgst(0.0);
            tranxPurOrder.setTotalsgst(0.0);
            tranxPurOrder.setTotaligst(Double.parseDouble(request.getParameter("totaligst")));
        }
        tranxPurOrder.setTotalqty(Long.parseLong(request.getParameter("totalqty")));
        tranxPurOrder.setTcs(Double.parseDouble(request.getParameter("tcs")));
        tranxPurOrder.setTaxableAmount(Double.parseDouble(request.getParameter("taxable_amount")));
        tranxPurOrder.setStatus(true);
        tranxPurOrder.setOperations("inserted");
        tranxPurOrder.setNarration(request.getParameter("narration"));
        try {
            mPurchaseTranx = tranxPurOrderRepository.save(tranxPurOrder);
            if (mPurchaseTranx != null) {
                /* Save into Duties and Taxes */
                String taxStr = request.getParameter("taxCalculation");
                // JsonObject duties_taxes = new JsonObject(taxStr);
                JsonObject duties_taxes = new Gson().fromJson(taxStr, JsonObject.class);
                saveIntoPOInvoiceDutiesTaxesEdit(duties_taxes, mPurchaseTranx, taxFlag);
                /* save into Additional Charges  */
                String jsonStr = request.getParameter("row");
                JsonParser parser = new JsonParser();
                JsonElement purDetailsJson = parser.parse(jsonStr);
                JsonArray array = purDetailsJson.getAsJsonArray();
                //JsonArray array = new JsonArray(jsonStr);
                saveIntoPOInvoiceDetails(array, mPurchaseTranx,
                        branch, outlet, users.getId(), tranxType, "update");
            }
        } catch (DataIntegrityViolationException e) {
            System.out.println("Exception:" + e.getMessage());

        } catch (Exception e1) {
            System.out.println("Exception:" + e1.getMessage());
        }
        return mPurchaseTranx;
    }

    private void saveIntoPOInvoiceDutiesTaxesEdit(JsonObject duties_taxes,
                                                  TranxPurOrder mPurchaseTranx, Boolean taxFlag) {
    }

    public JsonObject getPOInvoiceIds(HttpServletRequest request) {
        JsonArray array = new JsonArray();
        JsonObject result = new JsonObject();
        try {
            String json = request.getParameter("ids");
            JsonParser parser = new JsonParser();
            JsonElement tradeElement = parser.parse(json);
            array = tradeElement.getAsJsonArray();
            for (JsonElement mList : array) {
                Long id = mList.getAsLong();
                TranxPurOrder mOrder = tranxPurOrderRepository.findByIdAndStatus(id, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error:" + e.getMessage());
        }
        return result;
    }
}
