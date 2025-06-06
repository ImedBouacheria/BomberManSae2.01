package Jeu_PowerUp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import java.util.Random;

public class BombermanMap extends Application {

    private static final int GRID_WIDTH = 15;
    private static final int GRID_HEIGHT = 13;
    private static final int CELL_SIZE = 40;

    // Palette de couleurs rétro classique
    private static final Color RETRO_BLUE = Color.web("#5C94FC");
    private static final Color RETRO_GREEN = Color.web("#00A800");
    private static final Color RETRO_RED = Color.web("#F83800");
    private static final Color RETRO_YELLOW = Color.web("#FFA044");
    private static final Color RETRO_PURPLE = Color.web("#8058F8");
    private static final Color RETRO_GRAY = Color.web("#BCBCBC");
    private static final Color RETRO_DARK_GRAY = Color.web("#7C7C7C");
    private static final Color RETRO_BLACK = Color.web("#000000");
    private static final Color RETRO_WHITE = Color.web("#FCFCFC");

    // Types de cellules
    private static final int EMPTY = 0;
    private static final int WALL = 1;
    private static final int DESTRUCTIBLE = 2;
    private static final int SPAWN_ZONE = 3;

    // Variables pour l'animation
    private double time = 0;
    private Random random = new Random();
    private StackPane rootPane;
    private GridPane gridPane;

    // Carte de base avec structure fixe
    private int[][] baseMap = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,3,0,0,0,0,0,0,0,0,0,0,0,3,1},
            {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
            {1,3,0,0,0,0,0,0,0,0,0,0,0,3,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    private int[][] gameMap;

    @Override
    public void start(Stage primaryStage) {
        generateRandomMap();

        rootPane = new StackPane();

        // Créer le fond animé
        createAnimatedBackground();

        // Créer la grille de jeu
        gridPane = new GridPane();
        gridPane.setHgap(0);
        gridPane.setVgap(0);

        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                StackPane cellContainer = new StackPane();
                cellContainer.setPrefSize(CELL_SIZE, CELL_SIZE);
                cellContainer.setAlignment(Pos.CENTER);

                switch (gameMap[row][col]) {
                    case EMPTY:
                        cellContainer = createRetroEmptyCell();
                        break;
                    case WALL:
                        cellContainer = createRetroWallCell();
                        break;
                    case DESTRUCTIBLE:
                        cellContainer = createRetroDestructibleCell();
                        break;
                    case SPAWN_ZONE:
                        cellContainer = createRetroSpawnZoneCell();
                        break;
                }

                gridPane.add(cellContainer, col, row);
            }
        }

        // Conteneur de la zone de jeu avec effet
        StackPane gameArea = new StackPane(gridPane);
        gameArea.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 15; -fx-padding: 25;");

        // Ajouter un effet de lueur
        DropShadow shadow = new DropShadow();
        shadow.setColor(RETRO_BLUE);
        shadow.setRadius(20);
        gameArea.setEffect(shadow);

        rootPane.getChildren().add(gameArea);

        Scene scene = new Scene(rootPane, GRID_WIDTH * CELL_SIZE + 200, GRID_HEIGHT * CELL_SIZE + 200);
        primaryStage.setTitle("🎮 BOMBERMAN - Retro Animated Style 🎮");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Démarrer l'animation simple
        startAnimation();
    }

    private void createAnimatedBackground() {
        // Fond principal avec dégradé
        Rectangle background = new Rectangle(800, 700);
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#001122")),
                new Stop(0.5, Color.web("#000033")),
                new Stop(1, Color.web("#000011"))
        );
        background.setFill(gradient);

        rootPane.getChildren().add(background);

        // Ajouter quelques étoiles fixes
        for (int i = 0; i < 30; i++) {
            Circle star = new Circle(2);
            star.setFill(Color.WHITE);
            star.setTranslateX(random.nextDouble() * 600 - 300);
            star.setTranslateY(random.nextDouble() * 500 - 250);
            star.setOpacity(0.3 + random.nextDouble() * 0.7);
            rootPane.getChildren().add(star);
        }

        // Ajouter des étoiles colorées
        Color[] colors = {RETRO_YELLOW, RETRO_BLUE, RETRO_PURPLE, RETRO_GREEN};
        for (int i = 0; i < 15; i++) {
            Circle colorStar = new Circle(1.5);
            colorStar.setFill(colors[random.nextInt(colors.length)]);
            colorStar.setTranslateX(random.nextDouble() * 600 - 300);
            colorStar.setTranslateY(random.nextDouble() * 500 - 250);
            colorStar.setOpacity(0.5 + random.nextDouble() * 0.5);
            rootPane.getChildren().add(colorStar);
        }
    }

    private void startAnimation() {
        // Animation simple pour faire scintiller les étoiles
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> {
                    time += 0.1;
                    // Faire scintiller quelques étoiles aléatoirement
                    if (random.nextDouble() < 0.1) {
                        updateStarsTwinkle();
                    }
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateStarsTwinkle() {
        // Simple effet de scintillement sur les étoiles existantes
        rootPane.getChildren().stream()
                .filter(node -> node instanceof Circle)
                .forEach(node -> {
                    Circle star = (Circle) node;
                    if (random.nextDouble() < 0.3) {
                        double newOpacity = 0.2 + random.nextDouble() * 0.8;
                        star.setOpacity(newOpacity);
                    }
                });
    }

    private StackPane createRetroEmptyCell() {
        StackPane container = new StackPane();

        Rectangle floor = new Rectangle(CELL_SIZE, CELL_SIZE);
        floor.setFill(RETRO_GRAY);
        floor.setStroke(RETRO_WHITE);
        floor.setStrokeWidth(1);
        floor.setOpacity(0.9);

        Rectangle checker1 = new Rectangle(4, 4);
        checker1.setFill(RETRO_WHITE);
        checker1.setTranslateX(-10);
        checker1.setTranslateY(-10);
        checker1.setOpacity(0.7);

        Rectangle checker2 = new Rectangle(4, 4);
        checker2.setFill(RETRO_WHITE);
        checker2.setTranslateX(10);
        checker2.setTranslateY(10);
        checker2.setOpacity(0.7);

        container.getChildren().addAll(floor, checker1, checker2);
        return container;
    }

    private StackPane createRetroWallCell() {
        StackPane container = new StackPane();

        Rectangle wall = new Rectangle(CELL_SIZE, CELL_SIZE);
        wall.setFill(RETRO_BLUE);
        wall.setStroke(RETRO_BLACK);
        wall.setStrokeWidth(2);

        // Ajouter un effet de lueur aux murs
        Glow glow = new Glow();
        glow.setLevel(0.3);
        wall.setEffect(glow);

        Rectangle brick1 = new Rectangle(CELL_SIZE - 8, 6);
        brick1.setFill(RETRO_WHITE);
        brick1.setTranslateY(-8);

        Rectangle brick2 = new Rectangle(CELL_SIZE - 8, 6);
        brick2.setFill(RETRO_WHITE);
        brick2.setTranslateY(8);

        Rectangle line1 = new Rectangle(CELL_SIZE - 4, 2);
        line1.setFill(RETRO_DARK_GRAY);
        line1.setTranslateY(-2);

        Rectangle line2 = new Rectangle(CELL_SIZE - 4, 2);
        line2.setFill(RETRO_DARK_GRAY);
        line2.setTranslateY(2);

        Rectangle corner1 = new Rectangle(4, 4);
        corner1.setFill(RETRO_WHITE);
        corner1.setTranslateX(-10);
        corner1.setTranslateY(-10);

        Rectangle corner2 = new Rectangle(4, 4);
        corner2.setFill(RETRO_WHITE);
        corner2.setTranslateX(10);
        corner2.setTranslateY(-10);

        Rectangle corner3 = new Rectangle(4, 4);
        corner3.setFill(RETRO_WHITE);
        corner3.setTranslateX(-10);
        corner3.setTranslateY(10);

        Rectangle corner4 = new Rectangle(4, 4);
        corner4.setFill(RETRO_WHITE);
        corner4.setTranslateX(10);
        corner4.setTranslateY(10);

        container.getChildren().addAll(wall, brick1, brick2, line1, line2,
                corner1, corner2, corner3, corner4);
        return container;
    }

    private StackPane createRetroDestructibleCell() {
        StackPane container = new StackPane();

        Rectangle block = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2);
        block.setFill(RETRO_YELLOW);
        block.setStroke(RETRO_RED);
        block.setStrokeWidth(2);

        Rectangle crossV = new Rectangle(4, CELL_SIZE - 8);
        crossV.setFill(RETRO_RED);

        Rectangle crossH = new Rectangle(CELL_SIZE - 8, 4);
        crossH.setFill(RETRO_RED);

        Circle dot1 = new Circle(2);
        dot1.setFill(RETRO_WHITE);
        dot1.setTranslateX(-8);
        dot1.setTranslateY(-8);

        Circle dot2 = new Circle(2);
        dot2.setFill(RETRO_WHITE);
        dot2.setTranslateX(8);
        dot2.setTranslateY(-8);

        Circle dot3 = new Circle(2);
        dot3.setFill(RETRO_WHITE);
        dot3.setTranslateX(-8);
        dot3.setTranslateY(8);

        Circle dot4 = new Circle(2);
        dot4.setFill(RETRO_WHITE);
        dot4.setTranslateX(8);
        dot4.setTranslateY(8);

        container.getChildren().addAll(block, crossV, crossH, dot1, dot2, dot3, dot4);
        return container;
    }

    private StackPane createRetroSpawnZoneCell() {
        StackPane container = new StackPane();

        Rectangle floor = new Rectangle(CELL_SIZE, CELL_SIZE);
        floor.setFill(RETRO_GREEN);
        floor.setStroke(RETRO_BLACK);
        floor.setStrokeWidth(1);

        // Effet de lueur pour les zones de spawn
        Glow spawnGlow = new Glow();
        spawnGlow.setLevel(0.5);
        floor.setEffect(spawnGlow);

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
        star.setFill(RETRO_WHITE);
        star.setStroke(RETRO_BLACK);
        star.setStrokeWidth(1);

        Circle spawnCircle = new Circle(12);
        spawnCircle.setFill(Color.TRANSPARENT);
        spawnCircle.setStroke(RETRO_WHITE);
        spawnCircle.setStrokeWidth(2);

        Rectangle pixel1 = new Rectangle(2, 2);
        pixel1.setFill(RETRO_WHITE);
        pixel1.setTranslateX(-6);
        pixel1.setTranslateY(-6);

        Rectangle pixel2 = new Rectangle(2, 2);
        pixel2.setFill(RETRO_WHITE);
        pixel2.setTranslateX(6);
        pixel2.setTranslateY(-6);

        Rectangle pixel3 = new Rectangle(2, 2);
        pixel3.setFill(RETRO_WHITE);
        pixel3.setTranslateX(-6);
        pixel3.setTranslateY(6);

        Rectangle pixel4 = new Rectangle(2, 2);
        pixel4.setFill(RETRO_WHITE);
        pixel4.setTranslateX(6);
        pixel4.setTranslateY(6);

        container.getChildren().addAll(floor, spawnCircle, star, pixel1, pixel2, pixel3, pixel4);
        return container;
    }

    private void generateRandomMap() {
        Random random = new Random();
        gameMap = new int[GRID_HEIGHT][GRID_WIDTH];

        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                gameMap[row][col] = baseMap[row][col];
            }
        }

        for (int row = 1; row < GRID_HEIGHT - 1; row++) {
            for (int col = 1; col < GRID_WIDTH - 1; col++) {
                if (gameMap[row][col] == 0 && !isNearSpawn(row, col)) {
                    if (random.nextDouble() < 0.65) {
                        gameMap[row][col] = DESTRUCTIBLE;
                    }
                }
            }
        }
    }

    private boolean isNearSpawn(int row, int col) {
        if ((row <= 2 && col <= 2)) return true;
        if ((row <= 2 && col >= GRID_WIDTH - 3)) return true;
        if ((row >= GRID_HEIGHT - 3 && col <= 2)) return true;
        if ((row >= GRID_HEIGHT - 3 && col >= GRID_WIDTH - 3)) return true;

        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}