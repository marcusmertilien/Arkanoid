package csc413_arkanoid_team3;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class InputHandler implements KeyListener {

    // Class fields
    // ============

    private static InputHandler instance;      // the singleton instance
    private static EventManager eventManager;  // access to the event system


    // Constructors
    // ============

    protected InputHandler() {
        this.eventManager = EventManager.getInstance();
    }

    public static InputHandler getInstance() {
        // Create or return the singleton instance.
        if (instance == null) {
            instance = new InputHandler();
        }

        return instance;
    }


    // KeyListener interface
    // =====================

    @Override
    public void keyTyped(KeyEvent ev) {
        eventManager.keyTyped(ev);
    }

    @Override
    public void keyPressed(KeyEvent ev) {
        eventManager.keyPressed(ev);
    }

    @Override
    public void keyReleased(KeyEvent ev) {
        eventManager.keyReleased(ev);
    }

}
