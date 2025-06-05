package Jeu_Coordinateur;
import Jeu_Perso_Non_Bot.Perso_Non_Bot;
import Jeu_Mouvement.BombermanMovement;
import Jeu_PowerUp.Bomb;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

/**
 * Classe principale qui coordonne l'interaction entre les personnages,
 * le système de mouvement et les bombes dans le jeu Bomberman
 */
class BombermanGameCoordinator extends BombermanMovement {

    // Système de mouvement
    private BombermanMovement movementSystem;

    // Liste des joueurs actifs
    private Map<Integer, Perso_Non_Bot> joueurs;

    // Map pour associer les contrôles aux joueurs
    private Map<Integer, BombermanMovement.PlayerControls> controlsMap;

    // Taille de la grille
    private final int gridWidth = 15;
    private final int gridHeight = 13;

    // État du jeu
    private boolean gameRunning;
    private int joueursVivants;

    public BombermanGameCoordinator() {
        this.movementSystem = new BombermanMovement();
        this.joueurs = new HashMap<>();
        this.controlsMap = new HashMap<>();
        this.gameRunning = true;
        this.joueursVivants = 0;

        initializeControlsMap();
        initializeJoueurs();
    }

    /**
     * Initialise la map des contrôles pour chaque joueur
     */
    private void initializeControlsMap() {
        // Récupérer les contrôles depuis BombermanMovement via réflexion ou les redéfinir
        controlsMap.put(1, new BombermanMovement.PlayerControls(
                KeyEvent.VK_Q, KeyEvent.VK_S, KeyEvent.VK_Z, KeyEvent.VK_D
        ));
        controlsMap.put(2, new BombermanMovement.PlayerControls(
                KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_UP, KeyEvent.VK_RIGHT
        ));
        controlsMap.put(3, new BombermanMovement.PlayerControls(
                KeyEvent.VK_G, KeyEvent.VK_H, KeyEvent.VK_Y, KeyEvent.VK_J
        ));
        controlsMap.put(4, new BombermanMovement.PlayerControls(
                KeyEvent.VK_O, KeyEvent.VK_K, KeyEvent.VK_L, KeyEvent.VK_M
        ));
    }

    /**
     * Initialise les personnages avec leurs positions de départ
     */
    private void initializeJoueurs() {
        // Créer les 4 joueurs
        Perso_Non_Bot joueur1 = new Perso_Non_Bot(1, "Player", "One", "sprite1.png");
        Perso_Non_Bot joueur2 = new Perso_Non_Bot(2, "Player", "Two", "sprite2.png");
        Perso_Non_Bot joueur3 = new Perso_Non_Bot(3, "Player", "Three", "sprite3.png");
        Perso_Non_Bot joueur4 = new Perso_Non_Bot(4, "Player", "Four", "sprite4.png");

        // Définir les positions initiales (coins de la carte)
        joueur1.setPosition(0, 0);          // Coin haut-gauche
        joueur2.setPosition(gridWidth-1, 0); // Coin haut-droite
        joueur3.setPosition(0, gridHeight-1); // Coin bas-gauche
        joueur4.setPosition(gridWidth-1, gridHeight-1); // Coin bas-droite

        // Synchroniser avec le système de mouvement
        movementSystem.setPlayerPosition(1, 0, 0);
        movementSystem.setPlayerPosition(2, gridWidth-1, 0);
        movementSystem.setPlayerPosition(3, 0, gridHeight-1);
        movementSystem.setPlayerPosition(4, gridWidth-1, gridHeight-1);

        // Ajouter les listeners pour les événements des personnages
        setupPersonnageListeners(joueur1);
        setupPersonnageListeners(joueur2);
        setupPersonnageListeners(joueur3);
        setupPersonnageListeners(joueur4);

        // Ajouter à la map des joueurs
        joueurs.put(1, joueur1);
        joueurs.put(2, joueur2);
        joueurs.put(3, joueur3);
        joueurs.put(4, joueur4);

        joueursVivants = 4;
    }

    /**
     * Configure les listeners pour un personnage
     */
    private void setupPersonnageListeners(Perso_Non_Bot personnage) {
        personnage.setListener(new Perso_Non_Bot.PersonnageListener() {
            @Override
            public void onPersonnageMort(Perso_Non_Bot personnage) {
                joueursVivants--;
                System.out.println("💀 " + personnage.getNomComplet() + " est éliminé! Joueurs restants: " + joueursVivants);

                if (joueursVivants <= 1) {
                    finirPartie();
                }
            }

            @Override
            public void onPersonnageTouche(Perso_Non_Bot personnage, int degats) {
                System.out.println("💥 " + personnage.getPrenom() + " a subi " + degats + " dégât(s)!");
            }

            @Override
            public void onBombePlacee(Perso_Non_Bot personnage, int x, int y) {
                System.out.println("💣 " + personnage.getPrenom() + " a placé une bombe en (" + x + "," + y + ")");

                // Ici vous pouvez ajouter la logique pour afficher la bombe sur la carte
                // et gérer son explosion après un délai
            }

            @Override
            public void onPositionChanged(Perso_Non_Bot personnage, int oldX, int oldY, int newX, int newY) {
                // Synchroniser avec le système de mouvement
                movementSystem.setPlayerPosition(personnage.getId(), newX, newY);
                System.out.println("🚶 " + personnage.getPrenom() + " bouge de (" + oldX + "," + oldY + ") vers (" + newX + "," + newY + ")");
            }
        });
    }

    /**
     * Gère les événements clavier en coordonnant mouvement et actions
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameRunning) return;

        int keyCode = e.getKeyCode();

        // Traiter les mouvements et actions pour chaque joueur
        for (int playerId = 1; playerId <= 4; playerId++) {
            Perso_Non_Bot joueur = joueurs.get(playerId);
            BombermanMovement.PlayerControls controls = controlsMap.get(playerId);

            if (joueur != null && joueur.isAlive() && controls != null) {


                // Gestion du placement de bombe (espace pour tous les joueurs pour simplicité)
                // Vous pouvez définir des touches spécifiques pour chaque joueur
                if (keyCode == KeyEvent.VK_SPACE) {
                    joueur.placerBombe();
                }

                // Appeler aussi la méthode de gestion des touches du personnage
                joueur.gererTouche(e, controls);
            }
        }

        // Passer l'événement au système de mouvement pour la synchronisation
        movementSystem.keyPressed(e);
    }

    /**
     * Tente un mouvement pour un joueur avec vérification des collisions
     */
    private void tentativeMovement(Perso_Non_Bot joueur, int deltaX, int deltaY) {
        int newX = joueur.getX() + deltaX;
        int newY = joueur.getY() + deltaY;

        // Vérifier les limites de la grille
        if (newX >= 0 && newX < gridWidth && newY >= 0 && newY < gridHeight) {

            // Vérifier les collisions avec d'autres joueurs
            if (!isPositionOccupied(newX, newY, joueur.getId())) {

                // Effectuer le mouvement
                joueur.deplacer(newX, newY);

                // Vérifier s'il y a un power-up à cette position
                checkPowerUpAtPosition(joueur, newX, newY);
            } else {
                System.out.println("❌ Position (" + newX + "," + newY + ") occupée!");
            }
        } else {
            System.out.println("❌ Mouvement hors limites pour " + joueur.getPrenom());
        }
    }

    /**
     * Vérifie si une position est occupée par un autre joueur
     */
    private boolean isPositionOccupied(int x, int y, int currentPlayerId) {
        for (Perso_Non_Bot joueur : joueurs.values()) {
            if (joueur.getId() != currentPlayerId && joueur.isAlive()
                    && joueur.getX() == x && joueur.getY() == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie s'il y a un power-up à la position donnée
     * (À implémenter selon votre système de power-ups)
     */
    private void checkPowerUpAtPosition(Perso_Non_Bot joueur, int x, int y) {
        // Simulation d'un power-up aléatoire (à remplacer par votre logique)
        if (Math.random() < 0.1) { // 10% de chance
            joueur.attraperBombeSupplementaire();
        }
    }

    /**
     * Termine la partie et annonce le gagnant
     */
    private void finirPartie() {
        gameRunning = false;

        // Trouver le gagnant
        Perso_Non_Bot gagnant = null;
        for (Perso_Non_Bot joueur : joueurs.values()) {
            if (joueur.isAlive()) {
                gagnant = joueur;
                break;
            }
        }

        if (gagnant != null) {
            System.out.println("🏆 VICTOIRE! " + gagnant.getNomComplet() + " remporte la partie!");
        } else {
            System.out.println("🤝 Match nul! Aucun survivant.");
        }

        afficherStatistiques();
    }

    /**
     * Affiche les statistiques de fin de partie
     */
    private void afficherStatistiques() {
        System.out.println("\n=== STATISTIQUES DE LA PARTIE ===");
        for (Perso_Non_Bot joueur : joueurs.values()) {
            System.out.println(joueur.getNomComplet() + ":");
            System.out.println("  - Bombes placées: " + joueur.getStatistiques().getBombesPlacees());
            System.out.println("  - Temps de survie: " + joueur.getStatistiques().getTempsVie() + "ms");
            System.out.println("  - Vivant: " + (joueur.isAlive() ? "✅" : "❌"));
        }
        System.out.println("================================\n");
    }

    /**
     * Affiche l'état actuel du jeu
     */
    public void afficherEtatJeu() {
        System.out.println("\n=== ÉTAT DU JEU ===");
        for (Perso_Non_Bot joueur : joueurs.values()) {
            if (joueur.isAlive()) {
                System.out.println(joueur.getPrenom() + ": Position(" + joueur.getX() + "," + joueur.getY() +
                        ") Vies:" + joueur.getNombreVie() + " Bombes:" + joueur.getNombreBombe());
            }
        }
        System.out.println("Joueurs vivants: " + joueursVivants);
        System.out.println("==================\n");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        movementSystem.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        movementSystem.keyTyped(e);
    }

    // Getters
    public Map<Integer, Perso_Non_Bot> getJoueurs() { return joueurs; }
    public BombermanMovement getMovementSystem() { return movementSystem; }
    public boolean isGameRunning() { return gameRunning; }
    public int getJoueursVivants() { return joueursVivants; }

    /**
     * Méthode de test et démonstration
     */
    public static void main(String[] args) {
        System.out.println("=== BOMBERMAN GAME COORDINATOR ===");

        BombermanGameCoordinator game = new BombermanGameCoordinator();

        System.out.println("Jeu initialisé avec 4 joueurs!");
        System.out.println("Contrôles:");
        System.out.println("Joueur 1: Q(gauche), S(bas), Z(haut), D(droite)");
        System.out.println("Joueur 2: ←(gauche), ↓(bas), ↑(haut), →(droite)");
        System.out.println("Joueur 3: G(gauche), H(bas), Y(haut), J(droite)");
        System.out.println("Joueur 4: O(gauche), K(bas), L(haut), M(droite)");
        System.out.println("Bombe: ESPACE");

        game.afficherEtatJeu();

        // Simulation de quelques mouvements
        System.out.println("=== SIMULATION DE MOUVEMENTS ===");

        // Joueur 1 se déplace à droite
        KeyEvent moveRight = new KeyEvent(new java.awt.Button(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_D, 'D');
        game.keyPressed(moveRight);

        // Joueur 2 se déplace à gauche
        KeyEvent moveLeft = new KeyEvent(new java.awt.Button(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED);
        game.keyPressed(moveLeft);

        // Joueur 1 place une bombe
        KeyEvent placeBomb = new KeyEvent(new java.awt.Button(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, ' ');
        game.keyPressed(placeBomb);

        game.afficherEtatJeu();

        System.out.println("=== Intégration réussie des trois classes! ===");
    }
}
