module com.example.bombermansae201 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.example.bombermansae201 to javafx.fxml;
    exports com.example.bombermansae201;
    exports MenuInterface;
    opens MenuInterface to javafx.fxml;
}