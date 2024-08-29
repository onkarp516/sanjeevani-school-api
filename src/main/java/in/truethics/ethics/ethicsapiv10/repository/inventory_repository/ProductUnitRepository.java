package in.truethics.ethics.ethicsapiv10.repository.inventory_repository;


import in.truethics.ethics.ethicsapiv10.model.inventory.ProductUnitPacking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductUnitRepository extends JpaRepository<ProductUnitPacking, Long> {
    List<ProductUnitPacking> findByProductId(Long id);

    ProductUnitPacking findByIdAndStatus(long unitDetailId, boolean b);

    List<ProductUnitPacking> findByProductIdAndStatus(Long id, boolean b);

    List<ProductUnitPacking> findByProductIdAndPackingMasterId(long product_id, Long id);

    @Query(
            value = "select distinct packing_master_id from `product_unit_packing_tbl` where product_id=?1", nativeQuery = true
    )
    List<Long> findProductIdDistinct(long product_id);

    ProductUnitPacking findByPackingMasterId(Long mPack);
}
