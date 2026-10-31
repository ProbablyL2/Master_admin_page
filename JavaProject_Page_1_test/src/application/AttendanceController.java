package application;

import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AttendanceController {

    @FXML private VBox rootPane, matrixFilterBox, dailyFilterBox;
    @FXML private Button btnClassView, btnIndividualView, btnMatrixSummary, btnDailyLog, btnExport, btnBackToList;
    @FXML private HBox subModeBar, hintBox;
    @FXML private Label studentHeaderLabel;
    
    // Matrix / Individual filter controls
    @FXML private ComboBox<String> comboDepartment, comboYear, comboSemester;
    @FXML private DatePicker startDatePicker, endDatePicker;
    @FXML private TextField searchField;
    @FXML private CheckBox chkLowAttendance;

    // Daily Log filter controls
    @FXML private DatePicker dailyDatePicker;
    @FXML private ComboBox<String> dailyPeriodCombo;
    @FXML private TextField dailySearchField;
    @FXML private CheckBox chkPresent, chkAbsent, chkExcuse;

    @FXML private TableView<AttendanceRecord> matrixTable;
    @FXML private TableColumn<AttendanceRecord, Integer> colNoMatrix;
    @FXML private TableColumn<AttendanceRecord, String> colRollMatrix, colNameMatrix, colDeptMatrix, colYearMatrix, colSubjMatrix, colOverallMatrix;

    @FXML private TableView<AttendanceRecord> dailyLogTable;
    @FXML private TableColumn<AttendanceRecord, Integer> colNoLog;
    @FXML private TableColumn<AttendanceRecord, String> colRollLog, colNameLog, colDateLog, colSubjLog, colStatusLog, colMarkedByLog, colRemarksLog;
    @FXML private TableColumn<AttendanceRecord, Integer> colPeriodLog;

    @FXML private TableView<AttendanceRecord> individualTable;
    @FXML private TableColumn<AttendanceRecord, Integer> colNoIndiv;
    @FXML private TableColumn<AttendanceRecord, String> colSubjIndiv, colAttendedIndiv, colPercentageIndiv, colRemarksIndiv;

    private ObservableList<AttendanceRecord> matrixData;
    private ObservableList<AttendanceRecord> dailyLogData;
    private ObservableList<AttendanceRecord> individualData;

    private FilteredList<AttendanceRecord> filteredMatrixData;
    private FilteredList<AttendanceRecord> filteredDailyData;
    private FilteredList<AttendanceRecord> filteredIndivData;

    private boolean isMatrixMode = true;
    private boolean isClassViewActive = true;
    private boolean isStudentLocked = false;

    private String selectedStudentRollNo = null;
    private String selectedStudentName = null;

    @FXML
    public void initialize() {
        if (rootPane != null) {
            rootPane.setOnMouseClicked(event -> {
                matrixTable.getSelectionModel().clearSelection();
                dailyLogTable.getSelectionModel().clearSelection();
                individualTable.getSelectionModel().clearSelection();
                rootPane.requestFocus();
            });
        }

        matrixTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        dailyLogTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        individualTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Matrix columns
        colNoMatrix.setCellValueFactory(cell -> cell.getValue().noProperty().asObject());
        colRollMatrix.setCellValueFactory(cell -> cell.getValue().rollNoProperty());
        colNameMatrix.setCellValueFactory(cell -> cell.getValue().studentNameProperty());
        colDeptMatrix.setCellValueFactory(cell -> cell.getValue().departmentProperty());
        colYearMatrix.setCellValueFactory(cell -> cell.getValue().yearProperty());
        colSubjMatrix.setCellValueFactory(cell -> cell.getValue().subjectProperty());
        colOverallMatrix.setCellValueFactory(cell -> cell.getValue().percentageProperty().asString("%.1f%%"));

        // Daily Log columns
        colNoLog.setCellValueFactory(cell -> cell.getValue().noProperty().asObject());
        colRollLog.setCellValueFactory(cell -> cell.getValue().rollNoProperty());
        colNameLog.setCellValueFactory(cell -> cell.getValue().studentNameProperty());
        colDateLog.setCellValueFactory(cell -> cell.getValue().dateProperty());
        colPeriodLog.setCellValueFactory(cell -> cell.getValue().periodProperty().asObject());
        colSubjLog.setCellValueFactory(cell -> cell.getValue().subjectProperty());
        colStatusLog.setCellValueFactory(cell -> cell.getValue().statusProperty());
        colMarkedByLog.setCellValueFactory(cell -> cell.getValue().markedByProperty());
        colRemarksLog.setCellValueFactory(cell -> cell.getValue().remarksProperty());

        // Individual columns
        colNoIndiv.setCellValueFactory(cell -> cell.getValue().noProperty().asObject());
        colSubjIndiv.setCellValueFactory(cell -> cell.getValue().subjectProperty());
        colAttendedIndiv.setCellValueFactory(cell -> cell.getValue().attendedTotalProperty());
        colPercentageIndiv.setCellValueFactory(cell -> cell.getValue().percentageProperty().asString("%.1f%%"));
        colRemarksIndiv.setCellValueFactory(cell -> cell.getValue().remarksProperty());

        // Combo Boxes population
        comboDepartment.setItems(FXCollections.observableArrayList(
            "All Departments", "CE", "ME", "EC", "EP", "CEIT", "MC", "ChE", "Arch", "PE", "MnE", "Met", "Tex", "FE"
        ));
        comboDepartment.setValue("All Departments");

        comboYear.setItems(FXCollections.observableArrayList(
            "All Years", "First Year", "Second Year", "Third Year", "Fourth Year", "Fifth Year"
        ));
        comboYear.setValue("All Years");

        comboSemester.setItems(FXCollections.observableArrayList(
            "All Semesters", "Semester I", "Semester II"
        ));
        comboSemester.setValue("All Semesters");

        dailyPeriodCombo.setItems(FXCollections.observableArrayList(
            "All Periods", "1", "2", "3", "4", "5", "6", "7", "8", "9"
        ));
        dailyPeriodCombo.setValue("All Periods");

        // --- MATRIX SUMMARY DATA (All 10 Students) ---
        matrixData = FXCollections.observableArrayList(
            new AttendanceRecord(1, "III-CEIT-1", "Mg Mg", "CEIT", "Third Year", "Semester I", "Advanced Java", "38/40", 95.0, LocalDate.of(2026, 7, 20)),
            new AttendanceRecord(2, "III-CEIT-2", "Aye Aye", "CEIT", "Third Year", "Semester I", "Data Structures", "35/40", 87.5, LocalDate.of(2026, 7, 22)),
            new AttendanceRecord(3, "V-ChE-2", "Su Su", "ChE", "Fifth Year", "Semester II", "Chemical Kinetics", "28/40", 70.0, LocalDate.of(2026, 7, 26)),
            new AttendanceRecord(4, "IV-ME-1", "Aung Aung", "ME", "Fourth Year", "Semester I", "Thermodynamics", "36/40", 90.0, LocalDate.of(2026, 7, 26)),
            new AttendanceRecord(5, "III-EC-4", "Hla Hla", "EC", "Third Year", "Semester II", "Signal Processing", "26/40", 65.0, LocalDate.of(2026, 7, 28)),
            new AttendanceRecord(6, "I-CE-1", "Kyaw Kyaw", "CE", "First Year", "Semester I", "Engineering Drawing", "35/40", 87.5, LocalDate.of(2026, 8, 2)),
            new AttendanceRecord(7, "II-EP-3", "Zin Mar", "EP", "Second Year", "Semester I", "Circuit Theory", "38/42", 90.5, LocalDate.of(2026, 8, 5)),
            new AttendanceRecord(8, "IV-MC-2", "Phone Naing", "MC", "Fourth Year", "Semester II", "Microcontrollers", "27/40", 67.5, LocalDate.of(2026, 8, 10)),
            new AttendanceRecord(9, "V-Arch-1", "Nilar", "Arch", "Fifth Year", "Semester II", "Urban Planning", "38/40", 95.0, LocalDate.of(2026, 8, 15)),
            new AttendanceRecord(10, "I-PE-2", "Hein Htet", "PE", "First Year", "Semester II", "Calculus II", "24/40", 60.0, LocalDate.of(2026, 8, 20))
        );

        // --- EXPANDED DAILY LOG DATA ---
        dailyLogData = FXCollections.observableArrayList(
            new AttendanceRecord(1, "III-CEIT-1", "Mg Mg", "CEIT", "Third Year", "Semester I", LocalDate.of(2026, 7, 20), 1, "Advanced Java", "Present", "U Ba", "On time"),
            new AttendanceRecord(2, "III-CEIT-1", "Mg Mg", "CEIT", "Third Year", "Semester I", LocalDate.of(2026, 7, 20), 2, "Data Structures", "Present", "U Ba", "On time"),
            new AttendanceRecord(3, "III-CEIT-2", "Aye Aye", "CEIT", "Third Year", "Semester I", LocalDate.of(2026, 7, 22), 3, "Algorithms", "Present", "U Myint", "On time"),
            new AttendanceRecord(4, "V-ChE-2", "Su Su", "ChE", "Fifth Year", "Semester II", LocalDate.of(2026, 7, 26), 1, "Chemical Kinetics", "Absent", "Daw Mya", "Unexcused"),
            new AttendanceRecord(5, "IV-ME-1", "Aung Aung", "ME", "Fourth Year", "Semester I", LocalDate.of(2026, 7, 26), 4, "Thermodynamics", "Excused", "U Tun", "Medical Leave"),
            new AttendanceRecord(6, "III-EC-4", "Hla Hla", "EC", "Third Year", "Semester II", LocalDate.of(2026, 7, 28), 5, "Signal Processing", "Absent", "U Hla", "Unexcused"),
            new AttendanceRecord(7, "I-CE-1", "Kyaw Kyaw", "CE", "First Year", "Semester I", LocalDate.of(2026, 8, 2), 2, "Engineering Drawing", "Present", "Daw Hlaing", "On time"),
            new AttendanceRecord(8, "II-EP-3", "Zin Mar", "EP", "Second Year", "Semester I", LocalDate.of(2026, 8, 5), 6, "Circuit Theory", "Present", "U Soe", "On time"),
            new AttendanceRecord(9, "IV-MC-2", "Phone Naing", "MC", "Fourth Year", "Semester II", LocalDate.of(2026, 8, 10), 7, "Microcontrollers", "Absent", "U Kyaw", "Unexcused"),
            new AttendanceRecord(10, "V-Arch-1", "Nilar", "Arch", "Fifth Year", "Semester II", LocalDate.of(2026, 8, 15), 3, "Urban Planning", "Present", "Daw Khin", "On time"),
            new AttendanceRecord(11, "I-PE-2", "Hein Htet", "PE", "First Year", "Semester II", LocalDate.of(2026, 8, 20), 8, "Calculus II", "Absent", "U Ba", "Unexcused"),
            new AttendanceRecord(12, "III-CEIT-1", "Mg Mg", "CEIT", "Third Year", "Semester II", LocalDate.of(2026, 8, 21), 9, "Computer Networks", "Excused", "Daw Mya", "Family Emergency")
        );

        // --- EXPANDED INDIVIDUAL STUDENT VIEW DATA ---
        individualData = FXCollections.observableArrayList(
            new AttendanceRecord(1, "III-CEIT-1", "Mg Mg", "CEIT", "Third Year", "Semester I", "Advanced Java", "38/40", 95.0, LocalDate.of(2026, 7, 20)),
            new AttendanceRecord(2, "III-CEIT-1", "Mg Mg", "CEIT", "Third Year", "Semester I", "Software Engineering", "37/40", 92.5, LocalDate.of(2026, 7, 21)),
            new AttendanceRecord(3, "III-CEIT-1", "Mg Mg", "CEIT", "Third Year", "Semester II", "Computer Networks", "35/40", 87.5, LocalDate.of(2026, 7, 22)),
            new AttendanceRecord(4, "III-CEIT-1", "Mg Mg", "CEIT", "Third Year", "Semester II", "Database Systems", "36/40", 90.0, LocalDate.of(2026, 7, 23)),
            new AttendanceRecord(5, "III-CEIT-1", "Mg Mg", "CEIT", "Fourth Year", "Semester I", "Advanced Microprocessors", "30/40", 75.0, LocalDate.of(2026, 8, 5)),

            new AttendanceRecord(1, "III-CEIT-2", "Aye Aye", "CEIT", "Third Year", "Semester I", "Advanced Java", "36/40", 90.0, LocalDate.of(2026, 7, 20)),
            new AttendanceRecord(2, "III-CEIT-2", "Aye Aye", "CEIT", "Third Year", "Semester I", "Data Structures", "35/40", 87.5, LocalDate.of(2026, 7, 22)),
            new AttendanceRecord(3, "III-CEIT-2", "Aye Aye", "CEIT", "Third Year", "Semester II", "Operating Systems", "34/40", 85.0, LocalDate.of(2026, 7, 24)),

            new AttendanceRecord(1, "V-ChE-2", "Su Su", "ChE", "Fourth Year", "Semester II", "Fluid Mechanics", "31/40", 77.5, LocalDate.of(2026, 7, 24)),
            new AttendanceRecord(2, "V-ChE-2", "Su Su", "ChE", "Fifth Year", "Semester II", "Chemical Kinetics", "28/40", 70.0, LocalDate.of(2026, 7, 26)),
            new AttendanceRecord(3, "V-ChE-2", "Su Su", "ChE", "Fifth Year", "Semester II", "Reactor Design", "32/40", 80.0, LocalDate.of(2026, 8, 10)),
            new AttendanceRecord(4, "V-ChE-2", "Su Su", "ChE", "Fifth Year", "Semester II", "Process Control", "29/40", 72.5, LocalDate.of(2026, 8, 12)),

            new AttendanceRecord(1, "IV-ME-1", "Aung Aung", "ME", "Fourth Year", "Semester I", "Thermodynamics", "36/40", 90.0, LocalDate.of(2026, 7, 26)),
            new AttendanceRecord(2, "IV-ME-1", "Aung Aung", "ME", "Fourth Year", "Semester I", "Fluid Mechanics", "35/40", 87.5, LocalDate.of(2026, 8, 12)),
            new AttendanceRecord(3, "IV-ME-1", "Aung Aung", "ME", "Fourth Year", "Semester II", "Machine Design", "37/40", 92.5, LocalDate.of(2026, 8, 14)),

            new AttendanceRecord(1, "III-EC-4", "Hla Hla", "EC", "Third Year", "Semester II", "Signal Processing", "26/40", 65.0, LocalDate.of(2026, 7, 28)),
            new AttendanceRecord(2, "III-EC-4", "Hla Hla", "EC", "Third Year", "Semester II", "Digital Communications", "27/40", 67.5, LocalDate.of(2026, 7, 30)),
            new AttendanceRecord(3, "III-EC-4", "Hla Hla", "EC", "Third Year", "Semester II", "Microprocessors", "30/40", 75.0, LocalDate.of(2026, 8, 2)),

            new AttendanceRecord(1, "I-CE-1", "Kyaw Kyaw", "CE", "First Year", "Semester I", "Engineering Drawing", "35/40", 87.5, LocalDate.of(2026, 8, 2)),
            new AttendanceRecord(2, "I-CE-1", "Kyaw Kyaw", "CE", "First Year", "Semester I", "Engineering Mechanics", "36/40", 90.0, LocalDate.of(2026, 8, 4)),

            new AttendanceRecord(1, "II-EP-3", "Zin Mar", "EP", "Second Year", "Semester I", "Circuit Theory", "38/42", 90.5, LocalDate.of(2026, 8, 5)),
            new AttendanceRecord(2, "II-EP-3", "Zin Mar", "EP", "Second Year", "Semester I", "Electromagnetics", "37/40", 92.5, LocalDate.of(2026, 8, 7)),

            new AttendanceRecord(1, "IV-MC-2", "Phone Naing", "MC", "Fourth Year", "Semester II", "Microcontrollers", "27/40", 67.5, LocalDate.of(2026, 8, 10)),
            new AttendanceRecord(2, "IV-MC-2", "Phone Naing", "MC", "Fourth Year", "Semester II", "Robotics Lab", "28/40", 70.0, LocalDate.of(2026, 8, 12)),

            new AttendanceRecord(1, "V-Arch-1", "Nilar", "Arch", "Fifth Year", "Semester II", "Urban Planning", "38/40", 95.0, LocalDate.of(2026, 8, 15)),
            new AttendanceRecord(2, "V-Arch-1", "Nilar", "Arch", "Fifth Year", "Semester II", "Architectural Design V", "39/40", 97.5, LocalDate.of(2026, 8, 17)),

            new AttendanceRecord(1, "I-PE-2", "Hein Htet", "PE", "First Year", "Semester II", "Calculus II", "24/40", 60.0, LocalDate.of(2026, 8, 20)),
            new AttendanceRecord(2, "I-PE-2", "Hein Htet", "PE", "First Year", "Semester II", "Engineering Physics", "25/40", 62.5, LocalDate.of(2026, 8, 22))
        );

        filteredMatrixData = new FilteredList<>(matrixData, b -> true);
        filteredDailyData = new FilteredList<>(dailyLogData, b -> true);
        filteredIndivData = new FilteredList<>(individualData, b -> true);

        matrixTable.setItems(filteredMatrixData);
        dailyLogTable.setItems(filteredDailyData);
        individualTable.setItems(filteredIndivData);

        matrixTable.setRowFactory(tv -> {
            TableRow<AttendanceRecord> row = createLowAttendanceRowFactory();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    AttendanceRecord student = row.getItem();
                    selectedStudentRollNo = student.getRollNo();
                    selectedStudentName = student.getStudentName();
                    isStudentLocked = true;
                    switchToIndividualView();
                }
            });
            return row;
        });

        // --- STRIKING ROW COLOR FACTORY FOR DAILY LOG TABLE ---
        dailyLogTable.setRowFactory(tv -> {
            TableRow<AttendanceRecord> row = new TableRow<AttendanceRecord>() {
                @Override
                protected void updateItem(AttendanceRecord item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else {
                        String status = item.getStatus() != null ? item.getStatus().trim() : "";
                        applyDailyRowStyle(this, status, isSelected());
                    }
                }
            };

            row.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (row.getItem() != null && !row.isEmpty()) {
                    String status = row.getItem().getStatus() != null ? row.getItem().getStatus().trim() : "";
                    applyDailyRowStyle(row, status, newVal);
                }
            });

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    AttendanceRecord student = row.getItem();
                    selectedStudentRollNo = student.getRollNo();
                    selectedStudentName = student.getStudentName();
                    isStudentLocked = true;
                    switchToIndividualView();
                }
            });
            return row;
        });

        applyFilters();
    }

    private void applyDailyRowStyle(TableRow<AttendanceRecord> row, String status, boolean selected) {
        if (status.equalsIgnoreCase("Present")) {
            if (selected) {
                row.setStyle("-fx-background-color: #a7f3d0;"); // Darker mint green when selected
            } else {
                row.setStyle("-fx-background-color: #d1fae5;"); // Soft vibrant mint green
            }
        } else if (status.equalsIgnoreCase("Absent")) {
            if (selected) {
                row.setStyle("-fx-background-color: #fca5a5;"); // Darker coral red when selected
            } else {
                row.setStyle("-fx-background-color: #fee2e2;"); // Soft striking coral red
            }
        } else if (status.equalsIgnoreCase("Excused") || status.equalsIgnoreCase("Excuse")) {
            if (selected) {
                row.setStyle("-fx-background-color: #fde68a;"); // Darker amber when selected
            } else {
                row.setStyle("-fx-background-color: #fef3c7;"); // Warm amber / gold
            }
        } else {
            row.setStyle("");
        }
    }

    private TableRow<AttendanceRecord> createLowAttendanceRowFactory() {
        TableRow<AttendanceRecord> row = new TableRow<AttendanceRecord>() {
            @Override
            protected void updateItem(AttendanceRecord item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.isLowAttendance()) {
                    if (isSelected()) {
                        setStyle("-fx-background-color: #fca5a5;");
                    } else {
                        setStyle("-fx-background-color: #fee2e2;");
                    }
                } else {
                    setStyle("");
                }
            }
        };

        row.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (row.getItem() != null && row.getItem().isLowAttendance()) {
                if (newVal) {
                    row.setStyle("-fx-background-color: #fca5a5;");
                } else {
                    row.setStyle("-fx-background-color: #fee2e2;");
                }
            }
        });
        return row;
    }

    @FXML
    private void switchToClassView() {
        isClassViewActive = true;
        isStudentLocked = false;
        selectedStudentRollNo = null;
        selectedStudentName = null;

        subModeBar.setVisible(true);
        subModeBar.setManaged(true);
        
        startDatePicker.setVisible(true);
        startDatePicker.setManaged(true);
        endDatePicker.setVisible(true);
        endDatePicker.setManaged(true);

        hintBox.setVisible(true);
        hintBox.setManaged(true);

        studentHeaderLabel.setVisible(false);
        studentHeaderLabel.setManaged(false);

        if (isMatrixMode) {
            matrixFilterBox.setVisible(true);
            matrixFilterBox.setManaged(true);
            dailyFilterBox.setVisible(false);
            dailyFilterBox.setManaged(false);
            matrixTable.setVisible(true);
            matrixTable.setManaged(true);
            dailyLogTable.setVisible(false);
            dailyLogTable.setManaged(false);
        } else {
            matrixFilterBox.setVisible(false);
            matrixFilterBox.setManaged(false);
            dailyFilterBox.setVisible(true);
            dailyFilterBox.setManaged(true);
            matrixTable.setVisible(false);
            matrixTable.setManaged(false);
            dailyLogTable.setVisible(true);
            dailyLogTable.setManaged(true);
        }
        
        individualTable.setVisible(false);
        individualTable.setManaged(false);
        comboDepartment.setVisible(true);
        comboDepartment.setManaged(true);
        
        btnBackToList.setVisible(false);
        btnBackToList.setManaged(false);
        btnExport.setText("Export Matrix Report");
        
        updateActiveButtonStyles(btnClassView, btnIndividualView);
        applyFilters();
    }

    @FXML
    private void switchToIndividualView() {
        isClassViewActive = false;

        subModeBar.setVisible(false);
        subModeBar.setManaged(false);
        
        matrixFilterBox.setVisible(true);
        matrixFilterBox.setManaged(true);
        dailyFilterBox.setVisible(false);
        dailyFilterBox.setManaged(false);

        startDatePicker.setVisible(false);
        startDatePicker.setManaged(false);
        endDatePicker.setVisible(false);
        endDatePicker.setManaged(false);

        hintBox.setVisible(false);
        hintBox.setManaged(false);

        matrixTable.setVisible(false);
        matrixTable.setManaged(false);
        dailyLogTable.setVisible(false);
        dailyLogTable.setManaged(false);
        
        individualTable.setVisible(true);
        individualTable.setManaged(true);

        comboDepartment.setVisible(!isStudentLocked);
        comboDepartment.setManaged(!isStudentLocked);

        btnBackToList.setVisible(isStudentLocked);
        btnBackToList.setManaged(isStudentLocked);

        if (isStudentLocked && selectedStudentRollNo != null) {
            studentHeaderLabel.setText("👤 Student Details: " + selectedStudentRollNo + " - " + selectedStudentName);
            studentHeaderLabel.setVisible(true);
            studentHeaderLabel.setManaged(true);
        } else {
            studentHeaderLabel.setVisible(false);
            studentHeaderLabel.setManaged(false);
        }
        
        btnExport.setText("Export Student Audit");
        updateActiveButtonStyles(btnIndividualView, btnClassView);

        applyFilters();
    }

    @FXML
    private void handleBackToList() {
        isStudentLocked = false;
        selectedStudentRollNo = null;
        selectedStudentName = null;
        btnBackToList.setVisible(false);
        btnBackToList.setManaged(false);
        
        studentHeaderLabel.setVisible(false);
        studentHeaderLabel.setManaged(false);

        comboDepartment.setVisible(true);
        comboDepartment.setManaged(true);
        comboDepartment.setValue("All Departments");
        comboYear.setValue("All Years");
        comboSemester.setValue("All Semesters");
        searchField.clear();
        
        applyFilters();
    }

    @FXML
    private void switchToMatrixSummary() {
        isMatrixMode = true;
        matrixFilterBox.setVisible(true);
        matrixFilterBox.setManaged(true);
        dailyFilterBox.setVisible(false);
        dailyFilterBox.setManaged(false);

        matrixTable.setVisible(true);
        matrixTable.setManaged(true);
        dailyLogTable.setVisible(false);
        dailyLogTable.setManaged(false);

        updateActiveButtonStyles(btnMatrixSummary, btnDailyLog);
        applyFilters();
    }

    @FXML
    private void switchToDailyLog() {
        isMatrixMode = false;
        matrixFilterBox.setVisible(false);
        matrixFilterBox.setManaged(false);
        dailyFilterBox.setVisible(true);
        dailyFilterBox.setManaged(true);

        matrixTable.setVisible(false);
        matrixTable.setManaged(false);
        dailyLogTable.setVisible(true);
        dailyLogTable.setManaged(true);

        updateActiveButtonStyles(btnDailyLog, btnMatrixSummary);
        applyFilters();
    }

    private void updateActiveButtonStyles(Button activeBtn, Button inactiveBtn) {
        activeBtn.getStyleClass().remove("reset-button");
        if (!activeBtn.getStyleClass().contains("search-button")) {
            activeBtn.getStyleClass().add("search-button");
        }

        inactiveBtn.getStyleClass().remove("search-button");
        if (!inactiveBtn.getStyleClass().contains("reset-button")) {
            inactiveBtn.getStyleClass().add("reset-button");
        }
    }

    @FXML
    private void applyFilters() {
        if (isClassViewActive && !isMatrixMode) {
            LocalDate selectedDate = dailyDatePicker.getValue();
            String selectedPeriod = dailyPeriodCombo.getValue();
            String searchText = dailySearchField.getText() == null ? "" : dailySearchField.getText().toLowerCase().trim();

            boolean pChecked = chkPresent.isSelected();
            boolean aChecked = chkAbsent.isSelected();
            boolean eChecked = chkExcuse.isSelected();
            boolean anyRemarkChecked = pChecked || aChecked || eChecked;

            filteredDailyData.setPredicate(record -> {
                boolean matchesDate = (selectedDate == null) || (record.getDateObj() != null && record.getDateObj().equals(selectedDate));
                boolean matchesPeriod = (selectedPeriod == null || selectedPeriod.equals("All Periods")) || 
                    String.valueOf(record.getPeriod()).equals(selectedPeriod);
                boolean matchesSearch = searchText.isEmpty() || 
                    record.getStudentName().toLowerCase().contains(searchText) ||
                    record.getRollNo().toLowerCase().contains(searchText) ||
                    record.getSubject().toLowerCase().contains(searchText);

                boolean matchesRemark = true;
                if (anyRemarkChecked) {
                    String rem = record.getRemarks() != null ? record.getRemarks().toLowerCase() : "";
                    String stat = record.getStatus() != null ? record.getStatus().toLowerCase() : "";
                    
                    boolean isPresentMatch = pChecked && (rem.contains("on time") || stat.contains("present"));
                    boolean isAbsentMatch = aChecked && (rem.contains("unexcused") || stat.contains("absent"));
                    boolean isExcuseMatch = eChecked && (rem.contains("medical") || rem.contains("family") || stat.contains("excused"));

                    matchesRemark = isPresentMatch || isAbsentMatch || isExcuseMatch;
                }

                return matchesDate && matchesPeriod && matchesSearch && matchesRemark;
            });

        } else if (isClassViewActive && isMatrixMode) {
            String searchText = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();
            String selectedDept = comboDepartment.getValue();
            String selectedYear = comboYear.getValue();
            String selectedSemester = comboSemester.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            boolean lowOnly = chkLowAttendance.isSelected();

            filteredMatrixData.setPredicate(record -> {
                boolean matchesSearch = searchText.isEmpty() || 
                    record.getStudentName().toLowerCase().contains(searchText) ||
                    record.getRollNo().toLowerCase().contains(searchText);
                boolean matchesDept = (selectedDept == null || selectedDept.equals("All Departments")) || 
                    record.getDepartment().equalsIgnoreCase(selectedDept);
                boolean matchesYear = (selectedYear == null || selectedYear.equals("All Years")) || 
                    matchesYearHelper(record, selectedYear);
                boolean matchesSemester = (selectedSemester == null || selectedSemester.equals("All Semesters")) || 
                    record.getSemester().equalsIgnoreCase(selectedSemester);
                boolean matchesDate = (startDate == null || endDate == null) || 
                    (record.getDateObj() != null && !record.getDateObj().isBefore(startDate) && !record.getDateObj().isAfter(endDate));
                boolean matchesLow = !lowOnly || record.isLowAttendance();

                return matchesSearch && matchesDept && matchesYear && matchesSemester && matchesDate && matchesLow;
            });

        } else {
            String searchText = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();
            String selectedDept = comboDepartment.getValue();
            String selectedYear = comboYear.getValue();
            String selectedSemester = comboSemester.getValue();
            boolean lowOnly = chkLowAttendance.isSelected();

            filteredIndivData.setPredicate(record -> {
                if (isStudentLocked && selectedStudentRollNo != null) {
                    boolean matchesStudent = record.getRollNo().equalsIgnoreCase(selectedStudentRollNo);
                    boolean matchesYear = (selectedYear == null || selectedYear.equals("All Years")) || 
                        matchesYearHelper(record, selectedYear);
                    boolean matchesSemester = (selectedSemester == null || selectedSemester.equals("All Semesters")) || 
                        record.getSemester().equalsIgnoreCase(selectedSemester);
                    boolean matchesLow = !lowOnly || record.isLowAttendance();
                    
                    return matchesStudent && matchesYear && matchesSemester && matchesLow;
                }

                boolean matchesDept = (selectedDept == null || selectedDept.equals("All Departments")) || 
                    record.getDepartment().equalsIgnoreCase(selectedDept);
                boolean matchesYear = (selectedYear == null || selectedYear.equals("All Years")) || 
                    matchesYearHelper(record, selectedYear);
                boolean matchesSemester = (selectedSemester == null || selectedSemester.equals("All Semesters")) || 
                    record.getSemester().equalsIgnoreCase(selectedSemester);
                boolean matchesSearch = searchText.isEmpty() || 
                    record.getSubject().toLowerCase().contains(searchText) ||
                    record.getStudentName().toLowerCase().contains(searchText) ||
                    record.getRollNo().toLowerCase().contains(searchText);
                boolean matchesLow = !lowOnly || record.isLowAttendance();

                return matchesDept && matchesYear && matchesSemester && matchesSearch && matchesLow;
            });
        }
    }

    private boolean matchesYearHelper(AttendanceRecord record, String selectedYear) {
        String rollNo = record.getRollNo() != null ? record.getRollNo().trim().toUpperCase() : "";
        String romanPrefix = "";
        
        if (rollNo.contains("-")) {
            romanPrefix = rollNo.substring(0, rollNo.indexOf("-"));
        }

        String expectedRoman = "";
        if (selectedYear.equalsIgnoreCase("First Year")) expectedRoman = "I";
        else if (selectedYear.equalsIgnoreCase("Second Year")) expectedRoman = "II";
        else if (selectedYear.equalsIgnoreCase("Third Year")) expectedRoman = "III";
        else if (selectedYear.equalsIgnoreCase("Fourth Year")) expectedRoman = "IV";
        else if (selectedYear.equalsIgnoreCase("Fifth Year")) expectedRoman = "V";

        if (!expectedRoman.isEmpty() && romanPrefix.equals(expectedRoman)) {
            return true;
        }

        return record.getYear() != null && record.getYear().equalsIgnoreCase(selectedYear);
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        comboDepartment.setValue("All Departments");
        comboYear.setValue("All Years");
        comboSemester.setValue("All Semesters");
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        chkLowAttendance.setSelected(false);

        dailyDatePicker.setValue(null);
        dailyPeriodCombo.setValue("All Periods");
        dailySearchField.clear();
        chkPresent.setSelected(false);
        chkAbsent.setSelected(false);
        chkExcuse.setSelected(false);

        applyFilters();
    }

    @FXML
    private void handleExport() {
        System.out.println("Exporting report...");
    }
}