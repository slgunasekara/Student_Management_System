package lk.ijse.sams.dto;
import lombok.*;
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class LecturerDTO {
    private String lecturerId, name, email, phone, qualification, department;
    private boolean active;
}
