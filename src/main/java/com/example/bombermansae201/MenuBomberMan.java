package com.example.bombermansae201;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.Optional;

public class MenuBomberMan extends Application {

    private Stage primaryStage;
    private GameController gameController;
    private Scene menuScene;
    private Scene gameScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("🎮 BOMBERMAN 🎮");
        primaryStage.setResizable(false);

        createMenuScene();
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    private void createMenuScene() {
        // Conteneur principal
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #FF4500, #FF6600, #FF8C00);");

        // Titre
        Label title = new Label("BOMBERMAN");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 56));
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-effect: dropshadow(gaussian, black, 5, 0, 3, 3);");

        // Container des boutons de jeu
        VBox gameButtons = new VBox(20);
        gameButtons.setAlignment(Pos.CENTER);
        gameButtons.setPadding(new Insets(40));
        gameButtons.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-border-color: white; -fx-border-width: 4;");

        // Boutons de mode de jeu
        Button multiButton = createGameButton("🎮 MULTIJOUEUR", this::launchMultiplayer);
        Button aiButton = createGameButton("🤖 CONTRE IA", this::launchAI);
        Button settingsButton = createGameButton("⚙️ PARAMETRES", this::showSettings);
        Button quitButton = createGameButton("❌ QUITTER", this::quitGame);

        gameButtons.getChildren().addAll(multiButton, aiButton, settingsButton, quitButton);
        root.getChildren().addAll(title, gameButtons);

        menuScene = new Scene(root, 1000, 700);
    }

    private Button createGameButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefSize(250, 60);
        button.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: #00AA00; -fx-border-color: white; -fx-border-width: 3; -fx-effect: dropshadow(gaussian, black, 3, 0, 2, 2);");

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: #00FF00; -fx-border-color: white; -fx-border-width: 3; -fx-effect: dropshadow(gaussian, black, 3, 0, 2, 2);");
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: #00AA00; -fx-border-color: white; -fx-border-width: 3; -fx-effect: dropshadow(gaussian, black, 3, 0, 2, 2);");
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });

        button.setOnAction(e -> action.run());
        return button;
    }

    private void launchMultiplayer() {
        System.out.println("🎮 Lancement du mode multijoueur");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("🎮 MODE MULTIJOUEUR");
        dialog.setHeaderText("Choisissez le nombre de joueurs :");
        dialog.setContentText("Sélectionnez le nombre de joueurs pour la partie");

        ButtonType two = new ButtonType("2 Joueurs");
        ButtonType three = new ButtonType("3 Joueurs");
        ButtonType four = new ButtonType("4 Joueurs");
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getButtonTypes().setAll(two, three, four, cancel);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == two) {
                startGame(2);
            } else if (result.get() == three) {
                startGame(3);
            } else if (result.get() == four) {
                startGame(4);
            }
        }
    }

    private void launchAI() {
        System.out.println("🤖 Lancement du mode IA");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("🤖 MODE CONTRE IA");
        dialog.setHeaderText("Choisissez le nombre de joueurs humains :");
        dialog.setContentText("Les autres joueurs seront contrôlés par l'IA");

        ButtonType one = new ButtonType("1 Joueur");
        ButtonType two = new ButtonType("2 Joueurs");
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getButtonTypes().setAll(one, two, cancel);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == one) {
                startGame(1);
            } else if (result.get() == two) {
                startGame(2);
            }
        }
    }

    private void startGame(int playerCount) {
        try {
            System.out.println("🚀 Démarrage du jeu avec " + playerCount + " joueurs");

            // Créer le contrôleur de jeu directement
            gameController = new GameController();

            // Créer la scène de jeu
            BorderPane gameRoot = gameController.createGameScene();
            gameScene = new Scene(gameRoot, 1000, 700);

            // Configurer les événements clavier
            gameScene.setOnKeyPressed(gameController::handleKeyPressed);
            gameScene.setOnKeyReleased(gameController::handleKeyReleased);

            // Passer à la scène de jeu
            primaryStage.setScene(gameScene);
            primaryStage.setTitle("🎮 BOMBERMAN - En Jeu (" + playerCount + " joueurs) 🎮");

            // Initialiser le jeu
            gameController.initializeGame(playerCount);

            // Ajouter un bouton de retour au menu dans le jeu
            addBackToMenuButton();

            // Focus pour capturer les touches
            gameScene.getRoot().requestFocus();

            System.out.println("✅ Jeu lancé avec succès !");

        } catch (Exception e) {
            System.out.println("❌ Erreur lors du lancement du jeu: " + e.getMessage());
            e.printStackTrace();

            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Erreur");
            error.setHeaderText("Impossible de lancer le jeu");
            error.setContentText("Erreur: " + e.getMessage());
            error.showAndWait();
        }
    }

    private void addBackToMenuButton() {
        // Cette méthode pourrait ajouter un bouton "Retour au menu" dans le jeu
        // Pour l'instant, on peut utiliser la touche ESCAPE pour revenir au menu
        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                backToMenu();
            } else if (gameController != null) {
                gameController.handleKeyPressed(event);
            }
        });
    }

    private void backToMenu() {
        System.out.println("🔙 Retour au menu principal");
        primaryStage.setScene(menuScene);
        primaryStage.setTitle("🎮 BOMBERMAN 🎮");
    }

    private void showSettings() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #4B0082, #800080);");

        Label title = new Label("⚙️ PARAMETRES");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 48));
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-effect: dropshadow(gaussian, black, 5, 0, 3, 3);");

        VBox controls = new VBox(20);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(30));
        controls.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-border-color: white; -fx-border-width: 4;");

        Label info = new Label("🎮 CONTROLES DES JOUEURS:\n\n" +
                "🔴 Joueur 1: Z Q S D + A (bombe)\n" +
                "🔵 Joueur 2: ↑ ↓ ← → + ESPACE (bombe)\n" +
                "🟢 Joueur 3: Y G H J + T (bombe)\n" +
                "🟡 Joueur 4: O K L M + I (bombe)\n\n" +
                "⌨️ TOUCHES SPECIALES:\n" +
                "• ESCAPE: Retour au menu\n" +
                "• PAUSE: Mettre en pause");

        info.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        info.setTextFill(Color.WHITE);
        info.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button back = createGameButton("🔙 RETOUR", this::backToMenu);
        controls.getChildren().addAll(info, back);
        root.getChildren().addAll(title, controls);

        Scene settingsScene = new Scene(root, 1000, 700);
        primaryStage.setScene(settingsScene);
        primaryStage.setTitle("🎮 BOMBERMAN - Paramètres 🎮");
    }

    private void quitGame() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("❌ QUITTER");
        confirm.setHeaderText("Voulez-vous vraiment quitter le jeu ?");
        confirm.setContentText("Toute progression sera perdue.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("👋 Fermeture du jeu");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}