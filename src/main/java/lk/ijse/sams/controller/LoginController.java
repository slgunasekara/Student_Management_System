package lk.ijse.sams.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.sams.AppInitializer;
import lk.ijse.sams.bo.BOFactory;
import lk.ijse.sams.bo.BOTypes;
import lk.ijse.sams.bo.custom.UserBO;
import lk.ijse.sams.bo.exception.LoginException;
import lk.ijse.sams.dto.UserDTO;
import lk.ijse.sams.util.SessionManager;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField pfPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Button btnLogin;
    @FXML private Button btnTogglePassword;
    @FXML private Label lblError;
    @FXML private Label lblUsernameError;
    @FXML private Label lblPasswordError;

    private boolean passwordVisible = false;
    private final UserBO userBO = BOFactory.getInstance().getBO(BOTypes.USER);

    @FXML
    public void initialize() {
        // Sync text fields
        txtPasswordVisible.textProperty().bindBidirectional(pfPassword.textProperty());

        // Enter key triggers login
        txtUsername.setOnAction(e -> pfPassword.requestFocus());
        pfPassword.setOnAction(e -> handleLogin(null));
        txtPasswordVisible.setOnAction(e -> handleLogin(null));

        // Live validation clear
        txtUsername.textProperty().addListener((o, oldV, newV) -> lblUsernameError.setText(""));
        pfPassword.textProperty().addListener((o, oldV, newV) -> lblPasswordError.setText(""));
    }

    @FXML
    public void handleTogglePassword(ActionEvent event) {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            pfPassword.setVisible(false);
            pfPassword.setManaged(false);
            btnTogglePassword.setText("🙈");
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        } else {
            pfPassword.setVisible(true);
            pfPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            btnTogglePassword.setText("👁");
            pfPassword.requestFocus();
            pfPassword.positionCaret(pfPassword.getText().length());
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        clearErrors();

        String username = txtUsername.getText().trim();
        String password = pfPassword.getText();

        boolean valid = true;
        if (username.isEmpty()) {
            lblUsernameError.setText("⚠ Username is required");
            valid = false;
        }
        if (password.isEmpty()) {
            lblPasswordError.setText("⚠ Password is required");
            valid = false;
        }
        if (!valid) return;

        try {
            btnLogin.setDisable(true);
            btnLogin.setText("Signing in...");

            UserDTO user = userBO.login(username, password);
            SessionManager.setCurrentUser(user);

            // Navigate to Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DashboardView.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            AppInitializer.attachFullscreenToggle(stage, scene);
            stage.setScene(scene);
            stage.setWidth(1280);
            stage.setHeight(768);
            stage.centerOnScreen();

        } catch (LoginException ex) {
            lblError.setText("⚠  " + ex.getMessage());
            lblError.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px; " +
                    "-fx-background-color: #fff5f5; -fx-background-radius: 8; -fx-padding: 8 14;");
        } catch (Exception ex) {
            lblError.setText("⚠  System error. Please try again.");
            ex.printStackTrace();
        } finally {
            btnLogin.setDisable(false);
            btnLogin.setText("Sign In  →");
        }
    }

    private void clearErrors() {
        lblError.setText("");
        lblUsernameError.setText("");
        lblPasswordError.setText("");
    }
}
