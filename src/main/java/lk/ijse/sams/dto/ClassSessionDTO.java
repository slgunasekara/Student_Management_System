package lk.ijse.sams.dto;
import lombok.*;
import java.sql.Date;
import java.sql.Time;
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class ClassSessionDTO {
    private String sessionId, subjectId, subjectName, lecturerId, lecturerName, venue, notes;
    private Date sessionDate;
    private Time startTime, endTime;
}
