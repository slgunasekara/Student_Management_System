package lk.ijse.sams.dto;
import lombok.*;
import java.sql.Date;
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class StudentDTO {
    private String studentId, regNumber, name, email, phone, address, courseId, courseName;
    private Date dateOfBirth, enrollmentDate;
}
