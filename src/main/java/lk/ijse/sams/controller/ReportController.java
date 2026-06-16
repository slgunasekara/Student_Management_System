package lk.ijse.sams.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import lk.ijse.sams.bo.BOFactory;
import lk.ijse.sams.bo.BOTypes;
import lk.ijse.sams.bo.custom.*;
import lk.ijse.sams.dto.*;
import lk.ijse.sams.entity.Attendance;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportController {

    @FXML private ComboBox<String>      cboReportType;
    @FXML private ComboBox<CourseDTO>   cboCourse;
    @FXML private ComboBox<StudentDTO>  cboStudent;
    @FXML private DatePicker            dpFrom, dpTo;
    @FXML private Label lblTotalSessions, lblPresentCount, lblAbsentCount, lblAttendancePercent;

    @FXML private TableView<ReportRow>              tblReport;
    @FXML private TableColumn<ReportRow, String>    colStudentId, colRegNo, colName, colCourse,
                                                     colSubject, colDate, colStatus, colPercent;

    private final AttendanceBO   attendanceBO = BOFactory.getInstance().getBO(BOTypes.ATTENDANCE);
    private final CourseBO       courseBO     = BOFactory.getInstance().getBO(BOTypes.COURSE);
    private final StudentBO      studentBO    = BOFactory.getInstance().getBO(BOTypes.STUDENT);
    private final SubjectBO      subjectBO    = BOFactory.getInstance().getBO(BOTypes.SUBJECT);

    private ObservableList<ReportRow> reportData;

    @FXML
    public void initialize() {
        setupColumns();

        cboReportType.setItems(FXCollections.observableArrayList(
                "All Attendance", "By Student", "By Course", "By Date Range"));
        cboReportType.setValue("All Attendance");

        cboCourse.setItems(FXCollections.observableArrayList(courseBO.getAllCourses()));
        cboCourse.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(CourseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getCourseName());
            }
        });
        cboCourse.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(CourseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getCourseName());
            }
        });

        cboStudent.setItems(FXCollections.observableArrayList(studentBO.getAllStudents()));
        cboStudent.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(StudentDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName() + " (" + item.getRegNumber() + ")");
            }
        });
        cboStudent.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(StudentDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName() + " (" + item.getRegNumber() + ")");
            }
        });

        // Set default date range (last 30 days)
        dpTo.setValue(LocalDate.now());
        dpFrom.setValue(LocalDate.now().minusDays(30));

        reportData = FXCollections.observableArrayList();
        tblReport.setItems(reportData);
    }

    @FXML
    public void onReportTypeChanged(ActionEvent event) {
        // show/hide filters based on type – handled in FXML via visible binding for simplicity
    }

    @FXML
    public void onCourseChanged(ActionEvent event) {
        CourseDTO selected = cboCourse.getValue();
        if (selected != null) {
            List<StudentDTO> students = studentBO.getAllStudents().stream()
                    .filter(s -> selected.getCourseId().equals(s.getCourseId()))
                    .toList();
            cboStudent.setItems(FXCollections.observableArrayList(students));
        }
    }

    @FXML
    public void generateReport(ActionEvent event) {
        reportData.clear();

        String reportType = cboReportType.getValue();
        List<AttendanceDTO> allAttendance;

        try {
            if ("By Student".equals(reportType) && cboStudent.getValue() != null) {
                StudentDTO stu = cboStudent.getValue();
                Date from = dpFrom.getValue() != null ? Date.valueOf(dpFrom.getValue()) : Date.valueOf(LocalDate.now().minusDays(90));
                Date to   = dpTo.getValue()   != null ? Date.valueOf(dpTo.getValue())   : Date.valueOf(LocalDate.now());
                allAttendance = attendanceBO.getAttendanceByStudentDateRange(stu.getStudentId(), from, to);
            } else if ("By Date Range".equals(reportType)) {
                Date from = dpFrom.getValue() != null ? Date.valueOf(dpFrom.getValue()) : Date.valueOf(LocalDate.now().minusDays(30));
                Date to   = dpTo.getValue()   != null ? Date.valueOf(dpTo.getValue())   : Date.valueOf(LocalDate.now());
                allAttendance = attendanceBO.getAttendanceByStudentDateRange(null, from, to);
            } else {
                allAttendance = attendanceBO.getAttendanceByStudent(
                        cboStudent.getValue() != null ? cboStudent.getValue().getStudentId() : "__ALL__");
                if ("All Attendance".equals(reportType)) {
                    // get all students' attendance
                    allAttendance = new ArrayList<>();
                    for (StudentDTO student : studentBO.getAllStudents()) {
                        allAttendance.addAll(attendanceBO.getAttendanceByStudent(student.getStudentId()));
                    }
                }
            }

            // Build report rows
            for (AttendanceDTO att : allAttendance) {
                StudentDTO student = studentBO.findById(att.getStudentId()).orElse(null);
                if (student == null) continue;

                // Filter by course if selected
                if (cboCourse.getValue() != null &&
                        !cboCourse.getValue().getCourseId().equals(student.getCourseId())) continue;

                ReportRow row = new ReportRow();
                row.studentId   = att.getStudentId();
                row.regNo       = student.getRegNumber();
                row.name        = student.getName();
                row.courseName  = student.getCourseName() != null ? student.getCourseName() : "";
                row.subject     = "";  // populated if needed
                row.date        = att.getMarkedDate() != null ? att.getMarkedDate().toString() : "";
                row.status      = att.getStatus() != null ? att.getStatus().name() : "";
                row.percent     = "";  // calculated below
                reportData.add(row);
            }

            // Calculate percentage for each student+subject combo visible
            for (ReportRow row : reportData) {
                // Simple aggregate: count present / total per student across all subjects
                long present = allAttendance.stream()
                        .filter(a -> a.getStudentId().equals(row.studentId)
                                && (a.getStatus() == Attendance.AttendanceStatus.PRESENT
                                    || a.getStatus() == Attendance.AttendanceStatus.LATE))
                        .count();
                long total = allAttendance.stream()
                        .filter(a -> a.getStudentId().equals(row.studentId))
                        .count();
                row.percent = total > 0 ? String.format("%.1f%%", (double) present / total * 100) : "N/A";
            }

            updateSummary(allAttendance);
            tblReport.refresh();

        } catch (Exception ex) {
            showAlert("Report Error", ex.getMessage(), Alert.AlertType.ERROR);
            ex.printStackTrace();
        }
    }

    private void updateSummary(List<AttendanceDTO> data) {
        lblTotalSessions.setText(String.valueOf(data.size()));
        long present = data.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT ||
                                                  a.getStatus() == Attendance.AttendanceStatus.LATE).count();
        long absent  = data.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.ABSENT).count();
        lblPresentCount.setText(String.valueOf(present));
        lblAbsentCount.setText(String.valueOf(absent));
        double pct = data.isEmpty() ? 0 : (double) present / data.size() * 100;
        lblAttendancePercent.setText(String.format("%.1f%%", pct));
    }

    @FXML
    public void exportReport(ActionEvent event) {
        if (reportData == null || reportData.isEmpty()) {
            showAlert("Export", "No data to export. Please generate a report first.", Alert.AlertType.WARNING);
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Report as CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fc.setInitialFileName("attendance_report_" + LocalDate.now() + ".csv");
        File file = fc.showSaveDialog(tblReport.getScene().getWindow());
        if (file == null) return;

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("Student ID,Reg No,Name,Course,Subject,Date,Status,Attendance %");
            for (ReportRow row : reportData) {
                pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        row.studentId, row.regNo, row.name, row.courseName,
                        row.subject, row.date, row.status, row.percent);
            }
            showAlert("Export Successful", "Report saved to:\n" + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            showAlert("Export Failed", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupColumns() {
        colStudentId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().studentId));
        colRegNo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().regNo));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name));
        colCourse.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().courseName));
        colSubject.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().subject));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().date));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status));
        colPercent.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().percent));

        // Color-code status column
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item);
                if (!empty && item != null) {
                    setStyle(switch (item) {
                        case "PRESENT"  -> "-fx-text-fill: #16a34a; -fx-font-weight: bold;";
                        case "ABSENT"   -> "-fx-text-fill: #dc2626; -fx-font-weight: bold;";
                        case "LATE"     -> "-fx-text-fill: #d97706; -fx-font-weight: bold;";
                        case "EXCUSED"  -> "-fx-text-fill: #6b7280; -fx-font-weight: bold;";
                        default         -> "";
                    });
                }
            }
        });
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    // Inner class for report table model
    public static class ReportRow {
        public String studentId = "", regNo = "", name = "", courseName = "",
                      subject = "", date = "", status = "", percent = "";
    }
}
