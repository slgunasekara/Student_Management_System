package lk.ijse.sams.dao.custom.impl;

import lk.ijse.sams.config.FactoryConfiguration;
import lk.ijse.sams.dao.custom.ClassSessionDAO;
import lk.ijse.sams.entity.ClassSession;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public class ClassSessionDAOImpl implements ClassSessionDAO {

    private final FactoryConfiguration fc = FactoryConfiguration.getInstance();

    @Override
    public List<ClassSession> getAll() {
        Session session = fc.getSession();
        try {
            return session.createQuery(
                    "SELECT cs FROM ClassSession cs " +
                    "LEFT JOIN FETCH cs.subject LEFT JOIN FETCH cs.lecturer",
                    ClassSession.class).list();
        } finally { session.close(); }
    }

    @Override
    public boolean save(ClassSession cs) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try { session.persist(cs); tx.commit(); return true; }
        catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean update(ClassSession cs) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            ClassSession existing = session.get(ClassSession.class, cs.getSessionId());
            if (existing == null) return false;
            existing.setSessionDate(cs.getSessionDate());
            existing.setStartTime(cs.getStartTime());
            existing.setEndTime(cs.getEndTime());
            existing.setVenue(cs.getVenue());
            existing.setNotes(cs.getNotes());
            existing.setSubject(cs.getSubject());
            existing.setLecturer(cs.getLecturer());
            session.merge(existing); tx.commit(); return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean delete(String id) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            ClassSession cs = session.get(ClassSession.class, id);
            if (cs == null) return false;
            session.remove(cs); tx.commit(); return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public Optional<ClassSession> findById(String id) {
        Session session = fc.getSession();
        try {
            Query<ClassSession> q = session.createQuery(
                    "SELECT cs FROM ClassSession cs " +
                    "LEFT JOIN FETCH cs.subject LEFT JOIN FETCH cs.lecturer " +
                    "WHERE cs.sessionId = :id", ClassSession.class);
            q.setParameter("id", id);
            return q.uniqueResultOptional();
        } finally { session.close(); }
    }

    @Override
    public List<ClassSession> findBySubjectId(String subjectId) {
        Session session = fc.getSession();
        try {
            Query<ClassSession> q = session.createQuery(
                    "SELECT cs FROM ClassSession cs " +
                    "LEFT JOIN FETCH cs.subject LEFT JOIN FETCH cs.lecturer " +
                    "WHERE cs.subject.subjectId = :sid ORDER BY cs.sessionDate DESC",
                    ClassSession.class);
            q.setParameter("sid", subjectId);
            return q.list();
        } finally { session.close(); }
    }

    @Override
    public List<ClassSession> findByLecturerId(String lecturerId) {
        Session session = fc.getSession();
        try {
            Query<ClassSession> q = session.createQuery(
                    "SELECT cs FROM ClassSession cs " +
                    "LEFT JOIN FETCH cs.subject LEFT JOIN FETCH cs.lecturer " +
                    "WHERE cs.lecturer.lecturerId = :lid ORDER BY cs.sessionDate DESC",
                    ClassSession.class);
            q.setParameter("lid", lecturerId);
            return q.list();
        } finally { session.close(); }
    }

    @Override
    public List<ClassSession> findByDateRange(Date from, Date to) {
        Session session = fc.getSession();
        try {
            Query<ClassSession> q = session.createQuery(
                    "SELECT cs FROM ClassSession cs " +
                    "LEFT JOIN FETCH cs.subject LEFT JOIN FETCH cs.lecturer " +
                    "WHERE cs.sessionDate BETWEEN :f AND :t ORDER BY cs.sessionDate",
                    ClassSession.class);
            q.setParameter("f", from);
            q.setParameter("t", to);
            return q.list();
        } finally { session.close(); }
    }

    @Override
    public String getLastId() {
        Session session = fc.getSession();
        try {
            Query<String> q = session.createQuery(
                    "SELECT cs.sessionId FROM ClassSession cs ORDER BY cs.sessionId DESC",
                    String.class);
            q.setMaxResults(1);
            return q.uniqueResult();
        } finally { session.close(); }
    }
}
