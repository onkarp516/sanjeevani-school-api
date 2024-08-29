package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.master.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    /*Company findByIdAndStatus(long companyId, boolean b);

    List<Company> findByCreatedByAndStatus(Long id, boolean b);

    @Procedure("default_ledgers")
    void createDefaultLedgers(Long branchId, Long instituteId, Long outletId, Long createdBy);

    @Procedure("duties_taxes_registered_outlet")
    void createDefaultGST(Long branchId, Long instituteId, Long outletId, Long createdBy);

    @Procedure("duties_taxes_unregistered_outlet")
    void createDefaultGSTUnRegistered(Long branchId, Long instituteId, Long outletId, Long createdBy);
*/
}
