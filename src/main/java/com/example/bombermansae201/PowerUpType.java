package com.example.bombermansae201;

/**
 * Énumération représentant les différents types de power-ups
 * disponibles dans le jeu Bomberman
 */
public enum PowerUpType {
    BOMB_COUNT(
            "💣 Bombes +1",
            "Augmente le nombre de bombes disponibles",
            "💣",
            100
    ),
    BOMB_POWER(
            "💥 Puissance +1",
            "Augmente la puissance d'explosion des bombes",
            "💥",
            150
    ),
    SPEED(
            "⚡ Vitesse +1",
            "Augmente la vitesse de déplacement du joueur",
            "⚡",
            120
    ),
    LIFE(
            "❤️ Vie +1",
            "Ajoute une vie supplémentaire au joueur",
            "❤️",
            200
    );

    private final String displayName;
    private final String description;
    private final String emoji;
    private final int scoreValue;

    /**
     * Constructeur de l'énumération
     * @param displayName Nom affiché du power-up
     * @param description Description de l'effet
     * @param emoji Émoji représentant le power-up
     * @param scoreValue Points accordés lors de la collecte
     */
    PowerUpType(String displayName, String description, String emoji, int scoreValue) {
        this.displayName = displayName;
        this.description = description;
        this.emoji = emoji;
        this.scoreValue = scoreValue;
    }

    /**
     * Retourne le nom d'affichage du power-up
     * @return Le nom d'affichage
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Retourne la description de l'effet
     * @return La description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retourne l'émoji du power-up
     * @return L'émoji
     */
    public String getEmoji() {
        return emoji;
    }

    /**
     * Retourne la valeur en points du power-up
     * @return La valeur en points
     */
    public int getScoreValue() {
        return scoreValue;
    }

    /**
     * Retourne le nom complet avec émoji
     * @return Nom complet avec émoji
     */
    public String getFullDisplayName() {
        return emoji + " " + displayName;
    }

    /**
     * Retourne la couleur associée au power-up (pour l'interface)
     * @return Code couleur hexadécimal
     */
    public String getColorCode() {
        switch (this) {
            case BOMB_COUNT: return "#FF8800"; // Orange
            case BOMB_POWER: return "#FF0000"; // Rouge
            case SPEED: return "#00FFFF"; // Cyan
            case LIFE: return "#FF69B4"; // Rose
            default: return "#FFFFFF"; // Blanc par défaut
        }
    }

    /**
     * Retourne la rareté du power-up
     * @return Niveau de rareté (1=commun, 5=très rare)
     */
    public int getRarity() {
        switch (this) {
            case BOMB_COUNT: return 2; // Assez commun
            case BOMB_POWER: return 3; // Moyen
            case SPEED: return 3; // Moyen
            case LIFE: return 5; // Très rare
            default: return 1;
        }
    }

    /**
     * Retourne la probabilité d'apparition du power-up
     * @return Probabilité entre 0.0 et 1.0
     */
    public double getSpawnProbability() {
        switch (this) {
            case BOMB_COUNT: return 0.4; // 40%
            case BOMB_POWER: return 0.3; // 30%
            case SPEED: return 0.2; // 20%
            case LIFE: return 0.1; // 10%
            default: return 0.1;
        }
    }

    /**
     * Vérifie si le power-up affecte les bombes
     * @return true si le power-up concerne les bombes
     */
    public boolean isBombRelated() {
        return this == BOMB_COUNT || this == BOMB_POWER;
    }

    /**
     * Vérifie si le power-up affecte le mouvement
     * @return true si le power-up concerne le mouvement
     */
    public boolean isMovementRelated() {
        return this == SPEED;
    }

    /**
     * Vérifie si le power-up affecte la survie
     * @return true si le power-up concerne la survie
     */
    public boolean isSurvivalRelated() {
        return this == LIFE;
    }

    /**
     * Retourne tous les power-ups disponibles
     * @return Tableau de tous les power-ups
     */
    public static PowerUpType[] getAllPowerUps() {
        return values();
    }

    /**
     * Retourne un power-up aléatoire selon les probabilités
     * @return Un power-up choisi aléatoirement
     */
    public static PowerUpType getRandomPowerUp() {
        double random = Math.random();
        double cumulative = 0.0;

        for (PowerUpType type : values()) {
            cumulative += type.getSpawnProbability();
            if (random <= cumulative) {
                return type;
            }
        }

        return BOMB_COUNT; // Fallback
    }

    /**
     * Retourne un power-up aléatoire avec probabilités égales
     * @return Un power-up choisi aléatoirement
     */
    public static PowerUpType getRandomPowerUpUniform() {
        PowerUpType[] types = values();
        return types[(int) (Math.random() * types.length)];
    }

    /**
     * Trouve un power-up par son nom d'affichage
     * @param displayName Le nom d'affichage à rechercher
     * @return Le power-up correspondant ou null si non trouvé
     */
    public static PowerUpType fromDisplayName(String displayName) {
        for (PowerUpType type : values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Retourne une représentation textuelle du power-up
     * @return Représentation textuelle
     */
    @Override
    public String toString() {
        return String.format("PowerUpType{name='%s', emoji='%s', score=%d, rarity=%d}",
                displayName, emoji, scoreValue, getRarity());
    }

    /**
     * Retourne une description complète du power-up
     * @return Description complète
     */
    public String getFullDescription() {
        return String.format("%s\n%s\n🏆 Points: %d\n⭐ Rareté: %d/5",
                getFullDisplayName(), description, scoreValue, getRarity());
    }
}