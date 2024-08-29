package in.truethics.ethics.ethicsapiv10.model.history_table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tranx_journal_details_history_tbl")
public class JournalDetailsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tranxJournalDetailsId;
    private Long  branchId;
    private Long outletId;
    private Long ledgerMasterId;
    private Long journalMasterId;

    private String type; //Cr or Dr
    private String ledgerType;
    private Double paidAmount;
    private String operationType;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long createdBy;
    private Boolean status;
}
