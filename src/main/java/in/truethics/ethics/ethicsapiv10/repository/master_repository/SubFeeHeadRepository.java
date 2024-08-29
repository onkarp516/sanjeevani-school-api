package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.SubFeeHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubFeeHeadRepository extends JpaRepository<SubFeeHead, Long> {
    List<SubFeeHead> findByOutletIdAndStatus(Long outletId, boolean b);

    SubFeeHead findByIdAndStatus(long id, boolean b);

    List<SubFeeHead> findByFeeHeadIdAndStatus(Long feeHeadId, boolean b);

    List<SubFeeHead> findByOutletIdAndBranchIdAndStatus(Long outletId, Long id, boolean b);
}
