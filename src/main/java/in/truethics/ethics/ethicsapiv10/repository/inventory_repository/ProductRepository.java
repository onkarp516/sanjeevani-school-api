package in.truethics.ethics.ethicsapiv10.repository.inventory_repository;


import in.truethics.ethics.ethicsapiv10.model.inventory.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByIdAndStatus(long product_id, boolean b);

    List<Product> findByOutletIdAndStatus(Long id, boolean b);

    List<Product> findByOutletId(Long id);
}
