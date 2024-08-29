package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.debitnote_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.debit_note.TranxDebitNoteDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranxDebitNoteDetailsRepository extends JpaRepository<TranxDebitNoteDetails, Long> {

    // List<TranxDebitNoteDetails> findBySundryCreditorIdAndStatusAndTransactionStatusIdAndAdjustmentStatusAndOutletId(Long sundryCreditorId, boolean b, long l, String future, Long id);

    List<TranxDebitNoteDetails> findBySundryCreditorIdAndStatusAndTransactionStatusIdAndOutletId(Long sundryCreditorId, boolean b, long l, Long id);
}
