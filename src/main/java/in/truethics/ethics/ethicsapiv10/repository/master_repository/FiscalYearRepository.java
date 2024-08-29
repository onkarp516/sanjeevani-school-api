package in.truethics.ethics.ethicsapiv10.repository.master_repository;


import in.truethics.ethics.ethicsapiv10.model.master.FiscalYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface FiscalYearRepository extends JpaRepository<FiscalYear, Long> {
    // 0 for default and 1 for User defined financial year and month
    @Query(
            value = " SELECT * FROM fiscal_year_tbl WHERE ?1 BETWEEN date_start AND date_end ", nativeQuery = true
    )
    FiscalYear findFiscalYear(LocalDate curDate);

    @Query(
            value = " SELECT YEAR(date_start) FROM fiscal_year_tbl ", nativeQuery = true
    )
    String getStartYear();

    @Query(
            value = " SELECT YEAR(date_end) FROM fiscal_year_tbl ", nativeQuery = true
    )
    String getLastYear();
}
