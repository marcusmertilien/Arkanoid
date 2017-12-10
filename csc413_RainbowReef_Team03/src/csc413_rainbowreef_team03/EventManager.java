package csc413_rainbowreef_team03;

import java.awt.event.KeyEvent;
import java.util.Observable;


public class EventManager extends Observable {

    private static EventManager instance = null;


    // Constructors
    // ============

    protected EventManager() { }

    public static EventManager getInstance() {
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

         // TODO: this isn't in the right place yet - only for testing
        if (e.getKeyCode() == KeyEvent.VK_P) {
            SoundManager.getInstance().stopSoundtrack();
        }
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
