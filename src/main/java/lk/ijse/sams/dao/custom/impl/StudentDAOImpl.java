package lk.ijse.sams.dao.custom.impl;

import lk.ijse.sams.config.FactoryConfiguration;
import lk.ijse.sams.dao.custom.StudentDAO;
import lk.ijse.sams.entity.Student;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class StudentDAOImpl implements StudentDAO {

    private final FactoryConfiguration fc = FactoryConfiguration.getInstance();

    @Override
    public List<Student> getAll() {
        Session session = fc.getSession();
        try {
            return session.createQuery(
                    "SELECT s FROM Student s LEFT JOIN FETCH s.course", Student.class).list();
        } finally { session.close(); }
    }

    @Override
    public boolean save(Student student) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try { session.persist(student); tx.commit(); return true; }
        catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean update(Student student) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            Student existing = session.get(Student.class, student.getStudentId());
            if (existing == null) return false;
            existing.setName(student.getName());
            existing.setEmail(student.getEmail());
            existing.setPhone(student.getPhone());
            existing.setDateOfBirth(student.getDateOfBirth());
            existing.setAddress(student.getAddress());
            existing.setCourse(student.getCourse());
            session.merge(existing); tx.commit(); return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean delete(String id) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            Student s = session.get(Student.class, id);
            if (s == null) return false;
            session.remove(s); tx.commit(); return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public Optional<Student> findById(String id) {
        Session session = fc.getSession();
        try {
            Query<Student> q = session.createQuery(
                    "SELECT s FROM Student s LEFT JOIN FETCH s.course WHERE s.studentId = :id",
                    Student.class);
            q.setParameter("id", id);
            return q.uniqueResultOptional();
        } finally { session.close(); }
    }

    @Override
    public Optional<Student> findByRegNumber(String regNumber) {
        Session session = fc.getSession();
        try {
            Query<Student> q = session.createQuery(
                    "FROM Student WHERE regNumber = :r", Student.class);
            q.setParameter("r", regNumber);
            return q.uniqueResultOptional();
        } finally { session.close(); }
    }

    @Override
    public List<Student> findByCourseId(String courseId) {
        Session session = fc.getSession();
        try {
            Query<Student> q = session.createQuery(
                    "SELECT s FROM Student s LEFT JOIN FETCH s.course " +
                    "WHERE s.course.courseId = :cid", Student.class);
            q.setParameter("cid", courseId);
            return q.list();
        } finally { session.close(); }
    }

    @Override
    public boolean existsByRegNumber(String regNumber) {
        Session session = fc.getSession();
        try {
            Query<Long> q = session.createQuery(
                    "SELECT COUNT(s) FROM Student s WHERE s.regNumber = :r", Long.class);
            q.setParameter("r", regNumber);
            return q.uniqueResult() > 0;
        } finally { session.close(); }
    }

    @Override
    public boolean existsByEmail(String email) {
        Session session = fc.getSession();
        try {
            Query<Long> q = session.createQuery(
                    "SELECT COUNT(s) FROM Student s WHERE s.email = :e", Long.class);
            q.setParameter("e", email);
            return q.uniqueResult() > 0;
        } finally { session.close(); }
    }

    @Override
    public String getLastId() {
        Session session = fc.getSession();
        try {
            Query<String> q = session.createQuery(
                    "SELECT s.studentId FROM Student s ORDER BY s.studentId DESC", String.class);
            q.setMaxResults(1);
            return q.uniqueResult();
        } finally { session.close(); }
    }
}
