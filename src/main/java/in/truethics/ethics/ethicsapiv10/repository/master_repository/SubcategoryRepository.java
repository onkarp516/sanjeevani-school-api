package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.master.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    List<Subcategory> findByCategoryIdAndStatus(Long categoryId, boolean b);

    List<Subcategory> findByOutletIdAndStatus(Long outletId, boolean b);

    Subcategory findByIdAndStatus(long id, boolean b);
}
