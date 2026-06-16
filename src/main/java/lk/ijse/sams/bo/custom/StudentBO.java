package lk.ijse.sams.bo.custom;
import lk.ijse.sams.bo.SuperBO;
import lk.ijse.sams.dto.StudentDTO;
import java.util.List;
import java.util.Optional;
public interface StudentBO extends SuperBO {
    void saveStudent(StudentDTO dto);
    void updateStudent(StudentDTO dto);
    void deleteStudent(String id);
    List<StudentDTO> getAllStudents();
    Optional<StudentDTO> findById(String id);
    String generateNextId();
    String generateNextRegNumber();
}
