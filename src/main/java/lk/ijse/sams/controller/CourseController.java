package lk.ijse.sams.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.sams.bo.BOFactory;
import lk.ijse.sams.bo.BOTypes;
import lk.ijse.sams.bo.custom.CourseBO;
import lk.ijse.sams.bo.exception.DuplicateException;
import lk.ijse.sams.bo.exception.NotFoundException;
import lk.ijse.sams.dto.CourseDTO;

import java.util.List;

public class CourseController {

    @FXML private TextField txtCourseId, txtCourseName, txtCourseCode, txtDuration;
    @FXML private TextArea  txtDescription;
    @FXML private Label     lblError, lblCount;
    @FXML private Button    btnSave, btnUpdate, btnDelete;
    @FXML private TextField txtSearch;

    @FXML private TableView<CourseDTO>              tblCourses;
    @FXML private TableColumn<CourseDTO, String>    colCourseId, colCourseName, colCourseCode, colDesc;
    @FXML private TableColumn<CourseDTO, Integer>   colDuration;

    private final CourseBO courseBO = BOFactory.getInstance().getBO(BOTypes.COURSE);
    private ObservableList<CourseDTO> masterList;

    @FXML
    public void initialize() {
        setupColumns();
        loadTable();
        setupSearch();
        generateId();

        tblCourses.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) populateForm(selected);
        });
    }

    private void setupColumns() {
        colCourseId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCourseId()));
        colCourseName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCourseName()));
        colCourseCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCourseCode()));
        colDuration.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getDurationMonths()).asObject());
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
    }

    private void loadTable() {
        List<CourseDTO> list = courseBO.getAllCourses();
        masterList = FXCollections.observableArrayList(list);
        tblCourses.setItems(masterList);
        lblCount.setText(masterList.size() + " records");
    }

    private void setupSearch() {
        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, old, query) -> {
                if (masterList == null) return;
                if (query == null || query.isBlank()) {
                    tblCourses.setItems(masterList);
                } else {
                    String q = query.toLowerCase();
                    FilteredList<CourseDTO> filtered = masterList.filtered(c ->
                            c.getCourseName().toLowerCase().contains(q) ||
                            c.getCourseCode().toLowerCase().contains(q));
                    tblCourses.setItems(filtered);
                }
            });
        }
    }

    private void generateId() {
        txtCourseId.setText(courseBO.generateNextId());
    }

    @FXML public void handleNew(ActionEvent event)    { handleClear(event); }

    @FXML
    public void handleSave(ActionEvent event) {
        lblError.setText("");
        try {
            CourseDTO dto = buildDTO();
            courseBO.saveCourse(dto);
            showSuccess("Course saved successfully!");
            loadTable();
            handleClear(null);
        } catch (DuplicateException | IllegalArgumentException ex) {
            lblError.setText("⚠ " + ex.getMessage());
        } catch (Exception ex) {
            lblError.setText("⚠ Unexpected error: " + ex.getMessage());
        }
    }

    @FXML
    public void handleUpdate(ActionEvent event) {
        lblError.setText("");
        try {
            CourseDTO dto = buildDTO();
            courseBO.updateCourse(dto);
            showSuccess("Course updated!");
            loadTable();
            handleClear(null);
        } catch (NotFoundException | IllegalArgumentException ex) {
            lblError.setText("⚠ " + ex.getMessage());
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        CourseDTO selected = tblCourses.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete course '" + selected.getCourseName() + "'?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Delete");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                courseBO.deleteCourse(selected.getCourseId());
                loadTable();
                handleClear(null);
            }
        });
    }

    @FXML
    public void handleClear(ActionEvent event) {
        txtCourseName.clear();
        txtCourseCode.clear();
        txtDuration.clear();
        txtDescription.clear();
        lblError.setText("");
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        tblCourses.getSelectionModel().clearSelection();
        generateId();
    }

    private void populateForm(CourseDTO dto) {
        txtCourseId.setText(dto.getCourseId());
        txtCourseName.setText(dto.getCourseName());
        txtCourseCode.setText(dto.getCourseCode());
        txtDuration.setText(String.valueOf(dto.getDurationMonths()));
        txtDescription.setText(dto.getDescription());
        btnSave.setDisable(true);
        btnUpdate.setDisable(false);
        btnDelete.setDisable(false);
    }

    private CourseDTO buildDTO() {
        String name = txtCourseName.getText().trim();
        String code = txtCourseCode.getText().trim();
        String durStr = txtDuration.getText().trim();
        if (name.isEmpty()) throw new IllegalArgumentException("Course name is required!");
        if (code.isEmpty()) throw new IllegalArgumentException("Course code is required!");
        int dur = 0;
        if (!durStr.isEmpty()) {
            try { dur = Integer.parseInt(durStr); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Duration must be a number!"); }
        }
        CourseDTO dto = new CourseDTO();
        dto.setCourseId(txtCourseId.getText().trim());
        dto.setCourseName(name);
        dto.setCourseCode(code);
        dto.setDurationMonths(dur);
        dto.setDescription(txtDescription.getText().trim());
        return dto;
    }

    private void showSuccess(String msg) {
        lblError.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12px;");
        lblError.setText("✔ " + msg);
    }
}
