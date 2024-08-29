package in.truethics.ethics.ethicsapiv10.model.history_table;

import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ledger_balance_summary_history_tbl")
public class LedgerBalanceSummaryHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ledgerBalanceSummaryId;
    private String foundationsName;
    private String principlesName;
    private String principlesGroupName;
    private String associateGroupName;
    private Long  branchId;
    private Long outletId;
    private String ledgerMasterName;

    private Double debit;
    private Double credit;
    private Double openingBal;
    private Double closingBal;
    private Double balance;
    private String underPrefix;
    private String operationType;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean status;
}
