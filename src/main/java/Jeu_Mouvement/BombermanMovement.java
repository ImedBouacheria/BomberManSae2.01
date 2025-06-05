package Jeu_Mouvement;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class BombermanMovement implements KeyListener {

    // Position des joueurs (x, y)
    private int[] player1Pos = {0, 0};
    private int[] player2Pos = {1, 0};
    private int[] player3Pos = {0, 1};
    private int[] player4Pos = {1, 1};

    // Taille de la grille de jeu
    private final int gridWidth = 15;
    private final int gridHeight = 13;

    // Map des contrôles pour chaque joueur
    private Map<Integer, PlayerControls> controls;

    public BombermanMovement() {
        initializeControls();
    }

    private void initializeControls() {
        controls = new HashMap<>();

        // Joueur 1: q,s,z,d (gauche,bas,haut,droite)
        controls.put(1, new PlayerControls(
                KeyEvent.VK_Q, // gauche
                KeyEvent.VK_S, // bas
                KeyEvent.VK_Z, // haut
                KeyEvent.VK_D  // droite
        ));

        // Joueur 2: flèches directionnelles
        controls.put(2, new PlayerControls(
                KeyEvent.VK_LEFT,  // gauche
                KeyEvent.VK_DOWN,  // bas
                KeyEvent.VK_UP,    // haut
                KeyEvent.VK_RIGHT  // droite
        ));

        // Joueur 3: g,h,y,j (gauche,bas,haut,droite)
        controls.put(3, new PlayerControls(
                KeyEvent.VK_G, // gauche
                KeyEvent.VK_H, // bas
                KeyEvent.VK_Y, // haut
                KeyEvent.VK_J  // droite
        ));

        // Joueur 4: o,k,l,m (gauche,bas,haut,droite)
        controls.put(4, new PlayerControls(
                KeyEvent.VK_O, // gauche
                KeyEvent.VK_K, // bas
                KeyEvent.VK_L, // haut
                KeyEvent.VK_M  // droite
        ));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // Vérifier les contrôles pour chaque joueur
        for (int playerId = 1; playerId <= 4; playerId++) {
            PlayerControls playerControls = controls.get(playerId);

            if (keyCode == playerControls.left) {
                movePlayer(playerId, -1, 0);
            } else if (keyCode == playerControls.down) {
                movePlayer(playerId, 0, 1);
            } else if (keyCode == playerControls.up) {
                movePlayer(playerId, 0, -1);
            } else if (keyCode == playerControls.right) {
                movePlayer(playerId, 1, 0);
            }
        }
    }

    private void movePlayer(int playerId, int deltaX, int deltaY) {
        int[] playerPos = getPlayerPosition(playerId);

        int newX = playerPos[0] + deltaX;
        int newY = playerPos[1] + deltaY;

        // Vérifier les limites de la grille
        if (isValidPosition(newX, newY)) {
            playerPos[0] = newX;
            playerPos[1] = newY;

            System.out.println("Joueur " + playerId + " déplacé vers (" + newX + ", " + newY + ")");
        }
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < gridWidth && y >= 0 && y < gridHeight;
    }

    private int[] getPlayerPosition(int playerId) {
        switch (playerId) {
            case 1:
                return player1Pos;
            case 2:
                return player2Pos;
            case 3:
                return player3Pos;
            case 4:
                return player4Pos;
            default:
                throw new IllegalArgumentException("ID joueur invalide: " + playerId);
        }
    }

    // Getters pour les positions des joueurs
    public int[] getPlayer1Position() {
        return player1Pos.clone();
    }

    public int[] getPlayer2Position() {
        return player2Pos.clone();
    }

    public int[] getPlayer3Position() {
        return player3Pos.clone();
    }

    public int[] getPlayer4Position() {
        return player4Pos.clone();
    }

    // Setters pour les positions initiales
    public void setPlayerPosition(int playerId, int x, int y) {
        if (isValidPosition(x, y)) {
            int[] pos = getPlayerPosition(playerId);
            pos[0] = x;
            pos[1] = y;
        }
    }

    public void printAllPositions() {
        System.out.println("=== Positions des joueurs ===");
        System.out.println("Joueur 1: (" + player1Pos[0] + ", " + player1Pos[1] + ")");
        System.out.println("Joueur 2: (" + player2Pos[0] + ", " + player2Pos[1] + ")");
        System.out.println("Joueur 3: (" + player3Pos[0] + ", " + player3Pos[1] + ")");
        System.out.println("Joueur 4: (" + player4Pos[0] + ", " + player4Pos[1] + ")");
        System.out.println("=============================");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Non utilisé pour le moment
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Non utilisé pour le moment
    }

    // Classe interne pour stocker les contrôles d'un joueur
    public static class PlayerControls {
        final int left, down, up, right;

        public PlayerControls(int left, int down, int up, int right) {
            this.left = left;
            this.down = down;
            this.up = up;
            this.right = right;
        }


        // Méthode de test
        public static void main(String[] args) {
            BombermanMovement movement = new BombermanMovement();

            System.out.println("=== Test des déplacements Bomberman ===");
            System.out.println("Contrôles:");
            System.out.println("Joueur 1: Q(gauche), S(bas), Z(haut), D(droite)");
            System.out.println("Joueur 2: ←(gauche), ↓(bas), ↑(haut), →(droite)");
            System.out.println("Joueur 3: G(gauche), H(bas), Y(haut), J(droite)");
            System.out.println("Joueur 4: O(gauche), K(bas), L(haut), M(droite)");
            System.out.println();

            movement.printAllPositions();

            // Simulation de quelques mouvements
            KeyEvent testEvent1 = new KeyEvent(new java.awt.Button(), KeyEvent.KEY_PRESSED,
                    System.currentTimeMillis(), 0, KeyEvent.VK_D, 'D');
            movement.keyPressed(testEvent1);

            KeyEvent testEvent2 = new KeyEvent(new java.awt.Button(), KeyEvent.KEY_PRESSED,
                    System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED);
            movement.keyPressed(testEvent2);

            movement.printAllPositions();
        }
    }
}