package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TranxReceiptMasterRepositoty extends JpaRepository<TranxReceiptMaster, Long> {

    @Query(
            value = " SELECT COUNT(*) FROM tranx_receipt_master_tbl WHERE outlet_id=?1 AND  branch_id=?2 ", nativeQuery = true
    )
    Long findLastRecord(Long id,Long id1);

    List<TranxReceiptMaster> findByOutletIdAndStatusOrderByIdDesc(Long id, boolean b);

    List<TranxReceiptMaster> findByOutletIdAndBranchIdAndStatusOrderByIdDesc(Long id, Long id1, boolean b);

    List<TranxReceiptMaster> findByOutletIdAndStatusAndBranchIdIsNullOrderByIdDesc(Long id, boolean b);

    TranxReceiptMaster findByIdAndStatus(Long transactionId, boolean b);
    @Query(
            value = " SELECT * FROM tranx_receipt_master_tbl WHERE transaction_date IS NULL and fiscal_year_id IS NULL outlet_id=?1 AND  branch_id=?2 and status=true ", nativeQuery = true
    )
    List<TranxReceiptMaster> findReceiptMasterDataHavingtrnxDateNull(boolean b);

    @Query(value="SELECT DISTINCT(tranx_receipt_master_tbl.receipt_no), tranx_receipt_master_tbl.* FROM `tranx_receipt_master_tbl` LEFT JOIN fees_transaction_detail_tbl" +
            " ON tranx_receipt_master_tbl.receipt_no=fees_transaction_detail_tbl.receipt_no LEFT JOIN fees_transaction_summary_tbl ON " +
            "fees_transaction_detail_tbl.fees_transaction_summary_id=fees_transaction_summary_tbl.id WHERE fees_transaction_detail_tbl.outlet_id=?1 " +
            " AND fees_transaction_detail_tbl.branch_id=?2 AND standard_id=?3 AND academic_year_id=?4 AND student_type=?5 AND fees_transaction_detail_tbl.transaction_date" +
            " BETWEEN ?6 AND ?7 AND tranx_receipt_master_tbl.status=?8 ORDER BY transaction_date",nativeQuery = true)
    List<TranxReceiptMaster> getDataFromReceiptMasterByStandardAndAcademicAndStudentTypeAndDateBetween(Long id, Long id1, String standardId, String academicYearId, String studentType, LocalDate parse, LocalDate parse1, boolean b);

    List<TranxReceiptMaster> findByReceiptNo(String receiptNo);
}
