package Joueur;

import bombermanMain.BombermanApplication;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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

import java.util.List;
import java.util.Optional;

/**
 * Interface de gestion des profils pour Bomberman
 */
public class ProfileInterface {

    private BombermanApplication application;
    private ProfileManager profileManager;
    private Stage primaryStage;
    private Timeline titleAnimation;

    public ProfileInterface(BombermanApplication application) {
        this.application = application;
        this.profileManager = ProfileManager.getInstance();
        this.primaryStage = application.getPrimaryStage();
    }

    /**
     * Affiche la page principale des profils
     */
    public void showProfileMainPage() {
        stopAnimations();

        StackPane root = new StackPane();
        root.setPrefSize(1000, 700);

        // Fond dégradé
        LinearGradient backgroundGradient = new LinearGradient(
                0, 0, 1, 1, true, null,
                new Stop(0, Color.web("#4B0082")),
                new Stop(0.5, Color.web("#663399")),
                new Stop(1, Color.web("#800080"))
        );
        root.setBackground(new Background(new BackgroundFill(backgroundGradient, null, null)));

        // Conteneur principal
        VBox mainContainer = new VBox(50);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(50));

        // Titre principal
        Label titleLabel = new Label(">>> PROFILS <<<");
        titleLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 56));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, black, 5, 0, 4, 4);");

        // Conteneur des boutons principaux
        VBox buttonContainer = new VBox(30);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(40));
        buttonContainer.setMaxWidth(500);
        buttonContainer.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-border-color: white; -fx-border-width: 4;");

        // Bouton "Déjà connecté"
        Button existingProfileButton = createRetroButton("👤 DEJA CONNECTE", Color.web("#0088FF"));
        existingProfileButton.setOnAction(e -> showExistingProfilesPage());

        // Bouton "Créer votre profil"
        Button createProfileButton = createRetroButton("✨ CREER VOTRE PROFIL", Color.web("#00AA00"));
        createProfileButton.setOnAction(e -> showCreateProfilePage());

        // Informations
        Label infoLabel = new Label("💾 " + profileManager.getProfileCount() + " profil(s) enregistré(s)");
        infoLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        infoLabel.setTextFill(Color.YELLOW);

        // Bouton retour
        Button backButton = createRetroButton("🔙 RETOUR AU MENU", Color.web("#AA0000"));
        backButton.setOnAction(e -> application.showMenu());

        buttonContainer.getChildren().addAll(
                existingProfileButton,
                createProfileButton,
                new Separator(),
                infoLabel
        );

        mainContainer.getChildren().addAll(titleLabel, buttonContainer, backButton);
        root.getChildren().add(mainContainer);

        Scene profileScene = new Scene(root);
        primaryStage.setScene(profileScene);
        primaryStage.setTitle("🎮 BOMBERMAN - Profils 🎮");

        // Animation du titre
        startTitleAnimation(titleLabel);

        System.out.println("✅ Page principale des profils affichée");
    }

    /**
     * Affiche la page de création de profil
     */
    public void showCreateProfilePage() {
        stopAnimations();

        StackPane root = new StackPane();
        root.setPrefSize(1000, 700);

        // Fond dégradé
        LinearGradient backgroundGradient = new LinearGradient(
                0, 0, 1, 1, true, null,
                new Stop(0, Color.web("#2E5984")),
                new Stop(1, Color.web("#4682B4"))
        );
        root.setBackground(new Background(new BackgroundFill(backgroundGradient, null, null)));

        VBox mainContainer = new VBox(40);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(50));

        // Titre
        Label titleLabel = new Label(">>> NOUVEAU PROFIL <<<");
        titleLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 48));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, black, 5, 0, 3, 3);");

        // Formulaire
        VBox formContainer = new VBox(25);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(40));
        formContainer.setMaxWidth(500);
        formContainer.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-border-color: white; -fx-border-width: 4;");

        // Champs de saisie
        TextField firstNameField = createRetroTextField("Prénom");
        TextField lastNameField = createRetroTextField("Nom");

        // ComboBox pour la couleur
        ComboBox<String> colorComboBox = new ComboBox<>();
        colorComboBox.getItems().addAll(ProfileManager.getAvailableColors());
        colorComboBox.setPromptText("Choisir une couleur");
        styleRetroComboBox(colorComboBox);

        // Boutons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button createButton = createRetroButton("✅ CREER", Color.web("#00AA00"));
        createButton.setOnAction(e -> {
            handleCreateProfile(firstNameField.getText(), lastNameField.getText(), colorComboBox.getValue());
        });

        Button cancelButton = createRetroButton("❌ ANNULER", Color.web("#AA0000"));
        cancelButton.setOnAction(e -> showProfileMainPage());

        buttonBox.getChildren().addAll(createButton, cancelButton);

        formContainer.getChildren().addAll(
                createRetroLabel("PRÉNOM:"), firstNameField,
                createRetroLabel("NOM:"), lastNameField,
                createRetroLabel("COULEUR DU SPRITE:"), colorComboBox,
                new Region(),
                buttonBox
        );

        mainContainer.getChildren().addAll(titleLabel, formContainer);
        root.getChildren().add(mainContainer);

        Scene createScene = new Scene(root);
        primaryStage.setScene(createScene);
        primaryStage.setTitle("🎮 BOMBERMAN - Nouveau Profil 🎮");

        startTitleAnimation(titleLabel);
    }

    /**
     * Affiche la page des profils existants
     */
    public void showExistingProfilesPage() {
        stopAnimations();

        List<Profile> profiles = profileManager.getAllProfiles();

        if (profiles.isEmpty()) {
            showAlert("AUCUN PROFIL", "Aucun profil n'a été créé.\nVeuillez d'abord créer un profil.", Alert.AlertType.INFORMATION);
            showProfileMainPage();
            return;
        }

        StackPane root = new StackPane();
        root.setPrefSize(1000, 700);

        // Fond dégradé
        LinearGradient backgroundGradient = new LinearGradient(
                0, 0, 1, 1, true, null,
                new Stop(0, Color.web("#1a472a")),
                new Stop(1, Color.web("#2d5a3d"))
        );
        root.setBackground(new Background(new BackgroundFill(backgroundGradient, null, null)));

        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(30));

        // Titre
        Label titleLabel = new Label(">>> PROFILS EXISTANTS <<<");
        titleLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 42));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, black, 5, 0, 3, 3);");

        // Liste des profils dans un ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMaxWidth(800);
        scrollPane.setMaxHeight(400);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-border-width: 2;");

        VBox profilesList = new VBox(15);
        profilesList.setAlignment(Pos.CENTER);
        profilesList.setPadding(new Insets(20));

        for (Profile profile : profiles) {
            HBox profileBox = createProfileBox(profile);
            profilesList.getChildren().add(profileBox);
        }

        scrollPane.setContent(profilesList);

        // Bouton retour
        Button backButton = createRetroButton("🔙 RETOUR", Color.web("#AA0000"));
        backButton.setOnAction(e -> showProfileMainPage());

        mainContainer.getChildren().addAll(titleLabel, scrollPane, backButton);
        root.getChildren().add(mainContainer);

        Scene existingScene = new Scene(root);
        primaryStage.setScene(existingScene);
        primaryStage.setTitle("🎮 BOMBERMAN - Profils Existants 🎮");

        startTitleAnimation(titleLabel);
    }

    /**
     * Crée une boîte d'affichage pour un profil
     */
    private HBox createProfileBox(Profile profile) {
        HBox profileBox = new HBox(20);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        profileBox.setPadding(new Insets(15));
        profileBox.setMaxWidth(750);
        profileBox.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-border-color: " +
                getColorCode(profile.getColor()) + "; -fx-border-width: 2;");

        // Informations du profil
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(profile.getFullName());
        nameLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 18));
        nameLabel.setTextFill(profile.getColor());

        Label colorLabel = new Label("Couleur: " + profile.getColorName());
        colorLabel.setFont(Font.font("Monospace", FontWeight.NORMAL, 14));
        colorLabel.setTextFill(Color.WHITE);

        Label statsLabel = new Label(String.format("Parties: %d | Victoires: %d | Score: %d",
                profile.getGamesPlayed(), profile.getGamesWon(), profile.getTotalScore()));
        statsLabel.setFont(Font.font("Monospace", FontWeight.NORMAL, 12));
        statsLabel.setTextFill(Color.LIGHTGRAY);

        infoBox.getChildren().addAll(nameLabel, colorLabel, statsLabel);

        // Boutons d'actions
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button selectButton = createSmallRetroButton("SELECTIONNER", Color.web("#0088FF"));
        selectButton.setOnAction(e -> {
            showAlert("PROFIL SÉLECTIONNÉ", "Profil de " + profile.getFullName() + " sélectionné !\n" +
                    "Ce profil sera utilisé lors de la prochaine partie.", Alert.AlertType.INFORMATION);
        });

        Button deleteButton = createSmallRetroButton("SUPPRIMER", Color.web("#FF4444"));
        deleteButton.setOnAction(e -> {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("CONFIRMATION");
            confirmDialog.setHeaderText("Supprimer le profil");
            confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer le profil de " + profile.getFullName() + " ?");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                profileManager.removeProfile(profile);
                showExistingProfilesPage(); // Rafraîchir la page
            }
        });

        buttonsBox.getChildren().addAll(selectButton, deleteButton);

        profileBox.getChildren().addAll(infoBox, buttonsBox);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        return profileBox;
    }

    /**
     * Gère la création d'un nouveau profil
     */
    private void handleCreateProfile(String firstName, String lastName, String color) {
        // Validation des champs
        if (firstName == null || firstName.trim().isEmpty()) {
            showAlert("ERREUR", "Veuillez saisir un prénom.", Alert.AlertType.ERROR);
            return;
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            showAlert("ERREUR", "Veuillez saisir un nom.", Alert.AlertType.ERROR);
            return;
        }
        if (color == null || color.isEmpty()) {
            showAlert("ERREUR", "Veuillez choisir une couleur.", Alert.AlertType.ERROR);
            return;
        }

        firstName = firstName.trim();
        lastName = lastName.trim();

        // Vérifier si le profil existe déjà
        if (profileManager.profileExists(firstName, lastName)) {
            showAlert("PROFIL EXISTANT", "Un profil avec ce nom existe déjà !\n" +
                    "Prénom: " + firstName + "\nNom: " + lastName, Alert.AlertType.WARNING);
            return;
        }

        // Créer le nouveau profil
        Profile newProfile = new Profile(firstName, lastName, color);

        if (profileManager.addProfile(newProfile)) {
            showAlert("SUCCÈS", "Profil créé avec succès !\n\n" +
                    "Nom: " + newProfile.getFullName() + "\n" +
                    "Couleur: " + color, Alert.AlertType.INFORMATION);
            showProfileMainPage();
        } else {
            showAlert("ERREUR", "Impossible de créer le profil.\nVeuillez réessayer.", Alert.AlertType.ERROR);
        }
    }

    // ===== MÉTHODES UTILITAIRES =====

    private Button createRetroButton(String text, Color baseColor) {
        Button button = new Button(text);
        button.setPrefSize(300, 60);
        button.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        button.setTextFill(Color.WHITE);

        String colorCode = getColorCode(baseColor);
        button.setStyle("-fx-background-color: " + colorCode + "; -fx-border-color: white; -fx-border-width: 3; " +
                "-fx-effect: dropshadow(gaussian, black, 4, 0, 3, 3);");

        // Effets de survol
        button.setOnMouseEntered(e -> {
            Color hoverColor = baseColor.brighter();
            String hoverColorCode = getColorCode(hoverColor);
            button.setStyle("-fx-background-color: " + hoverColorCode + "; -fx-border-color: white; -fx-border-width: 3; " +
                    "-fx-effect: dropshadow(gaussian, black, 4, 0, 3, 3);");
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + colorCode + "; -fx-border-color: white; -fx-border-width: 3; " +
                    "-fx-effect: dropshadow(gaussian, black, 4, 0, 3, 3);");
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });

        return button;
    }

    private Button createSmallRetroButton(String text, Color baseColor) {
        Button button = new Button(text);
        button.setPrefSize(120, 35);
        button.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
        button.setTextFill(Color.WHITE);

        String colorCode = getColorCode(baseColor);
        button.setStyle("-fx-background-color: " + colorCode + "; -fx-border-color: white; -fx-border-width: 2;");

        button.setOnMouseEntered(e -> {
            Color hoverColor = baseColor.brighter();
            String hoverColorCode = getColorCode(hoverColor);
            button.setStyle("-fx-background-color: " + hoverColorCode + "; -fx-border-color: white; -fx-border-width: 2;");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + colorCode + "; -fx-border-color: white; -fx-border-width: 2;");
        });

        return button;
    }

    private TextField createRetroTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setPrefHeight(45);
        field.setMaxWidth(350);
        field.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
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
        label.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        label.setTextFill(Color.WHITE);
        return label;
    }

    private void styleRetroComboBox(ComboBox<String> comboBox) {
        comboBox.setPrefHeight(45);
        comboBox.setMaxWidth(350);
        comboBox.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: #00ff00;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2;" +
                        "-fx-font-family: 'Monospace';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;"
        );
    }

    private String getColorCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void startTitleAnimation(Label titleLabel) {
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

    private void stopAnimations() {
        if (titleAnimation != null) {
            titleAnimation.stop();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(">>> " + title + " <<<");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Méthode pour sélectionner des profils lors du lancement d'une partie
     */
    public Profile selectProfileForPlayer(int playerNumber, String defaultPlayerName, Color defaultColor) {
        List<Profile> profiles = profileManager.getAllProfiles();

        if (profiles.isEmpty()) {
            // Aucun profil disponible, utiliser les paramètres par défaut
            showAlert("AUCUN PROFIL", "Aucun profil disponible.\nUtilisation des paramètres par défaut pour " +
                    defaultPlayerName + ".", Alert.AlertType.INFORMATION);
            return null;
        }

        // Créer une boîte de dialogue pour sélectionner un profil
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("SÉLECTION DE PROFIL");
        dialog.setHeaderText("Joueur " + playerNumber + " - " + defaultPlayerName);
        dialog.setContentText("Voulez-vous utiliser un profil existant ?");

        ButtonType useProfileButton = new ButtonType("Utiliser un profil");
        ButtonType useDefaultButton = new ButtonType("Paramètres par défaut");
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getButtonTypes().setAll(useProfileButton, useDefaultButton, cancelButton);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (result.get() == useProfileButton) {
                return showProfileSelectionDialog(playerNumber);
            } else if (result.get() == useDefaultButton) {
                return null; // Utiliser les paramètres par défaut
            }
        }

        return null; // Annulé ou par défaut
    }

    /**
     * Affiche une boîte de dialogue pour sélectionner un profil - Version corrigée
     */
    private Profile showProfileSelectionDialog(int playerNumber) {
        List<Profile> profiles = profileManager.getAllProfiles();

        // Créer une liste de chaînes de caractères pour l'affichage
        String[] profileNames = new String[profiles.size()];
        for (int i = 0; i < profiles.size(); i++) {
            Profile profile = profiles.get(i);
            profileNames[i] = profile.getFullName() + " (" + profile.getColorName() + ")";
        }

        // Créer un dialogue de choix simple
        ChoiceDialog<String> dialog = new ChoiceDialog<>(profileNames[0], profileNames);
        dialog.setTitle("SÉLECTION DE PROFIL");
        dialog.setHeaderText("Joueur " + playerNumber);
        dialog.setContentText("Choisissez un profil :");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            // Retrouver le profil correspondant à la sélection
            String selectedName = result.get();
            for (int i = 0; i < profileNames.length; i++) {
                if (profileNames[i].equals(selectedName)) {
                    Profile selectedProfile = profiles.get(i);
                    System.out.println("✅ Profil sélectionné pour Joueur " + playerNumber + ": " + selectedProfile.getFullName());
                    return selectedProfile;
                }
            }
        }

        return null;
    }

    // Getters
    public ProfileManager getProfileManager() {
        return profileManager;
    }
}