package com.example.bombermansae201;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.effect.Glow;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Blend;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PowerUp extends GameObject {

    private PowerUpType type;
    private boolean collected;

    public PowerUp(int x, int y, PowerUpType type) {
        super(x, y);
        this.type = type;
        this.collected = false;
    }

    public StackPane createVisualRepresentation() {
        StackPane powerUpNode = new StackPane();
        powerUpNode.setPrefSize(40, 40);
        powerUpNode.getStyleClass().add("powerup-node");
        powerUpNode.setUserData("powerup-" + type.name());

        // Icône spécifique selon le type
        switch (type) {
            case BOMB_COUNT:
                // Utiliser le sprite personnalisé pour les bombes
                createBombSpriteIcon(powerUpNode);
                break;

            case BOMB_POWER:
                // Fond coloré + icône explosion
                Circle background = new Circle(15);
                background.setFill(getBackgroundColor());
                background.setStroke(Color.WHITE);
                background.setStrokeWidth(2);

                Glow glow = new Glow();
                glow.setLevel(0.6);
                background.setEffect(glow);

                powerUpNode.getChildren().add(background);
                createExplosionIcon(powerUpNode);
                break;

            case SPEED:
                // Fond coloré + icône vitesse
                Circle speedBg = new Circle(15);
                speedBg.setFill(getBackgroundColor());
                speedBg.setStroke(Color.WHITE);
                speedBg.setStrokeWidth(2);

                Glow speedGlow = new Glow();
                speedGlow.setLevel(0.6);
                speedBg.setEffect(speedGlow);

                powerUpNode.getChildren().add(speedBg);
                createSpeedIcon(powerUpNode);
                break;

            case LIFE:
                // Fond coloré + icône cœur
                Circle lifeBg = new Circle(15);
                lifeBg.setFill(getBackgroundColor());
                lifeBg.setStroke(Color.WHITE);
                lifeBg.setStrokeWidth(2);

                Glow lifeGlow = new Glow();
                lifeGlow.setLevel(0.6);
                lifeBg.setEffect(lifeGlow);

                powerUpNode.getChildren().add(lifeBg);
                createHeartIcon(powerUpNode);
                break;
        }

        return powerUpNode;
    }

    private void createBombSpriteIcon(StackPane container) {
        try {
            // Charger votre sprite depuis les ressources
            // Remplacez "bomb_powerup.png" par le nom exact de votre fichier
            String imagePath = "/com/example/bombermansae201/Bombe/PowerUp_bombe.png"; // ou "/images/bomb_powerup.png" selon votre structure

            javafx.scene.image.Image bombImage = new javafx.scene.image.Image(
                    getClass().getResourceAsStream(imagePath)
            );

            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(bombImage);

            // Redimensionner l'image pour qu'elle s'adapte à la cellule
            imageView.setFitWidth(35);
            imageView.setFitHeight(35);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            // Effet de brillance optionnel
            Glow glow = new Glow();
            glow.setLevel(0.3);
            imageView.setEffect(glow);

            container.getChildren().add(imageView);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du sprite bomb_powerup: " + e.getMessage());
            // Fallback : utiliser l'ancienne méthode si l'image ne se charge pas
            createBombFallbackIcon(container);
        }
    }

    private void createBombFallbackIcon(StackPane container) {
        // Fallback en cas d'échec du chargement de l'image
        Circle background = new Circle(15);
        background.setFill(getBackgroundColor());
        background.setStroke(Color.WHITE);
        background.setStrokeWidth(2);

        Glow glow = new Glow();
        glow.setLevel(0.6);
        background.setEffect(glow);

        Circle bomb = new Circle(8);
        bomb.setFill(Color.BLACK);
        Text plus = new Text("+");
        plus.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        plus.setFill(Color.WHITE);

        container.getChildren().addAll(background, bomb, plus);
    }

    private Color getBackgroundColor() {
        switch (type) {
            case BOMB_COUNT: return Color.ORANGE;
            case BOMB_POWER: return Color.RED;
            case SPEED: return Color.CYAN;
            case LIFE: return Color.PINK;
            default: return Color.GRAY;
        }
    }

    private void createExplosionIcon(StackPane container) {
        // Étoile d'explosion
        Polygon star = new Polygon();
        star.getPoints().addAll(new Double[]{
                0.0, -8.0,
                2.0, -2.0,
                8.0, -2.0,
                3.0, 2.0,
                5.0, 8.0,
                0.0, 4.0,
                -5.0, 8.0,
                -3.0, 2.0,
                -8.0, -2.0,
                -2.0, -2.0
        });
        star.setFill(Color.YELLOW);
        star.setStroke(Color.ORANGE);
        star.setStrokeWidth(1);
        container.getChildren().add(star);
    }

    private void createSpeedIcon(StackPane container) {
        // Flèche vers la droite
        Polygon arrow = new Polygon();
        arrow.getPoints().addAll(new Double[]{
                -6.0, -4.0,
                2.0, -4.0,
                2.0, -8.0,
                8.0, 0.0,
                2.0, 8.0,
                2.0, 4.0,
                -6.0, 4.0
        });
        arrow.setFill(Color.WHITE);
        container.getChildren().add(arrow);
    }

    private void createHeartIcon(StackPane container) {
        // Cœur simplifié avec deux cercles et un triangle
        Circle left = new Circle(-3, -2, 4);
        left.setFill(Color.RED);
        Circle right = new Circle(3, -2, 4);
        right.setFill(Color.RED);

        Polygon bottom = new Polygon();
        bottom.getPoints().addAll(new Double[]{
                -6.0, 0.0,
                6.0, 0.0,
                0.0, 8.0
        });
        bottom.setFill(Color.RED);

        container.getChildren().addAll(bottom, left, right);
    }

    @Override
    public void update() {
        // Les power-ups sont statiques, pas de mise à jour nécessaire
    }

    public void collect() {
        this.collected = true;
    }

    // Getters
    public PowerUpType getType() {
        return type;
    }

    public boolean isCollected() {
        return collected;
    }

    public int getGridX() {
        return x;
    }

    public int getGridY() {
        return y;
    }
}