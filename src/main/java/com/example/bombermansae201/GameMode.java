package com.example.bombermansae201;

/**
 * Énumération représentant les différents modes de jeu
 * pour la gestion des bombes
 */
public enum GameMode {
    LIMITED_BOMBS("Bombes Limitées", "Les joueurs ont un nombre limité de bombes (3 par défaut)"),
    INFINITE_BOMBS("Bombes Infinies", "Les joueurs peuvent placer un nombre illimité de bombes");

    private final String displayName;
    private final String description;

    GameMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Alterne entre les modes de jeu
     */
    public GameMode toggle() {
        return this == LIMITED_BOMBS ? INFINITE_BOMBS : LIMITED_BOMBS;
    }
}