package lk.ijse.sams.dao.custom;

import lk.ijse.sams.dao.CrudDAO;
import lk.ijse.sams.entity.Attendance;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface AttendanceDAO extends CrudDAO<Attendance> {
    List<Attendance> findByStudentId(String studentId);
    List<Attendance> findBySessionId(String sessionId);
    List<Attendance> findByStudentAndDateRange(String studentId, Date from, Date to);
    Optional<Attendance> findByStudentAndSession(String studentId, String sessionId);
    long countPresentByStudentAndSubject(String studentId, String subjectId);
    long countTotalByStudentAndSubject(String studentId, String subjectId);
}
