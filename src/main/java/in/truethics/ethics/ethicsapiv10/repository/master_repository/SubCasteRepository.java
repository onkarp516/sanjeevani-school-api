package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.SubCaste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCasteRepository extends JpaRepository<SubCaste, Long> {
    List<SubCaste> findByCasteIdAndStatus(Long casteId, boolean b);

    List<SubCaste> findByStatus(boolean b);

    List<SubCaste> findByCreatedByAndStatus(Long id, boolean b);

    SubCaste findByIdAndStatus(long id, boolean b);
}
