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

public class StudentDetailController {

    @FXML
    private Label lblRollNo, lblDob, lblName, lblGender, lblMajor, lblNationality, lblYear, lblSemester, lblNrc;
    @FXML
    private Label lblPhone, lblEmail, lblCurrentAddress, lblPermanentAddress;
    @FXML
    private Label lblFatherName, lblFatherOcc, lblFatherPhone, lblFatherEmail;
    @FXML
    private Label lblMotherName, lblMotherOcc, lblMotherPhone, lblMotherEmail;
    @FXML
    private Label lblPrevSchool, lblYearCompleted, lblAdditionalInfo;

    private TextField txtRollNo, txtDob, txtName, txtGender, txtMajor, txtNationality, txtYear, txtSemester, txtNrc;
    private TextField txtPhone, txtEmail, txtCurrentAddress, txtPermanentAddress;
    private TextField txtFatherName, txtFatherOcc, txtFatherPhone, txtFatherEmail;
    private TextField txtMotherName, txtMotherOcc, txtMotherPhone, txtMotherEmail;
    private TextField txtPrevSchool, txtYearCompleted, txtAdditionalInfo;

    @FXML
    private ImageView passportImageView;
    @FXML
    private Button btnSave;
    @FXML
    private HBox topActionBar;
    @FXML
    private VBox contentContainer;

    private Student currentStudent;
    private boolean isEditMode = false;

    public void initData(Student student) {
        initData(student, false);
    }

    public void initData(Student student, boolean editMode) {
        this.currentStudent = student;
        this.isEditMode = editMode;

        if (contentContainer != null && !contentContainer.getChildren().isEmpty()) {
            HBox banner = new HBox();
            banner.setStyle(
                    "-fx-background-color: #dbeafe; -fx-border-color: #2563eb; -fx-border-width: 1.5; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 12 18;");

            String bannerText = String.format("⭐   %d     |     %s     |     %s     |     %s     |     %s",
                    student.getNo(),
                    student.getRollNo(),
                    student.getName(),
                    student.getMajor(),
                    student.getYear());

            Label bannerLabel = new Label(bannerText);
            bannerLabel.setStyle("-fx-text-fill: #1e3a8a; -fx-font-weight: bold; -fx-font-size: 15px;");
            banner.getChildren().add(bannerLabel);
            contentContainer.getChildren().add(0, banner);
        }

        if (!isEditMode) {
            lblRollNo.setText(student.getRollNo());
            lblDob.setText(student.getDob());
            lblName.setText(student.getName());
            lblGender.setText(student.getGender());
            lblMajor.setText(student.getMajor());
            lblNationality.setText(student.getNationality());
            lblYear.setText(student.getYear());
            lblSemester.setText(toDisplaySemester(student.getSemester()));
            lblNrc.setText(student.getNrc());

            lblPhone.setText(student.getPhoneNo());
            lblEmail.setText(student.getEmail());
            lblCurrentAddress.setText(student.getCurrentAddress());
            lblPermanentAddress.setText(student.getPermanentAddress());

            lblFatherName.setText(student.getFatherName());
            lblFatherOcc.setText(student.getFatherOccupation());
            lblFatherPhone.setText(student.getFatherPhone());
            lblFatherEmail.setText(student.getFatherEmail());

            lblMotherName.setText(student.getMotherName());
            lblMotherOcc.setText(student.getMotherOccupation());
            lblMotherPhone.setText(student.getMotherPhone());
            lblMotherEmail.setText(student.getMotherEmail());

            lblPrevSchool.setText(student.getPrevSchool());
            lblYearCompleted.setText(student.getYearCompleted());
            lblAdditionalInfo.setText(student.getAdditionalInfo());

            if (btnSave != null)
                btnSave.setVisible(false);
        } else {
            replaceLabelWithTextField(lblRollNo, txtRollNo = new TextField(student.getRollNo()));
            replaceLabelWithTextField(lblDob, txtDob = new TextField(student.getDob()));
            replaceLabelWithTextField(lblName, txtName = new TextField(student.getName()));
            replaceLabelWithTextField(lblGender, txtGender = new TextField(student.getGender()));
            replaceLabelWithTextField(lblMajor, txtMajor = new TextField(student.getMajor()));
            replaceLabelWithTextField(lblNationality, txtNationality = new TextField(student.getNationality()));
            replaceLabelWithTextField(lblYear, txtYear = new TextField(student.getYear()));
            replaceLabelWithTextField(lblSemester,
                    txtSemester = new TextField(toDisplaySemester(student.getSemester())));
            replaceLabelWithTextField(lblNrc, txtNrc = new TextField(student.getNrc()));

            replaceLabelWithTextField(lblPhone, txtPhone = new TextField(student.getPhoneNo()));
            replaceLabelWithTextField(lblEmail, txtEmail = new TextField(student.getEmail()));
            replaceLabelWithTextField(lblCurrentAddress,
                    txtCurrentAddress = new TextField(student.getCurrentAddress()));
            replaceLabelWithTextField(lblPermanentAddress,
                    txtPermanentAddress = new TextField(student.getPermanentAddress()));

            replaceLabelWithTextField(lblFatherName, txtFatherName = new TextField(student.getFatherName()));
            replaceLabelWithTextField(lblFatherOcc, txtFatherOcc = new TextField(student.getFatherOccupation()));
            replaceLabelWithTextField(lblFatherPhone, txtFatherPhone = new TextField(student.getFatherPhone()));
            replaceLabelWithTextField(lblFatherEmail, txtFatherEmail = new TextField(student.getFatherEmail()));

            replaceLabelWithTextField(lblMotherName, txtMotherName = new TextField(student.getMotherName()));
            replaceLabelWithTextField(lblMotherOcc, txtMotherOcc = new TextField(student.getMotherOccupation()));
            replaceLabelWithTextField(lblMotherPhone, txtMotherPhone = new TextField(student.getMotherPhone()));
            replaceLabelWithTextField(lblMotherEmail, txtMotherEmail = new TextField(student.getMotherEmail()));

            replaceLabelWithTextField(lblPrevSchool, txtPrevSchool = new TextField(student.getPrevSchool()));
            replaceLabelWithTextField(lblYearCompleted, txtYearCompleted = new TextField(student.getYearCompleted()));
            replaceLabelWithTextField(lblAdditionalInfo,
                    txtAdditionalInfo = new TextField(student.getAdditionalInfo()));

            if (topActionBar != null && btnSave == null) {
                btnSave = new Button("Save Changes");
                btnSave.getStyleClass().add("search-button");
                btnSave.setPrefHeight(35);
                btnSave.setOnAction(e -> handleSave());
                topActionBar.getChildren().add(btnSave);
            }
        }

        if (student.getPhotoPath() != null && !student.getPhotoPath().isEmpty()) {
            try {
                Image img = new Image(getClass().getResourceAsStream(student.getPhotoPath()));
                if (img != null)
                    passportImageView.setImage(img);
            } catch (Exception e) {
            }
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

    private String toDisplaySemester(String dbSemester) {
        if (dbSemester == null)
            return "I";
        if (dbSemester.equalsIgnoreCase("Semester I") || dbSemester.equalsIgnoreCase("I")
                || dbSemester.equalsIgnoreCase("1")) {
            return "I";
        }
        if (dbSemester.equalsIgnoreCase("Semester II") || dbSemester.equalsIgnoreCase("II")
                || dbSemester.equalsIgnoreCase("2")) {
            return "II";
        }
        return dbSemester;
    }

    private String toDbSemester(String input) {
        if (input == null)
            return null;
        String trimmed = input.trim();
        if (trimmed.equalsIgnoreCase("I") || trimmed.equalsIgnoreCase("Semester I") || trimmed.equalsIgnoreCase("1")) {
            return "Semester I";
        } else if (trimmed.equalsIgnoreCase("II") || trimmed.equalsIgnoreCase("Semester II")
                || trimmed.equalsIgnoreCase("2")) {
            return "Semester II";
        }
        return null;
    }

    private String generateRollNo(String year, String major, String suffix) {
        String romanYear = "I";
        if (year.equalsIgnoreCase("First Year"))
            romanYear = "I";
        else if (year.equalsIgnoreCase("Second Year"))
            romanYear = "II";
        else if (year.equalsIgnoreCase("Third Year"))
            romanYear = "III";
        else if (year.equalsIgnoreCase("Fourth Year"))
            romanYear = "IV";
        else if (year.equalsIgnoreCase("Fifth Year"))
            romanYear = "V";

        return romanYear + "-" + major + "-" + suffix;
    }

    @FXML
    public void handleSave() {
        if (currentStudent != null && isEditMode) {
            // 1. Validate & Auto-Correct Programme/Major
            String majorInput = txtMajor != null ? txtMajor.getText().trim() : "";
            String correctedMajor = null;
            String[] validMajors = { "CE", "ME", "EC", "EP", "CEIT", "MC", "ChE", "Arch", "PE", "MnE", "Met", "Tex",
                    "FE" };
            for (String m : validMajors) {
                if (m.equalsIgnoreCase(majorInput)) {
                    correctedMajor = m;
                    break;
                }
            }
            if (correctedMajor == null) {
                showAlert("Invalid Major",
                        "Please input a valid Programme/Major from the dropdown choices:\nCE, ME, EC, EP, CEIT, MC, ChE, Arch, PE, MnE, Met, Tex, FE.",
                        Alert.AlertType.ERROR);
                return;
            }

            // 2. Validate & Auto-Correct Year/Level
            String yearInput = txtYear != null ? txtYear.getText().trim() : "";
            String correctedYear = null;
            String[] validYears = { "First Year", "Second Year", "Third Year", "Fourth Year", "Fifth Year" };
            for (String y : validYears) {
                if (y.equalsIgnoreCase(yearInput)) {
                    correctedYear = y;
                    break;
                }
            }
            if (correctedYear == null) {
                showAlert("Invalid Year/Level",
                        "Please input a valid Year/Level from the dropdown choices:\nFirst Year, Second Year, Third Year, Fourth Year, Fifth Year.",
                        Alert.AlertType.ERROR);
                return;
            }

            // 3. Validate & Auto-Correct Semester
            String semInput = txtSemester != null ? txtSemester.getText().trim() : "";
            String correctedSemester = toDbSemester(semInput);
            if (correctedSemester == null) {
                showAlert("Invalid Semester", "Please input a valid Semester (e.g., I or II).", Alert.AlertType.ERROR);
                return;
            }

            // 4. Validate Phone Number (must start with 09- followed by numbers only)
            String phoneInput = txtPhone != null ? txtPhone.getText().trim() : "";
            if (!phoneInput.matches("^09-\\d+$")) {
                showAlert("Invalid Phone Number",
                        "Please enter a valid phone number starting with '09-' followed by numbers only (e.g., 09-876543210).",
                        Alert.AlertType.ERROR);
                return;
            }

            // 5. Extract suffix number from Student ID (txtRollNo) safely
            String rollNoInput = txtRollNo != null ? txtRollNo.getText().trim() : currentStudent.getRollNo();
            String suffix = "1";
            if (rollNoInput != null && !rollNoInput.isEmpty()) {
                String[] parts = rollNoInput.split("-");
                if (parts.length >= 3) {
                    suffix = parts[parts.length - 1];
                } else if (parts.length == 2) {
                    suffix = parts[1];
                } else {
                    suffix = rollNoInput;
                }
            }

            // Generate updated Roll No automatically based on new Year and Major, plus
            // suffix
            String newRollNo = generateRollNo(correctedYear, correctedMajor, suffix);

            // Save all fields to Student data object
            currentStudent.setRollNo(newRollNo);
            if (txtDob != null)
                currentStudent.setDob(txtDob.getText());
            if (txtName != null)
                currentStudent.setName(txtName.getText());
            if (txtGender != null)
                currentStudent.setGender(txtGender.getText());
            currentStudent.setMajor(correctedMajor);
            if (txtNationality != null)
                currentStudent.setNationality(txtNationality.getText());
            currentStudent.setYear(correctedYear);
            currentStudent.setSemester(correctedSemester);
            if (txtNrc != null)
                currentStudent.setNrc(txtNrc.getText());

            if (txtPhone != null)
                currentStudent.setPhoneNo(phoneInput);
            if (txtEmail != null)
                currentStudent.setEmail(txtEmail.getText());
            if (txtCurrentAddress != null)
                currentStudent.setCurrentAddress(txtCurrentAddress.getText());
            if (txtPermanentAddress != null)
                currentStudent.setPermanentAddress(txtPermanentAddress.getText());

            if (txtFatherName != null)
                currentStudent.setFatherName(txtFatherName.getText());
            if (txtFatherOcc != null)
                currentStudent.setFatherOccupation(txtFatherOcc.getText());
            if (txtFatherPhone != null)
                currentStudent.setFatherPhone(txtFatherPhone.getText());
            if (txtFatherEmail != null)
                currentStudent.setFatherEmail(txtFatherEmail.getText());

            if (txtMotherName != null)
                currentStudent.setMotherName(txtMotherName.getText());
            if (txtMotherOcc != null)
                currentStudent.setMotherOccupation(txtMotherOcc.getText());
            if (txtMotherPhone != null)
                currentStudent.setMotherPhone(txtMotherPhone.getText());
            if (txtMotherEmail != null)
                currentStudent.setMotherEmail(txtMotherEmail.getText());

            if (txtPrevSchool != null)
                currentStudent.setPrevSchool(txtPrevSchool.getText());
            if (txtYearCompleted != null)
                currentStudent.setYearCompleted(txtYearCompleted.getText());
            if (txtAdditionalInfo != null)
                currentStudent.setAdditionalInfo(txtAdditionalInfo.getText());
        }
        handleReturn();
    }

    @FXML
    public void handleResetData() {
        if (currentStudent != null) {
            currentStudent.resetToOriginal();

            if (txtRollNo != null)
                txtRollNo.setText(currentStudent.getRollNo());
            if (txtDob != null)
                txtDob.setText(currentStudent.getDob());
            if (txtName != null)
                txtName.setText(currentStudent.getName());
            if (txtGender != null)
                txtGender.setText(currentStudent.getGender());
            if (txtMajor != null)
                txtMajor.setText(currentStudent.getMajor());
            if (txtNationality != null)
                txtNationality.setText(currentStudent.getNationality());
            if (txtYear != null)
                txtYear.setText(currentStudent.getYear());
            if (txtSemester != null)
                txtSemester.setText(toDisplaySemester(currentStudent.getSemester()));
            if (txtNrc != null)
                txtNrc.setText(currentStudent.getNrc());

            if (txtPhone != null)
                txtPhone.setText(currentStudent.getPhoneNo());
            if (txtEmail != null)
                txtEmail.setText(currentStudent.getEmail());
            if (txtCurrentAddress != null)
                txtCurrentAddress.setText(currentStudent.getCurrentAddress());
            if (txtPermanentAddress != null)
                txtPermanentAddress.setText(currentStudent.getPermanentAddress());

            if (txtFatherName != null)
                txtFatherName.setText(currentStudent.getFatherName());
            if (txtFatherOcc != null)
                txtFatherOcc.setText(currentStudent.getFatherOccupation());
            if (txtFatherPhone != null)
                txtFatherPhone.setText(currentStudent.getFatherPhone());
            if (txtFatherEmail != null)
                txtFatherEmail.setText(currentStudent.getFatherEmail());

            if (txtMotherName != null)
                txtMotherName.setText(currentStudent.getMotherName());
            if (txtMotherOcc != null)
                txtMotherOcc.setText(currentStudent.getMotherOccupation());
            if (txtMotherPhone != null)
                txtMotherPhone.setText(currentStudent.getMotherPhone());
            if (txtMotherEmail != null)
                txtMotherEmail.setText(currentStudent.getMotherEmail());

            if (txtPrevSchool != null)
                txtPrevSchool.setText(currentStudent.getPrevSchool());
            if (txtYearCompleted != null)
                txtYearCompleted.setText(currentStudent.getYearCompleted());
            if (txtAdditionalInfo != null)
                txtAdditionalInfo.setText(currentStudent.getAdditionalInfo());

            showAlert("Data Reset", "This student's record has been reset to its original state.",
                    Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleReturn() {
        try {
            Parent root = topActionBar.getScene().getRoot();
            if (root instanceof BorderPane) {
                BorderPane mainLayout = (BorderPane) root;
                Parent studentView = FXMLLoader.load(getClass().getResource("StudentContent.fxml"));
                mainLayout.setCenter(studentView);
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
        alert.showAndWait();
    }
}