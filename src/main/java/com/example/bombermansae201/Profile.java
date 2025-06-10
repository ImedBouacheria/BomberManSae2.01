package com.example.bombermansae201;

import javafx.scene.paint.Color;

/**
 * Classe représentant un profil de joueur
 */
public class Profile {
    private String firstName;
    private String lastName;
    private String colorName;
    private Color color;
    private int gamesPlayed;
    private int gamesWon;
    private int totalScore;

    /**
     * Constructeur principal
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
     * Constructeur complet avec statistiques
     */
    public Profile(String firstName, String lastName, String colorName, int gamesPlayed, int gamesWon, int totalScore) {
        this(firstName, lastName, colorName);
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.totalScore = totalScore;
    }

    /**
     * Convertit le nom de couleur en objet Color
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
     * Met à jour les statistiques après une partie
     */
    public void updateStats(boolean won, int score) {
        this.gamesPlayed++;
        if (won) {
            this.gamesWon++;
        }
        this.totalScore += score;
    }

    /**
     * Calcule le pourcentage de victoires
     */
    public double getWinRate() {
        if (gamesPlayed == 0) return 0.0;
        return (double) gamesWon / gamesPlayed * 100.0;
    }

    /**
     * Retourne le nom complet du joueur
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Retourne une représentation courte du profil
     */
    public String getDisplayName() {
        return firstName + " " + lastName.charAt(0) + ".";
    }

    // Getters et Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
        this.color = convertStringToColor(colorName);
    }

    public Color getColor() {
        return color;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s) - %d parties, %d victoires, %d points",
                firstName, lastName, colorName, gamesPlayed, gamesWon, totalScore);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Profile profile = (Profile) obj;
        return firstName.equals(profile.firstName) && lastName.equals(profile.lastName);
    }

    @Override
    public int hashCode() {
        return firstName.hashCode() + lastName.hashCode();
    }
}
