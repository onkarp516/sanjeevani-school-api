package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.master.Subgroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubgroupRepository extends JpaRepository<Subgroup, Long> {
    List<Subgroup> findByGroupIdAndStatus(Long groupId, boolean b);

    List<Subgroup> findByOutletIdAndStatus(Long outletId, boolean b);

    Subgroup findByIdAndStatus(long id, boolean b);
}
