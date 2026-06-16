package lk.ijse.sams.dao.custom.impl;

import lk.ijse.sams.config.FactoryConfiguration;
import lk.ijse.sams.dao.custom.CourseDAO;
import lk.ijse.sams.entity.Course;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class CourseDAOImpl implements CourseDAO {

    private final FactoryConfiguration fc = FactoryConfiguration.getInstance();

    @Override
    public List<Course> getAll() {
        Session session = fc.getSession();
        try { return session.createQuery("FROM Course", Course.class).list(); }
        finally { session.close(); }
    }

    @Override
    public boolean save(Course course) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try { session.persist(course); tx.commit(); return true; }
        catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean update(Course course) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            Course existing = session.get(Course.class, course.getCourseId());
            if (existing == null) return false;
            existing.setCourseName(course.getCourseName());
            existing.setCourseCode(course.getCourseCode());
            existing.setDescription(course.getDescription());
            existing.setDurationMonths(course.getDurationMonths());
            session.merge(existing); tx.commit(); return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean delete(String id) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            Course c = session.get(Course.class, id);
            if (c == null) return false;
            session.remove(c); tx.commit(); return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public Optional<Course> findById(String id) {
        Session session = fc.getSession();
        try { return Optional.ofNullable(session.get(Course.class, id)); }
        finally { session.close(); }
    }

    @Override
    public Optional<Course> findByCourseCode(String code) {
        Session session = fc.getSession();
        try {
            Query<Course> q = session.createQuery("FROM Course WHERE courseCode = :c", Course.class);
            q.setParameter("c", code);
            return q.uniqueResultOptional();
        } finally { session.close(); }
    }

    @Override
    public boolean existsByCourseCode(String code) {
        Session session = fc.getSession();
        try {
            Query<Long> q = session.createQuery("SELECT COUNT(c) FROM Course c WHERE c.courseCode = :c", Long.class);
            q.setParameter("c", code);
            return q.uniqueResult() > 0;
        } finally { session.close(); }
    }

    @Override
    public String getLastId() {
        Session session = fc.getSession();
        try {
            Query<String> q = session.createQuery("SELECT c.courseId FROM Course c ORDER BY c.courseId DESC", String.class);
            q.setMaxResults(1);
            return q.uniqueResult();
        } finally { session.close(); }
    }
}
