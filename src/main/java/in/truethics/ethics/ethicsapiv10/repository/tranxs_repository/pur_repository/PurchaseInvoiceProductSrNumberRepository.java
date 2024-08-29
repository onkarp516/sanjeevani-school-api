package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurchaseInvoiceProductSrNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseInvoiceProductSrNumberRepository extends
        JpaRepository<TranxPurchaseInvoiceProductSrNumber, Long> {
    List<TranxPurchaseInvoiceProductSrNumber> findByPurchaseTransactionId(Long id);
}
