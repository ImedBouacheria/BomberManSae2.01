package com.example.bombermansae201;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.effect.Glow;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Blend;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameController {

    // Composants UI
    private GridPane gameGrid;
    private Label player1Info;
    private Label player2Info;
    private Label player3Info;
    private Label player4Info;
    private Label gameStatusLabel;
    private VBox gameInfoPanel;
    private Button backToMenuButton;

    // Logique de jeu
    private BombermanApplication application;
    private BombermanMap gameMap;
    private List<JavaFXPlayer> players;
    private List<JavaFXBomb> bombs;
    private List<PowerUp> powerUps;
    private Set<KeyCode> pressedKeys;
    private AnimationTimer gameLoop;
    private GameState currentState;
    private int currentPlayerCount;

    // Tracking des nodes pour éviter les traces
    private Map<JavaFXPlayer, Node> playerNodes;
    private Map<JavaFXBomb, Node> bombNodes;
    private Map<PowerUp, Node> powerUpNodes;

    // Positions de spawn pour chaque joueur
    private int[][] spawnPositions;

    // Système de cooldown pour éviter les mouvements trop rapides
    private Map<JavaFXPlayer, Long> lastMoveTime;
    private static final long MOVE_COOLDOWN = 150_000_000; // 150ms en nanosecondes

    // Système d'apparition aléatoire des power-ups
    private long lastPowerUpSpawn;
    private static final long POWERUP_SPAWN_INTERVAL = 5_000_000_000L; // 5 secondes en nanosecondes
    private static final long POWERUP_SPAWN_VARIANCE = 3_000_000_000L; // ±3 secondes de variance

    // Système d'effets d'explosion
    private List<ExplosionEffect> explosionEffects;
    private static final long EXPLOSION_EFFECT_DURATION = 800_000_000L; // 800ms en nanosecondes

    // Images des flammes d'explosion
    private javafx.scene.image.Image flameStartImage;
    private javafx.scene.image.Image flameEndImage;
    private javafx.scene.image.Image flameCenterImage;

    // Constantes
    private static final int GRID_WIDTH = 15;
    private static final int GRID_HEIGHT = 13;

    public GameController() {
        players = new ArrayList<>();
        bombs = new ArrayList<>();
        powerUps = new ArrayList<>();
        explosionEffects = new ArrayList<>();
        pressedKeys = new HashSet<>();
        playerNodes = new HashMap<>();
        bombNodes = new HashMap<>();
        powerUpNodes = new HashMap<>();
        lastMoveTime = new HashMap<>();
        currentState = GameState.MENU;
        lastPowerUpSpawn = 0;

        // Initialisation des positions de spawn
        spawnPositions = new int[][]{
                {1, 1}, {GRID_WIDTH - 2, 1},
                {1, GRID_HEIGHT - 2}, {GRID_WIDTH - 2, GRID_HEIGHT - 2}
        };
    }

    public void setApplication(BombermanApplication app) {
        this.application = app;
    }

    public BorderPane createGameScene() {
        BorderPane root = new BorderPane();

        // Création de la grille de jeu
        createGameGrid();
        root.setCenter(gameGrid);

        // Création du panneau d'informations
        createInfoPanel();
        root.setRight(gameInfoPanel);

        // PAS de configuration des événements clavier ici pour éviter les doublons
        // Les événements sont gérés dans BombermanApplication

        return root;
    }

    private void createGameGrid() {
        gameGrid = new GridPane();
        gameGrid.setHgap(0);
        gameGrid.setVgap(0);
        gameGrid.setAlignment(Pos.CENTER);
        gameGrid.setStyle("-fx-background-color: #000033; -fx-border-color: #FFFFFF; -fx-border-width: 4;");
        gameGrid.setPadding(new Insets(10));
    }

    private void createInfoPanel() {
        gameInfoPanel = new VBox(20);
        gameInfoPanel.setAlignment(Pos.TOP_CENTER);
        gameInfoPanel.setMaxWidth(200);
        gameInfoPanel.setMinWidth(200);
        gameInfoPanel.setStyle("-fx-background-color: #000000; -fx-border-color: #FFFFFF; -fx-border-width: 2;");
        gameInfoPanel.setPadding(new Insets(20));

        // Titre
        Label titleLabel = new Label("BOMBERMAN");
        titleLabel.setStyle("-fx-text-fill: #FFFF00; -fx-font-size: 18; -fx-font-weight: bold;");

        // Status du jeu
        gameStatusLabel = new Label("EN ATTENTE");
        gameStatusLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-size: 14; -fx-font-weight: bold;");

        // Création des labels pour les joueurs
        createPlayerLabels();

        // Informations des contrôles
        VBox controlsInfo = createControlsInfo();

        // Bouton retour
        backToMenuButton = new Button("RETOUR MENU");
        backToMenuButton.setStyle("-fx-background-color: #AA0000; -fx-text-fill: #FFFFFF; " +
                "-fx-border-color: #FFFFFF; -fx-border-width: 2; -fx-font-size: 12; -fx-font-weight: bold;");
        backToMenuButton.setOnAction(e -> handleBackToMenu());

        // Assemblage
        gameInfoPanel.getChildren().addAll(
                titleLabel,
                gameStatusLabel,
                player1Info,
                player2Info,
                player3Info,
                player4Info,
                controlsInfo,
                backToMenuButton
        );
    }

    private void createPlayerLabels() {
        player1Info = new Label("JOUEUR 1\nVies: 3\nBombes: 3\nPuissance: 2");
        player1Info.setStyle("-fx-text-fill: #FF4444; -fx-font-size: 12; -fx-font-weight: bold;");

        player2Info = new Label("JOUEUR 2\nVies: 3\nBombes: 3\nPuissance: 2");
        player2Info.setStyle("-fx-text-fill: #4444FF; -fx-font-size: 12; -fx-font-weight: bold;");

        player3Info = new Label("JOUEUR 3\nVies: 3\nBombes: 3\nPuissance: 2");
        player3Info.setStyle("-fx-text-fill: #44FF44; -fx-font-size: 12; -fx-font-weight: bold;");
        player3Info.setVisible(false);

        player4Info = new Label("JOUEUR 4\nVies: 3\nBombes: 3\nPuissance: 2");
        player4Info.setStyle("-fx-text-fill: #FFFF44; -fx-font-size: 12; -fx-font-weight: bold;");
        player4Info.setVisible(false);
    }

    private VBox createControlsInfo() {
        VBox controlsInfo = new VBox(10);

        Label controlsTitle = new Label("CONTROLES:");
        controlsTitle.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 12; -fx-font-weight: bold;");

        Label controlsText = new Label("J1: ZQSD + A\nJ2: ↑↓←→ + SPACE\nJ3: YGHJ + T\nJ4: OKLM + I\n\nCollectez des power-ups\npour améliorer vos stats !");
        controlsText.setStyle("-fx-text-fill: #CCCCCC; -fx-font-size: 10;");

        controlsInfo.getChildren().addAll(controlsTitle, controlsText);
        return controlsInfo;
    }

    public void initializeGame(int playerCount) {
        System.out.println("Initialisation du jeu avec " + playerCount + " joueurs...");
        this.currentPlayerCount = playerCount;

        // Nettoyage des données précédentes
        cleanupGame();

        // Création de la carte
        gameMap = new BombermanMap();
        gameMap.generateRandomMap();

        // Création des joueurs
        createPlayers(playerCount);

        // Affichage de la carte
        displayMap();

        // Placement des joueurs
        placePlayers();

        // Générer seulement quelques power-ups au début (optionnel)
        generateInitialPowerUps();

        // Mise à jour des infos
        updatePlayerInfo();

        // Démarrage du jeu
        startGame();

        // Charger les sprites d'explosion
        loadExplosionSprites();

        System.out.println("Jeu initialisé avec succès !");
    }

    private void cleanupGame() {
        // Arrêter le game loop si il tourne
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }

        // Nettoyer les collections
        players.clear();
        bombs.clear();
        powerUps.clear();
        explosionEffects.clear();
        playerNodes.clear();
        bombNodes.clear();
        powerUpNodes.clear();
        lastMoveTime.clear();
        pressedKeys.clear();

        // Réinitialiser les timers
        lastPowerUpSpawn = 0;

        // Nettoyer la grille
        if (gameGrid != null) {
            gameGrid.getChildren().clear();
        }
    }

    private void createPlayers(int playerCount) {
        players.clear();
        playerNodes.clear();

        // Configuration des touches originales
        KeyCode[][] playerKeys = {
                {KeyCode.Z, KeyCode.S, KeyCode.Q, KeyCode.D, KeyCode.A},           // Joueur 1: ZQSD + A
                {KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.SPACE}, // Joueur 2: Flèches + SPACE
                {KeyCode.Y, KeyCode.H, KeyCode.G, KeyCode.J, KeyCode.T},           // Joueur 3: YGHJ + T
                {KeyCode.O, KeyCode.L, KeyCode.K, KeyCode.M, KeyCode.I}            // Joueur 4: OKLM + I
        };

        String[] playerNames = {"Joueur 1", "Joueur 2", "Joueur 3", "Joueur 4"};
        javafx.scene.paint.Color[] playerColors = {
                javafx.scene.paint.Color.RED,
                javafx.scene.paint.Color.BLUE,
                javafx.scene.paint.Color.GREEN,
                javafx.scene.paint.Color.YELLOW
        };

        for (int i = 0; i < playerCount && i < 4; i++) {
            JavaFXPlayer player = new JavaFXPlayer(playerNames[i], playerColors[i]);
            player.setKeys(playerKeys[i][0], playerKeys[i][1], playerKeys[i][2], playerKeys[i][3], playerKeys[i][4]);
            players.add(player);
            System.out.println("Joueur créé: " + playerNames[i] + " (Couleur: " + playerColors[i] + ")");
            System.out.println("  Touches: " +
                    playerKeys[i][0] + " " + playerKeys[i][1] + " " +
                    playerKeys[i][2] + " " + playerKeys[i][3] + " " + playerKeys[i][4]);
        }
    }

    private void displayMap() {
        gameGrid.getChildren().clear();
        int[][] map = gameMap.getGameMap();

        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                StackPane cell = createCellForType(map[row][col]);
                if (cell != null) {
                    gameGrid.add(cell, col, row);
                }
            }
        }
    }

    private StackPane createCellForType(int cellType) {
        switch (cellType) {
            case 0: return gameMap.createRetroEmptyCell();
            case 1: return gameMap.createRetroWallCell();
            case 2: return gameMap.createRetroDestructibleCell();
            case 3: return gameMap.createRetroSpawnZoneCell();
            default: return null;
        }
    }

    private void placePlayers() {
        for (int i = 0; i < players.size(); i++) {
            JavaFXPlayer player = players.get(i);
            int spawnX = spawnPositions[i][0];
            int spawnY = spawnPositions[i][1];

            // Stocker la position de spawn dans le joueur pour pouvoir y retourner
            player.setSpawnPosition(spawnX, spawnY);
            player.setGridPosition(spawnX, spawnY);

            // Création et ajout du node visuel
            StackPane playerNode = player.createVisualRepresentation();

            // Ajouter un identifiant pour le tracking
            playerNode.getStyleClass().add("player-node");
            playerNode.setUserData("player-" + player.getName());

            gameGrid.add(playerNode, spawnX, spawnY);
            playerNodes.put(player, playerNode);

            System.out.println("Joueur " + (i+1) + " placé en position (" + spawnX + ", " + spawnY + ")");
        }
    }

    // ===== SYSTÈME DE POWER-UPS =====

    private void generateInitialPowerUps() {
        System.out.println("Génération de quelques power-ups initiaux...");

        // Générer seulement 1-2 power-ups au début
        int initialCount = 1 + (int)(Math.random() * 2); // 1-2 power-ups

        for (int i = 0; i < initialCount; i++) {
            // Types possibles au début (priorité aux bombes)
            PowerUpType[] initialTypes = {PowerUpType.BOMB_COUNT, PowerUpType.BOMB_POWER, PowerUpType.SPEED};
            PowerUpType randomType = initialTypes[(int)(Math.random() * initialTypes.length)];
            generateSinglePowerUp(randomType);
        }

        System.out.println("Power-ups initiaux générés: " + powerUps.size());
    }

    private void handleRandomPowerUpSpawning() {
        long currentTime = System.nanoTime();

        // Calculer le prochain temps d'apparition avec variance
        long variance = (long)(Math.random() * POWERUP_SPAWN_VARIANCE * 2) - POWERUP_SPAWN_VARIANCE;
        long nextSpawnTime = lastPowerUpSpawn + POWERUP_SPAWN_INTERVAL + variance;

        // Vérifier s'il est temps de faire apparaître un power-up
        if (currentTime >= nextSpawnTime) {
            // Limiter le nombre de power-ups simultanés
            int maxPowerUps = 5; // Maximum 5 power-ups sur la carte

            if (powerUps.stream().mapToInt(p -> p.isCollected() ? 0 : 1).sum() < maxPowerUps) {
                spawnRandomPowerUp();
                lastPowerUpSpawn = currentTime;

                System.out.println("🎁 Nouveau power-up apparu ! Total actifs: " +
                        powerUps.stream().mapToInt(p -> p.isCollected() ? 0 : 1).sum());
            }
        }
    }

    private void spawnRandomPowerUp() {
        // Probabilités d'apparition des différents types
        double random = Math.random();
        PowerUpType typeToSpawn;

        if (random < 0.4) {
            typeToSpawn = PowerUpType.BOMB_COUNT; // 40% - Bombes (plus fréquent)
        } else if (random < 0.7) {
            typeToSpawn = PowerUpType.BOMB_POWER; // 30% - Puissance
        } else if (random < 0.9) {
            typeToSpawn = PowerUpType.SPEED; // 20% - Vitesse
        } else {
            typeToSpawn = PowerUpType.LIFE; // 10% - Vie (rare)
        }

        generateSinglePowerUp(typeToSpawn);
    }

    private void generateSinglePowerUp(PowerUpType type) {
        int[][] map = gameMap.getGameMap();
        int attempts = 0;

        while (attempts < 50) { // Limite pour éviter les boucles infinies
            int x = 1 + (int)(Math.random() * (GRID_WIDTH - 2));
            int y = 1 + (int)(Math.random() * (GRID_HEIGHT - 2));

            // Vérifier que la position est valide et libre
            if (map[y][x] == 0 && // Cellule vide
                    !isSpawnPosition(x, y) && // Pas une position de spawn
                    !isPowerUpAt(x, y) && // Pas déjà un power-up
                    !isOccupiedByPlayer(x, y, null) && // Pas occupé par un joueur
                    !isOccupiedByBomb(x, y)) { // Pas occupé par une bombe

                PowerUp powerUp = new PowerUp(x, y, type);
                powerUps.add(powerUp);

                // Ajouter visuellement le power-up
                StackPane powerUpNode = powerUp.createVisualRepresentation();
                gameGrid.add(powerUpNode, x, y);
                powerUpNodes.put(powerUp, powerUpNode);

                System.out.println("✨ Power-up " + type + " apparu en (" + x + ", " + y + ")");
                return;
            }
            attempts++;
        }

        System.out.println("⚠️ Impossible de placer un power-up " + type + " après 50 tentatives");
    }

    private boolean isOccupiedByBomb(int x, int y) {
        return bombs.stream().anyMatch(bomb -> bomb.getGridX() == x && bomb.getGridY() == y);
    }

    private boolean isSpawnPosition(int x, int y) {
        for (int[] spawn : spawnPositions) {
            if (spawn[0] == x && spawn[1] == y) {
                return true;
            }
        }
        return false;
    }

    private boolean isPowerUpAt(int x, int y) {
        return powerUps.stream().anyMatch(p -> p.getGridX() == x && p.getGridY() == y && !p.isCollected());
    }

    private void checkPowerUpCollection() {
        for (JavaFXPlayer player : players) {
            if (!player.isAlive()) continue;

            int playerX = player.getGridX();
            int playerY = player.getGridY();

            // Chercher un power-up à la position du joueur
            PowerUp powerUpToCollect = powerUps.stream()
                    .filter(p -> !p.isCollected() &&
                            p.getGridX() == playerX &&
                            p.getGridY() == playerY)
                    .findFirst()
                    .orElse(null);

            if (powerUpToCollect != null) {
                collectPowerUp(player, powerUpToCollect);
            }
        }
    }

    private void collectPowerUp(JavaFXPlayer player, PowerUp powerUp) {
        System.out.println(player.getName() + " collecte un power-up " + powerUp.getType());

        // Marquer comme collecté
        powerUp.collect();

        // Supprimer visuellement
        Node powerUpNode = powerUpNodes.get(powerUp);
        if (powerUpNode != null) {
            gameGrid.getChildren().remove(powerUpNode);
            powerUpNodes.remove(powerUp);
        }

        // Appliquer l'effet du power-up
        player.applyPowerUp(powerUp.getType());

        // Remettre la cellule de base
        restoreBaseCell(powerUp.getGridX(), powerUp.getGridY());
    }

    private void updatePlayerInfo() {
        Label[] infoLabels = {player1Info, player2Info, player3Info, player4Info};

        for (int i = 0; i < infoLabels.length; i++) {
            if (i < players.size()) {
                JavaFXPlayer player = players.get(i);
                String aliveStatus = player.isAlive() ? "VIVANT" : "MORT";
                infoLabels[i].setText(String.format("%s\nVies: %d\nBombes: %d\nPuissance: %d\nStatut: %s",
                        player.getName(), player.getLives(), player.getBombCount(),
                        player.getBombPower(), aliveStatus));
                infoLabels[i].setVisible(true);
            } else {
                infoLabels[i].setVisible(false);
            }
        }

        // Mise à jour du statut de jeu
        long aliveCount = players.stream().filter(JavaFXPlayer::isAlive).count();
        gameStatusLabel.setText("EN JEU (" + aliveCount + "/" + currentPlayerCount + " vivants)");
    }

    public void startGame() {
        currentState = GameState.PLAYING;
        gameStatusLabel.setText("EN JEU");

        // Initialiser le timer pour les power-ups
        lastPowerUpSpawn = System.nanoTime();

        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long currentTime) {
                if (currentTime - lastUpdate >= 16_000_000) { // ~60 FPS
                    updateGame();
                    lastUpdate = currentTime;
                }
            }
        };

        gameLoop.start();
        System.out.println("Boucle de jeu démarrée avec apparition aléatoire de power-ups");
    }

    private void updateGame() {
        // Gestion continue des mouvements basée sur les touches pressées
        processContinuousMovement();

        // Apparition aléatoire de power-ups pendant la partie
        handleRandomPowerUpSpawning();

        // Mise à jour des effets d'explosion
        updateExplosionEffects();

        // Vérification de la collecte des power-ups
        checkPowerUpCollection();

        // Mise à jour des joueurs
        for (JavaFXPlayer player : players) {
            player.update();
        }

        // Mise à jour des bombes
        bombs.removeIf(bomb -> {
            bomb.update();
            return bomb.isExploded();
        });

        // Vérification des conditions de victoire
        checkWinCondition();

        // Mise à jour des infos
        updatePlayerInfo();
    }

    private void processContinuousMovement() {
        long currentTime = System.nanoTime();

        for (JavaFXPlayer player : players) {
            if (!player.isAlive()) continue;

            // Vérifier le cooldown pour ce joueur
            Long lastMove = lastMoveTime.get(player);
            if (lastMove != null && (currentTime - lastMove) < MOVE_COOLDOWN) {
                continue; // Ce joueur est encore en cooldown
            }

            boolean playerMoved = false;

            // Vérifier les touches de mouvement pressées pour ce joueur
            if (pressedKeys.contains(player.getUpKey())) {
                movePlayer(player, Direction.UP);
                playerMoved = true;
            } else if (pressedKeys.contains(player.getDownKey())) {
                movePlayer(player, Direction.DOWN);
                playerMoved = true;
            } else if (pressedKeys.contains(player.getLeftKey())) {
                movePlayer(player, Direction.LEFT);
                playerMoved = true;
            } else if (pressedKeys.contains(player.getRightKey())) {
                movePlayer(player, Direction.RIGHT);
                playerMoved = true;
            }

            // Mettre à jour le temps du dernier mouvement
            if (playerMoved) {
                lastMoveTime.put(player, currentTime);
            }
        }
    }

    private void checkWinCondition() {
        long aliveCount = players.stream().filter(JavaFXPlayer::isAlive).count();

        if (aliveCount <= 1) {
            JavaFXPlayer winner = players.stream()
                    .filter(JavaFXPlayer::isAlive)
                    .findFirst()
                    .orElse(null);
            endGame(winner);
        }
    }

    private void endGame(JavaFXPlayer winner) {
        currentState = GameState.GAME_OVER;

        if (gameLoop != null) {
            gameLoop.stop();
        }

        String message = winner != null ? winner.getName() + " GAGNE !" : "MATCH NUL !";
        gameStatusLabel.setText(message);

        System.out.println("Fin de partie: " + message);

        // Retour au menu après 5 secondes
        Timeline returnToMenu = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {
                    System.out.println("Retour au menu automatique");
                    application.showMenu();
                })
        );
        returnToMenu.play();
    }

    public void handleKeyPressed(KeyEvent event) {
        if (currentState != GameState.PLAYING) return;

        KeyCode key = event.getCode();

        // Ajouter simplement la touche aux touches pressées
        pressedKeys.add(key);

        // Debug
        System.out.println("Touche pressée: " + key);

        // Gestion des touches spéciales
        if (key == KeyCode.ESCAPE) {
            pauseGame();
            return;
        }

        // Traitement immédiat pour chaque joueur (mouvement simultané possible)
        for (JavaFXPlayer player : players) {
            if (!player.isAlive()) continue;

            // Vérifier le cooldown pour ce joueur spécifique
            long currentTime = System.nanoTime();
            Long lastMove = lastMoveTime.get(player);
            if (lastMove != null && (currentTime - lastMove) < MOVE_COOLDOWN) {
                continue; // Ce joueur est encore en cooldown
            }

            boolean playerMoved = false;

            if (key == player.getUpKey()) {
                System.out.println(player.getName() + " - UP");
                movePlayer(player, Direction.UP);
                playerMoved = true;
            } else if (key == player.getDownKey()) {
                System.out.println(player.getName() + " - DOWN");
                movePlayer(player, Direction.DOWN);
                playerMoved = true;
            } else if (key == player.getLeftKey()) {
                System.out.println(player.getName() + " - LEFT");
                movePlayer(player, Direction.LEFT);
                playerMoved = true;
            } else if (key == player.getRightKey()) {
                System.out.println(player.getName() + " - RIGHT");
                movePlayer(player, Direction.RIGHT);
                playerMoved = true;
            } else if (key == player.getBombKey()) {
                System.out.println(player.getName() + " - BOMB");
                placeBomb(player);
                // Pas de cooldown pour les bombes
            }

            // Mettre à jour le temps du dernier mouvement pour ce joueur
            if (playerMoved) {
                lastMoveTime.put(player, currentTime);
            }
        }
    }

    public void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }

    private void movePlayer(JavaFXPlayer player, Direction direction) {
        int currentX = player.getGridX();
        int currentY = player.getGridY();
        int newX = currentX;
        int newY = currentY;

        // Calcul de la nouvelle position
        switch (direction) {
            case UP -> newY--;
            case DOWN -> newY++;
            case LEFT -> newX--;
            case RIGHT -> newX++;
        }

        // Vérification si le mouvement est valide
        if (isValidPosition(newX, newY) && !isOccupiedByPlayer(newX, newY, player)) {

            // Supprimer l'ancien node du joueur
            removePlayerFromPosition(player);

            // Restaurer la cellule de base à l'ancienne position
            restoreBaseCell(currentX, currentY);

            // Mettre à jour la position logique du joueur
            player.setGridPosition(newX, newY);
            player.move(direction);

            // Créer et ajouter le nouveau node
            StackPane newPlayerNode = player.createVisualRepresentation();
            newPlayerNode.getStyleClass().add("player-node");
            newPlayerNode.setUserData("player-" + player.getName());

            gameGrid.add(newPlayerNode, newX, newY);
            playerNodes.put(player, newPlayerNode);

            System.out.println(player.getName() + " bouge vers (" + newX + ", " + newY + ")");
        }
    }

    private void removePlayerFromPosition(JavaFXPlayer player) {
        Node playerNode = playerNodes.get(player);
        if (playerNode != null) {
            gameGrid.getChildren().remove(playerNode);
            playerNodes.remove(player);
        }
    }

    private void restoreBaseCell(int x, int y) {
        // Vérifier qu'il n'y a pas d'autres éléments importants à cette position
        boolean hasOtherElements = players.stream()
                .anyMatch(p -> p.getGridX() == x && p.getGridY() == y) ||
                bombs.stream()
                        .anyMatch(b -> b.getGridX() == x && b.getGridY() == y) ||
                powerUps.stream()
                        .anyMatch(p -> p.getGridX() == x && p.getGridY() == y && !p.isCollected());

        if (!hasOtherElements) {
            int[][] map = gameMap.getGameMap();
            if (y >= 0 && y < map.length && x >= 0 && x < map[0].length) {
                StackPane baseCell = createCellForType(map[y][x]);
                if (baseCell != null) {
                    gameGrid.add(baseCell, x, y);
                }
            }
        }
    }

    private boolean isValidPosition(int x, int y) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            return false;
        }

        int[][] map = gameMap.getGameMap();
        int cellType = map[y][x];
        return cellType == 0 || cellType == 3; // EMPTY ou SPAWN_ZONE
    }

    private boolean isOccupiedByPlayer(int x, int y, JavaFXPlayer currentPlayer) {
        return players.stream()
                .filter(p -> p != currentPlayer && p.isAlive())
                .anyMatch(p -> p.getGridX() == x && p.getGridY() == y);
    }

    private void placeBomb(JavaFXPlayer player) {
        if (!player.canPlaceBomb()) {
            System.out.println(player.getName() + " ne peut pas placer de bombe");
            return;
        }

        int bombX = player.getGridX();
        int bombY = player.getGridY();

        JavaFXBomb bomb = new JavaFXBomb(player, bombX, bombY, player.getBombPower());
        bombs.add(bomb);
        player.placeBomb();

        // Ajout visuel de la bombe
        StackPane bombNode = bomb.createVisualRepresentation();
        bombNode.getStyleClass().add("bomb-node");
        bombNode.setUserData("bomb-" + System.currentTimeMillis());

        gameGrid.add(bombNode, bombX, bombY);
        bombNodes.put(bomb, bombNode);

        // Démarrage du timer de la bombe
        bomb.startCountdown(() -> explodeBomb(bomb));

        System.out.println(player.getName() + " place une bombe en (" + bombX + ", " + bombY + ")");
    }

    private void explodeBomb(JavaFXBomb bomb) {
        System.out.println("Explosion de la bombe en (" + bomb.getGridX() + ", " + bomb.getGridY() + ")");

        // Supprimer la bombe de la liste ET de la grille AVANT de traiter les dégâts
        removeBombFromPosition(bomb);
        bombs.remove(bomb);

        // Création de l'explosion (qui va endommager les joueurs)
        createExplosion(bomb.getGridX(), bomb.getGridY(), bomb.getPower());

        // Le joueur peut replacer une bombe
        bomb.getOwner().bombExploded();
    }

    private void removeBombFromPosition(JavaFXBomb bomb) {
        Node bombNode = bombNodes.get(bomb);
        if (bombNode != null) {
            gameGrid.getChildren().remove(bombNode);
            bombNodes.remove(bomb);
        }
    }

    private void createExplosion(int centerX, int centerY, int power) {
        System.out.println("Création d'explosion au centre (" + centerX + ", " + centerY +
                ") avec puissance " + power);

        // Explosion au centre
        createExplosionEffect(centerX, centerY, true); // Centre = true
        damageAtPosition(centerX, centerY);

        // Explosion dans les 4 directions
        Direction[] directions = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};

        for (Direction dir : directions) {
            for (int i = 1; i <= power; i++) {
                int x = centerX;
                int y = centerY;

                switch (dir) {
                    case LEFT -> x -= i;
                    case RIGHT -> x += i;
                    case UP -> y -= i;
                    case DOWN -> y += i;
                }

                // Vérification des limites
                if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
                    break;
                }

                int[][] map = gameMap.getGameMap();
                int cellType = map[y][x];

                // Mur indestructible arrête l'explosion
                if (cellType == 1) {
                    break;
                }

                // Créer l'effet visuel d'explosion
                boolean isLastCell = (i == power); // Dernière cellule de cette direction
                createExplosionEffect(x, y, false, dir, isLastCell);

                // Dommages à cette position
                damageAtPosition(x, y);

                // Mur destructible arrête l'explosion après destruction
                if (cellType == 2) {
                    destroyWall(x, y);
                    break;
                }
            }
        }
    }

    private void damageAtPosition(int x, int y) {
        players.stream()
                .filter(p -> p.isAlive() && p.getGridX() == x && p.getGridY() == y)
                .forEach(player -> {
                    System.out.println(player.getName() + " subit des dégâts !");
                    damagePlayer(player);
                });
    }

    private void damagePlayer(JavaFXPlayer player) {
        // Supprimer le joueur de sa position actuelle
        removePlayerFromPosition(player);

        // Restaurer la cellule de base à l'ancienne position
        restoreBaseCell(player.getGridX(), player.getGridY());

        // Le joueur subit des dégâts
        player.takeDamage();

        if (!player.isAlive()) {
            System.out.println(player.getName() + " est éliminé !");
            // Ne pas remettre le joueur sur la grille s'il est mort
        } else {
            System.out.println(player.getName() + " perd une vie ! Retour au spawn.");
            // Remettre le joueur à son spawn
            respawnPlayer(player);
        }
    }

    private void respawnPlayer(JavaFXPlayer player) {
        // Récupérer la position de spawn du joueur
        int spawnX = player.getSpawnX();
        int spawnY = player.getSpawnY();

        System.out.println(player.getName() + " respawn à la position (" + spawnX + ", " + spawnY + ")");

        // Mettre à jour la position du joueur directement au spawn
        player.setGridPosition(spawnX, spawnY);

        // Vérifier si la position de spawn est libre (en excluant le joueur actuel)
        if (isPositionSafeForRespawn(spawnX, spawnY, player)) {
            // Créer et ajouter le nouveau node visuel au spawn exact
            StackPane newPlayerNode = player.createVisualRepresentation();
            newPlayerNode.getStyleClass().add("player-node");
            newPlayerNode.setUserData("player-" + player.getName());

            gameGrid.add(newPlayerNode, spawnX, spawnY);
            playerNodes.put(player, newPlayerNode);

            System.out.println("✅ " + player.getName() + " replacé au spawn exact (" + spawnX + ", " + spawnY + ")");
        } else {
            // Si le spawn n'est pas sûr, chercher une position proche
            System.out.println("⚠️ Spawn occupé pour " + player.getName() + ", recherche d'une position alternative...");
            respawnPlayerToSafePosition(player);
        }
    }

    private boolean isPositionSafeForRespawn(int x, int y, JavaFXPlayer playerToRespawn) {
        // Vérifier que la position est valide
        if (!isValidPosition(x, y)) {
            return false;
        }

        // Vérifier qu'il n'y a pas d'autre joueur (en excluant le joueur à respawn)
        boolean occupiedByOtherPlayer = players.stream()
                .filter(p -> p != playerToRespawn && p.isAlive())
                .anyMatch(p -> p.getGridX() == x && p.getGridY() == y);

        if (occupiedByOtherPlayer) {
            System.out.println("Position (" + x + ", " + y + ") occupée par un autre joueur");
            return false;
        }

        // Vérifier qu'il n'y a pas de bombe
        boolean hasBomb = bombs.stream()
                .anyMatch(bomb -> bomb.getGridX() == x && bomb.getGridY() == y);

        if (hasBomb) {
            System.out.println("Position (" + x + ", " + y + ") contient une bombe");
            return false;
        }

        System.out.println("Position (" + x + ", " + y + ") est sûre pour " + playerToRespawn.getName());
        return true;
    }

    private void respawnPlayerToSafePosition(JavaFXPlayer player) {
        int originalSpawnX = player.getSpawnX();
        int originalSpawnY = player.getSpawnY();

        // Essayer d'abord le spawn exact même s'il semble occupé (cas des bombes qui viennent d'exploser)
        System.out.println("🔍 Vérification alternative du spawn exact pour " + player.getName());

        // Double vérification : il se peut que la bombe ait été supprimée entre temps
        if (isPositionSafeForRespawn(originalSpawnX, originalSpawnY, player)) {
            // Finalement le spawn est libre !
            player.setGridPosition(originalSpawnX, originalSpawnY);

            StackPane newPlayerNode = player.createVisualRepresentation();
            newPlayerNode.getStyleClass().add("player-node");
            newPlayerNode.setUserData("player-" + player.getName());

            gameGrid.add(newPlayerNode, originalSpawnX, originalSpawnY);
            playerNodes.put(player, newPlayerNode);

            System.out.println("✅ " + player.getName() + " respawn finalement au spawn exact (" + originalSpawnX + ", " + originalSpawnY + ")");
            return;
        }

        // Chercher une position sûre autour du spawn original
        int[] dx = {1, -1, 0, 0, 1, 1, -1, -1}; // Positions à tester (sans 0,0 car déjà testé)
        int[] dy = {0, 0, 1, -1, 1, -1, 1, -1};

        for (int i = 0; i < dx.length; i++) {
            int testX = originalSpawnX + dx[i];
            int testY = originalSpawnY + dy[i];

            if (isPositionSafeForRespawn(testX, testY, player)) {
                // Position sûre trouvée
                player.setGridPosition(testX, testY);

                StackPane newPlayerNode = player.createVisualRepresentation();
                newPlayerNode.getStyleClass().add("player-node");
                newPlayerNode.setUserData("player-" + player.getName());

                gameGrid.add(newPlayerNode, testX, testY);
                playerNodes.put(player, newPlayerNode);

                System.out.println("⚠️ " + player.getName() + " respawn à une position proche (" + testX + ", " + testY + ")");
                return;
            }
        }

        // Si vraiment aucune position n'est trouvée, forcer le respawn au spawn (ignorer les obstacles)
        System.out.println("🚨 Respawn forcé au spawn original pour " + player.getName());
        forceRespawnAtSpawn(player);
    }

    private void forceRespawnAtSpawn(JavaFXPlayer player) {
        int spawnX = player.getSpawnX();
        int spawnY = player.getSpawnY();

        // Nettoyer complètement la position du spawn de tout élément
        cleanPositionCompletely(spawnX, spawnY);

        // Forcer le placement du joueur
        player.setGridPosition(spawnX, spawnY);

        StackPane newPlayerNode = player.createVisualRepresentation();
        newPlayerNode.getStyleClass().add("player-node");
        newPlayerNode.setUserData("player-" + player.getName());

        gameGrid.add(newPlayerNode, spawnX, spawnY);
        playerNodes.put(player, newPlayerNode);

        // Restaurer la cellule de base par dessus
        restoreBaseCell(spawnX, spawnY);

        System.out.println("🔧 " + player.getName() + " respawn forcé au spawn (" + spawnX + ", " + spawnY + ")");
    }

    private void cleanPositionCompletely(int x, int y) {
        // Supprimer tous les éléments à cette position
        gameGrid.getChildren().removeIf(node -> {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            int col = (colIndex != null) ? colIndex : 0;
            int row = (rowIndex != null) ? rowIndex : 0;
            return col == x && row == y;
        });
    }

    private void destroyWall(int x, int y) {
        System.out.println("Destruction du mur en (" + x + ", " + y + ")");

        // Mise à jour de la carte
        gameMap.setCell(x, y, 0); // EMPTY

        // Suppression visuelle de l'ancien mur et ajout de la cellule vide
        gameGrid.getChildren().removeIf(node -> {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            int col = (colIndex != null) ? colIndex : 0;
            int row = (rowIndex != null) ? rowIndex : 0;
            return col == x && row == y;
        });

        // Ajout de la nouvelle cellule vide
        StackPane emptyCell = gameMap.createRetroEmptyCell();
        gameGrid.add(emptyCell, x, y);
    }

    public void pauseGame() {
        if (currentState == GameState.PLAYING) {
            currentState = GameState.PAUSED;
            gameStatusLabel.setText("PAUSE");
            if (gameLoop != null) {
                gameLoop.stop();
            }
            System.out.println("Jeu mis en pause");
        } else if (currentState == GameState.PAUSED) {
            currentState = GameState.PLAYING;
            gameStatusLabel.setText("EN JEU");
            startGame();
            System.out.println("Jeu repris");
        }
    }

    private void handleBackToMenu() {
        System.out.println("Retour au menu demandé");
        cleanupGame();
        application.showMenu();
    }

    // ===== SYSTÈME D'EFFETS D'EXPLOSION =====

    private void loadExplosionSprites() {
        try {
            // Charger les sprites d'explosion depuis vos ressources
            // Ajustez ces chemins selon l'organisation de vos fichiers
            String flameStartPath = "/com/example/bombermansae201/Bombe/Debut_flamme.png";
            String flameEndPath = "/com/example/bombermansae201/Bombe/Fin_flamme.png";
            String flameCenterPath = "/com/example/bombermansae201/Bombe/Milieu_flamme.png"; // Optionnel

            flameStartImage = new javafx.scene.image.Image(getClass().getResourceAsStream(flameStartPath));
            flameEndImage = new javafx.scene.image.Image(getClass().getResourceAsStream(flameEndPath));

            // Si vous avez un sprite pour le centre, sinon on utilisera le sprite de début
            try {
                flameCenterImage = new javafx.scene.image.Image(getClass().getResourceAsStream(flameCenterPath));
            } catch (Exception e) {
                flameCenterImage = flameStartImage; // Utiliser le sprite de début comme centre
            }

            System.out.println("✅ Sprites d'explosion chargés avec succès");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des sprites d'explosion: " + e.getMessage());
            flameStartImage = null;
            flameEndImage = null;
            flameCenterImage = null;
        }
    }

    private static class ExplosionEffect {
        int x, y;
        long startTime;
        boolean isCenter;
        Direction direction;
        boolean isEnd;
        StackPane effectNode;

        ExplosionEffect(int x, int y, long startTime, boolean isCenter, Direction direction, boolean isEnd, StackPane effectNode) {
            this.x = x;
            this.y = y;
            this.startTime = startTime;
            this.isCenter = isCenter;
            this.direction = direction;
            this.isEnd = isEnd;
            this.effectNode = effectNode;
        }
    }

    private void createExplosionEffect(int x, int y, boolean isCenter) {
        createExplosionEffect(x, y, isCenter, null, false);
    }

    private void createExplosionEffect(int x, int y, boolean isCenter, Direction direction, boolean isEnd) {
        StackPane explosionNode = new StackPane();
        explosionNode.setPrefSize(40, 40);
        explosionNode.setAlignment(javafx.geometry.Pos.CENTER);

        if (flameStartImage != null && flameEndImage != null) {
            // Utiliser les sprites personnalisés
            createSpriteExplosionEffect(explosionNode, isCenter, direction, isEnd);
        } else {
            // Fallback vers les formes géométriques
            createGeometricExplosionEffect(explosionNode, isCenter, direction, isEnd);
        }

        // Ajouter à la grille
        gameGrid.add(explosionNode, x, y);

        // Enregistrer l'effet pour le supprimer plus tard
        ExplosionEffect effect = new ExplosionEffect(x, y, System.nanoTime(), isCenter, direction, isEnd, explosionNode);
        explosionEffects.add(effect);

        System.out.println("💥 Effet d'explosion créé en (" + x + ", " + y + ")" +
                (isCenter ? " [CENTRE]" : " [RAYON " + direction + (isEnd ? " - FIN]" : " - MILIEU]")));
    }

    private void createSpriteExplosionEffect(StackPane container, boolean isCenter, Direction direction, boolean isEnd) {
        javafx.scene.image.ImageView flameView;

        if (isCenter) {
            // Centre de l'explosion
            flameView = new javafx.scene.image.ImageView(flameCenterImage);
        } else if (isEnd) {
            // Fin de flamme
            flameView = new javafx.scene.image.ImageView(flameEndImage);
        } else {
            // Début/milieu de flamme
            flameView = new javafx.scene.image.ImageView(flameStartImage);
        }

        // Configurer la taille de l'image
        flameView.setFitWidth(35);
        flameView.setFitHeight(35);
        flameView.setPreserveRatio(true);
        flameView.setSmooth(true);

        // Rotation selon la direction
        if (direction != null) {
            switch (direction) {
                case UP -> flameView.setRotate(270);    // ↑
                case DOWN -> flameView.setRotate(90);   // ↓
                case LEFT -> flameView.setRotate(180);  // ←
                case RIGHT -> flameView.setRotate(0);   // → (pas de rotation)
            }
        }

        // Centrer l'image
        StackPane.setAlignment(flameView, javafx.geometry.Pos.CENTER);

        // Effets visuels
        javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow();
        glow.setLevel(isCenter ? 0.8 : 0.5); // Centre plus lumineux

        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setColor(javafx.scene.paint.Color.BLACK);
        shadow.setRadius(3);
        shadow.setOffsetX(1);
        shadow.setOffsetY(1);

        // Combiner les effets
        javafx.scene.effect.Blend blend = new javafx.scene.effect.Blend();
        blend.setTopInput(glow);
        blend.setBottomInput(shadow);
        flameView.setEffect(blend);

        container.getChildren().add(flameView);
    }

    private void createGeometricExplosionEffect(StackPane container, boolean isCenter, Direction direction, boolean isEnd) {
        // Fallback avec formes géométriques (version précédente)
        if (isCenter) {
            // Effet du centre - explosion principale
            Circle centerExplosion = new Circle(18);
            centerExplosion.setFill(Color.ORANGE);
            centerExplosion.setStroke(Color.RED);
            centerExplosion.setStrokeWidth(3);

            // Effet de lueur intense
            Glow glow = new Glow();
            glow.setLevel(0.9);
            centerExplosion.setEffect(glow);

            container.getChildren().add(centerExplosion);

        } else {
            // Effet des rayons d'explosion
            if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                // Rayon horizontal
                Rectangle ray = new Rectangle(40, 20);
                ray.setFill(Color.YELLOW);
                ray.setStroke(Color.ORANGE);
                ray.setStrokeWidth(2);

                if (isEnd) {
                    // Bout du rayon - plus petit
                    ray.setWidth(30);
                    ray.setFill(Color.LIGHTYELLOW);
                }

                container.getChildren().add(ray);

            } else {
                // Rayon vertical
                Rectangle ray = new Rectangle(20, 40);
                ray.setFill(Color.YELLOW);
                ray.setStroke(Color.ORANGE);
                ray.setStrokeWidth(2);

                if (isEnd) {
                    // Bout du rayon - plus petit
                    ray.setHeight(30);
                    ray.setFill(Color.LIGHTYELLOW);
                }

                container.getChildren().add(ray);
            }

            // Effet de brillance pour les rayons
            Glow rayGlow = new Glow();
            rayGlow.setLevel(0.6);
            container.setEffect(rayGlow);
        }
    }

    private void updateExplosionEffects() {
        long currentTime = System.nanoTime();

        // Supprimer les effets expirés
        explosionEffects.removeIf(effect -> {
            if (currentTime - effect.startTime >= EXPLOSION_EFFECT_DURATION) {
                // Supprimer l'effet visuel de la grille
                gameGrid.getChildren().remove(effect.effectNode);

                // Restaurer la cellule de base
                restoreBaseCell(effect.x, effect.y);

                return true; // Supprimer de la liste
            }
            return false; // Garder dans la liste
        });
    }
}