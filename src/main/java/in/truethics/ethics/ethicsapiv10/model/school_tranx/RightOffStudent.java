package in.truethics.ethics.ethicsapiv10.model.school_tranx;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "right_off_tbl")
public class RightOffStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private String studentName;
    private Double rightOffAmt;
    private Long academicYearId;
    private String academicYear;
    private String FiscalYear;
    private String standardName;
    private Long standardId;
    private String rightOffNote;
    private Long createdBy;
    private Long updatedBy;
    private Boolean status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long feesTransactionSummaryId;
    private Long feesHeadId;
    private Long subFeesHeadId;


    private Long branchId;
    private Long OutletId;

}
