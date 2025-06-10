package com.example.bombermansae201;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private Button aiModeButton;
    @FXML private Button multiplayerButton;
    @FXML private Button captureModeButton;
    @FXML private Button editorButton;
    @FXML private Button profileButton;
    @FXML private Button settingsButton;
    @FXML private Button quitButton;

    private BombermanApplication application;
    private Timeline titleAnimation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("=== INITIALISATION DU MENU CONTROLLER ===");

        // Vérification des composants FXML
        System.out.println("titleLabel: " + (titleLabel != null ? "✅ OK" : "❌ NULL"));
        System.out.println("multiplayerButton: " + (multiplayerButton != null ? "✅ OK" : "❌ NULL"));
        System.out.println("aiModeButton: " + (aiModeButton != null ? "✅ OK" : "❌ NULL"));

        // TEST SPÉCIAL pour le bouton multijoueur
        if (multiplayerButton != null) {
            System.out.println("🔧 Configuration manuelle du bouton MULTIJOUEUR...");

            // Forcer la configuration du bouton
            multiplayerButton.setDisable(false);
            multiplayerButton.setVisible(true);
            multiplayerButton.setManaged(true);

            // Ajouter des listeners de test
            multiplayerButton.setOnMouseEntered(e -> {
                System.out.println("👆 DÉTECTION: Souris sur le bouton MULTIJOUEUR");
            });

            multiplayerButton.setOnMouseClicked(e -> {
                System.out.println("🖱️ DÉTECTION: Clic souris sur MULTIJOUEUR");
                handleMultiplayer();
            });

            // Forcer l'action FXML aussi
            multiplayerButton.setOnAction(e -> {
                System.out.println("⚡ DÉTECTION: Action FXML sur MULTIJOUEUR");
                handleMultiplayer();
            });

            System.out.println("✅ Bouton MULTIJOUEUR reconfiguré manuellement");
        } else {
            System.out.println("❌ ERREUR: Le bouton MULTIJOUEUR est NULL !");
        }

        // Démarrage de l'animation du titre
        startTitleAnimation();

        // Configuration des effets de survol
        setupButtonEffects();

        System.out.println("✅ Menu Controller initialisé avec succès");
    }

    public void setApplication(BombermanApplication app) {
        this.application = app;
        System.out.println("🔗 Application liée au MenuController: " + (app != null ? "✅ OK" : "❌ NULL"));
    }

    @FXML
    private void handleAIMode() {
        System.out.println("🤖 Bouton CONTRE IA cliqué");

        if (application == null) {
            System.out.println("❌ ERREUR: Application non liée !");
            showErrorAlert();
            return;
        }

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("MODE CONTRE IA");
        dialog.setHeaderText("Choisissez le nombre de joueurs humains :");
        dialog.setContentText("Combien de joueurs humains ?");

        ButtonType onePlayer = new ButtonType("1 Joueur");
        ButtonType twoPlayers = new ButtonType("2 Joueurs");
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getButtonTypes().setAll(onePlayer, twoPlayers, cancel);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == onePlayer) {
                System.out.println("🚀 Lancement du jeu avec 1 joueur + IA");
                application.showGame(1);
            } else if (result.get() == twoPlayers) {
                System.out.println("🚀 Lancement du jeu avec 2 joueurs + IA");
                application.showGame(2);
            }
        }
    }

    @FXML
    private void handleMultiplayer() {
        System.out.println("🎮 BOUTON MULTIJOUEUR CLIQUÉ !");

        if (application == null) {
            System.out.println("❌ ERREUR: Application non liée au MenuController !");
            showErrorAlert();
            return;
        }

        System.out.println("✅ Application trouvée, création du dialogue...");

        try {
            Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
            dialog.setTitle("🎮 MODE MULTIJOUEUR 🎮");
            dialog.setHeaderText("Choisissez le nombre de joueurs :");
            dialog.setContentText("Combien de joueurs voulez-vous ?");

            ButtonType twoPlayers = new ButtonType("2 Joueurs");
            ButtonType threePlayers = new ButtonType("3 Joueurs");
            ButtonType fourPlayers = new ButtonType("4 Joueurs");
            ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

            dialog.getButtonTypes().setAll(twoPlayers, threePlayers, fourPlayers, cancel);

            System.out.println("📋 Dialogue créé, affichage...");

            Optional<ButtonType> result = dialog.showAndWait();
            System.out.println("📋 Dialogue fermé, résultat: " + result);

            if (result.isPresent()) {
                ButtonType choice = result.get();
                System.out.println("👤 Choix utilisateur: " + choice.getText());

                if (choice == twoPlayers) {
                    System.out.println("🚀 Lancement du jeu multijoueur avec 2 joueurs...");
                    application.showGame(2);
                } else if (choice == threePlayers) {
                    System.out.println("🚀 Lancement du jeu multijoueur avec 3 joueurs...");
                    application.showGame(3);
                } else if (choice == fourPlayers) {
                    System.out.println("🚀 Lancement du jeu multijoueur avec 4 joueurs...");
                    application.showGame(4);
                } else {
                    System.out.println("❌ Utilisateur a annulé");
                }
            } else {
                System.out.println("❌ Aucun résultat (dialogue fermé sans choix)");
            }
        } catch (Exception e) {
            System.out.println("💥 EXCEPTION dans handleMultiplayer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCaptureMode() {
        System.out.println("🏁 Bouton CAPTURE MODE cliqué");
        showAlert("MODE CAPTURE", "Mode non encore implémenté.\nDisponible dans une future version !");
    }

    @FXML
    private void handleEditor() {
        System.out.println("🛠️ Bouton EDITEUR cliqué");
        showAlert("EDITEUR", "Fonctionnalité en développement.\nBientôt disponible !");
    }

    @FXML
    private void handleProfile() {
        System.out.println("👤 Bouton PROFIL cliqué");
        if (application != null) {
            showProfilePage();
        } else {
            showErrorAlert();
        }
    }

    /*@FXML
    private void handleSettings() {
        System.out.println("⚙️ Bouton PARAMETRES cliqué");
        if (application != null) {
            application.showSettings();
        } else {
            showErrorAlert();
        }
    }*/

    @FXML
    private void handleQuit() {
        System.out.println("🚪 Bouton QUITTER cliqué");

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("QUITTER");
        confirmDialog.setHeaderText("Êtes-vous sûr de vouloir quitter le jeu ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("✅ Fermeture de l'application confirmée");
            if (application != null) {
                application.exitGame();
            } else {
                System.exit(0);
            }
        }
    }

    private void showErrorAlert() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Erreur");
        errorAlert.setHeaderText("Erreur d'initialisation");
        errorAlert.setContentText("L'application n'est pas correctement initialisée.\nVeuillez redémarrer le jeu.");
        errorAlert.showAndWait();
    }

    private void startTitleAnimation() {
        if (titleLabel != null) {
            titleAnimation = new Timeline(
                    new KeyFrame(Duration.millis(0), new KeyValue(titleLabel.textFillProperty(), Color.WHITE)),
                    new KeyFrame(Duration.millis(500), new KeyValue(titleLabel.textFillProperty(), Color.YELLOW)),
                    new KeyFrame(Duration.millis(1000), new KeyValue(titleLabel.textFillProperty(), Color.WHITE)),
                    new KeyFrame(Duration.millis(1500), new KeyValue(titleLabel.textFillProperty(), Color.LIME)),
                    new KeyFrame(Duration.millis(2000), new KeyValue(titleLabel.textFillProperty(), Color.WHITE))
            );
            titleAnimation.setCycleCount(Timeline.INDEFINITE);
            titleAnimation.play();
            System.out.println("🎬 Animation du titre démarrée");
        }
    }

    private void setupButtonEffects() {
        System.out.println("🎨 Configuration des effets de survol...");
        setupButtonHoverEffect(aiModeButton);
        setupButtonHoverEffect(multiplayerButton);
        setupButtonHoverEffect(captureModeButton);
        setupButtonHoverEffect(editorButton);
        setupButtonHoverEffect(profileButton);
        setupButtonHoverEffect(settingsButton);
        setupButtonHoverEffect(quitButton);
    }

    private void setupButtonHoverEffect(Button button) {
        if (button == null) return;

        String originalStyle = button.getStyle();

        button.setOnMouseEntered(e -> {
            System.out.println("👆 Survol du bouton: " + button.getText());
            String hoverStyle = originalStyle.replace("#00AA00", "#00FF00")
                    .replace("#0088FF", "#00AAFF")
                    .replace("#888888", "#AAAAAA")
                    .replace("#FF4444", "#FF6666")
                    .replace("#8000FF", "#A000FF");
            button.setStyle(hoverStyle);

            Timeline pulse = new Timeline(
                    new KeyFrame(Duration.millis(0), new KeyValue(button.scaleXProperty(), 1.0)),
                    new KeyFrame(Duration.millis(100), new KeyValue(button.scaleXProperty(), 1.05)),
                    new KeyFrame(Duration.millis(200), new KeyValue(button.scaleXProperty(), 1.0))
            );
            pulse.play();
        });

        button.setOnMouseExited(e -> {
            System.out.println("👋 Sortie du bouton: " + button.getText());
            button.setStyle(originalStyle);
        });
    }

    private void showProfilePage() {
        if (titleAnimation != null) {
            titleAnimation.stop();
        }

        StackPane root = new StackPane();
        root.setPrefSize(1000, 700);

        LinearGradient backgroundGradient = new LinearGradient(
                0, 0, 1, 1, true, null,
                new Stop(0, Color.web("#4B0082")),
                new Stop(1, Color.web("#800080"))
        );
        root.setBackground(new Background(new BackgroundFill(backgroundGradient, null, null)));

        VBox mainContainer = new VBox(40);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(50));

        Label title = new Label(">>> PROFIL <<<");
        title.setFont(Font.font("System", FontWeight.BOLD, 48));
        title.setTextFill(Color.WHITE);

        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(30));
        formContainer.setMaxWidth(400);
        formContainer.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-border-color: white; -fx-border-width: 4;");

        TextField nomField = createRetroTextField("Nom");
        TextField prenomField = createRetroTextField("Prénom");

        ComboBox<String> colorComboBox = new ComboBox<>();
        colorComboBox.getItems().addAll("Rouge", "Bleu", "Vert", "Jaune");
        colorComboBox.setPromptText("Couleur du sprite");
        styleRetroComboBox(colorComboBox);

        Button saveButton = new Button("SAUVEGARDER");
        styleRetroButton(saveButton, Color.GREEN);
        saveButton.setOnAction(e -> {
            showAlert("SAUVEGARDE",
                    "Profil sauvegardé :\nNom: " + nomField.getText() +
                            "\nPrénom: " + prenomField.getText() +
                            "\nCouleur: " + colorComboBox.getValue());
        });

        Button backButton = new Button(">>> RETOUR AU MENU <<<");
        styleRetroButton(backButton, Color.web("#AA0000"));
        backButton.setOnAction(e -> {
            application.showMenu();
            startTitleAnimation();
        });

        formContainer.getChildren().addAll(
                createRetroLabel("NOM:"), nomField,
                createRetroLabel("PRÉNOM:"), prenomField,
                createRetroLabel("COULEUR DU SPRITE:"), colorComboBox,
                new Region(), saveButton
        );

        mainContainer.getChildren().addAll(title, formContainer, backButton);
        root.getChildren().add(mainContainer);

        Scene profileScene = new Scene(root);
        Stage primaryStage = application.getPrimaryStage();
        primaryStage.setScene(profileScene);
        primaryStage.setTitle("🎮 BOMBERMAN - Profil 🎮");

        startProfileTitleAnimation(title);
    }

    private TextField createRetroTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setPrefHeight(40);
        field.setFont(Font.font("System", FontWeight.BOLD, 14));
        field.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: #00ff00;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2;" +
                        "-fx-prompt-text-fill: #888888;"
        );
        return field;
    }

    private Label createRetroLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 14));
        label.setTextFill(Color.WHITE);
        return label;
    }

    private void styleRetroComboBox(ComboBox<String> comboBox) {
        comboBox.setPrefHeight(40);
        comboBox.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: #00ff00;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2;" +
                        "-fx-font-family: 'System';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;"
        );
    }

    private void styleRetroButton(Button button, Color baseColor) {
        button.setPrefSize(200, 40);
        button.setFont(Font.font("System", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);
        button.setStyle(
                "-fx-background-color: " + toRGBCode(baseColor) + ";" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2;"
        );

        button.setOnMouseEntered(e -> {
            Color hoverColor = baseColor.brighter();
            button.setStyle(
                    "-fx-background-color: " + toRGBCode(hoverColor) + ";" +
                            "-fx-border-color: white;" +
                            "-fx-border-width: 2;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: " + toRGBCode(baseColor) + ";" +
                            "-fx-border-color: white;" +
                            "-fx-border-width: 2;"
            );
        });
    }

    private String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void startProfileTitleAnimation(Label title) {
        Timeline titleAnimation = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(title.textFillProperty(), Color.WHITE)),
                new KeyFrame(Duration.millis(800), new KeyValue(title.textFillProperty(), Color.CYAN)),
                new KeyFrame(Duration.millis(1600), new KeyValue(title.textFillProperty(), Color.WHITE)),
                new KeyFrame(Duration.millis(2400), new KeyValue(title.textFillProperty(), Color.MAGENTA)),
                new KeyFrame(Duration.millis(3200), new KeyValue(title.textFillProperty(), Color.WHITE))
        );
        titleAnimation.setCycleCount(Timeline.INDEFINITE);
        titleAnimation.play();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(">>> " + title + " <<<");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void stopAnimations() {
        if (titleAnimation != null) {
            titleAnimation.stop();
        }
    }
}