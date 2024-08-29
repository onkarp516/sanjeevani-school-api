package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.FeeHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeHeadRepository extends JpaRepository<FeeHead, Long> {
    List<FeeHead> findByOutletIdAndStatus(Long outletId, boolean b);

    FeeHead findByIdAndStatus(long id, boolean b);

    List<FeeHead> findByBranchIdAndStatus(Long branchId, boolean b);

    List<FeeHead> findByBranchIdAndStudentTypeAndStatus(Long branchId, Integer studentType, boolean b);

    List<FeeHead> findByOutletIdAndBranchIdAndStatus(Long outletId, Long id, boolean b);

    FeeHead findByLedgerIdAndStatus(Long ledgerId, boolean b);

    FeeHead findByFeeHeadNameAndStatus(String bus, boolean b);

//    FeeHead findByLedgerMasterIdAndUnderBranchIdAndStatus(Long ledgerId, Long id, boolean b);
}
