package in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository;

import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerTransactionPostings;
import in.truethics.ethics.ethicsapiv10.model.school_master.StudentRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LedgerTransactionPostingsRepository extends JpaRepository<LedgerTransactionPostings, Long> {


    @Query(value = "SELECT * FROM `ledger_transaction_postings_tbl` WHERE ledger_master_id=?1 and branch_id=?2 and status=true", nativeQuery = true)
    List<LedgerTransactionPostings> findByMessLedgerIdAndStatus(Long id, Long id1);

    @Query(value = "SELECT * FROM `ledger_transaction_postings_tbl` WHERE ledger_master_id=?1 and branch_id=?2 and status=true", nativeQuery = true)
    List<Object[]> findByHostelLedgerIdAndStatus(Long ledgerId, long underBranchId);

    @Query(value = "SELECT * FROM `ledger_transaction_postings_tbl`  WHERE transaction_date IS NULL OR fiscal_year_id" +
            " Is NULL and outlet_id=1 and branch_id=1 and status=true;", nativeQuery = true)
    List<LedgerTransactionPostings> findDataWhichHavingTrnxDateFiscalNull(Long id, Long id1, boolean b);


    LedgerTransactionPostings findByInvoiceNoAndLedgerMasterIdAndTransactionIdAndLedgerTypeAndTranxTypeAndStatus(String salesInvoiceNo, Long id, Long id1, String dr, String s, boolean b);

    LedgerTransactionPostings findByLedgerMasterIdAndTransactionTypeIdAndTransactionId(Long ledgerId,
                                                                                       Long tranxType, Long tranxId);

    @Query(
            value = "SELECT IFNULL(SUM(amount),0.0) ,ledger_type FROM `ledger_transaction_postings_tbl` WHERE ledger_type ='CR' AND " +
                    "ledger_master_id=?1 ORDER BY transaction_date ASC", nativeQuery = true
    )
    Double findsumCR(Long id);

    @Query(
            value = "SELECT IFNULL(SUM(amount),0.0),ledger_type FROM `ledger_transaction_postings_tbl` WHERE ledger_type ='DR' AND " +
                    "ledger_master_id=?1 ORDER BY transaction_date ASC", nativeQuery = true
    )
    Double findsumDR(Long id);

    List<LedgerTransactionPostings> findByLedgerMasterIdAndOutletIdAndBranchIdAndStatus(Long sdid, Long id, Long id1, boolean b);

    List<LedgerTransactionPostings> findByLedgerMasterIdAndOutletIdAndStatus(Long sdid, Long id, boolean b);


    LedgerTransactionPostings findByLedgerMasterIdAndOutletIdAndBranchIdAndTransactionIdAndStatus(Long id, Long id1, Long id2, Long id3, boolean b);

    LedgerTransactionPostings findByTransactionTypeIdAndLedgerMasterIdAndStatusAndTransactionId(long l, Long id, boolean b, Long id1);

    @Query(
            value = "SELECT * FROM `ledger_transaction_postings_tbl` WHERE outlet_id=?1 AND branch_id=?2 AND status=?3 AND " +
                    "ledger_master_id=?4 AND DATE(transaction_date) BETWEEN ?5 AND ?6 ORDER BY transaction_date ASC", nativeQuery = true
    )
    List<LedgerTransactionPostings> findByDetailsBetweenDates(Long id, Long id1, boolean b, Long ledger_master_id, LocalDate startDate, LocalDate endDate);

    @Query(
            value = "SELECT * FROM `ledger_transaction_postings_tbl` WHERE outlet_id=?1 AND branch_id IS NULL AND status=?2 AND " +
                    "ledger_master_id=?3 AND DATE(transaction_date) BETWEEN ?4 AND ?5 ORDER BY transaction_date ASC", nativeQuery = true
    )
    List<LedgerTransactionPostings> findByDetails(Long id, boolean b, Long ledger_master_id,LocalDate startDate, LocalDate endDate);

    @Query(
            value = "SELECT * FROM `ledger_transaction_postings_tbl` WHERE outlet_id=?1 AND branch_id=?2 AND status=?3 AND " +
                    "ledger_master_id=?4 ORDER BY transaction_date ASC", nativeQuery = true
    )
    List<LedgerTransactionPostings> findByDetailsBranch(Long id, Long id1, boolean b, Long ledger_master_id);

    @Query(
            value = "SELECT * FROM `ledger_transaction_postings_tbl` WHERE outlet_id=?1 AND status=?2 AND " +
                    "ledger_master_id=?3 ORDER BY transaction_date ASC", nativeQuery = true
    )
    List<LedgerTransactionPostings> findByDetailsFisc(Long id, boolean b, Long ledger_master_id);

    @Query(
            value = " SELECT IFNULL(SUM(amount),0.0) FROM `ledger_transaction_postings_tbl` WHERE ledger_master_id=?1 AND " +
                    "outlet_id=?2 AND branch_id=?3 AND " +
                    "ledger_type=?4 AND transaction_date <=?5 ", nativeQuery = true
    )
    Double findLedgerOpeningBranch(Long ledgerId, Long outletId, Long branchId,String ledgerType, LocalDate startDate);
    @Query(
            value = " SELECT IFNULL(SUM(amount),0.0) FROM `ledger_transaction_postings_tbl` WHERE ledger_master_id=?1 AND " +
                    "outlet_id=?2 AND branch_id IS NULL AND " +
                    "ledger_type=?3 AND transaction_date <= ?4", nativeQuery = true
    )
    Double findLedgerOpening(Long ledgerId, Long outletId,String ledgerType, LocalDate startDate);
}
