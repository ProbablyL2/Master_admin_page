package testpack;

import javafx.stage.*;

import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.application.*;

public class Main extends Application{
	public void start(Stage s1) {
		Group root = new Group();
		Scene sc = new Scene(root,Color.BLACK);
		s1.setScene(sc);
		
		s1.setWidth(1000);
		s1.setHeight(700);
		s1.setTitle("Five Nights At Freddy");
		
		Image icon = new Image("freddy.jpg");
		s1.getIcons().add(icon);
		
		s1.setFullScreen(true);
		s1.setFullScreenExitKeyCombination(KeyCombination.valueOf("f"));
		s1.setFullScreenExitHint("Are You Ready to Spend Five Nights at Freddy?");
		s1.show();
	}
	
	public static void main(String[] args) {
		launch(args);

	}

}