package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.payment_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.payment.TranxPaymentPerticularsDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranxPaymentPerticularsDetailsRepository extends JpaRepository<TranxPaymentPerticularsDetails, Long> {

    List<TranxPaymentPerticularsDetails> findByLedgerMasterIdAndOutletIdAndBranchIdAndStatus(Long voucherId, Long id, Long id1, boolean b);


    TranxPaymentPerticularsDetails findBytranxPaymentMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(Long id, Long id1, Long id2, boolean b, String dr);

    List<TranxPaymentPerticularsDetails> findByLedgerMasterIdAndOutletIdAndStatus(Long voucherId, Long id, boolean b);

    TranxPaymentPerticularsDetails findByIdAndOutletIdAndBranchIdAndStatus(Long transactionId, Long id, Long id1, boolean b);

    TranxPaymentPerticularsDetails findByIdAndOutletIdAndStatus(Long transactionId, Long id, boolean b);
}
