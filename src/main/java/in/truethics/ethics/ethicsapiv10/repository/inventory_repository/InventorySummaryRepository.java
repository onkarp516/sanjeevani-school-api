package in.truethics.ethics.ethicsapiv10.repository.inventory_repository;

import in.truethics.ethics.ethicsapiv10.model.inventory.InventorySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventorySummaryRepository extends JpaRepository<InventorySummary, Long> {
    @Query(
            value = " SELECT * FROM inventory_summary_tbl WHERE product_id=?1 ORDER BY ID DESC LIMIT 1 ", nativeQuery = true
    )
    InventorySummary findLastRecord(Long id);

    InventorySummary findByProductId(Long id);

    @Query(
            value = " SELECT IFNULL(closing_stock,0) FROM inventory_summary_tbl WHERE product_id=?1 AND outlet_id=?2", nativeQuery = true
    )
    Double findClosingStocks(Long product_id, Long userId);

    List<InventorySummary> findByOutletIdOrderByProductId(Long id);
}
