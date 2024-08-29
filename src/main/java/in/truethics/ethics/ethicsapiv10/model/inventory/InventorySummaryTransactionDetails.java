package in.truethics.ethics.ethicsapiv10.model.inventory;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import in.truethics.ethics.ethicsapiv10.model.master.TransactionTypeMaster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "inventory_summary_transaction_details_tbl")
public class InventorySummaryTransactionDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double openingStock;
    private Double stockIn;
    private Double stockOut;
    private String tranxAction; //in or out
    private Double closingStock;
    private LocalDate tranxDate;
    private Long tranxId;
    private Double hUnitConvesion;
    private Double mUnitConversion;
    private Double lUnitConversion;
    private String financialYear;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Boolean status;
    private Double valuation; // Valuation (qnt*purchase_rate)
    private Double avgValuation;  // valuation/closing_stock
    private Double purPrice;
    private Double salesPrice;
    private String uniqueBatchNo;
    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonManagedReference
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    @JsonManagedReference
    private Outlet outlet;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonManagedReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "transaction_type_id")
    @JsonManagedReference
    private TransactionTypeMaster transactionType;

    @ManyToOne
    @JoinColumn(name = "packaging_id")
    @JsonManagedReference
    private PackingMaster packingMaster;
}
