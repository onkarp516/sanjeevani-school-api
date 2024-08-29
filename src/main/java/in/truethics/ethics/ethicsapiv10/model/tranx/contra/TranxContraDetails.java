package in.truethics.ethics.ethicsapiv10.model.tranx.contra;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.LedgerMaster;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
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
@Table(name = "tranx_contra_details_tbl")
public class TranxContraDetails {

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
    @JoinColumn(name = "ledger_id")
    @JsonManagedReference
    private LedgerMaster ledgerMaster;

    @ManyToOne
    @JoinColumn(name = "tranx_contra_master_tbl")
    @JsonManagedReference
    private TranxContraMaster tranxContraMaster;

    private String type; //Cr or Dr
    private String ledgerType;
    private String ledgerName;
    private Double paidAmount;

    private String payment_type; //cash ,cheque, UPI id
    private String bankName;
    private String bankPaymentNo;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long createdBy;
    private Boolean status;
}
