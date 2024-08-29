package in.truethics.ethics.ethicsapiv10.model.school_master;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "student_transport_tbl")
public class StudentTransport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer studentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalMonth;
    private Double busFee;
    private Double outstanding;
    private Integer paidMonth;
    private Double paidAmount;
    private Long createdBy;
    private Long updatedBy;
    private Boolean status;
    private Boolean isBusFeePending;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "bus_id")
    @JsonManagedReference
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    @JsonManagedReference
    private AcademicYear academicYear;

    @ManyToOne
    @JoinColumn(name = "standard_id")
    @JsonManagedReference
    private Standard standard;


    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonManagedReference
    private StudentRegister studentRegister;


    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonManagedReference
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    @JsonManagedReference
    private Outlet outlet;

    @ManyToOne
    @JoinColumn(name = "division_id")
    @JsonManagedReference
    private Division division;


}