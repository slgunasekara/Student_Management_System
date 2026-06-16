package lk.ijse.sams.dao.custom;

import lk.ijse.sams.dao.CrudDAO;
import lk.ijse.sams.entity.ClassSession;

import java.sql.Date;
import java.util.List;

public interface ClassSessionDAO extends CrudDAO<ClassSession> {
    List<ClassSession> findBySubjectId(String subjectId);
    List<ClassSession> findByLecturerId(String lecturerId);
    List<ClassSession> findByDateRange(Date from, Date to);
}
