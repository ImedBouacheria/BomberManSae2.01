module com.example.bombermansae201 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.example.Parametre to javafx.fxml;
    exports com.example.Parametre;
    exports MenuInterface;
    opens MenuInterface to javafx.fxml;
    exports bombermanMain;
    opens bombermanMain to javafx.fxml;
    exports IABomberMan;
    opens IABomberMan to javafx.fxml;
    exports fonctionnaliteInitial;
    opens fonctionnaliteInitial to javafx.fxml;
    exports Joueur;
    opens Joueur to javafx.fxml;
    exports Test;
    opens Test to javafx.fxml;
    exports Etat;
    opens Etat to javafx.fxml;
}