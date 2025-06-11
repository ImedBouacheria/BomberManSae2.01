package Parametre;

import bombermanMain.BombermanApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private Label controlsTitle;
    @FXML private VBox player1Controls;
    @FXML private VBox player2Controls;
    @FXML private VBox player3Controls;
    @FXML private VBox player4Controls;
    @FXML private Button backButton;

    private BombermanApplication application;
    private Timeline titleAnimation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuration du texte décoratif
        if (titleLabel != null) {
            titleLabel.setText(">>> PARAMETRES <<<");
        }

        // Configuration du bouton avec texte décoratif
        if (backButton != null) {
            backButton.setText(">>> RETOUR AU MENU <<<");
        }

        // Démarrage de l'animation du titre
        startTitleAnimation();

        // Configuration des informations de contrôles
        setupControlsInfo();

        // Configuration des effets de survol
        setupButtonEffects();
    }

    public void setApplication(BombermanApplication app) {
        this.application = app;
    }

    private void startTitleAnimation() {
        if (titleLabel != null) {
            // Animation de changement de couleur pour le titre
            titleAnimation = new Timeline(
                    new KeyFrame(Duration.millis(0), new KeyValue(titleLabel.textFillProperty(), Color.WHITE)),
                    new KeyFrame(Duration.millis(800), new KeyValue(titleLabel.textFillProperty(), Color.CYAN)),
                    new KeyFrame(Duration.millis(1600), new KeyValue(titleLabel.textFillProperty(), Color.WHITE)),
                    new KeyFrame(Duration.millis(2400), new KeyValue(titleLabel.textFillProperty(), Color.MAGENTA)),
                    new KeyFrame(Duration.millis(3200), new KeyValue(titleLabel.textFillProperty(), Color.WHITE))
            );
            titleAnimation.setCycleCount(Timeline.INDEFINITE);
            titleAnimation.play();
        }
    }

    private void setupControlsInfo() {
        // Configuration des informations de contrôles pour chaque joueur
        if (player1Controls != null) {
            setupPlayerControlsBox(player1Controls, "JOUEUR 1", "Déplacement: Z Q S D\nBombe: A", "#FF4444");
        }
        if (player2Controls != null) {
            setupPlayerControlsBox(player2Controls, "JOUEUR 2", "Déplacement: ↑ ↓ ← →\nBombe: ESPACE", "#44FF44");
        }
        if (player3Controls != null) {
            setupPlayerControlsBox(player3Controls, "JOUEUR 3", "Déplacement: Y G H J\nBombe: T", "#4444FF");
        }
        if (player4Controls != null) {
            setupPlayerControlsBox(player4Controls, "JOUEUR 4", "Déplacement: O K L M\nBombe: I", "#FFFF44");
        }
    }

    private void setupPlayerControlsBox(VBox playerBox, String playerName, String controls, String color) {
        // Configuration du style et du contenu pour chaque boîte de contrôles
        playerBox.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.8); " +
                        "-fx-border-color: " + color + "; " +
                        "-fx-border-width: 4; " +
                        "-fx-padding: 20;"
        );

        // Si les labels n'existent pas dans le FXML, les créer dynamiquement
        if (playerBox.getChildren().isEmpty()) {
            Label nameLabel = new Label(playerName);
            nameLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 16; -fx-font-weight: bold;");

            Label controlsLabel = new Label(controls);
            controlsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12;");

            playerBox.getChildren().addAll(nameLabel, controlsLabel);
        }
    }

    private void setupButtonEffects() {
        if (backButton != null) {
            String originalStyle = backButton.getStyle();

            backButton.setOnMouseEntered(e -> {
                // Effet de survol
                String hoverStyle = originalStyle.replace("#AA0000", "#FF0000");
                backButton.setStyle(hoverStyle);

                // Animation de pulsation
                Timeline pulse = new Timeline(
                        new KeyFrame(Duration.millis(0), new KeyValue(backButton.scaleXProperty(), 1.0)),
                        new KeyFrame(Duration.millis(100), new KeyValue(backButton.scaleXProperty(), 1.05)),
                        new KeyFrame(Duration.millis(200), new KeyValue(backButton.scaleXProperty(), 1.0))
                );
                pulse.play();
            });

            backButton.setOnMouseExited(e -> {
                backButton.setStyle(originalStyle);
            });
        }
    }

    @FXML
    private void handleBackToMenu() {
        // Animation de sélection
        if (backButton != null) {
            Timeline selectAnimation = new Timeline(
                    new KeyFrame(Duration.millis(0),
                            new KeyValue(backButton.scaleXProperty(), 1.0),
                            new KeyValue(backButton.scaleYProperty(), 1.0)),
                    new KeyFrame(Duration.millis(150),
                            new KeyValue(backButton.scaleXProperty(), 1.1),
                            new KeyValue(backButton.scaleYProperty(), 1.1)),
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(backButton.scaleXProperty(), 1.0),
                            new KeyValue(backButton.scaleYProperty(), 1.0))
            );

            selectAnimation.setOnFinished(e -> {
                if (application != null) {
                    application.showMenu();
                }
            });
            selectAnimation.play();
        } else {
            // Fallback si le bouton n'existe pas
            if (application != null) {
                application.showMenu();
            }
        }
    }

    public void stopAnimations() {
        if (titleAnimation != null) {
            titleAnimation.stop();
        }
    }
}