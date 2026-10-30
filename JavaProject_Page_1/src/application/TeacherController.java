package application;

import java.io.IOException;
import java.util.List;

import application.dao.TeacherDAO;
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

public class TeacherController {

    @FXML
    private VBox rootPane;
    @FXML
    private TableView<Teacher> teacherTable;
    @FXML
    private TableColumn<Teacher, Integer> colNo;
    @FXML
    private TableColumn<Teacher, String> colName;
    @FXML
    private TableColumn<Teacher, String> colDepartment;
    @FXML
    private TableColumn<Teacher, String> colPosition;
    @FXML
    private TableColumn<Teacher, String> colPhone;
    @FXML
    private Button modify;
    @FXML
    private Button add;
    @FXML
    private Button remove;

    @FXML
    private TextField searchField;

    public static ObservableList<Teacher> masterData = null;
    private FilteredList<Teacher> filteredData;

    private ComboBox<String> deptHeaderCombo;
    private ComboBox<String> posHeaderCombo;

    @FXML
    public void initialize() {
        if (rootPane != null) {
            rootPane.setOnMouseClicked(event -> {
                teacherTable.getSelectionModel().clearSelection();
                rootPane.requestFocus();
            });
        }

        colNo.setCellValueFactory(cellData -> cellData.getValue().noProperty().asObject());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colDepartment.setCellValueFactory(cellData -> cellData.getValue().departmentProperty());
        colPosition.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        colPhone.setCellValueFactory(cellData -> cellData.getValue().phoneNoProperty());

        ObservableList<String> departmentOptions = FXCollections.observableArrayList(
                "All Departments", "CE", "ME", "EC", "EP", "CEIT", "MC", "ChE", "Arch", "PE", "MnE", "Met", "Tex",
                "FE");

        ObservableList<String> positionOptions = FXCollections.observableArrayList(
                "All Positions", "Professor & Head", "Professor", "Associate Professor", "Lecturer",
                "Assistant Lecturer", "Tutor");

        deptHeaderCombo = new ComboBox<>(departmentOptions);
        deptHeaderCombo.setPromptText("Department");
        deptHeaderCombo.setMaxWidth(Double.MAX_VALUE);
        deptHeaderCombo.setValue("All Departments");
        colDepartment.setGraphic(deptHeaderCombo);

        posHeaderCombo = new ComboBox<>(positionOptions);
        posHeaderCombo.setPromptText("Position");
        posHeaderCombo.setMaxWidth(Double.MAX_VALUE);
        posHeaderCombo.setValue("All Positions");
        colPosition.setGraphic(posHeaderCombo);

        if (masterData == null) {
            TeacherDAO teacherDAO = new TeacherDAO();
            List<Teacher> teachers = teacherDAO.getAllTeacher();

            masterData = FXCollections.observableArrayList(teachers);
            teacherTable.setItems(masterData);
        }

        filteredData = new FilteredList<>(masterData, b -> true);
        SortedList<Teacher> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(teacherTable.comparatorProperty());
        teacherTable.setItems(sortedData);

        deptHeaderCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        posHeaderCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        teacherTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && teacherTable.getSelectionModel().getSelectedItem() != null) {
                openTeacherDetail(teacherTable.getSelectionModel().getSelectedItem(), false);
            }
        });
    }

    @FXML
    public void handleModify() {
        Teacher selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openTeacherDetail(selected, true);
        } else {
            showAlert("No Selection", "Please select a teacher row from the table to modify.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void handleAdd() {
        int newNo = masterData.size() + 1;
        Teacher newTeacher = new Teacher(newNo, "New Teacher", "CEIT", "Tutor", "09-000000000");
        masterData.add(newTeacher);
        openTeacherDetail(newTeacher, true);
    }

    @FXML
    public void handleRemove() {
        Teacher selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            masterData.remove(selected);
            int index = 1;
            for (Teacher t : masterData) {
                t.setNo(index++);
            }
            teacherTable.getSelectionModel().clearSelection();
            teacherTable.refresh();
        } else {
            showAlert("No Selection", "Please select a teacher row to remove.", Alert.AlertType.WARNING);
        }
    }

    private void openTeacherDetail(Teacher teacher, boolean editMode) {
        try {
            TeacherDAO teacherDAO = new TeacherDAO();
            Teacher fullTeacher = teacherDAO.getFullTeacher(teacher.getNo());
            Teacher targetTeacher = (fullTeacher != null) ? fullTeacher : teacher;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("TeacherDetail.fxml"));
            Parent detailView = loader.load();

            TeacherDetailController controller = loader.getController();
            controller.initData(targetTeacher, editMode);

            BorderPane mainLayout = (BorderPane) teacherTable.getScene().lookup("#mainBorderPane");
            if (mainLayout != null) {
                mainLayout.setCenter(detailView);
            } else {
                Parent root = teacherTable.getScene().getRoot();
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
        String selectedDept = deptHeaderCombo.getValue();
        String selectedPos = posHeaderCombo.getValue();

        TeacherDAO teacherDAO = new TeacherDAO();
        List<Teacher> filteredTeachers = teacherDAO.getFilteredTeachers(searchText, selectedDept, selectedPos);
        masterData = FXCollections.observableArrayList(filteredTeachers);
        teacherTable.setItems(masterData);
    }

    @FXML
    public void handleReset() {
        searchField.clear();
        deptHeaderCombo.setValue("All Departments");
        posHeaderCombo.setValue("All Positions");
        teacherTable.getSelectionModel().clearSelection();

        applyFilters();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinWidth(520);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        if (teacherTable != null && teacherTable.getScene() != null) {
            alert.initOwner(teacherTable.getScene().getWindow());
        }
        alert.showAndWait();
    }
}