package lk.ijse.sams.bo.custom;
import lk.ijse.sams.bo.SuperBO;
import lk.ijse.sams.dto.LecturerDTO;
import java.util.List;
import java.util.Optional;
public interface LecturerBO extends SuperBO {
    void saveLecturer(LecturerDTO dto);
    void updateLecturer(LecturerDTO dto);
    void deleteLecturer(String id);
    List<LecturerDTO> getAllLecturers();
    Optional<LecturerDTO> findById(String id);
    String generateNextId();
}
