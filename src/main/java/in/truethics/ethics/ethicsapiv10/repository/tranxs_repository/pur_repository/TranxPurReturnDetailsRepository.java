package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurReturnInvoiceDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranxPurReturnDetailsRepository extends JpaRepository<TranxPurReturnInvoiceDetails, Long> {
}
