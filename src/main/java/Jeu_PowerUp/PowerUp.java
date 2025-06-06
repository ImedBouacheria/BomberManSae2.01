package Jeu_PowerUp;
import Jeu_Perso_Non_Bot.Perso_Non_Bot;

import java.awt.image.BufferedImage;

public class PowerUp {
    public enum Type {
        BOMB_UP,       // Augmente le nombre de bombes
        FIRE_UP,       // Augmente la portée des explosions
        SPEED_UP,      // Augmente la vitesse de déplacement
        LIFE_UP        // Ajoute une vie supplémentaire
    }

    private final Type type;
    private final BufferedImage sprite;
    private final int x, y;  // Position sur la carte

    public PowerUp(Type type, BufferedImage sprite, int x, int y) {
        this.type = type;
        this.sprite = sprite;
        this.x = x;
        this.y = y;
    }

    // Getters
    public Type getType() { return type; }
    public BufferedImage getSprite() { return sprite; }
    public int getX() { return x; }
    public int getY() { return y; }

    // Méthode pour appliquer l'effet au joueur
    public void applyEffect(Perso_Non_Bot player) {
        switch (type) {
            case BOMB_UP:
                player.setBombs(player.getBombs() + 1);
                break;
        }
    }
}
