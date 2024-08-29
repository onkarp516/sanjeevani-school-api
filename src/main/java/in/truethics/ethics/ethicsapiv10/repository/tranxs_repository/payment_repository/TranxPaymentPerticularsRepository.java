package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.payment_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.payment.TranxPaymentPerticulars;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TranxPaymentPerticularsRepository extends JpaRepository<TranxPaymentPerticulars, Long> {

    List<TranxPaymentPerticulars> findByOutletIdAndStatusOrderByIdDesc(Long id, boolean b);

    @Query(
            value = "SELECT * FROM tranx_payment_perticulars_tbl WHERE" +
                    " tranx_payment_master_id=?1 AND (ledger_type='SC' OR ledger_type='SD'  OR ledger_type='IE') AND outlet_id=?2 AND status =?3 ",
            nativeQuery = true

    )
    TranxPaymentPerticulars findLedgerName(Long id, Long outlteId, boolean status);
}
