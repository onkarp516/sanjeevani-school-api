package in.truethics.ethics.ethicsapiv10.model.sales;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.*;
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
@Table(name = "tranx_sales_invoice_tbl")
public class TranxSalesInvoice {
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
    @JoinColumn(name = "sundry_debtors_id")
    @JsonManagedReference
    private LedgerMaster sundryDebtors;

    @ManyToOne
    @JoinColumn(name = "fees_account_ledger_id")
    @JsonManagedReference
    private LedgerMaster feesAccounts;  // Not Used

    @ManyToOne
    @JoinColumn(name = "fees_asso_account_ledger_id")
    @JsonManagedReference
    private AssociateGroups feesAct;

    @ManyToOne
    @JoinColumn(name = "sales_discount_ledger_id")
    @JsonManagedReference
    private LedgerMaster salesDiscountLedger;

    @ManyToOne
    @JoinColumn(name = "fiscal_year_id")
    @JsonManagedReference
    private FiscalYear fiscalYear;

    private Long salesSerialNumber;
    private String salesInvoiceNo;
    private LocalDate billDate;
    private String reference;
    private Double roundOff;
    private Double totalBaseAmount;  //qty*base_amount
    private Double totalAmount;
    private Double totalcgst;
    private Long totalqty;
    private Double totalsgst;
    private Double totaligst;
    private Double salesDiscountAmount;
    private Double salesDiscountPer;
    private Double totalSalesDiscountAmt;
    private Double taxableAmount;
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
    private Double balance;


    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxSalesInvoiceDetails> tranxSalesInvoiceDetails;
}

