package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.debitnote_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.debit_note.TranxDebitNoteNewReferenceMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TranxDebitNoteNewReferenceRepository extends
        JpaRepository<TranxDebitNoteNewReferenceMaster, Long> {

    @Query(
            value = " SELECT COUNT(*) FROM tranx_debit_note_new_reference_tbl WHERE " +
                    "outlet_id=?1 AND status =1", nativeQuery = true
    )
    Long findLastRecord(Long id);

    List<TranxDebitNoteNewReferenceMaster>
    findBySundryCreditorIdAndStatusAndTransactionStatusIdAndAdjustmentStatusAndOutletId(
            Long sundryCreditorId, boolean status, long tranxStatusId, String future, Long outletId);

    TranxDebitNoteNewReferenceMaster findByIdAndStatus(long debitNoteId, boolean b);

    List<TranxDebitNoteNewReferenceMaster> findByOutletIdAndStatusOrderByIdDesc(Long id, boolean b);
}
