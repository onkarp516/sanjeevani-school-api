package in.truethics.ethics.ethicsapiv10.model.history_table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ledger_transaction_details_history_tbl")
public class LedgerTranxDetailHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ledgerTransactionDetailsId;
    private String principlesName;
    private String principlesGroupName;
    private String foundationsName;
    private Long  branchId;
    private Long outletId;
    private String transactionTypeName;
    private String balancingMethodName;
    private String associateGroupName;
    private Long studentId;

    private String paymentStatus;
    private Double debit;
    private Double credit;
    private Double openingBal;
    private Double closingBal;
    private LocalDate transactionDate;
    private LocalDate paymentDate;
    private Long transactionId; // Transaction Id (purchase,sale)
    private String tranxType;
    private String financialYear;
    private String underPrefix;
    private String operationType;


    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long createdBy;
    private Long updatedBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean status;
}
