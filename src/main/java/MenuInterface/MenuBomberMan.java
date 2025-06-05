package MenuInterface;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class MenuBomberMan extends Application {

    private Pane particlePane;
    private Pane backgroundPane;
    private List<Circle> particles = new ArrayList<>();
    private List<Rectangle> backgroundBlocks = new ArrayList<>();
    private Random random = new Random();
    private Timeline particleTimeline;
    private Timeline backgroundTimeline;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Configuration de la fenêtre principale
        primaryStage.setTitle("BOMBERMAN - Menu Principal");
        primaryStage.setResizable(false);

        showMainMenu();
    }

    private void showMainMenu() {
        // Création du conteneur principal avec fond pixelisé
        StackPane root = new StackPane();
        root.setPrefSize(1000, 700);

        // Fond dégradé rétro avec des couleurs plus vives
        LinearGradient backgroundGradient = new LinearGradient(
                0, 0, 1, 1, true, null,
                new Stop(0, Color.web("#FF4500")),
                new Stop(0.5, Color.web("#FF6600")),
                new Stop(1, Color.web("#FF8C00"))
        );
        root.setBackground(new Background(new BackgroundFill(backgroundGradient, null, null)));

        // Pane pour les éléments d'arrière-plan pixelisés
        backgroundPane = new Pane();
        backgroundPane.setMouseTransparent(true);
        createPixelatedBackground();

        // Pane pour les particules
        particlePane = new Pane();
        particlePane.setMouseTransparent(true);

        // Conteneur principal du menu
        VBox mainContainer = new VBox(60);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(50));

        // Titre BOMBERMAN rétro
        Label title = createRetroTitle();

        // Conteneur des modes de jeu
        VBox gameModesContainer = createRetroGameModesContainer();

        // Menu latéral rétro
        VBox sideMenu = createRetroSideMenu();

        // Assemblage du layout principal
        mainContainer.getChildren().addAll(title, gameModesContainer);

        // Positionnement du menu latéral
        StackPane.setAlignment(sideMenu, Pos.TOP_RIGHT);
        StackPane.setMargin(sideMenu, new Insets(30, 30, 0, 0));

        // Ajout de tous les éléments au root
        root.getChildren().addAll(backgroundPane, particlePane, mainContainer, sideMenu);

        // Création de la scène
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Démarrage des animations
        startParticleAnimation();
        startRetroTitleAnimation(title);
        startBackgroundAnimation();

        // Fermeture propre des animations
        primaryStage.setOnCloseRequest(e -> {
            if (particleTimeline != null) particleTimeline.stop();
            if (backgroundTimeline != null) backgroundTimeline.stop();
        });
    }

    private void showSettingsPage() {
        // Arrêter les animations actuelles
        if (particleTimeline != null) particleTimeline.stop();
        if (backgroundTimeline != null) backgroundTimeline.stop();

        // Création du conteneur principal pour les paramètres
        StackPane root = new StackPane();
        root.setPrefSize(1000, 700);

        // Fond dégradé pour les paramètres
        LinearGradient backgroundGradient = new LinearGradient(
                0, 0, 1, 1, true, null,
                new Stop(0, Color.web("#2E0080")),
                new Stop(0.5, Color.web("#4B0080")),
                new Stop(1, Color.web("#6A0080"))
        );
        root.setBackground(new Background(new BackgroundFill(backgroundGradient, null, null)));

        // Conteneur principal
        VBox mainContainer = new VBox(40);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(50));

        // Titre de la page paramètres
        Label title = new Label(">>> PARAMETRES <<<");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 48));
        title.setTextFill(Color.WHITE);

        DropShadow titleShadow = new DropShadow();
        titleShadow.setColor(Color.BLACK);
        titleShadow.setOffsetX(4);
        titleShadow.setOffsetY(4);
        titleShadow.setRadius(0);
        title.setEffect(titleShadow);

        // Container pour les contrôles
        VBox controlsContainer = new VBox(30);
        controlsContainer.setAlignment(Pos.CENTER);
        controlsContainer.setPadding(new Insets(40));

        // Fond pixelisé pour le container des contrôles
        controlsContainer.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0.8),
                new CornerRadii(0),
                null
        )));
        controlsContainer.setBorder(new Border(new BorderStroke(
                Color.WHITE,
                BorderStrokeStyle.SOLID,
                new CornerRadii(0),
                new BorderWidths(6)
        )));

        // Titre des contrôles
        Label controlsTitle = new Label("CONTROLES DES JOUEURS");
        controlsTitle.setFont(Font.font("Monospace", FontWeight.BOLD, 24));
        controlsTitle.setTextFill(Color.YELLOW);
        controlsTitle.setTextAlignment(TextAlignment.CENTER);

        DropShadow controlsTitleShadow = new DropShadow();
        controlsTitleShadow.setColor(Color.BLACK);
        controlsTitleShadow.setOffsetX(2);
        controlsTitleShadow.setOffsetY(2);
        controlsTitleShadow.setRadius(0);
        controlsTitle.setEffect(controlsTitleShadow);

        // Grille des contrôles des joueurs
        GridPane controlsGrid = new GridPane();
        controlsGrid.setHgap(50);
        controlsGrid.setVgap(25);
        controlsGrid.setAlignment(Pos.CENTER);

        // Joueur 1
        VBox player1Box = createPlayerControlBox("JOUEUR 1", "Z Q S D", Color.web("#FF4444"));
        controlsGrid.add(player1Box, 0, 0);

        // Joueur 2
        VBox player2Box = createPlayerControlBox("JOUEUR 2", "↑ ↓ → ←", Color.web("#44FF44"));
        controlsGrid.add(player2Box, 1, 0);

        // Joueur 3
        VBox player3Box = createPlayerControlBox("JOUEUR 3", "Y G H J", Color.web("#4444FF"));
        controlsGrid.add(player3Box, 0, 1);

        // Joueur 4
        VBox player4Box = createPlayerControlBox("JOUEUR 4", "O K L M", Color.web("#FFFF44"));
        controlsGrid.add(player4Box, 1, 1);

        // Bouton retour
        Button backButton = createRetroBackButton();

        // Assemblage
        controlsContainer.getChildren().addAll(controlsTitle, controlsGrid);
        mainContainer.getChildren().addAll(title, controlsContainer, backButton);
        root.getChildren().add(mainContainer);

        // Affichage de la scène
        Scene settingsScene = new Scene(root);
        primaryStage.setScene(settingsScene);

        // Animation du titre
        startSettingsTitleAnimation(title);
    }

    private VBox createPlayerControlBox(String playerName, String controls, Color playerColor) {
        VBox playerBox = new VBox(15);
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setPadding(new Insets(20));
        playerBox.setPrefSize(200, 120);

        // Fond du joueur
        playerBox.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0.6),
                new CornerRadii(0),
                null
        )));
        playerBox.setBorder(new Border(new BorderStroke(
                playerColor,
                BorderStrokeStyle.SOLID,
                new CornerRadii(0),
                new BorderWidths(4)
        )));

        // Effet d'ombre
        DropShadow boxShadow = new DropShadow();
        boxShadow.setColor(Color.BLACK);
        boxShadow.setOffsetX(3);
        boxShadow.setOffsetY(3);
        boxShadow.setRadius(0);
        playerBox.setEffect(boxShadow);

        // Nom du joueur
        Label nameLabel = new Label(playerName);
        nameLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        nameLabel.setTextFill(playerColor);

        // Séparateur
        Rectangle separator = new Rectangle(150, 2);
        separator.setFill(playerColor);

        // Contrôles
        Label controlsLabel = new Label(controls);
        controlsLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 18));
        controlsLabel.setTextFill(Color.WHITE);
        controlsLabel.setTextAlignment(TextAlignment.CENTER);

        playerBox.getChildren().addAll(nameLabel, separator, controlsLabel);

        // Animation de clignotement au survol
        playerBox.setOnMouseEntered(e -> {
            Timeline blink = new Timeline(
                    new KeyFrame(Duration.millis(0), new KeyValue(playerBox.opacityProperty(), 1.0)),
                    new KeyFrame(Duration.millis(200), new KeyValue(playerBox.opacityProperty(), 0.7)),
                    new KeyFrame(Duration.millis(400), new KeyValue(playerBox.opacityProperty(), 1.0))
            );
            blink.play();
        });

        return playerBox;
    }

    private Button createRetroBackButton() {
        Button backButton = new Button(">>> RETOUR AU MENU <<<");
        backButton.setPrefSize(300, 60);
        backButton.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        backButton.setTextFill(Color.WHITE);

        // Style rétro
        backButton.setBackground(new Background(new BackgroundFill(
                Color.web("#AA0000"),
                new CornerRadii(0),
                null
        )));
        backButton.setBorder(new Border(new BorderStroke(
                Color.WHITE,
                BorderStrokeStyle.SOLID,
                new CornerRadii(0),
                new BorderWidths(4)
        )));

        // Effet d'ombre
        DropShadow buttonShadow = new DropShadow();
        buttonShadow.setColor(Color.BLACK);
        buttonShadow.setOffsetX(4);
        buttonShadow.setOffsetY(4);
        buttonShadow.setRadius(0);
        backButton.setEffect(buttonShadow);

        // Effets de survol
        backButton.setOnMouseEntered(e -> {
            backButton.setBackground(new Background(new BackgroundFill(
                    Color.web("#FF0000"),
                    new CornerRadii(0),
                    null
            )));

            // Animation de pulsation
            Timeline pulse = new Timeline(
                    new KeyFrame(Duration.millis(0), new KeyValue(backButton.scaleXProperty(), 1.0)),
                    new KeyFrame(Duration.millis(100), new KeyValue(backButton.scaleXProperty(), 1.05)),
                    new KeyFrame(Duration.millis(200), new KeyValue(backButton.scaleXProperty(), 1.0))
            );
            pulse.play();
        });

        backButton.setOnMouseExited(e -> {
            backButton.setBackground(new Background(new BackgroundFill(
                    Color.web("#AA0000"),
                    new CornerRadii(0),
                    null
            )));
        });

        backButton.setOnAction(e -> {
            // Retour au menu principal
            showMainMenu();
        });

        return backButton;
    }

    private void startSettingsTitleAnimation(Label title) {
        // Animation de changement de couleur pour le titre des paramètres
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

    private void createPixelatedBackground() {
        // Création d'un motif pixelisé en arrière-plan
        for (int i = 0; i < 50; i++) {
            Rectangle block = new Rectangle(8, 8);
            block.setFill(Color.rgb(255, 255, 255, 0.1));
            block.setLayoutX(random.nextDouble() * 1000);
            block.setLayoutY(random.nextDouble() * 700);
            backgroundPane.getChildren().add(block);
            backgroundBlocks.add(block);
        }
    }

    private Label createRetroTitle() {
        Label title = new Label("BOMBERMAN");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 56));
        title.setTextFill(Color.WHITE);

        // Effet d'ombre pixelisée multiple pour donner un aspect rétro
        DropShadow shadow1 = new DropShadow();
        shadow1.setColor(Color.BLACK);
        shadow1.setOffsetX(6);
        shadow1.setOffsetY(6);
        shadow1.setRadius(0);

        DropShadow shadow2 = new DropShadow();
        shadow2.setColor(Color.web("#FF0000"));
        shadow2.setOffsetX(3);
        shadow2.setOffsetY(3);
        shadow2.setRadius(0);
        shadow2.setInput(shadow1);

        title.setEffect(shadow2);

        return title;
    }

    private VBox createRetroGameModesContainer() {
        VBox container = new VBox(25);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(50));

        // Fond pixelisé avec bordure épaisse
        container.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0.8),
                new CornerRadii(0), // Coins carrés pour un look rétro
                null
        )));
        container.setBorder(new Border(new BorderStroke(
                Color.WHITE,
                BorderStrokeStyle.SOLID,
                new CornerRadii(0),
                new BorderWidths(6)
        )));

        // Bordure intérieure pour effet rétro
        Border innerBorder = new Border(new BorderStroke(
                Color.web("#00FF00"),
                BorderStrokeStyle.DASHED,
                new CornerRadii(0),
                new BorderWidths(2)
        ));

        // Grille des boutons de mode de jeu
        GridPane gameModesGrid = new GridPane();
        gameModesGrid.setHgap(30);
        gameModesGrid.setVgap(30);
        gameModesGrid.setAlignment(Pos.CENTER);

        // Création des boutons de mode rétro
        Button aiButton = createRetroGameModeButton("CONTRE IA", this::launchAIMode);
        Button multiplayerButton = createRetroGameModeButton("MULTIJOUEUR", this::launchMultiplayerMode);
        Button captureButton = createRetroGameModeButton("CAPTURE DE\nDRAPEAU", this::launchCaptureMode);
        Button editorButton = createRetroGameModeButton("EDITEUR DE\nNIVEAU", this::launchEditorMode);

        // Positionnement dans la grille
        gameModesGrid.add(aiButton, 0, 0);
        gameModesGrid.add(multiplayerButton, 1, 0);
        gameModesGrid.add(captureButton, 0, 1);
        gameModesGrid.add(editorButton, 1, 1);

        container.getChildren().add(gameModesGrid);
        return container;
    }

    private Button createRetroGameModeButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefSize(220, 90);
        button.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);

        // Style rétro avec couleurs vives et bordures pixelisées
        button.setBackground(new Background(new BackgroundFill(
                Color.web("#00AA00"),
                new CornerRadii(0),
                null
        )));
        button.setBorder(new Border(
                new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(4)),
                new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(2), new Insets(4))
        ));

        // Effet d'ombre pixelisée
        DropShadow pixelShadow = new DropShadow();
        pixelShadow.setColor(Color.BLACK);
        pixelShadow.setOffsetX(4);
        pixelShadow.setOffsetY(4);
        pixelShadow.setRadius(0);
        button.setEffect(pixelShadow);

        // Effets de survol rétro
        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(
                    Color.web("#00FF00"),
                    new CornerRadii(0),
                    null
            )));

            // Animation de clignotement rétro
            Timeline blink = new Timeline(
                    new KeyFrame(Duration.millis(0), new KeyValue(button.opacityProperty(), 1.0)),
                    new KeyFrame(Duration.millis(100), new KeyValue(button.opacityProperty(), 0.7)),
                    new KeyFrame(Duration.millis(200), new KeyValue(button.opacityProperty(), 1.0))
            );
            blink.play();

            // Son simulé (console output)
            System.out.println("♪ BEEP ♪");
        });

        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(
                    Color.web("#00AA00"),
                    new CornerRadii(0),
                    null
            )));
        });

        button.setOnMousePressed(e -> {
            // Effet d'enfoncement pixelisé
            button.setTranslateX(2);
            button.setTranslateY(2);
            DropShadow pressedShadow = new DropShadow();
            pressedShadow.setColor(Color.BLACK);
            pressedShadow.setOffsetX(2);
            pressedShadow.setOffsetY(2);
            pressedShadow.setRadius(0);
            button.setEffect(pressedShadow);
        });

        button.setOnMouseReleased(e -> {
            button.setTranslateX(0);
            button.setTranslateY(0);
            DropShadow normalShadow = new DropShadow();
            normalShadow.setColor(Color.BLACK);
            normalShadow.setOffsetX(4);
            normalShadow.setOffsetY(4);
            normalShadow.setRadius(0);
            button.setEffect(normalShadow);
        });

        button.setOnAction(e -> {
            // Animation de sélection rétro
            ScaleTransition select = new ScaleTransition(Duration.millis(150), button);
            select.setFromX(1.0);
            select.setFromY(1.0);
            select.setToX(1.1);
            select.setToY(1.1);
            select.setAutoReverse(true);
            select.setCycleCount(2);
            select.setOnFinished(ev -> action.run());
            select.play();

            System.out.println("♪ BLOOP ♪");
        });

        return button;
    }

    private VBox createRetroSideMenu() {
        VBox sideMenu = new VBox(20);
        sideMenu.setAlignment(Pos.TOP_RIGHT);

        // Création des boutons du menu latéral rétro
        Button profileButton = createRetroSideButton("PROFIL", Color.web("#0088FF"), this::openProfile);
        Button settingsButton = createRetroSideButton("PARAMETRES", Color.web("#888888"), this::openSettings);
        Button quitButton = createRetroSideButton("QUITTER", Color.web("#FF4444"), this::quitGame);

        sideMenu.getChildren().addAll(profileButton, settingsButton, quitButton);
        return sideMenu;
    }

    private Button createRetroSideButton(String text, Color baseColor, Runnable action) {
        Button button = new Button(text);
        button.setPrefSize(140, 45);
        button.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
        button.setTextFill(Color.WHITE);

        // Style rétro pixelisé
        button.setBackground(new Background(new BackgroundFill(
                baseColor,
                new CornerRadii(0),
                null
        )));
        button.setBorder(new Border(new BorderStroke(
                Color.WHITE,
                BorderStrokeStyle.SOLID,
                new CornerRadii(0),
                new BorderWidths(3)
        )));

        // Effet d'ombre pixelisée
        DropShadow pixelShadow = new DropShadow();
        pixelShadow.setColor(Color.BLACK);
        pixelShadow.setOffsetX(3);
        pixelShadow.setOffsetY(3);
        pixelShadow.setRadius(0);
        button.setEffect(pixelShadow);

        // Effets de survol rétro
        Color hoverColor = baseColor.brighter();
        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(
                    hoverColor,
                    new CornerRadii(0),
                    null
            )));

            // Animation de glitch rétro
            Timeline glitch = new Timeline(
                    new KeyFrame(Duration.millis(0), new KeyValue(button.translateXProperty(), 0)),
                    new KeyFrame(Duration.millis(50), new KeyValue(button.translateXProperty(), 2)),
                    new KeyFrame(Duration.millis(100), new KeyValue(button.translateXProperty(), -1)),
                    new KeyFrame(Duration.millis(150), new KeyValue(button.translateXProperty(), 0))
            );
            glitch.play();

            System.out.println("♪ CLICK ♪");
        });

        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(
                    baseColor,
                    new CornerRadii(0),
                    null
            )));
        });

        button.setOnAction(e -> {
            // Animation de validation rétro
            FadeTransition flash = new FadeTransition(Duration.millis(100), button);
            flash.setFromValue(1.0);
            flash.setToValue(0.3);
            flash.setAutoReverse(true);
            flash.setCycleCount(4);
            flash.setOnFinished(ev -> action.run());
            flash.play();
        });

        return button;
    }

    private void startRetroTitleAnimation(Label title) {
        // Animation de clignotement rétro pour le titre
        Timeline titleBlink = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(title.textFillProperty(), Color.WHITE)),
                new KeyFrame(Duration.millis(500), new KeyValue(title.textFillProperty(), Color.YELLOW)),
                new KeyFrame(Duration.millis(1000), new KeyValue(title.textFillProperty(), Color.WHITE)),
                new KeyFrame(Duration.millis(1500), new KeyValue(title.textFillProperty(), Color.web("#00FF00"))),
                new KeyFrame(Duration.millis(2000), new KeyValue(title.textFillProperty(), Color.WHITE))
        );
        titleBlink.setCycleCount(Timeline.INDEFINITE);
        titleBlink.play();

        // Animation de tremblement léger
        Timeline shake = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(title.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(50), new KeyValue(title.translateXProperty(), 1)),
                new KeyFrame(Duration.millis(100), new KeyValue(title.translateXProperty(), -1)),
                new KeyFrame(Duration.millis(150), new KeyValue(title.translateXProperty(), 0))
        );
        shake.setCycleCount(Timeline.INDEFINITE);
        shake.play();
    }

    private void startParticleAnimation() {
        particleTimeline = new Timeline(new KeyFrame(Duration.millis(300), e -> createRetroParticle()));
        particleTimeline.setCycleCount(Timeline.INDEFINITE);
        particleTimeline.play();
    }

    private void createRetroParticle() {
        // Particules pixelisées carrées
        Rectangle particle = new Rectangle(6, 6);
        Color[] retroColors = {Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.LIME, Color.WHITE};
        particle.setFill(retroColors[random.nextInt(retroColors.length)]);
        particle.setLayoutX(random.nextDouble() * 1000);
        particle.setLayoutY(700);

        particlePane.getChildren().add(particle);

        // Animation pixelisée par étapes
        Timeline moveAnimation = new Timeline();
        for (int i = 0; i <= 100; i++) {
            final double progress = i / 100.0;
            moveAnimation.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i * 50),
                            new KeyValue(particle.layoutYProperty(), 700 - (710 * progress)),
                            new KeyValue(particle.opacityProperty(), 1.0 - progress)
                    )
            );
        }

        moveAnimation.setOnFinished(e -> {
            particlePane.getChildren().remove(particle);
        });

        moveAnimation.play();
    }

    private void startBackgroundAnimation() {
        // Animation des blocs d'arrière-plan
        backgroundTimeline = new Timeline(new KeyFrame(Duration.millis(2000), e -> {
            for (Rectangle block : backgroundBlocks) {
                double newOpacity = random.nextDouble() * 0.3;
                FadeTransition fade = new FadeTransition(Duration.millis(1000), block);
                fade.setToValue(newOpacity);
                fade.play();
            }
        }));
        backgroundTimeline.setCycleCount(Timeline.INDEFINITE);
        backgroundTimeline.play();
    }

    // Méthodes des actions des boutons (avec style rétro)
    private void launchAIMode() {
        showRetroAlert("MODE DE JEU", ">>> INITIALISATION CONTRE IA <<<\n\nCHARGEMENT...", Alert.AlertType.INFORMATION);
    }

    private void launchMultiplayerMode() {
        showRetroAlert("MODE DE JEU", ">>> INITIALISATION MULTIJOUEUR <<<\n\nRECHERCHE DE JOUEURS...", Alert.AlertType.INFORMATION);
    }

    private void launchCaptureMode() {
        showRetroAlert("MODE DE JEU", ">>> CAPTURE DE DRAPEAU <<<\n\nGENERATION DE LA CARTE...", Alert.AlertType.INFORMATION);
    }

    private void launchEditorMode() {
        showRetroAlert("EDITEUR", ">>> EDITEUR DE NIVEAU <<<\n\nOUVERTURE DES OUTILS...", Alert.AlertType.INFORMATION);
    }
    private void showProfilePage() {
        // Arrêter les animations actuelles
        if (particleTimeline != null) particleTimeline.stop();
        if (backgroundTimeline != null) backgroundTimeline.stop();

        // Création du conteneur principal
        StackPane root = new StackPane();
        root.setPrefSize(1000, 700);

        // Fond dégradé rétro
        LinearGradient backgroundGradient = new LinearGradient(
                0, 0, 1, 1, true, null,
                new Stop(0, Color.web("#4B0082")),
                new Stop(1, Color.web("#800080"))
        );
        root.setBackground(new Background(new BackgroundFill(backgroundGradient, null, null)));

        // Conteneur principal
        VBox mainContainer = new VBox(40);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(50));

        // Titre
        Label title = new Label(">>> PROFIL <<<");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 48));
        title.setTextFill(Color.WHITE);

        // Conteneur du formulaire
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(30));
        formContainer.setMaxWidth(400);
        formContainer.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-border-color: white; -fx-border-width: 4;");

        // Champs du formulaire
        TextField nomField = createRetroTextField("Nom");
        TextField prenomField = createRetroTextField("Prénom");

        // ComboBox pour la couleur du sprite
        ComboBox<String> colorComboBox = new ComboBox<>();
        colorComboBox.getItems().addAll("Rose", "Orange", "Bleu", "Vert");
        colorComboBox.setPromptText("Couleur du sprite");
        styleRetroComboBox(colorComboBox);

        // Bouton de sauvegarde
        Button saveButton = new Button("SAUVEGARDER");
        styleRetroButton(saveButton, Color.GREEN);
        saveButton.setOnAction(e -> {
            showRetroAlert("SAUVEGARDE",
                    "Profil sauvegardé :\nNom: " + nomField.getText() +
                            "\nPrénom: " + prenomField.getText() +
                            "\nCouleur: " + colorComboBox.getValue(),
                    Alert.AlertType.INFORMATION);
        });

        // Bouton retour
        Button backButton = createRetroBackButton();

        // Assemblage
        formContainer.getChildren().addAll(
                createRetroLabel("NOM:"), nomField,
                createRetroLabel("PRÉNOM:"), prenomField,
                createRetroLabel("COULEUR DU SPRITE:"), colorComboBox,
                new Region(), saveButton
        );

        mainContainer.getChildren().addAll(title, formContainer, backButton);
        root.getChildren().add(mainContainer);

        Scene profileScene = new Scene(root);
        primaryStage.setScene(profileScene);
    }

    private TextField createRetroTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setPrefHeight(40);
        field.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
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
        label.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
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
                        "-fx-font-family: 'Monospace';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;"
        );
    }

    private void styleRetroButton(Button button, Color baseColor) {
        button.setPrefSize(200, 40);
        button.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);
        button.setStyle(
                "-fx-background-color: " + toRGBCode(baseColor) + ";" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2;"
        );
    }

    private String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    private void openProfile() {
        showProfilePage();
    }

    private void openSettings() {
        // Maintenant on affiche la vraie page de paramètres
        showSettingsPage();
    }

    private void quitGame() {
        System.out.println(">>> ARRET DU SYSTEME <<<");
        System.exit(0);
    }

    private void showRetroAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(">>> " + title + " <<<");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}