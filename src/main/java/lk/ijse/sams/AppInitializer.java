package lk.ijse.sams;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lk.ijse.sams.bo.BOFactory;
import lk.ijse.sams.bo.BOTypes;
import lk.ijse.sams.bo.custom.UserBO;
import lk.ijse.sams.dto.UserDTO;
import lk.ijse.sams.entity.User;


public class AppInitializer extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        seedDefaultAdmin();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
        Scene scene = new Scene(loader.load());

        attachFullscreenToggle(primaryStage, scene);

        primaryStage.setTitle("SAMS – Student Attendance Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void attachFullscreenToggle(Stage stage, Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
                e.consume();
            }
        });
        stage.sceneProperty().addListener((obs, old, newScene) -> {
            if (newScene != null && newScene != scene) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                    if (e.getCode() == KeyCode.F11) {
                        stage.setFullScreen(!stage.isFullScreen());
                        e.consume();
                    }
                });
            }
        });
    }

    private void seedDefaultAdmin() {
        try {
            UserBO userBO = BOFactory.getInstance().getBO(BOTypes.USER);
            if (userBO.getAllUsers().isEmpty()) {
                UserDTO admin = new UserDTO();
                admin.setUserId("U001");
                admin.setUsername("admin");
                admin.setPassword("Admin@1234");
                admin.setRole(User.UserRole.ADMIN);
                admin.setFullName("System Administrator");
                admin.setEmail("admin@sams.lk");
                admin.setActive(true);
                userBO.register(admin);
                System.out.println("Default admin created: admin / Admin@1234");
            }
        } catch (Exception e) {
            System.out.println("Admin seed skipped: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
