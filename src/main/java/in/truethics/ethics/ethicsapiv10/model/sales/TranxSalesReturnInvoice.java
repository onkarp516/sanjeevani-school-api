package in.truethics.ethics.ethicsapiv10.model.sales;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tranx_sales_return_invoice_tbl")
public class TranxSalesReturnInvoice {
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
    @JoinColumn(name = "fees_account_id")
    @JsonManagedReference
    private LedgerMaster feesAccount;


    @ManyToOne
    @JoinColumn(name = "fees_asso_account_ledger_id")
    @JsonManagedReference
    private AssociateGroups feesAct;


    @ManyToOne
    @JoinColumn(name = "sales_invoice_id")
    @JsonManagedReference
    private TranxSalesInvoice tranxSalesInvoice;

    @ManyToOne
    @JoinColumn(name = "fiscal_year_id")
    @JsonManagedReference
    private FiscalYear fiscalYear;

    private Long salesRtnSrNo;
    private String salesReturnNo; // Sales ReturnNo is sundry debtors billNo
    private LocalDate transactionDate;
    private Double roundOff;
    private Double totalBaseAmount;  //qty*base_amount
    private Double totalAmount;
    private Long totalqty;
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
    private LocalDateTime createdDate;
}
