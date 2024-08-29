package in.truethics.ethics.ethicsapiv10.repository.history_repository;

import in.truethics.ethics.ethicsapiv10.model.history_table.StudentTransportHistory;
import org.apache.xmlbeans.impl.jam.JamServiceParams;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentTransporHistoryRepository extends JpaRepository<StudentTransportHistory,Long> {
}
