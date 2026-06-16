package lk.ijse.sams.dto;
import lombok.*;
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class SubjectDTO {
    private String subjectId, subjectName, subjectCode, description, courseId, courseName;
    private int credits;
}
