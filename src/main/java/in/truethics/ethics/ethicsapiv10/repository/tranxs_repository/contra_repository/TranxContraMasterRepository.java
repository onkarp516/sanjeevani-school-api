package in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.contra_repository;

import in.truethics.ethics.ethicsapiv10.model.tranx.contra.TranxContraMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TranxContraMasterRepository extends JpaRepository<TranxContraMaster, Long> {
    @Query(
            value = "select COUNT(*) from tranx_contra_master_tbl WHERE outlet_id=?1 AND status=1 ", nativeQuery = true
    )
    Long findLastRecord(Long id);

    List<TranxContraMaster> findByOutletIdAndStatusOrderByIdDesc(Long id, boolean b);

    TranxContraMaster findByIdAndStatus(Long transactionId, boolean b);
}
