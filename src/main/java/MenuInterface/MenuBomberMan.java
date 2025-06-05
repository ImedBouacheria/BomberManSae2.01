package MenuInterface;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
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

    @Override
    public void start(Stage primaryStage) {
        // Configuration de la fenêtre principale
        primaryStage.setTitle("BOMBERMAN - Menu Principal");
        primaryStage.setResizable(false);

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

    private void openProfile() {
        showRetroAlert("PROFIL", ">>> DONNEES UTILISATEUR <<<\n\nACCES AU PROFIL...", Alert.AlertType.INFORMATION);
    }

    private void openSettings() {
        showRetroAlert("PARAMETRES", ">>> CONFIGURATION SYSTEME <<<\n\nCHARGEMENT DES OPTIONS...", Alert.AlertType.INFORMATION);
    }

    private void quitGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(">>> FERMETURE DU SYSTEME <<<");
        alert.setHeaderText("CONFIRMATION REQUISE");
        alert.setContentText("VOULEZ-VOUS VRAIMENT QUITTER LE JEU ?\n\n[OUI] / [NON]");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println(">>> ARRET DU SYSTEME <<<");
            System.exit(0);
        }
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