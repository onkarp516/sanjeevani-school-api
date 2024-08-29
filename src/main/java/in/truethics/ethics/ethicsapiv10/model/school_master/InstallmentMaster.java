package in.truethics.ethics.ethicsapiv10.model.school_master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "installment_master_tbl")
public class InstallmentMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    @JsonManagedReference
    private Outlet outlet;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonManagedReference
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "fees_master_id")
    @JsonManagedReference
    private FeesMaster feesMaster;

    private Integer studentType; // 1=> DayScholar, 2=> Residential
    private Integer concessionType; // 1=> No Concession, 2=> 3000 Concession, 3=> 1000 Concession 4=> 2000 concession
    private Integer studentGroup; // 1=> PCM, 2=> PCB
    private Integer installmentNo;
    private LocalDate expiryDate;
    private Double feesAmount; // sum of amount form installment details
    private Double boysAmount; // sum of boys amount form installment details
    private Double girlsAmount; // sum of girls amount form installment details

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<InstallmentDetails> installmentDetails;

    private Long createdBy;
    private Long updatedBy;
    private Boolean status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
