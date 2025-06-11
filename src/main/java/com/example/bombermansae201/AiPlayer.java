package com.example.bombermansae201;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class AiPlayer {
    private int playerId;
    private GameController gameController;
    private Random random;
    private Timeline aiTimeline;
    private boolean isActive;

    // Paramètres de comportement IA
    private static final double MOVE_PROBABILITY = 0.7;  // 70% de chance de bouger
    private static final double BOMB_PROBABILITY = 0.3;  // 30% de chance de poser une bombe
    private static final double ACTION_INTERVAL = 800;   // Action toutes les 800ms

    public AiPlayer(int playerId, GameController gameController) {
        this.playerId = playerId;
        this.gameController = gameController;
        this.random = new Random();
        this.isActive = true;

        initializeAI();
    }

    private void initializeAI() {
        // Timeline pour exécuter les actions IA périodiquement
        aiTimeline = new Timeline(new KeyFrame(
                Duration.millis(ACTION_INTERVAL + random.nextInt(400)), // Interval variable pour plus de réalisme
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

    private void performAIAction() {
        if (!isActive || gameController == null) {
            return;
        }

        try {
            // Décision aléatoire : bouger ou poser une bombe
            double actionChoice = random.nextDouble();

            if (actionChoice < MOVE_PROBABILITY) {
                // Mouvement aléatoire
                performRandomMovement();
            } else if (actionChoice < MOVE_PROBABILITY + BOMB_PROBABILITY) {
                // Poser une bombe
                placeBomb();
            }

            // Petite pause aléatoire avant la prochaine action
            adjustNextActionDelay();

        } catch (Exception e) {
            System.out.println("❌ Erreur IA Joueur " + playerId + ": " + e.getMessage());
        }
    }

    private void performRandomMovement() {
        // Directions possibles : HAUT, BAS, GAUCHE, DROITE
        String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
        String randomDirection = directions[random.nextInt(directions.length)];

        // Simuler l'appui et le relâchement de touche
        gameController.handleAIMovement(playerId, randomDirection, true);

        // Arrêter le mouvement après un court délai
        Timeline stopMovement = new Timeline(new KeyFrame(
                Duration.millis(200 + random.nextInt(300)),
                e -> gameController.handleAIMovement(playerId, randomDirection, false)
        ));
        stopMovement.play();

        System.out.println("🤖 Joueur " + playerId + " bouge vers " + randomDirection);
    }

    private void placeBomb() {
        gameController.handleAIBombPlacement(playerId);
        System.out.println("💣 Joueur " + playerId + " pose une bombe");
    }

    private void adjustNextActionDelay() {
        // Varier l'intervalle pour un comportement plus naturel
        double newInterval = ACTION_INTERVAL + random.nextInt(600) - 300;
        newInterval = Math.max(300, newInterval); // Minimum 300ms

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