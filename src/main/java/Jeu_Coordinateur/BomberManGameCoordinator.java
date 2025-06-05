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
class BombermanGameCoordinator extends BombermanMovement implements Bomb.ExplosionListener {

    // Système de mouvement
    private BombermanMovement movementSystem;

    // Liste des joueurs actifs
    private Map<Integer, Perso_Non_Bot> joueurs;

    // Système de bombes pour chaque joueur
    private Map<Integer, Bomb.Player> bombPlayers;

    // Liste des bombes actives sur le terrain
    private List<Bomb> activeBombs;

    // Map pour associer les contrôles aux joueurs
    private Map<Integer, BombermanMovement.PlayerControls> controlsMap;

    // Map des touches de bombes pour chaque joueur
    private Map<Integer, Integer> bombKeys;

    // Taille de la grille
    private final int gridWidth = 15;
    private final int gridHeight = 13;

    // Grille pour représenter l'état du terrain
    private boolean[][] obstacles;

    // État du jeu
    private boolean gameRunning;
    private int joueursVivants;

    public BombermanGameCoordinator() {
        this.movementSystem = new BombermanMovement();
        this.joueurs = new HashMap<>();
        this.bombPlayers = new HashMap<>();
        this.activeBombs = new ArrayList<>();
        this.controlsMap = new HashMap<>();
        this.bombKeys = new HashMap<>();
        this.obstacles = new boolean[gridWidth][gridHeight];
        this.gameRunning = true;
        this.joueursVivants = 0;

        initializeControlsMap();
        initializeBombKeys();
        initializeJoueurs();
    }

    /**
     * Initialise la map des contrôles pour chaque joueur
     */
    private void initializeControlsMap() {
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
     * Initialise les touches de bombes pour chaque joueur
     */
    private void initializeBombKeys() {
        bombKeys.put(1, KeyEvent.VK_A);      // A pour joueur 1
        bombKeys.put(2, KeyEvent.VK_SPACE);  // Espace pour joueur 2
        bombKeys.put(3, KeyEvent.VK_T);      // T pour joueur 3
        bombKeys.put(4, KeyEvent.VK_P);      // P pour joueur 4
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
        joueur1.setPosition(0, 0);
        joueur2.setPosition(gridWidth-1, 0);
        joueur3.setPosition(0, gridHeight-1);
        joueur4.setPosition(gridWidth-1, gridHeight-1);

        // Créer les systèmes de bombes pour chaque joueur
        Bomb.Player bombPlayer1 = new Bomb.Player(1);
        Bomb.Player bombPlayer2 = new Bomb.Player(2);
        Bomb.Player bombPlayer3 = new Bomb.Player(3);
        Bomb.Player bombPlayer4 = new Bomb.Player(4);

        bombPlayer1.setPosition(0, 0);
        bombPlayer2.setPosition(gridWidth-1, 0);
        bombPlayer3.setPosition(0, gridHeight-1);
        bombPlayer4.setPosition(gridWidth-1, gridHeight-1);

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

        // Ajouter les systèmes de bombes
        bombPlayers.put(1, bombPlayer1);
        bombPlayers.put(2, bombPlayer2);
        bombPlayers.put(3, bombPlayer3);
        bombPlayers.put(4, bombPlayer4);

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
            }

            @Override
            public void onPositionChanged(Perso_Non_Bot personnage, int oldX, int oldY, int newX, int newY) {
                // Synchroniser avec le système de mouvement et les bombes
                movementSystem.setPlayerPosition(personnage.getId(), newX, newY);
                Bomb.Player bombPlayer = bombPlayers.get(personnage.getId());
                if (bombPlayer != null) {
                    bombPlayer.setPosition(newX, newY);
                }
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
            Bomb.Player bombPlayer = bombPlayers.get(playerId);
            BombermanMovement.PlayerControls controls = controlsMap.get(playerId);
            Integer bombKey = bombKeys.get(playerId);

            if (joueur != null && joueur.isAlive() && controls != null) {
                // Gestion du placement de bombe
                if (bombKey != null && keyCode == bombKey) {
                    placerBombeJoueur(playerId);
                }

                // Appeler aussi la méthode de gestion des touches du personnage
                joueur.gererTouche(e, controls);
            }
        }

        // Passer l'événement au système de mouvement pour la synchronisation
        movementSystem.keyPressed(e);
    }

    /**
     * Place une bombe pour un joueur donné
     */
    private void placerBombeJoueur(int playerId) {
        Perso_Non_Bot joueur = joueurs.get(playerId);
        Bomb.Player bombPlayer = bombPlayers.get(playerId);

        if (joueur != null && bombPlayer != null && joueur.isAlive()) {
            int x = joueur.getX();
            int y = joueur.getY();

            // Vérifier qu'il n'y a pas déjà une bombe à cette position
            if (!hasBombAt(x, y)) {
                boolean placed = bombPlayer.tryPlaceBomb(x, y, this);
                if (placed) {
                    System.out.println("💣 " + joueur.getPrenom() + " place une bombe en (" + x + "," + y + ")");

                    // Déclencher l'événement dans le personnage aussi
                    joueur.placerBombe();
                } else {
                    System.out.println("❌ " + joueur.getPrenom() + " ne peut pas placer plus de bombes!");
                }
            } else {
                System.out.println("❌ Il y a déjà une bombe à cette position!");
            }
        }
    }

    /**
     * Vérifie s'il y a une bombe à la position donnée
     */
    private boolean hasBombAt(int x, int y) {
        for (Bomb bomb : activeBombs) {
            if (bomb.getX() == x && bomb.getY() == y && bomb.isActive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Implémentation de ExplosionListener - appelée quand une bombe explose
     */
    @Override
    public void onExplosion(int x, int y, List<int[]> affectedCells) {
        System.out.println("💥 EXPLOSION en (" + x + "," + y + ") affectant " + affectedCells.size() + " cellules");

        // Traiter chaque cellule affectée
        for (int[] cell : affectedCells) {
            int cellX = cell[0];
            int cellY = cell[1];

            // Vérifier les limites
            if (cellX >= 0 && cellX < gridWidth && cellY >= 0 && cellY < gridHeight) {

                // Vérifier si des joueurs sont touchés
                for (Perso_Non_Bot joueur : joueurs.values()) {
                    if (joueur.isAlive() && joueur.getX() == cellX && joueur.getY() == cellY) {
                        System.out.println("💀 " + joueur.getPrenom() + " touché par l'explosion!");
                        joueur.prendreDegats(1); // Supposons que l'explosion fait 1 dégât
                    }
                }

                // Vérifier si d'autres bombes sont touchées (explosions en chaîne)
                for (Bomb bomb : new ArrayList<>(activeBombs)) {
                    if (bomb.isActive() && bomb.getX() == cellX && bomb.getY() == cellY) {
                        System.out.println("🔗 Explosion en chaîne déclenchée!");
                        bomb.forceExplode();
                    }
                }
            }
        }
    }

    /**
     * Implémentation de ExplosionListener - appelée quand une bombe est détruite
     */
    @Override
    public void onBombDestroyed(Bomb bomb) {
        activeBombs.remove(bomb);
        System.out.println("🗑️ Bombe détruite en (" + bomb.getX() + "," + bomb.getY() + ")");
    }

    /**
     * Tente un mouvement pour un joueur avec vérification des collisions
     */
    private void tentativeMovement(Perso_Non_Bot joueur, int deltaX, int deltaY) {
        int newX = joueur.getX() + deltaX;
        int newY = joueur.getY() + deltaY;

        // Vérifier les limites de la grille
        if (newX >= 0 && newX < gridWidth && newY >= 0 && newY < gridHeight) {
            // Vérifier les obstacles
            if (!obstacles[newX][newY]) {
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
                System.out.println("❌ Obstacle en (" + newX + "," + newY + ")!");
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
     */
    private void checkPowerUpAtPosition(Perso_Non_Bot joueur, int x, int y) {
        // Simulation d'un power-up aléatoire
        if (Math.random() < 0.1) { // 10% de chance
            joueur.attraperBombeSupplementaire();

            // Augmenter aussi la capacité de bombes du système
            Bomb.Player bombPlayer = bombPlayers.get(joueur.getId());
            if (bombPlayer != null) {
                bombPlayer.increaseBombCapacity();
                System.out.println("🎁 " + joueur.getPrenom() + " obtient une bombe supplémentaire! (" +
                        bombPlayer.getMaxBombs() + " max)");
            }
        }
    }

    /**
     * Termine la partie et annonce le gagnant
     */
    private void finirPartie() {
        gameRunning = false;

        // Détruire toutes les bombes actives
        for (Bomb bomb : new ArrayList<>(activeBombs)) {
            bomb.destroy();
        }
        activeBombs.clear();

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
            Bomb.Player bombPlayer = bombPlayers.get(joueur.getId());
            System.out.println(joueur.getNomComplet() + ":");
            System.out.println("  - Bombes placées: " + joueur.getStatistiques().getBombesPlacees());
            System.out.println("  - Capacité bombes: " + (bombPlayer != null ? bombPlayer.getMaxBombs() : "N/A"));
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
                Bomb.Player bombPlayer = bombPlayers.get(joueur.getId());
                System.out.println(joueur.getPrenom() + ": Position(" + joueur.getX() + "," + joueur.getY() +
                        ") Vies:" + joueur.getNombreVie() + " Bombes:" + joueur.getNombreBombe() +
                        " Capacité:" + (bombPlayer != null ? bombPlayer.getActiveBombCount() + "/" + bombPlayer.getMaxBombs() : "N/A"));
            }
        }
        System.out.println("Joueurs vivants: " + joueursVivants);
        System.out.println("Bombes actives: " + activeBombs.size());
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
    public Map<Integer, Bomb.Player> getBombPlayers() { return bombPlayers; }
    public List<Bomb> getActiveBombs() { return new ArrayList<>(activeBombs); }
    public BombermanMovement getMovementSystem() { return movementSystem; }
    public boolean isGameRunning() { return gameRunning; }
    public int getJoueursVivants() { return joueursVivants; }

    /**
     * Méthode de test et démonstration
     */
    public static void main(String[] args) {
        System.out.println("=== BOMBERMAN GAME COORDINATOR AVEC BOMBES ===");

        BombermanGameCoordinator game = new BombermanGameCoordinator();

        System.out.println("Jeu initialisé avec 4 joueurs et système de bombes!");
        System.out.println("Contrôles:");
        System.out.println("Joueur 1: Q(gauche), S(bas), Z(haut), D(droite) + A(bombe)");
        System.out.println("Joueur 2: ←(gauche), ↓(bas), ↑(haut), →(droite) + ESPACE(bombe)");
        System.out.println("Joueur 3: G(gauche), H(bas), Y(haut), J(droite) + T(bombe)");
        System.out.println("Joueur 4: O(gauche), K(bas), L(haut), M(droite) + P(bombe)");

        game.afficherEtatJeu();

        // Simulation de quelques actions
        System.out.println("=== SIMULATION DE MOUVEMENTS ET BOMBES ===");

        // Joueur 1 se déplace à droite puis place une bombe
        KeyEvent moveRight = new KeyEvent(new java.awt.Button(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_D, 'D');
        game.keyPressed(moveRight);

        KeyEvent placeBomb1 = new KeyEvent(new java.awt.Button(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_A, 'A');
        game.keyPressed(placeBomb1);

        // Joueur 2 place une bombe
        KeyEvent placeBomb2 = new KeyEvent(new java.awt.Button(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, ' ');
        game.keyPressed(placeBomb2);

        game.afficherEtatJeu();

        System.out.println("\n⏰ Attente des explosions (3 secondes)...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        game.afficherEtatJeu();
        System.out.println("=== Intégration réussie avec le système de bombes! ===");
    }
}
