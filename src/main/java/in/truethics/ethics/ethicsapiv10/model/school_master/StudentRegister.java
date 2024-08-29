package in.truethics.ethics.ethicsapiv10.model.school_master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionSummary;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "student_register_tbl")
public class StudentRegister {
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

    //    step 1
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private LocalDate birthDate;
    private Long age;
    private String birthPlace;
    private String nationality;

    @ManyToOne
    @JoinColumn(name = "mother_tongue_id")
    @JsonManagedReference
    private MotherTongue motherTongue;

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

    @ManyToOne
    @JoinColumn(name = "caste_category_id")
    @JsonManagedReference
    private CasteCategory casteCategory;

    private String homeTown;
    private String aadharNo;
    private String saralId;
    private String studentImage;

    //     step 2
    private String fatherName;
    private String fatherOccupation;
    private String motherName;
    private String motherOccupation;
    private String officeAddress;
    private String currentAddress;
    private Boolean sameAsCurrentAddress;
    private String permanentAddress;
    private Long phoneNoHome;
    private Long mobileNo;
    private Long altMobileNo;
    private String emailId;
    private String fatherImage;
    private String motherImage;

    //    step 3
    private String generalRegisterNo;
    private String nameOfPreviousSchool;
    private String stdInPreviousSchool;
    private String result;
    private LocalDate dateOfAdmission;


    @Column(unique = true)
    private String studentUniqueNo;

    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    @JsonManagedReference
    private AcademicYear academicYear;

    @ManyToOne
    @JoinColumn(name = "admitted_standard_id")
    @JsonManagedReference
    private Standard admittedStandard;

    @ManyToOne
    @JoinColumn(name = "division_id")
    @JsonManagedReference
    private Division division;

    @ManyToOne
    @JoinColumn(name = "current_standard_id")
    @JsonManagedReference
    private Standard currentStandard;

    private Integer studentType; // 1=> DayScholar, 2=> Residential
    private Integer studentGroup; // 1=> PCM, 2=> PCB
    private Boolean isHostel;
    private Boolean isBusConcession;
    private Long busConcessionAmount;
    private Boolean isScholarship;
    private String typeOfStudent; // // 1=> New, 0=> Old
    private String nts;
    private Long concessionAmount;
    private Double maxFee;
    private Double discount;

    /* LC Parameters */
    private Long lcNo;
    private String generalConduct;
    private String progress;
    private LocalDate dol;
    private String stdInWhichAndWhen;
    private String reasonOfLeaveSchool;
    private String remarks;
    private String studentId;
    /* LC Parameters */

    private Long createdBy;
    private Long updatedBy;
    private Boolean status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<FeesTransactionSummary> feesTransactionSummaries;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<StudentAdmission> studentAdmissions;
}
