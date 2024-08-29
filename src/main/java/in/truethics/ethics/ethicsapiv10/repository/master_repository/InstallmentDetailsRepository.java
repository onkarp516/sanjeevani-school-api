package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.InstallmentDetails;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface InstallmentDetailsRepository extends JpaRepository<InstallmentDetails, Long> {
    List<InstallmentDetails> findByInstallmentMasterId(Long id);

    List<InstallmentDetails> findByInstallmentMasterIdAndStatus(Long id, boolean b);

    InstallmentDetails findByInstallmentMasterIdAndSubFeeHeadId(Long installno, long payHeadId);

    InstallmentDetails findByInstallmentMasterIdAndFeeHeadId(Long installno, long payHeadId);

    @Modifying
    @Transactional
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `installment_details_tbl` WHERE installment_master_id=?1", nativeQuery = true)
    void deleteDetailRecords(Long id);

}
