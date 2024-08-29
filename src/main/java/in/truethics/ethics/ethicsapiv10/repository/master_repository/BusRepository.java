package in.truethics.ethics.ethicsapiv10.repository.master_repository;

import in.truethics.ethics.ethicsapiv10.model.school_master.Bus;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findByCreatedByAndStatus(Long id, boolean b);

    Bus findByIdAndStatus(Long busStopId, boolean b);

    List<Bus> findByStatus(boolean b);


    @Transactional
    @Modifying
    @Cascade(CascadeType.DELETE)
    @Query(value = "DELETE FROM `bus_tbl` WHERE id=?1", nativeQuery = true)
    void deleteBusByid(Long id);

    Bus findByBusStopNameAndStatus(String checkbus, boolean b);

    List<Bus> findByStatusOrderByBusStopNameAsc(boolean b);
}
