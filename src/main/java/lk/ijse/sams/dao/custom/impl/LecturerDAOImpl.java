package lk.ijse.sams.dao.custom.impl;

import lk.ijse.sams.config.FactoryConfiguration;
import lk.ijse.sams.dao.custom.LecturerDAO;
import lk.ijse.sams.entity.Lecturer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class LecturerDAOImpl implements LecturerDAO {

    private final FactoryConfiguration fc = FactoryConfiguration.getInstance();

    @Override
    public List<Lecturer> getAll() {
        Session session = fc.getSession();
        try { return session.createQuery("FROM Lecturer", Lecturer.class).list(); }
        finally { session.close(); }
    }

    @Override
    public boolean save(Lecturer lecturer) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try { session.persist(lecturer); tx.commit(); return true; }
        catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean update(Lecturer lecturer) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            Lecturer existing = session.get(Lecturer.class, lecturer.getLecturerId());
            if (existing == null) return false;
            existing.setName(lecturer.getName());
            existing.setEmail(lecturer.getEmail());
            existing.setPhone(lecturer.getPhone());
            existing.setQualification(lecturer.getQualification());
            existing.setDepartment(lecturer.getDepartment());
            existing.setActive(lecturer.isActive());
            session.merge(existing); tx.commit(); return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean delete(String id) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            Lecturer l = session.get(Lecturer.class, id);
            if (l == null) return false;
            session.remove(l); tx.commit(); return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public Optional<Lecturer> findById(String id) {
        Session session = fc.getSession();
        try { return Optional.ofNullable(session.get(Lecturer.class, id)); }
        finally { session.close(); }
    }

    @Override
    public Optional<Lecturer> findByEmail(String email) {
        Session session = fc.getSession();
        try {
            Query<Lecturer> q = session.createQuery(
                    "FROM Lecturer WHERE email = :e", Lecturer.class);
            q.setParameter("e", email);
            return q.uniqueResultOptional();
        } finally { session.close(); }
    }

    @Override
    public boolean existsByEmail(String email) {
        Session session = fc.getSession();
        try {
            Query<Long> q = session.createQuery(
                    "SELECT COUNT(l) FROM Lecturer l WHERE l.email = :e", Long.class);
            q.setParameter("e", email);
            return q.uniqueResult() > 0;
        } finally { session.close(); }
    }

    @Override
    public String getLastId() {
        Session session = fc.getSession();
        try {
            Query<String> q = session.createQuery(
                    "SELECT l.lecturerId FROM Lecturer l ORDER BY l.lecturerId DESC", String.class);
            q.setMaxResults(1);
            return q.uniqueResult();
        } finally { session.close(); }
    }
}
