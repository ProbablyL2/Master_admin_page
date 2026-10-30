package application;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class MainController {

    @FXML private BorderPane mainBorderPane;
    @FXML private Button btnDashboard;
    @FXML private Button btnManageTeacher;
    @FXML private Button btnManageStudent;
    @FXML private Button btnAttendance;

    private Parent dashboardView;
    private Parent teacherView;
    private Parent studentView;
    private Parent attendanceView;

    @FXML
    public void initialize() {
        try {
            dashboardView = FXMLLoader.load(getClass().getResource("DashboardContent.fxml")); // Pre-load dashboard view
            teacherView = FXMLLoader.load(getClass().getResource("TeacherContents.fxml"));
            studentView = FXMLLoader.load(getClass().getResource("StudentContent.fxml"));
            attendanceView = FXMLLoader.load(getClass().getResource("AttendanceContent.fxml"));

            // Default startup view (Dashboard tab active)
            mainBorderPane.setCenter(dashboardView);
            setActiveButton(btnDashboard);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showDashboardView(ActionEvent event) {
        mainBorderPane.setCenter(dashboardView);
        setActiveButton(btnDashboard);
    }

    @FXML
    public void showTeacherView(ActionEvent event) {
        mainBorderPane.setCenter(teacherView);
        setActiveButton(btnManageTeacher);
    }

    @FXML
    public void showStudentView(ActionEvent event) {
        mainBorderPane.setCenter(studentView);
        setActiveButton(btnManageStudent);
    }

    @FXML
    public void showAttendanceView(ActionEvent event) {
        mainBorderPane.setCenter(attendanceView);
        setActiveButton(btnAttendance);
    }

    // Cleans up active class swapping while preserving hover/pressed CSS rules across all sidebar buttons
    private void setActiveButton(Button activeBtn) {
        // Reset Dashboard button style
        if (btnDashboard != null) {
            btnDashboard.getStyleClass().remove("sidebar-button-active");
            if (!btnDashboard.getStyleClass().contains("sidebar-button")) {
                btnDashboard.getStyleClass().add("sidebar-button");
            }
        }

        // Reset Teacher button style
        if (btnManageTeacher != null) {
            btnManageTeacher.getStyleClass().remove("sidebar-button-active");
            if (!btnManageTeacher.getStyleClass().contains("sidebar-button")) {
                btnManageTeacher.getStyleClass().add("sidebar-button");
            }
        }

        // Reset Student button style
        if (btnManageStudent != null) {
            btnManageStudent.getStyleClass().remove("sidebar-button-active");
            if (!btnManageStudent.getStyleClass().contains("sidebar-button")) {
                btnManageStudent.getStyleClass().add("sidebar-button");
            }
        }

        // Reset Attendance button style
        if (btnAttendance != null) {
            btnAttendance.getStyleClass().remove("sidebar-button-active");
            if (!btnAttendance.getStyleClass().contains("sidebar-button")) {
                btnAttendance.getStyleClass().add("sidebar-button");
            }
        }

        // Set the active style on the currently clicked button
        if (activeBtn != null) {
            activeBtn.getStyleClass().remove("sidebar-button");
            if (!activeBtn.getStyleClass().contains("sidebar-button-active")) {
                activeBtn.getStyleClass().add("sidebar-button-active");
            }
        }
    }
}