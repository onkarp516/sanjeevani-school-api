package in.truethics.ethics.ethicsapiv10.repository.master_repository;


import in.truethics.ethics.ethicsapiv10.model.inventory.ProductHsn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductHsnRepository extends JpaRepository<ProductHsn, Long> {
    ProductHsn findByIdAndStatus(long hsnId, boolean b);

    List<ProductHsn> findByOutletIdAndStatus(Long id, boolean b);
}
