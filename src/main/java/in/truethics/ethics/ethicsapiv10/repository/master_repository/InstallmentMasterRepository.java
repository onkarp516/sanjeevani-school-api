package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.InstallmentMaster;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface InstallmentMasterRepository extends JpaRepository<InstallmentMaster, Long> {
    List<InstallmentMaster> findByOutletIdAndStatus(Long outletId, boolean b);


    @Query(
            value = "SELECT DISTINCT(installment_no) FROM `installment_master_tbl` WHERE fees_master_id=?1", nativeQuery = true
    )
    List<Object[]> findInstallmentsByFeesMasterId(Long id);

    @Query(
            value = "SELECT DISTINCT(concession_type) FROM `installment_master_tbl` WHERE fees_master_id=?1", nativeQuery = true
    )
    List<Object[]> findConcessionByFeesMasterId(Long id);

    @Query(
            value = "SELECT DISTINCT(concession_type) FROM `installment_master_tbl` WHERE fees_master_id=?2 AND" +
                    " installment_no=?1", nativeQuery = true
    )
    List<Object[]> findConcessionByInstallment(Integer installmentNo, Long feesMasterId);

    InstallmentMaster findByConcessionTypeAndInstallmentNo(Integer concessionType, Integer installmentNo);

    InstallmentMaster findByFeesMasterIdAndConcessionTypeAndInstallmentNo(Long feesMasterId, Integer concessionType, Integer installmentNo);

    @Query(value = "SELECT IFNULL(SUM(amount),0), IFNULL(SUM(boys_amount),0), IFNULL(SUM(girls_amount),0) FROM" +
            " installment_details_tbl WHERE installment_master_id=?1", nativeQuery = true)
    String getTotalInstallmentAmount(Long id);


    @Query(value = "SELECT id FROM `installment_master_tbl` WHERE fees_master_id=?1", nativeQuery = true)
    List<Object[]> getIdsByFeesMaster(Long feesMasterId);

    @Query(value = "SELECT IFNULL(SUM(amount),0), IFNULL(SUM(boys_amount),0), IFNULL(SUM(girls_amount),0) FROM" +
            " `installment_details_tbl` WHERE installment_master_id IN (?1) AND fee_head_id=?2", nativeQuery = true)
    String getTotalOfHead(String toString, int payHeadId);


    @Query(value = "SELECT IFNULL(SUM(amount),0), IFNULL(SUM(boys_amount),0), IFNULL(SUM(girls_amount),0) FROM" +
            " `installment_details_tbl` WHERE installment_master_id IN (?1) AND sub_fee_head_id=?2", nativeQuery = true)
    String getTotalOfSubHead(String toString, int payHeadId);

    List<InstallmentMaster> findByFeesMasterIdAndOutletId(Long feesMasterId, Long outlet);

    InstallmentMaster findTop1ByFeesAmountGreaterThanEqualOrderByIdDesc(Double balance);

    List<InstallmentMaster> findByConcessionTypeAndFeesMasterIdAndOutletId(Integer concessionType, Long feesMasterId, Long id);

    InstallmentMaster findTop1ByFeesMasterIdAndConcessionTypeAndFeesAmountGreaterThanEqualOrderByIdDesc(Long feesMasterId, Integer concessionType, Double balance);

    InstallmentMaster findByIdAndStatus(Long installmentId, boolean b);

    List<InstallmentMaster> findByConcessionTypeAndFeesMasterIdAndOutletIdOrderByInstallmentNoAsc(Integer concessionType, Long feesMasterId, Long id);

    List<InstallmentMaster> findByOutletIdAndBranchIdAndStatus(Long outletId, Long id, boolean b);

    @Query(value = "SELECT installment_master_tbl.* FROM installment_master_tbl LEFT JOIN fees_master_tbl ON" +
            " installment_master_tbl.fees_master_id=fees_master_tbl.id WHERE installment_master_tbl.outlet_id=?1 AND" +
            " installment_master_tbl.branch_id=?2 AND installment_master_tbl.status=?3 GROUP BY fees_master_id, concession_type", nativeQuery = true)
    List<InstallmentMaster> getListByOutletIdAndBranchIdAndStatus(Long outletId, Long id, boolean b);

    @Query(value = "SELECT installment_master_tbl.* FROM installment_master_tbl LEFT JOIN fees_master_tbl ON" +
            " installment_master_tbl.fees_master_id=fees_master_tbl.id WHERE installment_master_tbl.outlet_id=?1 AND" +
            " installment_master_tbl.status=?2 GROUP BY fees_master_id, concession_type", nativeQuery = true)
    List<InstallmentMaster> getListByOutletIdAndStatus(Long outletId, boolean b);

    @Query(value = "SELECT installment_master_tbl.* FROM installment_master_tbl WHERE fees_master_id=?1 AND " +
            "concession_type=?2 ORDER BY id DESC LIMIT 1", nativeQuery = true)
    InstallmentMaster getLastRecordOfInstallment(Long feesMasterId, int concessionType);

    List<InstallmentMaster> findByFeesMasterIdAndConcessionType(Long feesMasterId, int concessionType);


    @Query(value = "SELECT installment_master_tbl.* FROM installment_master_tbl WHERE fees_master_id=?1 AND " +
            "installment_no=?2", nativeQuery = true)
    List<InstallmentMaster> getListForConcessionCheck(Long feesMasterId, int i);

    @Query(value = "SELECT installment_master_tbl.* FROM installment_master_tbl LEFT JOIN fees_master_tbl ON" +
            " installment_master_tbl.fees_master_id=fees_master_tbl.id WHERE installment_master_tbl.outlet_id=?1 AND" +
            " installment_master_tbl.branch_id=?2 AND academic_year_id=?3 AND standard_id=?4 AND installment_master_tbl.status=?5 GROUP BY fees_master_id, concession_type", nativeQuery = true)
    List<InstallmentMaster> findInstallmentMasterByAcademicYearAndStandardAndStatus(Long id, Long branchId, Long academicYearId, Long standardId, boolean b);

    @Query(value = "SELECT installment_master_tbl.* FROM installment_master_tbl LEFT JOIN fees_master_tbl ON" +
            " installment_master_tbl.fees_master_id=fees_master_tbl.id WHERE installment_master_tbl.outlet_id=?1 AND" +
            " installment_master_tbl.branch_id=?2 AND academic_year_id=?3 AND installment_master_tbl.status=?4 GROUP BY fees_master_id, concession_type", nativeQuery = true)
    List<InstallmentMaster> findInstallmentMasterByAcademicYearStatus(Long id, Long branchId, Long academicYearId, boolean b);

    @Query(value = "SELECT installment_master_tbl.* FROM installment_master_tbl LEFT JOIN fees_master_tbl ON" +
            " installment_master_tbl.fees_master_id=fees_master_tbl.id WHERE installment_master_tbl.outlet_id=?1 AND" +
            " installment_master_tbl.branch_id=?2 AND standard_id=?3 AND installment_master_tbl.status=?4 GROUP BY fees_master_id, concession_type", nativeQuery = true)
    List<InstallmentMaster> findInstallmentMasterByStandardStatus(Long id, Long branchId, Long standardId, boolean b);


    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `installment_master_tbl` WHERE id=?1", nativeQuery = true)
    void deleteMasterRecord(Long id);

    InstallmentMaster findByFeesMasterIdAndConcessionTypeAndInstallmentNoAndStudentGroup(Long feesMasterId, int concessionType, int installno, Integer studentGroup);

    @Query(value = "SELECT SUM(fees_amount), SUM(boys_amount), SUM(girls_amount) FROM `installment_master_tbl` WHERE fees_master_id=?1 " +
            " AND concession_type=?2 AND installment_no=?3", nativeQuery = true)
    String getTotalInstallmentAmountByInstallmentNoAndConcessionNo(Long feesMasterId, Integer concessionType, Integer installmentNo);
}
