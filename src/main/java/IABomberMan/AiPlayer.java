package IABomberMan;

import Etat.Direction;
import fonctionnaliteInitial.GameController;
import Joueur.JavaFXPlayer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.Random;
import java.util.List;

/**
 * Classe représentant un joueur contrôlé par l'intelligence artificielle.
 * <p>
 * Cette classe implémente le comportement d'un joueur IA qui peut se déplacer de façon autonome,
 * poser des bombes et réagir à son environnement dans le jeu Bomberman.
 * </p>
 */
public class AiPlayer {
    /** Identifiant unique du joueur IA */
    private int playerId;
    
    /** Contrôleur de jeu permettant d'interagir avec le monde du jeu */
    private GameController gameController;
    
    /** Générateur de nombres aléatoires pour les décisions de l'IA */
    private Random random;
    
    /** Timeline contrôlant les actions périodiques de l'IA */
    private Timeline aiTimeline;
    
    /** Indique si l'IA est actuellement active */
    private boolean isActive;
    
    /** Indique si l'IA vient de poser une bombe (pour déclencher une fuite) */
    private boolean justPlacedBomb = false;

    /** Probabilité que l'IA décide de se déplacer (90%) */
    private static final double MOVE_PROBABILITY = 0.9;
    
    /** Probabilité que l'IA décide de poser une bombe (10%) */
    private static final double BOMB_PROBABILITY = 0.1;
    
    /** Intervalle de base entre deux actions de l'IA (en millisecondes) */
    private static final double ACTION_INTERVAL = 800;

    /**
     * Constructeur de la classe AiPlayer.
     * 
     * @param playerId Identifiant unique du joueur contrôlé par l'IA
     * @param gameController Contrôleur de jeu utilisé pour interagir avec l'environnement
     */
    public AiPlayer(int playerId, GameController gameController) {
        this.playerId = playerId;
        this.gameController = gameController;
        this.random = new Random();
        this.isActive = true;

        initializeAI();
    }

    /**
     * Initialise la timeline qui contrôlera les actions périodiques de l'IA.
     */
    private void initializeAI() {
        aiTimeline = new Timeline(new KeyFrame(
                Duration.millis(ACTION_INTERVAL + random.nextInt(400)),
                e -> performAIAction()
        ));
        aiTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Démarre l'exécution de l'IA.
     * <p>
     * L'IA commencera à prendre des décisions et à effectuer des actions
     * de manière autonome à intervalles réguliers.
     * </p>
     */
    public void startAI() {
        if (isActive && aiTimeline != null) {
            aiTimeline.play();
            System.out.println("🤖 IA Joueur " + playerId + " activée");
        }
    }

    /**
     * Arrête l'exécution de l'IA.
     * <p>
     * L'IA cessera de prendre des décisions et d'effectuer des actions.
     * </p>
     */
    public void stopAI() {
        if (aiTimeline != null) {
            aiTimeline.stop();
        }
        isActive = false;
        System.out.println("🛑 IA Joueur " + playerId + " désactivée");
    }

    /**
     * Effectue un mouvement d'évasion lorsque l'IA est en danger.
     * <p>
     * Cette méthode est notamment utilisée après avoir posé une bombe pour
     * s'éloigner de la zone de danger.
     * </p>
     */
    private void performEscapeMovement() {
        JavaFXPlayer aiPlayer = gameController.getPlayerById(playerId);

        if (aiPlayer == null || !aiPlayer.isAlive()) {
            return;
        }

        List<Direction> safeDirs = gameController.getSafeDirections(aiPlayer);

        if (!safeDirs.isEmpty()) {
            // 🔍 Choisir une direction libre pour fuir
            Direction escapeDir = safeDirs.get(random.nextInt(safeDirs.size()));
            String directionStr = escapeDir.name();

            gameController.handleAIMovement(playerId, directionStr, true);

            Timeline stopMovement = new Timeline(new KeyFrame(
                    Duration.millis(100 + random.nextInt(100)), // 🔍 plus rapide que d'habitude
                    e -> gameController.handleAIMovement(playerId, directionStr, false)
            ));
            stopMovement.play();

            System.out.println("🏃💨 IA " + playerId + " fuit vers " + directionStr);
        } else {
            System.out.println("⚠️ IA " + playerId + " n'a nulle part où fuir !");
        }
    }

    /**
     * Méthode principale qui détermine et exécute l'action de l'IA.
     * <p>
     * Cette méthode est appelée périodiquement par la timeline et constitue
     * le cœur du comportement de l'IA.
     * </p>
     */
    private void performAIAction() {
        if (!isActive || gameController == null) {
            return;
        }

        try {
            // 🔍 Si une bombe vient d'être posée, priorité à la fuite !
            if (justPlacedBomb) {
                performEscapeMovement(); // méthode à ajouter ci-dessous
                justPlacedBomb = false;  // Reset pour la prochaine action
            } else {
                double actionChoice = random.nextDouble();

                if (actionChoice < MOVE_PROBABILITY) {
                    performSmartMovement(); // méthode existante qui bouge vers cases libres
                } else if (actionChoice < MOVE_PROBABILITY + BOMB_PROBABILITY) {
                    placeBomb();
                }
            }

            adjustNextActionDelay();

        } catch (Exception e) {
            System.out.println("❌ Erreur IA Joueur " + playerId + ": " + e.getMessage());
        }
    }

    /**
     * Effectue un mouvement intelligent en choisissant une direction sûre.
     * <p>
     * L'IA sélectionne aléatoirement une direction parmi celles qui ne sont pas bloquées.
     * </p>
     */
    private void performSmartMovement() {
        JavaFXPlayer aiPlayer = gameController.getPlayerById(playerId);

        if (aiPlayer == null || !aiPlayer.isAlive()) {
            System.out.println("⚠️ IA " + playerId + " non valide ou morte.");
            return;
        }

        List<Direction> safeDirs = gameController.getSafeDirections(aiPlayer);

        if (!safeDirs.isEmpty()) {
            Direction chosenDir = safeDirs.get(random.nextInt(safeDirs.size()));

            String directionStr = chosenDir.name(); // "UP", "DOWN", "LEFT", "RIGHT"

            gameController.handleAIMovement(playerId, directionStr, true);

            Timeline stopMovement = new Timeline(new KeyFrame(
                    Duration.millis(200 + random.nextInt(300)),
                    e -> gameController.handleAIMovement(playerId, directionStr, false)
            ));
            stopMovement.play();

            System.out.println("🤖 Joueur " + playerId + " se déplace vers " + directionStr);
        } else {
            System.out.println("🤖 IA " + playerId + " reste immobile (aucune case libre)");
        }
    }

    /**
     * Fait poser une bombe par l'IA et marque l'état pour déclencher une fuite.
     */
    private void placeBomb() {
        gameController.handleAIBombPlacement(playerId);
        justPlacedBomb = true; // 🔍 Signale qu'une bombe a été posée pour fuir ensuite
        System.out.println("💣 Joueur " + playerId + " pose une bombe");
    }

    /**
     * Ajuste l'intervalle avant la prochaine action de l'IA.
     * <p>
     * Introduit une variation aléatoire dans le timing des actions pour
     * rendre le comportement moins prévisible.
     * </p>
     */
    private void adjustNextActionDelay() {
        double newInterval = ACTION_INTERVAL + random.nextInt(600) - 300;
        newInterval = Math.max(300, newInterval);

        aiTimeline.stop();
        aiTimeline = new Timeline(new KeyFrame(
                Duration.millis(newInterval),
                e -> performAIAction()
        ));
        aiTimeline.setCycleCount(Timeline.INDEFINITE);
        aiTimeline.play();
    }

    /**
     * Vérifie si l'IA est actuellement active.
     * 
     * @return true si l'IA est active, false sinon
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Retourne l'identifiant du joueur contrôlé par cette IA.
     * 
     * @return L'identifiant unique du joueur
     */
    public int getPlayerId() {
        return playerId;
    }
}