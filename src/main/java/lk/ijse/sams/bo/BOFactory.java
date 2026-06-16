package lk.ijse.sams.bo;

import lk.ijse.sams.bo.custom.impl.*;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {}

    public static BOFactory getInstance() {
        return boFactory == null ? (boFactory = new BOFactory()) : boFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperBO> T getBO(BOTypes boType) {
        return switch (boType) {
            case USER          -> (T) new UserBOImpl();
            case COURSE        -> (T) new CourseBOImpl();
            case SUBJECT       -> (T) new SubjectBOImpl();
            case STUDENT       -> (T) new StudentBOImpl();
            case LECTURER      -> (T) new LecturerBOImpl();
            case CLASS_SESSION -> (T) new ClassSessionBOImpl();
            case ATTENDANCE    -> (T) new AttendanceBOImpl();
        };
    }
}
