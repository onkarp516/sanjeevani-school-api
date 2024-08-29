package in.truethics.ethics.ethicsapiv10.repository.student_tranx;

import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeesTransactionSummaryRepository extends JpaRepository<FeesTransactionSummary, Long> {
    FeesTransactionSummary findByStudentRegisterIdAndAcademicYearIdAndStandardIdAndDivisionIdAndStatus(Long studentId, Long academicYearId, Long standardId, Long divisionId, boolean b);

    List<FeesTransactionSummary> findByOutletIdAndStatus(Long outletId, boolean b);

    FeesTransactionSummary findByFeesMasterIdAndStudentRegisterId(Long id, Long studentId);

    FeesTransactionSummary findByFeesMasterIdAndStudentRegisterIdAndOutletId(Long feesMasterId, Long studentInfoId, Long id);

    FeesTransactionSummary findByFeesMasterIdAndStudentRegisterIdAndAcademicYearId(Long id, Long id1, Long id2);

    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndStatus(Long outletId, Long id, boolean b);

    FeesTransactionSummary findByIdAndStatus(Long transactionId, boolean b);

    @Query(value = "SELECT receipt_no FROM `fees_transaction_detail_tbl` WHERE fees_transaction_summary_id=?1 GROUP BY " +
            "receipt_no ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findTransactionIdbyReceipt(Long transactionId);


//    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndDivisionId(Long id, Long branchId, Long academicYearId, Long standardId, Long divisionId);


    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndStandardIdAndStudentTypeAndStatus(Long id, Long branchId, Long standardId, Integer studentType, boolean b);

    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndStudentTypeAndStatus(Long id, Long branchId, Long academicYearId, Long standardId, Integer studentType, boolean b);

    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndStatus(Long id, Long branchId, Long academicYearId, Long standardId, boolean b);

    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndAcademicYearIdAndStudentTypeAndStatus(Long id, Long branchId, Long academicYearId, Integer studentType, boolean b);

    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndAcademicYearIdAndStatus(Long id, Long branchId, Long academicYearId, boolean b);

    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndStandardIdAndStatus(Long id, Long branchId, Long standardId, boolean b);

    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndStudentTypeAndStatus(Long id, Long branchId, Integer studentType, boolean b);

    FeesTransactionSummary findByStudentRegisterId(Long id);


    @Query(value = "SELECT * FROM `fees_transaction_summary_tbl` LEFT JOIN student_register_tbl ON " +
            " fees_transaction_summary_tbl.student_id = student_register_tbl.id where fees_transaction_summary_tbl.outlet_id=?1" +
            " AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.academic_year_id=?3 and " +
            "fees_transaction_summary_tbl.standard_id=?4 AND fees_transaction_summary_tbl.division_id=?5 and fees_transaction_summary_tbl.student_type=?6 ORDER BY TRIM(student_register_tbl.last_name)", nativeQuery = true)
    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndDivisionIdForOutstanding(Long id, Long branchId, Long academicYearId, Long standardId, Long divisionId, Long studentType);


    @Query(value = " SELECT fees_transaction_summary_tbl.*, student_register_tbl.first_name,student_register_tbl.middle_name," +
            " student_register_tbl.last_name FROM `fees_transaction_summary_tbl` LEFT JOIN student_register_tbl ON " +
            " fees_transaction_summary_tbl.student_id = student_register_tbl.id where fees_transaction_summary_tbl.outlet_id=?1 " +
            " AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.academic_year_id=?3 and " +
            " fees_transaction_summary_tbl.standard_id=?4 AND fees_transaction_summary_tbl.student_type=?5 AND" +
            " student_register_tbl.status=?6 ORDER BY TRIM(student_register_tbl.last_name)", nativeQuery = true)
    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndStudentTypeAndStatusRawQuery(Long id, Long branchId, Long academicYearId, Long standardId, Integer studentType, boolean b);


    @Query
            (value = "SELECT fees_transaction_summary_tbl.*, student_register_tbl.first_name,student_register_tbl.middle_name," +
                    "  student_register_tbl.last_name FROM `fees_transaction_summary_tbl` LEFT JOIN student_register_tbl ON " +
                    "  fees_transaction_summary_tbl.student_id = student_register_tbl.id where fees_transaction_summary_tbl.outlet_id=?1 " +
                    "  AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.academic_year_id=?3 and " +
                    "  fees_transaction_summary_tbl.standard_id=?4 AND student_register_tbl.status=?5 ORDER BY TRIM(student_register_tbl.last_name) ", nativeQuery = true)
    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndAcademicYearIdAndStandardIdAndStatusRawQuery(Long id, Long branchId, Long academicYearId, Long standardId, boolean b);


    @Query
            (value = "SELECT fees_transaction_summary_tbl.*, student_register_tbl.first_name,student_register_tbl.middle_name," +
                    "  student_register_tbl.last_name FROM `fees_transaction_summary_tbl` LEFT JOIN student_register_tbl ON " +
                    "  fees_transaction_summary_tbl.student_id = student_register_tbl.id where fees_transaction_summary_tbl.outlet_id=?1 " +
                    "  AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.academic_year_id=?3 " +
                    "  AND fees_transaction_summary_tbl.student_type=?4 AND student_register_tbl.status=?5 ORDER BY TRIM(student_register_tbl.last_name) ", nativeQuery = true)
    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndAcademicYearIdAndStudentTypeAndStatusRawQuery(Long id, Long branchId, Long academicYearId, Integer studentType, boolean b);

    @Query
            (value = "SELECT fees_transaction_summary_tbl.*, student_register_tbl.first_name,student_register_tbl.middle_name," +
                    "  student_register_tbl.last_name FROM `fees_transaction_summary_tbl` LEFT JOIN student_register_tbl ON " +
                    "  fees_transaction_summary_tbl.student_id = student_register_tbl.id where fees_transaction_summary_tbl.outlet_id=?1 " +
                    "  AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.standard_id=?3" +
                    "  AND fees_transaction_summary_tbl.student_type=?4 AND student_register_tbl.status=?5 ORDER BY TRIM(student_register_tbl.last_name) ", nativeQuery = true)
    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndStandardIdAndStudentTypeAndStatusRawQuery(Long id, Long branchId, Long standardId, Integer studentType, boolean b);

    @Query
            (value = "SELECT fees_transaction_summary_tbl.*, student_register_tbl.first_name,student_register_tbl.middle_name," +
                    "  student_register_tbl.last_name FROM `fees_transaction_summary_tbl` LEFT JOIN student_register_tbl ON " +
                    "  fees_transaction_summary_tbl.student_id = student_register_tbl.id where fees_transaction_summary_tbl.outlet_id=?1 " +
                    "  AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.academic_year_id=?3 " +
                    "  AND student_register_tbl.status=?4 ORDER BY TRIM(student_register_tbl.last_name) ", nativeQuery = true)
    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndAcademicYearIdAndStatusRawQuery(Long id, Long branchId, Long academicYearId, boolean b);


    @Query
            (value = "SELECT fees_transaction_summary_tbl.*, student_register_tbl.first_name,student_register_tbl.middle_name," +
                    "  student_register_tbl.last_name FROM `fees_transaction_summary_tbl` LEFT JOIN student_register_tbl ON " +
                    "  fees_transaction_summary_tbl.student_id = student_register_tbl.id where fees_transaction_summary_tbl.outlet_id=?1 " +
                    "  AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.standard_id=?3 " +
                    "  AND student_register_tbl.status=?4 ORDER BY TRIM(student_register_tbl.last_name) ", nativeQuery = true)
    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndStandardIdAndStatusRawQuery(Long id, Long branchId, Long standardId, boolean b);


    @Query
            (value = "SELECT fees_transaction_summary_tbl.*, student_register_tbl.first_name,student_register_tbl.middle_name," +
                    "  student_register_tbl.last_name FROM `fees_transaction_summary_tbl` LEFT JOIN student_register_tbl ON " +
                    "  fees_transaction_summary_tbl.student_id = student_register_tbl.id where fees_transaction_summary_tbl.outlet_id=?1 " +
                    "  AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.student_type=?3 " +
                    "  AND student_register_tbl.status=?4 ORDER BY TRIM(student_register_tbl.last_name) ", nativeQuery = true)
    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndStudentTypeAndStatusRawQuery(Long id, Long branchId, Integer studentType, boolean b);


    @Query
            (value = "SELECT fees_transaction_summary_tbl.*, student_register_tbl.first_name,student_register_tbl.middle_name," +
                    "  student_register_tbl.last_name FROM `fees_transaction_summary_tbl` LEFT JOIN student_register_tbl ON " +
                    "  fees_transaction_summary_tbl.student_id = student_register_tbl.id where fees_transaction_summary_tbl.outlet_id=?1 " +
                    "  AND fees_transaction_summary_tbl.branch_id=?2  " +
                    "  AND student_register_tbl.status=?3 ORDER BY TRIM(student_register_tbl.last_name) ", nativeQuery = true)
    List<FeesTransactionSummary> findByOutletIdAndBranchIdAndStatusRawQuery(Long id, Long branchId, boolean b);

    FeesTransactionSummary findTop1ByStudentRegisterIdAndStatusOrderByIdDesc(Long studentId, boolean b);

    FeesTransactionSummary findByStudentRegisterIdAndStatus(Long studentId, boolean b);
}
