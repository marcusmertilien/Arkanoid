package csc413_arkanoid_team3;

import java.awt.event.KeyEvent;
import java.util.Observable;


public class EventManager extends Observable {

    // Class fields
    // ============

    private static EventManager instance; // the singleton instance


    // Constructors
    // ============

    protected EventManager() { }

    public static EventManager getInstance() {
        // Create or return the singleton.
        if (instance == null) {
            instance = new EventManager();
        }

        return instance;
    }


    // Public API
    // ==========

    public void keyPressed(KeyEvent e) {
        setChanged();
        notifyObservers(e);
    }

    public void keyReleased(KeyEvent e) {
        setChanged();
        notifyObservers(e);
    }

    public void keyTyped(KeyEvent e) {
        setChanged();
        notifyObservers(e);
    }
}
