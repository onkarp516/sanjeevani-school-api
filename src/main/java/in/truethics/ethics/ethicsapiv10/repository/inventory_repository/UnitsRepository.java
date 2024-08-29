package in.truethics.ethics.ethicsapiv10.repository.inventory_repository;

import in.truethics.ethics.ethicsapiv10.model.inventory.Units;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitsRepository extends JpaRepository<Units, Long> {

    List<Units> findByOutletIdAndStatus(Long id, boolean b);

    Units findByIdAndStatus(Long unitId, boolean b);
}
