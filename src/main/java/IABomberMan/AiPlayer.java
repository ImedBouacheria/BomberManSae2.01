package IABomberMan;

import Etat.Direction;
import fonctionnaliteInitial.GameController;
import Joueur.JavaFXPlayer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.Random;
import java.util.List;

public class AiPlayer {
    private int playerId;
    private GameController gameController;
    private Random random;
    private Timeline aiTimeline;
    private boolean isActive;
    private boolean justPlacedBomb = false;

    // Paramètres de comportement IA
    private static final double MOVE_PROBABILITY = 0.9;  // 90% de chance de bouger
    private static final double BOMB_PROBABILITY = 0.1;  // 10% de chance de poser une bombe
    private static final double ACTION_INTERVAL = 800;   // Action toutes les 800ms

    public AiPlayer(int playerId, GameController gameController) {
        this.playerId = playerId;
        this.gameController = gameController;
        this.random = new Random();
        this.isActive = true;

        initializeAI();
    }

    private void initializeAI() {
        aiTimeline = new Timeline(new KeyFrame(
                Duration.millis(ACTION_INTERVAL + random.nextInt(400)),
                e -> performAIAction()
        ));
        aiTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void startAI() {
        if (isActive && aiTimeline != null) {
            aiTimeline.play();
            System.out.println("🤖 IA Joueur " + playerId + " activée");
        }
    }

    public void stopAI() {
        if (aiTimeline != null) {
            aiTimeline.stop();
        }
        isActive = false;
        System.out.println("🛑 IA Joueur " + playerId + " désactivée");
    }

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

    // ✅ Nouveau : IA choisit seulement des directions valides
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

    private void placeBomb() {
        gameController.handleAIBombPlacement(playerId);
        justPlacedBomb = true; // 🔍 Signale qu'une bombe a été posée pour fuir ensuite
        System.out.println("💣 Joueur " + playerId + " pose une bombe");
    }

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

    public boolean isActive() {
        return isActive;
    }

    public int getPlayerId() {
        return playerId;
    }
}
