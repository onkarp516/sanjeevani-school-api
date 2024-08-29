package in.truethics.ethics.ethicsapiv10.repository.inventory_repository;

import in.truethics.ethics.ethicsapiv10.model.inventory.InventorySerialNumberSummaryDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventorySerialNumberSummaryDetailsRepository extends
        JpaRepository<InventorySerialNumberSummaryDetails, Long> {
}
