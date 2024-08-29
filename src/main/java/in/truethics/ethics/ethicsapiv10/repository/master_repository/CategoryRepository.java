package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.master.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findBySubgroupIdAndStatus(Long subgroupId, boolean b);

    Category findByIdAndStatus(long id, boolean b);

    List<Category> findByOutletIdAndStatus(Long outletId, boolean b);
}
