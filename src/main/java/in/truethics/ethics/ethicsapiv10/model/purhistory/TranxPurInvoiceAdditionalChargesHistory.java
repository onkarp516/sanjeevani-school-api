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
@Table(name = "tranx_purchase_invoice_additional_charges_history_tbl")
public class TranxPurInvoiceAdditionalChargesHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tranxPurInvoiceAdditionalChargesId;
    private Long purchaseTransactionId;
    private Long additionalChargesId;
    private Double amount;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long createdBy;
    private Boolean status;
}
