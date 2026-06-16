package lk.ijse.sams.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.sams.bo.BOFactory;
import lk.ijse.sams.bo.BOTypes;
import lk.ijse.sams.bo.custom.LecturerBO;
import lk.ijse.sams.bo.exception.DuplicateException;
import lk.ijse.sams.bo.exception.ValidationException;
import lk.ijse.sams.dto.LecturerDTO;

import java.util.List;

public class LecturerController {

    @FXML private TextField  txtLecturerId, txtName, txtEmail, txtPhone, txtQualification, txtDepartment;
    @FXML private CheckBox   chkActive;
    @FXML private Label      lblError, lblCount;
    @FXML private Button     btnSave, btnUpdate, btnDelete;
    @FXML private TextField  txtSearch;

    @FXML private TableView<LecturerDTO>            tblLecturers;
    @FXML private TableColumn<LecturerDTO, String>  colId, colName, colEmail, colPhone, colQualification, colDepartment, colActive;

    private final LecturerBO lecturerBO = BOFactory.getInstance().getBO(BOTypes.LECTURER);
    private ObservableList<LecturerDTO> masterList;

    @FXML
    public void initialize() {
        setupColumns();
        loadTable();
        setupSearch();
        generateId();

        tblLecturers.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });
    }

    private void setupColumns() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLecturerId()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        colQualification.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getQualification()));
        colDepartment.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colActive.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActive() ? "Active" : "Inactive"));
    }

    private void loadTable() {
        List<LecturerDTO> list = lecturerBO.getAllLecturers();
        masterList = FXCollections.observableArrayList(list);
        tblLecturers.setItems(masterList);
        lblCount.setText(masterList.size() + " records");
    }

    private void setupSearch() {
        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, old, q) -> {
                if (masterList == null) return;
                if (q == null || q.isBlank()) { tblLecturers.setItems(masterList); return; }
                String query = q.toLowerCase();
                tblLecturers.setItems(masterList.filtered(l ->
                        l.getName().toLowerCase().contains(query) ||
                        l.getEmail().toLowerCase().contains(query)));
            });
        }
    }

    private void generateId() {
        txtLecturerId.setText(lecturerBO.generateNextId());
    }

    @FXML public void handleNew(ActionEvent event) { handleClear(event); }

    @FXML
    public void handleSave(ActionEvent event) {
        lblError.setText("");
        try {
            LecturerDTO dto = buildDTO();
            lecturerBO.saveLecturer(dto);
            showSuccess("Lecturer saved!");
            loadTable(); handleClear(null);
        } catch (ValidationException | DuplicateException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    @FXML
    public void handleUpdate(ActionEvent event) {
        try {
            lecturerBO.updateLecturer(buildDTO());
            showSuccess("Lecturer updated!");
            loadTable(); handleClear(null);
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        LecturerDTO sel = tblLecturers.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        new Alert(Alert.AlertType.CONFIRMATION, "Delete '" + sel.getName() + "'?",
                ButtonType.YES, ButtonType.NO).showAndWait()
                .ifPresent(b -> { if (b == ButtonType.YES) { lecturerBO.deleteLecturer(sel.getLecturerId()); loadTable(); handleClear(null); } });
    }

    @FXML
    public void handleClear(ActionEvent event) {
        txtName.clear(); txtEmail.clear(); txtPhone.clear();
        txtQualification.clear(); txtDepartment.clear();
        chkActive.setSelected(true);
        lblError.setText(""); lblError.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11px;");
        btnSave.setDisable(false); btnUpdate.setDisable(true); btnDelete.setDisable(true);
        tblLecturers.getSelectionModel().clearSelection();
        generateId();
    }

    private void populateForm(LecturerDTO dto) {
        txtLecturerId.setText(dto.getLecturerId());
        txtName.setText(dto.getName());
        txtEmail.setText(dto.getEmail());
        txtPhone.setText(dto.getPhone());
        txtQualification.setText(dto.getQualification() != null ? dto.getQualification() : "");
        txtDepartment.setText(dto.getDepartment() != null ? dto.getDepartment() : "");
        chkActive.setSelected(dto.isActive());
        btnSave.setDisable(true); btnUpdate.setDisable(false); btnDelete.setDisable(false);
    }

    private LecturerDTO buildDTO() {
        LecturerDTO dto = new LecturerDTO();
        dto.setLecturerId(txtLecturerId.getText().trim());
        dto.setName(txtName.getText().trim());
        dto.setEmail(txtEmail.getText().trim());
        dto.setPhone(txtPhone.getText().trim());
        dto.setQualification(txtQualification.getText().trim());
        dto.setDepartment(txtDepartment.getText().trim());
        dto.setActive(chkActive.isSelected());
        return dto;
    }

    private void showError(String msg) { lblError.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11px;"); lblError.setText("⚠ " + msg); }
    private void showSuccess(String msg) { lblError.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12px;"); lblError.setText("✔ " + msg); }
}
