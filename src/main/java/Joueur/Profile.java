package Joueur;

import javafx.scene.paint.Color;

/**
 * La classe Profile représente un profil de joueur dans le jeu Bomberman.
 * <p>
 * Cette classe gère les informations personnelles du joueur (nom, prénom, couleur préférée) 
 * ainsi que ses statistiques de jeu (parties jouées, victoires, score total).
 * Les profils sont utilisés pour identifier les joueurs et suivre leurs performances
 * au fil du temps.
 * </p>
 * 
 * @author [Auteur du projet]
 * @version 1.0
 * @since 1.0
 */
public class Profile {
    
    /**
     * Le prénom du joueur.
     */
    private String firstName;
    
    /**
     * Le nom de famille du joueur.
     */
    private String lastName;
    
    /**
     * Le nom de la couleur préférée du joueur en format texte.
     */
    private String colorName;
    
    /**
     * L'objet Color JavaFX correspondant à la couleur préférée du joueur.
     */
    private Color color;
    
    /**
     * Le nombre total de parties jouées par le joueur.
     */
    private int gamesPlayed;
    
    /**
     * Le nombre total de parties gagnées par le joueur.
     */
    private int gamesWon;
    
    /**
     * Le score total accumulé par le joueur dans toutes ses parties.
     */
    private int totalScore;

    /**
     * Constructeur principal pour créer un nouveau profil de joueur.
     * <p>
     * Initialise un nouveau profil avec des statistiques à zéro.
     * </p>
     *
     * @param firstName  Le prénom du joueur
     * @param lastName   Le nom de famille du joueur
     * @param colorName  Le nom de la couleur préférée du joueur
     */
    public Profile(String firstName, String lastName, String colorName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.colorName = colorName;
        this.color = convertStringToColor(colorName);
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.totalScore = 0;
    }

    /**
     * Constructeur complet avec statistiques pour charger un profil existant.
     * <p>
     * Utilisé principalement lors du chargement des profils depuis un fichier
     * de sauvegarde par le ProfileManager.
     * </p>
     *
     * @param firstName    Le prénom du joueur
     * @param lastName     Le nom de famille du joueur
     * @param colorName    Le nom de la couleur préférée du joueur
     * @param gamesPlayed  Le nombre de parties jouées
     * @param gamesWon     Le nombre de parties gagnées
     * @param totalScore   Le score total accumulé
     */
    public Profile(String firstName, String lastName, String colorName, int gamesPlayed, int gamesWon, int totalScore) {
        this(firstName, lastName, colorName);
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.totalScore = totalScore;
    }

    /**
     * Convertit le nom d'une couleur en objet Color JavaFX.
     * <p>
     * Prend en charge les couleurs principales en français : rouge, bleu, vert, jaune,
     * orange, violet, rose et cyan. Si la couleur n'est pas reconnue, retourne blanc.
     * </p>
     *
     * @param colorName  Le nom de la couleur en français
     * @return           L'objet Color JavaFX correspondant
     */
    private Color convertStringToColor(String colorName) {
        switch (colorName.toLowerCase()) {
            case "rouge": return Color.RED;
            case "bleu": return Color.BLUE;
            case "vert": return Color.GREEN;
            case "jaune": return Color.YELLOW;
            case "orange": return Color.ORANGE;
            case "violet": return Color.PURPLE;
            case "rose": return Color.PINK;
            case "cyan": return Color.CYAN;
            default: return Color.WHITE;
        }
    }

    /**
     * Met à jour les statistiques du joueur après une partie.
     * <p>
     * Incrémente le compteur de parties jouées, ainsi que le compteur de victoires
     * si la partie a été gagnée. Ajoute également le score obtenu au score total.
     * </p>
     *
     * @param won    true si le joueur a gagné la partie, false sinon
     * @param score  Le score obtenu dans la partie
     */
    public void updateStats(boolean won, int score) {
        this.gamesPlayed++;
        if (won) {
            this.gamesWon++;
        }
        this.totalScore += score;
    }

    /**
     * Calcule le pourcentage de victoires du joueur.
     * <p>
     * Le taux est calculé en divisant le nombre de victoires par le nombre total
     * de parties jouées, puis en multipliant par 100 pour obtenir un pourcentage.
     * Retourne 0 si aucune partie n'a été jouée.
     * </p>
     *
     * @return  Le pourcentage de victoires (entre 0.0 et 100.0)
     */
    public double getWinRate() {
        if (gamesPlayed == 0) return 0.0;
        return (double) gamesWon / gamesPlayed * 100.0;
    }

    /**
     * Retourne le nom complet du joueur.
     * <p>
     * Concatène le prénom et le nom de famille avec un espace entre les deux.
     * </p>
     *
     * @return  Le nom complet du joueur (prénom + nom)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Retourne une représentation courte du nom du joueur.
     * <p>
     * Format : prénom + première lettre du nom suivie d'un point.
     * Par exemple : "Jean D."
     * </p>
     *
     * @return  Le nom court du joueur
     */
    public String getDisplayName() {
        return firstName + " " + lastName.charAt(0) + ".";
    }

    /**
     * Récupère le prénom du joueur.
     *
     * @return  Le prénom du joueur
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Modifie le prénom du joueur.
     *
     * @param firstName  Le nouveau prénom du joueur
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Récupère le nom de famille du joueur.
     *
     * @return  Le nom de famille du joueur
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Modifie le nom de famille du joueur.
     *
     * @param lastName  Le nouveau nom de famille du joueur
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Récupère le nom de la couleur préférée du joueur.
     *
     * @return  Le nom de la couleur en français
     */
    public String getColorName() {
        return colorName;
    }

    /**
     * Modifie la couleur préférée du joueur.
     * <p>
     * Met à jour à la fois le nom de la couleur et l'objet Color correspondant.
     * </p>
     *
     * @param colorName  Le nouveau nom de couleur en français
     */
    public void setColorName(String colorName) {
        this.colorName = colorName;
        this.color = convertStringToColor(colorName);
    }

    /**
     * Récupère l'objet Color JavaFX correspondant à la couleur préférée du joueur.
     *
     * @return  L'objet Color JavaFX
     */
    public Color getColor() {
        return color;
    }

    /**
     * Récupère le nombre de parties jouées par le joueur.
     *
     * @return  Le nombre total de parties jouées
     */
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    /**
     * Modifie le nombre de parties jouées par le joueur.
     *
     * @param gamesPlayed  Le nouveau nombre de parties jouées
     */
    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    /**
     * Récupère le nombre de parties gagnées par le joueur.
     *
     * @return  Le nombre total de parties gagnées
     */
    public int getGamesWon() {
        return gamesWon;
    }

    /**
     * Modifie le nombre de parties gagnées par le joueur.
     *
     * @param gamesWon  Le nouveau nombre de parties gagnées
     */
    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    /**
     * Récupère le score total accumulé par le joueur.
     *
     * @return  Le score total du joueur
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * Modifie le score total du joueur.
     *
     * @param totalScore  Le nouveau score total
     */
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    /**
     * Retourne une représentation textuelle du profil.
     * <p>
     * Inclut le nom complet, la couleur préférée et les statistiques de jeu.
     * </p>
     *
     * @return  Une chaîne formatée représentant le profil
     */
    @Override
    public String toString() {
        return String.format("%s %s (%s) - %d parties, %d victoires, %d points",
                firstName, lastName, colorName, gamesPlayed, gamesWon, totalScore);
    }

    /**
     * Compare ce profil avec un autre objet pour déterminer l'égalité.
     * <p>
     * Deux profils sont considérés égaux s'ils ont exactement le même prénom et le même nom de famille.
     * Les autres attributs (couleur, statistiques) ne sont pas pris en compte dans la comparaison.
     * </p>
     *
     * @param obj  L'objet à comparer avec ce profil
     * @return     true si les deux objets sont égaux, false sinon
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Profile profile = (Profile) obj;
        return firstName.equals(profile.firstName) && lastName.equals(profile.lastName);
    }

    /**
     * Génère un code de hachage pour ce profil.
     * <p>
     * Le code de hachage est basé uniquement sur le prénom et le nom de famille,
     * pour correspondre à l'implémentation de la méthode equals.
     * </p>
     *
     * @return  Le code de hachage du profil
     */
    @Override
    public int hashCode() {
        return firstName.hashCode() + lastName.hashCode();
    }
}