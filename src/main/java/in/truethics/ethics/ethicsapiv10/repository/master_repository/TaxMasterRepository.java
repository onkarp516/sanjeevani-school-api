package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.master.TaxMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxMasterRepository extends JpaRepository<TaxMaster, Long> {

    List<TaxMaster> findByOutletIdAndStatus(Long id, boolean b);

    TaxMaster findByIdAndStatus(long id, boolean b);
}
