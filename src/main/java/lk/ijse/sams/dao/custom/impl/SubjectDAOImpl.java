package lk.ijse.sams.dao.custom.impl;

import lk.ijse.sams.config.FactoryConfiguration;
import lk.ijse.sams.dao.custom.SubjectDAO;
import lk.ijse.sams.entity.Subject;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class SubjectDAOImpl implements SubjectDAO {

    private final FactoryConfiguration fc = FactoryConfiguration.getInstance();

    @Override
    public List<Subject> getAll() {
        Session session = fc.getSession();
        try {
            return session.createQuery(
                    "SELECT s FROM Subject s LEFT JOIN FETCH s.course", Subject.class).list();
        } finally { session.close(); }
    }

    @Override
    public boolean save(Subject subject) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try { session.persist(subject); tx.commit(); return true; }
        catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean update(Subject subject) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            Subject existing = session.get(Subject.class, subject.getSubjectId());
            if (existing == null) return false;
            existing.setSubjectName(subject.getSubjectName());
            existing.setSubjectCode(subject.getSubjectCode());
            existing.setCredits(subject.getCredits());
            existing.setDescription(subject.getDescription());
            existing.setCourse(subject.getCourse());
            session.merge(existing); tx.commit(); return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean delete(String id) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            Subject s = session.get(Subject.class, id);
            if (s == null) return false;
            session.remove(s); tx.commit(); return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public Optional<Subject> findById(String id) {
        Session session = fc.getSession();
        try {
            Query<Subject> q = session.createQuery(
                    "SELECT s FROM Subject s LEFT JOIN FETCH s.course WHERE s.subjectId = :id",
                    Subject.class);
            q.setParameter("id", id);
            return q.uniqueResultOptional();
        } finally { session.close(); }
    }

    @Override
    public List<Subject> findByCourseId(String courseId) {
        Session session = fc.getSession();
        try {
            Query<Subject> q = session.createQuery(
                    "SELECT s FROM Subject s LEFT JOIN FETCH s.course " +
                    "WHERE s.course.courseId = :cid", Subject.class);
            q.setParameter("cid", courseId);
            return q.list();
        } finally { session.close(); }
    }

    @Override
    public boolean existsBySubjectCode(String code) {
        Session session = fc.getSession();
        try {
            Query<Long> q = session.createQuery(
                    "SELECT COUNT(s) FROM Subject s WHERE s.subjectCode = :c", Long.class);
            q.setParameter("c", code);
            return q.uniqueResult() > 0;
        } finally { session.close(); }
    }

    @Override
    public String getLastId() {
        Session session = fc.getSession();
        try {
            Query<String> q = session.createQuery(
                    "SELECT s.subjectId FROM Subject s ORDER BY s.subjectId DESC", String.class);
            q.setMaxResults(1);
            return q.uniqueResult();
        } finally { session.close(); }
    }
}
