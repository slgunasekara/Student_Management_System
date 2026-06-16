package lk.ijse.sams.dao.custom;

import lk.ijse.sams.dao.CrudDAO;
import lk.ijse.sams.entity.Course;

import java.util.Optional;

public interface CourseDAO extends CrudDAO<Course> {
    Optional<Course> findByCourseCode(String code);
    boolean existsByCourseCode(String code);
}
