package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TranxPurOrderRepository extends JpaRepository<TranxPurOrder, Long> {
    @Query(
            value = "SELECT COUNT(*) FROM tranx_purchase_order_tbl WHERE outlet_id=?1 AND status=1", nativeQuery = true
    )
    Long findLastRecord(Long outletId);

    TranxPurOrder findByIdAndStatus(long poInvoiceId, boolean b);

    List<TranxPurOrder> findAllByStatus(boolean b);

    TranxPurOrder findByIdAndOutletIdAndStatus(Long id, Long id1, boolean b);

    List<TranxPurOrder> findByOutletIdAndStatusOrderByIdDesc(Long id, boolean b);

    List<TranxPurOrder> findBySundryCreditorsIdAndStatusAndTransactionStatusId(Long id, boolean b, long l);
}
