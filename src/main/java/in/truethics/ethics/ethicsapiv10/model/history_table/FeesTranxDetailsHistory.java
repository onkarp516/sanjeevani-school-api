package in.truethics.ethics.ethicsapiv10.model.history_table;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fees_transaction_detail_history_tbl")
public class FeesTranxDetailsHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long feeTransactionDetailId;
    private String receiptNo; // branch_short_code+academicyear-mmdd-001 -> SGJ2223-0529-001, SGS2223-0529-001
    private LocalDate transactionDate;
    private Double headFee;
    private Double opening;
    private Double amount;
    private Double balance; // closing
    private Double paidAmount; // closing
    private Double concessionAmount;
    private Double specialConcessionAmount;
    private Integer installmentNo;
    private Integer paymentMode;
    private String paymentNo;

    private Long createdBy;
    private Long updatedBy;
    private Boolean status;
    private String operationType;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long feesTransactionSummaryId;
    private Long feesHeadId;
    private Long subFeesHeadId;


    private Long branchId;
    private Long OutletId;
}
