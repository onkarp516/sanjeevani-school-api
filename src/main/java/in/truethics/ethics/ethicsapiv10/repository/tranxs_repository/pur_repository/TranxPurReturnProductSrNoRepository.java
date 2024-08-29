package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurReturnInvoiceProductSrNo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranxPurReturnProductSrNoRepository extends
        JpaRepository<TranxPurReturnInvoiceProductSrNo, Long> {
}
