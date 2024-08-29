package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurInvoiceDetailsUnits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranxPurInvoiceDetailsUnitsRepository extends JpaRepository<TranxPurInvoiceDetailsUnits, Long> {
    List<TranxPurInvoiceDetailsUnits> findByPurInvoiceDetailsIdAndStatus(Long id, boolean b);
}
