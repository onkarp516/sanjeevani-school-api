package in.truethics.ethics.ethicsapiv10.model.history_table;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="student_transport_history_tbl")
public class StudentTransportHistory
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentTransportId;
    private Integer studentType;
    private Long studentBusId;
    private String academicYearId;
    private Long studentRegisterId;
    private Long standardId;
    private Long branchId;
    private Long outletId;
    private Long createdBy;
    private Long updatedBy;
    private Boolean status;
    private String operationType;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
