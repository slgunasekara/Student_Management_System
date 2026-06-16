package lk.ijse.sams.dao;

import lk.ijse.sams.dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory() {}

    public static DAOFactory getInstance() {
        return daoFactory == null ? (daoFactory = new DAOFactory()) : daoFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperDAO> T getDAO(DAOTypes daoType) {
        return switch (daoType) {
            case USER          -> (T) new UserDAOImpl();
            case COURSE        -> (T) new CourseDAOImpl();
            case SUBJECT       -> (T) new SubjectDAOImpl();
            case STUDENT       -> (T) new StudentDAOImpl();
            case LECTURER      -> (T) new LecturerDAOImpl();
            case CLASS_SESSION -> (T) new ClassSessionDAOImpl();
            case ATTENDANCE    -> (T) new AttendanceDAOImpl();
        };
    }
}
