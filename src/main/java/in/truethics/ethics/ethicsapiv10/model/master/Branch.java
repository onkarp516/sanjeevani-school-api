package in.truethics.ethics.ethicsapiv10.model.master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.inventory.*;
import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerBalanceSummary;
import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerTransactionDetails;
import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesInvoiceDetails;
import in.truethics.ethics.ethicsapiv10.model.school_master.*;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionDetail;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionSummary;
import in.truethics.ethics.ethicsapiv10.model.user.UserRole;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "branch_tbl")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String branchName;
    private String branchCode;
    private Long mobileNumber;
    private String branchContactPerson;
    private Long createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Boolean status;
    private String companyName;
    private String companyCode;
    private String registeredAddress;
    private String corporateAddress;
    private String pincode;
    private Long whatsappNumber;
    private String email;
    private String website;
    private Boolean gstApplicable;
    private String gstNumber;
    private LocalDate gstApplicableDate;
    private String stateCode;
    private String currency;

    private Long affiliationNo; // for LC
    private Long grNo; // for LC & Bonafide
    private Long udiseNo; // for LC

    @ManyToOne
    @JoinColumn(name = "outlet_id", nullable = false)
    @JsonManagedReference
    private Outlet outlet;

    @ManyToOne
    @JoinColumn(name = "gst_type_master_id", nullable = false)
    @JsonManagedReference
    private GstTypeMaster gstTypeMaster;

    @ManyToOne
    @JoinColumn(name = "country_id")
    @JsonManagedReference
    private Country country;

    @ManyToOne
    @JoinColumn(name = "state_id")
    @JsonManagedReference
    private State state;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<UserRole> userRoles;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Users> users;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<LedgerMaster> ledgerMasters;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<AssociateGroups> associateGroups;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<LedgerBalanceSummary> ledgerBalanceSummaries;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<LedgerTransactionDetails> ledgerTransactionDetails;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Product> product;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Units> units;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<ProductHsn> productHsns;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TaxMaster> taxMasters;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Category> category;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Group> groups;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Subcategory> subcategory;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Subgroup> subgroups;

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
    private List<PackingMaster> packingMasters;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<FeeHead> feeHeads;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<FeesMaster> feesMasters;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<AcademicYear> academicYears;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Standard> standards;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Division> divisions;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<FeesDetails> feesDetails;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<StudentRegister> studentRegisters;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<StudentAdmission> studentAdmissions;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<FeesTransactionSummary> feesTransactionSummaries;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<FeesTransactionDetail> feesTransactionDetails;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<InstallmentMaster> installmentMasters;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxSalesInvoiceDetails> tranxSalesInvoiceDetails;
}
