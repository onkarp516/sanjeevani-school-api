package in.truethics.ethics.ethicsapiv10.model.inventory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/* units of individual product*/
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_unit_packing_tbl")
public class ProductUnitPacking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String unitType;
    private Double unitConversion;
    private Double unitConvMargn;
    private Double discountInPer;
    private Double discountInAmt;
    private Double purchaseRate;
    private Double salesRate;
    private Double mrp;
    private Double minQty;
    private Double maxQty;
    private Double minSalesRate;
    private Double openingQty;
    private Double openingValution;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "units_id")
    @JsonManagedReference
    private Units units;

    @ManyToOne
    @JoinColumn(name = "packing_master_id")
    @JsonManagedReference
    private PackingMaster packingMaster;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<ProductOpeningStocks> productOpeningStocks;

    private Long createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private Boolean status;
}

