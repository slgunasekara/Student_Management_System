package lk.ijse.sams.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lk.ijse.sams.bo.BOFactory;
import lk.ijse.sams.bo.BOTypes;
import lk.ijse.sams.bo.custom.*;
import lk.ijse.sams.util.SessionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HomeController {

    @FXML private Label lblWelcome;
    @FXML private Label lblDate;
    @FXML private Label lblTotalCourses;
    @FXML private Label lblTotalStudents;
    @FXML private Label lblTotalLecturers;
    @FXML private Label lblTotalSessions;

    // Quick action tiles
    @FXML private VBox qaAddStudent;
    @FXML private VBox qaMarkAttendance;
    @FXML private VBox qaAddClass;
    @FXML private VBox qaViewReports;

    @FXML
    public void initialize() {
        if (SessionManager.getCurrentUser() != null) {
            lblWelcome.setText("Welcome back, " + SessionManager.getCurrentUser().getFullName() + "! 👋");
        }
        lblDate.setText("Today is " + LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));

        loadStats();
        wireQuickActions();
    }

    private void loadStats() {
        try {
            CourseBO courseBO = BOFactory.getInstance().getBO(BOTypes.COURSE);
            lblTotalCourses.setText(String.valueOf(courseBO.getAllCourses().size()));
        } catch (Exception e) { lblTotalCourses.setText("—"); }

        try {
            StudentBO studentBO = BOFactory.getInstance().getBO(BOTypes.STUDENT);
            lblTotalStudents.setText(String.valueOf(studentBO.getAllStudents().size()));
        } catch (Exception e) { lblTotalStudents.setText("—"); }

        try {
            LecturerBO lecturerBO = BOFactory.getInstance().getBO(BOTypes.LECTURER);
            lblTotalLecturers.setText(String.valueOf(lecturerBO.getAllLecturers().size()));
        } catch (Exception e) { lblTotalLecturers.setText("—"); }

        try {
            ClassSessionBO sessionBO = BOFactory.getInstance().getBO(BOTypes.CLASS_SESSION);
            lblTotalSessions.setText(String.valueOf(sessionBO.getAllSessions().size()));
        } catch (Exception e) { lblTotalSessions.setText("—"); }
    }

    private void wireQuickActions() {
        qaAddStudent.setOnMouseClicked(e -> navigateTo("/view/StudentView.fxml", "Manage Students"));
        qaMarkAttendance.setOnMouseClicked(e -> navigateTo("/view/AttendanceView.fxml", "Mark Attendance"));
        qaAddClass.setOnMouseClicked(e -> navigateTo("/view/ClassSessionView.fxml", "Class Scheduling"));
        qaViewReports.setOnMouseClicked(e -> navigateTo("/view/ReportView.fxml", "Attendance Reports"));
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Node view = loader.load();
            // Navigate via parent dashboard StackPane
            javafx.scene.Parent parent = lblWelcome.getScene().getRoot();
            if (parent.lookup("#contentArea") instanceof javafx.scene.layout.StackPane sp) {
                sp.getChildren().setAll(view);
            }
            // Update title
            if (parent.lookup("#lblPageTitle") instanceof Label lbl) {
                lbl.setText(title);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
