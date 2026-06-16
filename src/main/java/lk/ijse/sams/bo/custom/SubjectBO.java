package lk.ijse.sams.bo.custom;
import lk.ijse.sams.bo.SuperBO;
import lk.ijse.sams.dto.SubjectDTO;
import java.util.List;
import java.util.Optional;
public interface SubjectBO extends SuperBO {
    void saveSubject(SubjectDTO dto);
    void updateSubject(SubjectDTO dto);
    void deleteSubject(String id);
    List<SubjectDTO> getAllSubjects();
    List<SubjectDTO> getSubjectsByCourse(String courseId);
    Optional<SubjectDTO> findById(String id);
    String generateNextId();
}
