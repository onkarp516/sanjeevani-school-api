package in.truethics.ethics.ethicsapiv10.repository.user_repository;

import in.truethics.ethics.ethicsapiv10.model.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByUsercode(String username);

    List<Users> findByBranchIdAndStatus(Long branchId, boolean b);

    List<Users> findByOutletIdAndStatus(Long outletId, boolean b);

    List<Users> findByUserRoleAndStatus(String userRole, boolean b);

    Users findByIdAndStatus(long id, boolean b);

    Users findByUsername(String usercode);

    List<Users> findByUserRoleAndCreatedByAndStatus(String userRole, Long id, boolean b);

    List<Users> findByUserRoleIgnoreCaseAndStatus(String sadmin, boolean b);

    List<Users> findByOutletIdAndUserRoleIgnoreCaseAndStatus(Long id, String badmin, boolean b);

    List<Users> findByBranchIdAndUserRoleIgnoreCaseAndStatus(Long id, String badmin, boolean b);
}
