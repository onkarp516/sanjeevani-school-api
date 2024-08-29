package in.truethics.ethics.ethicsapiv10.repository.inventory_repository;

import in.truethics.ethics.ethicsapiv10.model.inventory.InventorySummaryTransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface InventoryTransactionDetailsRepository extends
        JpaRepository<InventorySummaryTransactionDetails, Long> {
    @Query(
            value = " SELECT * FROM inventory_summary_transaction_details_tbl WHERE product_id=?1 ORDER BY ID DESC LIMIT 1 ", nativeQuery = true
    )
    InventorySummaryTransactionDetails findLastRecord(Long id);

    List<InventorySummaryTransactionDetails> findByProductId(long product_id);

    List<InventorySummaryTransactionDetails> findByOutletId(Long id);

    InventorySummaryTransactionDetails findTop1ByProductIdAndOutletIdOrderByIdDesc(Long pid, Long oid);

    //     To get The opening stocks of productId #First Row.. of Starting date
    InventorySummaryTransactionDetails findTop1ByTranxDateAndProductIdAndOutletIdOrderByIdAsc(LocalDate startDate, Long pid, Long oid1);

    InventorySummaryTransactionDetails findTop1ByProductIdAndOutletIdOrderByIdAsc(Long productId, Long id);

    //     To get The closing stocks of productId #Last Row.. of ending date
    InventorySummaryTransactionDetails findTop1ByTranxDateAndProductIdAndOutletIdOrderByIdDesc(LocalDate endDate, Long pid, Long oid1);

    //     To get the closing stocks of productId when record is not present in the table of the given date have to take last transitions closing stock
//
    @Query(
            value = "SELECT * FROM inventory_summary_transaction_details_tbl WHERE product_id = ?2 AND outlet_id =?3 AND tranx_date <= ?1 ORDER BY id DESC LIMIT 1  ;", nativeQuery = true
    )
    InventorySummaryTransactionDetails findClosingOfLastRecAccToEndDate(LocalDate endDate, Long pid, Long oid1);


    List<InventorySummaryTransactionDetails> findAllByProductIdAndOutletId(Long pid, Long oid1);


    //    For Stock In /Inward
    @Query(
            value = " SELECT sum(stock_in) FROM inventory_summary_transaction_details_tbl WHERE product_id=?1 And outlet_id=?2 ", nativeQuery = true
    )
    double findSumOfStockInByProductIdAndOutletId(Long pid, Long oid1);

    @Query(
            value = "SELECT sum(stock_in) FROM inventory_summary_transaction_details_tbl WHERE product_id =?1 AND tranx_date between ?2 AND ?3 And outlet_id=?4 ", nativeQuery = true
    )
    double findSumOfStockInBetweenDate(Long pid, LocalDate startDate, LocalDate endDate, Long oid1);

    // For Stock Out.. /Outward
    @Query(
            value = " SELECT sum(stock_out) FROM inventory_summary_transaction_details_tbl WHERE product_id=?1 ", nativeQuery = true
    )
    double findSumOfStockOutByProductIdAndOutletId(Long pid, Long oid1);

    @Query(
            value = "SELECT sum(stock_out) FROM inventory_summary_transaction_details_tbl WHERE product_id =?1 AND tranx_date between ?2 AND ?3 And outlet_id=?4 ", nativeQuery = true
    )
    double findSumOfStockOutBetweenDate(Long pid, LocalDate startDate, LocalDate endDate, Long oid1);


}


//  SELECT * FROM inventory_summary_transaction_details_tbl WHERE product_id = 1 AND outlet_id =3 AND tranx_date <= '2022-01-31' LIMIT 1;