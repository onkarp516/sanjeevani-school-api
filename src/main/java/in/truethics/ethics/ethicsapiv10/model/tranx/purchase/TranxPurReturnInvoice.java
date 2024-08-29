package in.truethics.ethics.ethicsapiv10.model.tranx.purchase;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.tranx.debit_note.TranxDebitNoteNewReferenceMaster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tranx_pur_return_invoice_tbl")
public class TranxPurReturnInvoice {
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
    @JoinColumn(name = "purchase_discount_ledger_id")
    @JsonManagedReference
    private LedgerMaster purchaseDiscountLedger;

    @ManyToOne
    @JoinColumn(name = "associates_groups_id")
    @JsonManagedReference
    private AssociateGroups associateGroups;

    @ManyToOne
    @JoinColumn(name = "purchase_roundoff_id")
    @JsonManagedReference
    private LedgerMaster purchaseRoundOff;

    @ManyToOne
    @JoinColumn(name = "tranx_pur_invoice_id")
    @JsonManagedReference
    private TranxPurInvoice tranxPurInvoice;

    @ManyToOne
    @JoinColumn(name = "tranx_pur_challan_id")
    @JsonManagedReference
    private TranxPurChallan tranxPurChallan;

    @ManyToOne
    @JoinColumn(name = "fiscal_year_id")
    @JsonManagedReference
    private FiscalYear fiscalYear;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxPurReturnInvoiceDetails> tranxPurReturnInvoiceDetails;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxPurReturnInvoiceAddCharges> tranxPurReturnInvoiceAddCharges;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxPurReturnInvoiceDutiesTaxes> tranxPurReturnInvoiceDutiesTaxes;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxPurReturnInvoiceProductSrNo> tranxPurReturnInvoiceProdSrNos;


    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxDebitNoteNewReferenceMaster> tranxDebitNoteNewReferences;

    private Long purReturnSrno;
    private String purRtnNo; //Auto Generated
    private LocalDate transactionDate;
    private Double roundOff;
    private Double totalBaseAmount;  //qty*base_amount
    private Double totalAmount;
    private Double totalcgst;
    private Long totalqty;
    private Double totalsgst;
    private Double totaligst;
    private Double purchaseDiscountAmount;
    private Double purchaseDiscountPer;
    private Double totalPurchaseDiscountAmt;
    private Double additionalChargesTotal;
    private Double taxableAmount;
    private Double tcs;
    private Boolean status;
    private String financialYear;
    private String narration;
    private String operations; //insertion , updatation , deletion
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long createdBy;
}
