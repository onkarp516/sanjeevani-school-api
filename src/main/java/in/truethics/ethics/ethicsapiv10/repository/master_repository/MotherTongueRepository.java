package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.MotherTongue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotherTongueRepository extends JpaRepository<MotherTongue, Long> {
    MotherTongue findByIdAndStatus(long id, boolean b);

    List<MotherTongue> findByCreatedByAndStatus(Long id, boolean b);

    List<MotherTongue> findByStatus(boolean b);
}
