package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptPerticularsDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranxReceiptPerticularsDetailsRepository extends JpaRepository<TranxReceiptPerticularsDetails, Long> {

    List<TranxReceiptPerticularsDetails> findByLedgerMasterIdAndOutletIdAndBranchIdAndStatus(Long voucherId, Long id, Long id1, boolean b);



    List<TranxReceiptPerticularsDetails> findByLedgerMasterIdAndOutletIdAndStatus(Long voucherId, Long id, boolean b);

    List<TranxReceiptPerticularsDetails> findByTranxReceiptMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(Long voucherId, Long id, Long id1, boolean b, String cr);

    List<TranxReceiptPerticularsDetails> findByTranxReceiptMasterIdAndOutletIdAndStatusAndTypeIgnoreCase(Long voucherId, Long id, boolean b, String cr);


    List<TranxReceiptPerticularsDetails> findByTranxReceiptMasterIdAndOutletIdAndBranchIdAndStatusOrderByTypeDesc(Long id, Long id1, Long id2, boolean b);

    List<TranxReceiptPerticularsDetails> findByTranxReceiptMasterIdAndOutletIdAndBranchIdAndStatus(Long id, Long id1, Long id2, boolean b);

    TranxReceiptPerticularsDetails findByIdAndOutletIdAndBranchIdAndStatus(Long transactionId, Long id, Long id1, boolean b);

    TranxReceiptPerticularsDetails findByIdAndOutletIdAndStatus(Long transactionId, Long id, boolean b);

    List<TranxReceiptPerticularsDetails> findByTranxReceiptMasterIdAndOutletIdAndStatus(Long transactionId, Long id, boolean b);
}
