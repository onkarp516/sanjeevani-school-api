package in.truethics.ethics.ethicsapiv10.model.history_table;

import in.truethics.ethics.ethicsapiv10.model.school_master.FeeHead;
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
@Table(name = "tranx_sales_invoice_details_history_tbl")
public class SalesInvoiceDetailsHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tranxSalesInvoiceDetailsId;
    private Long fiscalYearId;
    private Long sales_invoice_id;
    private Long fee_head_id;
    private Double amount;
    private Boolean status;
    private Long branchId;
    private Long outletId;
    private Long createdBy;
    private String operationType;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long updatedBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
