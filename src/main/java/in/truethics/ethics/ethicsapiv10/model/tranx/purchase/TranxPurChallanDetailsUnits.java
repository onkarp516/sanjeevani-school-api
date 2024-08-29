package in.truethics.ethics.ethicsapiv10.model.tranx.purchase;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.inventory.Product;
import in.truethics.ethics.ethicsapiv10.model.inventory.Units;
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
@Table(name = "tranx_purchase_challan_details_units_tbl")
public class TranxPurChallanDetailsUnits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "purchase_challan_id")
    @JsonManagedReference
    private TranxPurChallan tranxPurChallan;

    @ManyToOne
    @JoinColumn(name = "purchase_challan_details_id")
    @JsonManagedReference
    private TranxPurChallanDetails tranxPurChallanDetails;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonManagedReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    @JsonManagedReference
    private Units units;
    private Double unitConversions;
    private Double qty;
    private Double rate;
    private Double baseAmt;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long createdBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private Boolean status;


}
