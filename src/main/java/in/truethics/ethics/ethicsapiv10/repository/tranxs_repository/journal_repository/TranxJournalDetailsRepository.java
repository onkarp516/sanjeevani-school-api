package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.journal_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranxJournalDetailsRepository extends JpaRepository<TranxJournalDetails, Long> {
    List<TranxJournalDetails> findByLedgerMasterIdAndOutletIdAndBranchId(Long sdid, Long id, Long id1);

    List<TranxJournalDetails> findByLedgerMasterIdAndOutletId(Long sdid, Long id);


//    TranxJournalMaster findByIdAndStatus(Long transactionId, boolean b);

    List<TranxJournalDetails> findByTranxJournalMasterIdAndOutletIdAndBranchIdAndStatus(Long voucherId, Long id, Long id1, boolean b);

    List<TranxJournalDetails> findByTranxJournalMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(Long id, Long id1, Long id2, boolean b, String dr);

    List<TranxJournalDetails> findByTranxJournalMasterIdAndOutletIdAndStatus(Long voucherId, Long id, boolean b);

    List<TranxJournalDetails> findByTranxJournalMasterIdAndOutletIdAndStatusAndTypeIgnoreCase(Long voucherId, Long id, boolean b, String dr);

    List<TranxJournalDetails> findByTranxJournalMasterIdAndOutletIdAndBranchIdAndStatusOrderByTypeDesc(Long id, Long id1, Long id2, boolean b);

    TranxJournalDetails findByIdAndStatus(Long transactionId, boolean b);

    List<TranxJournalDetails> findByTranxJournalMasterIdAndStatus(Long id, boolean b);
}
