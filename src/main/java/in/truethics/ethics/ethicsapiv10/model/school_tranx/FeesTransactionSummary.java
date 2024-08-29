package in.truethics.ethics.ethicsapiv10.model.school_tranx;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import in.truethics.ethics.ethicsapiv10.model.school_master.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "fees_transaction_summary_tbl")
public class FeesTransactionSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double totalFees;
    private Double paidAmount;
    private Double balance;
    private Double discount;

    private Integer studentType; // 1=> DayScholar, 2=> Residential
    private Integer studentGroup; // 1=> PCM, 2=> PCB
    private Integer concessionType; // 1=> Special Concession, 2=> Scholarship, 0=> Not Applicable
    private Boolean isManual; // 1=> Manual fees paid, 0=> Installment wise paid
    private Integer concessionAmount;

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
    @JoinColumn(name = "standard_id")
    @JsonManagedReference
    private Standard standard;

    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    @JsonManagedReference
    private AcademicYear academicYear;

    @ManyToOne
    @JoinColumn(name = "division_id")
    @JsonManagedReference
    private Division division;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonManagedReference
    private StudentRegister studentRegister;

    @ManyToOne
    @JoinColumn(name = "fees_master_id")
    @JsonManagedReference
    private FeesMaster feesMaster;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<FeesTransactionMaster> feesTransactionMasters;
}
