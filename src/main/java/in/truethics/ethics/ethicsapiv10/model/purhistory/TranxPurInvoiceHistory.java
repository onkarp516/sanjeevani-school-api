package in.truethics.ethics.ethicsapiv10.model.purhistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tranx_purchase_invoice_history_tbl")
public class TranxPurInvoiceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long purchaseInvoiceId;
    private Long fiscalYearId;
    private Long branchId;
    private Long outletId;
    private Long sundryCreditorsId;
    private Long purchaseAccountLedgerId;
    private Long purchaseDiscountLedgerId;
    private Long purchaseRoundOffId;
    private Long associatesGroupsId;
    private Long srno;
    private String vendorInvoiceNo;
    private LocalDate transactionDate;
    private LocalDate invoiceDate;
    private String transportName;
    private String reference;
    private Double roundOff;
    private Double totalBaseAmount;  //qty*base_amount
    private Double totalAmount;
    private Double totalcgst;
    private Long totalqty;
    private Double totalsgst;
    private Double totaligst;
    private Double purchaseDiscountAmount;
    private Double purchaseDiscountPer;
    private Double totalPurchaseDiscountAmt;
    private Double additionalChargesTotal;
    private Double taxableAmount;
    private Double tcs;
    private Long createdBy;
    private Boolean status;
    private String financialYear;
    private String narration;
    private String operations; //insertion , updatation , deletion
    /* Purchase Order and Purchase Challan reference */
    private String poId; //Purchase Order Id
    private String pcId; // Purchase Challan Id
    @CreationTimestamp
    private LocalDateTime createdAt;
}
