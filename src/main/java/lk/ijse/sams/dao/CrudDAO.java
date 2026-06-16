package lk.ijse.sams.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDAO<T> extends SuperDAO {
    List<T> getAll();
    boolean save(T t);
    boolean update(T t);
    boolean delete(String id);
    Optional<T> findById(String id);
    String getLastId();
}
