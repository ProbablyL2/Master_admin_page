package application;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import application.dao.StudentDAO;

public class StudentController {

    @FXML
    private VBox rootPane;
    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TableColumn<Student, Integer> colNo;
    @FXML
    private TableColumn<Student, String> colRollNo;
    @FXML
    private TableColumn<Student, String> colName;
    @FXML
    private TableColumn<Student, String> colMajor;
    @FXML
    private TableColumn<Student, String> colYear;
    @FXML
    private TableColumn<Student, String> colSemester;
    @FXML
    private TableColumn<Student, String> colPhone;

    @FXML
    private Button modify;
    @FXML
    private Button remove;
    @FXML
    private Button add;

    @FXML
    private TextField searchField;

    public static ObservableList<Student> masterData = null;

    private FilteredList<Student> filteredData;
    private ComboBox<String> majorHeaderCombo;
    private ComboBox<String> yearHeaderCombo;
    private ComboBox<String> semesterHeaderCombo;

    @FXML
    public void initialize() {
        if (rootPane != null) {
            rootPane.setOnMouseClicked(event -> {
                studentTable.getSelectionModel().clearSelection();
                rootPane.requestFocus();
            });
        }

        colNo.setCellValueFactory(cell -> cell.getValue().noProperty().asObject());
        colRollNo.setCellValueFactory(cell -> cell.getValue().rollNoProperty());
        colName.setCellValueFactory(cell -> cell.getValue().nameProperty());
        colMajor.setCellValueFactory(cell -> cell.getValue().majorProperty());
        colYear.setCellValueFactory(cell -> cell.getValue().yearProperty());
        colSemester.setCellValueFactory(cell -> cell.getValue().semesterProperty());
        colPhone.setCellValueFactory(cell -> cell.getValue().phoneNoProperty());

        ObservableList<String> majorOptions = FXCollections.observableArrayList(
                "All Majors", "CE", "ME", "EC", "EP", "CEIT", "MC", "ChE", "Arch", "PE", "MnE", "Met", "Tex", "FE");

        ObservableList<String> yearOptions = FXCollections.observableArrayList(
                "All Years", "First Year", "Second Year", "Third Year", "Fourth Year", "Fifth Year");

        ObservableList<String> semesterOptions = FXCollections.observableArrayList(
                "All Semesters", "First Semester", "Second Semester");

        majorHeaderCombo = new ComboBox<>(majorOptions);
        majorHeaderCombo.setPromptText("Major");
        majorHeaderCombo.setMaxWidth(Double.MAX_VALUE);
        majorHeaderCombo.setValue("All Majors");
        colMajor.setGraphic(majorHeaderCombo);

        yearHeaderCombo = new ComboBox<>(yearOptions);
        yearHeaderCombo.setPromptText("Year");
        yearHeaderCombo.setMaxWidth(Double.MAX_VALUE);
        yearHeaderCombo.setValue("All Years");
        colYear.setGraphic(yearHeaderCombo);

        semesterHeaderCombo = new ComboBox<>(semesterOptions);
        semesterHeaderCombo.setPromptText("Semester");
        semesterHeaderCombo.setMaxWidth(Double.MAX_VALUE);
        semesterHeaderCombo.setValue("All Semesters");
        colSemester.setGraphic(semesterHeaderCombo);

        if (masterData == null) {
            StudentDAO student = new StudentDAO();

            List<Student> studentDAO = student.getAllStudent();

            masterData = FXCollections.observableArrayList(studentDAO);
            studentTable.setItems(masterData);
        }

        filteredData = new FilteredList<>(masterData, b -> true);

        SortedList<Student> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(studentTable.comparatorProperty());
        studentTable.setItems(sortedData);

        majorHeaderCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        yearHeaderCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        semesterHeaderCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        studentTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && studentTable.getSelectionModel().getSelectedItem() != null) {
                openStudentDetail(studentTable.getSelectionModel().getSelectedItem(), false);
            }
        });
    }

    @FXML
    public void handleModify() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openStudentDetail(selected, true);
        } else {
            showAlert("No Selection", "Please select a student row from the table to modify.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void handleRemove() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            masterData.remove(selected);

            int index = 1;
            for (Student s : masterData) {
                s.setNo(index++);
            }

            studentTable.getSelectionModel().clearSelection();
            studentTable.refresh();
        } else {
            showAlert("No Selection", "Please select a student row to remove.", Alert.AlertType.WARNING);
        }
    }

    private void openStudentDetail(Student student, boolean editMode) {
        try {
            StudentDAO studentDAO = new StudentDAO();
            Student fullStudent = studentDAO.getStudentbyId(student.getNo());

            Student targetStudent = (fullStudent != null) ? fullStudent : student;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentDetail.fxml"));
            Parent detailView = loader.load();

            StudentDetailController controller = loader.getController();
            controller.initData(targetStudent, editMode);

            BorderPane mainLayout = (BorderPane) studentTable.getScene().lookup("#mainBorderPane");
            if (mainLayout != null) {
                mainLayout.setCenter(detailView);
            } else {
                Parent root = studentTable.getScene().getRoot();
                if (root instanceof BorderPane) {
                    ((BorderPane) root).setCenter(detailView);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSearch() {
        applyFilters();
    }

    private void applyFilters() {
        String searchText = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String selectedMajor = majorHeaderCombo.getValue();
        String selectedYear = yearHeaderCombo.getValue();
        String selectedSemester = semesterHeaderCombo.getValue();
        StudentDAO studentDAO = new StudentDAO();
        List<Student> filteredStudents = studentDAO.getFilteredStudents(searchText, selectedMajor, selectedYear,
                selectedSemester);

        masterData = FXCollections.observableArrayList(filteredStudents);
        studentTable.setItems(masterData);
    }

    @FXML
    public void handleReset() {
        searchField.clear();
        majorHeaderCombo.setValue("All Majors");
        yearHeaderCombo.setValue("All Years");
        semesterHeaderCombo.setValue("All Semesters");
        studentTable.getSelectionModel().clearSelection();

        applyFilters();
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