package Jeu_Mouvement;
import java.awt.image.BufferedImage;

public class BombermanMovement {
    private BufferedImage[] sprites; // Sprites pour chaque direction (gauche, bas, haut, droit)
    private int currentDirection;
    private int playerId;

    public BombermanMovement(int playerId, BufferedImage[] sprites) {
        this.playerId = playerId;
        this.sprites = sprites;
        this.currentDirection = 1; // Par défaut vers le bas
    }

    public void move(String input) {
        switch (playerId) {
            case 1: // Joueur 1 (Q, S, Z, D)
                if (input.equalsIgnoreCase("q")) {
                    currentDirection = 0; // Gauche
                } else if (input.equalsIgnoreCase("s")) {
                    currentDirection = 1; // Bas
                } else if (input.equalsIgnoreCase("z")) {
                    currentDirection = 2; // Haut
                } else if (input.equalsIgnoreCase("d")) {
                    currentDirection = 3; // Droite
                }
                break;
            case 2: // Joueur 2 (Flèches)
                if (input.equalsIgnoreCase("left")) {
                    currentDirection = 0;
                } else if (input.equalsIgnoreCase("down")) {
                    currentDirection = 1;
                } else if (input.equalsIgnoreCase("up")) {
                    currentDirection = 2;
                } else if (input.equalsIgnoreCase("right")) {
                    currentDirection = 3;
                }
                break;
            case 3: // Joueur 3 (G, H, Y, J)
                if (input.equalsIgnoreCase("g")) {
                    currentDirection = 0;
                } else if (input.equalsIgnoreCase("h")) {
                    currentDirection = 1;
                } else if (input.equalsIgnoreCase("y")) {
                    currentDirection = 2;
                } else if (input.equalsIgnoreCase("j")) {
                    currentDirection = 3;
                }
                break;
            case 4: // Joueur 4 (K, L, O, M)
                if (input.equalsIgnoreCase("k")) {
                    currentDirection = 0;
                } else if (input.equalsIgnoreCase("l")) {
                    currentDirection = 1;
                } else if (input.equalsIgnoreCase("o")) {
                    currentDirection = 2;
                } else if (input.equalsIgnoreCase("m")) {
                    currentDirection = 3;
                }
                break;
        }
    }

    public BufferedImage getCurrentSprite() {
        return sprites[currentDirection];
    }

    public int getCurrentDirection() {
        return currentDirection;
    }
}