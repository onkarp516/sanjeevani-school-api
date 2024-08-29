package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurChallanDetailsUnits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranxPurChallanDetailsUnitRepository extends JpaRepository<TranxPurChallanDetailsUnits, Long> {
    List<TranxPurChallanDetailsUnits> findByTranxPurChallanDetailsIdAndStatus(Long id, boolean b);
}
