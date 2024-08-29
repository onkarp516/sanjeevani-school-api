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
@Table(name = "tranx_purchase_order_details_units_tbl")
public class TranxPurOrderDetailsUnits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    @JsonManagedReference
    private TranxPurOrder tranxPurOrder;

    @ManyToOne
    @JoinColumn(name = "purchase_order_details_id")
    @JsonManagedReference
    private TranxPurOrderDetails tranxPurOrderDetails;

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
