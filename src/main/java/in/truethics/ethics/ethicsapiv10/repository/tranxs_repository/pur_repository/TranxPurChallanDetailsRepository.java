package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurChallanDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranxPurChallanDetailsRepository extends JpaRepository<TranxPurChallanDetails, Long> {
    List<TranxPurChallanDetails> findByTranxPurChallanIdAndStatus(long id, boolean b);

    TranxPurChallanDetails findByTranxPurChallanIdAndProductIdAndStatus(Long referenceId, Long prdId, boolean b);
}
