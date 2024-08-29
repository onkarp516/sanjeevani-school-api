package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurOrderDutiesTaxes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranxPurOrderDutiesTaxesRepository extends JpaRepository<TranxPurOrderDutiesTaxes, Long> {

    TranxPurOrderDutiesTaxes findByTranxPurOrderId(Long id);
}
