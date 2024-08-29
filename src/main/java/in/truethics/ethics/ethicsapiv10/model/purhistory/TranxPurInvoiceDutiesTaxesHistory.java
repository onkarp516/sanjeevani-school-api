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
@Table(name = "tranx_purchase_invoice_duties_taxes_history_tbl")
public class TranxPurInvoiceDutiesTaxesHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tranxPurchaseInvoiceDutiesTaxesId;
    private Long purchaseTransactionId;
    private Long sundryCreditorsId;
    private Long dutiesTaxesId;
    private Boolean intra; //intra is for within state i.e cgst and sgst and inter is for outside state ie. igst
    private Double amount;
    private Boolean status;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
