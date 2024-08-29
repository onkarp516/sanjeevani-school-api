package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.CasteCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CasteCategoryRepository extends JpaRepository<CasteCategory, Long> {
    List<CasteCategory> findBySubCasteIdAndStatus(Long subCasteId, boolean b);

    CasteCategory findByIdAndStatus(long id, boolean b);

    List<CasteCategory> findByCreatedByAndStatus(Long id, boolean b);

    List<CasteCategory> findByStatus(boolean b);
}
