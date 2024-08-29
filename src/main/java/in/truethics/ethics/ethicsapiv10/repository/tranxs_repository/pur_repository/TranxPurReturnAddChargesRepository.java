package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurReturnInvoiceAddCharges;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranxPurReturnAddChargesRepository extends JpaRepository<
        TranxPurReturnInvoiceAddCharges, Long> {

    List<TranxPurReturnInvoiceAddCharges> findByPurReturnInvoiceIdAndStatus(Long mPurchaseTranx, boolean b);
}
