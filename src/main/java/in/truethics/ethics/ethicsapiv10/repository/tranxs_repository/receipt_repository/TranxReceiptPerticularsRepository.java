package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptPerticulars;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TranxReceiptPerticularsRepository extends JpaRepository<TranxReceiptPerticulars, Long> {
    @Query(
            value = "SELECT * FROM tranx_receipt_perticulars_tbl WHERE" +
                    " tranx_receipt_master_id=?1 AND (ledger_type='SC' OR ledger_type='SD')  AND outlet_id=?2 AND status =?3 ",
            nativeQuery = true

    )
    TranxReceiptPerticulars findLedgerName(Long id, Long outlteId, boolean status);

}
