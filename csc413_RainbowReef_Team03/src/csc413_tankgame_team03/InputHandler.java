package csc413_tankgame_team03;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class InputHandler implements KeyListener {

    private static InputHandler instance = null;
    private static EventManager eventManager;

    // Constructors
    // ============

    protected InputHandler() {
        this.eventManager = EventManager.getInstance();
    }

    public static InputHandler getInstance() {
        if (instance == null) {
            instance = new InputHandler();
        }

        return instance;
    }

    // KeyListener interface
    // =====================

    @Override
    public void keyTyped(KeyEvent e) {
        this.eventManager.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.eventManager.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.eventManager.keyReleased(e);
    }

}
