package in.truethics.ethics.ethicsapiv10.model.purhistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tranx_purchase_invoice_details_history_tbl")
public class TranxPurInvoiceDetailsHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tranxPurInvoiceDetailsId;
    private Long purchaseTransactionId;
    private Long productId;
    private Double base_amt;
    private Double totalAmount;
    private Double discountAmount;
    private Double discountPer;
    private Double discountAmountCal;
    private Double discountPerCal;
    private Double igst;
    private Double sgst;
    private Double cgst;
    private Double totalIgst;
    private Double totalSgst;
    private Double totalCgst;
    private Double finalAmount;
    private Double qtyHigh;
    private Double rateHigh;
    private Double qtyMedium;
    private Double rateMedium;
    private Double qtyLow;
    private Double rateLow;
    private Double baseAmtHigh;
    private Double baseAmtLow;
    private Double baseAmtMedium;
    private Boolean status;
    private String operations;
    private String referenceId; // id of poId or PCId
    private String referenceType; // purchase_order or purchase_challan
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long createdBy;

}
