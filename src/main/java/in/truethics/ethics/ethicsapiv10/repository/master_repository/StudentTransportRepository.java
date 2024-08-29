package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.StudentTransport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentTransportRepository extends JpaRepository<StudentTransport, Long> {
    List<StudentTransport> findByOutletIdAndBranchIdAndStatus(Long id, Long id1, boolean b);

    StudentTransport findByIdAndStatus(Long studentTransportId, boolean b);

    @Query(value = "SELECT student_transport_tbl.*, academic_year_tbl.year, standard_tbl.standard_name," +
            " bus_tbl.bus_stop_name FROM `student_transport_tbl` LEFT JOIN student_admission_tbl ON" +
            " student_transport_tbl.student_id=student_admission_tbl.student_id LEFT JOIN academic_year_tbl ON" +
            " student_transport_tbl.academic_year_id=academic_year_tbl.id LEFT JOIN standard_tbl ON" +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN bus_tbl ON" +
            " student_transport_tbl.bus_id=bus_tbl.id LEFT JOIN student_register_tbl ON student_transport_tbl.student_id=student_register_tbl.id" +
            " WHERE student_transport_tbl.outlet_id=?1 AND student_transport_tbl.branch_id=?2" +
            " AND student_transport_tbl.academic_year_id=?3 AND  student_transport_tbl.is_bus_fee_pending=false AND student_transport_tbl.is_bus_fee_pending=false AND  student_admission_tbl.standard_id=?4 AND student_transport_tbl.status=?5" +
            " ORDER BY TRIM(student_register_tbl.last_name) ASC", nativeQuery = true)
    List<StudentTransport> getDataByAcademicAndStandard(Long id, Long id1, String academicYearId, String standardId, boolean b);

    @Query(value = "SELECT student_transport_tbl.*, academic_year_tbl.year, standard_tbl.standard_name," +
            " bus_tbl.bus_stop_name FROM `student_transport_tbl` LEFT JOIN student_admission_tbl ON" +
            " student_transport_tbl.student_id=student_admission_tbl.student_id LEFT JOIN academic_year_tbl ON" +
            " student_transport_tbl.academic_year_id=academic_year_tbl.id LEFT JOIN standard_tbl ON" +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN bus_tbl ON" +
            " student_transport_tbl.bus_id=bus_tbl.id LEFT JOIN student_register_tbl ON student_transport_tbl.student_id=student_register_tbl.id" +
            " WHERE student_transport_tbl.outlet_id=?1 AND student_transport_tbl.is_bus_fee_pending=false AND student_transport_tbl.branch_id=?2" +
            " AND student_transport_tbl.academic_year_id=?3 AND student_transport_tbl.status=?4 " +
            " ORDER BY TRIM(student_register_tbl.last_name) ASC", nativeQuery = true)
    List<StudentTransport> getDataByAcademic(Long id, Long id1, String academicYearId, boolean b);

    @Query(value = "SELECT student_transport_tbl.*, academic_year_tbl.year, standard_tbl.standard_name," +
            " bus_tbl.bus_stop_name FROM `student_transport_tbl` LEFT JOIN student_admission_tbl ON" +
            " student_transport_tbl.student_id=student_admission_tbl.student_id LEFT JOIN academic_year_tbl ON" +
            " student_transport_tbl.academic_year_id=academic_year_tbl.id LEFT JOIN standard_tbl ON" +
            " student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN bus_tbl ON" +
            " student_transport_tbl.bus_id=bus_tbl.id LEFT JOIN student_register_tbl ON student_transport_tbl.student_id=student_register_tbl.id" +
            " WHERE student_transport_tbl.outlet_id=?1 AND student_transport_tbl.is_bus_fee_pending=false AND student_transport_tbl.branch_id=?2" +
            " AND student_admission_tbl.standard_id=?3 AND student_transport_tbl.status=?4" +
            " ORDER BY TRIM(student_register_tbl.last_name) ASC", nativeQuery = true)
    List<StudentTransport> getDataByStandard(Long id, Long id1, String standardId, boolean b);

    StudentTransport findByStudentRegisterIdAndStatus(Long id, boolean b);

    StudentTransport findByStudentRegisterIdAndIsBusFeePendingAndStatus(Long id, boolean b, boolean b1);
    @Query(value = "SELECT student_transport_tbl.*, academic_year_tbl.year, standard_tbl.standard_name," +
            " bus_tbl.bus_stop_name FROM `student_transport_tbl` LEFT JOIN student_admission_tbl ON " +
            " student_transport_tbl.student_id=student_admission_tbl.student_id LEFT JOIN academic_year_tbl ON " +
            " student_transport_tbl.academic_year_id=academic_year_tbl.id LEFT JOIN division_tbl ON " +
            " student_admission_tbl.division_id=division_tbl.id LEFT JOIN standard_tbl ON " +
            "  student_admission_tbl.standard_id=standard_tbl.id LEFT JOIN bus_tbl ON " +
            " student_transport_tbl.bus_id=bus_tbl.id LEFT JOIN student_register_tbl ON student_transport_tbl.student_id=student_register_tbl.id " +
            " WHERE student_transport_tbl.outlet_id=?1 AND student_transport_tbl.branch_id=?2 " +
            " AND student_admission_tbl.academic_year_id=?3 AND  student_admission_tbl.standard_id=?4 AND student_admission_tbl.division_id=?5 " +
            " AND student_admission_tbl.student_type=?6 AND student_transport_tbl.status=?7 " +
            " ORDER BY TRIM(student_register_tbl.last_name) ASC", nativeQuery = true)
    List<StudentTransport> getDataByAcademicAndStandardAndDivisionAndType(Long id, Long id1, String academicYearId, String standardId, String divisionId, String studentType, boolean b);


    StudentTransport findByStudentRegisterIdAndAcademicYearIdAndStandardIdAndDivisionIdAndIsBusFeePendingAndStatus(long student_id, long academic_year_id, long standard_id, long division_id, boolean b, boolean b1);
}
