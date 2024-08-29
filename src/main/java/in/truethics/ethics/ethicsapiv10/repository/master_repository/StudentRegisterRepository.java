package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.StudentRegister;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface StudentRegisterRepository extends JpaRepository<StudentRegister, Long> {
    List<StudentRegister> findByOutletIdAndBranchIdAndAcademicYearIdAndAdmittedStandardIdAndDivisionIdAndStudentTypeAndStatus(Long id, Long branchId, Long academicYearId, Long standardId, Long divisionId, Integer studentType, boolean b);

    List<StudentRegister> findByOutletIdAndStatus(Long outletId, boolean b);

    StudentRegister findByIdAndStatus(long studentId, boolean b);

    List<StudentRegister> findByOutletIdAndBranchIdAndAcademicYearIdAndCurrentStandardIdAndDivisionIdAndStudentTypeAndStatus(Long id, Long branchId, Long academicYearId, Long standardId, Long divisionId, Integer studentType, boolean b);

    StudentRegister findByOutletIdAndBranchIdAndAcademicYearIdAndCurrentStandardIdAndDivisionIdAndStudentTypeAndIdAndStatus(Long id, Long branchId, Long academicYearId, Long standardId, Long divisionId, Integer studentType, Long studentRollNo, boolean b);

    @Query(value = "SELECT id from student_register_tbl where NOT EXISTS(SELECT student_id FROM student_admission_tbl" +
            " WHERE student_register_tbl.id=student_admission_tbl.student_id) ORDER BY id ASC", nativeQuery = true)
    List<Object[]> getStudentIdsFromStudentRegister();

    List<StudentRegister> findByFirstNameAndLastNameAndStatus(String firstName, String lastName, boolean b);

    List<StudentRegister> findByFirstNameAndMiddleNameAndLastNameAndStatus(String firstName, String middleName, String lastName, boolean b);

    List<StudentRegister> findByOutletIdAndBranchIdAndStatus(Long outletId, Long branchId, boolean b);


    @Query(value = "SELECT student_register_tbl.id FROM student_register_tbl LEFT JOIN student_admission_tbl ON" +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON" +
            " student_admission_tbl.standard_id=standard_tbl.id WHERE student_register_tbl.outlet_id=?1 AND" +
            " student_register_tbl.branch_id=?2 AND student_register_tbl.status=?3 ORDER BY standard_tbl.standard_name ASC", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndStatus(Long outletId, Long id, boolean b);


    @Query(value = "SELECT student_register_tbl.id FROM student_register_tbl LEFT JOIN student_admission_tbl ON" +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON" +
            " student_admission_tbl.standard_id=standard_tbl.id WHERE student_register_tbl.outlet_id=?1 AND" +
            " student_register_tbl.status=?3 ORDER BY standard_tbl.standard_name ASC", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndStatus(Long outletId, boolean b);


    @Query(value = "SELECT student_register_tbl.id ,IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL( student_admission_tbl.date_of_admission, '') , student_admission_tbl.student_type, standard_tbl.standard_name,academic_year_tbl.year,student_register_tbl.type_of_student " +
            "     ,IFNULL(father_name, ''),IFNULL(mother_name,''),IFNULL(permanent_address,'')," +
            " IFNULL(caste_tbl.caste_name,''),IFNULL(sub_caste_tbl.sub_caste_name,''),IFNULL(caste_category_tbl.category_name,'') " +
            " ,IFNULL(aadhar_no,'') ,IFNULL(student_register_tbl.student_id,'') ,IFNULL(student_transport_tbl.id,''),  student_admission_tbl.id AS admission_id ,IFNULL( student_register_tbl.date_of_admission, '')" +
            " FROM student_register_tbl LEFT JOIN student_admission_tbl ON " +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON " +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            " LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT" +
            " JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id LEFT JOIN caste_category_tbl ON" +
            " student_register_tbl.caste_category_id=caste_category_tbl.id  LEFT JOIN student_transport_tbl ON " +
            " student_register_tbl.id=student_transport_tbl.student_id WHERE student_register_tbl.outlet_id=?1 AND " +
            " student_register_tbl.branch_id=?2 AND student_admission_tbl.academic_year_id=?3" +
            " AND student_admission_tbl.standard_id=?4 AND student_admission_tbl.student_type=?5 AND student_transport_tbl.bus_id=?6" +
            " AND student_register_tbl.status=?7 ORDER BY TRIM(student_register_tbl.last_name)", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdANDStudentTypeAndBusStopIdAndStatus(Long id, Long branchId, Long academicYearId, Long standardId, Integer studentType, Long busStopId, boolean b);


    @Query(value = "SELECT student_register_tbl.id ,IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL( student_admission_tbl.date_of_admission, '') , student_admission_tbl.student_type, standard_tbl.standard_name,academic_year_tbl.year,student_register_tbl.type_of_student" +
            " ,IFNULL(father_name, ''),IFNULL(mother_name,''),IFNULL(permanent_address,'')," +
            " IFNULL(caste_tbl.caste_name,''),IFNULL(sub_caste_tbl.sub_caste_name,''),IFNULL(caste_category_tbl.category_name,'')" +
            " ,IFNULL(aadhar_no,'') ,IFNULL(student_register_tbl.student_id,''), student_admission_tbl.id AS admission_id ,IFNULL( student_register_tbl.date_of_admission, '') FROM student_register_tbl LEFT JOIN student_admission_tbl ON" +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON" +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            " LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT " +
            " JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id LEFT JOIN caste_category_tbl ON " +
            " student_register_tbl.caste_category_id=caste_category_tbl.id  " +
            " WHERE student_register_tbl.outlet_id=?1 AND" +
            " student_register_tbl.branch_id=?2 AND student_admission_tbl.academic_year_id=?3" +
            "  AND student_admission_tbl.standard_id=?4 AND student_admission_tbl.student_type=?5 " +
            " AND student_register_tbl.status=?6 ORDER BY TRIM(student_register_tbl.last_name) ", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdANDStudentTypeAndStatus(Long id, Long branchId, Long academicYearId, Long standardId, Integer studentType, boolean b);

    @Query(value = "SELECT student_register_tbl.id ,IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL( student_admission_tbl.date_of_admission, '') , student_admission_tbl.student_type, standard_tbl.standard_name,academic_year_tbl.year,student_register_tbl.type_of_student," +
            " IFNULL(father_name, '') ,IFNULL(mother_name,''),IFNULL(permanent_address,'')," +
            " IFNULL(caste_tbl.caste_name,''),IFNULL(sub_caste_tbl.sub_caste_name,''),IFNULL(caste_category_tbl.category_name,'')" +
            " ,IFNULL(aadhar_no,'') ,IFNULL(student_register_tbl.student_id,'') , student_admission_tbl.id AS admission_id ,IFNULL( student_register_tbl.date_of_admission, '') FROM student_register_tbl LEFT JOIN student_admission_tbl ON" +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON" +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            " LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT  " +
            " JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id LEFT JOIN caste_category_tbl ON " +
            " student_register_tbl.caste_category_id=caste_category_tbl.id " +
            " WHERE student_register_tbl.outlet_id=?1 AND" +
            " student_register_tbl.branch_id=?2 AND student_admission_tbl.academic_year_id=?3 AND student_admission_tbl.standard_id=?4 " +
            " AND student_register_tbl.status=?5  ORDER BY TRIM(student_register_tbl.last_name) ", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndAcademicYearIdStandardIdANDStatus(Long id, Long branchId, Long academicYearId, Long standardId, boolean b);

    @Query(value = "SELECT student_register_tbl.id,IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL( student_admission_tbl.date_of_admission, '') , student_admission_tbl.student_type, standard_tbl.standard_name,academic_year_tbl.year,student_register_tbl.type_of_student " +
            " ,IFNULL(father_name, '') ,IFNULL(mother_name,''),IFNULL(permanent_address,'')," +
            " IFNULL(caste_tbl.caste_name,''),IFNULL(sub_caste_tbl.sub_caste_name,''),IFNULL(caste_category_tbl.category_name,'') " +
            " ,IFNULL(aadhar_no,'') ,IFNULL(student_register_tbl.student_id,'') , student_admission_tbl.id AS admission_id,IFNULL( student_register_tbl.date_of_admission, '') FROM student_register_tbl LEFT JOIN student_admission_tbl ON" +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON" +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            "  LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT " +
            " JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id LEFT JOIN caste_category_tbl ON " +
            " student_register_tbl.caste_category_id=caste_category_tbl.id " +
            " WHERE student_register_tbl.outlet_id=?1 AND" +
            " student_register_tbl.branch_id=?2 AND student_admission_tbl.academic_year_id=?3 AND student_admission_tbl.student_type=?4 " +
            " AND student_register_tbl.status=?5 ORDER BY TRIM(student_register_tbl.last_name)", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndAcademicYearIdStudentTypeANDStatus(Long id, Long branchId, Long academicYearId, Integer studentType, boolean b);


    @Query(value = "SELECT student_register_tbl.id, IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL( student_admission_tbl.date_of_admission, '') ,student_admission_tbl.student_type, standard_tbl.standard_name,academic_year_tbl.year ,student_register_tbl.type_of_student " +
            " ,IFNULL(father_name, '')  ,IFNULL(mother_name,''),IFNULL(permanent_address,'')," +
            "   IFNULL(caste_tbl.caste_name,''),IFNULL(sub_caste_tbl.sub_caste_name,''),IFNULL(caste_category_tbl.category_name,'')" +
            " ,IFNULL(aadhar_no,'') ,IFNULL(student_register_tbl.student_id,'') , student_admission_tbl.id AS admission_id ,IFNULL( student_register_tbl.date_of_admission, '') FROM student_register_tbl LEFT JOIN student_admission_tbl ON" +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON" +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            " LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT " +
            "  JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id LEFT JOIN caste_category_tbl ON " +
            "  student_register_tbl.caste_category_id=caste_category_tbl.id " +
            " WHERE student_register_tbl.outlet_id=?1 AND" +
            " student_register_tbl.branch_id=?2 AND student_admission_tbl.student_type=?3 " +
            " AND student_register_tbl.status=?4  ORDER BY TRIM(student_register_tbl.last_name)", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndStudentTypeANDStatus(Long id, Long branchId, Integer studentType, boolean b);

    @Query(value = "SELECT student_register_tbl.id, IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL( student_admission_tbl.date_of_admission, '') ,student_admission_tbl.student_type, standard_tbl.standard_name,academic_year_tbl.year ,student_register_tbl.type_of_student " +
            " , IFNULL(father_name, '') ,IFNULL(mother_name,''),IFNULL(permanent_address,''),IFNULL(caste_tbl.caste_name,''),IFNULL(sub_caste_tbl.sub_caste_name,''),IFNULL(caste_category_tbl.category_name,'')," +
            " IFNULL(aadhar_no,'') ,IFNULL(student_register_tbl.student_id,''), student_admission_tbl.id AS admission_id ,IFNULL( student_register_tbl.date_of_admission, '') FROM student_register_tbl LEFT JOIN student_admission_tbl ON" +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON" +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            "  LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT " +
            "   JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id LEFT JOIN caste_category_tbl ON " +
            "     student_register_tbl.caste_category_id=caste_category_tbl.id" +
            " WHERE student_register_tbl.outlet_id=?1 AND" +
            " student_register_tbl.branch_id=?2 AND student_admission_tbl.standard_id=?3 AND student_admission_tbl.student_type=?4 " +
            " AND student_register_tbl.status=?5 ORDER BY TRIM(student_register_tbl.last_name) ", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndStandardIdAndStudentTypeANDStatus(Long id, Long branchId, Long standardId, Integer studentType, boolean b);

    @Query(value = "SELECT student_register_tbl.id, IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL( student_admission_tbl.date_of_admission, '') , IFNULL(student_admission_tbl.student_type,''), standard_tbl.standard_name,academic_year_tbl.year ,student_register_tbl.type_of_student" +
            " , IFNULL(father_name, '') ,IFNULL(mother_name,''),IFNULL(permanent_address,'')," +
            "  IFNULL(caste_tbl.caste_name,''),IFNULL(sub_caste_tbl.sub_caste_name,''),IFNULL(caste_category_tbl.category_name,'')" +
            " ,IFNULL(aadhar_no,'') ,IFNULL(student_register_tbl.student_id,''), student_admission_tbl.id AS admission_id,IFNULL( student_register_tbl.date_of_admission, '') FROM student_register_tbl LEFT JOIN student_admission_tbl ON" +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON" +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            " LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT " +
            "  JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id LEFT JOIN caste_category_tbl ON " +
            "  student_register_tbl.caste_category_id=caste_category_tbl.id " +
            " WHERE student_register_tbl.outlet_id=?1 AND" +
            " student_register_tbl.branch_id=?2 AND student_admission_tbl.academic_year_id=?3" +
            " AND student_register_tbl.status=?4 ORDER BY TRIM(student_register_tbl.last_name) ", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndAcademicYearIdANDStatus(Long id, Long branchId, Long academicYearId, boolean b);


    @Query(value = "SELECT student_register_tbl.id, IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, ''), " +
            " IFNULL( student_admission_tbl.date_of_admission, '') , IFNULL(student_admission_tbl.student_type,''), standard_tbl.standard_name,academic_year_tbl.year ,student_register_tbl.type_of_student" +
            " ,IFNULL(father_name, '')  ,IFNULL(mother_name,''),IFNULL(permanent_address,'')," +
            "   IFNULL(caste_tbl.caste_name,''),IFNULL(sub_caste_tbl.sub_caste_name,''),IFNULL(caste_category_tbl.category_name,'')" +
            ",IFNULL(aadhar_no,'') ,IFNULL(student_register_tbl.student_id,''),  student_admission_tbl.id AS admission_id ,IFNULL(student_register_tbl.date_of_admission, '')  FROM " +
            " student_register_tbl LEFT JOIN student_admission_tbl ON " +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON " +
            " student_admission_tbl.standard_id=standard_tbl.id  LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id " +
            " LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT " +
            "  JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id LEFT JOIN caste_category_tbl ON " +
            "  student_register_tbl.caste_category_id=caste_category_tbl.id " +
            " WHERE student_register_tbl.outlet_id=?1 AND " +
            " student_register_tbl.branch_id=?2 AND student_admission_tbl.standard_id=?3 AND student_register_tbl.status=?4 " +
            " ORDER BY TRIM(student_register_tbl.last_name)", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndStandardIdANDStatus(Long id, Long branchId, Long standardId, boolean b);

    @Query(value = "SELECT student_register_tbl.id, IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, '')," +
            " IFNULL(mobile_no, ''), IFNULL(birth_date, ''),  IFNULL(student_admission_tbl.date_of_admission, ''), IFNULL(father_name, '') ," +
            " IFNULL(mother_name,''),IFNULL(permanent_address,''), IFNULL(student_register_tbl.type_of_student,''), IFNULL(caste_tbl.caste_name,'')," +
            " IFNULL(sub_caste_tbl.sub_caste_name, ''), IFNULL(caste_category_tbl.category_name, ''),IFNULL(aadhar_no,'') ,IFNULL(student_unique_no,''),  " +
            " student_admission_tbl.id AS admission_id ,IFNULL( student_register_tbl.date_of_admission, '') FROM student_register_tbl LEFT JOIN student_admission_tbl ON student_register_tbl.id=student_admission_tbl.student_id " +
            " LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id " +
            " LEFT JOIN caste_category_tbl ON student_register_tbl.caste_category_id=caste_category_tbl.id " +
            "  WHERE student_admission_tbl.outlet_id=?1 AND student_admission_tbl.branch_id=?2 AND student_register_tbl.status=?3 ORDER BY TRIM(student_register_tbl.last_name)", nativeQuery = true)
    List<Object[]> getDataByStatus(Long id, Long aLong, boolean b);


    @Query(value = "SELECT student_register_tbl.id ,first_name, IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL(date_of_admission, '') , student_admission_tbl.student_type, standard_tbl.standard_name,academic_year_tbl.year" +
            " FROM student_register_tbl LEFT JOIN student_admission_tbl ON" +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON" +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN division_tbl ON student_admission_tbl.division_id=division_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            " WHERE student_register_tbl.outlet_id=?1 AND" +
            " student_register_tbl.branch_id=?2 AND student_admission_tbl.academic_year_id=?3 AND student_admission_tbl.standard_id=?4" +
            " AND student_admission_tbl.division_id=?5 AND  student_admission_tbl.student_type=?6 AND student_register_tbl.status=?7 " +
            " ORDER BY TRIM(student_register_tbl.last_name)", nativeQuery = true)
    List<Object[]> getStudentListByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndDivisionIdAndStudentTypeAndStatus(Long id, Long id1, Long academicYearId, Long standardId, Long divisionId, Integer studentType, boolean b);

    @Query(value = "SELECT student_register_tbl.id ,IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL(student_admission_tbl.date_of_admission, '') , student_admission_tbl.student_type, standard_tbl.standard_name,academic_year_tbl.year" +
            " FROM student_register_tbl LEFT JOIN student_admission_tbl ON" +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON" +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            " WHERE student_register_tbl.outlet_id=?1 AND" +
            " student_register_tbl.branch_id=?2 AND student_admission_tbl.academic_year_id=?3  " +
            " AND student_admission_tbl.student_type=?4 AND student_register_tbl.status=?5 AND " +
            " student_register_tbl.id NOT IN (SELECT IFNULL(student_id,0) FROM student_transport_tbl) ", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndAcademicYearIdANDStudentTypeAndStatusAndNsList(Long id, Long branchId, Long academicYearId, Integer studentType, boolean b);

    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `student_admission_tbl` WHERE student_id=?1", nativeQuery = true)
    void deleteStudentFromAdmission(Long id);


    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `ledger_balance_summary_tbl` WHERE ledger_master_id=?1", nativeQuery = true)
    void deleteLedgerBalanceDataByLedgerId(Long id);


    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `ledger_transaction_details_tbl` WHERE ledger_master_id=?1", nativeQuery = true)
    void deleteLedgerTransDataByLedgerId(Long id);


    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `ledger_master_tbl` WHERE student_id=?1", nativeQuery = true)
    void deleteLedgerDataByStudentId(Long id);


    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `tranx_sales_invoice_tbl` WHERE sundry_debtors_id=?1", nativeQuery = true)
    void deleteSalesTransDataByLedgerId(Long id);


    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `tranx_sales_invoice_details_tbl` WHERE sales_invoice_id=?1", nativeQuery = true)
    void deleteSalesTranxDetailsBySalesInvoiceId(Long id);


    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `fees_transaction_summary_tbl` WHERE student_id=?1", nativeQuery = true)
    void deleteStudentFeesSummaryByStudentId(Long id);

    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `fees_transaction_detail_tbl` WHERE fees_transaction_summary_id=?1", nativeQuery = true)
    void deleteStudentFeesDetailsByFeesSummaryId(Long id);


    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `student_register_tbl` WHERE id=?1", nativeQuery = true)
    void deleteStudentDataById(Long id);

    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `student_transport_tbl` WHERE student_id=?1", nativeQuery = true)
    void deleteStudentTransportByStdId(Long id);


    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `tranx_receipt_perticulars_tbl` WHERE ledger_id=?1", nativeQuery = true)
    void deleteReceiptPartDataByLedgerId(Long id);


    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `student_transport_tbl` WHERE id=?1", nativeQuery = true)
    void deleteStudentBusId(Long studentTransportId);

    @Query(value = "SELECT bus_tbl.bus_stop_name, bus_tbl.bus_fee FROM `student_transport_tbl` LEFT JOIN bus_tbl ON" +
            " student_transport_tbl.bus_id=bus_tbl.id WHERE student_id=?1", nativeQuery = true)
    String getStudentBusData(String toString);


    @Query(value = "SELECT student_register_tbl.id ,IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL( student_admission_tbl.date_of_admission, '') , student_admission_tbl.student_type, standard_tbl.standard_name,academic_year_tbl.year,student_register_tbl.type_of_student " +
            "     ,IFNULL(father_name, ''),IFNULL(mother_name,''),IFNULL(permanent_address,'')," +
            " IFNULL(caste_tbl.caste_name,''),IFNULL(sub_caste_tbl.sub_caste_name,''),IFNULL(caste_category_tbl.category_name,'') " +
            " ,IFNULL(aadhar_no,'') ,IFNULL(student_register_tbl.student_id,'') ,IFNULL(student_transport_tbl.id,''),  student_admission_tbl.id AS admission_id ,IFNULL( student_register_tbl.date_of_admission, '')" +
            " FROM student_register_tbl LEFT JOIN student_admission_tbl ON " +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON " +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            " LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT" +
            " JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id LEFT JOIN caste_category_tbl ON" +
            " student_register_tbl.caste_category_id=caste_category_tbl.id  LEFT JOIN student_transport_tbl ON " +
            " student_register_tbl.id=student_transport_tbl.student_id WHERE student_register_tbl.outlet_id=?1 AND " +
            " student_register_tbl.branch_id=?2 " +
            "  AND student_transport_tbl.bus_id=?3 AND student_register_tbl.status=?4 ORDER BY TRIM(student_register_tbl.last_name)", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndBusStopIdANDStatus(Long id, Long branchId, Long busStopId, boolean b);


    @Query(value = "SELECT student_register_tbl.id ,IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL( student_admission_tbl.date_of_admission, '') , student_admission_tbl.student_type, standard_tbl.standard_name,academic_year_tbl.year,student_register_tbl.type_of_student " +
            "     ,IFNULL(father_name, ''),IFNULL(mother_name,''),IFNULL(permanent_address,'')," +
            " IFNULL(caste_tbl.caste_name,''),IFNULL(sub_caste_tbl.sub_caste_name,''),IFNULL(caste_category_tbl.category_name,'') " +
            " ,IFNULL(aadhar_no,'') ,IFNULL(student_register_tbl.student_id,'') ,IFNULL(student_transport_tbl.id,'') , student_admission_tbl.id AS admission_id,IFNULL( student_register_tbl.date_of_admission, '')" +
            " FROM student_register_tbl LEFT JOIN student_admission_tbl ON " +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON " +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            " LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT" +
            " JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id LEFT JOIN caste_category_tbl ON" +
            " student_register_tbl.caste_category_id=caste_category_tbl.id  LEFT JOIN student_transport_tbl ON " +
            " student_register_tbl.id=student_transport_tbl.student_id WHERE student_register_tbl.outlet_id=?1 AND " +
            " student_register_tbl.branch_id=?2 AND student_admission_tbl.academic_year_id=?3 " +
            "  AND student_transport_tbl.bus_id=?4 AND student_register_tbl.status=?5 ORDER BY TRIM(student_register_tbl.last_name)", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndAcademicYearIdBusStopIdANDStatus(Long id, Long branchId, Long academicYearId, Long busStopId, boolean b);


    @Query(value = "SELECT student_register_tbl.id ,IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL( student_admission_tbl.date_of_admission, '') , student_admission_tbl.student_type, standard_tbl.standard_name,academic_year_tbl.year,student_register_tbl.type_of_student " +
            "     ,IFNULL(father_name, ''),IFNULL(mother_name,''),IFNULL(permanent_address,'')," +
            " IFNULL(caste_tbl.caste_name,''),IFNULL(sub_caste_tbl.sub_caste_name,''),IFNULL(caste_category_tbl.category_name,'') " +
            " ,IFNULL(aadhar_no,'') ,IFNULL(student_register_tbl.student_id,'') ,IFNULL(student_transport_tbl.id,''),  student_admission_tbl.id AS admission_id ,IFNULL( student_register_tbl.date_of_admission, '')" +
            " FROM student_register_tbl LEFT JOIN student_admission_tbl ON " +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON " +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            " LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT" +
            " JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id LEFT JOIN caste_category_tbl ON" +
            " student_register_tbl.caste_category_id=caste_category_tbl.id  LEFT JOIN student_transport_tbl ON " +
            " student_register_tbl.id=student_transport_tbl.student_id WHERE student_register_tbl.outlet_id=?1 AND " +
            " student_register_tbl.branch_id=?2 AND student_admission_tbl.academic_year_id=?3" +
            " AND student_admission_tbl.standard_id=?4 AND student_transport_tbl.bus_id=?5" +
            " AND student_register_tbl.status=?6   ORDER BY TRIM(student_register_tbl.last_name)", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdANDBusStopIdAndStatus(Long id, Long branchId, Long academicYearId, Long standardId, Long busStopId, boolean b);


    @Query(value = "SELECT student_register_tbl.id ,IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL( student_admission_tbl.date_of_admission, '') , student_admission_tbl.student_type, standard_tbl.standard_name,academic_year_tbl.year,student_register_tbl.type_of_student " +
            "     ,IFNULL(father_name, ''),IFNULL(mother_name,''),IFNULL(permanent_address,'')," +
            " IFNULL(caste_tbl.caste_name,''),IFNULL(sub_caste_tbl.sub_caste_name,''),IFNULL(caste_category_tbl.category_name,'') " +
            " ,IFNULL(aadhar_no,'') ,IFNULL(student_register_tbl.student_id,'') ,IFNULL(student_transport_tbl.id,''), student_admission_tbl.id AS admission_id,IFNULL( student_register_tbl.date_of_admission, '')" +
            " FROM student_register_tbl LEFT JOIN student_admission_tbl ON " +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON " +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            " LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT" +
            " JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id LEFT JOIN caste_category_tbl ON" +
            " student_register_tbl.caste_category_id=caste_category_tbl.id  LEFT JOIN student_transport_tbl ON " +
            " student_register_tbl.id=student_transport_tbl.student_id WHERE student_register_tbl.outlet_id=?1 AND " +
            " student_register_tbl.branch_id=?2 AND student_admission_tbl.academic_year_id=?3" +
            "  AND student_admission_tbl.student_type=?4" +
            " AND student_transport_tbl.bus_id=?5  AND student_register_tbl.status=?6 ORDER BY TRIM(student_register_tbl.last_name)", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndAcademicYearIdStudentTypeANDBusStopdIdANdStatus(Long id, Long branchId, Long academicYearId, Integer studentType, Long busStopId, boolean b);


    @Query(value = "SELECT student_register_tbl.id ,IFNULL(first_name, ''), IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), IFNULL(birth_date, '')," +
            " IFNULL( student_admission_tbl.date_of_admission, '') , student_admission_tbl.student_type, standard_tbl.standard_name,academic_year_tbl.year,student_register_tbl.type_of_student " +
            "     ,IFNULL(father_name, ''),IFNULL(mother_name,''),IFNULL(permanent_address,'')," +
            " IFNULL(caste_tbl.caste_name,''),IFNULL(sub_caste_tbl.sub_caste_name,''),IFNULL(caste_category_tbl.category_name,'') " +
            " ,IFNULL(aadhar_no,'') ,IFNULL(student_register_tbl.student_id,'') ,IFNULL(student_transport_tbl.id,''), student_admission_tbl.id AS admission_id, IFNULL( student_register_tbl.date_of_admission, '') " +
            " FROM student_register_tbl LEFT JOIN student_admission_tbl ON " +
            " student_register_tbl.id=student_admission_tbl.student_id LEFT JOIN standard_tbl ON " +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN academic_year_tbl ON student_admission_tbl.academic_year_id=academic_year_tbl.id" +
            " LEFT JOIN caste_tbl ON student_register_tbl.caste_id=caste_tbl.id LEFT" +
            " JOIN sub_caste_tbl ON student_register_tbl.sub_caste_id=sub_caste_tbl.id LEFT JOIN caste_category_tbl ON" +
            " student_register_tbl.caste_category_id=caste_category_tbl.id  LEFT JOIN student_transport_tbl ON " +
            " student_register_tbl.id=student_transport_tbl.student_id WHERE student_register_tbl.outlet_id=?1 AND " +
            " student_register_tbl.branch_id=?2 " +
            " AND student_admission_tbl.standard_id=?3 " +
            " AND student_transport_tbl.bus_id=?4 AND student_register_tbl.status=?5  ORDER BY TRIM(student_register_tbl.last_name)", nativeQuery = true)
    List<Object[]> getStudentsByOutletIdAndBranchIdAndStandardIdAndBusStopIdANDStatus(Long id, Long branchId, Long standardId, Long busStopId, boolean b);

    StudentRegister findTop1ByOrderByIdDesc();


    @Query(value="SELECT count(id) FROM student_register_tbl where status=true",nativeQuery = true)
    Long TotalCountOfRegisteredStudent();

    @Query(value="SELECT * FROM `student_register_tbl` WHERE outlet_id=?1 and branch_id=?2 and status=true",nativeQuery = true)
    List<StudentRegister> findStudentsbyStatus(Long id, Long id1, boolean b);
    @Query(
            value="SELECT student_register_tbl.id ,first_name, IFNULL(last_name, ''), IFNULL(gender, ''), IFNULL(mobile_no, ''), " +
                    " IFNULL(birth_date, ''),IFNULL(student_admission_tbl.date_of_admission, '') , student_admission_tbl.student_type," +
                    " standard_tbl.standard_name,academic_year_tbl.year, student_admission_tbl.division_id FROM " +
                    " student_register_tbl LEFT JOIN student_admission_tbl ON student_register_tbl.id=student_admission_tbl.student_id" +
                    " LEFT JOIN standard_tbl ON student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN " +
                    " fees_transaction_summary_tbl ON student_admission_tbl.student_id=fees_transaction_summary_tbl.student_id " +
                    " LEFT JOIN division_tbl ON student_admission_tbl.division_id=division_tbl.id LEFT JOIN academic_year_tbl ON " +
                    " student_admission_tbl.academic_year_id=academic_year_tbl.id WHERE student_register_tbl.outlet_id=?1 AND" +
                    " student_register_tbl.branch_id=?2 AND student_admission_tbl.academic_year_id=?3 AND student_admission_tbl.standard_id=?4 AND" +
                    "  fees_transaction_summary_tbl.balance>0 AND student_admission_tbl.division_id=?5 AND  student_admission_tbl.student_type=?6 AND " +
                    " student_register_tbl.status=?7  ORDER BY TRIM(student_register_tbl.last_name)"
            ,nativeQuery = true)
    List<Object[]> getStudentListhavingOutstanding(Long id, Long id1, Long academicYearId, Long standardId, Long divisionId, Integer studentType, boolean b);
}
