package lk.ijse.sams.dto;
import lk.ijse.sams.entity.Attendance;
import lombok.*;
import java.sql.Date;
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class AttendanceDTO {
    private String attendanceId, studentId, studentName, sessionId, remarks;
    private Attendance.AttendanceStatus status;
    private Date markedDate;
}
