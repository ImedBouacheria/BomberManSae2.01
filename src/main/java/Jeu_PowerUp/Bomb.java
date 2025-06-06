package Jeu_PowerUp;
import Jeu_Perso_Non_Bot.Perso_Non_Bot;

import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;

public class Bomb {
    public interface BombListener {
        void onBombExploded(Bomb bomb);
        void onBombPlaced(Bomb bomb);
    }

    private int x, y;
    private Perso_Non_Bot owner;
    private BufferedImage normalSprite;
    private BufferedImage warningSprite;
    private BufferedImage explosionSprite;
    private BombState state;
    private Timer explosionTimer;
    private boolean exploded;
    private int explosionRange;
    private List<BombListener> listeners = new ArrayList<>();

    public Bomb(int x, int y, Perso_Non_Bot owner, int explosionRange) {
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.explosionRange = explosionRange;
        this.state = BombState.NORMAL;
        this.exploded = false;
        notifyBombPlaced();
        startCountdown();
    }

    public void addListener(BombListener listener) {
        listeners.add(listener);
    }

    public void removeListener(BombListener listener) {
        listeners.remove(listener);
    }

    private void notifyBombPlaced() {
        for (BombListener listener : listeners) {
            listener.onBombPlaced(this);
        }
    }

    private void notifyBombExploded() {
        for (BombListener listener : listeners) {
            listener.onBombExploded(this);
        }
    }

    private void startCountdown() {
        explosionTimer = new Timer();
        explosionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                explode();
            }
        }, 2000); // Explose après 2 secondes
    }

    private void explode() {
        state = BombState.EXPLODING;
        exploded = true;
        owner.setCanPlaceBomb(true);
        notifyBombExploded();

        // Après l'explosion, supprimer la bombe après un court délai
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                state = BombState.DONE;
            }
        }, 500);
    }

    public BufferedImage getCurrentSprite() {
        switch (state) {
            case NORMAL: return normalSprite;
            case WARNING: return warningSprite;
            case EXPLODING: return explosionSprite;
            default: return null;
        }
    }

    public boolean hasExploded() {
        return exploded;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public BombState getState() { return state; }
    public int getExplosionRange() { return explosionRange; }

    // Zone d'explosion
    public boolean isInExplosionZone(int targetX, int targetY) {
        return (targetX == x && Math.abs(targetY - y) <= explosionRange) || // Vertical
                (targetY == y && Math.abs(targetX - x) <= explosionRange);   // Horizontal
    }
}

enum BombState {
    NORMAL, WARNING, EXPLODING, DONE
}