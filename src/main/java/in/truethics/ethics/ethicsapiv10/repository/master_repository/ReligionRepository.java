package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.Religion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReligionRepository extends JpaRepository<Religion, Long> {
    Religion findByIdAndStatus(Long religionId, boolean b);

    List<Religion> findByCreatedByAndStatus(Long id, boolean b);

    List<Religion> findByStatus(boolean b);
}
