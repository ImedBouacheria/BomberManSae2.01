package IABomberMan;

import fonctionnaliteInitial.GameController;

import java.util.HashMap;
import java.util.Map;

public class AIManager {
    private Map<Integer, AiPlayer> aiPlayers;
    private GameController gameController;

    public AIManager(GameController gameController) {
        this.gameController = gameController;
        this.aiPlayers = new HashMap<>();
    }

    public void addAIPlayer(int playerId) {
        AiPlayer aiPlayer = new AiPlayer(playerId, gameController);
        aiPlayers.put(playerId, aiPlayer);
        System.out.println("🤖 IA ajoutée pour le joueur " + playerId);
    }

    public void startAllAI() {
        for (AiPlayer aiPlayer : aiPlayers.values()) {
            aiPlayer.startAI();
        }
        System.out.println("🚀 Toutes les IA ont été démarrées");
    }

    public void stopAllAI() {
        for (AiPlayer aiPlayer : aiPlayers.values()) {
            aiPlayer.stopAI();
        }
        System.out.println("🛑 Toutes les IA ont été arrêtées");
    }

    public void removeAIPlayer(int playerId) {
        AiPlayer aiPlayer = aiPlayers.get(playerId);
        if (aiPlayer != null) {
            aiPlayer.stopAI();
            aiPlayers.remove(playerId);
            System.out.println("🗑️ IA supprimée pour le joueur " + playerId);
        }
    }

    public boolean hasAIPlayer(int playerId) {
        return aiPlayers.containsKey(playerId);
    }

    public void pauseAllAI() {
        for (AiPlayer aiPlayer : aiPlayers.values()) {
            aiPlayer.stopAI();
        }
    }

    public void resumeAllAI() {
        for (AiPlayer aiPlayer : aiPlayers.values()) {
            aiPlayer.startAI();
        }
    }
}