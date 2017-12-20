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

    private final static  int FIRING_SPEED = 30;
    //PowerUp Attributes
    private boolean fire;
    private int shotCooldown;
    
    private int lives;
    
    // Class fields
    // ============

    private HashMap<Controls, Boolean> buttonStates; // current pressed buttons
    private HashMap<Integer, Controls> controlMap;   // map of keys to player controls


    // Constructors
    // ============

    public Player(int x, int y, HashMap<Integer, Controls> controls) {
        super(x, y);
        lives = 3;
        _initControls(controls);
        _primeCanons();
    }

    private void _initControls(HashMap<Integer, Controls> controls) {
        buttonStates = new HashMap<Controls, Boolean>();
        controlMap = controls;

        // Create control mapping.
        for (Controls c : controlMap.values()) {
            buttonStates.put(c, false);
        }
    }
    
    private void _primeCanons(){
        shotCooldown = FIRING_SPEED; // Shot cooldown
        fire = true;
    }


    // Public API
    // ==========

    public void update(ArrayList<Projectile> shots) {
        _updatePosition();
        _updateShoot(shots);
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
    
    private void _updateShoot(ArrayList<Projectile> shots){
        if(fire){
            if (shotCooldown <= 0 && buttonStates.get(Controls.SHOOT)) {

                int projX;
                int projY;
                int projXSpeed;
                int projYSpeed;
                int projSize = Projectile.PROJECTILE_SIZE;

                projX = x + (this.width-projSize)/2;
                projY = this.y;

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
  
    public HashMap<Controls, Boolean> getButtonStates(){
       return this.buttonStates;
    }
    
    public void powerUp(PowerUp p){
        switch(p.type){
            case LAZER:
                this.fire = true;
                break;
            case EXTEND:
                break;
            case SLOW:
                if(this.speed>1){
                    this.speed = this.speed--;
                }
                break;
            case CATCH:
                break;
            case DISRUPT:
                this.fire = false;
                this.speed = super.getSpeed();
                break;
            case TWIN:
                break;
            case NEWDISRUPT:
                break;
            case PLAYER:
                lives++;
                break;
            case REDUCE:                
                break;
            
        }
    }
    
    public int getLives(){
        return lives;
    }
    public void setLives(int change){
        lives = change;
    }
}
