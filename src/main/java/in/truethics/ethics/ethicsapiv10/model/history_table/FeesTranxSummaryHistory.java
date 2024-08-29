package in.truethics.ethics.ethicsapiv10.model.history_table;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fees_transaction_summary_history_tbl")
public class FeesTranxSummaryHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long feesTransactionSummaryId;
    private Double totalFees;
    private Double paidAmount;
    private Double balance;

    private Integer studentType; // 1=> DayScholar, 2=> Residential
    private Integer studentGroup; // 1=> PCM, 2=> PCB
    private Integer concessionType; // 1=> Special Concession, 2=> Scholarship, 0=> Not Applicable
    private Boolean isManual; // 1=> Manual fees paid, 0=> Installment wise paid
    private Integer concessionAmount;
    private String operationType;


    private Long createdBy;
    private Long updatedBy;
    private Boolean status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long outletId;
    private Long branchId;
    private Long standardName;
    private String divisionName;
    private String academicYear;
    private Long studentId;
    private Long feesMasterId;

}
