package Etat;

/**
 * Énumération représentant les différents modes de jeu
 * pour la gestion des bombes dans Bomberman
 */
public enum GameMode {
    LIMITED_BOMBS(
            "🎯 Bombes Limitées",
            "Les joueurs commencent avec 10 bombes. Collectez des power-ups pour en avoir plus !",
            "🎯",
            3
    ),
    INFINITE_BOMBS(
            "♾️ Bombes Infinies",
            "Les joueurs peuvent placer un nombre illimité de bombes en permanence",
            "♾️",
            Integer.MAX_VALUE
    );

    private final String displayName;
    private final String description;
    private final String emoji;
    private final int defaultBombCount;

    /**
     * Constructeur de l'énumération
     * @param displayName Nom affiché du mode
     * @param description Description détaillée du mode
     * @param emoji Émoji représentant le mode
     * @param defaultBombCount Nombre de bombes par défaut (-1 pour infini)
     */
    GameMode(String displayName, String description, String emoji, int defaultBombCount) {
        this.displayName = displayName;
        this.description = description;
        this.emoji = emoji;
        this.defaultBombCount = defaultBombCount;
    }

    /**
     * Retourne le nom d'affichage du mode
     * @return Le nom d'affichage
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Retourne la description du mode
     * @return La description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retourne l'émoji du mode
     * @return L'émoji
     */
    public String getEmoji() {
        return emoji;
    }

    /**
     * Retourne le nombre de bombes par défaut
     * @return Le nombre de bombes par défaut
     */
    public int getDefaultBombCount() {
        return defaultBombCount;
    }

    /**
     * Vérifie si le mode a des bombes infinies
     * @return true si les bombes sont infinies
     */
    public boolean isInfinite() {
        return this == INFINITE_BOMBS;
    }

    /**
     * Vérifie si le mode a des bombes limitées
     * @return true si les bombes sont limitées
     */
    public boolean isLimited() {
        return this == LIMITED_BOMBS;
    }

    /**
     * Alterne entre les modes de jeu
     * @return Le mode opposé
     */
    public GameMode toggle() {
        return this == LIMITED_BOMBS ? INFINITE_BOMBS : LIMITED_BOMBS;
    }

    /**
     * Retourne le nom complet avec émoji
     * @return Nom complet avec émoji
     */
    public String getFullDisplayName() {
        return emoji + " " + displayName;
    }

    /**
     * Retourne une description courte du mode
     * @return Description courte
     */
    public String getShortDescription() {
        return this == INFINITE_BOMBS ? "Bombes illimitées" : "Max 3 bombes";
    }

    /**
     * Retourne la couleur associée au mode (pour l'interface)
     * @return Code couleur hexadécimal
     */
    public String getColorCode() {
        return this == INFINITE_BOMBS ? "#00FFAA" : "#FFAA00";
    }

    /**
     * Retourne la couleur de fond associée au mode
     * @return Code couleur de fond hexadécimal
     */
    public String getBackgroundColorCode() {
        return this == INFINITE_BOMBS ? "#004422" : "#442200";
    }

    /**
     * Retourne tous les modes disponibles
     * @return Tableau de tous les modes
     */
    public static GameMode[] getAllModes() {
        return values();
    }

    /**
     * Trouve un mode par son nom d'affichage
     * @param displayName Le nom d'affichage à rechercher
     * @return Le mode correspondant ou null si non trouvé
     */
    public static GameMode fromDisplayName(String displayName) {
        for (GameMode mode : values()) {
            if (mode.getDisplayName().equalsIgnoreCase(displayName)) {
                return mode;
            }
        }
        return null;
    }

    /**
     * Retourne une représentation textuelle du mode
     * @return Représentation textuelle
     */
    @Override
    public String toString() {
        return String.format("GameMode{name='%s', emoji='%s', bombs=%s}",
                displayName, emoji,
                isInfinite() ? "∞" : String.valueOf(defaultBombCount));
    }
}
