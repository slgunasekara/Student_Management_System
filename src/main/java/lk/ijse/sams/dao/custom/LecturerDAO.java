package lk.ijse.sams.dao.custom;

import lk.ijse.sams.dao.CrudDAO;
import lk.ijse.sams.entity.Lecturer;

import java.util.Optional;

public interface LecturerDAO extends CrudDAO<Lecturer> {
    Optional<Lecturer> findByEmail(String email);
    boolean existsByEmail(String email);
}
