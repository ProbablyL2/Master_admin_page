package application;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StudentDetailController {
	@FXML private TableView<Student> studentTable;
    @FXML private Label lblRollNo, lblDob, lblName, lblGender, lblMajor, lblNationality, lblYear, lblSemester, lblNrc;
    @FXML private Label lblPhone, lblEmail, lblCurrentAddress, lblPermanentAddress;
    @FXML private Label lblFatherName, lblFatherOcc, lblFatherPhone, lblFatherEmail;
    @FXML private Label lblMotherName, lblMotherOcc, lblMotherPhone, lblMotherEmail;
    @FXML private Label lblPrevSchool, lblYearCompleted, lblAdditionalInfo;
    @FXML private ImageView passportImageView;
    @FXML private VBox contentContainer;

    public void initData(Student student) {
        if (student != null) {
            if (contentContainer != null && !contentContainer.getChildren().isEmpty()) {
                HBox banner = new HBox();
                banner.setStyle("-fx-background-color: #dbeafe; -fx-border-color: #2563eb; -fx-border-width: 1.5; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 12 18;");
                
                String bannerText = String.format("⭐     %d     |     %s     |     %s     |     %s     |     %s",
                    student.getNo(),
                    student.getRollNo(),
                    student.getName(),
                    student.getMajor(),
                    student.getYear()
                );

                Label bannerLabel = new Label(bannerText);
                bannerLabel.setStyle("-fx-text-fill: #1e3a8a; -fx-font-weight: bold; -fx-font-size: 15px;");
                banner.getChildren().add(bannerLabel);
                contentContainer.getChildren().add(0, banner);
            }

            lblRollNo.setText(student.getRollNo());
            lblDob.setText(student.getDob());
            lblName.setText(student.getName());
            lblGender.setText(student.getGender());
            lblMajor.setText(student.getMajor());
            lblNationality.setText(student.getNationality());
            lblYear.setText(student.getYear());
            lblSemester.setText(student.getSemester());
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

            if (student.getPhotoPath() != null && !student.getPhotoPath().trim().isEmpty()) {
                try {
                    String path = student.getPhotoPath();
                    Image img = (path.startsWith("file:") || path.startsWith("http:"))
                            ? new Image(path, 86, 110, false, true)
                            : new Image(getClass().getResourceAsStream(path));
                    if (img != null) {
                        passportImageView.setImage(img);
                    }
                } catch (Exception e) {}
            }
        }
    }

    @FXML
    private void handleReturn() {
        try {
            Parent root = contentContainer.getScene().getRoot();
            if (root instanceof BorderPane) {
                BorderPane mainLayout = (BorderPane) root;
                Parent studentView = FXMLLoader.load(getClass().getResource("StudentContent.fxml"));
                mainLayout.setCenter(studentView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}