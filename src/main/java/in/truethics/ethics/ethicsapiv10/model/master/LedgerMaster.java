package in.truethics.ethics.ethicsapiv10.model.master;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerBalanceSummary;
import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerTransactionDetails;
import in.truethics.ethics.ethicsapiv10.model.school_master.FeeHead;
import in.truethics.ethics.ethicsapiv10.model.school_master.StudentRegister;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ledger_master_tbl")
public class LedgerMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ledgerName;
    @Column(unique = true)
    private String ledgerCode;
    private String uniqueCode;
    private String mailingName;
    private String openingBalType;
    private Double openingBal;
    private String address;
    private Long pincode;
    private String email;
    private Long mobile;
    private Boolean taxable;
    private String gstin;
    private String stateCode;
    private Long registrationType;
    private LocalDate dateOfRegistration;
    private String pancard;
    private String bankName;
    private String accountNumber;
    private String ifsc;
    private String bankBranch;
    private String taxType;
    private String slugName;
    private Long createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long updatedBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean status;
    private String underPrefix;
    private Boolean isDeleted;
    private Boolean isDefaultLedger;
    private Boolean isPrivate;
    /* pune visit new changes */
    private Integer creditDays;
    private String applicableFrom; //from billDate or deliveryDate
    private String foodLicenseNo;
    private Boolean tds;
    private LocalDate tdsApplicableDate;
    private Boolean tcs;
    private LocalDate tcsApplicableDate;
    private String district;
    /* End .... */

    @ManyToOne
    @JoinColumn(name = "principle_id")
    @JsonManagedReference
    private Principles principles;

    @ManyToOne
    @JoinColumn(name = "principle_groups_id")
    @JsonManagedReference
    private PrincipleGroups principleGroups;

    @ManyToOne
    @JoinColumn(name = "foundation_id")
    @JsonManagedReference
    private Foundations foundations;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonManagedReference
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    @JsonManagedReference
    private Outlet outlet;

    @ManyToOne
    @JoinColumn(name = "country_id")
    @JsonManagedReference
    private Country country;

    @ManyToOne
    @JoinColumn(name = "state_id")
    @JsonManagedReference
    private State state;

    @ManyToOne
    @JoinColumn(name = "balancing_method_id")
    @JsonManagedReference
    private BalancingMethod balancingMethod;

    @ManyToOne
    @JoinColumn(name = "associates_groups_id")
    @JsonManagedReference
    private AssociateGroups associateGroups;

    @OneToOne
    @JoinColumn(name = "student_id")
    @JsonManagedReference
    private StudentRegister studentRegister;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<LedgerTransactionDetails> ledgerTransactionDetails;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<LedgerBalanceSummary> ledgerBalanceSummaries;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<LedgerDeptDetails> ledgerDeptDetails;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<LedgerGstDetails> ledgerGstDetails;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<LedgerShippingAddress> ledgerShippingAddresses;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<LedgerBillingDetails> ledgerBillingDetails;


    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<FeeHead> feeHeads;
}
