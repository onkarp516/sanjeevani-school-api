package in.truethics.ethics.ethicsapiv10.model.school_tranx;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import in.truethics.ethics.ethicsapiv10.model.school_master.FeeHead;
import in.truethics.ethics.ethicsapiv10.model.school_master.StudentRegister;
import in.truethics.ethics.ethicsapiv10.model.school_master.SubFeeHead;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "fees_transaction_master_tbl")
public class FeesTransactionMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long createdBy;
    private Long updatedBy;
    private Boolean status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    @JsonManagedReference
    private Outlet outlet;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonManagedReference
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonManagedReference
    private StudentRegister studentRegister;

    @ManyToOne
    @JoinColumn(name = "fees_transaction_summary_id")
    @JsonManagedReference
    private FeesTransactionSummary feesTransactionSummary;

    @ManyToOne
    @JoinColumn(name = "fee_head_id")
    @JsonManagedReference
    private FeeHead feeHead;

    @ManyToOne
    @JoinColumn(name = "sub_fee_head_id")
    @JsonManagedReference
    private SubFeeHead subFeeHead;

    private Double feeAmount;
    private Double installment1;
    private Double installment2;
    private Double installment3;
    private Double installment4;
    private Double installment5;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<FeesTransactionDetail> feesTransactionDetails;
}
