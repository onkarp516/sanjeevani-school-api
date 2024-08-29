package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.Caste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CasteRepository extends JpaRepository<Caste, Long> {
    List<Caste> findByReligionIdAndStatus(Long religionId, boolean b);

    Caste findByIdAndStatus(long id, boolean b);

    List<Caste> findByCreatedByAndStatus(Long id, boolean b);
}
