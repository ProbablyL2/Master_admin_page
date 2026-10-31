package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TeacherDetailController {

    @FXML private Label lblFormTitle, lblPhotoAction;
    @FXML private Label lblTeacherId, lblDob, lblName, lblGender, lblDept, lblNationality, lblPosition, lblNrc;
    @FXML private Label lblPhone, lblEmail, lblCurrentAddress, lblPermanentAddress;
    @FXML private Label lblDegree, lblUniversity, lblYearCompleted, lblSpecialization, lblEmergency;

    @FXML private GridPane gridTeacherDetails;
    @FXML private GridPane gridContactInfo;
    @FXML private GridPane gridAcademicInfo;

    private TextField txtTeacherId, txtDob, txtName, txtGender, txtDept, txtNationality, txtPosition, txtNrc;
    private TextField txtPhone, txtEmail, txtCurrentAddress, txtPermanentAddress;
    private TextField txtDegree, txtUniversity, txtYearCompleted, txtSpecialization, txtEmergency;

    @FXML private ImageView passportImageView;
    @FXML private Button btnSave;
    @FXML private Button btnReset;
    @FXML private HBox topActionBar;
    @FXML private VBox contentContainer;

    @FXML private VBox importBox;
    @FXML private TextField txtExcelPath;
    @FXML private VBox dropZone;

    private Teacher currentTeacher;
    private boolean isEditMode = false;
    private boolean isAddMode = false;
    private boolean isUpdating = false;
    private File selectedExcelFile = null;
    private String updatedPhotoPath = null;
    private HBox bannerBox = null;
    private final String DEFAULT_IMAGE_PATH = "/freddy.jpg";

    private final String[] VALID_DEPTS = {"CE", "ME", "EC", "EP", "CEIT", "MC", "ChE", "Arch", "PE", "MnE", "Met", "Tex", "FE"};
    private final String[] VALID_POSITIONS = {"Professor & Head", "Professor", "Associate Professor", "Lecturer", "Assistant Lecturer", "Tutor"};

    public void initData(Teacher teacher) {
        initData(teacher, false);
    }

    public void initData(Teacher teacher, boolean editMode) {
        this.currentTeacher = teacher;
        this.isEditMode = editMode;
        this.isAddMode = false;

        if (importBox != null) {
            importBox.setVisible(false);
            importBox.setManaged(false);
        }

        if (lblFormTitle != null) {
            lblFormTitle.setText(editMode ? "FACULTY / TEACHER INFORMATION FORM (MODIFY)" : "FACULTY / TEACHER INFORMATION FORM (VIEW)");
        }
        if (lblPhotoAction != null) {
            lblPhotoAction.setVisible(editMode);
            lblPhotoAction.setManaged(editMode);
        }

        if (contentContainer != null && teacher != null) {
            if (bannerBox != null && contentContainer.getChildren().contains(bannerBox)) {
                contentContainer.getChildren().remove(bannerBox);
            }
            bannerBox = new HBox();
            bannerBox.setStyle("-fx-background-color: #dbeafe; -fx-border-color: #2563eb; -fx-border-width: 1.5; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 12 18;");
            
            String bannerText = String.format("⭐     %d     |     %s     |     %s     |     %s     |     %s",
                teacher.getNo(),
                teacher.getTeacherId(),
                teacher.getName(),
                teacher.getDepartment(),
                teacher.getPosition()
            );

            Label bannerLabel = new Label(bannerText);
            bannerLabel.setStyle("-fx-text-fill: #1e3a8a; -fx-font-weight: bold; -fx-font-size: 15px;");
            bannerBox.getChildren().add(bannerLabel);
            contentContainer.getChildren().add(0, bannerBox);
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

            if (btnSave != null) { btnSave.setVisible(false); btnSave.setManaged(false); }
            if (btnReset != null) { btnReset.setVisible(false); btnReset.setManaged(false); }
        } else {
            createTextFields(teacher);
            setupTeacherBidirectionalSync();

            if (btnSave != null) {
                btnSave.setText("Save Changes");
                btnSave.setVisible(true);
                btnSave.setManaged(true);
            }
            if (btnReset != null) {
                btnReset.setVisible(true);
                btnReset.setManaged(true);
            }
        }

        loadImage(teacher.getPhotoPath());
    }

    private void setupTeacherBidirectionalSync() {
        if (!isEditMode) return;

        // When user modifies Teacher ID -> update Department text field
        if (txtTeacherId != null) {
            txtTeacherId.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal && !isUpdating) {
                    syncDeptFromTeacherId();
                }
            });
            txtTeacherId.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!isUpdating && newVal != null && newVal.contains("-")) {
                    String[] parts = newVal.trim().split("-");
                    if (parts.length >= 2) {
                        String deptCode = findValidDepartment(parts[1].trim());
                        if (deptCode != null && txtDept != null) {
                            isUpdating = true;
                            try {
                                txtDept.setText(deptCode);
                            } finally {
                                isUpdating = false;
                            }
                        }
                    }
                }
            });
        }

        // When user modifies Department -> update Teacher ID field
        if (txtDept != null) {
            txtDept.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal && !isUpdating) {
                    syncTeacherIdFromDept();
                }
            });
        }
    }

    private void syncDeptFromTeacherId() {
        if (txtTeacherId == null || txtTeacherId.getText() == null) return;
        String raw = txtTeacherId.getText().trim();
        String[] parts = raw.split("-");
        if (parts.length >= 2) {
            String deptCode = findValidDepartment(parts[1].trim());
            if (deptCode != null && txtDept != null) {
                isUpdating = true;
                try {
                    txtDept.setText(deptCode);
                } finally {
                    isUpdating = false;
                }
            }
        }
    }

    private void syncTeacherIdFromDept() {
        if (txtDept == null || txtTeacherId == null) return;
        String rawDept = txtDept.getText() != null ? txtDept.getText().trim() : "";
        String validDept = findValidDepartment(rawDept);
        if (validDept != null) {
            String currentId = txtTeacherId.getText() != null ? txtTeacherId.getText().trim() : "";
            if (currentId.contains("-")) {
                String[] parts = currentId.split("-");
                String prefix = parts[0].trim();
                String num = parts[parts.length - 1].trim();

                isUpdating = true;
                try {
                    txtTeacherId.setText(prefix + "-" + validDept + "-" + num);
                    txtDept.setText(validDept);
                } finally {
                    isUpdating = false;
                }
            }
        }
    }

    public void initAddMode() {
        this.currentTeacher = null;
        this.isEditMode = false;
        this.isAddMode = true;

        if (importBox != null) {
            importBox.setVisible(true);
            importBox.setManaged(true);
        }

        if (lblFormTitle != null) {
            lblFormTitle.setText("FACULTY / TEACHER INFORMATION FORM (ADD NEW TEACHER)");
        }
        if (lblPhotoAction != null) {
            lblPhotoAction.setText("📷 Upload Photo");
            lblPhotoAction.setVisible(true);
            lblPhotoAction.setManaged(true);
        }

        Teacher blankTeacher = new Teacher(
            TeacherController.masterData != null ? TeacherController.masterData.size() + 1 : 1,
            "", "CEIT", "Tutor", "",
            "TCH-CEIT-101", "", "", "Myanmar", "", "",
            "", "", "", "", "", "", "", DEFAULT_IMAGE_PATH
        );

        createTextFields(blankTeacher);

        txtTeacherId.setText("");
        txtTeacherId.setPromptText("e.g. TCH-CEIT-101");

        String sessionUser = Session.getUsername();
        String sessionEmail = Session.getEmail();
        txtName.setText(sessionUser != null && !sessionUser.isEmpty() ? sessionUser : "");
        txtName.setPromptText("Faculty Full Name");
        txtEmail.setText(sessionEmail != null && !sessionEmail.isEmpty() ? sessionEmail : "");
        txtEmail.setPromptText("teacher@ytu.edu.mm");

        txtDept.setText("");
        txtDept.setPromptText("e.g. CEIT");
        txtPosition.setText("");
        txtPosition.setPromptText("e.g. Lecturer");
        txtPhone.setText("");
        txtPhone.setPromptText("09-231231231");
        txtEmergency.setText("");
        txtEmergency.setPromptText("09-231231231");

        if (btnSave != null) {
            btnSave.setText("Add New Teacher");
            btnSave.setVisible(true);
            btnSave.setManaged(true);
        }
        if (btnReset != null) {
            btnReset.setText("Reset Data");
            btnReset.setVisible(true);
            btnReset.setManaged(true);
        }

        loadImage(DEFAULT_IMAGE_PATH);
    }

    private void createTextFields(Teacher teacher) {
        replaceLabelWithTextField(lblTeacherId, txtTeacherId = new TextField(teacher != null ? teacher.getTeacherId() : ""));
        replaceLabelWithTextField(lblDob, txtDob = new TextField(teacher != null ? teacher.getDob() : ""));
        replaceLabelWithTextField(lblName, txtName = new TextField(teacher != null ? teacher.getName() : ""));
        replaceLabelWithTextField(lblGender, txtGender = new TextField(teacher != null ? teacher.getGender() : ""));
        replaceLabelWithTextField(lblDept, txtDept = new TextField(teacher != null ? teacher.getDepartment() : ""));
        replaceLabelWithTextField(lblNationality, txtNationality = new TextField(teacher != null ? teacher.getNationality() : "Myanmar"));
        replaceLabelWithTextField(lblPosition, txtPosition = new TextField(teacher != null ? teacher.getPosition() : ""));
        replaceLabelWithTextField(lblNrc, txtNrc = new TextField(teacher != null ? teacher.getNrc() : ""));

        replaceLabelWithTextField(lblPhone, txtPhone = new TextField(teacher != null ? teacher.getPhoneNo() : ""));
        replaceLabelWithTextField(lblEmail, txtEmail = new TextField(teacher != null ? teacher.getEmail() : ""));
        replaceLabelWithTextField(lblCurrentAddress, txtCurrentAddress = new TextField(teacher != null ? teacher.getCurrentAddress() : ""));
        replaceLabelWithTextField(lblPermanentAddress, txtPermanentAddress = new TextField(teacher != null ? teacher.getPermanentAddress() : ""));

        replaceLabelWithTextField(lblDegree, txtDegree = new TextField(teacher != null ? teacher.getDegree() : ""));
        replaceLabelWithTextField(lblUniversity, txtUniversity = new TextField(teacher != null ? teacher.getUniversity() : ""));
        replaceLabelWithTextField(lblYearCompleted, txtYearCompleted = new TextField(teacher != null ? teacher.getYearCompleted() : ""));
        replaceLabelWithTextField(lblSpecialization, txtSpecialization = new TextField(teacher != null ? teacher.getSpecialization() : ""));
        replaceLabelWithTextField(lblEmergency, txtEmergency = new TextField(teacher != null ? teacher.getEmergencyContact() : ""));

        txtPhone.setPromptText("09-231231231");
        txtEmail.setPromptText("teacher@ytu.edu.mm");
        txtEmergency.setPromptText("09-231231231");
        txtDob.setPromptText("DD-MM-YYYY");
        txtYearCompleted.setPromptText("e.g. 2015");
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

    // ==========================================
    // EXCEL / CSV IMPORT & DRAG-AND-DROP
    // ==========================================

    @FXML
    public void handleBrowseExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Faculty Excel / CSV File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Spreadsheet & CSV Files (*.xlsx, *.csv)", "*.xlsx", "*.csv"),
            new FileChooser.ExtensionFilter("All Files (*.*)", "*.*")
        );

        Stage stage = (Stage) topActionBar.getScene().getWindow();
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
        if (dropZone != null && event.getGestureSource() != dropZone && event.getDragboard().hasFiles()) {
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
        if (dropZone != null) {
            dropZone.setStyle("-fx-border-color: #94a3b8; -fx-border-width: 1.5; -fx-border-style: dashed; -fx-border-radius: 6; -fx-background-color: #f8fafc; -fx-background-radius: 6; -fx-cursor: hand;");
        }
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
        if (dropZone != null) {
            dropZone.setStyle("-fx-border-color: #94a3b8; -fx-border-width: 1.5; -fx-border-style: dashed; -fx-border-radius: 6; -fx-background-color: #f8fafc; -fx-cursor: hand;");
        }
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
                showAlert("No Data Row", "The selected file does not contain a row of faculty data below the header.", Alert.AlertType.WARNING);
                return;
            }

            String[] values = dataLine.split(delimiter, -1);
            Map<String, String> map = new HashMap<>();

            for (int i = 0; i < headers.length && i < values.length; i++) {
                String cleanHeader = headers[i].replace("\"", "").trim().toLowerCase();
                String cleanVal = values[i].replace("\"", "").trim();
                map.put(cleanHeader, cleanVal);
            }

            populateTeacherFieldsFromMap(map, values);
            showAlert("Import Successful", "Teacher data from " + file.getName() + " has been loaded into the form.", Alert.AlertType.INFORMATION);
        }
    }

    private boolean importFromNativeXlsx(File file) {
        try (ZipFile zip = new ZipFile(file)) {
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

            populateTeacherFieldsFromMap(map, rawValues);
            showAlert("Import Successful", "Faculty data from Excel file (" + file.getName() + ") has been loaded into the form.", Alert.AlertType.INFORMATION);
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

    private void populateTeacherFieldsFromMap(Map<String, String> map, String[] rawOrder) {
        if (txtTeacherId != null) txtTeacherId.setText(getMapOrIndex(map, rawOrder, 0, "teacher id", "id", "teacherid", "employee id"));
        if (txtName != null) txtName.setText(getMapOrIndex(map, rawOrder, 1, "name", "full name", "fullname", "teacher name"));
        
        String importedDept = getMapOrIndex(map, rawOrder, 2, "department", "dept");
        String normDept = findValidDepartment(importedDept);
        if (txtDept != null) txtDept.setText(normDept != null ? normDept : importedDept);

        String importedPos = getMapOrIndex(map, rawOrder, 3, "position", "rank", "title");
        String normPos = findValidPosition(importedPos);
        if (txtPosition != null) txtPosition.setText(normPos != null ? normPos : importedPos);

        if (txtPhone != null) txtPhone.setText(getMapOrIndex(map, rawOrder, 4, "phone", "phone number", "phone-no", "mobile"));
        if (txtDob != null) txtDob.setText(getMapOrIndex(map, rawOrder, 5, "dob", "date of birth", "birthdate"));
        if (txtGender != null) txtGender.setText(getMapOrIndex(map, rawOrder, 6, "gender", "sex"));
        
        String nat = getMapOrIndex(map, rawOrder, 7, "nationality", "country");
        if (txtNationality != null) txtNationality.setText(!nat.isEmpty() ? nat : "Myanmar");
        
        if (txtNrc != null) txtNrc.setText(getMapOrIndex(map, rawOrder, 8, "nrc", "nrc / id no.", "nrc number"));
        if (txtEmail != null) txtEmail.setText(getMapOrIndex(map, rawOrder, 9, "email", "email address"));
        if (txtCurrentAddress != null) txtCurrentAddress.setText(getMapOrIndex(map, rawOrder, 10, "current address", "address"));
        if (txtPermanentAddress != null) txtPermanentAddress.setText(getMapOrIndex(map, rawOrder, 11, "permanent address"));
        
        if (txtDegree != null) txtDegree.setText(getMapOrIndex(map, rawOrder, 12, "highest degree", "degree"));
        if (txtUniversity != null) txtUniversity.setText(getMapOrIndex(map, rawOrder, 13, "university", "institution", "college"));
        if (txtYearCompleted != null) txtYearCompleted.setText(getMapOrIndex(map, rawOrder, 14, "year completed", "graduation year"));
        if (txtSpecialization != null) txtSpecialization.setText(getMapOrIndex(map, rawOrder, 15, "specialization", "area", "major"));
        if (txtEmergency != null) txtEmergency.setText(getMapOrIndex(map, rawOrder, 16, "emergency contact", "emergency phone", "emergency"));
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

    private String findValidDepartment(String input) {
        if (input == null) return null;
        for (String d : VALID_DEPTS) {
            if (d.equalsIgnoreCase(input.trim())) {
                return d;
            }
        }
        return null;
    }

    private String findValidPosition(String input) {
        if (input == null) return null;
        for (String p : VALID_POSITIONS) {
            if (p.equalsIgnoreCase(input.trim())) {
                return p;
            }
        }
        return null;
    }

    @FXML
    public void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Faculty Passport Photo");
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
        String rawId = txtTeacherId != null ? txtTeacherId.getText().trim() : "";
        String rawName = txtName != null ? txtName.getText().trim() : "";
        String rawDept = txtDept != null ? txtDept.getText().trim() : "";
        String rawPos = txtPosition != null ? txtPosition.getText().trim() : "";
        String rawPhone = txtPhone != null ? txtPhone.getText().trim() : "";
        String rawEmail = txtEmail != null ? txtEmail.getText().trim() : "";
        String rawDob = txtDob != null ? txtDob.getText().trim() : "";
        String rawEmergency = txtEmergency != null ? txtEmergency.getText().trim() : "";
        String rawYearComp = txtYearCompleted != null ? txtYearCompleted.getText().trim() : "";

        // 0. Empty Field Validation
        if (rawId.isEmpty() || rawName.isEmpty() || rawDept.isEmpty() || rawPos.isEmpty() || rawPhone.isEmpty()) {
            showAlert("Missing Information", "Please fill in Teacher ID, Full Name, Department, Position, and Phone Number.", Alert.AlertType.ERROR);
            return;
        }

        // 1. Strict Structural Format Check for Teacher ID (e.g. TCH-CEIT-111)
        if (!rawId.matches("^[a-zA-Z]+-[a-zA-Z0-9]+-\\d+$")) {
            showAlert("Invalid Teacher ID Format", 
                "Teacher ID must be in the format: [Prefix]-[Department]-[Number]\n(e.g., TCH-CEIT-101 or TCH-EC-112).\n\n'" + rawId + "' is not a valid format.", 
                Alert.AlertType.ERROR);
            return;
        }

        String[] parts = rawId.split("-");
        String idPrefix = parts[0].trim();
        String idDeptCode = parts[1].trim();
        String idNum = parts[2].trim();

        // 2. Validate Department Code from Teacher ID
        String deptFromId = findValidDepartment(idDeptCode);
        if (deptFromId == null) {
            showAlert("Invalid Department in Teacher ID", 
                "The department code '" + idDeptCode + "' in Teacher ID is invalid.\nValid departments are:\n" +
                "CE, ME, EC, EP, CEIT, MC, ChE, Arch, PE, MnE, Met, Tex, FE.", 
                Alert.AlertType.ERROR);
            return;
        }

        // 3. Validate Department Field directly
        String validatedDept = findValidDepartment(rawDept);
        if (validatedDept == null) {
            showAlert("Invalid Department", 
                "Department '" + rawDept + "' does not match any valid department:\n" +
                "CE, ME, EC, EP, CEIT, MC, ChE, Arch, PE, MnE, Met, Tex, FE.", 
                Alert.AlertType.ERROR);
            return;
        }

        // 4. Validate Position Field directly
        String validatedPos = findValidPosition(rawPos);
        if (validatedPos == null) {
            showAlert("Invalid Position", 
                "Position '" + rawPos + "' does not match any valid position:\n" +
                "Professor & Head, Professor, Associate Professor, Lecturer, Assistant Lecturer, Tutor.", 
                Alert.AlertType.ERROR);
            return;
        }

        // Add Mode strict mismatch check vs Modify Mode bidirectional sync
        String standardizedId = rawId;
        if (isAddMode) {
            if (!deptFromId.equalsIgnoreCase(validatedDept)) {
                showAlert("Detail Mismatch Error", 
                    String.format("Mismatch detected between Teacher ID and Department:\n\n• Teacher ID indicates Department '%s' but selected Department is '%s'.\n\nPlease correct either the Teacher ID or Department.", idDeptCode, validatedDept), 
                    Alert.AlertType.ERROR);
                return;
            }
            standardizedId = idPrefix + "-" + validatedDept + "-" + idNum;
        } else if (isEditMode) {
            // If user updated the Teacher ID to a new department, propagate to Department field
            if (!deptFromId.equalsIgnoreCase(validatedDept)) {
                validatedDept = deptFromId;
            }
            standardizedId = idPrefix + "-" + validatedDept + "-" + idNum;
        }

        // Normalize Casing on form
        isUpdating = true;
        try {
            txtTeacherId.setText(standardizedId);
            txtDept.setText(validatedDept);
            txtPosition.setText(validatedPos);
        } finally {
            isUpdating = false;
        }

        // 5. Contact & Date Validations
        if (!rawPhone.matches("^09-\\d+$")) {
            showAlert("Invalid Phone Number", "Teacher phone number must be in the format 09-xxxxxxxx (starting with 09- followed by numbers).", Alert.AlertType.ERROR);
            return;
        }
        if (!rawEmail.isEmpty() && !rawEmail.contains("@")) {
            showAlert("Invalid Email Address", "Email address must contain '@'.", Alert.AlertType.ERROR);
            return;
        }
        if (!rawEmergency.isEmpty() && !rawEmergency.matches("^09-\\d+$")) {
            showAlert("Invalid Emergency Contact", "Emergency contact must be in the format 09-xxxxxxxx (starting with 09- followed by numbers).", Alert.AlertType.ERROR);
            return;
        }
        if (!rawDob.isEmpty() && (rawDob.matches(".*[a-zA-Z]+.*") || !rawDob.matches("^\\d{1,2}-\\d{1,2}-\\d{4}$"))) {
            showAlert("Invalid Date of Birth", "Date of Birth must be in the format DD-MM-YYYY (e.g., 15-05-1985).", Alert.AlertType.ERROR);
            return;
        }
        if (!rawYearComp.isEmpty() && !rawYearComp.matches("^\\d{4}$")) {
            showAlert("Invalid Year Completed", "Year Completed must be a 4-digit year (e.g., 2012).", Alert.AlertType.ERROR);
            return;
        }

        String finalPhoto = updatedPhotoPath != null ? updatedPhotoPath : (currentTeacher != null ? currentTeacher.getPhotoPath() : DEFAULT_IMAGE_PATH);

        if (isAddMode) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Addition");
            confirmAlert.setHeaderText("Add New Faculty Member");
            confirmAlert.setContentText("Are you sure you want to register this teacher into the system?");
            confirmAlert.getDialogPane().setMinWidth(480);

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) return;

            int newNo = TeacherController.masterData != null ? TeacherController.masterData.size() + 1 : 1;
            Teacher newTeacher = new Teacher(
                newNo, rawName, validatedDept, validatedPos, rawPhone,
                standardizedId, rawDob, txtGender.getText().trim(), txtNationality.getText().trim(),
                txtNrc.getText().trim(), rawEmail, txtCurrentAddress.getText().trim(), txtPermanentAddress.getText().trim(),
                txtDegree.getText().trim(), txtUniversity.getText().trim(), rawYearComp, txtSpecialization.getText().trim(),
                rawEmergency, finalPhoto
            );

            if (TeacherController.masterData != null) {
                TeacherController.masterData.add(newTeacher);
            }
            Session.clear();
        } else if (isEditMode && currentTeacher != null) {
            currentTeacher.setTeacherId(standardizedId);
            currentTeacher.setName(rawName);
            currentTeacher.setDepartment(validatedDept);
            currentTeacher.setPosition(validatedPos);
            currentTeacher.setPhoneNo(rawPhone);
            currentTeacher.setDob(rawDob);
            currentTeacher.setGender(txtGender.getText().trim());
            currentTeacher.setNationality(txtNationality.getText().trim());
            currentTeacher.setNrc(txtNrc.getText().trim());
            currentTeacher.setEmail(rawEmail);
            currentTeacher.setCurrentAddress(txtCurrentAddress.getText().trim());
            currentTeacher.setPermanentAddress(txtPermanentAddress.getText().trim());
            currentTeacher.setDegree(txtDegree.getText().trim());
            currentTeacher.setUniversity(txtUniversity.getText().trim());
            currentTeacher.setYearCompleted(rawYearComp);
            currentTeacher.setSpecialization(txtSpecialization.getText().trim());
            currentTeacher.setEmergencyContact(rawEmergency);
            if (updatedPhotoPath != null) {
                currentTeacher.photoPathProperty().set(updatedPhotoPath);
            }
        }

        handleReturn();
    }

    @FXML
    public void handleResetData() {
        if (isAddMode) {
            if (txtTeacherId != null) txtTeacherId.clear();
            if (txtName != null) txtName.clear();
            if (txtDept != null) txtDept.clear();
            if (txtPosition != null) txtPosition.clear();
            if (txtPhone != null) txtPhone.clear();
            if (txtDob != null) txtDob.clear();
            if (txtGender != null) txtGender.clear();
            if (txtNationality != null) txtNationality.setText("Myanmar");
            if (txtNrc != null) txtNrc.clear();
            if (txtEmail != null) txtEmail.clear();
            if (txtCurrentAddress != null) txtCurrentAddress.clear();
            if (txtPermanentAddress != null) txtPermanentAddress.clear();
            if (txtDegree != null) txtDegree.clear();
            if (txtUniversity != null) txtUniversity.clear();
            if (txtYearCompleted != null) txtYearCompleted.clear();
            if (txtSpecialization != null) txtSpecialization.clear();
            if (txtEmergency != null) txtEmergency.clear();
            if (txtExcelPath != null) txtExcelPath.clear();
            selectedExcelFile = null;
            updatedPhotoPath = null;
            loadImage(DEFAULT_IMAGE_PATH);
            showAlert("Data Reset", "Teacher form inputs have been cleared.", Alert.AlertType.INFORMATION);
        } else if (currentTeacher != null) {
            currentTeacher.resetToOriginal();
            initData(currentTeacher, isEditMode);
            showAlert("Data Reset", "Faculty record has been reset to its original state.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleReturn() {
        Session.clear();
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