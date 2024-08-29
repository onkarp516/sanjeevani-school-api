package in.truethics.ethics.ethicsapiv10.dto;

import in.truethics.ethics.ethicsapiv10.common.GenerateCoreLogic;
import in.truethics.ethics.ethicsapiv10.model.inventory.*;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import in.truethics.ethics.ethicsapiv10.model.master.TransactionTypeMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurReturnInvoiceProductSrNo;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurchaseChallanProductSrNumber;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurchaseInvoiceProductSrNumber;
import in.truethics.ethics.ethicsapiv10.repository.inventory_repository.*;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.TransactionTypeMasterRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Data
public class InventoryDTO {
    @Autowired
    private InventoryTransactionDetailsRepository inventoryDetailsRepo;
    @Autowired
    private TransactionTypeMasterRepository typeMasterRepository;
    @Autowired
    private ProductUnitRepository unitRepository;
    @Autowired
    private GenerateCoreLogic generateCoreLogic;
    @Autowired
    private InventorySummaryRepository inventorySummaryRepository;
    @Autowired
    private InventorySerialNumberSummaryDetailsRepository serialNumberSummaryDetailsRepository;
    @Autowired
    private InventorySerialNumberSummaryRepository serialNumberSummaryRepository;

    /* Inserting into Inventory Transaction Details : for every transaction maintaining in and out of stock */
    public Boolean insertIntoInventoryTranxDetails(Product product, LocalDate transactionDate,
                                                   String tranxType, Double qtyHigh, Double qtyMedium,
                                                   Double qtyLow, String tranxStatus,
                                                   Long transactionId, String financialYear, LocalDate invoiceDate,
                                                   Branch branch, Outlet outlet) {

        Boolean flag = false;
        InventorySummaryTransactionDetails inventorySummaryTransactionDetails = new InventorySummaryTransactionDetails();
        InventorySummaryTransactionDetails mInventoryDetails = null;
        mInventoryDetails = inventoryDetailsRepo.findLastRecord(product.getId());
        TransactionTypeMaster tranxTypeMaster = typeMasterRepository.findByTransactionNameIgnoreCase(tranxType);
        List<ProductUnitPacking> unitsList = unitRepository.findByProductId(product.getId());
        HashMap<String, Double> conversions = new HashMap<>();
        Double cloingBal = 0.0;
        Double openingBal = 0.0;
        Double qty = 0.0;

      /* this is for unit level logic
        // product has ore than one level of unit
        if(unitsList.size()>1) {
            for (ProductUnit mUnit : unitsList) {
                if (mUnit.getUnitType().equalsIgnoreCase("High")) {
                    conversions.put("High", mUnit.getUnitConversion());
                } else if (mUnit.getUnitType().equalsIgnoreCase("Medium")) {
                    conversions.put("Medium", mUnit.getUnitConversion());
                } else if (mUnit.getUnitType().equalsIgnoreCase("Low")) {
                    conversions.put("Low", mUnit.getUnitConversion());
                }
            }
        }
            // product has only one level of unit
        else{

        }
*/
        inventorySummaryTransactionDetails.setProduct(product);
        inventorySummaryTransactionDetails.setTranxDate(transactionDate);
        inventorySummaryTransactionDetails.setTransactionType(tranxTypeMaster);
        inventorySummaryTransactionDetails.setTranxId(transactionId);
        inventorySummaryTransactionDetails.setTranxAction(tranxStatus);//in or out
        inventorySummaryTransactionDetails.setHUnitConvesion(unitsList.get(0).getUnitConversion());
        inventorySummaryTransactionDetails.setMUnitConversion(0.0);
        inventorySummaryTransactionDetails.setLUnitConversion(0.0);
        inventorySummaryTransactionDetails.setBranch(branch);
        inventorySummaryTransactionDetails.setOutlet(outlet);

        /* Inventory of Purchase Transactions */
        if (tranxStatus.equalsIgnoreCase("STOCKIN")) {
            if (qtyHigh != 0) {
                qty = qtyHigh;
                inventorySummaryTransactionDetails.setStockIn(qty);
                inventorySummaryTransactionDetails.setStockOut(0.0);
                if (mInventoryDetails != null) {
                    openingBal = mInventoryDetails.getClosingStock();
                    cloingBal = generateCoreLogic.generateCrLogic(openingBal, 0.0, qty);

                } else {
                    cloingBal = generateCoreLogic.generateCrLogic(0.0, 0.0, qty);
                }
            }
            if (qtyMedium != 0) {

            }
            if (qtyLow != 0) {

            }
        }
        /* Inventory of Sales Transactions */
        else {
            if (qtyHigh != 0) {
                // conversions.put("High", unitsList.get(0).getUnitConversion());
                qty = qtyHigh * -1;
                inventorySummaryTransactionDetails.setStockOut(qty);
                inventorySummaryTransactionDetails.setStockIn(0.0);
                if (mInventoryDetails != null) {
                    openingBal = mInventoryDetails.getClosingStock();
                    cloingBal = generateCoreLogic.generateDrLogic(openingBal, qty, 0.0);
                } else {
                    cloingBal = generateCoreLogic.generateDrLogic(openingBal, qty, 0.0);
                }
            }
        }
        inventorySummaryTransactionDetails.setClosingStock(cloingBal);
        inventorySummaryTransactionDetails.setOpeningStock(openingBal);
        inventorySummaryTransactionDetails.setFinancialYear(financialYear);
        inventorySummaryTransactionDetails.setStatus(true);
        flag = inventoryDetailsRepo.save(inventorySummaryTransactionDetails) != null;
        if (flag) {
            flag = insertIntoInventorySummaryDetails(product,
                    transactionDate, invoiceDate, qty, branch, outlet);
        }
        return flag;
    }

    /* insert into Inventory Summary (Current Stock as of Date) */
    public Boolean insertIntoInventorySummaryDetails(Product product, LocalDate tranxDate,
                                                     LocalDate invoiceDate, Double qty, Branch branch, Outlet outlet) {

        InventorySummary inventoryBalance = null;
        Boolean flag = false;
        inventoryBalance = inventorySummaryRepository.findByProductId(product.getId());
        /* Stock summary of Product */
        if (inventoryBalance != null) {

            Double closingStock = inventoryBalance.getClosingStock() + qty;
            inventoryBalance.setClosingStock(closingStock);
        } else {
            inventoryBalance = new InventorySummary();
            Double closingStock = 0.0 + qty;
            inventoryBalance.setClosingStock(closingStock);
        }
        inventoryBalance.setProduct(product);
        inventoryBalance.setBranch(branch);
        inventoryBalance.setOutlet(outlet);

        try {
            inventorySummaryRepository.save(inventoryBalance);
            flag = true;
        } catch (DataIntegrityViolationException e) {
            flag = false;
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e1) {
            flag = false;
            e1.printStackTrace();
            System.out.println(e1.getMessage());
        }

        return flag;
    }

    /* maitaining of inventory for serial number of Purchase invoice Tranxs */
    public void saveIntoSerialTranxSummaryDetails(List<TranxPurchaseInvoiceProductSrNumber>
                                                          newSerialNumbers, String tranxType) {
        Boolean flag = false;
        String inoutType = "STOCKIN";
        List<InventorySerialNumberSummaryDetails> inventoryList = new ArrayList<>();
        TransactionTypeMaster tranxTypeMaster = typeMasterRepository.findByTransactionNameIgnoreCase(tranxType);
        for (TranxPurchaseInvoiceProductSrNumber mSerialNumber : newSerialNumbers) {
            insertIntoSerialNumbersGeneric(mSerialNumber.getProduct(),
                    tranxTypeMaster, mSerialNumber.getSerialNo(), inoutType, mSerialNumber.getTranxPurInvoiceDetails()
                            .getPurchaseTransaction().getTransactionDate());
        }
    }

    /* maitaining of inventory for serial number of Purchase challan Tranxs */
    public void saveIntoSerialTranxSummaryDetailsPurChallan(List<TranxPurchaseChallanProductSrNumber>
                                                                    newSerialNumbers, String tranxType) {
        Boolean flag = false;
        String inoutType = "STOCKIN";
        List<InventorySerialNumberSummaryDetails> inventoryList = new ArrayList<>();
        TransactionTypeMaster tranxTypeMaster = typeMasterRepository.findByTransactionNameIgnoreCase(tranxType);
        for (TranxPurchaseChallanProductSrNumber mSerialNumber : newSerialNumbers) {
            insertIntoSerialNumbersGeneric(mSerialNumber.getProduct(),
                    tranxTypeMaster, mSerialNumber.getSerialNo(), inoutType, mSerialNumber.getTranxPurChallanDetails()
                            .getTranxPurChallan().getTransactionDate());
        }
    }

    /* maitaining of inventory for serial number of Sales challan Tranxs */
/*    public void saveIntoSerialTranxSummaryDetailsSalesChallan(List<TranxSalesChallanProductSerialNumber>
                                                                      newSerialNumbers, String tranxType) {
        Boolean flag = false;
        String inoutType = "STOCKOUT";
        List<InventorySerialNumberSummaryDetails> inventoryList = new ArrayList<>();
        TransactionTypeMaster tranxTypeMaster = typeMasterRepository.findByTransactionNameIgnoreCase(tranxType);
        for (TranxSalesChallanProductSerialNumber mSerialNumber : newSerialNumbers) {
            insertIntoSerialNumbersGeneric(mSerialNumber.getProduct(),
                    tranxTypeMaster, mSerialNumber.getSerialNo(), inoutType, mSerialNumber.getSalesInvoiceDetails()
                            .getSalesTransaction().getBillDate());
        }
    }*/
    /* maitaining of inventory for serial number of Sales invoice Tranxs */
  /*  public void saveIntoSerialTranxSummaryDetailsSalesInvoice(List<TranxSalesInvoiceProductSrNumber>
                                                                      newSerialNumbers, String tranxType) {
        Boolean flag = false;
        String inoutType = "STOCKOUT";
        List<InventorySerialNumberSummaryDetails> inventoryList = new ArrayList<>();
        TransactionTypeMaster tranxTypeMaster = typeMasterRepository.findByTransactionNameIgnoreCase(tranxType);
        for (TranxSalesInvoiceProductSrNumber mSerialNumber : newSerialNumbers) {
            insertIntoSerialNumbersGeneric(mSerialNumber.getProduct(),
                    tranxTypeMaster, mSerialNumber.getSerialNo(), inoutType, mSerialNumber.getTranxSalesInvoiceDetails()
                            .getSalesInvoice().getBillDate());
        }
    }*/

    private void insertIntoSerialNumbersGeneric(Product product,
                                                TransactionTypeMaster tranxTypeMaster, String serialNo, String inoutType,
                                                LocalDate transactionDate) {
        InventorySerialNumberSummaryDetails intserialNo = new InventorySerialNumberSummaryDetails();
        intserialNo.setProduct(product);
        intserialNo.setTransactionType(tranxTypeMaster);
        intserialNo.setSerialNo(serialNo);
        intserialNo.setTranxAction(inoutType);
        intserialNo.setTranxActionDate(transactionDate);
        intserialNo.setStatus(true);
        serialNumberSummaryDetailsRepository.save(intserialNo);
        saveIntoSerialNumberSummary(product, serialNo, inoutType, tranxTypeMaster);
    }

    public Boolean saveIntoSerialNumberSummary(Product product, String srno,
                                               String tranxStatus, TransactionTypeMaster tranxId) {
        Boolean flag = false;

        InventorySerialNumberSummary intserialNo = null;
        intserialNo = serialNumberSummaryRepository.findByProductIdAndSerialNo(product.getId(),
                srno);
        if (intserialNo != null) {
            intserialNo.setTranxAction(tranxStatus); //in or out
        } else {
            intserialNo = new InventorySerialNumberSummary();
            intserialNo.setProduct(product);
            intserialNo.setTranxAction(tranxStatus);  //in or out
            intserialNo.setSerialNo(srno);
        }
        intserialNo.setTransactionType(tranxId);
        try {
            serialNumberSummaryRepository.save(intserialNo);
            flag = true;
        } catch (DataIntegrityViolationException e1) {
            System.out.println(e1.getMessage());
            e1.printStackTrace();
            flag = false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /* maitaining of inventory for serial number of Purchase return invoice Tranxs */
    public void saveIntoSerialTranxSummaryDetailsPurReturn(List<TranxPurReturnInvoiceProductSrNo>
                                                                   newSerialNumbers, String tranxType) {
        Boolean flag = false;
        String inoutType = "STOCKOUT";
        List<InventorySerialNumberSummaryDetails> inventoryList = new ArrayList<>();
        TransactionTypeMaster tranxTypeMaster = typeMasterRepository.findByTransactionNameIgnoreCase(tranxType);
        for (TranxPurReturnInvoiceProductSrNo mSerialNumber : newSerialNumbers) {
            insertIntoSerialNumbersGeneric(mSerialNumber.getProduct(),
                    tranxTypeMaster, mSerialNumber.getSerialNo(), inoutType, mSerialNumber
                            .getPurReturnInvoice().getTransactionDate());
        }
    }

    /* maitaining of inventory for serial number of Sales return Tranxs */
   /* public void saveIntoSerialTranxSummaryDetailsSalesReturns(List<TranxSalesReturnProductSrNo>
                                                                      newSerialNumbers, String tranxType) {
        Boolean flag = false;
        String inoutType = "STOCKIN";
        List<InventorySerialNumberSummaryDetails> inventoryList = new ArrayList<>();
        TransactionTypeMaster tranxTypeMaster = typeMasterRepository.findByTransactionNameIgnoreCase(tranxType);
        for (TranxSalesReturnProductSrNo mSerialNumber : newSerialNumbers) {
            insertIntoSerialNumbersGeneric(mSerialNumber.getProduct(),
                    tranxTypeMaster, mSerialNumber.getSerialNo(), inoutType, mSerialNumber.getTranxSalesReturnInvoice().getTransactionDate()
            );
        }
    }*/
}
