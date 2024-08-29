package in.truethics.ethics.ethicsapiv10.model.school_master;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "student_admission_tbl")
public class StudentAdmission {
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
    @JoinColumn(name = "new_admitted_standard_id")
    @JsonManagedReference
    private Standard newAdmittedStandard;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonManagedReference
    private StudentRegister studentRegister;

    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    @JsonManagedReference
    private AcademicYear academicYear;

    @ManyToOne
    @JoinColumn(name = "standard_id") // current standard
    @JsonManagedReference
    private Standard standard;

    @ManyToOne
    @JoinColumn(name = "division_id")
    @JsonManagedReference
    private Division division;

    private Integer studentType; // 1=> DayScholar, 2=> Residential
    private Integer studentGroup; // 1=> PCM, 2=> PCB
    private LocalDate dateOfAdmission;

    private Boolean isHostel;
    private Boolean isBusConcession;
    private Boolean isVacation;
    private Long busConcessionAmount;
    private Boolean isScholarship;
    private String typeOfStudent; // // 1=> New, 0=> Old
    private String nts;
    private Long concessionAmount;
    private Boolean isRightOff;

    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private Boolean status;

    private String reason;
    private Double paidAmount;
    private Double outstanding;
    private Double refundAmount;
}
