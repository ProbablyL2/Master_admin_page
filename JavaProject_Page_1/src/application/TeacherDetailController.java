package application;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TeacherDetailController {

    @FXML private Label lblTeacherId, lblDob, lblName, lblGender, lblDept, lblNationality, lblPosition, lblNrc;
    @FXML private Label lblPhone, lblEmail, lblCurrentAddress, lblPermanentAddress;
    @FXML private Label lblDegree, lblUniversity, lblYearCompleted, lblSpecialization, lblEmergency;

    private TextField txtTeacherId, txtDob, txtName, txtGender, txtDept, txtNationality, txtPosition, txtNrc;
    private TextField txtPhone, txtEmail, txtCurrentAddress, txtPermanentAddress;
    private TextField txtDegree, txtUniversity, txtYearCompleted, txtSpecialization, txtEmergency;

    @FXML private ImageView passportImageView;
    @FXML private Button btnSave;
    @FXML private Button btnReset;
    @FXML private HBox topActionBar;
    @FXML private VBox contentContainer;

    private Teacher currentTeacher;
    private boolean isEditMode = false;

    public void initData(Teacher teacher) {
        initData(teacher, false);
    }

    public void initData(Teacher teacher, boolean editMode) {
        this.currentTeacher = teacher;
        this.isEditMode = editMode;

        if (contentContainer != null && !contentContainer.getChildren().isEmpty()) {
            HBox banner = new HBox();
            banner.setStyle("-fx-background-color: #dbeafe; -fx-border-color: #2563eb; -fx-border-width: 1.5; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 12 18;");
            
            String bannerText = String.format("⭐   %d     |     %s     |     %s     |     %s     |     %s",
                teacher.getNo(),
                teacher.getTeacherId(),
                teacher.getName(),
                teacher.getDepartment(),
                teacher.getPosition()
            );

            Label bannerLabel = new Label(bannerText);
            bannerLabel.setStyle("-fx-text-fill: #1e3a8a; -fx-font-weight: bold; -fx-font-size: 15px;");
            banner.getChildren().add(bannerLabel);
            contentContainer.getChildren().add(0, banner);
        }

        if (!isEditMode) {
            lblTeacherId.setText(teacher.getTeacherId());
            lblDob.setText(teacher.getDob());
            lblName.setText(teacher.getName());
            lblGender.setText(teacher.getGender());
            lblDept.setText(teacher.getDepartment());
            lblNationality.setText(teacher.getNationality());
            lblPosition.setText(teacher.getPosition());
            lblNrc.setText(teacher.getNrc());
            
            lblPhone.setText(teacher.getPhoneNo());
            lblEmail.setText(teacher.getEmail());
            lblCurrentAddress.setText(teacher.getCurrentAddress());
            lblPermanentAddress.setText(teacher.getPermanentAddress());
            
            lblDegree.setText(teacher.getDegree());
            lblUniversity.setText(teacher.getUniversity());
            lblYearCompleted.setText(teacher.getYearCompleted());
            lblSpecialization.setText(teacher.getSpecialization());
            lblEmergency.setText(teacher.getEmergencyContact());

            if (btnSave != null) btnSave.setVisible(false);
            if (btnReset != null) btnReset.setVisible(false);
        } else {
            replaceLabelWithTextField(lblTeacherId, txtTeacherId = new TextField(teacher.getTeacherId()));
            replaceLabelWithTextField(lblDob, txtDob = new TextField(teacher.getDob()));
            replaceLabelWithTextField(lblName, txtName = new TextField(teacher.getName()));
            replaceLabelWithTextField(lblGender, txtGender = new TextField(teacher.getGender()));
            replaceLabelWithTextField(lblDept, txtDept = new TextField(teacher.getDepartment()));
            replaceLabelWithTextField(lblNationality, txtNationality = new TextField(teacher.getNationality()));
            replaceLabelWithTextField(lblPosition, txtPosition = new TextField(teacher.getPosition()));
            replaceLabelWithTextField(lblNrc, txtNrc = new TextField(teacher.getNrc()));

            replaceLabelWithTextField(lblPhone, txtPhone = new TextField(teacher.getPhoneNo()));
            replaceLabelWithTextField(lblEmail, txtEmail = new TextField(teacher.getEmail()));
            replaceLabelWithTextField(lblCurrentAddress, txtCurrentAddress = new TextField(teacher.getCurrentAddress()));
            replaceLabelWithTextField(lblPermanentAddress, txtPermanentAddress = new TextField(teacher.getPermanentAddress()));

            replaceLabelWithTextField(lblDegree, txtDegree = new TextField(teacher.getDegree()));
            replaceLabelWithTextField(lblUniversity, txtUniversity = new TextField(teacher.getUniversity()));
            replaceLabelWithTextField(lblYearCompleted, txtYearCompleted = new TextField(teacher.getYearCompleted()));
            replaceLabelWithTextField(lblSpecialization, txtSpecialization = new TextField(teacher.getSpecialization()));
            replaceLabelWithTextField(lblEmergency, txtEmergency = new TextField(teacher.getEmergencyContact()));

            if (topActionBar != null && btnSave == null) {
                btnSave = new Button("Save Changes");
                btnSave.getStyleClass().add("search-button");
                btnSave.setPrefHeight(35);
                btnSave.setPrefWidth(120);
                btnSave.setOnAction(e -> handleSave());
                topActionBar.getChildren().add(btnSave);
            }
        }

        if (teacher.getPhotoPath() != null && !teacher.getPhotoPath().isEmpty()) {
            try {
                Image img = new Image(getClass().getResourceAsStream(teacher.getPhotoPath()));
                if (img != null) {
                    passportImageView.setImage(img);
                }
            } catch (Exception e) {}
        }
    }

    private void replaceLabelWithTextField(Label label, TextField textField) {
        if (label != null && label.getParent() instanceof GridPane) {
            GridPane grid = (GridPane) label.getParent();
            Integer col = GridPane.getColumnIndex(label);
            Integer row = GridPane.getRowIndex(label);

            grid.getChildren().remove(label);
            textField.setStyle("-fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-radius: 4;");
            grid.add(textField, col != null ? col : 0, row != null ? row : 0);
        }
    }

    @FXML
    public void handleSave() {
        if (currentTeacher != null && isEditMode) {
            // 1. Validate Department
            String deptInput = txtDept != null ? txtDept.getText().trim() : "";
            String correctedDept = null;
            String[] validDepts = {"CE", "ME", "EC", "EP", "CEIT", "MC", "ChE", "Arch", "PE", "MnE", "Met", "Tex", "FE"};
            for (String d : validDepts) {
                if (d.equalsIgnoreCase(deptInput)) {
                    correctedDept = d;
                    break;
                }
            }
            if (correctedDept == null) {
                showAlert("Invalid Department", "Please input a valid Department:\nCE, ME, EC, EP, CEIT, MC, ChE, Arch, PE, MnE, Met, Tex, FE.", Alert.AlertType.ERROR);
                return;
            }

            // 2. Validate Position
            String posInput = txtPosition != null ? txtPosition.getText().trim() : "";
            String correctedPos = null;
            String[] validPositions = {"Professor & Head", "Professor", "Associate Professor", "Lecturer", "Assistant Lecturer", "Tutor"};
            for (String p : validPositions) {
                if (p.equalsIgnoreCase(posInput)) {
                    correctedPos = p;
                    break;
                }
            }
            if (correctedPos == null) {
                showAlert("Invalid Position", "Please input a valid Position:\nProfessor & Head, Professor, Associate Professor, Lecturer, Assistant Lecturer, Tutor.", Alert.AlertType.ERROR);
                return;
            }

            // 3. Validate Phone Number
            String phoneInput = txtPhone != null ? txtPhone.getText().trim() : "";
            if (!phoneInput.matches("^09-\\d+$")) {
                showAlert("Invalid Phone Number", "Please enter a valid phone number starting with '09-' followed by numbers only (e.g., 09-123456789).", Alert.AlertType.ERROR);
                return;
            }

            // Save values
            if (txtTeacherId != null) currentTeacher.setTeacherId(txtTeacherId.getText());
            if (txtDob != null) currentTeacher.setDob(txtDob.getText());
            if (txtName != null) currentTeacher.setName(txtName.getText());
            if (txtGender != null) currentTeacher.setGender(txtGender.getText());
            currentTeacher.setDepartment(correctedDept);
            if (txtNationality != null) currentTeacher.setNationality(txtNationality.getText());
            currentTeacher.setPosition(correctedPos);
            if (txtNrc != null) currentTeacher.setNrc(txtNrc.getText());

            currentTeacher.setPhoneNo(phoneInput);
            if (txtEmail != null) currentTeacher.setEmail(txtEmail.getText());
            if (txtCurrentAddress != null) currentTeacher.setCurrentAddress(txtCurrentAddress.getText());
            if (txtPermanentAddress != null) currentTeacher.setPermanentAddress(txtPermanentAddress.getText());

            if (txtDegree != null) currentTeacher.setDegree(txtDegree.getText());
            if (txtUniversity != null) currentTeacher.setUniversity(txtUniversity.getText());
            if (txtYearCompleted != null) currentTeacher.setYearCompleted(txtYearCompleted.getText());
            if (txtSpecialization != null) currentTeacher.setSpecialization(txtSpecialization.getText());
            if (txtEmergency != null) currentTeacher.setEmergencyContact(txtEmergency.getText());
        }
        handleReturn();
    }

    @FXML
    public void handleResetData() {
        if (currentTeacher != null) {
            currentTeacher.resetToOriginal();

            if (txtTeacherId != null) txtTeacherId.setText(currentTeacher.getTeacherId());
            if (txtDob != null) txtDob.setText(currentTeacher.getDob());
            if (txtName != null) txtName.setText(currentTeacher.getName());
            if (txtGender != null) txtGender.setText(currentTeacher.getGender());
            if (txtDept != null) txtDept.setText(currentTeacher.getDepartment());
            if (txtNationality != null) txtNationality.setText(currentTeacher.getNationality());
            if (txtPosition != null) txtPosition.setText(currentTeacher.getPosition());
            if (txtNrc != null) txtNrc.setText(currentTeacher.getNrc());

            if (txtPhone != null) txtPhone.setText(currentTeacher.getPhoneNo());
            if (txtEmail != null) txtEmail.setText(currentTeacher.getEmail());
            if (txtCurrentAddress != null) txtCurrentAddress.setText(currentTeacher.getCurrentAddress());
            if (txtPermanentAddress != null) txtPermanentAddress.setText(currentTeacher.getPermanentAddress());

            if (txtDegree != null) txtDegree.setText(currentTeacher.getDegree());
            if (txtUniversity != null) txtUniversity.setText(currentTeacher.getUniversity());
            if (txtYearCompleted != null) txtYearCompleted.setText(currentTeacher.getYearCompleted());
            if (txtSpecialization != null) txtSpecialization.setText(currentTeacher.getSpecialization());
            if (txtEmergency != null) txtEmergency.setText(currentTeacher.getEmergencyContact());

            showAlert("Data Reset", "This teacher's record has been reset to its original state.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleReturn() {
        try {
            Parent teacherView = FXMLLoader.load(getClass().getResource("TeacherContents.fxml"));
            BorderPane mainLayout = (BorderPane) topActionBar.getScene().lookup("#mainBorderPane");
            if (mainLayout != null) {
                mainLayout.setCenter(teacherView);
            } else {
                Parent root = topActionBar.getScene().getRoot();
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
        if (topActionBar != null && topActionBar.getScene() != null) {
            alert.initOwner(topActionBar.getScene().getWindow());
        }
        alert.showAndWait();
    }
}