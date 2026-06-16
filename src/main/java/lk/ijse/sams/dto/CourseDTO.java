package lk.ijse.sams.dto;
import lombok.*;
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class CourseDTO {
    private String courseId, courseName, courseCode, description;
    private int durationMonths;
}
