package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurChallan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TranxPurChallanRepository extends JpaRepository<TranxPurChallan, Long> {
    @Query(
            value = " SELECT COUNT(*) FROM tranx_purchase_challan_tbl WHERE outlet_id=?1 AND status=1", nativeQuery = true
    )
    Long findLastRecord(Long outletId);

    List<TranxPurChallan> findAllByStatus(boolean b);

    TranxPurChallan findByIdAndStatus(long poChallanInvoiceId, boolean b);

    List<TranxPurChallan> findBySundryCreditorsIdAndStatus(Long id, boolean b);

    List<TranxPurChallan> findByOutletIdAndStatusOrderByIdDesc(Long id, boolean b);

    @Query(
            value = " SELECT * FROM tranx_purchase_challan_tbl as a WHERE a.outlet_id=?1 And a.status=?2 " +
                    "And a.sundry_creditors_id=?3 AND date(a.invoice_date) BETWEEN ?4 AND ?5",
            nativeQuery = true
    )
    List<TranxPurChallan> findBySuppliersWithDates(
            Long id, boolean status, long sundry_creditor_id, String dateFrom, String dateTo);

    List<TranxPurChallan> findBySundryCreditorsIdAndOutletIdAndTransactionStatusIdAndStatus(
            Long ledgerId, Long id, long l, boolean b);
}
