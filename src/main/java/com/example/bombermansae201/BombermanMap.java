package com.example.bombermansae201;

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
import java.util.Random;

public class BombermanMap extends Application {

    private static final int GRID_WIDTH = 15;
    private static final int GRID_HEIGHT = 13;
    private static final int CELL_SIZE = 40; // Taille augmentée pour meilleure visibilité

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

    // Carte de base avec structure fixe (murs et spawns)
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
        // Générer la carte avec blocs aléatoires
        generateRandomMap();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(0);
        gridPane.setVgap(0);
        // Arrière-plan style NES
        gridPane.setStyle("-fx-background-color: #000000; -fx-padding: 20;");

        // Créer la grille de jeu style 8-bit
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

        Scene scene = new Scene(gridPane, GRID_WIDTH * CELL_SIZE + 100, GRID_HEIGHT * CELL_SIZE + 150);
        primaryStage.setTitle("🎮 BOMBERMAN - Retro Style 🎮");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private StackPane createRetroEmptyCell() {
        StackPane container = new StackPane();

        // Sol pixelisé style rétro
        Rectangle floor = new Rectangle(CELL_SIZE, CELL_SIZE);
        floor.setFill(RETRO_GRAY);
        floor.setStroke(RETRO_WHITE);
        floor.setStrokeWidth(1);

        // Motif de carrelage 8-bit
        Rectangle checker1 = new Rectangle(4, 4);
        checker1.setFill(RETRO_WHITE);
        checker1.setTranslateX(-10);
        checker1.setTranslateY(-10);

        Rectangle checker2 = new Rectangle(4, 4);
        checker2.setFill(RETRO_WHITE);
        checker2.setTranslateX(10);
        checker2.setTranslateY(10);

        container.getChildren().addAll(floor, checker1, checker2);
        return container;
    }

    private StackPane createRetroWallCell() {
        StackPane container = new StackPane();

        // Mur principal style rétro - bleu classique
        Rectangle wall = new Rectangle(CELL_SIZE, CELL_SIZE);
        wall.setFill(RETRO_BLUE);
        wall.setStroke(RETRO_BLACK);
        wall.setStrokeWidth(2);

        // Détails pixelisés - style briques
        Rectangle brick1 = new Rectangle(CELL_SIZE - 8, 6);
        brick1.setFill(RETRO_WHITE);
        brick1.setTranslateY(-8);

        Rectangle brick2 = new Rectangle(CELL_SIZE - 8, 6);
        brick2.setFill(RETRO_WHITE);
        brick2.setTranslateY(8);

        // Lignes de séparation
        Rectangle line1 = new Rectangle(CELL_SIZE - 4, 2);
        line1.setFill(RETRO_DARK_GRAY);
        line1.setTranslateY(-2);

        Rectangle line2 = new Rectangle(CELL_SIZE - 4, 2);
        line2.setFill(RETRO_DARK_GRAY);
        line2.setTranslateY(2);

        // Coins arrondis style rétro
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

        // Bloc destructible style rétro - orange/rouge classique
        Rectangle block = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2);
        block.setFill(RETRO_YELLOW);
        block.setStroke(RETRO_RED);
        block.setStrokeWidth(2);

        // Motif en croix style 8-bit
        Rectangle crossV = new Rectangle(4, CELL_SIZE - 8);
        crossV.setFill(RETRO_RED);

        Rectangle crossH = new Rectangle(CELL_SIZE - 8, 4);
        crossH.setFill(RETRO_RED);

        // Points de détail pixelisés
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

        // Sol de spawn style rétro
        Rectangle floor = new Rectangle(CELL_SIZE, CELL_SIZE);
        floor.setFill(RETRO_GREEN);
        floor.setStroke(RETRO_BLACK);
        floor.setStrokeWidth(1);

        // Étoile de spawn style 8-bit
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

        // Cercle de contour
        Circle spawnCircle = new Circle(12);
        spawnCircle.setFill(Color.TRANSPARENT);
        spawnCircle.setStroke(RETRO_WHITE);
        spawnCircle.setStrokeWidth(2);

        // Points clignotants style arcade
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

        // Copier la carte de base
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                gameMap[row][col] = baseMap[row][col];
            }
        }

        // Ajouter des blocs destructibles aléatoirement
        for (int row = 1; row < GRID_HEIGHT - 1; row++) {
            for (int col = 1; col < GRID_WIDTH - 1; col++) {
                // Ne pas placer de blocs sur les murs fixes, zones de spawn ou leurs alentours immédiats
                if (gameMap[row][col] == 0 && !isNearSpawn(row, col)) {
                    // 65% de chance de placer un bloc destructible
                    if (random.nextDouble() < 0.65) {
                        gameMap[row][col] = DESTRUCTIBLE;
                    }
                }
            }
        }
    }

    private boolean isNearSpawn(int row, int col) {
        // Vérifier si la position est près d'une zone de spawn (coins)
        // Coin supérieur gauche
        if ((row <= 2 && col <= 2)) return true;
        // Coin supérieur droit
        if ((row <= 2 && col >= GRID_WIDTH - 3)) return true;
        // Coin inférieur gauche
        if ((row >= GRID_HEIGHT - 3 && col <= 2)) return true;
        // Coin inférieur droit
        if ((row >= GRID_HEIGHT - 3 && col >= GRID_WIDTH - 3)) return true;

        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}