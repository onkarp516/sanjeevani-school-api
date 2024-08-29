package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.master.AssociateGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssociateGroupsRepository extends JpaRepository<AssociateGroups, Long> {


    AssociateGroups findByIdAndStatus(long associates_id, boolean b);

    @Query(
            value = " SELECT associates_name FROM `associates_groups_tbl` WHERE id=?1 ",
            nativeQuery = true
    )
    String findName(Long associateId);

    List<AssociateGroups> findByOutletId(Long id);

    List<AssociateGroups> findByOutletIdOrderByIdDesc(Long id);

    List<AssociateGroups> findByOutletIdAndBranchIdOrderByIdDesc(Long id, Long id1);

    AssociateGroups findByAssociatesNameIgnoreCaseAndOutletIdAndBranchIdAndStatus(String fees_account, Long id, Long id1, boolean b);
}
