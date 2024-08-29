package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    List<AcademicYear> findByOutletIdAndStatus(Long id, boolean b);

    AcademicYear findByIdAndStatus(long id, boolean b);

    List<AcademicYear> findByBranchIdAndStatus(Long branchId, boolean b);

    List<AcademicYear> findByOutletIdAndBranchIdAndStatus(Long id, Long id1, boolean b);

    AcademicYear findByYearAndStatus(String academicYearName, boolean b);


    @Query(value="update academic_year_tbl set status=false where id=?1",nativeQuery = true)
    void deleteAcademicYear(Long academicyearId);

    AcademicYear findByBranchIdAndFiscalYearIdAndStatus(Long id, Long id1, boolean b);
}
