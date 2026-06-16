package lk.ijse.sams.dao.custom.impl;

import lk.ijse.sams.config.FactoryConfiguration;
import lk.ijse.sams.dao.custom.AttendanceDAO;
import lk.ijse.sams.entity.Attendance;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public class AttendanceDAOImpl implements AttendanceDAO {

    private final FactoryConfiguration fc = FactoryConfiguration.getInstance();

    @Override
    public List<Attendance> getAll() {
        Session session = fc.getSession();
        try {
            return session.createQuery(
                    "SELECT a FROM Attendance a " +
                    "LEFT JOIN FETCH a.student LEFT JOIN FETCH a.classSession",
                    Attendance.class).list();
        } finally { session.close(); }
    }

    @Override
    public boolean save(Attendance attendance) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try { session.persist(attendance); tx.commit(); return true; }
        catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean update(Attendance attendance) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            Attendance existing = session.get(Attendance.class, attendance.getAttendanceId());
            if (existing == null) return false;
            existing.setStatus(attendance.getStatus());
            existing.setRemarks(attendance.getRemarks());
            session.merge(existing); tx.commit(); return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean delete(String id) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            Attendance a = session.get(Attendance.class, id);
            if (a == null) return false;
            session.remove(a); tx.commit(); return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public Optional<Attendance> findById(String id) {
        Session session = fc.getSession();
        try { return Optional.ofNullable(session.get(Attendance.class, id)); }
        finally { session.close(); }
    }

    @Override
    public List<Attendance> findByStudentId(String studentId) {
        Session session = fc.getSession();
        try {
            Query<Attendance> q = session.createQuery(
                    "SELECT a FROM Attendance a LEFT JOIN FETCH a.student LEFT JOIN FETCH a.classSession " +
                    "WHERE a.student.studentId = :sid ORDER BY a.markedDate DESC",
                    Attendance.class);
            q.setParameter("sid", studentId);
            return q.list();
        } finally { session.close(); }
    }

    @Override
    public List<Attendance> findBySessionId(String sessionId) {
        Session session = fc.getSession();
        try {
            Query<Attendance> q = session.createQuery(
                    "SELECT a FROM Attendance a LEFT JOIN FETCH a.student LEFT JOIN FETCH a.classSession " +
                    "WHERE a.classSession.sessionId = :ssid",
                    Attendance.class);
            q.setParameter("ssid", sessionId);
            return q.list();
        } finally { session.close(); }
    }

    @Override
    public List<Attendance> findByStudentAndDateRange(String studentId, Date from, Date to) {
        Session session = fc.getSession();
        try {
            String hql = (studentId == null || studentId.equals("__ALL__"))
                    ? "SELECT a FROM Attendance a LEFT JOIN FETCH a.student LEFT JOIN FETCH a.classSession " +
                      "WHERE a.markedDate BETWEEN :f AND :t ORDER BY a.markedDate"
                    : "SELECT a FROM Attendance a LEFT JOIN FETCH a.student LEFT JOIN FETCH a.classSession " +
                      "WHERE a.student.studentId = :sid AND a.markedDate BETWEEN :f AND :t ORDER BY a.markedDate";
            Query<Attendance> q = session.createQuery(hql, Attendance.class);
            if (studentId != null && !studentId.equals("__ALL__")) q.setParameter("sid", studentId);
            q.setParameter("f", from);
            q.setParameter("t", to);
            return q.list();
        } finally { session.close(); }
    }

    @Override
    public Optional<Attendance> findByStudentAndSession(String studentId, String sessionId) {
        Session session = fc.getSession();
        try {
            Query<Attendance> q = session.createQuery(
                    "SELECT a FROM Attendance a LEFT JOIN FETCH a.student LEFT JOIN FETCH a.classSession " +
                    "WHERE a.student.studentId = :sid AND a.classSession.sessionId = :ssid",
                    Attendance.class);
            q.setParameter("sid", studentId);
            q.setParameter("ssid", sessionId);
            return q.uniqueResultOptional();
        } finally { session.close(); }
    }

    @Override
    public long countPresentByStudentAndSubject(String studentId, String subjectId) {
        Session session = fc.getSession();
        try {
            Query<Long> q = session.createQuery(
                    "SELECT COUNT(a) FROM Attendance a " +
                    "WHERE a.student.studentId = :sid " +
                    "AND a.classSession.subject.subjectId = :subid " +
                    "AND a.status IN ('PRESENT', 'LATE')", Long.class);
            q.setParameter("sid", studentId);
            q.setParameter("subid", subjectId);
            return q.uniqueResult();
        } finally { session.close(); }
    }

    @Override
    public long countTotalByStudentAndSubject(String studentId, String subjectId) {
        Session session = fc.getSession();
        try {
            Query<Long> q = session.createQuery(
                    "SELECT COUNT(a) FROM Attendance a " +
                    "WHERE a.student.studentId = :sid " +
                    "AND a.classSession.subject.subjectId = :subid", Long.class);
            q.setParameter("sid", studentId);
            q.setParameter("subid", subjectId);
            return q.uniqueResult();
        } finally { session.close(); }
    }

    @Override
    public String getLastId() {
        Session session = fc.getSession();
        try {
            Query<String> q = session.createQuery(
                    "SELECT a.attendanceId FROM Attendance a ORDER BY a.attendanceId DESC",
                    String.class);
            q.setMaxResults(1);
            return q.uniqueResult();
        } finally { session.close(); }
    }
}
