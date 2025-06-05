package Jeu_PowerUp;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;

public class Bomb {
    // Constantes de la bombe
    private static final int EXPLOSION_DELAY = 2000; // 2 secondes en millisecondes
    private static final int EXPLOSION_RANGE = 2; // Amplitude de 2 carrés

    // Position de la bombe
    private int x;
    private int y;

    // État de la bombe
    private boolean hasExploded;
    private boolean isActive;
    private Timer explosionTimer;

    // Référence au joueur qui a posé la bombe
    private Player owner;

    // Interface pour gérer les événements d'explosion
    public interface ExplosionListener {
        void onExplosion(int x, int y, List<int[]> affectedCells);
        void onBombDestroyed(Bomb bomb);
    }

    private ExplosionListener explosionListener;

    // Constructeur
    public Bomb(int x, int y, Player owner) {
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.hasExploded = false;
        this.isActive = true;

        startExplosionTimer();
    }

    // Démarre le timer d'explosion
    private void startExplosionTimer() {
        explosionTimer = new Timer();
        explosionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                explode();
            }
        }, EXPLOSION_DELAY);
    }

    // Fait exploser la bombe
    private void explode() {
        if (hasExploded || !isActive) {
            return;
        }

        hasExploded = true;
        isActive = false;

        // Calculer les cellules affectées par l'explosion
        List<int[]> affectedCells = calculateAffectedCells();

        // Notifier l'explosion
        if (explosionListener != null) {
            explosionListener.onExplosion(x, y, affectedCells);
            explosionListener.onBombDestroyed(this);
        }

        // Libérer le joueur pour qu'il puisse poser une nouvelle bombe
        if (owner != null) {
            owner.onBombExploded(this);
        }

        // Nettoyer le timer
        if (explosionTimer != null) {
            explosionTimer.cancel();
        }
    }

    // Calcule les cellules affectées par l'explosion (croix)
    private List<int[]> calculateAffectedCells() {
        List<int[]> affectedCells = new ArrayList<>();

        // Ajouter la position de la bombe
        affectedCells.add(new int[]{x, y});

        // Explosion horizontale (gauche et droite)
        for (int i = 1; i <= EXPLOSION_RANGE; i++) {
            affectedCells.add(new int[]{x - i, y}); // Gauche
            affectedCells.add(new int[]{x + i, y}); // Droite
        }

        // Explosion verticale (haut et bas)
        for (int i = 1; i <= EXPLOSION_RANGE; i++) {
            affectedCells.add(new int[]{x, y - i}); // Haut
            affectedCells.add(new int[]{x, y + i}); // Bas
        }

        return affectedCells;
    }

    // Force l'explosion (pour les explosions en chaîne)
    public void forceExplode() {
        if (!hasExploded && isActive) {
            if (explosionTimer != null) {
                explosionTimer.cancel();
            }
            explode();
        }
    }

    // Détruit la bombe sans explosion
    public void destroy() {
        isActive = false;
        if (explosionTimer != null) {
            explosionTimer.cancel();
        }
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean hasExploded() { return hasExploded; }
    public boolean isActive() { return isActive; }
    public Player getOwner() { return owner; }

    // Setter pour le listener d'explosion
    public void setExplosionListener(ExplosionListener listener) {
        this.explosionListener = listener;
    }

    // Classe Player pour gérer les bombes du joueur
    public static class Player {
        private int maxBombs;
        private List<Bomb> activeBombs;
        private int playerId;
        private int x, y; // Position du joueur

        public Player(int playerId) {
            this.playerId = playerId;
            this.maxBombs = 1; // Initialement 1 bombe
            this.activeBombs = new ArrayList<>();
        }

        // Tente de placer une bombe
        public boolean tryPlaceBomb(int bombX, int bombY, ExplosionListener listener) {
            if (canPlaceBomb()) {
                Bomb bomb = new Bomb(bombX, bombY, this);
                bomb.setExplosionListener(listener);
                activeBombs.add(bomb);
                return true;
            }
            return false;
        }

        // Vérifie si le joueur peut placer une bombe
        public boolean canPlaceBomb() {
            return activeBombs.size() < maxBombs;
        }

        // Appelé quand une bombe explose
        public void onBombExploded(Bomb bomb) {
            activeBombs.remove(bomb);
        }

        // Augmente le nombre maximum de bombes (bonus)
        public void increaseBombCapacity() {
            maxBombs++;
        }

        // Getters et setters
        public int getMaxBombs() { return maxBombs; }
        public int getActiveBombCount() { return activeBombs.size(); }
        public List<Bomb> getActiveBombs() { return new ArrayList<>(activeBombs); }
        public int getPlayerId() { return playerId; }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }
    }



    // Méthode de test simple
    public static void main(String[] args) {
        System.out.println("=== Test du système de bombes Bomberman ===");

        // Créer un joueur
        Player player1 = new Player(1);
        player1.setPosition(5, 5);

        // Test de placement de bombe
        System.out.println("🎮 Joueur 1 tente de placer une bombe");

        ExplosionListener testListener = new ExplosionListener() {
            @Override
            public void onExplosion(int x, int y, List<int[]> affectedCells) {
                System.out.println("💥 Explosion détectée à (" + x + ", " + y + ")");
                System.out.println("Cellules affectées: " + affectedCells.size());
                for (int[] cell : affectedCells) {
                    System.out.println("  - (" + cell[0] + ", " + cell[1] + ")");
                }
            }

            @Override
            public void onBombDestroyed(Bomb bomb) {
                System.out.println("🗑️ Bombe détruite à (" + bomb.getX() + ", " + bomb.getY() + ")");
            }
        };

        boolean placed = player1.tryPlaceBomb(3, 3, testListener);
        System.out.println("Bombe placée: " + placed);
        System.out.println("Bombes actives: " + player1.getActiveBombCount() + "/" + player1.getMaxBombs());

        // Tenter de placer une deuxième bombe (devrait échouer)
        boolean placed2 = player1.tryPlaceBomb(5, 5, testListener);
        System.out.println("Deuxième bombe placée: " + placed2);

        // Attendre l'explosion
        System.out.println("\n⏰ Attente de l'explosion (2 secondes)...");

        try {
            Thread.sleep(3000); // Attendre 3 secondes pour voir l'explosion
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Bombes actives après explosion: " + player1.getActiveBombCount());

        // Test d'augmentation de capacité
        player1.increaseBombCapacity();
        System.out.println("Capacité augmentée à: " + player1.getMaxBombs());
    }
}
