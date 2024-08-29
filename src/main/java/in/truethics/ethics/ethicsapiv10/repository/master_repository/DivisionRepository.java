package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.Division;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DivisionRepository extends JpaRepository<Division, Long> {
    List<Division> findByOutletIdAndStatus(Long id, boolean b);

    Division findByIdAndStatus(long id, boolean b);

    List<Division> findByStandardIdAndStatus(Long standardId, boolean b);

    List<Division> findByOutletIdAndBranchIdAndStatus(Long id, Long id1, boolean b);
}
