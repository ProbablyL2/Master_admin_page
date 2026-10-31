package application;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TeacherCreatePromptController {

    @FXML private VBox rootPane;
    @FXML private ImageView logoImageView;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;

    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private Label eyeIcon1;
    private boolean isPasswordVisible = false;

    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordTextField;
    @FXML private Label eyeIcon2;
    private boolean isConfirmPasswordVisible = false;

    @FXML private Button createBtn;
    @FXML private Button backBtn;

    @FXML
    public void initialize() {
        passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());
        confirmPasswordField.textProperty().bindBidirectional(confirmPasswordTextField.textProperty());

        if (rootPane != null) {
            rootPane.requestFocus();
        }
    }

    @FXML
    public void handleBackgroundClick() {
        if (rootPane != null) {
            rootPane.requestFocus();
        }
    }

    @FXML
    public void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            eyeIcon1.setText("👁‍🗨");
        } else {
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            eyeIcon1.setText("👁");
        }
    }

    @FXML
    public void toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        if (isConfirmPasswordVisible) {
            confirmPasswordTextField.setVisible(true);
            confirmPasswordTextField.setManaged(true);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            eyeIcon2.setText("👁‍🗨");
        } else {
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            confirmPasswordTextField.setVisible(false);
            confirmPasswordTextField.setManaged(false);
            eyeIcon2.setText("👁");
        }
    }

    @FXML
    public void handleCreate() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        String confirmPassword = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Missing Information", "Please fill in all fields before proceeding.", Alert.AlertType.ERROR);
            return;
        }

        if (!email.contains("@")) {
            showAlert("Invalid Email", "Email must contain '@'.", Alert.AlertType.ERROR);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Password Mismatch", "Passwords do not match. Please check again.", Alert.AlertType.ERROR);
            return;
        }

        // Store account info temporarily in Session
        Session.setAccountData(username, email);

        // Open TeacherDetail in Add mode
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TeacherDetail.fxml"));
            Parent teacherAddView = loader.load();

            TeacherDetailController controller = loader.getController();
            controller.initAddMode();

            BorderPane mainLayout = (BorderPane) rootPane.getScene().lookup("#mainBorderPane");
            if (mainLayout != null) {
                mainLayout.setCenter(teacherAddView);
            } else {
                Parent root = rootPane.getScene().getRoot();
                if (root instanceof BorderPane) {
                    ((BorderPane) root).setCenter(teacherAddView);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBackToManage() {
        try {
            Parent teacherView = FXMLLoader.load(getClass().getResource("TeacherContents.fxml"));
            BorderPane mainLayout = (BorderPane) rootPane.getScene().lookup("#mainBorderPane");
            if (mainLayout != null) {
                mainLayout.setCenter(teacherView);
            } else {
                Parent root = rootPane.getScene().getRoot();
                if (root instanceof BorderPane) {
                    ((BorderPane) root).setCenter(teacherView);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinWidth(520);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        if (rootPane != null && rootPane.getScene() != null) {
            alert.initOwner(rootPane.getScene().getWindow());
        }
        alert.showAndWait();
    }
}
