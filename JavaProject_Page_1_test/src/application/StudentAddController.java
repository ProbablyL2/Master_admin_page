package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class StudentAddController {

    @FXML private TextField txtRollNo, txtDob, txtName, txtGender, txtMajor, txtNationality, txtYear, txtSemester, txtNrc;
    @FXML private TextField txtPhone, txtEmail, txtCurrentAddress, txtPermanentAddress;
    @FXML private TextField txtFatherName, txtFatherOcc, txtFatherPhone, txtFatherEmail;
    @FXML private TextField txtMotherName, txtMotherOcc, txtMotherPhone, txtMotherEmail;
    @FXML private TextField txtPrevSchool, txtYearCompleted, txtAdditionalInfo;
    @FXML private ImageView passportImageView;

    @FXML private TextField txtExcelPath;
    @FXML private VBox dropZone;

    @FXML private DatePicker datePickerDob;

    private File selectedExcelFile = null;
    private String selectedPhotoPath = null;
    private final String DEFAULT_IMAGE_PATH = "/freddy.jpg";

    private final String[] VALID_MAJORS = {"CE", "ME", "EC", "EP", "CEIT", "MC", "ChE", "Arch", "PE", "MnE", "Met", "Tex", "FE"};
    private final String[] VALID_YEARS = {"First Year", "Second Year", "Third Year", "Fourth Year", "Fifth Year"};

    @FXML
    public void initialize() {
        if (txtSemester != null && (txtSemester.getText() == null || txtSemester.getText().trim().isEmpty())) {
            txtSemester.setText("I");
        }

        if (txtName != null && Session.getUsername() != null && !Session.getUsername().trim().isEmpty()) {
            txtName.setText(Session.getUsername());
        }
        if (txtEmail != null && Session.getEmail() != null && !Session.getEmail().trim().isEmpty()) {
            txtEmail.setText(Session.getEmail());
        }

        if (datePickerDob != null && txtDob != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            
            datePickerDob.setConverter(new StringConverter<LocalDate>() {
                @Override
                public String toString(LocalDate date) {
                    return (date != null) ? formatter.format(date) : "";
                }

                @Override
                public LocalDate fromString(String string) {
                    if (string != null && !string.trim().isEmpty()) {
                        try {
                            return LocalDate.parse(string, formatter);
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    return null;
                }
            });

            datePickerDob.valueProperty().addListener((obs, oldDate, newDate) -> {
                if (newDate != null) {
                    txtDob.setText(formatter.format(newDate));
                }
            });
        }
    }

    // ==========================================
    // EXCEL / CSV IMPORT & DRAG-AND-DROP
    // ==========================================

    @FXML
    public void handleBrowseExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Excel / CSV File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Spreadsheet & CSV Files (*.xlsx, *.csv)", "*.xlsx", "*.csv"),
            new FileChooser.ExtensionFilter("All Files (*.*)", "*.*")
        );

        Stage stage = (Stage) txtRollNo.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            selectedExcelFile = file;
            if (txtExcelPath != null) {
                txtExcelPath.setText(file.getAbsolutePath());
            }
        }
    }

    @FXML
    public void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != dropZone && event.getDragboard().hasFiles()) {
            for (File file : event.getDragboard().getFiles()) {
                String name = file.getName().toLowerCase();
                if (name.endsWith(".xlsx") || name.endsWith(".csv") || name.endsWith(".txt")) {
                    event.acceptTransferModes(TransferMode.COPY);
                    dropZone.setStyle("-fx-border-color: #2563eb; -fx-border-width: 2.0; -fx-border-style: dashed; -fx-border-radius: 6; -fx-background-color: #eff6ff; -fx-background-radius: 6; -fx-cursor: hand;");
                    break;
                }
            }
        }
        event.consume();
    }

    @FXML
    public void handleDragExited(DragEvent event) {
        dropZone.setStyle("-fx-border-color: #94a3b8; -fx-border-width: 1.5; -fx-border-style: dashed; -fx-border-radius: 6; -fx-background-color: #f8fafc; -fx-background-radius: 6; -fx-cursor: hand;");
        event.consume();
    }

    @FXML
    public void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            for (File file : db.getFiles()) {
                String name = file.getName().toLowerCase();
                if (name.endsWith(".xlsx") || name.endsWith(".csv") || name.endsWith(".txt")) {
                    selectedExcelFile = file;
                    if (txtExcelPath != null) {
                        txtExcelPath.setText(file.getAbsolutePath());
                    }
                    success = true;
                    handleImportExcel();
                    break;
                }
            }
        }

        event.setDropCompleted(success);
        dropZone.setStyle("-fx-border-color: #94a3b8; -fx-border-width: 1.5; -fx-border-style: dashed; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: #f8fafc; -fx-cursor: hand;");
        event.consume();
    }

    @FXML
    public void handleImportExcel() {
        if (selectedExcelFile == null || !selectedExcelFile.exists()) {
            showAlert("No File Selected", "Please browse or drag and drop a valid Excel or CSV file first.", Alert.AlertType.WARNING);
            return;
        }

        String fileName = selectedExcelFile.getName().toLowerCase();

        try {
            if (fileName.endsWith(".csv") || fileName.endsWith(".txt")) {
                importFromCsv(selectedExcelFile);
            } else if (fileName.endsWith(".xlsx")) {
                boolean success = importFromNativeXlsx(selectedExcelFile);
                if (!success) {
                    showAlert("Import Notice", "Could not parse data from .xlsx file. Please try the .csv template instead.", Alert.AlertType.WARNING);
                }
            } else {
                showAlert("Unsupported File", "Please select a supported file (.xlsx or .csv).", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Import Error", "Failed to parse spreadsheet: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void importFromCsv(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                showAlert("Empty File", "The selected file is empty.", Alert.AlertType.WARNING);
                return;
            }

            String delimiter = headerLine.contains(",") ? "," : (headerLine.contains("\t") ? "\t" : ";");
            String[] headers = headerLine.split(delimiter);

            String dataLine = br.readLine();
            if (dataLine == null) {
                showAlert("No Data Row", "The selected file does not contain a row of student data below the header.", Alert.AlertType.WARNING);
                return;
            }

            String[] values = dataLine.split(delimiter, -1);
            Map<String, String> map = new HashMap<>();

            for (int i = 0; i < headers.length && i < values.length; i++) {
                String cleanHeader = headers[i].replace("\"", "").trim().toLowerCase();
                String cleanVal = values[i].replace("\"", "").trim();
                map.put(cleanHeader, cleanVal);
            }

            populateFieldsFromMap(map, values);
            showAlert("Import Successful", "Student data from " + file.getName() + " has been populated into the form fields. Please review and click 'Add New Student' to register.", Alert.AlertType.INFORMATION);
        }
    }

    /**
     * Reads native .xlsx files without requiring java.xml module or external JARs.
     */
    private boolean importFromNativeXlsx(File file) {
        try (ZipFile zip = new ZipFile(file)) {
            // 1. Read shared strings (xl/sharedStrings.xml)
            List<String> sharedStrings = new ArrayList<>();
            ZipEntry sstEntry = zip.getEntry("xl/sharedStrings.xml");
            if (sstEntry != null) {
                String sstXml = new String(zip.getInputStream(sstEntry).readAllBytes(), StandardCharsets.UTF_8);
                Matcher siMatcher = Pattern.compile("<si>(.*?)</si>", Pattern.DOTALL).matcher(sstXml);
                while (siMatcher.find()) {
                    String siContent = siMatcher.group(1);
                    StringBuilder sb = new StringBuilder();
                    Matcher tMatcher = Pattern.compile("<t[^>]*>(.*?)</t>", Pattern.DOTALL).matcher(siContent);
                    while (tMatcher.find()) {
                        sb.append(tMatcher.group(1));
                    }
                    sharedStrings.add(unescapeXml(sb.toString()));
                }
            }

            // 2. Read sheet 1 (xl/worksheets/sheet1.xml)
            ZipEntry sheetEntry = zip.getEntry("xl/worksheets/sheet1.xml");
            if (sheetEntry == null) return false;

            String sheetXml = new String(zip.getInputStream(sheetEntry).readAllBytes(), StandardCharsets.UTF_8);
            Matcher rowMatcher = Pattern.compile("<row[^>]*>(.*?)</row>", Pattern.DOTALL).matcher(sheetXml);

            List<String> headers = null;
            List<String> values = null;

            if (rowMatcher.find()) {
                headers = extractCellsFromRow(rowMatcher.group(1), sharedStrings);
            }
            if (rowMatcher.find()) {
                values = extractCellsFromRow(rowMatcher.group(1), sharedStrings);
            }

            if (headers == null || values == null || headers.isEmpty()) {
                return false;
            }

            Map<String, String> map = new HashMap<>();
            String[] rawValues = new String[headers.size()];

            for (int i = 0; i < headers.size(); i++) {
                String h = headers.get(i).trim().toLowerCase();
                String v = i < values.size() ? values.get(i).trim() : "";
                map.put(h, v);
                rawValues[i] = v;
            }

            populateFieldsFromMap(map, rawValues);
            showAlert("Import Successful", "Data from Excel file (" + file.getName() + ") has been loaded into the form.", Alert.AlertType.INFORMATION);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<String> extractCellsFromRow(String rowContent, List<String> sharedStrings) {
        List<String> rowCells = new ArrayList<>();
        Matcher cMatcher = Pattern.compile("<c\\b([^>]*)>(.*?)</c>", Pattern.DOTALL).matcher(rowContent);
        int nextExpectedCol = 0;

        while (cMatcher.find()) {
            String attrs = cMatcher.group(1);
            String body = cMatcher.group(2);

            Matcher rMatcher = Pattern.compile("r=\"([A-Z]+)[0-9]+\"").matcher(attrs);
            int colIndex = nextExpectedCol;
            if (rMatcher.find()) {
                colIndex = getColIndex(rMatcher.group(1));
            }

            while (nextExpectedCol < colIndex) {
                rowCells.add("");
                nextExpectedCol++;
            }

            boolean isShared = attrs.contains("t=\"s\"");
            String val = "";

            Matcher vMatcher = Pattern.compile("<v>(.*?)</v>").matcher(body);
            if (vMatcher.find()) {
                val = vMatcher.group(1).trim();
                if (isShared) {
                    try {
                        int sIndex = Integer.parseInt(val);
                        if (sIndex >= 0 && sIndex < sharedStrings.size()) {
                            val = sharedStrings.get(sIndex);
                        }
                    } catch (Exception ex) {}
                }
            } else {
                Matcher isMatcher = Pattern.compile("<is>.*?<t[^>]*>(.*?)</t>.*?</is>", Pattern.DOTALL).matcher(body);
                if (isMatcher.find()) {
                    val = isMatcher.group(1).trim();
                }
            }

            rowCells.add(unescapeXml(val));
            nextExpectedCol = colIndex + 1;
        }
        return rowCells;
    }

    private int getColIndex(String colStr) {
        int col = 0;
        for (int i = 0; i < colStr.length(); i++) {
            col = col * 26 + (colStr.charAt(i) - 'A' + 1);
        }
        return Math.max(0, col - 1);
    }

    private String unescapeXml(String text) {
        if (text == null) return "";
        return text.replace("&amp;", "&")
                   .replace("&lt;", "<")
                   .replace("&gt;", ">")
                   .replace("&quot;", "\"")
                   .replace("&apos;", "'");
    }

    private void populateFieldsFromMap(Map<String, String> map, String[] rawOrder) {
        txtRollNo.setText(getMapOrIndex(map, rawOrder, 0, "roll no", "rollno", "roll_no", "id", "studentid"));
        txtName.setText(getMapOrIndex(map, rawOrder, 1, "name", "full name", "fullname", "student name"));
        
        String importedMajor = getMapOrIndex(map, rawOrder, 2, "major", "programme", "department");
        String normMajor = findValidMajor(importedMajor);
        txtMajor.setText(normMajor != null ? normMajor : importedMajor);

        String importedYear = getMapOrIndex(map, rawOrder, 3, "year", "year / level", "level");
        String normYear = findValidYear(importedYear);
        txtYear.setText(normYear != null ? normYear : importedYear);

        String importedSem = getMapOrIndex(map, rawOrder, 4, "semester", "sem");
        String normSem = findValidSemester(importedSem);
        txtSemester.setText(normSem != null ? normSem : (importedSem.isEmpty() ? "I" : importedSem));

        txtPhone.setText(getMapOrIndex(map, rawOrder, 5, "phone", "phone number", "phone no", "phoneno"));
        txtDob.setText(getMapOrIndex(map, rawOrder, 6, "dob", "date of birth", "birthdate"));
        txtGender.setText(getMapOrIndex(map, rawOrder, 7, "gender", "sex"));
        
        String nat = getMapOrIndex(map, rawOrder, 8, "nationality", "country");
        txtNationality.setText(!nat.isEmpty() ? nat : "Myanmar");
        
        txtNrc.setText(getMapOrIndex(map, rawOrder, 9, "nrc", "nrc / id no.", "id no", "nrc number"));
        txtEmail.setText(getMapOrIndex(map, rawOrder, 10, "email", "email address"));
        txtCurrentAddress.setText(getMapOrIndex(map, rawOrder, 11, "current address", "address"));
        txtPermanentAddress.setText(getMapOrIndex(map, rawOrder, 12, "permanent address"));
        
        txtFatherName.setText(getMapOrIndex(map, rawOrder, 13, "father name", "father's name", "father"));
        txtFatherOcc.setText(getMapOrIndex(map, rawOrder, 14, "father occupation", "father occ"));
        txtFatherPhone.setText(getMapOrIndex(map, rawOrder, 15, "father phone", "father's phone"));
        txtFatherEmail.setText(getMapOrIndex(map, rawOrder, 16, "father email", "father's email"));
        
        txtMotherName.setText(getMapOrIndex(map, rawOrder, 17, "mother name", "mother's name", "mother"));
        txtMotherOcc.setText(getMapOrIndex(map, rawOrder, 18, "mother occupation", "mother occ"));
        txtMotherPhone.setText(getMapOrIndex(map, rawOrder, 19, "mother phone", "mother's phone"));
        txtMotherEmail.setText(getMapOrIndex(map, rawOrder, 20, "mother email", "mother's email"));
        
        txtPrevSchool.setText(getMapOrIndex(map, rawOrder, 21, "previous school", "school"));
        txtYearCompleted.setText(getMapOrIndex(map, rawOrder, 22, "year completed", "matriculation year"));
        txtAdditionalInfo.setText(getMapOrIndex(map, rawOrder, 23, "additional info", "remarks"));
    }

    private String getMapOrIndex(Map<String, String> map, String[] rawOrder, int index, String... keys) {
        for (String k : keys) {
            if (map.containsKey(k) && !map.get(k).trim().isEmpty()) {
                return map.get(k).trim();
            }
        }
        if (index < rawOrder.length && rawOrder[index] != null && !rawOrder[index].trim().isEmpty()) {
            return rawOrder[index].trim();
        }
        return "";
    }

    // ==========================================
    // VALIDATIONS & REGISTRATION LOGIC
    // ==========================================

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

    private String findValidSemester(String input) {
        if (input == null) return null;
        String s = input.trim().toLowerCase();
        if (s.equals("ii") || s.equals("2") || s.equals("semester ii") || s.equals("semester 2")) return "II";
        if (s.equals("i") || s.equals("1") || s.equals("semester i") || s.equals("semester 1")) return "I";
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
        fileChooser.setTitle("Select Passport Size Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files (*.png, *.jpg, *.jpeg, *.bmp)", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );

        if (passportImageView != null && passportImageView.getScene() != null) {
            Stage stage = (Stage) passportImageView.getScene().getWindow();
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                try {
                    selectedPhotoPath = file.toURI().toString();
                    Image image = new Image(selectedPhotoPath, 86, 110, false, true);
                    passportImageView.setImage(image);
                } catch (Exception e) {
                    showAlert("Image Error", "Could not load image: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }
    }

    @FXML
    public void handleAddStudent() {
        String rawRollNo = txtRollNo != null ? txtRollNo.getText().trim() : "";
        String rawYear = txtYear != null ? txtYear.getText().trim() : "";
        String rawMajor = txtMajor != null ? txtMajor.getText().trim() : "";
        String rawSem = txtSemester != null ? txtSemester.getText().trim() : "";
        String phoneInput = txtPhone != null ? txtPhone.getText().trim() : "";
        String emailInput = txtEmail != null ? txtEmail.getText().trim() : "";
        String dobInput = txtDob != null ? txtDob.getText().trim() : "";
        String nameInput = txtName != null ? txtName.getText().trim() : "";
        String yearCompInput = txtYearCompleted != null ? txtYearCompleted.getText().trim() : "";

        String fPhoneInput = txtFatherPhone != null ? txtFatherPhone.getText().trim() : "";
        String fEmailInput = txtFatherEmail != null ? txtFatherEmail.getText().trim() : "";
        String mPhoneInput = txtMotherPhone != null ? txtMotherPhone.getText().trim() : "";
        String mEmailInput = txtMotherEmail != null ? txtMotherEmail.getText().trim() : "";

        // 0. Empty Field Validation Check
        if (rawRollNo.isEmpty() || nameInput.isEmpty() || rawMajor.isEmpty() || 
            rawYear.isEmpty() || phoneInput.isEmpty() || emailInput.isEmpty() || dobInput.isEmpty()) {
            showAlert("Missing Information", "Please fill in all required student details before saving.", Alert.AlertType.ERROR);
            return;
        }

        // 1. Roll No Format Check
        if (!rawRollNo.matches("^[^-]+-[^-]+-\\d+$")) {
            showAlert("Invalid Roll No Format", "Roll No must be in the format year-major-number (e.g., II-CEIT-2).", Alert.AlertType.ERROR);
            return;
        }

        // 2. Validate Major case-insensitively against valid dropdown options
        String validatedMajor = findValidMajor(rawMajor);
        if (validatedMajor == null) {
            showAlert("Invalid Major", "Entered Major '" + rawMajor + "' does not match any valid Major:\nCE, ME, EC, EP, CEIT, MC, ChE, Arch, PE, MnE, Met, Tex, FE.", Alert.AlertType.ERROR);
            return;
        }

        // 3. Validate Year case-insensitively against valid dropdown options
        String validatedYear = findValidYear(rawYear);
        if (validatedYear == null) {
            showAlert("Invalid Year", "Entered Year '" + rawYear + "' does not match any valid Year:\nFirst Year, Second Year, Third Year, Fourth Year, Fifth Year.", Alert.AlertType.ERROR);
            return;
        }

        // 4. Validate Semester case-insensitively against valid dropdown options
        String validatedSemester = findValidSemester(rawSem);
        if (validatedSemester == null) {
            showAlert("Invalid Semester", "Entered Semester '" + rawSem + "' is invalid. Please enter 'I' or 'II'.", Alert.AlertType.ERROR);
            return;
        }

        // Auto-correct textfields directly in the form
        txtMajor.setText(validatedMajor);
        txtYear.setText(validatedYear);
        txtSemester.setText(validatedSemester);

        // 5. Roll No vs Dropdown Data Mismatch Verification
        String[] rollParts = rawRollNo.split("-");
        String rollYearRoman = rollParts[0].trim().toUpperCase();
        String rollMajor = rollParts[1].trim();
        String rollNumber = rollParts[2].trim();
        String expectedYearRoman = getRomanYear(validatedYear);

        boolean yearMismatch = !rollYearRoman.equalsIgnoreCase(expectedYearRoman);
        boolean majorMismatch = !rollMajor.equalsIgnoreCase(validatedMajor);

        if (yearMismatch || majorMismatch) {
            StringBuilder errorMsg = new StringBuilder("Mismatch detected between Roll No and entered student details:\n\n");
            if (yearMismatch) {
                errorMsg.append(String.format("• Year Mismatch: Roll No indicates '%s' but Year is '%s' (%s).\n", rollYearRoman, validatedYear, expectedYearRoman));
            }
            if (majorMismatch) {
                errorMsg.append(String.format("• Major Mismatch: Roll No indicates '%s' but Major is '%s'.\n", rollMajor, validatedMajor));
            }
            errorMsg.append("\nPlease correct either the Roll No or the corresponding Year / Major.");
            showAlert("Detail Mismatch Error", errorMsg.toString(), Alert.AlertType.ERROR);
            return;
        }

        // Standardize the Roll No's case for saving (e.g., ii-ceit-2 -> II-CEIT-2)
        String standardizedRollNo = expectedYearRoman + "-" + validatedMajor + "-" + rollNumber;
        txtRollNo.setText(standardizedRollNo);

        // 6. Date of Birth Validation
        if (!dobInput.isEmpty()) {
            if (dobInput.matches(".*[a-zA-Z]+.*") || !dobInput.matches("^\\d{1,2}-\\d{1,2}-\\d{4}$")) {
                showAlert("Invalid Date of Birth", "Date of Birth must be in the format number-month-year (e.g., 11-12-2003) and contain no letters.", Alert.AlertType.ERROR);
                return;
            }
        }

        // 7. Student Phone Number & Email Validation
        if (!phoneInput.matches("^09-\\d+$")) {
            showAlert("Invalid Phone Number", "Student phone number must be in the format 09-xxxxxxxx (starting with 09- followed by numbers).", Alert.AlertType.ERROR);
            return;
        }
        if (!emailInput.contains("@")) {
            showAlert("Invalid Email Address", "Student email address must contain '@'.", Alert.AlertType.ERROR);
            return;
        }

        // 8. Father's Contact Validations
        if (!fPhoneInput.isEmpty() && !fPhoneInput.matches("^09-\\d+$")) {
            showAlert("Invalid Father's Phone Number", "Father's phone number must be in the format 09-xxxxxxxx (starting with 09- followed by numbers).", Alert.AlertType.ERROR);
            return;
        }
        if (!fEmailInput.isEmpty() && !fEmailInput.contains("@")) {
            showAlert("Invalid Father's Email", "Father's email address must contain '@'.", Alert.AlertType.ERROR);
            return;
        }

        // 9. Mother's Contact Validations
        if (!mPhoneInput.isEmpty() && !mPhoneInput.matches("^09-\\d+$")) {
            showAlert("Invalid Mother's Phone Number", "Mother's phone number must be in the format 09-xxxxxxxx (starting with 09- followed by numbers).", Alert.AlertType.ERROR);
            return;
        }
        if (!mEmailInput.isEmpty() && !mEmailInput.contains("@")) {
            showAlert("Invalid Mother's Email", "Mother's email address must contain '@'.", Alert.AlertType.ERROR);
            return;
        }

        // 10. Year Completed Validation
        if (!yearCompInput.isEmpty() && !yearCompInput.matches("^\\d{4}$")) {
            showAlert("Invalid Year Completed", "Year Completed must be in the 4-digit format (e.g., 2020).", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Addition");
        confirmAlert.setHeaderText("Add New Student");
        confirmAlert.setContentText("Are you sure you want to add this new student to the system?");
        confirmAlert.getDialogPane().setMinWidth(480);

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        int newNo = StudentController.masterData != null ? StudentController.masterData.size() + 1 : 1;
        String gender = txtGender != null ? txtGender.getText().trim() : "";
        String nationality = txtNationality != null && !txtNationality.getText().trim().isEmpty() ? txtNationality.getText().trim() : "Myanmar";
        String nrc = txtNrc != null ? txtNrc.getText().trim() : "";
        String currAddr = txtCurrentAddress != null ? txtCurrentAddress.getText().trim() : "";
        String permAddr = txtPermanentAddress != null ? txtPermanentAddress.getText().trim() : "";
        String fName = txtFatherName != null ? txtFatherName.getText().trim() : "";
        String fOcc = txtFatherOcc != null ? txtFatherOcc.getText().trim() : "";
        String mName = txtMotherName != null ? txtMotherName.getText().trim() : "";
        String mOcc = txtMotherOcc != null ? txtMotherOcc.getText().trim() : "";
        String prevSchool = txtPrevSchool != null ? txtPrevSchool.getText().trim() : "";
        String addInfo = txtAdditionalInfo != null ? txtAdditionalInfo.getText().trim() : "";

        String finalPhotoPath = (selectedPhotoPath != null) ? selectedPhotoPath : DEFAULT_IMAGE_PATH;

        // Saves with standardized values (CEIT, Second Year, I, and II-CEIT-X)
        Student newStudent = new Student(newNo, standardizedRollNo, nameInput, validatedMajor, validatedYear, validatedSemester, phoneInput,
                dobInput, gender, nationality, nrc, emailInput, currAddr, permAddr,
                fName, fOcc, fPhoneInput, fEmailInput, mName, mOcc, mPhoneInput, mEmailInput,
                prevSchool, yearCompInput, addInfo, finalPhotoPath);

        if (StudentController.masterData != null) {
            StudentController.masterData.add(newStudent);
        }

        Session.clear();
        handleReturn();
    }

    @FXML
    public void handleResetData() {
        if (txtRollNo != null) txtRollNo.clear();
        if (txtDob != null) txtDob.clear();
        if (datePickerDob != null) datePickerDob.setValue(null);
        if (txtName != null) txtName.clear();
        if (txtGender != null) txtGender.clear();
        if (txtMajor != null) txtMajor.clear();
        if (txtNationality != null) txtNationality.clear();
        if (txtYear != null) txtYear.clear();
        if (txtSemester != null) txtSemester.setText("I");
        if (txtNrc != null) txtNrc.clear();
        if (txtPhone != null) txtPhone.clear();
        if (txtEmail != null) txtEmail.clear();
        if (txtCurrentAddress != null) txtCurrentAddress.clear();
        if (txtPermanentAddress != null) txtPermanentAddress.clear();
        if (txtFatherName != null) txtFatherName.clear();
        if (txtFatherOcc != null) txtFatherOcc.clear();
        if (txtFatherPhone != null) txtFatherPhone.clear();
        if (txtFatherEmail != null) txtFatherEmail.clear();
        if (txtMotherName != null) txtMotherName.clear();
        if (txtMotherOcc != null) txtMotherOcc.clear();
        if (txtMotherPhone != null) txtMotherPhone.clear();
        if (txtMotherEmail != null) txtMotherEmail.clear();
        if (txtPrevSchool != null) txtPrevSchool.clear();
        if (txtYearCompleted != null) txtYearCompleted.clear();
        if (txtAdditionalInfo != null) txtAdditionalInfo.clear();
        if (txtExcelPath != null) txtExcelPath.clear();
        selectedExcelFile = null;

        selectedPhotoPath = null;
        try {
            passportImageView.setImage(new Image(getClass().getResourceAsStream(DEFAULT_IMAGE_PATH)));
        } catch (Exception e) {}

        showAlert("Data Reset", "Form inputs have been cleared.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleReturn() {
        Session.clear();
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