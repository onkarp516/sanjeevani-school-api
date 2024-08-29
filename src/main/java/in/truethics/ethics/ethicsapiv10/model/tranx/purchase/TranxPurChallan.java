package in.truethics.ethics.ethicsapiv10.model.tranx.purchase;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.tranx.debit_note.TranxDebitNoteNewReferenceMaster;
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
@Table(name = "tranx_purchase_challan_tbl")
public class TranxPurChallan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonManagedReference
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    @JsonManagedReference
    private Outlet outlet;

    @ManyToOne
    @JoinColumn(name = "sundry_creditors_id")
    @JsonManagedReference
    private LedgerMaster sundryCreditors;

    @ManyToOne
    @JoinColumn(name = "purchase_account_ledger_id")
    @JsonManagedReference
    private LedgerMaster purchaseAccountLedger;

    @ManyToOne
    @JoinColumn(name = "purchase_roundoff_id")
    @JsonManagedReference
    private LedgerMaster purchaseRoundOff;

    @ManyToOne
    @JoinColumn(name = "transaction_status_id", nullable = false)
    @JsonManagedReference
    private TransactionStatus transactionStatus;

    @ManyToOne
    @JoinColumn(name = "fiscal_year_id")
    @JsonManagedReference
    private FiscalYear fiscalYear;


    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxPurchaseChallanProductSrNumber> tranxPurchaseChallanProductSrNumbers;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxPurChallanDetails> tranxPurChallanDetails;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxPurChallanDutiesTaxes> tranxPurChallanDutiesTaxes;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxPurReturnInvoice> tranxPurReturnInvoices;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxDebitNoteNewReferenceMaster> tranxDebitNoteNewReferences;

    private Long purChallanSrno;
    private String vendorInvoiceNo;
    private String orderReference;
    private String referenceType;
    private LocalDate transactionDate;
    private LocalDate invoiceDate;
    private String transportName;
    private String reference;
    private Double roundOff;
    private Double totalBaseAmount;  //qty*base_amount
    private Double totalAmount;
    private Double totalcgst;
    private Long totalqty;
    private Double totalsgst;
    private Double totaligst;
    private Double taxableAmount;
    private Double tcs;
    private Boolean status;
    private String financialYear;
    private String narration;
    private String operations;
    private Long createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long updatedBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
