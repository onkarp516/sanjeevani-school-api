package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findAllByStatus(boolean b);

    List<Branch> findByCreatedByAndStatus(Long userId, boolean b);

    Branch findByIdAndStatus(long id, boolean b);

    List<Branch> findByOutletIdAndStatus(Long id, boolean b);


    @Procedure("default_ledgers")
    void createDefaultLedgers(Long branchId, Long outletId, Long createdBy);

    @Procedure("default_fees_ledger")
    void createDefaultFeesLedgers(Long id, Long id1, Long createdBy);

    @Query(value = "SELECT * FROM `branch_tbl` WHERE branch_name LIKE '%hostel%' ", nativeQuery = true)
    Branch getBranchRecord();

    @Procedure("default_dp_salary_ledger")
    void createDefaultDpSalaryLedgers(Long id, Long id1, Long createdBy);

    @Procedure("default_concession_ledger")
    void createDefaultConcessionSalaryLedgers(Long id, Long id1, Long createdBy);

    @Query(value = "SELECT * FROM `branch_tbl` WHERE outlet_id=?1 AND status=?2 AND branch_name LIKE '%hostel%' ", nativeQuery = true)
    Branch getHostelBranchRecord(Long id, boolean b);
}
