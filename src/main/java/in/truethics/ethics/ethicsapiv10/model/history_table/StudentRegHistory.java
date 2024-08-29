package in.truethics.ethics.ethicsapiv10.model.history_table;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="student_reg_history_tbl")
public class StudentRegHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentRegisterId;
    private Long branchId;
    private Long outletId;
    //step1
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private LocalDate birthDate;
    private Long age;
    private String birthPlace;
    private String nationality;
    private String caste;
    private String religion;
    private String motherTongue;
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
    //    step 3
    private String generalRegisterNo;
    private String nameOfPreviousSchool;
    private String stdInPreviousSchool;
    private String result;
    private LocalDate dateOfAdmission;
    private String studentUniqueNo;

    private String typeOfStudent; // // 1=> New, 0=> Old
    private String nts;
    private Long concessionAmount;
    /* LC Parameters */
    private Long lcNo;
    private String generalConduct;
    private String progress;
    private LocalDate dol;
    private String stdInWhichAndWhen;
    private String reasonOfLeaveSchool;
    private String remarks;
    private String studentId;
    private String operationType;

    /* LC Parameters */

    private Long createdBy;
    private Long updatedBy;
    private Boolean status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

  }





