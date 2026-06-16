package lk.ijse.sams.dto;
import lk.ijse.sams.entity.User;
import lombok.*;
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class UserDTO {
    private String userId, username, password, fullName, email;
    private User.UserRole role;
    private boolean active;
}
