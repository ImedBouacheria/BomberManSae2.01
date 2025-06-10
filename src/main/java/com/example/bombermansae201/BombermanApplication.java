package com.example.bombermansae201;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

public class BombermanApplication extends Application {

    private Stage primaryStage;
    private GameController gameController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("BOMBERMAN - Menu Principal");
        primaryStage.setResizable(false);

        gameController = new GameController();
        gameController.setApplication(this);

        showMainMenu();
    }

    public void showMenu() {
        showMainMenu();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void showSettings() {
        showAlert("PARAMÈTRES", "Page des paramètres - En cours de développement");
    }

    public void exitGame() {
        System.exit(0);
    }

    public void showGame(int playerCount) {
        System.out.println("🎮 Lancement du jeu avec " + playerCount + " joueurs");

        try {
            BorderPane gameScene = gameController.createGameScene();
            Scene scene = new Scene(gameScene, 1200, 700);

            // CORRECTION 1: Configuration améliorée des événements clavier
            scene.setOnKeyPressed(event -> {
                System.out.println("🔑 Touche détectée dans BombermanApplication: " + event.getCode());
                gameController.handleKeyPressed(event);
                event.consume(); // Important pour éviter la propagation
            });

            scene.setOnKeyReleased(event -> {
                gameController.handleKeyReleased(event);
                event.consume();
            });

            // CORRECTION 2: Configuration du focus pour recevoir les événements clavier
            gameScene.setFocusTraversable(true);

            primaryStage.setScene(scene);
            primaryStage.setTitle("BOMBERMAN - Jeu en cours");

            // CORRECTION 3: Forcer le focus après affichage
            primaryStage.show();
            gameScene.requestFocus();

            // Alternative: Si le problème persiste, utiliser cette méthode
            // Platform.runLater(() -> gameScene.requestFocus());

            gameController.initializeGame(playerCount);

            System.out.println("✅ Jeu lancé avec succès !");
            System.out.println("🔍 Focus sur gameScene: " + gameScene.isFocused());

        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
            showAlert("ERREUR", "Impossible de lancer le jeu: " + e.getMessage());
        }
    }

    private void showMainMenu() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #FF4500, #FF8C00);");

        // Titre
        Label title = new Label("BOMBERMAN");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        title.setTextFill(Color.WHITE);

        // Container des boutons
        VBox buttonContainer = new VBox(20);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(30));
        buttonContainer.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-border-color: white; -fx-border-width: 3;");

        // Boutons principaux
        Button multiplayerButton = createButton("MULTIJOUEUR");
        multiplayerButton.setOnAction(e -> {
            System.out.println("🎮 BOUTON MULTIJOUEUR CLIQUÉ !");
            launchMultiplayerMode();
        });

        Button aiButton = createButton("CONTRE IA");
        aiButton.setOnAction(e -> launchAIMode());

        Button settingsButton = createButton("PARAMÈTRES");
        settingsButton.setOnAction(e -> showSettings());

        Button quitButton = createButton("QUITTER");
        quitButton.setOnAction(e -> exitGame());

        buttonContainer.getChildren().addAll(multiplayerButton, aiButton, settingsButton, quitButton);
        root.getChildren().addAll(title, buttonContainer);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("✅ Menu principal affiché");
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(300, 60);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setStyle("-fx-background-color: #00AA00; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2;");

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: #00FF00; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2;");
            System.out.println("👆 Survol: " + text);
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: #00AA00; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2;");
        });

        return button;
    }

    private void launchMultiplayerMode() {
        System.out.println("🚀 launchMultiplayerMode() appelée");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("MODE MULTIJOUEUR");
        dialog.setHeaderText("Choisissez le nombre de joueurs :");
        dialog.setContentText("Combien de joueurs ?");

        ButtonType twoPlayers = new ButtonType("2 Joueurs");
        ButtonType threePlayers = new ButtonType("3 Joueurs");
        ButtonType fourPlayers = new ButtonType("4 Joueurs");
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getButtonTypes().setAll(twoPlayers, threePlayers, fourPlayers, cancel);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            ButtonType choice = result.get();
            System.out.println("👤 Choix: " + choice.getText());

            if (choice == twoPlayers) {
                System.out.println("🎮 Lancement avec 2 joueurs");
                showGame(2);
            } else if (choice == threePlayers) {
                System.out.println("🎮 Lancement avec 3 joueurs");
                showGame(3);
            } else if (choice == fourPlayers) {
                System.out.println("🎮 Lancement avec 4 joueurs");
                showGame(4);
            }
        } else {
            System.out.println("❌ Annulé");
        }
    }

    private void launchAIMode() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("MODE CONTRE IA");
        dialog.setHeaderText("Combien de joueurs humains ?");

        ButtonType onePlayer = new ButtonType("1 Joueur");
        ButtonType twoPlayers = new ButtonType("2 Joueurs");
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getButtonTypes().setAll(onePlayer, twoPlayers, cancel);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == onePlayer) {
                showGame(1);
            } else if (result.get() == twoPlayers) {
                showGame(2);
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}