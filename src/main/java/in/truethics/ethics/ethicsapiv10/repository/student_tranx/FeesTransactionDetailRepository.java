package in.truethics.ethics.ethicsapiv10.repository.student_tranx;

import in.truethics.ethics.ethicsapiv10.model.school_tranx.FeesTransactionDetail;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

public interface FeesTransactionDetailRepository extends JpaRepository<FeesTransactionDetail, Long> {

    FeesTransactionDetail findTop1ByFeesTransactionSummaryIdOrderByIdDesc(long transactionId);

    FeesTransactionDetail findTop1ByFeesTransactionSummaryIdAndFeeHeadIdAndInstallmentNoOrderByIdDesc(Long id, Long id1, int installmentNo);

    FeesTransactionDetail findTop1ByFeesTransactionSummaryIdAndSubFeeHeadIdAndInstallmentNoOrderByIdDesc(Long id, Long id1, int installmentNo);


    List<FeesTransactionDetail> findByFeesTransactionSummaryIdAndReceiptNo(Long transactionId, String lastReceiptNo);

    @Query(value = "SELECT IFNULL(SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id," +
            "   receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name,  " +
            "  student_register_tbl.last_name, fees_transaction_summary_tbl.id  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl " +
            "  ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN  " +
            "  student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id  " +
            "  WHERE fees_transaction_summary_tbl.outlet_id=?1 " +
            "  AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.student_type=?5 AND fees_transaction_detail_tbl.transaction_date BETWEEN ?3 " +
            " AND ?4 GROUP BY receipt_no ORDER BY student_register_tbl.last_name ASC", nativeQuery = true)
    List<Object[]> findByTransactionDate(Long id, Long id1, LocalDate fromDate, LocalDate toDate, String studentType);


    @Query(value = "SELECT IFNULL(SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id," +
            "   receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name,  " +
            "  student_register_tbl.last_name, fees_transaction_summary_tbl.id, fees_transaction_summary_tbl.concession_amount  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl " +
            "  ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN  " +
            "  student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id  " +
            "  WHERE fees_transaction_summary_tbl.outlet_id=?1 " +
            "  AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_detail_tbl.transaction_date=?3" +
            " AND fees_transaction_summary_tbl.student_type=?4 GROUP BY receipt_no ORDER BY student_register_tbl.last_name ASC", nativeQuery = true)
    List<Object[]> findByTransactionDateNow(Long id, Long id1, LocalDate currentDate, String studentType);

    /*Taken Backup for runtime issue resovled*/
/*
    @Query
            (value = "SELECT IFNULL(SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id," +
                    "  receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name, " +
                    " student_register_tbl.last_name, fees_transaction_summary_tbl.id, fees_transaction_summary_tbl.concession_amount  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl" +
                    " ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN " +
                    " student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id " +
                    " WHERE fees_transaction_summary_tbl.outlet_id=?1" +
                    " AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.academic_year_id=?3 " +
                    " AND fees_transaction_summary_tbl.standard_id=?4 " +
                    " AND fees_transaction_summary_tbl.student_type=?7 AND fees_transaction_detail_tbl.transaction_date BETWEEN ?5 AND ?6 GROUP BY receipt_no " +
                    "ORDER BY student_register_tbl.last_name ASC", nativeQuery = true)
    List<Object[]> findByTransactionDateWithYearAndStandard(Long id, Long id1, String academicyear, String standard, String fromDate, String toDate, String studentType);
*/

    @Query
            (value = "SELECT IFNULL(SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id," +
                    "  receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name, " +
                    " student_register_tbl.last_name, fees_transaction_summary_tbl.id, IFNULL(fees_transaction_summary_tbl.concession_amount, 0)  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl" +
                    " ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN " +
                    " student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id " +
                    " WHERE fees_transaction_summary_tbl.outlet_id=?1" +
                    " AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.academic_year_id=?3 " +
                    " AND fees_transaction_summary_tbl.standard_id=?4 " +
                    " AND fees_transaction_summary_tbl.student_type=?7 AND fees_transaction_detail_tbl.transaction_date BETWEEN ?5 AND ?6 GROUP BY receipt_no ORDER BY fees_transaction_detail_tbl.created_at", nativeQuery = true)
//                    "ORDER BY student_register_tbl.last_name ASC, student_register_tbl.first_name ASC, fees_transaction_detail_tbl.transaction_date ASC", nativeQuery = true)
    List<Object[]> findByTransactionDateWithYearAndStandard(Long id, Long id1, String academicyear, String standard, String fromDate, String toDate, String studentType);


    @Query(value = "SELECT IFNULL(SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id," +
            "   receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name,  " +
            "  student_register_tbl.last_name, fees_transaction_summary_tbl.id  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl " +
            "  ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN  " +
            "  student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id  " +
            "  WHERE fees_transaction_summary_tbl.outlet_id=?1 " +
            "  AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.academic_year_id=?3  " +
            "  AND fees_transaction_summary_tbl.student_type=?6 AND fees_transaction_detail_tbl.transaction_date BETWEEN ?4 AND ?5 GROUP BY receipt_no ORDER BY student_register_tbl.last_name ASC", nativeQuery = true)
    List<Object[]> findByTransactionDatewithAcademicYear(Long id, Long id1, String academicyear, String fromDate, String toDate, String studentType);


    @Query(value = "SELECT IFNULL(SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id," +
            "   receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name,  " +
            "  student_register_tbl.last_name, fees_transaction_summary_tbl.id  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl " +
            "  ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN  " +
            "  student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id  " +
            "  WHERE fees_transaction_summary_tbl.outlet_id=?1 " +
            "  AND fees_transaction_summary_tbl.branch_id=?2 AND  fees_transaction_summary_tbl.standard_id=?3 " +
            "  AND fees_transaction_summary_tbl.student_type=?6 AND fees_transaction_detail_tbl.transaction_date BETWEEN ?4 AND ?5 GROUP BY receipt_no ORDER BY student_register_tbl.last_name ASC", nativeQuery = true)
    List<Object[]> findByTransactionDateAndStandard(Long id, Long id1, String standard, String fromDate, String toDate, String studentType);


    @Query(value = "SELECT IFNULL(SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id," +
            "   receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name,  " +
            "  student_register_tbl.last_name, fees_transaction_summary_tbl.id  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl " +
            "  ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN  " +
            "  student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id  " +
            "  WHERE fees_transaction_summary_tbl.outlet_id=?1 " +
            "  AND fees_transaction_summary_tbl.branch_id=?2 AND  fees_transaction_summary_tbl.standard_id=?3 " +
            " AND fees_transaction_summary_tbl.student_type=?5 AND fees_transaction_detail_tbl.transaction_date=?4 GROUP BY receipt_no  ORDER BY student_register_tbl.last_name ASC", nativeQuery = true)
    List<Object[]> findByTransactionDateAndStandardNow(Long id, Long id1, String standard, LocalDate now, String studentType);

    @Query(value = "SELECT IFNULL(SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id," +
            "   receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name,  " +
            "  student_register_tbl.last_name, fees_transaction_summary_tbl.id  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl " +
            "  ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN  " +
            "  student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id  " +
            "  WHERE fees_transaction_summary_tbl.outlet_id=?1 " +
            "  AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.academic_year_id=?3 " +
            " AND fees_transaction_summary_tbl.student_type=?5 AND fees_transaction_detail_tbl.transaction_date=?4 GROUP BY receipt_no  ORDER BY student_register_tbl.last_name ASC", nativeQuery = true)
    List<Object[]> findByTransactionDatewithAcademicYearNow(Long id, Long id1, String academicyear, LocalDate now, String studentType);


    @Query
            (value = "SELECT IFNULL(SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id," +
                    "  receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name, " +
                    " student_register_tbl.last_name, fees_transaction_summary_tbl.id  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl" +
                    " ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN " +
                    " student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id " +
                    " WHERE fees_transaction_summary_tbl.outlet_id=?1" +
                    " AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.academic_year_id=?3 " +
                    " AND fees_transaction_summary_tbl.standard_id=?4 " +
                    " AND fees_transaction_summary_tbl.student_type=?6 AND fees_transaction_detail_tbl.transaction_date=?5 GROUP BY receipt_no  ORDER BY student_register_tbl.last_name ASC", nativeQuery = true)
    List<Object[]> findByTransactionDateWithYearAndStandardNow(Long id, Long id1, Long academicyear, Long standard, LocalDate now, String studentType);

    @Query(value = "SELECT id, head_fee, IFNULL(SUM(paid_amount),0), fee_head_id, IFNULL(sub_fee_head_id,0), receipt_no," +
            " payment_mode, IFNULL(payment_no, ''), transaction_date FROM `fees_transaction_detail_tbl` WHERE fees_transaction_summary_id=?1 AND" +
            " receipt_no=?2 GROUP BY fee_head_id, sub_fee_head_id ", nativeQuery = true)
    List<Object[]> getFeeHeadsList(Long transactionId, String lastReceiptNo);

    FeesTransactionDetail findTop1ByTransactionDateOrderByIdDesc(LocalDate trDate);

    @Modifying
    @Transactional
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `fees_transaction_detail_tbl` WHERE id=?1 ", nativeQuery = true)
    void DeleteColumnFeesTransaction(Long tId);

    List<FeesTransactionDetail> findByOutletIdAndBranchIdAndStatus(Long outletId, Long branchId, boolean b);

    @Query(value = "SELECT IFNULL(SUM(paid_amount), 0) FROM `fees_transaction_detail_tbl` LEFT JOIN fee_head_tbl ON" +
            " fees_transaction_detail_tbl.fee_head_id=fee_head_tbl.id WHERE receipt_no=?1 AND" +
            " fee_head_tbl.fee_head_name LIKE '%hostel%'", nativeQuery = true)
    String getHostelFee(String receiptNo);

    @Query(value = "SELECT IFNULL(SUM(paid_amount), 0) FROM `fees_transaction_detail_tbl` LEFT JOIN fee_head_tbl ON" +
            " fees_transaction_detail_tbl.fee_head_id=fee_head_tbl.id WHERE fees_transaction_summary_id=?1 AND" +
            " fee_head_tbl.fee_head_name LIKE '%hostel%' GROUP BY fees_transaction_summary_id", nativeQuery = true)
    String getHostelFeePaid(Long id);

    @Query(value = "SELECT receipt_no, transaction_date, SUM(paid_amount) FROM `fees_transaction_detail_tbl` WHERE" +
            " fees_transaction_summary_id=?1 GROUP BY receipt_no", nativeQuery = true)
    List<Object[]> getReceiptRowsByFeesTransactionSummaryId(Long id);

//    @Query(value = "SELECT  IFNULL(SUM(paid_amount),0), fee_head_id, fee_head_tbl.fee_head_name, IFNULL(sub_fee_head_id,0)" +
//            " FROM `fees_transaction_detail_tbl` LEFT JOIN fee_head_tbl ON fees_transaction_detail_tbl.fee_head_id=fee_head_tbl.id" +
//            " WHERE fees_transaction_summary_id=?1 GROUP BY fee_head_id", nativeQuery = true)
    @Query(value = "SELECT  IFNULL(SUM(paid_amount),0), fee_head_id, fee_head_tbl.fee_head_name, IFNULL(sub_fee_head_id,0)" +
            "  fee_head_tbl.is_receipt_current_branch FROM `fees_transaction_detail_tbl` LEFT JOIN fee_head_tbl ON fees_transaction_detail_tbl.fee_head_id=fee_head_tbl.id" +
            " WHERE fees_transaction_summary_id=?1 GROUP BY fee_head_id", nativeQuery = true)
    List<Object[]> getPaidHeadsListByTransactionId(Long transactionId);

    @Query(value = "SELECT IFNULL(SUM(paid_amount), 0) FROM `fees_transaction_detail_tbl` LEFT JOIN fee_head_tbl ON" +
            " fees_transaction_detail_tbl.fee_head_id=fee_head_tbl.id WHERE receipt_no=?1 AND fee_head_tbl.is_receipt_current_branch=?2", nativeQuery = true)
    String getPaidHostelFeeByReceiptNo(String receiptNo, boolean b);

    @Query(value = "SELECT IFNULL(SUM(paid_amount), 0) FROM `fees_transaction_detail_tbl` LEFT JOIN fee_head_tbl ON" +
            " fees_transaction_detail_tbl.fee_head_id=fee_head_tbl.id WHERE fees_transaction_summary_id=?1 AND" +
            " fee_head_tbl.is_receipt_current_branch=?2 GROUP BY fees_transaction_summary_id", nativeQuery = true)
    String getPaidHostelFeeHeadsByTransactionSummary(Long id, boolean b);

    @Query(value = "SELECT fees_transaction_detail_tbl.id, head_fee, IFNULL(SUM(paid_amount),0), fee_head_id, IFNULL(sub_fee_head_id,0)," +
            " receipt_no, payment_mode, IFNULL(payment_no, ''), transaction_date,fee_head_tbl.fee_head_name, fee_head_tbl.is_receipt_current_branch FROM" +
            " `fees_transaction_detail_tbl` LEFT JOIN fee_head_tbl ON fees_transaction_detail_tbl.fee_head_id=fee_head_tbl.id" +
            " WHERE fees_transaction_summary_id=?1 AND receipt_no=?2 GROUP BY fee_head_id, sub_fee_head_id", nativeQuery = true)
    List<Object[]> getFeeHeadsListByTransactionIdAndReceipt(Long transactionId, String lastReceiptNo);

    @Query(value = "SELECT IFNULL(SUM(paid_amount),0) FROM fees_transaction_detail_tbl", nativeQuery = true)
    Double CalculatePaidAmount();

    @Query(value = "SELECT IFNULL(SUM(paid_amount),0) FROM fees_transaction_detail_tbl where transaction_date=?1", nativeQuery = true)
    Double GetDailyCollection(LocalDate currentDate);

    @Query(value = "SELECT id, IFNULL(SUM(head_fee),0), IFNULL(SUM(paid_amount),0), fee_head_id, IFNULL(sub_fee_head_id,0), receipt_no," +
            " payment_mode, IFNULL(payment_no, ''), transaction_date FROM `fees_transaction_detail_tbl` WHERE fees_transaction_summary_id=?1 AND" +
            " receipt_no=?2 GROUP BY fee_head_id ", nativeQuery = true)
    List<Object[]> getFeeHeadsMasterList(Long transactionId, String lastReceiptNo);


    @Query(value = "SELECT IFNULL(SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id," +
                    "  receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name, " +
                    " student_register_tbl.last_name, fees_transaction_summary_tbl.id, IFNULL(fees_transaction_summary_tbl.concession_amount, 0)  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl" +
                    " ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN " +
                    " student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id " +
                    " WHERE fees_transaction_summary_tbl.outlet_id=?1" +
                    " AND fees_transaction_summary_tbl.branch_id=?2 GROUP BY receipt_no " +
                    " ORDER BY student_register_tbl.last_name ASC, student_register_tbl.first_name ASC, fees_transaction_detail_tbl.transaction_date ASC", nativeQuery = true)
    List<Object[]> findFeesTransactionsByOutletAndBranch(Long id, Long id1);

    @Query(value="SELECT IFNULL(SUM(paid_amount),0) FROM `fees_transaction_detail_tbl` WHERE transaction_date=?1",nativeQuery = true)
    String getCollectionByWeek(String days);

    FeesTransactionDetail findByFeesTransactionSummaryIdAndTransactionDate(long feestranxsummaryId, LocalDate transactionDate);



    @Query
            (value = "SELECT IFNULL(SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id," +
                    "  receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name, " +
                    " student_register_tbl.last_name, fees_transaction_summary_tbl.id, IFNULL(fees_transaction_summary_tbl.concession_amount, 0)  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl" +
                    " ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN " +
                    " student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id " +
                    " WHERE fees_transaction_summary_tbl.outlet_id=?1 AND fees_transaction_summary_tbl.branch_id=?2 AND " +
                    " fees_transaction_summary_tbl.paid_amount>0 GROUP BY receipt_no"+
                    " ORDER BY student_register_tbl.last_name ASC, student_register_tbl.first_name ASC, fees_transaction_detail_tbl.transaction_date ASC", nativeQuery = true)
                  List<Object[]> findByTransactionDateWithYearAndStandards(Long id, Long id1);



    @Query(value = "SELECT head_fee, IFNULL(SUM(fees_transaction_detail_tbl.paid_amount),0), receipt_no, transaction_date," +
            " student_id, fee_head_tbl.fee_head_name, IFNULL(sub_fee_head_tbl.sub_fee_head_name, ''), IFNULL(sub_fee_head_id,0)  FROM `fees_transaction_detail_tbl`" +
            " LEFT JOIN fees_transaction_summary_tbl ON fees_transaction_detail_tbl.fees_transaction_summary_id=fees_transaction_summary_tbl.id" +

            " LEFT JOIN fee_head_tbl ON fees_transaction_detail_tbl.fee_head_id=fee_head_tbl.id LEFT JOIN sub_fee_head_tbl ON" +
            " fees_transaction_detail_tbl.sub_fee_head_id=sub_fee_head_tbl.id WHERE receipt_no=?1 AND is_receipt_current_branch=?2 "+
            " GROUP BY fees_transaction_detail_tbl.fee_head_id, fees_transaction_detail_tbl.sub_fee_head_id", nativeQuery = true)
    List<Object[]> getPaidFeeHeadsListByReceipt(String lastReceiptNo,int receipt_curent_branch);



    List<FeesTransactionDetail> findByReceiptNo(String receiptNo);

    FeesTransactionDetail findTop1ByTransactionDateAndReceiptNoOrderByIdDesc(LocalDate trDate, String lastReceiptNo);

    FeesTransactionDetail findTop1ByReceiptNoOrderByIdDesc(String lastReceiptNo);

    List<FeesTransactionDetail> findByFeeHeadIdAndStatus(Long id, boolean b);

    @Query(value="SELECT IFNULL (SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id,"+
       " receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name," +
      "student_register_tbl.last_name, fees_transaction_summary_tbl.id ,IFNULL(fees_transaction_summary_tbl.concession_amount, 0)  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl "+
        "      ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN "+
       "       student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id "+
      "        WHERE fees_transaction_summary_tbl.outlet_id=?1 "+
     "         AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.academic_year_id=?3 " +
    "          AND fees_transaction_summary_tbl.student_type IN(1,2) AND fees_transaction_detail_tbl.transaction_date "+
   " BETWEEN ?4 AND ?5 GROUP BY receipt_no ORDER BY student_register_tbl.last_name ASC",nativeQuery=true)
    List<Object[]> findallTransactionByDateWise(Long id, Long id1, String academicyear, String fromDate, String toDate);


    @Query(value="SELECT IFNULL (SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id,"+
            " receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name," +
            "student_register_tbl.last_name, fees_transaction_summary_tbl.id ,IFNULL(fees_transaction_summary_tbl.concession_amount, 0)  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl "+
            "      ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN "+
            "       student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id "+
            "        WHERE fees_transaction_summary_tbl.outlet_id=?1 "+
            "         AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.academic_year_id=?3 " +
            "          AND fees_transaction_summary_tbl.student_type=?6 AND fees_transaction_detail_tbl.transaction_date "+
            " BETWEEN ?4 AND ?5 GROUP BY receipt_no ORDER BY student_register_tbl.last_name ASC",nativeQuery=true)
    List<Object[]> findallStandardOnlyByDateWise(Long id, Long id1, String academicyear, String fromDate, String toDate, String studentType);


    @Query(value="SELECT IFNULL (SUM(fees_transaction_detail_tbl.paid_amount),0), transaction_date, fees_transaction_summary_tbl.student_id,"+
            " receipt_no,  fees_transaction_summary_tbl.standard_id, student_register_tbl.first_name," +
            "student_register_tbl.last_name, fees_transaction_summary_tbl.id ,IFNULL(fees_transaction_summary_tbl.concession_amount, 0)  FROM `fees_transaction_summary_tbl` LEFT JOIN fees_transaction_detail_tbl "+
            "      ON fees_transaction_summary_tbl.id=fees_transaction_detail_tbl.fees_transaction_summary_id LEFT JOIN "+
            "       student_register_tbl ON fees_transaction_summary_tbl.student_id=student_register_tbl.id "+
            "        WHERE fees_transaction_summary_tbl.outlet_id=?1 "+
            "         AND fees_transaction_summary_tbl.branch_id=?2 AND fees_transaction_summary_tbl.academic_year_id=?3 " +
            "          AND fees_transaction_summary_tbl.standard_id=?6 AND fees_transaction_summary_tbl.student_type IN(1,2) AND fees_transaction_detail_tbl.transaction_date "+
            " BETWEEN ?4 AND ?5 GROUP BY receipt_no ORDER BY student_register_tbl.last_name ASC",nativeQuery=true)
    List<Object[]> findallStudentTypeOnlyByDateWise(Long id, Long id1, String academicyear, String fromDate, String toDate, String standard);

    FeesTransactionDetail findTop1ByFeesTransactionSummaryIdAndFeeHeadIdOrderByIdDesc(Long id, Long id1);
}
