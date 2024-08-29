package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurOrderDetailsUnits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranxPurOrderDetailsUnitRepository extends JpaRepository<TranxPurOrderDetailsUnits, Long> {

    List<TranxPurOrderDetailsUnits> findByTranxPurOrderDetailsIdAndStatus(Long id, boolean b);
}
