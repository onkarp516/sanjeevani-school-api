package in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository;

import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerTransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

public interface LedgerTransactionDetailsRepository extends JpaRepository<LedgerTransactionDetails, Long> {

    List<LedgerTransactionDetails> findByLedgerMasterIdAndOutletIdAndTransactionTypeId(long id,
                                                                                       Long id1, long l);

    @Procedure("LEDGER_TRANSACTION_DETAILS_POSTINGS_INSERT_CADMIN")
    void insertIntoLegerTranxDetailsPosting(Long foundation_id, Long principle_id, Long principle_group_id, Long associates_group_id, Long tranx_type_master_id, Long balancing_method_id, Long branch_id, Long outlet_id, String payment_status, Double debit, Double credit, LocalDate transactionDate, LocalDate payment_date, Long tranx_id, String transaction_name, String under_prefix, String financial_year, Long created_by, Long ledger_master_id, String voucher_no);

  @Procedure("LEDGER_TRANSACTION_DETAILS_POSTING_UPDATED")
    void insertIntoLegerTranxDetail(Long foundation_id, Long principle_id, Long principle_group_id, Long associates_group_id, Long tranx_type_master_id, Long balancing_method_id, Long branch_id, Long outlet_id, String payment_status, Double debit, Double credit, LocalDate transactionDate, LocalDate payment_date, Long tranx_id, String transaction_name, String under_prefix, String financial_year, Long created_by, Long ledger_master_id, String voucher_no, boolean b);

    @Query(
            value = " SELECT ledger_master_id FROM ledger_transaction_details_tbl WHERE" +
                    " transaction_id=?1 AND transaction_type_id =?2 ", nativeQuery = true
    )
    List<Long> findByTransactionId(Long id, Long tranxTypeId);

    @Procedure("LEDGER_POSTING_EDIT_TRANX")
    void ledgerPostingEdit(Long ledgerMasterId, Long transactionId,
                           Long transactionTypeId, String tranxType, Double totalamt);

    @Procedure("LEDGER_POSTING_TRANX_REMOVE")
    void ledgerPostingRemove(Long ledgerMasterId, Long transactionId,
                             Long transactionTypeId);

//    LedgerTransactionDetails findByLedgerMasterIdAndStatus(Long id, boolean b);

    LedgerTransactionDetails findByTransactionTypeIdAndLedgerMasterIdAndStatus(long l, Long id, boolean b);

    LedgerTransactionDetails findByOutletIdAndStatusOrderByIdDesc(Long id, boolean b);

    LedgerTransactionDetails findByLedgerMasterIdAndOutletIdAndBranchIdAndTransactionId(Long id, Long id1, Long id2, Long id3);

    LedgerTransactionDetails findByTransactionIdAndLedgerMasterIdAndStatus(Long id, Long id1, boolean b);


    @Query(value = "SELECT SUM(closing_bal) FROM `ledger_transaction_details_tbl` LEFT JOIN ledger_master_tbl ON ledger_transaction_details_tbl.ledger_master_id=ledger_master_tbl.id WHERE ledger_transaction_details_tbl.outlet_id=?1 AND ledger_transaction_details_tbl.branch_id=?2 AND ledger_transaction_details_tbl.status=?3 AND unique_code='DIIC' AND ledger_transaction_details_tbl.transaction_date BETWEEN  ?4 AND ?5 ORDER BY ledger_transaction_details_tbl.transaction_date ASC", nativeQuery = true)
    Double findByDateWiseDirectIncomeTotalAmountOutletAndBranchStatus(Long id, Long id1, boolean b, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT SUM(closing_bal) FROM `ledger_transaction_details_tbl` LEFT JOIN ledger_master_tbl ON ledger_transaction_details_tbl.ledger_master_id=ledger_master_tbl.id WHERE ledger_transaction_details_tbl.outlet_id=?1 AND ledger_transaction_details_tbl.status=?3 AND unique_code='DIIC' AND ledger_transaction_details_tbl.transaction_date BETWEEN  ?4 AND ?5 ORDER BY ledger_transaction_details_tbl.transaction_date ASC", nativeQuery = true)
    Double findByDateWiseDirectIncomeTotalAmountOutletAndStatus(Long id, boolean b, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT SUM(closing_bal) FROM `ledger_transaction_details_tbl` LEFT JOIN ledger_master_tbl ON ledger_transaction_details_tbl.ledger_master_id=ledger_master_tbl.id WHERE ledger_transaction_details_tbl.outlet_id=?1 AND ledger_transaction_details_tbl.branch_id=?2 AND ledger_transaction_details_tbl.status=?3 AND unique_code='INIC' AND ledger_transaction_details_tbl.transaction_date BETWEEN  ?4 AND ?5 ORDER BY ledger_transaction_details_tbl.transaction_date ASC", nativeQuery = true)
    Double findByDateWiseDirectIncomeTotalAmountOutletAndBranchStatusInDIn(Long id, Long id1, boolean b, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT SUM(closing_bal) FROM `ledger_transaction_details_tbl` LEFT JOIN ledger_master_tbl ON ledger_transaction_details_tbl.ledger_master_id=ledger_master_tbl.id WHERE ledger_transaction_details_tbl.outlet_id=?1 AND ledger_transaction_details_tbl.status=?3 AND unique_code='INIC' AND ledger_transaction_details_tbl.transaction_date BETWEEN  ?4 AND ?5 ORDER BY ledger_transaction_details_tbl.transaction_date ASC", nativeQuery = true)
    Double findByDateWiseDirectIncomeTotalAmountOutletAndStatusInDIn(Long id, boolean b, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT SUM(closing_bal) FROM `ledger_transaction_details_tbl` LEFT JOIN ledger_master_tbl ON ledger_transaction_details_tbl.ledger_master_id=ledger_master_tbl.id WHERE ledger_transaction_details_tbl.outlet_id=?1 AND ledger_transaction_details_tbl.branch_id=?2 AND ledger_transaction_details_tbl.status=?3 AND unique_code='DIEX' AND ledger_transaction_details_tbl.transaction_date BETWEEN  ?4 AND ?5 ORDER BY ledger_transaction_details_tbl.transaction_date ASC", nativeQuery = true)
    Double findByDateWiseDirectIncomeTotalAmountOutletAndBranchStatusDE(Long id, Long id1, boolean b, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT SUM(closing_bal) FROM `ledger_transaction_details_tbl` LEFT JOIN ledger_master_tbl ON ledger_transaction_details_tbl.ledger_master_id=ledger_master_tbl.id WHERE ledger_transaction_details_tbl.outlet_id=?1 AND ledger_transaction_details_tbl.status=?3 AND unique_code='DIEX' AND ledger_transaction_details_tbl.transaction_date BETWEEN  ?4 AND ?5 ORDER BY ledger_transaction_details_tbl.transaction_date ASC", nativeQuery = true)
    Double findByDateWiseDirectIncomeTotalAmountOutletAndStatusDE(Long id, boolean b, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT SUM(closing_bal) FROM `ledger_transaction_details_tbl` LEFT JOIN ledger_master_tbl ON ledger_transaction_details_tbl.ledger_master_id=ledger_master_tbl.id WHERE ledger_transaction_details_tbl.outlet_id=?1 AND ledger_transaction_details_tbl.branch_id=?2 AND ledger_transaction_details_tbl.status=?3 AND unique_code='INEX' AND ledger_transaction_details_tbl.transaction_date BETWEEN  ?4 AND ?5 ORDER BY ledger_transaction_details_tbl.transaction_date ASC", nativeQuery = true)
    Double findByDateWiseDirectIncomeTotalAmountOutletAndBranchStatusINDE(Long id, Long id1, boolean b, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT SUM(closing_bal) FROM `ledger_transaction_details_tbl` LEFT JOIN ledger_master_tbl ON ledger_transaction_details_tbl.ledger_master_id=ledger_master_tbl.id WHERE ledger_transaction_details_tbl.outlet_id=?1 AND ledger_transaction_details_tbl.status=?3 AND unique_code='INEX' AND ledger_transaction_details_tbl.transaction_date BETWEEN  ?4 AND ?5 ORDER BY ledger_transaction_details_tbl.transaction_date ASC", nativeQuery = true)
    Double findByDateWiseDirectIncomeTotalAmountOutletAndStatusINDE(Long id, boolean b, LocalDate startDate, LocalDate endDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE ledger_transaction_details_tbl SET status=0 WHERE transaction_id=?1 AND transaction_type_id=?2", nativeQuery = true)
    void updateToStatusZeroTranxLedgerDetailByJVIdAndTranxType(Long id, long l);

    List<LedgerTransactionDetails> findByLedgerMasterIdAndOutletIdAndBranchIdAndStatus(Long sdid, Long id, Long id1, boolean b);

    List<LedgerTransactionDetails> findByLedgerMasterIdAndOutletIdAndStatus(Long sdid, Long id, boolean b);

  @Modifying
  @Transactional
  @Query(value = "UPDATE ledger_transaction_details_tbl SET transaction_date=?1 WHERE transaction_id=?2 AND transaction_type_id=?3", nativeQuery = true)
  void updateInvoiceTransactionDate(LocalDate doa, Long id, long l);

  @Procedure("LEDGER_TRANSACTION_DETAILS_POSTING_TRANX_DATE")
  void insertIntoLegerTranxDetailWithTranxDate(Long foundation_id, Long principle_id, Long principle_group_id, Long associates_group_id,
                                               Long tranx_type_master_id, Long balancing_method_id, Long branch_id, Long outlet_id,
                                               String payment_status, Double debit, Double credit, LocalDate transactionDate,
                                               LocalDate payment_date, Long tranx_id, String transaction_name, String under_prefix,
                                               String financial_year, Long created_by, Long ledger_master_id, String voucher_no, boolean b);


  @Procedure("LEDGER_POSTING_EDIT_TRANX_DATE")
  void ledgerPostingEditTranxDate(Long ledgerMasterId, Long transactionId,Long transactionTypeId, String tranxType, Double totalamt, LocalDate lastTranxDate);


  @Query(value = "SELECT * FROM `ledger_transaction_details_tbl` WHERE ledger_master_id=?1 AND transaction_type_id=?2 AND" +
          " status=?3 ORDER BY transaction_date, id ASC", nativeQuery = true)
  List<LedgerTransactionDetails> getLedgerDataInTranxDateAscWise(Long ledgerId, int i, boolean b);

  @Query(value = "SELECT IFNULL(closing_bal,0) FROM ledger_transaction_details_tbl WHERE transaction_date=?1 AND ledger_master_id=?2" +
          " AND transaction_type_id=?3 AND status=?4 ORDER BY id DESC LIMIT 1", nativeQuery = true)
  Double getDateClosingAmtByTranxDateAndLedger(LocalDate transactionDate, Long ledgerId, int i, boolean b);

  @Modifying
  @Transactional
  @Query(value = "UPDATE ledger_transaction_details_tbl SET status=1 WHERE id=?1", nativeQuery = true)
  void updateToStatusTrueByLedgerTranxId(Long id);

  LedgerTransactionDetails findByLedgerMasterIdAndOutletIdAndBranchIdAndTransactionIdAndStatus(Long id, Long id1, Long id2, Long id3, boolean b);

  List<LedgerTransactionDetails> findTop10ByLedgerMasterIdAndOutletIdAndBranchIdAndStatus(Long id, Long id1, Long id2, boolean b);

  List<LedgerTransactionDetails> findByLedgerMasterIdAndStatus(Long id, boolean b);

  LedgerTransactionDetails findByLedgerMasterIdAndOutletIdAndBranchIdAndTransactionTypeIdAndStatus(Long id, Long id1, Long id2, Long tranxtypeid, boolean b);
  @Query(value = "SELECT * FROM `ledger_transaction_details_tbl` WHERE transaction_id=?1 AND transaction_type_id=?2  AND credit<>0 AND status=?3", nativeQuery = true)
  List<LedgerTransactionDetails> findByTransactionIdAndTransactionTypeIdAndCredit(Long id, Long tranxtypeid, boolean b);
}
