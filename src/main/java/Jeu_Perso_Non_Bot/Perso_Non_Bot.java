package Jeu_Perso_Non_Bot;
import Jeu_Mouvement.BombermanMovement;
import Jeu_PowerUp.Bomb;
import Jeu_PowerUp.PowerUp;

import java.awt.image.BufferedImage;

public class Perso_Non_Bot {
    private int id;
    private String firstName;
    private String lastName;
    private BufferedImage baseSprite;
    private int lives;
    private int bombs;
    private boolean canPlaceBomb;
    private BombermanMovement movement;

    public Perso_Non_Bot(int id, String firstName, String lastName, BufferedImage baseSprite, BufferedImage[] movementSprites) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.baseSprite = baseSprite;
        this.lives = 3;
        this.bombs = 1;
        this.canPlaceBomb = true;
        this.movement = new BombermanMovement(id, movementSprites);
    }

    // Getters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public BufferedImage getBaseSprite() { return baseSprite; }
    public int getLives() { return lives; }
    public int getBombs() { return bombs; }
    public boolean canPlaceBomb() { return canPlaceBomb; }
    public BombermanMovement getMovement() { return movement; }

    // Setters
    public void setLives(int lives) { this.lives = lives; }
    public void setBombs(int bombs) { this.bombs = bombs; }
    public void setCanPlaceBomb(boolean canPlaceBomb) { this.canPlaceBomb = canPlaceBomb; }

    // Méthodes
    public Bomb placeBomb(int x, int y) {
        if (canPlaceBomb && bombs > 0) {
            bombs--;
            canPlaceBomb = false;
            return new Bomb(x, y, this);
        }
        return null;
    }

    public void takeDamage() {
        lives--;
        if (lives <= 0) {
            // Joueur éliminé
        }
    }

    public void collectPowerUp(PowerUp powerUp, PowerUp.Type PowerUpType) {
        if (powerUp.getType() == PowerUpType.BOMB_UP) {
            bombs++;
        }
        // Autres power-ups...
    }
}