package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurchaseChallanProductSrNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranxPurchaseChallanProductSrNumberRepository extends JpaRepository<TranxPurchaseChallanProductSrNumber, Long> {
    List<TranxPurchaseChallanProductSrNumber> findByProductIdAndStatus(Long id, boolean b);
}
