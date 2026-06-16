package lk.ijse.sams.dao.custom;

import lk.ijse.sams.dao.CrudDAO;
import lk.ijse.sams.entity.Subject;

import java.util.List;

public interface SubjectDAO extends CrudDAO<Subject> {
    List<Subject> findByCourseId(String courseId);
    boolean existsBySubjectCode(String code);
}
