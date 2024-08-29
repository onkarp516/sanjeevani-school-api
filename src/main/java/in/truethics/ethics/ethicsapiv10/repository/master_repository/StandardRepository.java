package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.Standard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StandardRepository extends JpaRepository<Standard, Long> {
    Standard findByIdAndStatus(long id, boolean b);

    List<Standard> findByOutletIdAndStatus(Long outletId, boolean b);

    List<Standard> findByBranchIdAndStatus(Long branchId, boolean b);

    List<Standard> findByOutletIdAndBranchIdAndStatus(Long outletId, Long id, boolean b);


    List<Standard> findByBranchIdAndStatusOrderByStandardNameAsc(Long branchId, boolean b);

    List<Standard> findByStatus(boolean b);

    Standard findByStandardNameAndBranchId(String s, Long branchId);

    //Standard findByStandardNameAndStatus(String standardName, boolean b);
}
