package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.contra_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.contra.TranxContraDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TranxContraDetailsRepository extends JpaRepository<TranxContraDetails, Long> {
    @Query(
            value = "SELECT * FROM tranx_contra_details_tbl WHERE" +
                    " tranx_contra_master_tbl=?1 AND (ledger_type='others' OR ledger_type='bank_account') And outlet_id=?2 AND status =?3 ",
            nativeQuery = true

    )
    TranxContraDetails findLedgerName(Long id, Long outlteId, boolean status);


    List<TranxContraDetails> findByLedgerMasterIdAndOutletIdAndBranchIdAndStatus(Long voucherId, Long id, Long id1, boolean b);


    TranxContraDetails findByTranxContraMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(Long id, Long id1, Long id2, boolean b, String dr);

    List<TranxContraDetails> findByLedgerMasterIdAndOutletIdAndStatus(Long voucherId, Long id, boolean b);

    TranxContraDetails findByIdAndOutletIdAndBranchIdAndStatus(Long transactionId, Long id, Long id1, boolean b);

    TranxContraDetails findByIdAndOutletIdAndStatus(Long transactionId, Long id, boolean b);
}
