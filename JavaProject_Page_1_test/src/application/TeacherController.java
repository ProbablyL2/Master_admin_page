package application;

import java.io.IOException;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    private TableColumn<Teacher, String> colTeacherId;
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
        if (colTeacherId != null) {
            colTeacherId.setCellValueFactory(
                    cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTeacherId()));
        }
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
            masterData = FXCollections.observableArrayList(
                    new Teacher(1, "Dr. Aung Kyaw", "CEIT", "Professor & Head", "09-123456789", "TCH-CEIT-101",
                            "12-03-1975", "Male", "Myanmar", "12/LaMaTa(N)001122", "aungkyaw@ytu.edu.mm",
                            "Kamayut, Yangon", "Kamayut, Yangon", "Ph.D. in Computer Science",
                            "Yangon Technological University", "2005", "Artificial Intelligence & Distributed Systems",
                            "09-112233445", null),
                    new Teacher(2, "Daw Mya Mya", "CEIT", "Associate Professor", "09-987654321", "TCH-CEIT-102",
                            "18-07-1980", "Female", "Myanmar", "12/BaHnTa(N)003344", "myamya@ytu.edu.mm",
                            "Bahan, Yangon", "Bahan, Yangon", "Ph.D. in Information Technology",
                            "Yangon Technological University", "2010", "Software Engineering & Data Architecture",
                            "09-223344556", null),
                    new Teacher(3, "U Win Bo", "ChE", "Lecturer", "09-555666777", "TCH-ChE-103", "05-11-1984", "Male",
                            "Myanmar", "12/SaKaNa(N)005566", "winbo@ytu.edu.mm", "Sanchaung, Yangon",
                            "Sanchaung, Yangon", "M.E. Chemical Engineering", "Yangon Technological University", "2012",
                            "Process Optimization & Petroleum Chemistry", "09-334455667", null),
                    new Teacher(4, "Dr. Hla Hla", "EC", "Professor", "09-111222333", "TCH-EC-104", "22-09-1972",
                            "Female", "Myanmar", "12/InSeIn(N)007788", "hlahla.tch@ytu.edu.mm", "Insein, Yangon",
                            "Insein, Yangon", "Ph.D. Electronic Engineering", "University of Tokyo", "2002",
                            "VLSI & Digital Signal Processing", "09-445566778", null),
                    new Teacher(5, "U Kyaw Swar", "ME", "Assistant Lecturer", "09-444555666", "TCH-ME-105",
                            "14-04-1988", "Male", "Myanmar", "12/MaGaTa(N)009900", "kyawswar@ytu.edu.mm",
                            "Mayangone, Yangon", "Mayangone, Yangon", "M.E. Mechanical Engineering",
                            "Yangon Technological University", "2015", "Thermodynamics & Fluid Dynamics",
                            "09-556677889", null),
                    new Teacher(6, "Daw Su Su", "ChE", "Lecturer", "09-777888999", "TCH-ChE-106", "30-01-1985",
                            "Female", "Myanmar", "12/TaMaNa(N)112233", "susu.tch@ytu.edu.mm", "Tamwe, Yangon",
                            "Tamwe, Yangon", "M.E. Chemical Engineering", "Yangon Technological University", "2013",
                            "Biochemical Separation Technology", "09-667788990", null),
                    new Teacher(7, "Dr. Min Thu", "CE", "Associate Professor", "09-333222111", "TCH-CE-107",
                            "10-10-1978", "Male", "Myanmar", "12/AhLaNe(N)223344", "minthu@ytu.edu.mm",
                            "Ahlone, Yangon", "Ahlone, Yangon", "Ph.D. Civil Engineering", "AIT Thailand", "2008",
                            "Structural Engineering & Earthquake Mechanics", "09-778899001", null),
                    new Teacher(8, "Daw Thida", "CEIT", "Tutor", "09-666555444", "TCH-CEIT-108", "03-03-1995", "Female",
                            "Myanmar", "12/PaBaTo(N)334455", "thida@ytu.edu.mm", "Pazundaung, Yangon",
                            "Pazundaung, Yangon", "B.E. Information Technology", "Yangon Technological University",
                            "2018", "Computer Networks & Cloud Computing", "09-889900112", null),
                    new Teacher(9, "U Phyo Wai", "CEIT", "Lecturer", "09-888777666", "TCH-CEIT-109", "25-08-1986",
                            "Male", "Myanmar", "12/YaKaNa(N)445566", "phyowai@ytu.edu.mm", "Yankin, Yangon",
                            "Yankin, Yangon", "M.E. Computer Technology", "Yangon Technological University", "2014",
                            "Cybersecurity & Cryptography", "09-990011223", null),
                    new Teacher(10, "Dr. Zin Mar", "EP", "Professor", "09-222333444", "TCH-EP-110", "11-12-1974",
                            "Female", "Myanmar", "12/DaGaNa(N)556677", "zinmar@ytu.edu.mm", "Dagon, Yangon",
                            "Dagon, Yangon", "Ph.D. Electrical Power", "Nanyang Technological University", "2004",
                            "Renewable Energy Systems & Smart Grids", "09-123987456", null));
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TeacherCreatePrompt.fxml"));
            Parent createPromptView = loader.load();

            BorderPane mainLayout = (BorderPane) teacherTable.getScene().lookup("#mainBorderPane");
            if (mainLayout != null) {
                mainLayout.setCenter(createPromptView);
            } else {
                Parent root = teacherTable.getScene().getRoot();
                if (root instanceof BorderPane) {
                    ((BorderPane) root).setCenter(createPromptView);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openTeacherAddView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TeacherDetail.fxml"));
            Parent addView = loader.load();

            TeacherDetailController controller = loader.getController();
            controller.initAddMode();

            BorderPane mainLayout = (BorderPane) teacherTable.getScene().lookup("#mainBorderPane");
            if (mainLayout != null) {
                mainLayout.setCenter(addView);
            } else {
                Parent root = teacherTable.getScene().getRoot();
                if (root instanceof BorderPane) {
                    ((BorderPane) root).setCenter(addView);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRemove() {
        Teacher selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Are you sure you want to remove this faculty member?");
            confirmAlert.setContentText("Teacher ID: " + selected.getTeacherId() + "\nName: " + selected.getName());
            confirmAlert.getDialogPane().setMinWidth(450);
            confirmAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                masterData.remove(selected);
                int index = 1;
                for (Teacher t : masterData) {
                    t.setNo(index++);
                }
                teacherTable.getSelectionModel().clearSelection();
                teacherTable.refresh();
            }
        } else {
            showAlert("No Selection", "Please select a teacher row to remove.", Alert.AlertType.WARNING);
        }
    }

    private void openTeacherDetail(Teacher teacher, boolean editMode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TeacherDetail.fxml"));
            Parent detailView = loader.load();

            TeacherDetailController controller = loader.getController();
            controller.initData(teacher, editMode);

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
        String searchText = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String selectedDept = deptHeaderCombo.getValue();
        String selectedPos = posHeaderCombo.getValue();

        filteredData.setPredicate(teacher -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    teacher.getName().toLowerCase().contains(searchText) ||
                    teacher.getTeacherId().toLowerCase().contains(searchText) ||
                    teacher.getDepartment().toLowerCase().contains(searchText) ||
                    teacher.getPosition().toLowerCase().contains(searchText) ||
                    teacher.getPhoneNo().toLowerCase().contains(searchText);

            boolean matchesDept = (selectedDept == null || selectedDept.equals("All Departments")) ||
                    teacher.getDepartment().equalsIgnoreCase(selectedDept);

            boolean matchesPos = (selectedPos == null || selectedPos.equals("All Positions")) ||
                    teacher.getPosition().equalsIgnoreCase(selectedPos);

            return matchesSearch && matchesDept && matchesPos;
        });
    }

    @FXML
    public void handleReset() {
        searchField.clear();
        deptHeaderCombo.setValue("All Departments");
        posHeaderCombo.setValue("All Positions");
        teacherTable.getSelectionModel().clearSelection();
        filteredData.setPredicate(b -> true);
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