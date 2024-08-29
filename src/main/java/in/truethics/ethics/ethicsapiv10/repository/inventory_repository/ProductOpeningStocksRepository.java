package in.truethics.ethics.ethicsapiv10.repository.inventory_repository;

import in.truethics.ethics.ethicsapiv10.model.inventory.ProductOpeningStocks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOpeningStocksRepository extends JpaRepository<ProductOpeningStocks, Long> {
}
