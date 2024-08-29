package in.truethics.ethics.ethicsapiv10.repository.master_repository;


import in.truethics.ethics.ethicsapiv10.model.school_master.FeesMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeesMasterRepository extends JpaRepository<FeesMaster, Long> {
    List<FeesMaster> findByOutletIdAndStatus(Long outletId, boolean b);

    FeesMaster findByBranchIdAndStandardIdAndDivisionIdAndAcademicYearIdAndStudentTypeAndStatus(Long branchId, Long standardId, Long divisionId, Long academicYearId, Integer studentType, boolean b);

    FeesMaster findByIdAndStatus(Long feesMasterId, boolean b);

    FeesMaster findByBranchIdAndStandardIdAndAcademicYearIdAndStatus(Long branchId, Long standardId, Long academicYearId, boolean b);

    FeesMaster findByBranchIdAndStandardIdAndAcademicYearIdAndStudentTypeAndStatus(Long branchId, Long standardId, Long academicYearId, Integer studentType, boolean b);

    List<FeesMaster> findByOutletIdAndBranchIdAndStatus(Long outletId, Long id, boolean b);

    @Query(value = "SELECT IFNULL(bus_tbl.bus_fee, 0) FROM `student_transport_tbl` LEFT JOIN bus_tbl ON " +
            "student_transport_tbl.bus_id=bus_tbl.id WHERE student_id=?1 AND academic_year_id=?2", nativeQuery = true)
    String getStudentBusFeeByStudentIdAndAcademic(Long studentId, Long id);

    FeesMaster findByBranchIdAndStandardIdAndAcademicYearIdAndStudentTypeAndStudentGroupAndStatus(Long branchId, Long standardId, Long academicYearId, Integer studentType, int parseInt, boolean b);
}
