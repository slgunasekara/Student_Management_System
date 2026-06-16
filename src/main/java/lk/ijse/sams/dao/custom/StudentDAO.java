package lk.ijse.sams.dao.custom;

import lk.ijse.sams.dao.CrudDAO;
import lk.ijse.sams.entity.Student;

import java.util.List;
import java.util.Optional;

public interface StudentDAO extends CrudDAO<Student> {
    Optional<Student> findByRegNumber(String regNumber);
    List<Student> findByCourseId(String courseId);
    boolean existsByRegNumber(String regNumber);
    boolean existsByEmail(String email);
}
