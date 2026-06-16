package lk.ijse.sams.bo.custom;
import lk.ijse.sams.bo.SuperBO;
import lk.ijse.sams.dto.ClassSessionDTO;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
public interface ClassSessionBO extends SuperBO {
    void saveSession(ClassSessionDTO dto);
    void updateSession(ClassSessionDTO dto);
    void deleteSession(String id);
    List<ClassSessionDTO> getAllSessions();
    List<ClassSessionDTO> getSessionsBySubject(String subjectId);
    Optional<ClassSessionDTO> findById(String id);
    String generateNextId();
}
