package in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository;

import in.truethics.ethics.ethicsapiv10.model.master.LedgerGstDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerGstDetailsRepository extends JpaRepository<LedgerGstDetails, Long> {
    List<LedgerGstDetails> findByLedgerMasterIdAndStatus(Long ledgerId, boolean b);

    LedgerGstDetails findByIdAndStatus(long id, boolean b);
}
