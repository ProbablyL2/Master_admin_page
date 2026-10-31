module JavaFX {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.xml;
    requires java.desktop;
    opens application to javafx.graphics, javafx.fxml;
	requires java.sql;
}