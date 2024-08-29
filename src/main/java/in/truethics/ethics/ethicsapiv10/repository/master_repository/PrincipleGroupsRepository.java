package in.truethics.ethics.ethicsapiv10.repository.master_repository;


import in.truethics.ethics.ethicsapiv10.model.master.PrincipleGroups;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrincipleGroupsRepository extends JpaRepository<PrincipleGroups, Long> {
    PrincipleGroups findByIdAndStatus(long principle_group_id, boolean b);

    PrincipleGroups findByGroupNameIgnoreCase(String s);
}
