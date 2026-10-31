package application;

import java.io.File;
import java.io.IOException;

import application.dao.StudentDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class StudentModifyController {

    @FXML private TextField txtRollNo, txtDob, txtName, txtGender, txtMajor, txtNationality, txtYear, txtSemester, txtNrc;
    @FXML private TextField txtPhone, txtEmail, txtCurrentAddress, txtPermanentAddress;
    @FXML private TextField txtFatherName, txtFatherOcc, txtFatherPhone, txtFatherEmail;
    @FXML private TextField txtMotherName, txtMotherOcc, txtMotherPhone, txtMotherEmail;
    @FXML private TextField txtPrevSchool, txtYearCompleted, txtAdditionalInfo;
    @FXML private ImageView passportImageView;
    @FXML private VBox contentContainer;

    private Student currentStudent;
    private HBox bannerBox = null;
    private String updatedPhotoPath = null;
    private boolean isUpdating = false;

    private final String[] VALID_MAJORS = {"CE", "ME", "EC", "EP", "CEIT", "MC", "ChE", "Arch", "PE", "MnE", "Met", "Tex", "FE"};
    private final String[] VALID_YEARS = {"First Year", "Second Year", "Third Year", "Fourth Year", "Fifth Year"};

    @FXML
    public void initialize() {
        setupBidirectionalSync();
    }

    private void setupBidirectionalSync() {
        if (txtRollNo != null) {
            txtRollNo.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal && !isUpdating) {
                    syncFromRollNo();
                }
            });
            txtRollNo.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!isUpdating && newVal != null && newVal.matches("^[a-zA-Z]+-[a-zA-Z0-9]+-\\d+$")) {
                    syncFromRollNo();
                }
            });
        }

        if (txtMajor != null) {
            txtMajor.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal && !isUpdating) {
                    syncToRollNo();
                }
            });
        }
        if (txtYear != null) {
            txtYear.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal && !isUpdating) {
                    syncToRollNo();
                }
            });
        }
    }

    private void syncFromRollNo() {
        if (txtRollNo == null || txtRollNo.getText() == null) return;
        String raw = txtRollNo.getText().trim();
        String[] parts = raw.split("-");
        if (parts.length >= 3 && parts[2].matches("\\d+")) {
            String romanPart = parts[0].trim();
            String majorPart = parts[1].trim();

            String fullYear = getYearFromRoman(romanPart);
            String validMajor = findValidMajor(majorPart);

            if (fullYear != null && validMajor != null) {
                isUpdating = true;
                try {
                    if (txtMajor != null) txtMajor.setText(validMajor);
                    if (txtYear != null) txtYear.setText(fullYear);
                } finally {
                    isUpdating = false;
                }
            }
        }
    }

    private void syncToRollNo() {
        if (txtRollNo == null || txtYear == null || txtMajor == null) return;
        String rawYear = txtYear.getText() != null ? txtYear.getText().trim() : "";
        String rawMajor = txtMajor.getText() != null ? txtMajor.getText().trim() : "";

        String validYear = findValidYear(rawYear);
        String validMajor = findValidMajor(rawMajor);

        if (validYear != null && validMajor != null) {
            String roman = getRomanYear(validYear);
            String currentRoll = txtRollNo.getText() != null ? txtRollNo.getText().trim() : "";
            String[] parts = currentRoll.split("-");
            String rollNum = (parts.length >= 3 && parts[parts.length - 1].matches("\\d+")) 
                             ? parts[parts.length - 1] : "1";

            isUpdating = true;
            try {
                txtRollNo.setText(roman + "-" + validMajor + "-" + rollNum);
                txtYear.setText(validYear);
                txtMajor.setText(validMajor);
            } finally {
                isUpdating = false;
            }
        }
    }

    public void initData(Student student) {
        this.currentStudent = student;
        if (student != null) {
            if (contentContainer != null) {
                if (bannerBox != null && contentContainer.getChildren().contains(bannerBox)) {
                    contentContainer.getChildren().remove(bannerBox);
                } else if (!contentContainer.getChildren().isEmpty()) {
                    Node firstNode = contentContainer.getChildren().get(0);
                    if (firstNode instanceof HBox && firstNode.getStyle() != null && firstNode.getStyle().contains("-fx-background-color: #dbeafe;")) {
                        contentContainer.getChildren().remove(firstNode);
                    }
                }

                bannerBox = new HBox();
                bannerBox.setStyle("-fx-background-color: #dbeafe; -fx-border-color: #2563eb; -fx-border-width: 1.5; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 12 18;");
                
                String bannerText = String.format("⭐     %d     |     %s     |     %s     |     %s     |     %s",
                    student.getNo(),
                    safe(student.getRollNo()),
                    safe(student.getName()),
                    safe(student.getMajor()),
                    safe(student.getYear())
                );

                Label bannerLabel = new Label(bannerText);
                bannerLabel.setStyle("-fx-text-fill: #1e3a8a; -fx-font-weight: bold; -fx-font-size: 15px;");
                bannerBox.getChildren().add(bannerLabel);
                contentContainer.getChildren().add(0, bannerBox);
            }

            isUpdating = true;
            try {
                if (txtRollNo != null) txtRollNo.setText(safe(student.getRollNo()));
                if (txtDob != null) txtDob.setText(safe(student.getDob()));
                if (txtName != null) txtName.setText(safe(student.getName()));
                if (txtGender != null) txtGender.setText(safe(student.getGender()));
                if (txtMajor != null) txtMajor.setText(safe(student.getMajor()));
                if (txtNationality != null) txtNationality.setText(safe(student.getNationality(), "Myanmar"));
                if (txtYear != null) txtYear.setText(safe(student.getYear()));
                if (txtSemester != null) txtSemester.setText(safe(student.getSemester()));
                if (txtNrc != null) txtNrc.setText(safe(student.getNrc()));

                if (txtPhone != null) txtPhone.setText(safe(student.getPhoneNo()));
                if (txtEmail != null) txtEmail.setText(safe(student.getEmail()));
                if (txtCurrentAddress != null) txtCurrentAddress.setText(safe(student.getCurrentAddress()));
                if (txtPermanentAddress != null) txtPermanentAddress.setText(safe(student.getPermanentAddress()));

                if (txtFatherName != null) txtFatherName.setText(safe(student.getFatherName()));
                if (txtFatherOcc != null) txtFatherOcc.setText(safe(student.getFatherOccupation()));
                if (txtFatherPhone != null) txtFatherPhone.setText(safe(student.getFatherPhone()));
                if (txtFatherEmail != null) txtFatherEmail.setText(safe(student.getFatherEmail()));

                if (txtMotherName != null) txtMotherName.setText(safe(student.getMotherName()));
                if (txtMotherOcc != null) txtMotherOcc.setText(safe(student.getMotherOccupation()));
                if (txtMotherPhone != null) txtMotherPhone.setText(safe(student.getMotherPhone()));
                if (txtMotherEmail != null) txtMotherEmail.setText(safe(student.getMotherEmail()));

                if (txtPrevSchool != null) txtPrevSchool.setText(safe(student.getPrevSchool()));
                if (txtYearCompleted != null) txtYearCompleted.setText(safe(student.getYearCompleted()));
                if (txtAdditionalInfo != null) txtAdditionalInfo.setText(safe(student.getAdditionalInfo()));
            } finally {
                isUpdating = false;
            }

            updatedPhotoPath = student.getPhotoPath();
            loadImage(updatedPhotoPath);
        }
    }

    private String safe(String val) {
        return safe(val, "");
    }

    private String safe(String val, String defaultVal) {
        if (val == null || val.trim().equalsIgnoreCase("null") || val.trim().equals("-")) {
            return defaultVal;
        }
        return val.trim();
    }

    private String findValidMajor(String input) {
        if (input == null) return null;
        for (String m : VALID_MAJORS) {
            if (m.equalsIgnoreCase(input.trim())) {
                return m;
            }
        }
        return null;
    }

    private String findValidYear(String input) {
        if (input == null) return null;
        String s = input.trim().toLowerCase();
        if (s.contains("first") || s.equals("1") || s.equals("i")) return "First Year";
        if (s.contains("second") || s.equals("2") || s.equals("ii")) return "Second Year";
        if (s.contains("third") || s.equals("3") || s.equals("iii")) return "Third Year";
        if (s.contains("fourth") || s.equals("4") || s.equals("iv")) return "Fourth Year";
        if (s.contains("fifth") || s.equals("5") || s.equals("v")) return "Fifth Year";
        
        for (String y : VALID_YEARS) {
            if (y.equalsIgnoreCase(input.trim())) return y;
        }
        return null;
    }

    private String getYearFromRoman(String roman) {
        if (roman == null) return null;
        String r = roman.trim().toUpperCase();
        switch (r) {
            case "I": return "First Year";
            case "II": return "Second Year";
            case "III": return "Third Year";
            case "IV": return "Fourth Year";
            case "V": return "Fifth Year";
            default: return null;
        }
    }

    private String findValidSemester(String input) {
        if (input == null) return null;
        String s = input.trim().toLowerCase();
        if (s.equals("ii") || s.equals("2") || s.equals("second semester") || s.equals("semester ii") || s.equals("semester 2")) return "Second Semester";
        if (s.equals("i") || s.equals("1") || s.equals("first semester") || s.equals("semester i") || s.equals("semester 1")) return "First Semester";
        return null;
    }

    private String getRomanYear(String yearStr) {
        if (yearStr == null) return "";
        String y = yearStr.trim().toLowerCase();
        if (y.contains("first") || y.equals("i") || y.equals("1")) return "I";
        if (y.contains("second") || y.equals("ii") || y.equals("2")) return "II";
        if (y.contains("third") || y.equals("iii") || y.equals("3")) return "III";
        if (y.contains("fourth") || y.equals("iv") || y.equals("4")) return "IV";
        if (y.contains("fifth") || y.equals("v") || y.equals("5")) return "V";
        return "";
    }

    @FXML
    public void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Replace Passport Size Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files (*.png, *.jpg, *.jpeg, *.bmp)", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );

        if (passportImageView != null && passportImageView.getScene() != null) {
            Stage stage = (Stage) passportImageView.getScene().getWindow();
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                updatedPhotoPath = file.toURI().toString();
                loadImage(updatedPhotoPath);
            }
        }
    }

    private void loadImage(String path) {
        if (passportImageView == null) return;
        if (path != null && !path.trim().isEmpty()) {
            try {
                Image img = (path.startsWith("file:") || path.startsWith("http:"))
                        ? new Image(path, 86, 110, false, true)
                        : new Image(getClass().getResourceAsStream(path));
                if (img != null) {
                    passportImageView.setImage(img);
                }
            } catch (Exception e) {}
        }
    }

    @FXML
    public void handleSave() {
        if (currentStudent != null) {
            String rawRollNo = txtRollNo != null ? txtRollNo.getText().trim() : "";
            String rawYear = txtYear != null ? txtYear.getText().trim() : "";
            String rawMajor = txtMajor != null ? txtMajor.getText().trim() : "";
            String rawSem = txtSemester != null ? txtSemester.getText().trim() : "";
            String dobInput = txtDob != null ? txtDob.getText().trim() : "";
            String phoneInput = txtPhone != null ? txtPhone.getText().trim() : "";
            String emailInput = txtEmail != null ? txtEmail.getText().trim() : "";
            String yearCompInput = txtYearCompleted != null ? txtYearCompleted.getText().trim() : "";

            String fPhoneInput = txtFatherPhone != null ? txtFatherPhone.getText().trim() : "";
            String fEmailInput = txtFatherEmail != null ? txtFatherEmail.getText().trim() : "";
            String mPhoneInput = txtMotherPhone != null ? txtMotherPhone.getText().trim() : "";
            String mEmailInput = txtMotherEmail != null ? txtMotherEmail.getText().trim() : "";

            // 1. Strict Structural Format Check for Roll No (year-major-number)
            if (!rawRollNo.matches("^[a-zA-Z]+-[a-zA-Z0-9]+-\\d+$")) {
                showAlert("Invalid Roll No Format", 
                    "Roll No must be in the valid format: [Year]-[Major]-[Number]\n(e.g., II-CEIT-2 or V-FE-5).\n\n'" + rawRollNo + "' is not a valid format.", 
                    Alert.AlertType.ERROR);
                return;
            }

            String[] rollParts = rawRollNo.split("-");
            String rollRoman = rollParts[0].trim().toUpperCase();
            String rollMajor = rollParts[1].trim();
            String rollNum = rollParts[2].trim();

            String yearFromRoll = getYearFromRoman(rollRoman);
            if (yearFromRoll == null) {
                showAlert("Invalid Year in Roll No", 
                    "The year prefix '" + rollRoman + "' in Roll No is invalid.\nValid Roman years are: I, II, III, IV, V.", 
                    Alert.AlertType.ERROR);
                return;
            }

            String majorFromRoll = findValidMajor(rollMajor);
            if (majorFromRoll == null) {
                showAlert("Invalid Major in Roll No", 
                    "The major code '" + rollMajor + "' in Roll No does not match any valid Major:\n" +
                    "CE, ME, EC, EP, CEIT, MC, ChE, Arch, PE, MnE, Met, Tex, FE.", 
                    Alert.AlertType.ERROR);
                return;
            }

            String validatedMajor = findValidMajor(rawMajor);
            if (validatedMajor == null) {
                showAlert("Invalid Programme / Major", 
                    "Entered Major '" + rawMajor + "' does not match any valid Major:\n" +
                    "CE, ME, EC, EP, CEIT, MC, ChE, Arch, PE, MnE, Met, Tex, FE.", 
                    Alert.AlertType.ERROR);
                return;
            }

            String validatedYear = findValidYear(rawYear);
            if (validatedYear == null) {
                showAlert("Invalid Year / Level", 
                    "Entered Year '" + rawYear + "' does not match any valid Year:\n" +
                    "First Year, Second Year, Third Year, Fourth Year, Fifth Year.", 
                    Alert.AlertType.ERROR);
                return;
            }

            String validatedSemester = findValidSemester(rawSem);
            if (validatedSemester == null) {
                showAlert("Invalid Semester", "Entered Semester '" + rawSem + "' is invalid. Please enter 'First Semester' or 'Second Semester'.", Alert.AlertType.ERROR);
                return;
            }

            if (!majorFromRoll.equalsIgnoreCase(validatedMajor)) {
                validatedMajor = majorFromRoll;
            }
            if (!yearFromRoll.equalsIgnoreCase(validatedYear)) {
                validatedYear = yearFromRoll;
            }

            String finalRomanYear = getRomanYear(validatedYear);
            String standardizedRollNo = finalRomanYear + "-" + validatedMajor + "-" + rollNum;

            isUpdating = true;
            try {
                txtRollNo.setText(standardizedRollNo);
                txtMajor.setText(validatedMajor);
                txtYear.setText(validatedYear);
                txtSemester.setText(validatedSemester);
            } finally {
                isUpdating = false;
            }

            // 5. Date of Birth Validation (Strict format: Year-Month-Day e.g., 2003-12-11)
            if (!dobInput.isEmpty()) {
                if (!dobInput.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
                    showAlert("Invalid Date of Birth", 
                        "Date of Birth must be in the format: Year-Month-Day (e.g., 2003-12-11) with no letters.", 
                        Alert.AlertType.ERROR);
                    return;
                }
            }

            // Contact and School Validations
            if (!phoneInput.matches("^09-\\d+$")) {
                showAlert("Invalid Phone Number", "Phone number must be in the format 09-xxxxxxxx (starting with 09- followed by numbers).", Alert.AlertType.ERROR);
                return;
            }
            if (!emailInput.contains("@")) {
                showAlert("Invalid Email Address", "Email address must contain '@'.", Alert.AlertType.ERROR);
                return;
            }
            if (!fPhoneInput.isEmpty() && !fPhoneInput.matches("^09-\\d+$")) {
                showAlert("Invalid Father's Phone Number", "Father's phone number must be in the format 09-xxxxxxxx.", Alert.AlertType.ERROR);
                return;
            }
            if (!fEmailInput.isEmpty() && !fEmailInput.contains("@")) {
                showAlert("Invalid Father's Email", "Father's email address must contain '@'.", Alert.AlertType.ERROR);
                return;
            }
            if (!mPhoneInput.isEmpty() && !mPhoneInput.matches("^09-\\d+$")) {
                showAlert("Invalid Mother's Phone Number", "Mother's phone number must be in the format 09-xxxxxxxx.", Alert.AlertType.ERROR);
                return;
            }
            if (!mEmailInput.isEmpty() && !mEmailInput.contains("@")) {
                showAlert("Invalid Mother's Email", "Mother's email address must contain '@'.", Alert.AlertType.ERROR);
                return;
            }
            if (!yearCompInput.isEmpty() && !yearCompInput.matches("^\\d{4}$")) {
                showAlert("Invalid Year Completed", "Year Completed must be a 4-digit year (e.g., 2020).", Alert.AlertType.ERROR);
                return;
            }

            // Update in-memory Student model
            currentStudent.setRollNo(standardizedRollNo);
            if (txtDob != null) currentStudent.setDob(txtDob.getText().trim());
            if (txtName != null) currentStudent.setName(txtName.getText().trim());
            if (txtGender != null) currentStudent.setGender(txtGender.getText().trim());
            currentStudent.setMajor(validatedMajor);
            if (txtNationality != null) currentStudent.setNationality(txtNationality.getText().trim());
            currentStudent.setYear(validatedYear);
            currentStudent.setSemester(validatedSemester);
            if (txtNrc != null) currentStudent.setNrc(txtNrc.getText().trim());
            currentStudent.setPhoneNo(phoneInput);
            currentStudent.setEmail(emailInput);
            if (txtCurrentAddress != null) currentStudent.setCurrentAddress(txtCurrentAddress.getText().trim());
            if (txtPermanentAddress != null) currentStudent.setPermanentAddress(txtPermanentAddress.getText().trim());
            if (txtFatherName != null) currentStudent.setFatherName(txtFatherName.getText().trim());
            if (txtFatherOcc != null) currentStudent.setFatherOccupation(txtFatherOcc.getText().trim());
            currentStudent.setFatherPhone(fPhoneInput);
            currentStudent.setFatherEmail(fEmailInput);
            if (txtMotherName != null) currentStudent.setMotherName(txtMotherName.getText().trim());
            if (txtMotherOcc != null) currentStudent.setMotherOccupation(txtMotherOcc.getText().trim());
            currentStudent.setMotherPhone(mPhoneInput);
            currentStudent.setMotherEmail(mEmailInput);
            if (txtPrevSchool != null) currentStudent.setPrevSchool(txtPrevSchool.getText().trim());
            currentStudent.setYearCompleted(yearCompInput);
            if (txtAdditionalInfo != null) currentStudent.setAdditionalInfo(txtAdditionalInfo.getText().trim());

            if (updatedPhotoPath != null) {
                currentStudent.setPhotoPath(updatedPhotoPath);
            }

            // Sync with masterData list in-memory without DAO call
            if (StudentController.masterData != null) {
                for (int i = 0; i < StudentController.masterData.size(); i++) {
                    if (StudentController.masterData.get(i).getNo() == currentStudent.getNo()) {
                        StudentController.masterData.set(i, currentStudent);
                        break;
                    }
                }
            }
        }
        handleReturn();
    }

    @FXML
    public void handleResetData() {
        if (currentStudent != null) {
            StudentDAO studentDAO = new StudentDAO();
            Student fresh = studentDAO.getStudentbyId(currentStudent.getNo());
            if (fresh != null) {
                currentStudent = fresh;
            } else {
                currentStudent.resetToOriginal();
            }
            initData(currentStudent);
            showAlert("Data Reset", "This student's record has been reset to its database state.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleReturn() {
        try {
            Parent root = txtRollNo.getScene().getRoot();
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