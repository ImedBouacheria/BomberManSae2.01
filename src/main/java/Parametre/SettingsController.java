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

/**
 * Contrôleur pour l'écran des paramètres du jeu Bomberman.
 * <p>
 * Cette classe gère l'interface utilisateur des paramètres du jeu, notamment l'affichage
 * des commandes pour chaque joueur et la navigation vers le menu principal. Elle implémente
 * l'interface Initializable pour configurer les composants de l'interface au chargement.
 * </p>
 */
public class SettingsController implements Initializable {

    /**
     * Étiquette affichant le titre de la page des paramètres.
     * Cette étiquette bénéficie d'une animation de changement de couleur.
     */
    @FXML private Label titleLabel;

    /**
     * Étiquette pour le titre de la section des contrôles.
     */
    @FXML private Label controlsTitle;

    /**
     * Conteneur VBox pour afficher les commandes du joueur 1.
     */
    @FXML private VBox player1Controls;

    /**
     * Conteneur VBox pour afficher les commandes du joueur 2.
     */
    @FXML private VBox player2Controls;

    /**
     * Conteneur VBox pour afficher les commandes du joueur 3.
     */
    @FXML private VBox player3Controls;

    /**
     * Conteneur VBox pour afficher les commandes du joueur 4.
     */
    @FXML private VBox player4Controls;

    /**
     * Bouton permettant de retourner au menu principal.
     * Ce bouton possède des effets visuels lors du survol et du clic.
     */
    @FXML private Button backButton;

    /**
     * Référence à l'application principale pour la navigation entre les écrans.
     */
    private BombermanApplication application;

    /**
     * Timeline pour l'animation du titre.
     * Cette animation fait varier la couleur du texte du titre.
     */
    private Timeline titleAnimation;

    /**
     * Initialise l'interface utilisateur de l'écran des paramètres.
     * <p>
     * Cette méthode est automatiquement appelée après le chargement du fichier FXML.
     * Elle configure le texte des étiquettes, démarre l'animation du titre,
     * initialise les informations de contrôle pour chaque joueur et configure
     * les effets visuels des boutons.
     * </p>
     *
     * @param url L'emplacement utilisé pour résoudre les chemins relatifs des ressources.
     * @param resourceBundle Les ressources utilisées pour localiser l'interface utilisateur.
     */
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

    /**
     * Définit la référence à l'application principale.
     * <p>
     * Cette méthode permet d'établir une connexion avec l'application principale
     * pour faciliter la navigation entre les différents écrans.
     * </p>
     *
     * @param app L'instance de l'application Bomberman.
     */
    public void setApplication(BombermanApplication app) {
        this.application = app;
    }

    /**
     * Démarre l'animation de changement de couleur du titre.
     * <p>
     * Cette méthode configure et lance une animation qui fait varier cycliquement
     * la couleur du texte du titre entre blanc, cyan, blanc et magenta.
     * </p>
     */
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

    /**
     * Configure les informations de contrôles pour tous les joueurs.
     * <p>
     * Cette méthode initialise les boîtes de contrôles pour chaque joueur
     * avec leurs commandes spécifiques et une couleur distinctive.
     * </p>
     */
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

    /**
     * Configure une boîte de contrôles pour un joueur spécifique.
     * <p>
     * Cette méthode définit le style et le contenu d'une boîte VBox
     * pour afficher les informations de contrôle d'un joueur.
     * </p>
     *
     * @param playerBox Le conteneur VBox à configurer.
     * @param playerName Le nom du joueur à afficher.
     * @param controls La description textuelle des commandes du joueur.
     * @param color La couleur à utiliser pour le style du joueur (format hexadécimal).
     */
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

    /**
     * Configure les effets visuels pour les boutons de l'interface.
     * <p>
     * Cette méthode ajoute des effets de survol et d'animation
     * au bouton de retour vers le menu principal.
     * </p>
     */
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

    /**
     * Gère le retour au menu principal.
     * <p>
     * Cette méthode est appelée lorsque l'utilisateur clique sur le bouton "Retour".
     * Elle joue une animation de sélection avant de naviguer vers l'écran du menu principal.
     * </p>
     */
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

    /**
     * Arrête toutes les animations en cours.
     * <p>
     * Cette méthode doit être appelée lors de la fermeture de l'écran des paramètres
     * pour libérer les ressources et éviter les fuites de mémoire.
     * </p>
     */
    public void stopAnimations() {
        if (titleAnimation != null) {
            titleAnimation.stop();
        }
    }
}