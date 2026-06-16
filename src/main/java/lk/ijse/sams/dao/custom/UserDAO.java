package lk.ijse.sams.dao.custom;

import lk.ijse.sams.dao.CrudDAO;
import lk.ijse.sams.entity.User;

import java.util.Optional;

public interface UserDAO extends CrudDAO<User> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
