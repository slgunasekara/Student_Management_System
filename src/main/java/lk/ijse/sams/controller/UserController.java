package lk.ijse.sams.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.sams.bo.BOFactory;
import lk.ijse.sams.bo.BOTypes;
import lk.ijse.sams.bo.custom.UserBO;
import lk.ijse.sams.bo.exception.DuplicateException;
import lk.ijse.sams.dto.UserDTO;
import lk.ijse.sams.entity.User;

import java.util.List;

public class UserController {

    @FXML private TextField  txtUserId, txtFullName, txtUsername, txtEmail;
    @FXML private PasswordField pfPassword;
    @FXML private ComboBox<String> cboRole;
    @FXML private CheckBox   chkActive;
    @FXML private Label      lblError;
    @FXML private Button     btnSave, btnUpdate, btnDelete;

    @FXML private TableView<UserDTO>            tblUsers;
    @FXML private TableColumn<UserDTO, String>  colId, colFullName, colUsername, colEmail, colRole, colActive;

    private final UserBO userBO = BOFactory.getInstance().getBO(BOTypes.USER);
    private ObservableList<UserDTO> masterList;

    @FXML
    public void initialize() {
        cboRole.setItems(FXCollections.observableArrayList("ADMIN", "LECTURER"));
        cboRole.setValue("LECTURER");

        setupColumns();
        loadTable();
        generateId();

        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });
    }

    private void setupColumns() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUserId()));
        colFullName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));
        colUsername.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail() != null ? c.getValue().getEmail() : ""));
        colRole.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole().name()));
        colActive.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActive() ? "Active" : "Inactive"));
    }

    private void loadTable() {
        List<UserDTO> list = userBO.getAllUsers();
        masterList = FXCollections.observableArrayList(list);
        tblUsers.setItems(masterList);
    }

    private void generateId() {
        txtUserId.setText(userBO.generateNextId());
    }

    @FXML public void handleNew(ActionEvent event) { handleClear(event); }

    @FXML
    public void handleSave(ActionEvent event) {
        lblError.setText("");
        try {
            String pwd = pfPassword.getText();
            if (pwd.length() < 6) { showError("Password must be at least 6 characters!"); return; }

            UserDTO dto = buildDTO();
            dto.setPassword(pwd);
            userBO.register(dto);
            showSuccess("User created successfully!");
            loadTable();
            handleClear(null);
        } catch (DuplicateException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    @FXML
    public void handleUpdate(ActionEvent event) {
        try {
            UserDTO dto = buildDTO();
            String pwd = pfPassword.getText();
            if (!pwd.isEmpty()) dto.setPassword(pwd); // only update if provided
            userBO.updateUser(dto);
            showSuccess("User updated!");
            loadTable();
            handleClear(null);
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        UserDTO sel = tblUsers.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        new Alert(Alert.AlertType.CONFIRMATION,
                "Delete user '" + sel.getUsername() + "'?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(b -> {
                    if (b == ButtonType.YES) {
                        userBO.deleteUser(sel.getUserId());
                        loadTable();
                        handleClear(null);
                    }
                });
    }

    @FXML
    public void handleClear(ActionEvent event) {
        txtFullName.clear(); txtUsername.clear(); txtEmail.clear(); pfPassword.clear();
        cboRole.setValue("LECTURER"); chkActive.setSelected(true);
        lblError.setText("");
        btnSave.setDisable(false); btnUpdate.setDisable(true); btnDelete.setDisable(true);
        tblUsers.getSelectionModel().clearSelection();
        generateId();
    }

    private void populateForm(UserDTO dto) {
        txtUserId.setText(dto.getUserId());
        txtFullName.setText(dto.getFullName());
        txtUsername.setText(dto.getUsername());
        txtEmail.setText(dto.getEmail() != null ? dto.getEmail() : "");
        pfPassword.clear();
        cboRole.setValue(dto.getRole().name());
        chkActive.setSelected(dto.isActive());
        btnSave.setDisable(true); btnUpdate.setDisable(false); btnDelete.setDisable(false);
    }

    private UserDTO buildDTO() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        if (fullName.isEmpty()) throw new IllegalArgumentException("Full name is required!");
        if (username.isEmpty()) throw new IllegalArgumentException("Username is required!");
        UserDTO dto = new UserDTO();
        dto.setUserId(txtUserId.getText().trim());
        dto.setFullName(fullName);
        dto.setUsername(username);
        dto.setEmail(txtEmail.getText().trim());
        dto.setRole(User.UserRole.valueOf(cboRole.getValue()));
        dto.setActive(chkActive.isSelected());
        return dto;
    }

    private void showError(String msg)   { lblError.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11px;"); lblError.setText("⚠ " + msg); }
    private void showSuccess(String msg) { lblError.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12px;"); lblError.setText("✔ " + msg); }
}
