package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.StudentAdmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface StudentAdmissionRepository extends JpaRepository<StudentAdmission, Long> {
    StudentAdmission findByOutletIdAndBranchIdAndAcademicYearIdAndStudentRegisterId(Long id, Long id1, Long id2, Long id3);

    StudentAdmission findTop1ByStudentRegisterId(Long id);

    List<StudentAdmission> findByStudentRegisterId(Long id);

    List<StudentAdmission> findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndDivisionIdAndStudentTypeAndStatus(Long id, Long branchId, Long academicYearId, Long standardId, Long divisionId, Integer studentType, boolean b);

    StudentAdmission findByStudentRegisterIdAndStatus(Long studentId, boolean b);

    StudentAdmission findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndDivisionIdAndStudentRegisterIdAndStudentTypeAndStatus(Long id, Long branchId, Long academicYearId, Long standardId, Long divisionId, Long studentRollNo, Integer studentType, boolean b);


    StudentAdmission findTop1ByStudentRegisterIdOrderByIdDesc(Long id);

    @Query(value = "SELECT IFNULL(academic_year_tbl.year, ''),IFNULL(standard_tbl.standard_name, ''), IFNULL(student_type, '') FROM student_admission_tbl" +
            " LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id LEFT JOIN" +
            " standard_tbl ON student_admission_tbl.standard_id=standard_tbl.id WHERE student_id=?1 ORDER BY" +
            " student_admission_tbl.id DESC LIMIT 1", nativeQuery = true)
    String getLastAcademicData(long parseLong);

    StudentAdmission findTop1ByStudentRegisterIdAndStatusOrderByIdDesc(Long studentId, boolean b);

    StudentAdmission findByIdAndStatus(Long studentAdmissionId, boolean b);


    @Query(value = "SELECT * from student_admission_tbl WHERE academic_year_id=?1 AND type_of_student=?2 AND standard_id=?3 " +
            " AND student_id=?4 AND id !=?5 AND outlet_id=?6 AND branch_id=?7 ", nativeQuery = true)
    List<StudentAdmission> findStudentDuplication(String academicYearId1, String typeofStudent, long currentStdId, long studentRegisterId, long admissionId, Long outletId, Long branchId);
@Query(value="SELECT count(id) FROM student_admission_tbl where status=true",nativeQuery = true)
    Long TotalCountOfAdmission();
@Query(value="SELECT count(id) FROM student_admission_tbl where date_of_admission=?1 AND status=true",nativeQuery = true)
    Long TotalDayAdmission(LocalDate currentDate);

    StudentAdmission findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndStudentRegisterIdAndStudentTypeAndStatus(Long id, Long id1, Long id2, Long id3, Long studentId, Integer studentType, boolean b);

    List<StudentAdmission> findByOutletIdAndBranchIdAndAcademicYearIdAndStatus(Long id, Long id1, Long academicyearId, boolean b);

    StudentAdmission findTop1ByStudentRegisterIdAndIsRightOffAndStatusOrderByIdDesc(Long valueOf, boolean b, boolean b1);

    List<StudentAdmission> findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndStudentTypeAndIsRightOffAndStatus(Long id, Long branchId, Long academicYearId, Long standardId, Integer studentType, boolean b, boolean b1);

    List<StudentAdmission> findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndIsRightOffAndStatus(Long id, Long branchId, Long academicYearId, Long standardId, boolean b, boolean b1);

    List<StudentAdmission> findByOutletIdAndBranchIdAndAcademicYearIdAndStudentTypeAndIsRightOffAndStatus(Long id, Long branchId, Long academicYearId, Integer studentType, boolean b, boolean b1);

    List<StudentAdmission> findByOutletIdAndBranchIdAndStandardIdAndStudentTypeAndIsRightOffAndStatus(Long id, Long branchId, Long standardId, Integer studentType, boolean b, boolean b1);

    List<StudentAdmission> findByOutletIdAndBranchIdAndAcademicYearIdAndIsRightOffAndStatus(Long id, Long branchId, Long academicYearId, boolean b, boolean b1);

    List<StudentAdmission> findByOutletIdAndBranchIdAndStandardIdAndIsRightOffAndStatus(Long id, Long branchId, Long standardId, boolean b, boolean b1);

    List<StudentAdmission> findByOutletIdAndBranchIdAndStudentTypeAndIsRightOffAndStatus(Long id, Long branchId, Integer studentType, boolean b, boolean b1);

    List<StudentAdmission> findByOutletIdAndBranchIdAndIsRightOffAndStatus(Long id, Long id1, boolean b, boolean b1);

    StudentAdmission findByStudentRegisterIdAndAcademicYearIdAndStatus(Long id, Long id1, boolean b);


//    StudentAdmission findByStudentIdAndStatus(Long studentInfoId, boolean b);
}
