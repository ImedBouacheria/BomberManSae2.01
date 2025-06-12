package Joueur;

import Etat.Direction;
import Etat.GameMode;
import Etat.PowerUpType;
import fonctionnaliteInitial.GameObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.effect.Glow;
import javafx.scene.effect.DropShadow;

/**
 * Classe représentant un joueur dans le jeu Bomberman.
 * <p>
 * Cette classe gère tous les aspects d'un joueur dans le jeu, notamment :
 * <ul>
 *   <li>Les informations personnelles (nom, couleur)</li>
 *   <li>Les statistiques de jeu (vies, bombes, puissance, vitesse, score)</li>
 *   <li>Le mode de jeu pour les bombes (limité ou infini)</li>
 *   <li>La position et le spawn du joueur</li>
 *   <li>Les contrôles clavier</li>
 *   <li>La représentation visuelle et l'animation</li>
 *   <li>La gestion des power-ups et des dégâts</li>
 * </ul>
 * <p>
 * La classe utilise des sprites pour l'affichage du joueur avec des animations
 * de marche dans les quatre directions. Si les sprites ne sont pas disponibles,
 * un système de fallback utilisant des formes géométriques est implémenté.
 * 
 * @author [Auteur]
 * @version 1.0
 */
public class JavaFXPlayer extends GameObject {

    /**
     * Nom du joueur.
     */
    private String name;
    
    /**
     * Couleur du joueur, utilisée pour l'identification visuelle.
     */
    private Color color;
    
    /**
     * État de vie du joueur (true = vivant, false = mort).
     */
    private boolean alive;
    
    /**
     * Nombre de vies restantes du joueur.
     */
    private int lives;

    /**
     * Nombre de bombes dans l'inventaire du joueur.
     * Pertinent uniquement en mode de jeu limité.
     */
    private int bombInventory;
    
    /**
     * Puissance des bombes, détermine la portée de l'explosion.
     */
    private int bombPower;
    
    /**
     * Vitesse de déplacement du joueur.
     */
    private int speed;
    
    /**
     * Score accumulé par le joueur.
     */
    private int score;

    /**
     * Mode de jeu pour les bombes (limité ou infini).
     * @see Etat.GameMode
     */
    private GameMode gameMode;

    /**
     * Coordonnée X de la position de spawn du joueur.
     */
    private int spawnX;
    
    /**
     * Coordonnée Y de la position de spawn du joueur.
     */
    private int spawnY;

    /**
     * Touche pour se déplacer vers le haut.
     */
    private KeyCode upKey;
    
    /**
     * Touche pour se déplacer vers le bas.
     */
    private KeyCode downKey;
    
    /**
     * Touche pour se déplacer vers la gauche.
     */
    private KeyCode leftKey;
    
    /**
     * Touche pour se déplacer vers la droite.
     */
    private KeyCode rightKey;
    
    /**
     * Touche pour placer une bombe.
     */
    private KeyCode bombKey;

    /**
     * Direction actuelle du joueur.
     * @see Etat.Direction
     */
    private Direction currentDirection;
    
    /**
     * Indique si le joueur est en mouvement.
     */
    private boolean moving;

    /**
     * Nombre de vies par défaut pour un nouveau joueur.
     */
    private static final int DEFAULT_LIVES = 3;
    
    /**
     * Nombre de bombes par défaut dans l'inventaire.
     */
    private static final int DEFAULT_BOMB_INVENTORY = 10;
    
    /**
     * Puissance de bombe par défaut.
     */
    private static final int DEFAULT_BOMB_POWER = 2;
    
    /**
     * Vitesse par défaut.
     */
    private static final int DEFAULT_SPEED = 1;

    /**
     * Tableau contenant les images des sprites du joueur.
     */
    private transient Image[] sprites;
    
    /**
     * Sprite actuellement affiché.
     */
    private transient ImageView currentSprite;
    
    /**
     * Indique si le joueur est en train de marcher (pour l'animation).
     */
    private boolean walking;
    
    /**
     * Timestamp de la dernière mise à jour de sprite.
     */
    private long lastSpriteUpdate;
    
    /**
     * Intervalle entre les changements de sprites pendant l'animation (en nanosecondes).
     */
    private static final long SPRITE_UPDATE_INTERVAL = 200_000_000; // 200ms

    /**
     * Noms des fichiers de sprites pour les différentes animations.
     */
    private static final String[] SPRITE_NAMES = {
            "PersoBleu.png",             // 0 - face
            "PersoBleuMarcheDevant.png", // 1 - face_walking
            "PersoBleuDos.png",          // 2 - back
            "PersoBleuMarcheDeriere.png",// 3 - back_walking
            "PersoBleuDroite.png",       // 4 - right
            "PersoBleuMarcheDroite.png", // 5 - right_walking
            "PersoBleuGauche.png",       // 6 - left
            "PersoBleuMarcheGauche.png"  // 7 - left_walking
    };

    /**
     * Constructeur principal du joueur.
     * Initialise un nouveau joueur avec des valeurs par défaut et charge les sprites.
     *
     * @param name  Nom du joueur
     * @param color Couleur du joueur
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
        this.gameMode = GameMode.LIMITED_BOMBS;

        loadSprites(); // Charger les sprites

        System.out.println("🎮 JavaFXPlayer créé: " + name + " (" + color + ", Mode: " + gameMode.getEmoji() + ")");
    }

    /**
     * Charge les sprites du joueur depuis les ressources.
     * <p>
     * Cette méthode essaie de charger les sprites à partir de différents chemins possibles
     * et implémente un système de fallback en cas d'échec.
     */
    private void loadSprites() {
        sprites = new Image[8];
        String colorFolder = getColorFolder();

        System.out.println("🎨 Chargement des sprites pour " + name + " (couleur: " + colorFolder + ")");

        boolean allSpritesLoaded = true;

        for (int i = 0; i < SPRITE_NAMES.length; i++) {
            try {
                // Essayer différents chemins possibles
                String[] possiblePaths = {
                        "/com/example/bombermansae201/Personnage/" + colorFolder + "/" + SPRITE_NAMES[i],
                        "/com/example/bombermansae201/Personnage/" + colorFolder + ".png/" + SPRITE_NAMES[i],
                        "/Personnage/" + colorFolder + "/" + SPRITE_NAMES[i],
                        "/sprites/" + colorFolder + "/" + SPRITE_NAMES[i]
                };

                Image loadedImage = null;
                String usedPath = null;

                for (String path : possiblePaths) {
                    try {
                        var stream = getClass().getResourceAsStream(path);
                        if (stream != null) {
                            loadedImage = new Image(stream);
                            usedPath = path;
                            break;
                        }
                    } catch (Exception e) {
                        // Essayer le chemin suivant
                        continue;
                    }
                }

                if (loadedImage != null && !loadedImage.isError()) {
                    sprites[i] = loadedImage;
                    System.out.println("✅ Sprite " + i + " chargé: " + usedPath);
                } else {
                    System.err.println("❌ Impossible de charger le sprite " + i + ": " + SPRITE_NAMES[i]);
                    allSpritesLoaded = false;
                }

            } catch (Exception e) {
                System.err.println("❌ Erreur lors du chargement du sprite " + i + ": " + e.getMessage());
                allSpritesLoaded = false;
            }
        }

        if (allSpritesLoaded) {
            currentSprite = new ImageView(sprites[0]); // Face par défaut
            System.out.println("✅ Tous les sprites chargés pour " + name);
        } else {
            System.err.println("⚠️ Certains sprites n'ont pas pu être chargés pour " + name + ", utilisation du fallback");
            sprites = null;
            currentSprite = null;
        }
    }

    /**
     * Détermine le dossier de sprites à utiliser en fonction de la couleur du joueur.
     *
     * @return Nom du dossier correspondant à la couleur
     */
    private String getColorFolder() {
        if (color.equals(Color.PINK)) return "Rose";
        else if (color.equals(Color.BLUE)) return "Bleu";
        else if (color.equals(Color.GREEN)) return "Vert";
        else if (color.equals(Color.ORANGE)) return "Orange";
        else if (color.equals(Color.RED)) return "Rouge";
        else if (color.equals(Color.YELLOW)) return "Jaune";
        else return "Bleu"; // Fallback
    }

    /**
     * Met à jour le sprite affiché en fonction de la direction et de l'état de mouvement.
     * Gère l'animation de marche en alternant les sprites.
     */
    private void updateSprite() {
        if (sprites == null || currentSprite == null) return;

        long now = System.nanoTime();
        if (moving && (now - lastSpriteUpdate > SPRITE_UPDATE_INTERVAL)) {
            // Alterner entre sprite normal et sprite de marche
            walking = !walking;
            lastSpriteUpdate = now;
        } else if (!moving && walking) {
            // Si le joueur ne bouge plus, revenir au sprite statique
            walking = false;
            lastSpriteUpdate = now;
        }

        int spriteIndex = 0;
        switch (currentDirection) {
            case UP:
                spriteIndex = walking ? 3 : 2; // back_walking ou back
                break;
            case DOWN:
                spriteIndex = walking ? 1 : 0; // face_walking ou face
                break;
            case LEFT:
                spriteIndex = walking ? 7 : 6; // left_walking ou left
                break;
            case RIGHT:
                spriteIndex = walking ? 5 : 4; // right_walking ou right
                break;
        }

        if (spriteIndex < sprites.length && sprites[spriteIndex] != null) {
            currentSprite.setImage(sprites[spriteIndex]);
        }
    }

    /**
     * Crée une représentation visuelle du joueur pour l'affichage dans l'interface.
     * <p>
     * Si les sprites sont disponibles, utilise les images. Sinon, crée une représentation
     * alternative avec des formes géométriques.
     *
     * @return Un StackPane contenant la représentation visuelle du joueur
     */
    public StackPane createVisualRepresentation() {
        StackPane playerNode = new StackPane();
        playerNode.setPrefSize(40, 40);
        playerNode.setMaxSize(40, 40);
        playerNode.setMinSize(40, 40);

        if (sprites == null || currentSprite == null) {
            System.out.println("⚠️ Utilisation du fallback pour " + name);
            // Fallback aux formes géométriques si les sprites ne sont pas chargés
            Circle playerCircle = new Circle(18);
            playerCircle.setFill(color);
            playerCircle.setStroke(Color.BLACK);
            playerCircle.setStrokeWidth(2);

            if (gameMode.isInfinite()) {
                Glow infiniteGlow = new Glow();
                infiniteGlow.setLevel(0.6);
                playerCircle.setEffect(infiniteGlow);
            }

            Text playerText = new Text(getPlayerNumber());
            playerText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            playerText.setFill(Color.WHITE);

            playerNode.getChildren().addAll(playerCircle, playerText);
        } else {
            System.out.println("✅ Utilisation des sprites pour " + name);
            updateSprite();

            ImageView spriteView = new ImageView();
            spriteView.setImage(currentSprite.getImage());
            spriteView.setFitWidth(40);
            spriteView.setFitHeight(40);
            spriteView.setPreserveRatio(true);
            spriteView.setSmooth(true); // Améliore la qualité de rendu

            // Ajouter un effet d'ombre
            DropShadow shadow = new DropShadow();
            shadow.setRadius(3);
            shadow.setOffsetX(2);
            shadow.setOffsetY(2);
            spriteView.setEffect(shadow);

            playerNode.getChildren().add(spriteView);
        }

        // Indicateur de mode (petit symbole)
        Text modeIndicator = new Text(gameMode.getEmoji());
        modeIndicator.setFont(Font.font("Arial", FontWeight.BOLD, 8));
        modeIndicator.setTranslateX(12);
        modeIndicator.setTranslateY(-12);
        playerNode.getChildren().add(modeIndicator);

        playerNode.getStyleClass().add("player-node");
        playerNode.setUserData("player-" + name);

        return playerNode;
    }

    /**
     * Définit le mode de jeu pour les bombes.
     * <p>
     * Ajuste automatiquement l'inventaire de bombes en fonction du mode.
     *
     * @param gameMode Le nouveau mode de jeu
     * @see Etat.GameMode
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
     * Configure les touches de contrôle du joueur.
     *
     * @param up    Touche pour monter
     * @param down  Touche pour descendre
     * @param left  Touche pour aller à gauche
     * @param right Touche pour aller à droite
     * @param bomb  Touche pour poser une bombe
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
     * Définit la position de spawn du joueur.
     * C'est la position où le joueur apparaît au début et après avoir perdu une vie.
     *
     * @param x Coordonnée X du spawn
     * @param y Coordonnée Y du spawn
     */
    public void setSpawnPosition(int x, int y) {
        this.spawnX = x;
        this.spawnY = y;
        System.out.println("🏠 " + name + " - Spawn: (" + x + ", " + y + ")");
    }

    /**
     * Définit la position actuelle du joueur dans la grille de jeu.
     *
     * @param x Coordonnée X dans la grille
     * @param y Coordonnée Y dans la grille
     */
    public void setGridPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Déplace le joueur dans la direction spécifiée.
     * Met à jour la direction et l'état de mouvement, ainsi que le sprite.
     *
     * @param direction Direction du mouvement
     * @see Etat.Direction
     */
    public void move(Direction direction) {
        this.currentDirection = direction;
        this.moving = true;
        this.walking = true;
        this.lastSpriteUpdate = System.nanoTime();
        updateSprite(); // Mettre à jour immédiatement le sprite
    }

    /**
     * Arrête le mouvement du joueur.
     * Met à jour l'état de mouvement et le sprite.
     */
    public void stopMoving() {
        this.moving = false;
        this.walking = false;
        updateSprite(); // Mettre à jour pour afficher le sprite statique
    }

    /**
     * Vérifie si le joueur peut placer une bombe.
     * <p>
     * En mode limité, vérifie l'inventaire de bombes.
     * En mode infini, retourne toujours true si le joueur est vivant.
     *
     * @return true si le joueur peut placer une bombe, false sinon
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
     * Place une bombe à la position actuelle du joueur.
     * <p>
     * En mode limité, réduit l'inventaire de bombes.
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
     * Ajoute des bombes à l'inventaire du joueur.
     * <p>
     * En mode limité, augmente l'inventaire jusqu'à un maximum de 15.
     * En mode infini, cette méthode n'a qu'un effet cosmétique.
     *
     * @param count Nombre de bombes à ajouter
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
     * Méthode appelée lorsqu'une bombe du joueur explose.
     */
    public void bombExploded() {
        System.out.println("💥 Bombe de " + name + " a explosé");
    }

    /**
     * Inflige des dégâts au joueur.
     * <p>
     * Réduit le nombre de vies et vérifie si le joueur est éliminé.
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
     * Soigne le joueur en lui ajoutant une vie.
     * <p>
     * Le nombre maximum de vies est limité à 9.
     */
    public void heal() {
        if (alive && lives < 9) {
            lives++;
            System.out.println("❤️ " + name + " gagne une vie ! Vies: " + lives);
        }
    }

    /**
     * Augmente le nombre de bombes disponibles (power-up).
     * <p>
     * Appelle addBombs(1).
     */
    public void increaseBombCount() {
        addBombs(1);
    }

    /**
     * Augmente la puissance des bombes (power-up).
     * <p>
     * La puissance maximale est limitée à 8.
     */
    public void increaseBombPower() {
        if (bombPower < 8) {
            bombPower++;
            System.out.println("💥 " + name + " - Puissance des bombes: " + bombPower);
        }
    }

    /**
     * Augmente la vitesse du joueur (power-up).
     * <p>
     * La vitesse maximale est limitée à 5.
     */
    public void increaseSpeed() {
        if (speed < 5) {
            speed++;
            System.out.println("⚡ " + name + " - Vitesse: " + speed);
        }
    }

    /**
     * Ajoute des points au score du joueur.
     *
     * @param points Nombre de points à ajouter
     */
    public void addScore(int points) {
        score += points;
        System.out.println("🏆 " + name + " gagne " + points + " points ! Total: " + score);
    }

    /**
     * Réinitialise le joueur pour une nouvelle partie.
     * <p>
     * Restaure les vies, la position de spawn, et les statistiques par défaut.
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
     * Obtient le numéro du joueur basé sur sa couleur.
     *
     * @return Numéro du joueur sous forme de chaîne
     */
    private String getPlayerNumber() {
        if (color.equals(Color.RED)) return "1";
        if (color.equals(Color.BLUE)) return "2";
        if (color.equals(Color.GREEN)) return "3";
        if (color.equals(Color.YELLOW)) return "4";
        return "?";
    }

    /**
     * Méthode utilitaire pour l'affichage de l'inventaire de bombes dans l'UI.
     * <p>
     * En mode infini, renvoie "∞". En mode limité, renvoie le nombre de bombes.
     *
     * @return Représentation textuelle de l'inventaire de bombes
     */
    public String getBombInventoryDisplay() {
        if (gameMode.isInfinite()) {
            return "∞";
        } else {
            return String.valueOf(bombInventory);
        }
    }

    /**
     * Vérifie si le joueur peut recevoir un power-up spécifique.
     * <p>
     * Prend en compte l'état actuel du joueur et les limites des statistiques.
     *
     * @param powerUpType Type de power-up à vérifier
     * @return true si le joueur peut recevoir le power-up, false sinon
     * @see Etat.PowerUpType
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
     * Applique un power-up au joueur.
     * <p>
     * Vérifie d'abord si le joueur peut recevoir le power-up, puis l'applique
     * et ajoute un bonus de score.
     *
     * @param powerUpType Type de power-up à appliquer
     * @see Etat.PowerUpType
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
     * Méthode de mise à jour appelée à chaque frame du jeu.
     * <p>
     * Héritée de GameObject, peut être utilisée pour implémenter des comportements
     * spécifiques au joueur qui doivent être mis à jour régulièrement.
     */
    @Override
    public void update() {
        // Mise à jour du joueur si nécessaire
    }

    // ===== GETTERS =====

    /**
     * @return Nom du joueur
     */
    public String getName() { return name; }
    
    /**
     * @return Couleur du joueur
     */
    public Color getColor() { return color; }
    
    /**
     * @return true si le joueur est vivant, false sinon
     */
    public boolean isAlive() { return alive; }
    
    /**
     * @return Nombre de vies restantes
     */
    public int getLives() { return lives; }
    
    /**
     * @return Nombre de bombes dans l'inventaire
     */
    public int getBombInventory() { return bombInventory; }
    
    /**
     * @return Puissance des bombes
     */
    public int getBombPower() { return bombPower; }
    
    /**
     * @return Vitesse du joueur
     */
    public int getSpeed() { return speed; }
    
    /**
     * @return Score du joueur
     */
    public int getScore() { return score; }
    
    /**
     * @return Mode de jeu actuel pour les bombes
     */
    public GameMode getGameMode() { return gameMode; }
    
    /**
     * @return Coordonnée X dans la grille
     */
    public int getGridX() { return x; }
    
    /**
     * @return Coordonnée Y dans la grille
     */
    public int getGridY() { return y; }
    
    /**
     * @return Coordonnée X du spawn
     */
    public int getSpawnX() { return spawnX; }
    
    /**
     * @return Coordonnée Y du spawn
     */
    public int getSpawnY() { return spawnY; }
    
    /**
     * @return Touche pour monter
     */
    public KeyCode getUpKey() { return upKey; }
    
    /**
     * @return Touche pour descendre
     */
    public KeyCode getDownKey() { return downKey; }
    
    /**
     * @return Touche pour aller à gauche
     */
    public KeyCode getLeftKey() { return leftKey; }
    
    /**
     * @return Touche pour aller à droite
     */
    public KeyCode getRightKey() { return rightKey; }
    
    /**
     * @return Touche pour poser une bombe
     */
    public KeyCode getBombKey() { return bombKey; }
    
    /**
     * @return Direction actuelle du joueur
     */
    public Direction getCurrentDirection() { return currentDirection; }
    
    /**
     * @return true si le joueur est en mouvement, false sinon
     */
    public boolean isMoving() { return moving; }

    // ===== SETTERS =====

    /**
     * Définit le nom du joueur.
     * @param name Nouveau nom
     */
    public void setName(String name) { this.name = name; }
    
    /**
     * Définit la couleur du joueur.
     * @param color Nouvelle couleur
     */
    public void setColor(Color color) { this.color = color; }
    
    /**
     * Définit l'état de vie du joueur.
     * @param alive Nouvel état (true = vivant, false = mort)
     */
    public void setAlive(boolean alive) { this.alive = alive; }
    
    /**
     * Définit le nombre de vies du joueur.
     * <p>
     * Si le nombre de vies est inférieur ou égal à zéro, le joueur est marqué comme mort.
     * 
     * @param lives Nouveau nombre de vies
     */
    public void setLives(int lives) {
        this.lives = lives;
        if (lives <= 0) this.alive = false;
    }
    
    /**
     * Définit la puissance des bombes.
     * @param bombPower Nouvelle puissance
     */
    public void setBombPower(int bombPower) { this.bombPower = bombPower; }
    
    /**
     * Définit la vitesse du joueur.
     * @param speed Nouvelle vitesse
     */
    public void setSpeed(int speed) { this.speed = speed; }
    
    /**
     * Définit le score du joueur.
     * @param score Nouveau score
     */
    public void setScore(int score) { this.score = score; }
    
    /**
     * Définit le nombre de bombes dans l'inventaire.
     * @param bombInventory Nouveau nombre de bombes
     */
    public void setBombInventory(int bombInventory) { this.bombInventory = bombInventory; }

    // ===== MÉTHODES UTILITAIRES =====

    /**
     * Retourne une représentation textuelle du joueur.
     * 
     * @return Chaîne formatée contenant les informations principales du joueur
     */
    @Override
    public String toString() {
        return String.format("JavaFXPlayer{name='%s', alive=%s, lives=%d, position=(%d,%d), " +
                        "bombes=%s, mode=%s}", name, alive, lives, x, y,
                getBombInventoryDisplay(), gameMode.getEmoji());
    }

    /**
     * Vérifie si le joueur est à une position donnée dans la grille.
     *
     * @param x Coordonnée X à vérifier
     * @param y Coordonnée Y à vérifier
     * @return true si le joueur est à cette position, false sinon
     */
    public boolean isAtPosition(int x, int y) {
        return this.x == x && this.y == y;
    }

    /**
     * Vérifie si le joueur est à sa position de spawn.
     *
     * @return true si le joueur est à sa position de spawn, false sinon
     */
    public boolean isAtSpawn() {
        return x == spawnX && y == spawnY;
    }

    /**
     * Calcule la distance euclidienne entre le joueur et une position cible.
     *
     * @param targetX Coordonnée X de la cible
     * @param targetY Coordonnée Y de la cible
     * @return Distance en unités de grille
     */
    public double distanceTo(int targetX, int targetY) {
        return Math.sqrt(Math.pow(x - targetX, 2) + Math.pow(y - targetY, 2));
    }

    /**
     * Retourne les statistiques du joueur sous forme de texte formaté.
     * <p>
     * Utilisé pour l'affichage dans l'interface utilisateur.
     *
     * @return Chaîne formatée contenant les statistiques du joueur
     */
    public String getStatsText() {
        return String.format("%s\n❤️ Vies: %d\n%s Bombes: %s\n💥 Puissance: %d\n⚡ Vitesse: %d\n🏆 Score: %d\n%s",
                name, lives, gameMode.getEmoji(), getBombInventoryDisplay(),
                bombPower, speed, score, alive ? "✅ VIVANT" : "💀 MORT");
    }
}