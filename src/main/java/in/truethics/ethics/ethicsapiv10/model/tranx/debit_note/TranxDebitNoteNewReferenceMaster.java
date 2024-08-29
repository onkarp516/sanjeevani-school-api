package in.truethics.ethics.ethicsapiv10.model.tranx.debit_note;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurChallan;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurInvoice;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurReturnInvoice;
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
@Table(name = "tranx_debit_note_new_reference_tbl")
public class TranxDebitNoteNewReferenceMaster {
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
    @JoinColumn(name = "sundry_creditor_id")
    @JsonManagedReference
    private LedgerMaster sundryCreditor;

    @ManyToOne
    @JoinColumn(name = "tranx_pur_invoice_id")
    @JsonManagedReference
    private TranxPurInvoice tranxPurInvoice;

    @ManyToOne
    @JoinColumn(name = "tranx_pur_challan_id")
    @JsonManagedReference
    private TranxPurChallan tranxPurChallan;

    @ManyToOne
    @JoinColumn(name = "tranx_pur_return_invoice_id")
    @JsonManagedReference
    private TranxPurReturnInvoice tranxPurReturnInvoice;

    @ManyToOne
    @JoinColumn(name = "transaction_status_id")
    @JsonManagedReference
    private TransactionStatus transactionStatus;

    @ManyToOne
    @JoinColumn(name = "fiscal_year_id")
    @JsonManagedReference
    private FiscalYear fiscalYear;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxDebitNoteDetails> tranxDebitNoteDetails;

    private Long srno;
    private String debitnoteNewReferenceNo; //auto generate
    private Double roundOff;
    private Double totalBaseAmount;  //qty*base_amount
    private Double totalAmount;
    private Double taxableAmount;
    private Double totalgst;
    private Double purchaseDiscountAmount;
    private Double purchaseDiscountPer;
    private Double totalPurchaseDiscountAmt;
    private Double additionalChargesTotal;
    private String financialYear;
    private String source;
    private LocalDate transcationDate;
    private String narrations;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long createdBy;
    private boolean status;
    private String adjustmentStatus; // immediate , future or refund
    private Double balance;

}
