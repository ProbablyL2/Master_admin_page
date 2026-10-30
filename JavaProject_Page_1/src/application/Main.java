package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

	public void start(Stage primaryStage) {
		try {
			String css;
			css = this.getClass().getResource("style.css").toExternalForm();
			Parent root = FXMLLoader.load(getClass().getResource("MainLayout.fxml"));
			Scene scene = new Scene(root);

			scene.getStylesheets().add(css);

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
