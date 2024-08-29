package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.journal_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


public interface TranxJournalMasterRepository extends JpaRepository<TranxJournalMaster, Long> {
    @Query(
            value = "select COUNT(*) from tranx_journal_master_tbl WHERE outlet_id=?1 AND branch_id=?2", nativeQuery = true
    )
    Long findLastRecord(Long id, Long branchId);

    List<TranxJournalMaster> findByOutletIdAndStatusOrderByIdDesc(Long id, boolean b);

    TranxJournalMaster findByIdAndStatus(Long transactionId, boolean b);

    List<TranxJournalMaster> findByOutletIdAndBranchIdAndStatusOrderByIdDesc(Long id, Long id1, boolean b);

    TranxJournalMaster findByIdAndOutletIdAndBranchIdAndStatus(Long id, Long id1, Long id2, boolean b);

    TranxJournalMaster findByIdAndOutletIdAndStatus(Long id, Long id1, boolean b);

    List<TranxJournalMaster> findByFeeReceiptNo(String receiptNo);
    @Transactional
    @Modifying
    @Query(value="UPDATE tranx_journal_master_tbl SET status=0 WHERE fee_receipt_no=?1",nativeQuery = true)
    void deleteJournalMaster(String receiptNo);

    List<TranxJournalMaster> findByOutletIdAndTranscationDateBetweenAndStatusAndFeeReceiptNoIsNull(Long id, LocalDate parse, LocalDate localDate, boolean b);

    List<TranxJournalMaster> findByOutletIdAndTranscationDateBetweenAndStatusAndFeeReceiptNoIsNotNull(Long id, LocalDate parse, LocalDate parse1, boolean b);

    List<TranxJournalMaster> findByOutletIdAndTranscationDateBetweenAndStatusAndFeeReceiptNo(Long id, LocalDate parse, LocalDate parse1, boolean b, String s);

    List<TranxJournalMaster> findTop5ByOutletIdAndTranscationDateBetweenAndStatusAndFeeReceiptNoIsNotNull(Long id, LocalDate parse, LocalDate parse1, boolean b);

    @Query(value = "SELECT DISTINCT(journal_no), tranx_journal_master_tbl.* FROM `tranx_journal_master_tbl` LEFT JOIN fees_transaction_detail_tbl ON" +
            " tranx_journal_master_tbl.fee_receipt_no=fees_transaction_detail_tbl.receipt_no LEFT JOIN fees_transaction_summary_tbl ON" +
            " fees_transaction_detail_tbl.fees_transaction_summary_id=fees_transaction_summary_tbl.id WHERE fees_transaction_detail_tbl.outlet_id=?1" +
            " AND fees_transaction_detail_tbl.branch_id=?2 AND standard_id=?3 AND academic_year_id=?4 AND student_type=?5 AND fees_transaction_detail_tbl.transaction_date BETWEEN ?6 AND ?7" +
            " AND tranx_journal_master_tbl.status=?8 ORDER BY transaction_date", nativeQuery = true)
    List<TranxJournalMaster> getDataByStandardAndAcademicAndStudentTypeAndDateBetween(Long id, Long aLong, String standardId, String academicYearId, String studentType, LocalDate parse, LocalDate parse1, boolean b);

    List<TranxJournalMaster> findByFeeReceiptNoAndStatus(String receiptNo, boolean b);

    TranxJournalMaster findByFeeReceiptNoAndStatusAndBranchId(String receiptNo, boolean b, Long i);
}
