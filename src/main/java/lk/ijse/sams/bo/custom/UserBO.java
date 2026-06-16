package lk.ijse.sams.bo.custom;

import lk.ijse.sams.bo.SuperBO;
import lk.ijse.sams.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserBO extends SuperBO {
    void register(UserDTO dto);
    UserDTO login(String username, String password);
    List<UserDTO> getAllUsers();
    void updateUser(UserDTO dto);
    void deleteUser(String userId);
    Optional<UserDTO> findById(String id);
    String generateNextId();
}
