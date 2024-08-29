package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.FeesDetails;
import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionDetail;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface FeesDetailsRepository extends JpaRepository<FeesDetails, Long> {
    @Query(value = "SELECT IFNULL(SUM(amount_for_boy),0), IFNULL(SUM(amount_for_girl),0), IFNULL(SUM(amount),0) FROM `fees_details_tbl` " +
            "WHERE fees_master_id=?1 AND status=?2 AND sub_fee_head_id IS NULL", nativeQuery = true)
    String getTotalFeesByFeesMasterIdAndStatus(Long id, boolean b);

    List<FeesDetails> findByFeesMasterIdAndStatus(Long id, boolean b);

    List<FeesDetails> findByFeesMasterIdAndStatusAndSubFeeHeadNull(Long id, boolean b);

    List<FeesDetails> findByFeeHeadIdAndFeesMasterIdAndStatusAndSubFeeHeadIsNotNull(Long id, Long id1, boolean b);

    FeesDetails findByFeesMasterIdAndSubFeeHeadId(Long id, Long id1);

    FeesDetails findByFeesMasterIdAndFeeHeadId(Long id, Long id1);

    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `fees_details_tbl` WHERE fees_master_id=?1", nativeQuery = true)
    void deleteFeesDetails(Long id);

    @Query(value = "SELECT fee_head_tbl.fee_head_name, fees_details_tbl.* FROM `fees_details_tbl` LEFT JOIN fee_head_tbl ON" +
            " fees_details_tbl.fee_head_id=fee_head_tbl.id WHERE fees_master_id=?1 AND fee_head_tbl.fee_head_name LIKE '%hostel%'", nativeQuery = true)
    String checkHostelFeeHeadData(Long id);
    @Query(value = "SELECT IFNULL(SUM(amount),0), IFNULL(SUM(amount_for_boy),0), IFNULL(SUM(amount_for_girl),0) FROM `fees_details_tbl`" +
            " LEFT JOIN fee_head_tbl ON fees_details_tbl.fee_head_id=fee_head_tbl.id WHERE fee_head_tbl.is_receipt_current_branch=?2" +
            " AND fees_master_id=?1 AND fees_details_tbl.status=?3", nativeQuery = true)
    String getHostelHeadAmountByFeesMaster(Long id, boolean b, boolean b1);

    @Query(value = "SELECT * FROM `fees_details_tbl` LEFT JOIN fee_head_tbl ON fees_details_tbl.fee_head_id=fee_head_tbl.id" +
            " WHERE fee_head_tbl.is_receipt_current_branch=?2 AND fees_master_id=?1 AND fees_details_tbl.status=?3 AND sub_fee_head_id  IS NULL", nativeQuery = true)
    List<FeesDetails> findByFeesMasterIdAndStatusAndHostelsOnly(Long id, boolean b, boolean b1);

    List<FeesDetails> findByFeesMasterIdAndFeeHeadIdAndStatusAndSubFeeHeadNull(Long id, Long id1, boolean b);
}
