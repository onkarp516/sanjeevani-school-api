package in.truethics.ethics.ethicsapiv10.model.tranx.receipt;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.LedgerMaster;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tranx_receipt_perticulars_tbl")
public class TranxReceiptPerticulars {
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
    @JoinColumn(name = "tranx_receipt_master_id")
    @JsonManagedReference
    private TranxReceiptMaster tranxReceiptMaster;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxReceiptPerticularsDetails> tranxReceiptPerticularsDetails;


    private String type;
    private String ledgerType;
    private String ledgerName;
    private double dr;
    private double cr;
    private String paymentMethod;
    private String paymentTranxNo;
    private LocalDate transactionDate;
    private boolean status;
    private Long tranxInvoiceId;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long createdBy;
}
