package in.truethics.ethics.ethicsapiv10.model.school_tranx;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import in.truethics.ethics.ethicsapiv10.model.school_master.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "student_fees_master")
public class StudentFeesMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String FiscalYear;

    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    @JsonManagedReference
    private AcademicYear academicYear;
    @ManyToOne
    @JoinColumn(name = "division_id")
    @JsonManagedReference
    private Division division;

    @ManyToOne
    @JoinColumn(name = "standard_id")
    @JsonManagedReference
    private Standard standard;

    @ManyToOne
    @JoinColumn(name = "student_register_id")
    @JsonManagedReference
    private StudentRegister studentRegister;

    @ManyToOne
    @JoinColumn(name = "student_admission_id")
    @JsonManagedReference
    private StudentAdmission studentAdmission;

    @ManyToOne
    @JoinColumn(name = "fees_head_id")
    @JsonManagedReference
    private FeeHead feeHead;

    @ManyToOne
    @JoinColumn(name = "sub_fees_head_id")
    @JsonManagedReference
    private SubFeeHead subFeeHead;


    private Double feeHeadAmount;
    private  Double negotiatedFeesHeadAmount;

    private Double TotalFee;
    private Double negotiatedTotalAmount;






    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonManagedReference
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    @JsonManagedReference
    private Outlet outlet;

    private Long createdBy;
    private Long updatedBy;
    private Boolean status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
