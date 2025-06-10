package com.example.bombermansae201;

/**
 * Classe abstraite représentant tous les objets du jeu
 * (joueurs, bombes, explosions, murs, etc.)
 */
public abstract class GameObject {

    protected int x;    // Position X dans la grille
    protected int y;    // Position Y dans la grille

    /**
     * Constructeur par défaut
     */
    public GameObject() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Constructeur avec position
     * @param x Position X
     * @param y Position Y
     */
    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Méthode abstraite de mise à jour de l'objet
     * Doit être implémentée par chaque sous-classe
     */
    public abstract void update();

    /**
     * Obtient la position X
     * @return Position X
     */
    public int getX() {
        return x;
    }

    /**
     * Définit la position X
     * @param x Nouvelle position X
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Obtient la position Y
     * @return Position Y
     */
    public int getY() {
        return y;
    }

    /**
     * Définit la position Y
     * @param y Nouvelle position Y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Définit la position X et Y
     * @param x Nouvelle position X
     * @param y Nouvelle position Y
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}