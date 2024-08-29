package in.truethics.ethics.ethicsapiv10.repository.history_repository;

import in.truethics.ethics.ethicsapiv10.model.history_table.LedgerMasterHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerMasterHistoryRepository extends JpaRepository<LedgerMasterHistory ,Long> {
}
