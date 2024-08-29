package in.truethics.ethics.ethicsapiv10.model.school_master;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "installment_details_tbl")
public class InstallmentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "installment_master_id")
    @JsonManagedReference
    private InstallmentMaster installmentMaster;

    @ManyToOne
    @JoinColumn(name = "fee_head_id")
    @JsonManagedReference
    private FeeHead feeHead;

    @ManyToOne
    @JoinColumn(name = "sub_fee_head_id")
    @JsonManagedReference
    private SubFeeHead subFeeHead;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    @JsonManagedReference
    private Outlet outlet;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonManagedReference
    private Branch branch;

    private Double priority;
    private Double amount;
    private Double boysAmount;
    private Double girlsAmount;

    private Long createdBy;
    private Long updatedBy;
    private Boolean status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
