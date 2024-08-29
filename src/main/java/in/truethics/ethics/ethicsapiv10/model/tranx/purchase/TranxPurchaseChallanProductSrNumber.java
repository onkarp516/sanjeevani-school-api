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
@Table(name = "tranx_purchase_challan_product_sr_no_tbl")
public class TranxPurchaseChallanProductSrNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonManagedReference
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "outlet_id", nullable = false)
    @JsonManagedReference
    private Outlet outlet;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "tranx_pur_challan_id", nullable = true)
    @JsonManagedReference
    private TranxPurChallan tranxPurChallan;


    @ManyToOne
    @JoinColumn(name = "tranx_pur_challan_details_id", nullable = true)
    @JsonManagedReference
    private TranxPurChallanDetails tranxPurChallanDetails;

    @ManyToOne
    @JoinColumn(name = "transaction_type_master_id", nullable = true)
    @JsonManagedReference
    private TransactionTypeMaster transactionTypeMaster;

    private String serialNo;
    private LocalDateTime purchaseCreatedAt;
    private LocalDateTime saleCreatedAt;
    private String transactionStatus;
    private String operations;
    private Long createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long updatedBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean status;
}

