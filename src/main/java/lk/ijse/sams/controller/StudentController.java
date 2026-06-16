package lk.ijse.sams.controller;

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
import lk.ijse.sams.bo.custom.StudentBO;
import lk.ijse.sams.bo.exception.DuplicateException;
import lk.ijse.sams.bo.exception.ValidationException;
import lk.ijse.sams.dto.CourseDTO;
import lk.ijse.sams.dto.StudentDTO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.sql.Date;
import java.util.List;

public class StudentController {

    @FXML private TextField  txtStudentId, txtRegNumber, txtName, txtEmail, txtPhone, txtAddress;
    @FXML private DatePicker dpDob;
    @FXML private ComboBox<CourseDTO> cboCourse;
    @FXML private Label      lblError, lblCount;
    @FXML private Button     btnSave, btnUpdate, btnDelete;
    @FXML private TextField  txtSearch;

    @FXML private TableView<StudentDTO>           tblStudents;
    @FXML private TableColumn<StudentDTO, String> colStudentId, colRegNumber, colName, colEmail, colPhone, colCourse;

    private final StudentBO studentBO = BOFactory.getInstance().getBO(BOTypes.STUDENT);
    private final CourseBO  courseBO  = BOFactory.getInstance().getBO(BOTypes.COURSE);
    private ObservableList<StudentDTO> masterList;

    @FXML
    public void initialize() {
        setupColumns();
        loadCourses();
        loadTable();
        setupSearch();
        generateIds();

        tblStudents.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });

        // ComboBox display
        cboCourse.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(CourseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getCourseName() + " (" + item.getCourseCode() + ")");
            }
        });
        cboCourse.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(CourseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getCourseName() + " (" + item.getCourseCode() + ")");
            }
        });
    }

    private void setupColumns() {
        colStudentId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentId()));
        colRegNumber.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRegNumber()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        colCourse.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCourseName()));
    }

    private void loadCourses() {
        cboCourse.setItems(FXCollections.observableArrayList(courseBO.getAllCourses()));
    }

    private void loadTable() {
        List<StudentDTO> list = studentBO.getAllStudents();
        masterList = FXCollections.observableArrayList(list);
        tblStudents.setItems(masterList);
        lblCount.setText(masterList.size() + " records");
    }

    private void setupSearch() {
        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, old, q) -> {
                if (masterList == null) return;
                if (q == null || q.isBlank()) { tblStudents.setItems(masterList); return; }
                String query = q.toLowerCase();
                tblStudents.setItems(masterList.filtered(s ->
                        s.getName().toLowerCase().contains(query) ||
                        s.getRegNumber().toLowerCase().contains(query) ||
                        (s.getEmail() != null && s.getEmail().toLowerCase().contains(query))));
            });
        }
    }

    private void generateIds() {
        txtStudentId.setText(studentBO.generateNextId());
        txtRegNumber.setText(studentBO.generateNextRegNumber());
    }

    @FXML public void handleNew(ActionEvent event) { handleClear(event); }

    @FXML
    public void handleSave(ActionEvent event) {
        lblError.setText("");
        try {
            StudentDTO dto = buildDTO();
            studentBO.saveStudent(dto);
            showSuccess("Student registered successfully!");
            loadTable();
            handleClear(null);
        } catch (ValidationException | DuplicateException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    public void handleUpdate(ActionEvent event) {
        lblError.setText("");
        try {
            StudentDTO dto = buildDTO();
            studentBO.updateStudent(dto);
            showSuccess("Student updated!");
            loadTable();
            handleClear(null);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        StudentDTO sel = tblStudents.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete student '" + sel.getName() + "'?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Delete");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                studentBO.deleteStudent(sel.getStudentId());
                loadTable();
                handleClear(null);
            }
        });
    }

    @FXML
    public void handleClear(ActionEvent event) {
        txtName.clear(); txtEmail.clear(); txtPhone.clear();
        txtAddress.clear(); dpDob.setValue(null);
        cboCourse.getSelectionModel().clearSelection();
        lblError.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11px;");
        lblError.setText("");
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        tblStudents.getSelectionModel().clearSelection();
        generateIds();
    }

    private void populateForm(StudentDTO dto) {
        txtStudentId.setText(dto.getStudentId());
        txtRegNumber.setText(dto.getRegNumber());
        txtName.setText(dto.getName());
        txtEmail.setText(dto.getEmail() != null ? dto.getEmail() : "");
        txtPhone.setText(dto.getPhone());
        txtAddress.setText(dto.getAddress() != null ? dto.getAddress() : "");
        if (dto.getDateOfBirth() != null)
            dpDob.setValue(dto.getDateOfBirth().toLocalDate());
        // Select course
        cboCourse.getItems().stream()
                .filter(c -> c.getCourseId().equals(dto.getCourseId()))
                .findFirst()
                .ifPresent(cboCourse::setValue);
        btnSave.setDisable(true);
        btnUpdate.setDisable(false);
        btnDelete.setDisable(false);
    }

    private StudentDTO buildDTO() {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId(txtStudentId.getText().trim());
        dto.setRegNumber(txtRegNumber.getText().trim());
        dto.setName(txtName.getText().trim());
        dto.setEmail(txtEmail.getText().trim());
        dto.setPhone(txtPhone.getText().trim());
        dto.setAddress(txtAddress.getText().trim());
        if (dpDob.getValue() != null)
            dto.setDateOfBirth(Date.valueOf(dpDob.getValue()));
        CourseDTO course = cboCourse.getValue();
        if (course != null) {
            dto.setCourseId(course.getCourseId());
            dto.setCourseName(course.getCourseName());
        }
        return dto;
    }

    private void showError(String msg) {
        lblError.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11px;");
        lblError.setText("⚠ " + msg);
    }

    private void showSuccess(String msg) {
        lblError.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12px;");
        lblError.setText("✔ " + msg);
    }
}
