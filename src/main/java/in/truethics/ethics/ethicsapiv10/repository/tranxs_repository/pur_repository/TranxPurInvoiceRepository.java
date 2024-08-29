package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TranxPurInvoiceRepository extends JpaRepository<TranxPurInvoice, Long> {
    @Query(
            value = " SELECT COUNT(*) FROM tranx_purchase_invoice_tbl WHERE outlet_id=?1 AND status=1", nativeQuery = true
    )
    Long findLastRecord(Long id);

    TranxPurInvoice findByIdAndOutletIdAndStatus(Long id, Long id1, boolean b);

    @Query(
            value = " SELECT * FROM tranx_purchase_invoice_tbl as a WHERE a.outlet_id=?1 And a.status=?2 " +
                    "And a.sundry_creditors_id=?3 AND date(a.invoice_date) BETWEEN ?4 AND ?5",
            nativeQuery = true
    )
    List<TranxPurInvoice> findBySuppliersWithDates(
            Long id, boolean status, long sundry_creditor_id, String dateFrom, String dateTo);

    TranxPurInvoice findByIdAndStatus(long pur_invoice_id, boolean b);


    List<TranxPurInvoice> findByOutletIdAndStatusAndSundryCreditorsId(Long id, boolean b,
                                                                      long sundry_creditor_id);

    List<TranxPurInvoice> findByOutletIdAndStatusOrderByIdDesc(Long id, boolean b);

    List<TranxPurInvoice> findBySundryCreditorsIdAndOutletIdAndStatus(Long ledgerId, Long id, boolean b);

    @Query(
            value = " SELECT * FROM tranx_purchase_invoice_tbl as a WHERE a.outlet_id=?1 And a.status=?2 " +
                    "And a.sundry_creditors_id=?3 And balance>0",
            nativeQuery = true
    )
    List<TranxPurInvoice> findPendingBills(Long outletId, boolean b, Long ledgerId);


    TranxPurInvoice findByIdAndOutletIdAndBranchIdAndStatus(Long transactionId, Long id, Long id1, boolean b);
}
