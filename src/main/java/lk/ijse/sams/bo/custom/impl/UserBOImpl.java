package lk.ijse.sams.bo.custom.impl;

import lk.ijse.sams.bo.custom.UserBO;
import lk.ijse.sams.bo.exception.DuplicateException;
import lk.ijse.sams.bo.exception.LoginException;
import lk.ijse.sams.bo.exception.NotFoundException;
import lk.ijse.sams.dao.DAOFactory;
import lk.ijse.sams.dao.DAOTypes;
import lk.ijse.sams.dao.custom.UserDAO;
import lk.ijse.sams.dto.UserDTO;
import lk.ijse.sams.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserBOImpl implements UserBO {

    private final UserDAO userDAO = DAOFactory.getInstance().getDAO(DAOTypes.USER);

    @Override
    public void register(UserDTO dto) {
        if (userDAO.existsByUsername(dto.getUsername())) {
            throw new DuplicateException("Username '" + dto.getUsername() + "' already exists!");
        }
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        user.setRole(dto.getRole());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setActive(dto.isActive());
        userDAO.save(user);
    }

    @Override
    public UserDTO login(String username, String password) {
        Optional<User> opt = userDAO.findByUsername(username);
        if (opt.isEmpty()) throw new LoginException("Invalid username or password!");
        User user = opt.get();
        if (!user.isActive()) throw new LoginException("Account is deactivated. Contact admin.");
        if (!BCrypt.checkpw(password, user.getPassword())) throw new LoginException("Invalid username or password!");
        return toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userDAO.getAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public void updateUser(UserDTO dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        }
        user.setRole(dto.getRole());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setActive(dto.isActive());
        if (!userDAO.update(user)) throw new NotFoundException("User not found!");
    }

    @Override
    public void deleteUser(String userId) {
        if (!userDAO.delete(userId)) throw new NotFoundException("User not found!");
    }

    @Override
    public Optional<UserDTO> findById(String id) {
        return userDAO.findById(id).map(this::toDTO);
    }

    @Override
    public String generateNextId() {
        String last = userDAO.getLastId();
        if (last == null) return "U001";
        int num = Integer.parseInt(last.substring(1)) + 1;
        return String.format("U%03d", num);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setActive(user.isActive());
        return dto;
    }
}
