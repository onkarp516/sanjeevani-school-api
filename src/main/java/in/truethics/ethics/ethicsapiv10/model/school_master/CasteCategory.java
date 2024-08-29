package in.truethics.ethics.ethicsapiv10.model.school_master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "caste_category_tbl")
public class CasteCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "religion_id")
    @JsonManagedReference
    private Religion religion;

    @ManyToOne
    @JoinColumn(name = "caste_id")
    @JsonManagedReference
    private Caste caste;

    @ManyToOne
    @JoinColumn(name = "sub_caste_id")
    @JsonManagedReference
    private SubCaste subCaste;

    private String categoryName;
    private Long createdBy;
    private Long updatedBy;
    private Boolean status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<StudentRegister> studentRegisters;
}
