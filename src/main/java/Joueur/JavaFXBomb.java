package Joueur;

import fonctionnaliteInitial.GameObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class JavaFXBomb extends GameObject {

    private static final int EXPLOSION_DELAY = 3000; // 3 secondes
    private static final int BLINKING_START = 1000; // Commence à clignoter après 2 secondes (3000-1000)

    private int timer;
    private int power;
    private JavaFXPlayer owner;
    private boolean exploded;
    private int gridX;
    private int gridY;
    private Timeline countdownTimer;
    private Timeline blinkingTimer;
    private Runnable explosionCallback;

    // Images pour l'alternance
    private Image blackBombImage;
    private Image redBombImage;
    private ImageView bombImageView;
    private boolean useRedBomb;
    private boolean isBlinking;

    public JavaFXBomb(JavaFXPlayer owner, int x, int y, int power) {
        super(x, y);  // Appel du constructeur parent
        this.owner = owner;
        this.gridX = x;
        this.gridY = y;
        this.power = power;
        this.timer = EXPLOSION_DELAY;
        this.exploded = false;
        this.useRedBomb = false;
        this.isBlinking = false;

        // Pré-charger les images pour éviter les ralentissements
        loadBombImages();
    }

    private void loadBombImages() {
        try {
            // Charger les deux sprites de bombe
            String blackBombPath = "/com/example/bombermansae201/Bombe/Bombe_safe.png";
            String redBombPath = "/com/example/bombermansae201/Bombe/Bombe_rouge.png";

            blackBombImage = new Image(getClass().getResourceAsStream(blackBombPath));
            redBombImage = new Image(getClass().getResourceAsStream(redBombPath));

            System.out.println("✅ Sprites de bombe chargés avec succès (noir et rouge)");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des sprites de bombe: " + e.getMessage());
            blackBombImage = null;
            redBombImage = null;
        }
    }

    public StackPane createVisualRepresentation() {
        StackPane container = new StackPane();
        container.setPrefSize(40, 40);
        container.setMaxSize(40, 40);
        container.setMinSize(40, 40);
        container.setAlignment(javafx.geometry.Pos.CENTER);
        container.getStyleClass().add("bomb-node");

        // Essayer d'abord de charger le sprite personnalisé
        if (blackBombImage != null && redBombImage != null) {
            createBombSprite(container);
        } else {
            // Fallback vers l'ancienne version si les sprites ne chargent pas
            createFallbackBomb(container);
        }

        return container;
    }

    private void createBombSprite(StackPane container) {
        // Créer l'ImageView avec la bombe noire initialement
        bombImageView = new ImageView(blackBombImage);

        // Ajuster la taille de la bombe
        bombImageView.setFitWidth(35);
        bombImageView.setFitHeight(35);
        bombImageView.setPreserveRatio(true);
        bombImageView.setSmooth(true);

        // Centrer l'image dans le conteneur
        StackPane.setAlignment(bombImageView, javafx.geometry.Pos.CENTER);

        // Permettre à l'image de dépasser légèrement la cellule
        bombImageView.setPickOnBounds(false);

        // Effet de brillance pour indiquer que c'est interactif
        Glow glow = new Glow();
        glow.setLevel(0.4);

        // Ombre portée pour plus de profondeur
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(3);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);

        // Combiner les effets
        javafx.scene.effect.Blend blend = new javafx.scene.effect.Blend();
        blend.setTopInput(glow);
        blend.setBottomInput(shadow);
        bombImageView.setEffect(blend);

        container.getChildren().add(bombImageView);
        System.out.println("✅ Sprite de bombe avec alternance créé");
    }

    private void createFallbackBomb(StackPane container) {
        System.out.println("🔄 Utilisation du fallback pour la bombe avec clignotement");

        // Corps principal de la bombe (version agrandie)
        Circle bomb = new Circle(15);  // Plus grand que l'original (12 -> 15)
        bomb.setFill(Color.BLACK);
        bomb.setStroke(Color.DARKGRAY);
        bomb.setStrokeWidth(2);

        // Mèche de la bombe
        Rectangle fuse = new Rectangle(3, 10);  // Plus épaisse
        fuse.setFill(Color.ORANGE);
        fuse.setTranslateY(-18);

        // Étincelle au bout de la mèche
        Circle spark = new Circle(3);  // Plus grosse
        spark.setFill(Color.YELLOW);
        spark.setTranslateY(-23);

        // Effet de lueur pour la mèche
        Glow fuseGlow = new Glow();
        fuseGlow.setLevel(0.8);
        fuse.setEffect(fuseGlow);
        spark.setEffect(fuseGlow);

        // Effet d'ombre pour la bombe
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GRAY);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        shadow.setRadius(5);
        bomb.setEffect(shadow);

        // Motif sur la bombe (plus visible)
        Circle highlight = new Circle(5);  // Plus gros
        highlight.setFill(Color.DARKGRAY);
        highlight.setTranslateX(-5);
        highlight.setTranslateY(-5);

        container.getChildren().addAll(bomb, highlight, fuse, spark);

        // Pour le fallback, on peut faire clignoter la couleur du cercle
        // (alternative simple si les sprites ne chargent pas)
        Timeline fallbackBlink = new Timeline(
                new KeyFrame(Duration.millis(BLINKING_START), e -> {
                    Timeline colorBlink = new Timeline(
                            new KeyFrame(Duration.millis(200), ev -> {
                                Color currentColor = (Color) bomb.getFill();
                                bomb.setFill(currentColor.equals(Color.BLACK) ? Color.DARKRED : Color.BLACK);
                            })
                    );
                    colorBlink.setCycleCount(Timeline.INDEFINITE);
                    colorBlink.play();
                })
        );
        fallbackBlink.play();
    }

    public void startCountdown(Runnable onExplosion) {
        this.explosionCallback = onExplosion;

        // Timer principal pour l'explosion
        countdownTimer = new Timeline(
                new KeyFrame(Duration.millis(EXPLOSION_DELAY), e -> explode())
        );

        // Timer pour démarrer le clignotement
        Timeline startBlinkingTimer = new Timeline(
                new KeyFrame(Duration.millis(BLINKING_START), e -> startBlinking())
        );

        countdownTimer.play();
        startBlinkingTimer.play();
    }

    private void startBlinking() {
        if (bombImageView != null && !exploded) {
            isBlinking = true;
            System.out.println("💣 Bombe commence à clignoter !");

            // Clignotement toutes les 200ms (de plus en plus rapide vers la fin)
            blinkingTimer = new Timeline(
                    new KeyFrame(Duration.millis(200), e -> toggleBombColor())
            );
            blinkingTimer.setCycleCount(Timeline.INDEFINITE);
            blinkingTimer.play();

            // Accélérer le clignotement dans la dernière seconde
            Timeline accelerateTimer = new Timeline(
                    new KeyFrame(Duration.millis(500), e -> accelerateBlinking())
            );
            accelerateTimer.play();
        }
    }

    private void toggleBombColor() {
        if (bombImageView != null && !exploded) {
            useRedBomb = !useRedBomb;

            if (useRedBomb) {
                bombImageView.setImage(redBombImage);
                // Augmenter légèrement l'effet de brillance pour la bombe rouge
                Glow redGlow = new Glow();
                redGlow.setLevel(0.6);

                DropShadow shadow = new DropShadow();
                shadow.setColor(Color.BLACK);
                shadow.setRadius(3);
                shadow.setOffsetX(2);
                shadow.setOffsetY(2);

                javafx.scene.effect.Blend blend = new javafx.scene.effect.Blend();
                blend.setTopInput(redGlow);
                blend.setBottomInput(shadow);
                bombImageView.setEffect(blend);
            } else {
                bombImageView.setImage(blackBombImage);
                // Effet normal pour la bombe noire
                Glow normalGlow = new Glow();
                normalGlow.setLevel(0.4);

                DropShadow shadow = new DropShadow();
                shadow.setColor(Color.BLACK);
                shadow.setRadius(3);
                shadow.setOffsetX(2);
                shadow.setOffsetY(2);

                javafx.scene.effect.Blend blend = new javafx.scene.effect.Blend();
                blend.setTopInput(normalGlow);
                blend.setBottomInput(shadow);
                bombImageView.setEffect(blend);
            }
        }
    }

    private void accelerateBlinking() {
        if (blinkingTimer != null && !exploded) {
            blinkingTimer.stop();

            // Clignotement plus rapide (100ms) pour la dernière phase
            blinkingTimer = new Timeline(
                    new KeyFrame(Duration.millis(100), e -> toggleBombColor())
            );
            blinkingTimer.setCycleCount(Timeline.INDEFINITE);
            blinkingTimer.play();

            System.out.println("💥 Clignotement accéléré - explosion imminente !");
        }
    }

    @Override
    public void update() {
        // La mise à jour est gérée par le Timeline
        setPosition(gridX, gridY);  // Synchronisation des coordonnées
    }

    public void explode() {
        if (!exploded) {
            exploded = true;

            // Arrêter tous les timers
            if (countdownTimer != null) {
                countdownTimer.stop();
            }
            if (blinkingTimer != null) {
                blinkingTimer.stop();
            }

            System.out.println("💥 BOOM ! Bombe explosée !");

            if (explosionCallback != null) {
                explosionCallback.run();
            }
        }
    }

    // Getters
    public int getTimer() { return timer; }
    public int getPower() { return power; }
    public JavaFXPlayer getOwner() { return owner; }
    public boolean isExploded() { return exploded; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
}