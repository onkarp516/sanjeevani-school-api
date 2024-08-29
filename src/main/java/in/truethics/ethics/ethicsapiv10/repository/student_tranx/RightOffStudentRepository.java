package in.truethics.ethics.ethicsapiv10.repository.student_tranx;

import in.truethics.ethics.ethicsapiv10.model.school_tranx.RightOffStudent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RightOffStudentRepository extends JpaRepository<RightOffStudent ,Long> {
    RightOffStudent findByStudentIdAndStatus(Long id, boolean b);
}
