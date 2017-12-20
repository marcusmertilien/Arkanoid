package csc413_arkanoid_team3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;

import java.util.Observer;
import java.util.Observable;
import java.awt.event.KeyEvent;


public class Player extends Ship implements Observer {

    private HashMap<Controls, Boolean> buttonStates;
    private HashMap<Integer, Controls> controlMap;

    public Player(
        int x,
        int y,
        HashMap<Integer, Controls> controls
    ) {
        super(x, y);

        _initControls(controls);
    }

    private void _initControls(HashMap<Integer, Controls> controls) {
        buttonStates = new HashMap<Controls, Boolean>();
        controlMap = controls;

        // Create controls mapping.
        for (Controls c : controlMap.values()) {
            buttonStates.put(c, false);
        }
    }

    public void update() {
        _updatePosition();
    }

    private void _updatePosition() {
        // Store previous location.
        previousX = x;
        previousY = y;

        // Set x and y speeds
        xSpeed = buttonStates.get(Controls.LEFT) ? -speed :
                 buttonStates.get(Controls.RIGHT) ? speed :
                 0;

        // Update location.
        x += xSpeed;
    }

    @Override
    public void update(Observable obj, Object e) {

        // onKey event
        if (e instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) e;
            int keyCode = ke.getKeyCode();
            int keyId = ke.getID();
            Controls buttonPressed = controlMap.get(keyCode);

            // Ensure the key pressed is one we're interested in...
            if (buttonPressed == null) {
                // ... and if not, bail early.
                return;
            }

            // Switch on keyEvent type
            switch (keyId) {
                case KeyEvent.KEY_PRESSED:
                    buttonStates.put(buttonPressed, true);
                    break;
                case KeyEvent.KEY_RELEASED:
                    buttonStates.put(buttonPressed, false);
                    break;
            }
        }
        
    }

    public HashMap<Controls, Boolean> getButtonStates(){
       return this.buttonStates;
    }
    
    
    
}
