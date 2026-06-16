package lk.ijse.sams.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.sams.AppInitializer;
import lk.ijse.sams.util.SessionManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label lblPageTitle;
    @FXML private Label lblDateTime;
    @FXML private StackPane contentArea;

    @FXML private Button btnDashboard;
    @FXML private Button btnCourses;
    @FXML private Button btnSubjects;
    @FXML private Button btnStudents;
    @FXML private Button btnLecturers;
    @FXML private Button btnClasses;
    @FXML private Button btnAttendance;
    @FXML private Button btnReports;
    @FXML private Button btnUsers;

    private Button activeBtn;

    @FXML
    public void initialize() {
        // Set user info
        if (SessionManager.getCurrentUser() != null) {
            lblUserName.setText(SessionManager.getCurrentUser().getFullName());
            lblUserRole.setText(SessionManager.getCurrentUser().getRole().toString());
        }

        // Admin-only items
        if (!SessionManager.isAdmin()) {
            btnUsers.setVisible(false);
            btnUsers.setManaged(false);
        }

        // Live clock
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e ->
                lblDateTime.setText(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy  HH:mm:ss")))));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        // Load Home by default
        setActive(btnDashboard);
        loadView("/view/HomeView.fxml", "Dashboard");
    }

    @FXML
    public void navigate(ActionEvent event) {
        Button clicked = (Button) event.getSource();
        setActive(clicked);

        if (clicked == btnDashboard)  loadView("/view/HomeView.fxml",          "Dashboard");
        else if (clicked == btnCourses)    loadView("/view/CourseView.fxml",        "Manage Courses");
        else if (clicked == btnSubjects)   loadView("/view/SubjectView.fxml",       "Manage Subjects");
        else if (clicked == btnStudents)   loadView("/view/StudentView.fxml",       "Manage Students");
        else if (clicked == btnLecturers)  loadView("/view/LecturerView.fxml",      "Manage Lecturers");
        else if (clicked == btnClasses)    loadView("/view/ClassSessionView.fxml",  "Class Scheduling");
        else if (clicked == btnAttendance) loadView("/view/AttendanceView.fxml",    "Mark Attendance");
        else if (clicked == btnReports)    loadView("/view/ReportView.fxml",        "Attendance Reports");
        else if (clicked == btnUsers)      loadView("/view/UserView.fxml",          "User Management");
    }

    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            contentArea.getChildren().setAll(view);
            lblPageTitle.setText(title);
        } catch (IOException e) {
            e.printStackTrace();
            lblPageTitle.setText("Error loading view");
        }
    }

    public void loadViewExternally(String fxmlPath, String title) {
        loadView(fxmlPath, title);
    }

    private void setActive(Button btn) {
        if (activeBtn != null) {
            activeBtn.setStyle(activeBtn.getStyle()
                    .replace("-fx-background-color: rgba(255,255,255,0.15);",
                             "-fx-background-color: transparent;")
                    .replace("-fx-text-fill: white;", "-fx-text-fill: #fecaca;"));
        }
        activeBtn = btn;
        btn.setStyle(btn.getStyle()
                .replace("-fx-background-color: transparent;",
                         "-fx-background-color: rgba(255,255,255,0.15);")
                .replace("-fx-text-fill: #fecaca;", "-fx-text-fill: white;"));
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            SessionManager.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) contentArea.getScene().getWindow();
            AppInitializer.attachFullscreenToggle(stage, scene);
            stage.setScene(scene);
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
