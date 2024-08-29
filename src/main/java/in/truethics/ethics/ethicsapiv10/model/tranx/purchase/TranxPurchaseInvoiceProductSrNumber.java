package in.truethics.ethics.ethicsapiv10.model.tranx.purchase;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.inventory.Product;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import in.truethics.ethics.ethicsapiv10.model.master.TransactionTypeMaster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tranx_purchase_invoice_product_sr_no_tbl")
public class TranxPurchaseInvoiceProductSrNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "purchase_invoice_id", nullable = true)
    @JsonManagedReference
    private TranxPurInvoice purchaseTransaction;

    @ManyToOne
    @JoinColumn(name = "purchase_invoice_details_id", nullable = true)
    @JsonManagedReference
    private TranxPurInvoiceDetails tranxPurInvoiceDetails;

    @ManyToOne
    @JoinColumn(name = "transaction_type_master_id", nullable = true)
    @JsonManagedReference
    private TransactionTypeMaster transactionTypeMaster;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonManagedReference
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    @JsonManagedReference
    private Outlet outlet;

    private String serialNo;
    private LocalDateTime purchaseCreatedAt;
    private String transactionStatus; //purchase or sales or counter sales
    private String operations;
    private Long createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long updatedBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean status;
}
