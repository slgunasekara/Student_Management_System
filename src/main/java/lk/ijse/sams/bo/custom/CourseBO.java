package lk.ijse.sams.bo.custom;
import lk.ijse.sams.bo.SuperBO;
import lk.ijse.sams.dto.CourseDTO;
import java.util.List;
import java.util.Optional;
public interface CourseBO extends SuperBO {
    void saveCourse(CourseDTO dto);
    void updateCourse(CourseDTO dto);
    void deleteCourse(String id);
    List<CourseDTO> getAllCourses();
    Optional<CourseDTO> findById(String id);
    String generateNextId();
}
