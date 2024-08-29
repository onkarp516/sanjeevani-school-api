package in.truethics.ethics.ethicsapiv10.model.inventory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurInvoiceDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurInvoiceDetailsUnits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_tbl")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private String productCode;
    private Double price;
    private Boolean status;
    private String description;
    private String alias;
    private Boolean isWarrantyApplicable;
    private int warrantyDays;
    private Boolean isSerialNumber;
    private Boolean isNegativeStocks;
    private Boolean isDraft;
    private Boolean isInventory;
    private Long createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private LocalDate applicableDate;
    private String image;
    private Integer unitCount;
    private Boolean isMultiUnits;
    private Boolean isPackings;
    private Integer packingCount;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonManagedReference
    private Group group;

    @ManyToOne
    @JoinColumn(name = "subgroup_id")
    @JsonManagedReference
    private Subgroup subgroup;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonManagedReference
    private Category category;

    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    @JsonManagedReference
    private Subcategory subcategory;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonManagedReference
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    @JsonManagedReference
    private Outlet outlet;

    @ManyToOne
    @JoinColumn(name = "hsn_id")
    @JsonManagedReference
    private ProductHsn productHsn;

    @ManyToOne
    @JoinColumn(name = "taxmaster_id")
    @JsonManagedReference
    private TaxMaster taxMaster;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<ProductUnitPacking> productUnitPackings;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<InventorySummary> inventorySummaries;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<InventorySummaryTransactionDetails> inventorySummaryTransactionDetails;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<InventorySerialNumberSummary> inventorySerialNumberSummaries;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<InventorySerialNumberSummaryDetails> inventorySerialNumberSummaryDetails;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<ProductOpeningStocks> productOpeningStocks;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<ProductImagesMaster> productImagesMasters;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxPurInvoiceDetailsUnits> tranxPurInvoiceDetailsUnits;


    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxPurInvoiceDetails> tranxPurInvoiceDetails;
}
