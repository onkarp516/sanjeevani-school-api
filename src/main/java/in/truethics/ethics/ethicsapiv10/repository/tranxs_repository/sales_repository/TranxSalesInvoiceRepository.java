package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository;

import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TranxSalesInvoiceRepository extends JpaRepository<TranxSalesInvoice, Long> {
    @Query(
            value = " SELECT COUNT(*) FROM tranx_sales_invoice_tbl WHERE outlet_id=?1 AND branch_id=?2 ", nativeQuery = true
    )
    Long findLastRecord(Long id, Long branchId);

    TranxSalesInvoice findBySundryDebtorsId(Long id);

    TranxSalesInvoice findByIdAndStatus(long invoice_id, boolean b);

    // Sales Invoice List Block
//    List<TranxSalesInvoice> findByOutletIdAndBranchIdAndStatusOrderByIdDesc(Long id, Long id1, boolean b);
//
//    List<TranxSalesInvoice> findByOutletIdAndStatusOrderByIdDesc(Long id, boolean b);

    // Sales Invoice List From Date To End Date if Both Value Given
//
    List<TranxSalesInvoice> findByOutletIdAndBranchIdAndStatusAndBillDateBetweenOrderByIdDesc(Long id, Long id1, boolean b, LocalDate currentDatep, LocalDate endDatep);

    List<TranxSalesInvoice> findByOutletIdAndStatusAndBillDateBetweenOrderByIdDesc(Long id, boolean b, LocalDate currentDatep, LocalDate endDatep);

    // for load All list
    List<TranxSalesInvoice> findByOutletIdAndBranchIdAndStatusOrderByIdDesc(Long id, Long id1, boolean b);

    List<TranxSalesInvoice> findByOutletIdAndStatusOrderByIdDesc(Long id, boolean b);
    /* get Ledgre Details By ID*/

    TranxSalesInvoice findBySundryDebtorsIdAndOutletIdAndBranchId(Long sdid, Long id, Long id1);

    TranxSalesInvoice findBySundryDebtorsIdAndOutletId(Long sdid, Long id);

    TranxSalesInvoice findByIdAndOutletIdAndBranchIdAndStatus(Long voucherId, Long id, Long id1, boolean b);

    TranxSalesInvoice findByIdAndOutletIdAndStatus(Long voucherId, Long id, boolean b);

    TranxSalesInvoice findBySundryDebtorsIdAndStatus(Long id, boolean b);

    @Query(value = "SELECT IFNULL(SUM(total_amount),0) FROM `tranx_sales_invoice_tbl` WHERE outlet_id=?1 AND status=?2 " +
            "AND bill_date BETWEEN ?3 AND ?4 ORDER BY bill_date ASC", nativeQuery = true)
    Double findByDateWiseTotalAmountOuletAndStatus(Long id, boolean b, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT IFNULL(SUM(total_amount),0) FROM `tranx_sales_invoice_tbl`  WHERE outlet_id=?1 AND branch_id=?2 AND status=?3 AND bill_date BETWEEN ?4 AND ?5 ORDER BY bill_date ASC", nativeQuery = true)
    Double findByDateWiseTotalAmountOuletAndBranchStatus(Long id, Long id1, boolean b, LocalDate startDate, LocalDate endDate);

    List<TranxSalesInvoice> findByOutletIdAndStatus(Long id, boolean b);

    List<TranxSalesInvoice> findByOutletIdAndStatusAndId(Long id, boolean b, long l);
    //Sales Invoice List if only Current Date given

//    List<TranxSalesInvoice> findByOutletIdAndBranchIdAndStatusAndBillDateOrderByIdDesc(Long id, Long id1, boolean b, LocalDate currentDatep);

//    List<TranxSalesInvoice> findByOutletIdAndStatusAndBillDateOrderByIdDesc(Long id, boolean b, LocalDate currentDatep);
}
