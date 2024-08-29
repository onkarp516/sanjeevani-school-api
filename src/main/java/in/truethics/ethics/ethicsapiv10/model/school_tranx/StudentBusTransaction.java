package in.truethics.ethics.ethicsapiv10.model.school_tranx;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import in.truethics.ethics.ethicsapiv10.model.school_master.AcademicYear;
import in.truethics.ethics.ethicsapiv10.model.school_master.Bus;
import in.truethics.ethics.ethicsapiv10.model.school_master.StudentRegister;
import in.truethics.ethics.ethicsapiv10.model.school_master.StudentTransport;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "student_bus_transaction_tbl")
public class StudentBusTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate paidFrom;
    private LocalDate paidTo;
    private Integer totalMonth;
    private String monthName;
    private Double busFee;
    private Integer paidMonth;
    private Double paidAmount;

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
    @JoinColumn(name = "academic_year_id")
    @JsonManagedReference
    private AcademicYear academicYear;

    @ManyToOne
    @JoinColumn(name = "student_transport_id")
    @JsonManagedReference
    private StudentTransport studentTransport;


}
