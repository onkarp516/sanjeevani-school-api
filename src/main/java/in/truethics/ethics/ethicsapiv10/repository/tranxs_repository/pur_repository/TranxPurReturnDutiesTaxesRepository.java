package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurReturnInvoice;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurReturnInvoiceDutiesTaxes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranxPurReturnDutiesTaxesRepository extends JpaRepository<
        TranxPurReturnInvoiceDutiesTaxes, Long> {
    List<TranxPurReturnInvoiceDutiesTaxes> findByPurReturnInvoiceAndStatus(TranxPurReturnInvoice mPurchaseTranx, boolean b);
}
