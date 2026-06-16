package lk.ijse.sams.dao.custom.impl;

import lk.ijse.sams.config.FactoryConfiguration;
import lk.ijse.sams.dao.custom.UserDAO;
import lk.ijse.sams.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private final FactoryConfiguration fc = FactoryConfiguration.getInstance();

    @Override
    public List<User> getAll() {
        Session session = fc.getSession();
        try {
            return session.createQuery("FROM User", User.class).list();
        } finally { session.close(); }
    }

    @Override
    public boolean save(User user) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            session.persist(user);
            tx.commit();
            return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean update(User user) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            User existing = session.get(User.class, user.getUserId());
            if (existing == null) return false;
            existing.setUsername(user.getUsername());
            existing.setRole(user.getRole());
            existing.setFullName(user.getFullName());
            existing.setEmail(user.getEmail());
            existing.setActive(user.isActive());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existing.setPassword(user.getPassword());
            }
            session.merge(existing);
            tx.commit();
            return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public boolean delete(String id) {
        Session session = fc.getSession();
        Transaction tx = session.beginTransaction();
        try {
            User user = session.get(User.class, id);
            if (user == null) return false;
            session.remove(user);
            tx.commit();
            return true;
        } catch (Exception e) { tx.rollback(); return false; }
        finally { session.close(); }
    }

    @Override
    public Optional<User> findById(String id) {
        Session session = fc.getSession();
        try {
            return Optional.ofNullable(session.get(User.class, id));
        } finally { session.close(); }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Session session = fc.getSession();
        try {
            Query<User> q = session.createQuery("FROM User WHERE username = :u", User.class);
            q.setParameter("u", username);
            return q.uniqueResultOptional();
        } finally { session.close(); }
    }

    @Override
    public boolean existsByUsername(String username) {
        Session session = fc.getSession();
        try {
            Query<Long> q = session.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :u", Long.class);
            q.setParameter("u", username);
            return q.uniqueResult() > 0;
        } finally { session.close(); }
    }

    @Override
    public String getLastId() {
        Session session = fc.getSession();
        try {
            Query<String> q = session.createQuery("SELECT u.userId FROM User u ORDER BY u.userId DESC", String.class);
            q.setMaxResults(1);
            return q.uniqueResult();
        } finally { session.close(); }
    }
}
