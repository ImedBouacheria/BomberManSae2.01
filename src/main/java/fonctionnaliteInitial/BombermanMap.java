package fonctionnaliteInitial;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.effect.Glow;
import javafx.geometry.Pos;
import java.util.Random;

public class BombermanMap {

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

    // Palette de couleurs pour le thême pirate
    private static final Color PIRATE_SAND = Color.web("#F4D03F");
    private static final Color PIRATE_WOOD = Color.web("#8B4513");
    private static final Color PIRATE_ISLAND = Color.web("#1E8449");
    private static final Color PIRATE_TREASURE = Color.web("#F39C12");
    private static final Color PIRATE_WATER = Color.web("#3498DB");

    // Types de cellules
    private static final int EMPTY = 0;
    private static final int WALL = 1;
    private static final int DESTRUCTIBLE = 2;
    private static final int SPAWN_ZONE = 3;

    private Random random = new Random();

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

    public void generateRandomMap() {
        Random random = new Random();
        gameMap = new int[GRID_HEIGHT][GRID_WIDTH];

        // Copie de la carte de base
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                gameMap[row][col] = baseMap[row][col];
            }
        }

        // Ajout de murs destructibles aléatoires
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

    public enum Theme {
        CLASSIC,
        PIRATE
    }

    private static Theme currentTheme = Theme.CLASSIC;

    private boolean isNearSpawn(int row, int col) {
        // Zones de spawn aux 4 coins
        if ((row <= 2 && col <= 2)) return true;
        if ((row <= 2 && col >= GRID_WIDTH - 3)) return true;
        if ((row >= GRID_HEIGHT - 3 && col <= 2)) return true;
        if ((row >= GRID_HEIGHT - 3 && col >= GRID_WIDTH - 3)) return true;

        return false;
    }

    public static void setTheme(Theme theme) {
        currentTheme = theme;
    }

    public static Theme getCurrentTheme() {
        return currentTheme;
    }

    // Méthodes principales avec les noms originaux
    public StackPane createRetroEmptyCell() {
        if (currentTheme == Theme.PIRATE) {
            StackPane container = new StackPane();
            container.setPrefSize(CELL_SIZE, CELL_SIZE);
            container.setAlignment(Pos.CENTER);

            Rectangle sand = new Rectangle(CELL_SIZE, CELL_SIZE);
            sand.setFill(PIRATE_WATER);
            sand.setStroke(Color.web("#D4AC0D"));
            sand.setStrokeWidth(1);

            for (int i = 0; i < 5; i++) {
                Circle grain = new Circle(1);
                grain.setFill(Color.web("#D4AC0D"));
                grain.setTranslateX(random.nextInt(CELL_SIZE) - CELL_SIZE/2);
                grain.setTranslateY(random.nextInt(CELL_SIZE) - CELL_SIZE/2);
                container.getChildren().add(grain);
            }

            container.getChildren().add(sand);
            return container;
        } else {
            StackPane container = new StackPane();
            container.setPrefSize(CELL_SIZE, CELL_SIZE);
            container.setAlignment(Pos.CENTER);

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
    }

    public StackPane createRetroWallCell() {
        if (currentTheme == Theme.PIRATE) {
            StackPane container = new StackPane();
            container.setPrefSize(CELL_SIZE, CELL_SIZE);
            container.setAlignment(Pos.CENTER);

            Rectangle island = new Rectangle(CELL_SIZE, CELL_SIZE);
            island.setFill(PIRATE_SAND);
            island.setStroke(Color.web("#145A32"));
            island.setStrokeWidth(2);

            Polygon leaf1 = new Polygon();
            leaf1.getPoints().addAll(new Double[]{
                    0.0, 5.0,
                    -5.0, 0.0,
                    0.0, -5.0,
                    5.0, 0.0
            });
            leaf1.setFill(Color.web("#27AE60"));
            leaf1.setTranslateX(-10);
            leaf1.setTranslateY(-10);

            Polygon leaf2 = new Polygon();
            leaf2.getPoints().addAll(new Double[]{
                    0.0, 5.0,
                    -5.0, 0.0,
                    0.0, -5.0,
                    5.0, 0.0
            });
            leaf2.setFill(Color.web("#27AE60"));
            leaf2.setTranslateX(10);
            leaf2.setTranslateY(10);

            container.getChildren().addAll(island, leaf1, leaf2);
            return container;
        } else {
            StackPane container = new StackPane();
            container.setPrefSize(CELL_SIZE, CELL_SIZE);
            container.setAlignment(Pos.CENTER);

            Rectangle wall = new Rectangle(CELL_SIZE, CELL_SIZE);
            wall.setFill(RETRO_BLUE);
            wall.setStroke(RETRO_BLACK);
            wall.setStrokeWidth(2);

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
    }

    public StackPane createRetroDestructibleCell() {
        if (currentTheme == Theme.PIRATE) {
            StackPane container = new StackPane();
            container.setPrefSize(CELL_SIZE, CELL_SIZE);
            container.setAlignment(Pos.CENTER);

            Rectangle wood = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2);
            wood.setFill(PIRATE_WOOD);
            wood.setStroke(Color.web("#5D4037"));
            wood.setStrokeWidth(2);

            for (int i = 0; i < 3; i++) {
                Rectangle line = new Rectangle(CELL_SIZE - 10, 2);
                line.setFill(Color.web("#5D4037"));
                line.setTranslateY(-5 + i * 5);
                container.getChildren().add(line);
            }

            Circle nail1 = new Circle(2);
            nail1.setFill(Color.web("#BDC3C7"));
            nail1.setTranslateX(-8);
            nail1.setTranslateY(-8);

            Circle nail2 = new Circle(2);
            nail2.setFill(Color.web("#BDC3C7"));
            nail2.setTranslateX(8);
            nail2.setTranslateY(-8);

            Circle nail3 = new Circle(2);
            nail3.setFill(Color.web("#BDC3C7"));
            nail3.setTranslateX(-8);
            nail3.setTranslateY(8);

            Circle nail4 = new Circle(2);
            nail4.setFill(Color.web("#BDC3C7"));
            nail4.setTranslateX(8);
            nail4.setTranslateY(8);

            container.getChildren().addAll(wood, nail1, nail2, nail3, nail4);
            return container;
        } else {
            StackPane container = new StackPane();
            container.setPrefSize(CELL_SIZE, CELL_SIZE);
            container.setAlignment(Pos.CENTER);

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
    }

    public StackPane createRetroSpawnZoneCell() {
        StackPane container = new StackPane();
        container.setPrefSize(CELL_SIZE, CELL_SIZE);
        container.setAlignment(Pos.CENTER);

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

    // Getters et setters
    public int[][] getGameMap() {
        return gameMap;
    }

    public void setCell(int x, int y, int value) {
        if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT) {
            gameMap[y][x] = value;
        }
    }

    public int getCell(int x, int y) {
        if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT) {
            return gameMap[y][x];
        }
        return -1; // Valeur invalide
    }

    public int getGridWidth() {
        return GRID_WIDTH;
    }

    public int getGridHeight() {
        return GRID_HEIGHT;
    }

    public int getCellSize() {
        return CELL_SIZE;
    }
}