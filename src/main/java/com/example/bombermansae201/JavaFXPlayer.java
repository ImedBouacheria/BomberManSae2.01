package com.example.bombermansae201;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.effect.Glow;
import javafx.scene.effect.DropShadow;

public class JavaFXPlayer extends GameObject {

    // Informations du joueur
    private String name;
    private Color color;
    private boolean alive;
    private int lives;

    // Statistiques de jeu
    private int bombInventory;
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
    private static final int DEFAULT_BOMB_INVENTORY = 3;
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

        System.out.println("🎮 JavaFXPlayer créé: " + name + " (" + color + ", Mode: " + gameMode.getEmoji() + ")");
    }

    /**
     * Définit le mode de jeu pour les bombes
     */
    public void setGameMode(GameMode gameMode) {
        GameMode oldMode = this.gameMode;
        this.gameMode = gameMode;

        System.out.println("🔄 " + name + " - Mode changé: " + oldMode.getEmoji() + " → " + gameMode.getEmoji());

        // Ajustements lors du changement de mode
        if (gameMode.isLimited() && bombInventory == 0) {
            bombInventory = gameMode.getDefaultBombCount();
            System.out.println("🎁 " + name + " reçoit " + bombInventory + " bombes pour le mode limité");
        }
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

        System.out.println("⌨️ " + name + " - Touches: " + up + " " + down + " " + left + " " + right + " " + bomb);
    }

    /**
     * Définit la position de spawn du joueur
     */
    public void setSpawnPosition(int x, int y) {
        this.spawnX = x;
        this.spawnY = y;
        System.out.println("🏠 " + name + " - Spawn: (" + x + ", " + y + ")");
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
            System.out.println("💀 " + name + " - canPlaceBomb: false (joueur mort)");
            return false;
        }

        // En mode bombes infinies, on peut toujours placer une bombe
        if (gameMode.isInfinite()) {
            System.out.println("♾️ " + name + " - canPlaceBomb: true (Mode bombes infinies)");
            return true;
        }

        // En mode bombes limitées, vérifier l'inventaire
        boolean canPlace = bombInventory > 0;
        System.out.println((canPlace ? "✅" : "❌") + " " + name + " - canPlaceBomb: " + canPlace +
                " (Mode limité, inventaire: " + bombInventory + ")");
        return canPlace;
    }

    /**
     * Placement d'une bombe
     */
    public void placeBomb() {
        if (!alive) return;

        if (gameMode.isInfinite()) {
            System.out.println("♾️💣 " + name + " place une bombe (Mode bombes infinies)");
        } else {
            if (bombInventory > 0) {
                bombInventory--;
                System.out.println("🎯💣 " + name + " place une bombe (Inventaire: " + bombInventory + ")");
            } else {
                System.out.println("❌ " + name + " ne peut pas placer de bombe (Inventaire vide)");
            }
        }
    }

    /**
     * Ajouter des bombes à l'inventaire (power-up)
     */
    public void addBombs(int count) {
        if (gameMode.isLimited()) {
            int oldInventory = bombInventory;
            bombInventory += count;

            // Limite maximale pour éviter les abus
            if (bombInventory > 15) {
                bombInventory = 15;
            }

            System.out.println("💣+ " + name + " gagne " + count + " bombe(s) ! (" +
                    oldInventory + " → " + bombInventory + ")");
        } else {
            System.out.println("♾️💣+ " + name + " collecte un power-up bombes (Mode infini - effet cosmétique)");
        }
    }

    /**
     * Une bombe a explosé
     */
    public void bombExploded() {
        System.out.println("💥 Bombe de " + name + " a explosé");
    }

    /**
     * Le joueur subit des dégâts
     */
    public void takeDamage() {
        if (!alive) return;

        lives--;
        System.out.println("💔 " + name + " subit des dégâts ! Vies restantes: " + lives);

        if (lives <= 0) {
            alive = false;
            System.out.println("💀 " + name + " est éliminé !");
        }
    }

    /**
     * Guérison du joueur (power-up)
     */
    public void heal() {
        if (alive && lives < 9) {
            lives++;
            System.out.println("❤️ " + name + " gagne une vie ! Vies: " + lives);
        }
    }

    /**
     * Amélioration du nombre de bombes (power-up)
     */
    public void increaseBombCount() {
        addBombs(1);
    }

    /**
     * Amélioration de la puissance des bombes (power-up)
     */
    public void increaseBombPower() {
        if (bombPower < 8) {
            bombPower++;
            System.out.println("💥 " + name + " - Puissance des bombes: " + bombPower);
        }
    }

    /**
     * Amélioration de la vitesse (power-up)
     */
    public void increaseSpeed() {
        if (speed < 5) {
            speed++;
            System.out.println("⚡ " + name + " - Vitesse: " + speed);
        }
    }

    /**
     * Ajout de points
     */
    public void addScore(int points) {
        score += points;
        System.out.println("🏆 " + name + " gagne " + points + " points ! Total: " + score);
    }

    /**
     * Réinitialisation du joueur pour une nouvelle partie
     */
    public void reset() {
        this.alive = true;
        this.lives = DEFAULT_LIVES;
        this.bombInventory = gameMode.isLimited() ? gameMode.getDefaultBombCount() : 0;
        this.bombPower = DEFAULT_BOMB_POWER;
        this.speed = DEFAULT_SPEED;
        this.moving = false;
        this.currentDirection = Direction.DOWN;

        if (spawnX != 0 || spawnY != 0) {
            setGridPosition(spawnX, spawnY);
        }

        System.out.println("🔄 " + name + " réinitialisé (Mode: " + gameMode.getEmoji() + ")");
    }

    /**
     * Création de la représentation visuelle du joueur
     */
    public StackPane createVisualRepresentation() {
        StackPane playerNode = new StackPane();
        playerNode.setPrefSize(40, 40);
        playerNode.setMaxSize(40, 40);
        playerNode.setMinSize(40, 40);

        // Cercle principal avec effet selon le mode de jeu
        Circle playerCircle = new Circle(18);
        playerCircle.setFill(color);
        playerCircle.setStroke(Color.BLACK);
        playerCircle.setStrokeWidth(2);

        // Effet visuel selon le mode de jeu
        if (gameMode.isInfinite()) {
            Glow infiniteGlow = new Glow();
            infiniteGlow.setLevel(0.6);
            playerCircle.setEffect(infiniteGlow);
        } else {
            DropShadow normalShadow = new DropShadow();
            normalShadow.setColor(Color.GRAY);
            normalShadow.setRadius(3);
            normalShadow.setOffsetX(2);
            normalShadow.setOffsetY(2);
            playerCircle.setEffect(normalShadow);
        }

        // Numéro du joueur
        String playerNumber = getPlayerNumber();
        Text playerText = new Text(playerNumber);
        playerText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        playerText.setFill(Color.WHITE);

        // Indicateur de mode (petit symbole)
        Text modeIndicator = new Text(gameMode.getEmoji());
        modeIndicator.setFont(Font.font("Arial", FontWeight.BOLD, 8));
        modeIndicator.setTranslateX(12);
        modeIndicator.setTranslateY(-12);

        playerNode.getChildren().addAll(playerCircle, playerText, modeIndicator);
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
     * Méthode utilitaire pour l'affichage de l'inventaire dans l'UI
     */
    public String getBombInventoryDisplay() {
        if (gameMode.isInfinite()) {
            return "∞";
        } else {
            return String.valueOf(bombInventory);
        }
    }

    /**
     * Vérifie si le joueur peut recevoir un power-up
     */
    public boolean canReceivePowerUp(PowerUpType powerUpType) {
        if (!alive) return false;

        switch (powerUpType) {
            case BOMB_COUNT:
                return gameMode.isInfinite() || bombInventory < 15;
            case BOMB_POWER:
                return bombPower < 8;
            case SPEED:
                return speed < 5;
            case LIFE:
                return lives < 9;
            default:
                return true;
        }
    }

    /**
     * Application d'un power-up
     */
    public void applyPowerUp(PowerUpType powerUpType) {
        if (!canReceivePowerUp(powerUpType)) {
            System.out.println("❌ " + name + " ne peut pas recevoir le power-up " + powerUpType);
            return;
        }

        switch (powerUpType) {
            case BOMB_COUNT:
                increaseBombCount();
                break;
            case BOMB_POWER:
                increaseBombPower();
                break;
            case SPEED:
                increaseSpeed();
                break;
            case LIFE:
                heal();
                break;
        }

        addScore(100); // Bonus pour avoir récupéré un power-up
    }

    /**
     * Méthode de mise à jour (héritée de GameObject)
     */
    @Override
    public void update() {
        // Mise à jour du joueur si nécessaire
    }

    // ===== GETTERS =====

    public String getName() { return name; }
    public Color getColor() { return color; }
    public boolean isAlive() { return alive; }
    public int getLives() { return lives; }
    public int getBombInventory() { return bombInventory; }
    public int getBombPower() { return bombPower; }
    public int getSpeed() { return speed; }
    public int getScore() { return score; }
    public GameMode getGameMode() { return gameMode; }
    public int getGridX() { return x; }
    public int getGridY() { return y; }
    public int getSpawnX() { return spawnX; }
    public int getSpawnY() { return spawnY; }
    public KeyCode getUpKey() { return upKey; }
    public KeyCode getDownKey() { return downKey; }
    public KeyCode getLeftKey() { return leftKey; }
    public KeyCode getRightKey() { return rightKey; }
    public KeyCode getBombKey() { return bombKey; }
    public Direction getCurrentDirection() { return currentDirection; }
    public boolean isMoving() { return moving; }

    // ===== SETTERS =====

    public void setName(String name) { this.name = name; }
    public void setColor(Color color) { this.color = color; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public void setLives(int lives) {
        this.lives = lives;
        if (lives <= 0) this.alive = false;
    }
    public void setBombPower(int bombPower) { this.bombPower = bombPower; }
    public void setSpeed(int speed) { this.speed = speed; }
    public void setScore(int score) { this.score = score; }
    public void setBombInventory(int bombInventory) { this.bombInventory = bombInventory; }

    // ===== MÉTHODES UTILITAIRES =====

    /**
     * Représentation textuelle du joueur
     */
    @Override
    public String toString() {
        return String.format("JavaFXPlayer{name='%s', alive=%s, lives=%d, position=(%d,%d), " +
                        "bombes=%s, mode=%s}", name, alive, lives, x, y,
                getBombInventoryDisplay(), gameMode.getEmoji());
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
     * Retourne les statistiques du joueur sous forme de texte
     */
    public String getStatsText() {
        return String.format("%s\n❤️ Vies: %d\n%s Bombes: %s\n💥 Puissance: %d\n⚡ Vitesse: %d\n🏆 Score: %d\n%s",
                name, lives, gameMode.getEmoji(), getBombInventoryDisplay(),
                bombPower, speed, score, alive ? "✅ VIVANT" : "💀 MORT");
    }
}