package com.example.bombermansae201;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class JavaFXPlayer extends GameObject {

    // Informations du joueur
    private String name;
    private Color color;
    private boolean alive;
    private int lives;

    // Statistiques de jeu
    private int bombInventory;  // Nombre de bombes en inventaire
    private int bombPower;
    private int speed;
    private int score;

    // Mode de jeu pour les bombes
    private GameMode gameMode;

    // Position de spawn
    private int spawnX;
    private int spawnY;

    // Contrôles
    private KeyCode upKey;
    private KeyCode downKey;
    private KeyCode leftKey;
    private KeyCode rightKey;
    private KeyCode bombKey;

    // État du mouvement
    private Direction currentDirection;
    private boolean moving;

    // Constantes par défaut
    private static final int DEFAULT_LIVES = 3;
    private static final int DEFAULT_BOMB_INVENTORY = 3; // Commencer avec 3 bombes en inventaire
    private static final int DEFAULT_BOMB_POWER = 2;
    private static final int DEFAULT_SPEED = 1;

    /**
     * Constructeur principal
     */
    public JavaFXPlayer(String name, Color color) {
        super(0, 0);
        this.name = name;
        this.color = color;
        this.alive = true;
        this.lives = DEFAULT_LIVES;
        this.bombInventory = DEFAULT_BOMB_INVENTORY;
        this.bombPower = DEFAULT_BOMB_POWER;
        this.speed = DEFAULT_SPEED;
        this.score = 0;
        this.moving = false;
        this.currentDirection = Direction.DOWN;
        this.gameMode = GameMode.LIMITED_BOMBS; // Mode par défaut

        System.out.println("JavaFXPlayer créé: " + name + " (Couleur: " + color + ", Bombes: " + bombInventory + ")");
    }

    /**
     * Définit le mode de jeu pour les bombes
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        System.out.println(name + " - Mode de jeu défini: " + gameMode.getDisplayName());
    }

    /**
     * Configuration des touches de contrôle
     */
    public void setKeys(KeyCode up, KeyCode down, KeyCode left, KeyCode right, KeyCode bomb) {
        this.upKey = up;
        this.downKey = down;
        this.leftKey = left;
        this.rightKey = right;
        this.bombKey = bomb;

        System.out.println(name + " - Touches configurées: " +
                up + " " + down + " " + left + " " + right + " " + bomb);
    }

    /**
     * Définit la position de spawn du joueur
     */
    public void setSpawnPosition(int x, int y) {
        this.spawnX = x;
        this.spawnY = y;
        System.out.println(name + " - Position de spawn définie: (" + x + ", " + y + ")");
    }

    /**
     * Définit la position dans la grille
     */
    public void setGridPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Déplacement du joueur
     */
    public void move(Direction direction) {
        this.currentDirection = direction;
        this.moving = true;

        // Le mouvement réel est géré par le GameController
        // Cette méthode sert principalement à mettre à jour l'état du joueur
    }

    /**
     * Arrêt du mouvement
     */
    public void stopMoving() {
        this.moving = false;
    }

    /**
     * Vérification si le joueur peut placer une bombe
     */
    public boolean canPlaceBomb() {
        if (!alive) {
            System.out.println("❌ " + name + " - canPlaceBomb: false (joueur mort)");
            return false;
        }

        // En mode bombes infinies, on peut toujours placer une bombe
        if (gameMode == GameMode.INFINITE_BOMBS) {
            System.out.println("✅ " + name + " - canPlaceBomb: true (Mode bombes infinies)");
            return true;
        }

        // En mode bombes limitées, vérifier l'inventaire
        boolean canPlace = bombInventory > 0;
        System.out.println((canPlace ? "✅" : "❌") + " " + name + " - canPlaceBomb: " + canPlace +
                " (Mode bombes limitées, inventaire: " + bombInventory + ")");
        return canPlace;
    }

    /**
     * Placement d'une bombe (consomme une bombe de l'inventaire en mode limité)
     */
    public void placeBomb() {
        if (!alive) return;

        if (gameMode == GameMode.INFINITE_BOMBS) {
            System.out.println("💣 " + name + " place une bombe (Mode bombes infinies)");
            // En mode infini, on ne consomme pas de bombes de l'inventaire
        } else {
            // En mode limité, consommer une bombe de l'inventaire
            if (bombInventory > 0) {
                bombInventory--;
                System.out.println("💣 " + name + " place une bombe (Inventaire restant: " + bombInventory + ")");
            } else {
                System.out.println("❌ " + name + " ne peut pas placer de bombe (Inventaire: " + bombInventory + ")");
            }
        }
    }

    /**
     * Ajouter des bombes à l'inventaire (power-up)
     */
    public void addBombs(int count) {
        if (gameMode == GameMode.LIMITED_BOMBS) {
            bombInventory += count;
            System.out.println("💣+ " + name + " gagne " + count + " bombe(s) ! Inventaire: " + bombInventory);
        } else {
            System.out.println("💣+ " + name + " collecte un power-up bombes (Mode infini - pas d'effet sur l'inventaire)");
        }
    }

    /**
     * Une bombe a explosé (ne fait plus rien car on ne récupère pas les bombes)
     */
    public void bombExploded() {
        // Plus rien à faire - les bombes sont consommées définitivement
        System.out.println("💥 Bombe de " + name + " a explosé");
    }

    /**
     * Le joueur subit des dégâts
     */
    public void takeDamage() {
        if (!alive) return;

        lives--;
        System.out.println(name + " subit des dégâts ! Vies restantes: " + lives);

        if (lives <= 0) {
            alive = false;
            System.out.println(name + " est éliminé !");
        }
    }

    /**
     * Guérison du joueur (power-up)
     */
    public void heal() {
        if (alive) {
            lives++;
            System.out.println(name + " gagne une vie ! Vies: " + lives);
        }
    }

    /**
     * Amélioration du nombre de bombes (power-up)
     */
    public void increaseBombCount() {
        addBombs(1); // Ajouter 1 bombe à l'inventaire (ou rien en mode infini)
        if (gameMode == GameMode.LIMITED_BOMBS) {
            System.out.println("💣 " + name + " reçoit une bombe supplémentaire !");
        } else {
            System.out.println("💣 " + name + " collecte un power-up bombe (Mode infini - effet cosmétique)");
        }
    }

    /**
     * Amélioration de la puissance des bombes (power-up)
     */
    public void increaseBombPower() {
        bombPower++;
        System.out.println(name + " - Puissance des bombes augmentée à " + bombPower + " !");
    }

    /**
     * Amélioration de la vitesse (power-up)
     */
    public void increaseSpeed() {
        speed++;
        System.out.println(name + " - Vitesse augmentée à " + speed + " !");
    }

    /**
     * Ajout de points
     */
    public void addScore(int points) {
        score += points;
        System.out.println(name + " gagne " + points + " points ! Score total: " + score);
    }

    /**
     * Réinitialisation du joueur pour une nouvelle partie
     */
    public void reset() {
        this.alive = true;
        this.lives = DEFAULT_LIVES;
        this.bombInventory = DEFAULT_BOMB_INVENTORY;
        this.bombPower = DEFAULT_BOMB_POWER;
        this.speed = DEFAULT_SPEED;
        this.moving = false;
        this.currentDirection = Direction.DOWN;

        // Remettre le joueur à son spawn
        if (spawnX != 0 || spawnY != 0) {
            setGridPosition(spawnX, spawnY);
        }

        System.out.println(name + " réinitialisé pour une nouvelle partie (Mode: " + gameMode.getDisplayName() + ")");
    }

    /**
     * Création de la représentation visuelle du joueur
     */
    public StackPane createVisualRepresentation() {
        StackPane playerNode = new StackPane();
        playerNode.setPrefSize(40, 40);
        playerNode.setMaxSize(40, 40);
        playerNode.setMinSize(40, 40);

        // Cercle principal
        Circle playerCircle = new Circle(18);
        playerCircle.setFill(color);
        playerCircle.setStroke(Color.BLACK);
        playerCircle.setStrokeWidth(2);

        // Numéro du joueur (basé sur la couleur)
        String playerNumber = getPlayerNumber();
        Text playerText = new Text(playerNumber);
        playerText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        playerText.setFill(Color.WHITE);

        // Assemblage
        playerNode.getChildren().addAll(playerCircle, playerText);

        // Ajouter des classes CSS pour identification
        playerNode.getStyleClass().add("player-node");
        playerNode.setUserData("player-" + name);

        return playerNode;
    }

    /**
     * Obtient le numéro du joueur basé sur sa couleur
     */
    private String getPlayerNumber() {
        if (color.equals(Color.RED)) return "1";
        if (color.equals(Color.BLUE)) return "2";
        if (color.equals(Color.GREEN)) return "3";
        if (color.equals(Color.YELLOW)) return "4";
        return "?";
    }

    /**
     * Méthode de mise à jour (héritée de GameObject)
     */
    @Override
    public void update() {
        // Mise à jour du joueur (animations, effets, etc.)
        // Pour l'instant, rien de spécial à faire
    }

    // ===== GETTERS =====

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getLives() {
        return lives;
    }

    public int getBombCount() {
        // Afficher "∞" pour le mode infini ou l'inventaire pour le mode limité
        if (gameMode == GameMode.INFINITE_BOMBS) {
            return Integer.MAX_VALUE; // Sera géré dans l'affichage UI
        }
        return bombInventory;
    }

    public int getBombInventory() {
        return bombInventory;
    }

    public int getBombPower() {
        return bombPower;
    }

    public int getSpeed() {
        return speed;
    }

    public int getScore() {
        return score;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public int getGridX() {
        return x;
    }

    public int getGridY() {
        return y;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public KeyCode getUpKey() {
        return upKey;
    }

    public KeyCode getDownKey() {
        return downKey;
    }

    public KeyCode getLeftKey() {
        return leftKey;
    }

    public KeyCode getRightKey() {
        return rightKey;
    }

    public KeyCode getBombKey() {
        return bombKey;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public boolean isMoving() {
        return moving;
    }

    // ===== SETTERS =====

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setLives(int lives) {
        this.lives = lives;
        if (lives <= 0) {
            this.alive = false;
        }
    }

    public void setBombPower(int bombPower) {
        this.bombPower = bombPower;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setBombInventory(int bombInventory) {
        this.bombInventory = bombInventory;
    }

    // ===== MÉTHODES UTILITAIRES =====

    /**
     * Représentation textuelle du joueur
     */
    @Override
    public String toString() {
        String bombDisplay = (gameMode == GameMode.INFINITE_BOMBS) ? "∞" : String.valueOf(bombInventory);
        return String.format("JavaFXPlayer{name='%s', alive=%s, lives=%d, position=(%d,%d), spawn=(%d,%d), bombes=%s, mode=%s}",
                name, alive, lives, x, y, spawnX, spawnY, bombDisplay, gameMode.getDisplayName());
    }

    /**
     * Vérifie si le joueur est à une position donnée
     */
    public boolean isAtPosition(int x, int y) {
        return this.x == x && this.y == y;
    }

    /**
     * Vérifie si le joueur est à sa position de spawn
     */
    public boolean isAtSpawn() {
        return x == spawnX && y == spawnY;
    }

    /**
     * Calcule la distance jusqu'à une position
     */
    public double distanceTo(int targetX, int targetY) {
        return Math.sqrt(Math.pow(x - targetX, 2) + Math.pow(y - targetY, 2));
    }

    /**
     * Vérifie si le joueur peut recevoir un power-up
     */
    public boolean canReceivePowerUp(PowerUpType powerUpType) {
        if (!alive) return false;

        switch (powerUpType) {
            case BOMB_COUNT:
                // En mode bombes infinies, on peut toujours collecter (effet cosmétique)
                // En mode bombes limitées, limite d'inventaire
                return gameMode == GameMode.INFINITE_BOMBS || bombInventory < 15;
            case BOMB_POWER:
                return bombPower < 8; // Limite arbitraire
            case SPEED:
                return speed < 5; // Limite arbitraire
            case LIFE:
                return lives < 9; // Limite arbitraire
            default:
                return true;
        }
    }

    /**
     * Application d'un power-up
     */
    public void applyPowerUp(PowerUpType powerUpType) {
        if (!canReceivePowerUp(powerUpType)) {
            System.out.println(name + " ne peut pas recevoir le power-up " + powerUpType);
            return;
        }

        switch (powerUpType) {
            case BOMB_COUNT:
                increaseBombCount();
                if (gameMode == GameMode.LIMITED_BOMBS) {
                    System.out.println("💣 " + name + " - Inventaire de bombes: " + bombInventory);
                } else {
                    System.out.println("💣 " + name + " - Bombes infinies (collecte cosmétique)");
                }
                break;
            case BOMB_POWER:
                increaseBombPower();
                System.out.println("💥 " + name + " - Puissance des bombes: " + bombPower);
                break;
            case SPEED:
                increaseSpeed();
                System.out.println("⚡ " + name + " - Vitesse augmentée: " + speed);
                break;
            case LIFE:
                heal();
                System.out.println("❤️ " + name + " - Vies: " + lives);
                break;
        }

        addScore(100); // Bonus pour avoir récupéré un power-up
    }
}