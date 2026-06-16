package lk.ijse.sams.bo.custom;
import lk.ijse.sams.bo.SuperBO;
import lk.ijse.sams.dto.AttendanceDTO;
import java.sql.Date;
import java.util.List;
public interface AttendanceBO extends SuperBO {
    void markAttendance(AttendanceDTO dto);
    void updateAttendance(AttendanceDTO dto);
    List<AttendanceDTO> getAttendanceBySession(String sessionId);
    List<AttendanceDTO> getAttendanceByStudent(String studentId);
    List<AttendanceDTO> getAttendanceByStudentDateRange(String studentId, Date from, Date to);
    double getAttendancePercentage(String studentId, String subjectId);
    String generateNextId();
}
