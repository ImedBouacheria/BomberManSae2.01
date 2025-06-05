package Jeu_Perso_Non_Bot;
import Jeu_Mouvement.BombermanMovement;
import Jeu_PowerUp.Bomb;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;

public class Perso_Non_Bot {

    // Propriétés de base du personnage
    private int id;
    private String prenom;
    private String nom;
    private int nombreBombe;
    private String sprite;
    private int nombreVie;
    private boolean isAlive;

    // Position du personnage
    private int x;
    private int y;

    // Système de bombes (utilise la classe Bomb.Player)
    private Bomb.Player bombPlayer;

    // Interface pour notifier les événements du personnage
    public interface PersonnageListener {
        void onPersonnageMort(Perso_Non_Bot personnage);
        void onPersonnageTouche(Perso_Non_Bot personnage, int degats);
        void onBombePlacee(Perso_Non_Bot personnage, int x, int y);
        void onPositionChanged(Perso_Non_Bot personnage, int oldX, int oldY, int newX, int newY);
    }

    private PersonnageListener listener;

    // Constructeur
    public Perso_Non_Bot(int id, String prenom, String nom, String sprite) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.sprite = sprite;
        this.nombreBombe = 1;        // 1 bombe initialement
        this.nombreVie = 3;          // 3 vies initialement
        this.isAlive = true;

        // Initialiser le système de bombes
        this.bombPlayer = new Bomb.Player(id);

        // Position initiale
        this.x = 0;
        this.y = 0;
    }

    // Déplacement du personnage
    public boolean deplacer(int newX, int newY) {
        if (!isAlive) {
            return false;
        }

        int oldX = this.x;
        int oldY = this.y;

        this.x = newX;
        this.y = newY;

        // Mettre à jour la position dans le système de bombes
        bombPlayer.setPosition(newX, newY);

        // Notifier le changement de position
        if (listener != null) {
            listener.onPositionChanged(this, oldX, oldY, newX, newY);
        }

        return true;
    }

    // Placer une bombe
    public boolean placerBombe() {
        if (!isAlive) {
            return false;
        }

        // Créer un listener pour les explosions
        Bomb.ExplosionListener explosionListener = new Bomb.ExplosionListener() {
            @Override
            public void onExplosion(int bombX, int bombY, List<int[]> affectedCells) {
                // Cette méthode sera appelée par la classe qui gère la carte
                if (listener != null) {
                    // Vérifier si le personnage est touché par sa propre bombe
                    for (int[] cell : affectedCells) {
                        if (cell[0] == x && cell[1] == y) {
                            prendreDegats(1);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onBombDestroyed(Bomb bomb) {
                // Bombe détruite, rien de spécial à faire
            }
        };

        boolean bombePlacee = bombPlayer.tryPlaceBomb(x, y, explosionListener);

        if (bombePlacee && listener != null) {
            listener.onBombePlacee(this, x, y);
        }

        return bombePlacee;
    }

    // Prendre des dégâts (explosion de bombe)
    public void prendreDegats(int degats) {
        if (!isAlive) {
            return;
        }

        nombreVie -= degats;

        if (listener != null) {
            listener.onPersonnageTouche(this, degats);
        }

        System.out.println("💔 " + prenom + " a perdu " + degats + " vie(s). Vies restantes: " + nombreVie);

        if (nombreVie <= 0) {
            mourir();
        }
    }

    // Le personnage meurt
    private void mourir() {
        isAlive = false;
        nombreVie = 0;

        System.out.println("💀 " + prenom + " " + nom + " est mort et disparaît de la carte !");

        if (listener != null) {
            listener.onPersonnageMort(this);
        }
    }

    // Attraper le power-up bombe supplémentaire
    public void attraperBombeSupplementaire() {
        if (!isAlive) {
            return;
        }

        nombreBombe++;
        bombPlayer.increaseBombCapacity();
        System.out.println("💣 " + prenom + " a maintenant " + nombreBombe + " bombe(s) !");
    }

    // Vérifier si le personnage est touché par une explosion à une position donnée
    public boolean estToucheParExplosion(List<int[]> cellulesAffectees) {
        if (!isAlive) {
            return false;
        }

        for (int[] cellule : cellulesAffectees) {
            if (cellule[0] == x && cellule[1] == y) {
                prendreDegats(1);
                return true;
            }
        }
        return false;
    }

    // Méthode pour gérer les contrôles clavier (intégration avec BombermanMovement)
    public void gererTouche(KeyEvent keyEvent, BombermanMovement.PlayerControls controls) {
        if (!isAlive) {
            return;
        }

        int keyCode = keyEvent.getKeyCode();


        // Placement de bombe (par exemple avec la barre d'espace)
        if (keyCode == KeyEvent.VK_SPACE) {
            placerBombe();
        }
    }

    // Getters
    public int getId() { return id; }
    public String getPrenom() { return prenom; }
    public String getNom() { return nom; }
    public String getNomComplet() { return prenom + " " + nom; }
    public int getNombreBombe() { return nombreBombe; }
    public String getSprite() { return sprite; }
    public int getNombreVie() { return nombreVie; }
    public boolean isAlive() { return isAlive; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Bomb.Player getBombPlayer() { return bombPlayer; }

    // Setters
    public void setSprite(String sprite) { this.sprite = sprite; }
    public void setPosition(int x, int y) {
        int oldX = this.x;
        int oldY = this.y;
        this.x = x;
        this.y = y;
        bombPlayer.setPosition(x, y);

        if (listener != null) {
            listener.onPositionChanged(this, oldX, oldY, x, y);
        }
    }

    public void setListener(PersonnageListener listener) {
        this.listener = listener;
    }

    // Méthode toString pour debug
    @Override
    public String toString() {
        return String.format("Personnage[ID=%d, Nom=%s %s, Vies=%d, Bombes=%d, Position=(%d,%d), Vivant=%s]",
                id, prenom, nom, nombreVie, nombreBombe, x, y, isAlive);
    }

    // Classe pour les statistiques du personnage
    public static class Statistiques {
        private int bombesPlacees;
        private int blocsDetruits;
        private int adversairesTouches;
        private int degatsSubis;
        private long tempsVie;

        public Statistiques() {
            this.bombesPlacees = 0;
            this.blocsDetruits = 0;
            this.adversairesTouches = 0;
            this.degatsSubis = 0;
            this.tempsVie = System.currentTimeMillis();
        }

        // Getters et méthodes d'incrémentation
        public void incrementerBombesPlacees() { bombesPlacees++; }
        public void incrementerBlocsDetruits() { blocsDetruits++; }
        public void incrementerAdversairesTouches() { adversairesTouches++; }
        public void incrementerDegatsSubis(int degats) { degatsSubis += degats; }

        public int getBombesPlacees() { return bombesPlacees; }
        public int getBlocsDetruits() { return blocsDetruits; }
        public int getAdversairesTouches() { return adversairesTouches; }
        public int getDegatsSubis() { return degatsSubis; }
        public long getTempsVie() { return System.currentTimeMillis() - tempsVie; }
    }

    private Statistiques statistiques = new Statistiques();
    public Statistiques getStatistiques() { return statistiques; }

    // Méthode de test
    public static void main(String[] args) {
        System.out.println("=== Test de la classe Perso_Non_Bot ===");

        // Créer un personnage
        Perso_Non_Bot joueur1 = new Perso_Non_Bot(1, "Jean", "Dupont", "sprite_jean.png");

        // Définir un listener
        joueur1.setListener(new PersonnageListener() {
            @Override
            public void onPersonnageMort(Perso_Non_Bot personnage) {
                System.out.println("🚨 Le joueur " + personnage.getNomComplet() + " est mort !");
            }

            @Override
            public void onPersonnageTouche(Perso_Non_Bot personnage, int degats) {
                System.out.println("⚠️ " + personnage.getPrenom() + " a pris " + degats + " dégâts !");
            }

            @Override
            public void onBombePlacee(Perso_Non_Bot personnage, int x, int y) {
                System.out.println("💣 " + personnage.getPrenom() + " a placé une bombe à (" + x + ", " + y + ")");
            }

            @Override
            public void onPositionChanged(Perso_Non_Bot personnage, int oldX, int oldY, int newX, int newY) {
                System.out.println("🚶 " + personnage.getPrenom() + " s'est déplacé de (" + oldX + "," + oldY + ") vers (" + newX + "," + newY + ")");
            }
        });

        // Afficher les informations initiales
        System.out.println("Personnage créé: " + joueur1);

        // Test de déplacement
        joueur1.setPosition(5, 5);

        // Test de placement de bombe
        joueur1.placerBombe();

        // Test d'ajout de bombe supplémentaire
        joueur1.attraperBombeSupplementaire();

        System.out.println("Après power-up: " + joueur1);

        // Test de dégâts
        joueur1.prendreDegats(1);
        joueur1.prendreDegats(2);
        joueur1.prendreDegats(1); // Devrait tuer le personnage

        System.out.println("État final: " + joueur1);
    }
}