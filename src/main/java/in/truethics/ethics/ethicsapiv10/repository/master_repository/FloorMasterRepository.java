package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.master.FloorMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FloorMasterRepository extends JpaRepository<FloorMaster, Long> {
    List<FloorMaster> findAllByStatus(boolean b);
}
