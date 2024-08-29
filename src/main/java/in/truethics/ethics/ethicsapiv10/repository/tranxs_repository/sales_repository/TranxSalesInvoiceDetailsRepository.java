package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository;

import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesInvoiceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TranxSalesInvoiceDetailsRepository extends JpaRepository<TranxSalesInvoiceDetails, Long> {
    List<TranxSalesInvoiceDetails> findByTranxSalesInvoiceIdAndStatus(Long id, boolean b);

    TranxSalesInvoiceDetails findTop1ByTranxSalesInvoiceIdAndStatusOrderByIdAsc(Long id, boolean b);

    TranxSalesInvoiceDetails findByStatusAndTranxSalesInvoiceIdAndFeeHeadId(boolean b, Long id, Long id1);

    @Transactional
    @Modifying
    @Query(value="update tranx_sales_invoice_details_tbl SET status=?2 where sales_invoice_id=?1",nativeQuery = true)
    void MakeStatusZero(Long id, boolean b1);

}
