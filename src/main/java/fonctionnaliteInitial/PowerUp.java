package fonctionnaliteInitial;

import Etat.PowerUpType;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.effect.Glow;

/**
 * Classe représentant les power-ups dans le jeu Bomberman.
 * <p>
 * Les power-ups sont des objets que les joueurs peuvent collecter pour obtenir
 * des améliorations temporaires ou permanentes. Chaque power-up a un type spécifique
 * qui détermine son effet et son apparence visuelle dans le jeu.
 * </p>
 */
public class PowerUp extends GameObject {

    /** Type du power-up (bombe, puissance, vitesse, vie) */
    private PowerUpType type;

    /** Indique si le power-up a été collecté */
    private boolean collected;

    /**
     * Constructeur d'un power-up.
     * <p>
     * Crée un nouveau power-up avec les coordonnées spécifiées et le type indiqué.
     * </p>
     *
     * @param x Coordonnée X dans la grille
     * @param y Coordonnée Y dans la grille
     * @param type Type du power-up (définit son effet)
     */
    public PowerUp(int x, int y, PowerUpType type) {
        super(x, y);
        this.type = type;
        this.collected = false;
    }

    /**
     * Crée la représentation visuelle du power-up.
     * <p>
     * Cette méthode génère une représentation graphique du power-up en fonction
     * de son type. Chaque type a une apparence distincte qui permet aux joueurs
     * de les identifier facilement.
     * </p>
     *
     * @return Un conteneur StackPane avec la représentation visuelle du power-up
     */
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

    /**
     * Crée l'icône d'une bombe en utilisant un sprite.
     * <p>
     * Cette méthode tente de charger une image de bombe depuis les ressources.
     * En cas d'échec, elle utilise une méthode de secours pour créer une bombe simplifiée.
     * </p>
     *
     * @param container Le conteneur où ajouter l'icône
     */
    private void createBombSpriteIcon(StackPane container) {
        try {
            // Charger le sprite depuis les ressources
            String imagePath = "/com/example/bombermansae201/Bombe/PowerUp_bombe.png";

            javafx.scene.image.Image bombImage = new javafx.scene.image.Image(
                    getClass().getResourceAsStream(imagePath)
            );

            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(bombImage);

            // Redimensionner l'image pour qu'elle s'adapte à la cellule
            imageView.setFitWidth(35);
            imageView.setFitHeight(35);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            // Effet de brillance
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

    /**
     * Crée une icône de bombe de secours en cas d'échec du chargement de l'image.
     * <p>
     * Cette méthode est appelée si le chargement du sprite échoue pour créer
     * une représentation alternative de la bombe.
     * </p>
     *
     * @param container Le conteneur où ajouter l'icône
     */
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

    /**
     * Détermine la couleur de fond en fonction du type de power-up.
     * <p>
     * Chaque type de power-up a sa propre couleur distinctive qui aide
     * les joueurs à identifier rapidement son effet.
     * </p>
     *
     * @return La couleur correspondant au type de power-up
     */
    private Color getBackgroundColor() {
        switch (type) {
            case BOMB_COUNT: return Color.ORANGE;
            case BOMB_POWER: return Color.RED;
            case SPEED: return Color.CYAN;
            case LIFE: return Color.PINK;
            default: return Color.GRAY;
        }
    }

    /**
     * Crée une icône d'explosion pour le power-up de puissance de bombe.
     * <p>
     * Cette méthode génère une étoile stylisée représentant une explosion
     * pour le power-up qui augmente la portée des explosions.
     * </p>
     *
     * @param container Le conteneur où ajouter l'icône
     */
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

    /**
     * Crée une icône de flèche pour le power-up de vitesse.
     * <p>
     * Cette méthode génère une flèche pointant vers la droite
     * pour représenter l'augmentation de la vitesse du joueur.
     * </p>
     *
     * @param container Le conteneur où ajouter l'icône
     */
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

    /**
     * Crée une icône de cœur pour le power-up de vie supplémentaire.
     * <p>
     * Cette méthode génère un cœur simplifié en utilisant des cercles et un triangle
     * pour représenter l'ajout d'une vie au joueur.
     * </p>
     *
     * @param container Le conteneur où ajouter l'icône
     */
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

    /**
     * Méthode de mise à jour du power-up.
     * <p>
     * Les power-ups sont statiques et ne nécessitent pas de mise à jour
     * à chaque cycle du jeu. Cette méthode est implémentée pour respecter
     * le contrat de la classe abstraite GameObject.
     * </p>
     */
    @Override
    public void update() {
        // Les power-ups sont statiques, pas de mise à jour nécessaire
    }

    /**
     * Marque le power-up comme collecté.
     * <p>
     * Cette méthode est appelée lorsqu'un joueur ramasse le power-up.
     * </p>
     */
    public void collect() {
        this.collected = true;
    }

    /**
     * Obtient le type du power-up.
     *
     * @return Le type du power-up
     */
    public PowerUpType getType() {
        return type;
    }

    /**
     * Vérifie si le power-up a été collecté.
     *
     * @return true si le power-up a été collecté, false sinon
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Obtient la coordonnée X du power-up dans la grille.
     *
     * @return La coordonnée X
     */
    public int getGridX() {
        return x;
    }

    /**
     * Obtient la coordonnée Y du power-up dans la grille.
     *
     * @return La coordonnée Y
     */
    public int getGridY() {
        return y;
    }
}