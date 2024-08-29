package in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository;

import in.truethics.ethics.ethicsapiv10.model.master.LedgerMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LedgerMasterRepository extends JpaRepository<LedgerMaster, Long> {

    /* Get Sundry Creditors by outlet id */
    @Query(
            value = "SELECT id,ledger_name,ledger_code,state_code FROM ledger_master_tbl" +
                    " WHERE outlet_id =?1 AND " +
                    "principle_groups_id =5 And status=1",
            nativeQuery = true
    )
    List<Object[]> findSundryCreditorsByOutletId(Long outletId);

    /* Get Sundry Debtors by outlet id */
    @Query(
            value = "SELECT id,ledger_name,ledger_code,state_code FROM ledger_master_tbl " +
                    "WHERE outlet_id =?1 AND " +
                    "principle_groups_id =1 AND status=1",
            nativeQuery = true
    )
    List<Object[]> findSundryDebtorsByOutletId(Long outletId);

    /* Get Sundry Debtors by outlet id */
    @Query(
            value = "SELECT id,ledger_name,ledger_code,state_code FROM ledger_master_tbl " +
                    "WHERE outlet_id =?1 AND branch_id =?2 AND " +
                    "principle_groups_id =1 AND status=1",
            nativeQuery = true
    )
    List<Object[]> findSundryDebtorsByOutletIdAndBranchId(Long outletId, Long branchId);


    /* Get Cash-In Hand  by outlet id */
    @Query(
            value = "SELECT id,ledger_name,ledger_code,state_code FROM ledger_master_tbl WHERE outlet_id =?1 AND " +
                    "principle_groups_id =3 And status=1",
            nativeQuery = true
    )
    List<Object[]> findCashInHandByOutletId(Long outletId);

    /* Get Bank Accounts by outlet id */
    @Query(
            value = "SELECT id,ledger_name,ledger_code,state_code FROM ledger_master_tbl WHERE outlet_id =?1 AND " +
                    "principle_groups_id =2 And status=1",
            nativeQuery = true
    )
    List<Object[]> findBankAccountsByOutletId(Long outletId);

    List<LedgerMaster> findByOutletIdAndPrinciplesIdAndStatus(Long outletId, Long id, boolean b);

    LedgerMaster findByIdAndStatus(Long id, boolean b);

    LedgerMaster findByOutletIdAndLedgerNameIgnoreCaseAndStatus(Long id,
                                                                String ledger_name, boolean b);

    LedgerMaster findByOutletIdAndLedgerNameIgnoreCase(Long id, String round_off);

    LedgerMaster findByIdAndOutletIdAndStatus(long purchase_id, Long id, boolean b);

    LedgerMaster findByLedgerNameIgnoreCaseAndOutletId(String round_off, Long id);

    @Query(
            value = "SELECT * FROM `ledger_master_tbl` WHERE principle_groups_id =3 And status=1 And outlet_id=?1", nativeQuery = true
    )
    LedgerMaster findLedgerIdAndName(Long outlet_id);

    List<LedgerMaster> findByOutletIdAndPrincipleGroupsId(Long id, Long i);

    List<LedgerMaster> findByOutletIdAndPrinciplesId(Long id, long l);


    @Query(
            value = "SELECT * FROM `ledger_master_tbl` WHERE (principle_groups_id =3 Or principle_groups_id=2)  And status=1 And outlet_id=?1 And " +
                    " branch_id IS NULL ", nativeQuery = true
    )
    List<LedgerMaster> findBankAccountCashAccount(Long id);

    @Query(
            value = "SELECT * FROM `ledger_master_tbl` WHERE (principle_groups_id =3 Or principle_groups_id=2)  And status=1 And outlet_id=?1 And " +
                    " branch_id=?2  ", nativeQuery = true
    )
    List<LedgerMaster> findBankAccountCashAccountForBranch(Long id, Long branch_id);

    @Query(
            value = "SELECT * FROM `ledger_master_tbl` WHERE  (principle_groups_id  NOT IN (3,2) OR principle_groups_id IS NULL) And status=1 And outlet_id=?1", nativeQuery = true
    )
    List<LedgerMaster> findledgers(Long id);


    @Query(
            value = "SELECT * FROM `ledger_master_tbl` WHERE NOT slug_name='sundry_debtors' AND outlet_id=?1 AND branch_id=?2", nativeQuery = true
    )
    List<Object[]> getLedgerList(Long id, Long branchId);

    List<LedgerMaster> findByOutletIdAndStatus(Long id, boolean b);

    List<LedgerMaster> findByOutletIdAndBranchIdAndStatus(Long id, Long branchId, boolean b);

    LedgerMaster findByLedgerNameIgnoreCaseAndOutletIdAndBranchIdAndStatus(String s, Long id, Long id1, boolean b);

    LedgerMaster findByUniqueCodeAndStatusAndOutletId(String baac, boolean b, Long id);

//    LedgerMaster findByStudentRegisterIdAndOutletIdAndStatus(Long id, Long id1, boolean b);

    LedgerMaster findByStudentRegisterIdAndBranchIdAndStatus(Long id, Long id1, boolean b);

    List<LedgerMaster> findByStudentRegisterIdAndStatus(Long id, boolean b);

    LedgerMaster findByUniqueCodeAndStatusAndOutletIdAndBranchId(String baac, boolean b, Long id, Long id1);

    LedgerMaster findByOutletIdAndBranchIdAndLedgerNameIgnoreCaseAndStatus(Long id, Long id1, String ledger_name, boolean b);

//    LedgerMaster findByBranchIdAndStudentRegisterId(Long id, Long id1, boolean b);

    LedgerMaster findByStudentRegisterIdAndOutletIdAndBranchIdAndStatus(Long id, Long id1, Long id2, boolean b);

    List<LedgerMaster> findByOutletIdAndBranchIdAndPrincipleGroupsId(Long id, Long id1, long l);

    List<LedgerMaster> findByOutletIdAndPrincipleGroupsIdAndBranchIdIsNull(Long id, long l);


    LedgerMaster findByBranchIdAndStudentRegisterIdAndStatus(Long id, Long id1, boolean b);

    List<LedgerMaster> findBySlugNameAndOutletIdAndBranchIdAndStatus(String bank_account, Long id, Long id1, boolean b);

    LedgerMaster findByIdAndOutletIdAndBranchIdAndStatus(long ledgerId, Long id, Long id1, boolean b);

    @Query(
            value = "SELECT ledger_master_tbl.opening_bal FROM ledger_master_tbl WHERE ledger_master_tbl.id=?1",
            nativeQuery = true
    )
    Double findOpeningBalance(Long ledgerId);

    List<LedgerMaster> findByOutletIdAndBranchIdAndStatusOrderByIdDesc(Long id, Long id1, boolean b);

    List<LedgerMaster> findByOutletIdAndStatusAndBranchIsNullOrderByIdDesc(Long id, boolean b);

    @Query(
            value = " SELECT IFNULL(SUM(opening_bal),0.0) FROM `ledger_master_tbl` WHERE id=?1 AND " +
                    "AND outlet_id=?2 AND branch_id IS NULL AND opening_bal_type=?3 ", nativeQuery = true
    )
    Double findLedgerOpeningStocks(Long productId, Long outletId, Long branchId, String openingType);

    @Query(
            value = " SELECT IFNULL(SUM(opening_bal),0.0) FROM `ledger_master_tbl` WHERE id=?1 AND " +
                    "AND outlet_id=?2 AND branch_id=?3 AND opening_bal_type=?4 ", nativeQuery = true
    )
    Double findLedgerOpeningStocksBranch(Long productId, Long outletId, Long branchId, String openingType);
}

