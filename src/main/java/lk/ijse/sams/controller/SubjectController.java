package lk.ijse.sams.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.sams.bo.BOFactory;
import lk.ijse.sams.bo.BOTypes;
import lk.ijse.sams.bo.custom.CourseBO;
import lk.ijse.sams.bo.custom.SubjectBO;
import lk.ijse.sams.bo.exception.DuplicateException;
import lk.ijse.sams.dto.CourseDTO;
import lk.ijse.sams.dto.SubjectDTO;

import java.util.List;

public class SubjectController {

    @FXML private TextField  txtSubjectId, txtSubjectName, txtSubjectCode, txtCredits;
    @FXML private TextArea   txtDescription;
    @FXML private ComboBox<CourseDTO> cboCourse;
    @FXML private Label      lblError;
    @FXML private Button     btnSave, btnUpdate, btnDelete;

    @FXML private TableView<SubjectDTO>             tblSubjects;
    @FXML private TableColumn<SubjectDTO, String>   colId, colName, colCode, colCourse;
    @FXML private TableColumn<SubjectDTO, Integer>  colCredits;

    private final SubjectBO subjectBO = BOFactory.getInstance().getBO(BOTypes.SUBJECT);
    private final CourseBO  courseBO  = BOFactory.getInstance().getBO(BOTypes.COURSE);
    private ObservableList<SubjectDTO> masterList;

    @FXML
    public void initialize() {
        setupColumns();
        loadCourses();
        loadTable();
        generateId();

        cboCourse.setCellFactory(lv -> courseCell());
        cboCourse.setButtonCell(courseCell());

        tblSubjects.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });
    }

    private ListCell<CourseDTO> courseCell() {
        return new ListCell<>() {
            @Override protected void updateItem(CourseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getCourseName());
            }
        };
    }

    private void setupColumns() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubjectId()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubjectName()));
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubjectCode()));
        colCredits.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCredits()).asObject());
        colCourse.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCourseName()));
    }

    private void loadCourses() {
        cboCourse.setItems(FXCollections.observableArrayList(courseBO.getAllCourses()));
    }

    private void loadTable() {
        masterList = FXCollections.observableArrayList(subjectBO.getAllSubjects());
        tblSubjects.setItems(masterList);
    }

    private void generateId() {
        txtSubjectId.setText(subjectBO.generateNextId());
    }

    @FXML public void handleNew(ActionEvent event) { handleClear(event); }

    @FXML
    public void handleSave(ActionEvent event) {
        lblError.setText("");
        try {
            subjectBO.saveSubject(buildDTO());
            showSuccess("Subject saved!");
            loadTable(); handleClear(null);
        } catch (DuplicateException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    public void handleUpdate(ActionEvent event) {
        try {
            subjectBO.updateSubject(buildDTO());
            showSuccess("Subject updated!");
            loadTable(); handleClear(null);
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        SubjectDTO sel = tblSubjects.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        new Alert(Alert.AlertType.CONFIRMATION, "Delete subject '" + sel.getSubjectName() + "'?",
                ButtonType.YES, ButtonType.NO).showAndWait()
                .ifPresent(b -> { if (b == ButtonType.YES) { subjectBO.deleteSubject(sel.getSubjectId()); loadTable(); handleClear(null); } });
    }

    @FXML
    public void handleClear(ActionEvent event) {
        txtSubjectName.clear(); txtSubjectCode.clear(); txtCredits.clear();
        txtDescription.clear(); cboCourse.getSelectionModel().clearSelection();
        lblError.setText(""); btnSave.setDisable(false); btnUpdate.setDisable(true); btnDelete.setDisable(true);
        tblSubjects.getSelectionModel().clearSelection();
        generateId();
    }

    private void populateForm(SubjectDTO dto) {
        txtSubjectId.setText(dto.getSubjectId());
        txtSubjectName.setText(dto.getSubjectName());
        txtSubjectCode.setText(dto.getSubjectCode());
        txtCredits.setText(String.valueOf(dto.getCredits()));
        txtDescription.setText(dto.getDescription() != null ? dto.getDescription() : "");
        cboCourse.getItems().stream().filter(c -> c.getCourseId().equals(dto.getCourseId()))
                .findFirst().ifPresent(cboCourse::setValue);
        btnSave.setDisable(true); btnUpdate.setDisable(false); btnDelete.setDisable(false);
    }

    private SubjectDTO buildDTO() {
        String name = txtSubjectName.getText().trim();
        String code = txtSubjectCode.getText().trim();
        if (name.isEmpty()) throw new IllegalArgumentException("Subject name is required!");
        if (code.isEmpty()) throw new IllegalArgumentException("Subject code is required!");
        if (cboCourse.getValue() == null) throw new IllegalArgumentException("Please select a course!");

        SubjectDTO dto = new SubjectDTO();
        dto.setSubjectId(txtSubjectId.getText().trim());
        dto.setSubjectName(name);
        dto.setSubjectCode(code);
        try { dto.setCredits(Integer.parseInt(txtCredits.getText().trim())); } catch (Exception ignored) {}
        dto.setDescription(txtDescription.getText().trim());
        dto.setCourseId(cboCourse.getValue().getCourseId());
        return dto;
    }

    private void showError(String msg)   { lblError.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11px;"); lblError.setText("⚠ " + msg); }
    private void showSuccess(String msg) { lblError.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12px;"); lblError.setText("✔ " + msg); }
}
