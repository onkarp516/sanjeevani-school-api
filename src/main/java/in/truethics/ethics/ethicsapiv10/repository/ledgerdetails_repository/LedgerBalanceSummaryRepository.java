package in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository;

import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerBalanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LedgerBalanceSummaryRepository extends JpaRepository<LedgerBalanceSummary, Long> {
    LedgerBalanceSummary findByLedgerMasterId(Long id);

    @Query(
            value = " SELECT ledger_master_id,balance,ledger_name FROM ledger_balance_summary_tbl" +
                    " WHERE principle_groups_id=? AND balance<>0", nativeQuery = true
    )
    List<Object[]> calculate_total_amount(Long id);

    List<LedgerBalanceSummary> findByOutletId(Long id);

    List<LedgerBalanceSummary> findByPrincipleGroupsId(Long valueOf);

    @Query(
            value = " SELECT balance FROM ledger_balance_summary_tbl" +
                    " WHERE id=?1", nativeQuery = true
    )
    Double findBalance(Long sundryCreditorId);

    List<LedgerBalanceSummary> findByOutletIdOrderByIdDesc(Long id);

    LedgerBalanceSummary findByLedgerMasterIdAndStatus(Long id, boolean b);

    List<LedgerBalanceSummary> findByOutletIdAndBranchIdOrderByIdDesc(Long id, Long id1);

    List<LedgerBalanceSummary> findByOutletIdAndBranchNullOrderByIdDesc(Long id);
}
