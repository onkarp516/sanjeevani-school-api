package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurReturnInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TranxPurReturnsRepository extends JpaRepository<TranxPurReturnInvoice, Long> {

    @Query(
            value = " SELECT COUNT(*) FROM tranx_pur_return_invoice_tbl WHERE outlet_id=?1 AND status =1", nativeQuery = true
    )
    Long findLastRecord(Long id);

    List<TranxPurReturnInvoice> findByOutletIdAndStatusOrderByIdDesc(Long id, boolean b);

    TranxPurReturnInvoice findByIdAndStatus(Long transactionId, boolean b);

    TranxPurReturnInvoice findByIdAndOutletIdAndBranchIdAndStatus(Long transactionId, Long id, Long id1, boolean b);

    TranxPurReturnInvoice findByIdAndOutletIdAndStatus(Long transactionId, Long id, boolean b);
}
