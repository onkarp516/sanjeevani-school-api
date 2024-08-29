package in.truethics.ethics.ethicsapiv10.model.school_tranx;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import in.truethics.ethics.ethicsapiv10.model.school_master.FeeHead;
import in.truethics.ethics.ethicsapiv10.model.school_master.SubFeeHead;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fees_transaction_detail_tbl")
public class FeesTransactionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
}
