package in.truethics.ethics.ethicsapiv10.repository.inventory_repository;

import in.truethics.ethics.ethicsapiv10.model.inventory.InventorySerialNumberSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventorySerialNumberSummaryRepository extends
        JpaRepository<InventorySerialNumberSummary, Long> {
    InventorySerialNumberSummary findByProductIdAndSerialNo(Long id, String srno);

    InventorySerialNumberSummary findBySerialNoAndTranxAction(String srno, String in);
}
