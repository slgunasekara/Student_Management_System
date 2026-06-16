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
import lk.ijse.sams.bo.exception.ValidationException;
import lk.ijse.sams.dto.*;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class ClassSessionController {

    @FXML private TextField  txtSessionId, txtStartTime, txtEndTime, txtVenue;
    @FXML private ComboBox<CourseDTO>   cboCourse;
    @FXML private ComboBox<SubjectDTO>  cboSubject;
    @FXML private ComboBox<LecturerDTO> cboLecturer;
    @FXML private DatePicker dpSessionDate;
    @FXML private Label      lblError;
    @FXML private Button     btnSave, btnUpdate, btnDelete;

    @FXML private TableView<ClassSessionDTO>              tblSessions;
    @FXML private TableColumn<ClassSessionDTO, String>    colId, colDate, colSubject, colLecturer, colVenue, colStart, colEnd;

    private final ClassSessionBO sessionBO  = BOFactory.getInstance().getBO(BOTypes.CLASS_SESSION);
    private final CourseBO       courseBO   = BOFactory.getInstance().getBO(BOTypes.COURSE);
    private final SubjectBO      subjectBO  = BOFactory.getInstance().getBO(BOTypes.SUBJECT);
    private final LecturerBO     lecturerBO = BOFactory.getInstance().getBO(BOTypes.LECTURER);
    private ObservableList<ClassSessionDTO> masterList;

    @FXML
    public void initialize() {
        setupColumns();
        loadCourses();
        loadLecturers();
        loadTable();
        generateId();

        setupComboDisplays();

        tblSessions.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });
    }

    private void setupComboDisplays() {
        cboCourse.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(CourseDTO item, boolean empty) {
                super.updateItem(item, empty); setText(empty || item == null ? "" : item.getCourseName()); }});
        cboCourse.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(CourseDTO item, boolean empty) {
                super.updateItem(item, empty); setText(empty || item == null ? "" : item.getCourseName()); }});
        cboSubject.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(SubjectDTO item, boolean empty) {
                super.updateItem(item, empty); setText(empty || item == null ? "" : item.getSubjectName()); }});
        cboSubject.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(SubjectDTO item, boolean empty) {
                super.updateItem(item, empty); setText(empty || item == null ? "" : item.getSubjectName()); }});
        cboLecturer.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(LecturerDTO item, boolean empty) {
                super.updateItem(item, empty); setText(empty || item == null ? "" : item.getName()); }});
        cboLecturer.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(LecturerDTO item, boolean empty) {
                super.updateItem(item, empty); setText(empty || item == null ? "" : item.getName()); }});
    }

    private void setupColumns() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSessionId()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getSessionDate())));
        colSubject.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubjectName()));
        colLecturer.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLecturerName()));
        colVenue.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVenue()));
        colStart.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStartTime())));
        colEnd.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getEndTime())));
    }

    private void loadCourses() {
        cboCourse.setItems(FXCollections.observableArrayList(courseBO.getAllCourses()));
    }

    private void loadLecturers() {
        cboLecturer.setItems(FXCollections.observableArrayList(lecturerBO.getAllLecturers()));
    }

    @FXML
    public void onCourseSelected(ActionEvent event) {
        CourseDTO selected = cboCourse.getValue();
        if (selected == null) return;
        List<SubjectDTO> subjects = subjectBO.getSubjectsByCourse(selected.getCourseId());
        cboSubject.setItems(FXCollections.observableArrayList(subjects));
    }

    private void loadTable() {
        masterList = FXCollections.observableArrayList(sessionBO.getAllSessions());
        tblSessions.setItems(masterList);
    }

    private void generateId() {
        txtSessionId.setText(sessionBO.generateNextId());
    }

    @FXML public void handleNew(ActionEvent event) { handleClear(event); }

    @FXML
    public void handleSave(ActionEvent event) {
        lblError.setText("");
        try {
            sessionBO.saveSession(buildDTO());
            showSuccess("Session scheduled!");
            loadTable(); handleClear(null);
        } catch (ValidationException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    public void handleUpdate(ActionEvent event) {
        try {
            sessionBO.updateSession(buildDTO());
            showSuccess("Session updated!");
            loadTable(); handleClear(null);
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        ClassSessionDTO sel = tblSessions.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        new Alert(Alert.AlertType.CONFIRMATION, "Delete this session?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(b -> { if (b == ButtonType.YES) { sessionBO.deleteSession(sel.getSessionId()); loadTable(); handleClear(null); } });
    }

    @FXML
    public void handleClear(ActionEvent event) {
        cboCourse.getSelectionModel().clearSelection();
        cboSubject.getSelectionModel().clearSelection();
        cboLecturer.getSelectionModel().clearSelection();
        dpSessionDate.setValue(null);
        txtStartTime.clear(); txtEndTime.clear(); txtVenue.clear();
        lblError.setText(""); btnSave.setDisable(false); btnUpdate.setDisable(true); btnDelete.setDisable(true);
        tblSessions.getSelectionModel().clearSelection();
        generateId();
    }

    private void populateForm(ClassSessionDTO dto) {
        txtSessionId.setText(dto.getSessionId());
        if (dto.getSessionDate() != null) dpSessionDate.setValue(dto.getSessionDate().toLocalDate());
        txtStartTime.setText(dto.getStartTime() != null ? dto.getStartTime().toString().substring(0,5) : "");
        txtEndTime.setText(dto.getEndTime() != null ? dto.getEndTime().toString().substring(0,5) : "");
        txtVenue.setText(dto.getVenue() != null ? dto.getVenue() : "");
        cboSubject.getItems().stream().filter(s -> s.getSubjectId().equals(dto.getSubjectId()))
                .findFirst().ifPresent(cboSubject::setValue);
        cboLecturer.getItems().stream().filter(l -> l.getLecturerId().equals(dto.getLecturerId()))
                .findFirst().ifPresent(cboLecturer::setValue);
        btnSave.setDisable(true); btnUpdate.setDisable(false); btnDelete.setDisable(false);
    }

    private ClassSessionDTO buildDTO() {
        if (dpSessionDate.getValue() == null) throw new ValidationException("Session date is required!");
        if (cboSubject.getValue() == null)    throw new ValidationException("Subject is required!");
        if (cboLecturer.getValue() == null)   throw new ValidationException("Lecturer is required!");

        ClassSessionDTO dto = new ClassSessionDTO();
        dto.setSessionId(txtSessionId.getText().trim());
        dto.setSessionDate(Date.valueOf(dpSessionDate.getValue()));
        dto.setVenue(txtVenue.getText().trim());
        dto.setSubjectId(cboSubject.getValue().getSubjectId());
        dto.setLecturerId(cboLecturer.getValue().getLecturerId());

        try { dto.setStartTime(Time.valueOf(txtStartTime.getText().trim() + ":00")); } catch (Exception ignored) {}
        try { dto.setEndTime(Time.valueOf(txtEndTime.getText().trim() + ":00")); }   catch (Exception ignored) {}
        return dto;
    }

    private void showError(String msg)   { lblError.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11px;"); lblError.setText("⚠ " + msg); }
    private void showSuccess(String msg) { lblError.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12px;"); lblError.setText("✔ " + msg); }
}
