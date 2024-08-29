package in.truethics.ethics.ethicsapiv10.model.history_table;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="student_adm_history_tbl")
public class StudentAdmHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentAdmissionId;
    private Long branchId;
    private Long OutletId;
    private Long new_admitted_standard;
    private Long studentId;
    private String academicYear;
    private String division;
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
    private String operationType;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private Boolean status;

    private String reason;
    private Double paidAmount;
    private Double outstanding;
    private Double refundAmount;




}
