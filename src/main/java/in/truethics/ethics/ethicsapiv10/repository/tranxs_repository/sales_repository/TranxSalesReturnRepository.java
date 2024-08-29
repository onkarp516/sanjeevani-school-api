package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository;


import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesReturnInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TranxSalesReturnRepository extends JpaRepository<TranxSalesReturnInvoice, Long> {

    @Query(value = " SELECT COUNT(*) FROM tranx_sales_return_invoice_tbl WHERE outlet_id=?1 AND status =1 ", nativeQuery = true)
    Long findLastRecord(Long id);

    TranxSalesReturnInvoice findByIdAndStatus(Long transactionId, boolean b);

    TranxSalesReturnInvoice findByIdAndOutletIdAndBranchIdAndStatus(Long transactionId, Long id, Long id1, boolean b);

    TranxSalesReturnInvoice findByIdAndOutletIdAndStatus(Long transactionId, Long id, boolean b);
}
