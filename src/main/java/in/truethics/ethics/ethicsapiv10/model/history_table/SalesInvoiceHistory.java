package in.truethics.ethics.ethicsapiv10.model.history_table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tranx_sales_invoice_history_tbl")

public class SalesInvoiceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tranxSalesInvoiceId;
    private Long branchId;
    private Long outletId;
    private Long sundry_debtors_id;
    private Long fees_account_ledger_id;//LedgerMaster
    private Long fees_asso_account_ledger_id;//AssociateGroups
    private Long sales_discount_ledger_id;//LedgerMaster
    private Long fiscal_year_id;

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
    private String operationType;

    private Long createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long updatedBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Double balance;
}
