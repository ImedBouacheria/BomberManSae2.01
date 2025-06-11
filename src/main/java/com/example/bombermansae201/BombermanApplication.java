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
import javafx.animation.Timeline;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BombermanApplication extends Application {

    private Stage primaryStage;
    private GameController gameController;
    private GameMode selectedGameMode = GameMode.LIMITED_BOMBS; // Mode par défaut
    private ProfileInterface profileInterface;
    private List<Profile> selectedProfiles; // Profils sélectionnés pour la partie
    private AIManager aiManager;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("BOMBERMAN - Menu Principal");
        primaryStage.setResizable(false);

        gameController = new GameController();
        gameController.setApplication(this);

        // Initialiser l'interface des profils
        profileInterface = new ProfileInterface(this);

        // Initialiser la liste des profils sélectionnés
        selectedProfiles = new ArrayList<>();

        showMainMenu();
    }

    public void showMenu() {
        showMainMenu();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void showSettings() {
        VBox settingsLayout = new VBox(20);
        settingsLayout.setAlignment(Pos.CENTER);
        settingsLayout.setPadding(new Insets(40));
        settingsLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #2C3E50, #4CA1AF);");

        // Titre
        Label title = new Label("PARAMÈTRES");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        title.setTextFill(Color.WHITE);

        // Section thème
        Label themeLabel = new Label("CHANGEMENT DE THÈME");
        themeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        themeLabel.setTextFill(Color.WHITE);

        HBox themeButtons = new HBox(20);
        themeButtons.setAlignment(Pos.CENTER);

        Button classicThemeBtn = createThemeButton("Classique", BombermanMap.Theme.CLASSIC);
        Button pirateThemeBtn = createThemeButton("Pirate", BombermanMap.Theme.PIRATE);

        themeButtons.getChildren().addAll(classicThemeBtn, pirateThemeBtn);

        // Bouton retour
        Button backButton = new Button("RETOUR AU MENU");
        backButton.setPrefSize(200, 50);
        backButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        backButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
        backButton.setOnAction(e -> showMainMenu());

        settingsLayout.getChildren().addAll(title, themeLabel, themeButtons, backButton);

        Scene settingsScene = new Scene(settingsLayout, 800, 600);
        primaryStage.setScene(settingsScene);
    }

    private Button createThemeButton(String text, BombermanMap.Theme theme) {
        Button button = new Button(text);
        button.setPrefSize(200, 80);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        if (theme == BombermanMap.Theme.CLASSIC) {
            button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
        } else {
            button.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white;");
        }

        button.setOnAction(e -> {
            BombermanMap.setTheme(theme);
            System.out.println("Thème changé en : " + text);

            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(0.5),
                            event -> {
                                if (theme == BombermanMap.Theme.CLASSIC) {
                                    button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
                                } else {
                                    button.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white;");
                                }
                            }
                    )
            );
            timeline.play();
        });

        return button;
    }

    public void exitGame() {
        System.exit(0);
    }

    /**
     * Affiche l'interface des profils
     */
    public void showProfiles() {
        profileInterface.showProfileMainPage();
    }

    public void showGame(int playerCount, boolean isAIMode) {
        System.out.println("🎮 Lancement du jeu avec " + playerCount + " joueurs en mode " + selectedGameMode.getDisplayName());

        // Sélection des profils pour chaque joueur
        selectedProfiles.clear();
        selectProfilesForPlayers(playerCount);

        aiManager = new AIManager(gameController); // INITIALISATION CRUCIALE
        gameController.setAIManager(aiManager);    // transmettre l'AIManager au GameController

        try {
            // S'assurer que le GameController connaît le mode sélectionné
            gameController = new GameController();
            gameController.setApplication(this);

            BorderPane gameScene = gameController.createGameScene();
            Scene scene = new Scene(gameScene, 1200, 700);

            // Configuration des événements clavier
            scene.setOnKeyPressed(event -> {
                System.out.println("🔑 Touche détectée dans BombermanApplication: " + event.getCode());
                gameController.handleKeyPressed(event);
                event.consume();
            });

            scene.setOnKeyReleased(event -> {
                gameController.handleKeyReleased(event);
                event.consume();
            });

            gameScene.setFocusTraversable(true);

            primaryStage.setScene(scene);
            primaryStage.setTitle("BOMBERMAN - Jeu en cours (" + selectedGameMode.getDisplayName() + ")");

            primaryStage.show();
            gameScene.requestFocus();

            // Initialiser le jeu avec les profils sélectionnés
            gameController.initializeGameWithProfiles(playerCount, selectedProfiles);
            System.out.println("✅ Jeu lancé avec succès en mode " + selectedGameMode.getDisplayName() + " !");
            System.out.println("🔍 Focus sur gameScene: " + gameScene.isFocused());

            gameController.initializeGameWithProfiles(playerCount, selectedProfiles);

            if (isAIMode) {
                aiManager = new AIManager(gameController); // Ajouté
                gameController.setAIManager(aiManager);    // Ajouté
                setupAIMode(playerCount);
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
            showAlert("ERREUR", "Impossible de lancer le jeu: " + e.getMessage());
        }
    }

    /**
     * Sélectionne les profils pour chaque joueur
     */
    private void selectProfilesForPlayers(int playerCount) {
        String[] defaultPlayerNames = {"Joueur 1", "Joueur 2", "Joueur 3", "Joueur 4"};
        Color[] defaultColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};

        for (int i = 0; i < playerCount; i++) {
            System.out.println("🎯 Sélection du profil pour " + defaultPlayerNames[i]);

            Profile selectedProfile = profileInterface.selectProfileForPlayer(
                    i + 1,
                    defaultPlayerNames[i],
                    defaultColors[i]
            );

            selectedProfiles.add(selectedProfile); // null si aucun profil sélectionné

            if (selectedProfile != null) {
                System.out.println("✅ Profil sélectionné: " + selectedProfile.getFullName());
            } else {
                System.out.println("⚪ Paramètres par défaut pour " + defaultPlayerNames[i]);
            }
        }
    }

    private void showMainMenu() {
        VBox root = new VBox(25); // Espacement réduit pour faire place aux nouveaux éléments
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40)); // Padding réduit
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #FF4500, #FF8C00);");

        // Titre
        Label title = new Label("BOMBERMAN");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        title.setTextFill(Color.WHITE);

        // Sélecteur de mode de jeu
        VBox modeSelector = createModeSelector();

        // Container des boutons
        VBox buttonContainer = new VBox(15); // Espacement réduit
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(25)); // Padding réduit
        buttonContainer.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-border-color: white; -fx-border-width: 3;");

        // Boutons principaux
        Button multiplayerButton = createButton("🎮 MULTIJOUEUR");
        multiplayerButton.setOnAction(e -> {
            System.out.println("🎮 BOUTON MULTIJOUEUR CLIQUÉ !");
            launchMultiplayerMode();
        });

        Button aiButton = createButton("🤖 CONTRE IA");
        aiButton.setOnAction(e -> launchAIMode());

        Button profilesButton = createButton("👤 PROFILS");
        profilesButton.setOnAction(e -> {
            System.out.println("👤 BOUTON PROFILS CLIQUÉ !");
            showProfiles();
        });

        Button settingsButton = createButton("⚙️ PARAMÈTRES");
        settingsButton.setOnAction(e -> showSettings());

        Button quitButton = createButton("❌ QUITTER");
        quitButton.setOnAction(e -> exitGame());

        buttonContainer.getChildren().addAll(multiplayerButton, aiButton, profilesButton, settingsButton, quitButton);
        root.getChildren().addAll(title, modeSelector, buttonContainer);

        Scene scene = new Scene(root, 800, 650); // Hauteur légèrement augmentée
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("✅ Menu principal affiché");
    }

    private VBox createModeSelector() {
        VBox modeContainer = new VBox(10);
        modeContainer.setAlignment(Pos.CENTER);
        modeContainer.setPadding(new Insets(15));
        modeContainer.setStyle("-fx-background-color: rgba(0,0,100,0.8); -fx-border-color: cyan; -fx-border-width: 2;");

        // Titre de la section mode
        Label modeTitle = new Label("MODE DE JEU");
        modeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        modeTitle.setTextFill(Color.CYAN);

        // Label affichant le mode actuel
        Label currentModeLabel = new Label("Actuel: " + selectedGameMode.getDisplayName());
        currentModeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        currentModeLabel.setTextFill(Color.WHITE);

        // Bouton pour changer le mode
        Button toggleModeButton = new Button("CHANGER MODE");
        toggleModeButton.setPrefSize(200, 35);
        toggleModeButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        toggleModeButton.setStyle("-fx-background-color: #0066CC; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2;");

        // Description du mode actuel
        Label modeDescription = new Label(selectedGameMode.getDescription());
        modeDescription.setFont(Font.font("Arial", 10));
        modeDescription.setTextFill(Color.LIGHTGRAY);
        modeDescription.setWrapText(true);
        modeDescription.setMaxWidth(180);
        modeDescription.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Action du bouton toggle
        toggleModeButton.setOnAction(e -> {
            selectedGameMode = selectedGameMode.toggle();
            currentModeLabel.setText("Actuel: " + selectedGameMode.getDisplayName());
            modeDescription.setText(selectedGameMode.getDescription());

            System.out.println("🔄 Mode changé vers: " + selectedGameMode.getDisplayName());

            // Animation du bouton
            toggleModeButton.setDisable(true);
            javafx.animation.Timeline enableButton = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.millis(200),
                            event -> toggleModeButton.setDisable(false))
            );
            enableButton.play();
        });

        // Effets de survol
        toggleModeButton.setOnMouseEntered(e -> {
            toggleModeButton.setStyle("-fx-background-color: #0088FF; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2;");
        });

        toggleModeButton.setOnMouseExited(e -> {
            toggleModeButton.setStyle("-fx-background-color: #0066CC; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2;");
        });

        modeContainer.getChildren().addAll(modeTitle, currentModeLabel, toggleModeButton, modeDescription);
        return modeContainer;
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(300, 50); // Hauteur réduite
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

    private void setupAIMode(int humanPlayers) {
        System.out.println("🤖 Configuration du mode IA avec " + humanPlayers + " joueurs humains");

        int totalPlayers = 4; // On veut toujours 4 joueurs au total

        for (int i = humanPlayers; i < totalPlayers; i++) { // 🔍 CORRECTION ici
            aiManager.addAIPlayer(i);
            System.out.println("🤖 Joueur " + i + " configuré comme IA");
        }

        // Lancer l'IA après un petit délai
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(1000); // Laisser le temps à la scène de se charger
                aiManager.startAllAI();
                System.out.println("🚀 Mode IA démarré avec succès !");
            } catch (InterruptedException e) {
                System.out.println("❌ Erreur IA: " + e.getMessage());
            }
        });
    }

    private void launchMultiplayerMode() {
        System.out.println("🚀 launchMultiplayerMode() appelée avec mode: " + selectedGameMode.getDisplayName());

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("MODE MULTIJOUEUR");
        dialog.setHeaderText("Choisissez le nombre de joueurs :");
        dialog.setContentText("Combien de joueurs ?\n\nMode actuel: " + selectedGameMode.getDisplayName());

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
                showGame(2, false);
            } else if (choice == threePlayers) {
                System.out.println("🎮 Lancement avec 3 joueurs");
                showGame(3, false);
            } else if (choice == fourPlayers) {
                System.out.println("🎮 Lancement avec 4 joueurs");
                showGame(4, false);
            }
        } else {
            System.out.println("❌ Annulé");
        }
    }

    private void launchAIMode() {
        System.out.println("🤖 launchAIMode() appelée");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("MODE CONTRE IA");
        dialog.setHeaderText("Combien de joueurs humains ?");
        dialog.setContentText("Les autres joueurs seront contrôlés par l'IA");

        ButtonType onePlayer = new ButtonType("1 Joueur (vs 3 IA)");
        ButtonType twoPlayers = new ButtonType("2 Joueurs (vs 2 IA)");
        ButtonType aiOnly = new ButtonType("IA seulement (spectateur)");
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getButtonTypes().setAll(onePlayer, twoPlayers, aiOnly, cancel);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            ButtonType choice = result.get();
            System.out.println("🤖 Choix IA: " + choice.getText());

            if (choice == onePlayer) {
                System.out.println("🎮 Lancement 1 joueur vs 3 IA");
                showGame(1, true);
            } else if (choice == twoPlayers) {
                System.out.println("🎮 Lancement 2 joueurs vs 2 IA");
                showGame(2, true);
            } else if (choice == aiOnly) {
                System.out.println("🤖 Lancement mode spectateur (4 IA)");
                showGame(0, true);
            }
        } else {
            System.out.println("❌ Annulé");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getter pour le mode de jeu sélectionné
    public GameMode getSelectedGameMode() {
        return selectedGameMode;
    }

    // Getters pour accès aux composants
    public ProfileInterface getProfileInterface() {
        return profileInterface;
    }

    public List<Profile> getSelectedProfiles() {
        return selectedProfiles;
    }

    public static void main(String[] args) {
        launch(args);
    }
}