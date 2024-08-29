package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.payment_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.payment.TranxPaymentMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface TranxPaymentMasterRepository extends JpaRepository<TranxPaymentMaster, Long> {
    @Query(
            value = " SELECT COUNT(*) FROM tranx_payment_master_tbl WHERE outlet_id=?1 AND status=1", nativeQuery = true
    )
    Long findLastRecord(Long id);

    List<TranxPaymentMaster> findByOutletIdAndStatusOrderByIdDesc(Long id, boolean b);

    List<TranxPaymentMaster> findByOutletIdAndStatusAndBranchIdIsNullOrderByIdDesc(Long id, boolean b);

    List<TranxPaymentMaster> findByOutletIdAndBranchIdAndStatusOrderByIdDesc(Long id, Long id1, boolean b);

    TranxPaymentMaster findByIdAndStatus(Long transactionId, boolean b);
}
