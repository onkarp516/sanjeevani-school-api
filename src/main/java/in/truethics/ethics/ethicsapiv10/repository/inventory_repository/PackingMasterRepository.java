package in.truethics.ethics.ethicsapiv10.repository.inventory_repository;

import in.truethics.ethics.ethicsapiv10.model.inventory.PackingMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackingMasterRepository extends JpaRepository<PackingMaster, Long> {

    PackingMaster findByIdAndStatus(long aPackage, boolean b);

    List<PackingMaster> findByOutletIdAndStatus(Long id, boolean b);
}
