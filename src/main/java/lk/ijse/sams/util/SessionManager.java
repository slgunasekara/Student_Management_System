package lk.ijse.sams.util;

import lk.ijse.sams.dto.UserDTO;


public class SessionManager {

    private static UserDTO currentUser;

    public static void setCurrentUser(UserDTO user) {
        currentUser = user;
    }

    public static UserDTO getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isAdmin() {
        return currentUser != null &&
                currentUser.getRole() == lk.ijse.sams.entity.User.UserRole.ADMIN;
    }
}
