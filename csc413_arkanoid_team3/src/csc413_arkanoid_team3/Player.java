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

    // Class constants
    // ===============

    private final static int FIRING_SPEED = 30;
    private static final int DEFAULT_LIVES = 3;
    private static final int DEFAULT_SCORE = 0;

    // Class fields
    // ============

    private HashMap<Controls, Boolean> buttonStates; // current pressed buttons
    private HashMap<Integer, Controls> controlMap;   // map of keys to player controls

    private int lives;
    public int score;
    private int highScore;

    // PowerUp Attributes
    private boolean lazerActive;
    private int shotCooldown;

    // Constructors
    // ============

    public Player(int x, int y, HashMap<Integer, Controls> controls) {
        super(x, y);

        this.lives = DEFAULT_LIVES;
        this.score = DEFAULT_SCORE;
        this.highScore = DEFAULT_SCORE;
        this.shotCooldown = FIRING_SPEED;
        

        _initControls(controls);
    }

    private void _initControls(HashMap<Integer, Controls> controls) {
        buttonStates = new HashMap<Controls, Boolean>();
        controlMap = controls;

        // Create control mapping.
        for (Controls c : controlMap.values()) {
            buttonStates.put(c, false);
        }
    }

    // Public API
    // ==========

    public void update(ArrayList<Projectile> shots) {
        _updatePosition();
        _updateShoot(shots);
        _updateHighScore();
    }
    
    private void _updateHighScore(){
        if(score>highScore){
            highScore = score;
        }
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
    
    private void _updateShoot(ArrayList<Projectile> shots) {
        if (lazerActive) {
            if (shotCooldown <= 0 && buttonStates.get(Controls.SHOOT)) {

                int projSize = Projectile.PROJECTILE_SIZE;
                int projX = x + (this.width-projSize)/2;
                int projY = this.y;

                // Add new shot.
                shots.add(new Projectile(projX, projY, xSpeed, ySpeed));

                // Reset shot cooldown.
                shotCooldown = FIRING_SPEED;
            }
        }

        shotCooldown--;
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
    
    public void powerUp(PowerUp p) {
        switch(p.type) {
            case LAZER:
                lazerActive = true;
                break;
            case SLOW:
                lazerActive = false;
                if (speed > 2) speed--;
                break;
            case SPEED_UP:
                lazerActive = false;
                if (speed > 6) this.speed++;
                break;
            case PLAYER:
                lives++;
                break;
        }
    }

    public int getLives() {
        return lives;
    }

    public int incrementLives() {
        return ++lives;
    }

    public int decrementLives() {
        return --lives;
    }

    public void reset() {
        this.score = DEFAULT_SCORE;
        this.lives = DEFAULT_LIVES;
    }
    
    public int getHighScore(){
        return highScore;
    }

}
