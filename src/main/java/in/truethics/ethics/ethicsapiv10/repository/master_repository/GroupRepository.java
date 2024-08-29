package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.master.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByOutletIdAndStatus(Long outletId, boolean b);

    Group findByIdAndStatus(long id, boolean b);
}
