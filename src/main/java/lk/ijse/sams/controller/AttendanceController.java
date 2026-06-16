package lk.ijse.sams.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.sams.bo.BOFactory;
import lk.ijse.sams.bo.BOTypes;
import lk.ijse.sams.bo.custom.*;
import lk.ijse.sams.dto.*;
import lk.ijse.sams.entity.Attendance;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceController {

    @FXML private ComboBox<CourseDTO>       cboCourse;
    @FXML private ComboBox<SubjectDTO>      cboSubject;
    @FXML private ComboBox<ClassSessionDTO> cboSession;
    @FXML private Label lblSessionInfo;
    @FXML private Label lblPresent, lblAbsent, lblLate, lblTotal;
    @FXML private Button btnSaveAttendance;

    @FXML private TableView<AttendanceDTO>              tblAttendance;
    @FXML private TableColumn<AttendanceDTO, String>    colStudentId, colRegNo, colName, colRemarks;
    @FXML private TableColumn<AttendanceDTO, String>    colStatus;

    private final AttendanceBO    attendanceBO = BOFactory.getInstance().getBO(BOTypes.ATTENDANCE);
    private final CourseBO        courseBO     = BOFactory.getInstance().getBO(BOTypes.COURSE);
    private final SubjectBO       subjectBO    = BOFactory.getInstance().getBO(BOTypes.SUBJECT);
    private final ClassSessionBO  sessionBO    = BOFactory.getInstance().getBO(BOTypes.CLASS_SESSION);
    private final StudentBO       studentBO    = BOFactory.getInstance().getBO(BOTypes.STUDENT);

    private ObservableList<AttendanceDTO> attendanceList;

    @FXML
    public void initialize() {
        setupColumns();
        loadCourses();
        setupComboDisplays();
    }

    private void setupComboDisplays() {
        cboCourse.setCellFactory(lv -> comboCell(CourseDTO::getCourseName, null));
        cboCourse.setButtonCell(comboCell(CourseDTO::getCourseName, null));

        cboSubject.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(SubjectDTO item, boolean empty) {
                super.updateItem(item, empty); setText(empty || item == null ? "" : item.getSubjectName()); }});
        cboSubject.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(SubjectDTO item, boolean empty) {
                super.updateItem(item, empty); setText(empty || item == null ? "" : item.getSubjectName()); }});

        cboSession.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(ClassSessionDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getSessionDate() + " — " + item.getVenue()); }});
        cboSession.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(ClassSessionDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getSessionDate() + " — " + item.getVenue()); }});
    }

    @SuppressWarnings("unchecked")
    private <T> ListCell<T> comboCell(java.util.function.Function<T, String> getText, Object unused) {
        return (ListCell<T>) new ListCell<CourseDTO>() {
            @Override protected void updateItem(CourseDTO item, boolean empty) {
                super.updateItem(item, empty); setText(empty || item == null ? "" : item.getCourseName()); }};
    }

    private void setupColumns() {
        colStudentId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentId()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));
        colRemarks.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getRemarks() != null ? c.getValue().getRemarks() : ""));

        // Reg number from student list - use studentId as proxy
        if (colRegNo != null) {
            colRegNo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentId()));
        }

        // Status column with ComboBox
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStatus() != null ? c.getValue().getStatus().name() : "ABSENT"));

        colStatus.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<String> combo = new ComboBox<>(
                    FXCollections.observableArrayList("PRESENT", "ABSENT", "LATE", "EXCUSED"));
            {
                combo.setStyle("-fx-font-size: 12px;");
                combo.setOnAction(e -> {
                    AttendanceDTO dto = getTableView().getItems().get(getIndex());
                    dto.setStatus(Attendance.AttendanceStatus.valueOf(combo.getValue()));
                    updateSummary();
                });
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                combo.setValue(item != null ? item : "ABSENT");
                setGraphic(combo);
            }
        });

        // Editable remarks
        colRemarks.setCellFactory(col -> new TableCell<>() {
            private final TextField tf = new TextField();
            {
                tf.setStyle("-fx-font-size: 12px; -fx-border-color: #fecaca; -fx-border-radius: 4; -fx-background-radius: 4;");
                tf.focusedProperty().addListener((obs, old, focused) -> {
                    if (!focused && getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                        getTableView().getItems().get(getIndex()).setRemarks(tf.getText());
                    }
                });
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                tf.setText(item != null ? item : "");
                setGraphic(tf);
            }
        });
    }

    private void loadCourses() {
        cboCourse.setItems(FXCollections.observableArrayList(courseBO.getAllCourses()));
    }

    @FXML
    public void onCourseSelected(ActionEvent event) {
        CourseDTO course = cboCourse.getValue();
        if (course == null) return;
        List<SubjectDTO> subjects = subjectBO.getSubjectsByCourse(course.getCourseId());
        cboSubject.setItems(FXCollections.observableArrayList(subjects));
        cboSubject.getSelectionModel().clearSelection();
        cboSession.getItems().clear();
        attendanceList = FXCollections.observableArrayList();
        tblAttendance.setItems(attendanceList);
    }

    @FXML
    public void onSubjectSelected(ActionEvent event) {
        SubjectDTO subject = cboSubject.getValue();
        if (subject == null) return;
        List<ClassSessionDTO> sessions = sessionBO.getSessionsBySubject(subject.getSubjectId());
        cboSession.setItems(FXCollections.observableArrayList(sessions));
        cboSession.getSelectionModel().clearSelection();
    }

    @FXML
    public void onSessionSelected(ActionEvent event) {
        ClassSessionDTO session = cboSession.getValue();
        if (session == null) return;

        lblSessionInfo.setText(session.getSubjectName() + " | " + session.getSessionDate());

        // Load all students for this course
        CourseDTO course = cboCourse.getValue();
        if (course == null) return;
        List<StudentDTO> students = studentBO.getAllStudents().stream()
                .filter(s -> course.getCourseId().equals(s.getCourseId()))
                .toList();

        // Load existing attendance records for this session
        List<AttendanceDTO> existing = attendanceBO.getAttendanceBySession(session.getSessionId());

        // Build attendance rows — one per student
        List<AttendanceDTO> rows = new ArrayList<>();
        for (StudentDTO student : students) {
            AttendanceDTO row = existing.stream()
                    .filter(a -> a.getStudentId().equals(student.getStudentId()))
                    .findFirst()
                    .orElseGet(() -> {
                        AttendanceDTO a = new AttendanceDTO();
                        a.setAttendanceId(attendanceBO.generateNextId());
                        a.setStudentId(student.getStudentId());
                        a.setStudentName(student.getName());
                        a.setSessionId(session.getSessionId());
                        a.setStatus(Attendance.AttendanceStatus.ABSENT);
                        a.setMarkedDate(Date.valueOf(LocalDate.now()));
                        return a;
                    });
            row.setStudentName(student.getName()); // Always refresh name
            rows.add(row);
        }
        attendanceList = FXCollections.observableArrayList(rows);
        tblAttendance.setItems(attendanceList);
        updateSummary();
    }

    @FXML
    public void markAllPresent(ActionEvent event) {
        if (attendanceList == null) return;
        attendanceList.forEach(a -> a.setStatus(Attendance.AttendanceStatus.PRESENT));
        tblAttendance.refresh();
        updateSummary();
    }

    @FXML
    public void markAllAbsent(ActionEvent event) {
        if (attendanceList == null) return;
        attendanceList.forEach(a -> a.setStatus(Attendance.AttendanceStatus.ABSENT));
        tblAttendance.refresh();
        updateSummary();
    }

    @FXML
    public void handleSaveAttendance(ActionEvent event) {
        if (attendanceList == null || attendanceList.isEmpty()) return;
        try {
            for (AttendanceDTO dto : attendanceList) {
                attendanceBO.markAttendance(dto);
            }
            showAlert("Success", "Attendance saved for all " + attendanceList.size() + " students!", Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            showAlert("Error", "Failed to save attendance: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateSummary() {
        if (attendanceList == null) return;
        long present = attendanceList.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT).count();
        long absent  = attendanceList.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.ABSENT).count();
        long late    = attendanceList.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.LATE).count();
        lblPresent.setText("Present: " + present);
        lblAbsent.setText("Absent: " + absent);
        lblLate.setText("Late: " + late);
        lblTotal.setText("Total: " + attendanceList.size());
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
