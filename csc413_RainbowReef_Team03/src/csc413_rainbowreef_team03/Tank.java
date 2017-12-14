package csc413_rainbowreef_team03;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observer;
import java.util.Observable;
import javax.imageio.ImageIO;


public class Tank extends Actor implements Observer {

    // Constants
    private static final int TANK_SIZE = 30;
    private static final int TANK_SPEED = 2;
    private static  int FIRING_SPEED = 30;
    private static final int TOTAL_HEALTH = 100;
    private static final int TOTAL_LIVES = 3;

    // Tank color types
    public Colors color;
    public enum Colors {
        RED, GREEN
    }

    // Static image assets.
    private static BufferedImage redTank;
    private static BufferedImage greenTank;
    static {
        try {
            ClassLoader cl = Tank.class.getClassLoader();
            BufferedImage rawAsset;
            Image tempScaledImage;
            Graphics2D g2d;

            // Open and set red tank asset.
            rawAsset = ImageIO.read(cl.getResource(GameEngine.TANK_ASSET_PATH + "RedTank1.png"));
            tempScaledImage = rawAsset.getScaledInstance(TANK_SIZE, TANK_SIZE, Image.SCALE_SMOOTH);
            redTank = new BufferedImage(TANK_SIZE, TANK_SIZE, BufferedImage.TYPE_INT_ARGB);
            g2d = redTank.createGraphics();
            g2d.drawImage(tempScaledImage, 0, 0, null);
            g2d.dispose();

            // Open and set green tank asset.
            rawAsset = ImageIO.read(cl.getResource(GameEngine.TANK_ASSET_PATH + "GreenTank1.png"));
            tempScaledImage = rawAsset.getScaledInstance(TANK_SIZE, TANK_SIZE, Image.SCALE_SMOOTH);
            greenTank = new BufferedImage(TANK_SIZE, TANK_SIZE, BufferedImage.TYPE_INT_ARGB);
            g2d = greenTank.createGraphics();
            g2d.drawImage(tempScaledImage, 0, 0, null);
            g2d.dispose();
        } catch (Exception e) {
            System.out.print("No resources found\n");
        }
    }

    private HashMap<Controls, Boolean> buttonStates;
    private HashMap<Integer, Controls> controlMap;

    private int shotCooldown; // Shot cooldown
    private int originX;      // Point to reset tank to
    private int originY;      // Point to reset tank to

    protected int score;
    protected int health;
    protected int lives;
    protected int firingSpeed;
    protected int tankSpeed;


    // Constructors
    // ============
    public Tank(
        int x,
        int y,
        int speed,
        int size,
        HashMap<Integer, Controls> controls,
        Colors color
    ) {
        super(x, y, size, size, 0, 0, TANK_SPEED, 0);
        
        this.tankSpeed = TANK_SPEED;
        this.firingSpeed = FIRING_SPEED;
        this.score = 0;
        this.health = TOTAL_HEALTH;
        this.lives = TOTAL_LIVES;

        this.color = color;
        this.sprite = (this.color == Colors.RED) ? redTank : greenTank;

        this.originX = x;
        this.originY = y;

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


    // Public API
    // ==========
    public void update(ArrayList<Projectile> shots) {
        _updatePosition();
        _updateDirection();
        _updateShoot(shots);
    }

    private void _updatePosition() {
        // Store previous location.
        prevX = x;
        prevY = y;

        // Set x and y speeds
        xSpeed = buttonStates.get(Controls.LEFT) ? -speed :
                 buttonStates.get(Controls.RIGHT) ? speed :
                 0;

        ySpeed = buttonStates.get(Controls.UP) ? -speed :
                 buttonStates.get(Controls.DOWN) ? speed :
                 0;

        // Update location.
        x += xSpeed;
        y += ySpeed;

    }

    private void _updateDirection() {
        // Set tank's current direction.
        if (buttonStates.get(Controls.UP)) {
            direction = buttonStates.get(Controls.LEFT) ? 315 :
                        buttonStates.get(Controls.RIGHT) ? 45 :
                        0;
        } else if (buttonStates.get(Controls.DOWN)) {
            direction = buttonStates.get(Controls.LEFT) ? 225 :
                        buttonStates.get(Controls.RIGHT) ? 135 :
                        180;
        } else {
            direction = buttonStates.get(Controls.LEFT) ? 270 :
                        buttonStates.get(Controls.RIGHT) ? 90 :
                        direction;
        }
    }

    private void _updateShoot(ArrayList<Projectile> shots){

        if (shotCooldown <= 0 && buttonStates.get(Controls.SHOOT)) {

            int projX;
            int projY;
            int projXSpeed;
            int projYSpeed;
            int projSize = Projectile.PROJECTILE_SIZE;

            switch (direction) {
                case 0:
                    projX = x + (TANK_SIZE-projSize)/2;
                    projY = y;
                    projXSpeed = xSpeed;
                    projYSpeed = ySpeed;
                    break;

                case 45:
                    projX = x + (TANK_SIZE+projSize)/2;
                    projY = y;
                    projXSpeed = xSpeed;
                    projYSpeed = ySpeed;
                    break;

                case 90:
                    projX = x + TANK_SIZE;
                    projY = y + (TANK_SIZE-projSize)/2;
                    projXSpeed = xSpeed;
                    projYSpeed = 0;
                    break;

                case 135:
                    projX = x;
                    projY = y;
                    projXSpeed = xSpeed;
                    projYSpeed = ySpeed;
                    break;

                case 180:
                    projX = x + (TANK_SIZE-projSize)/2;
                    projY = y + TANK_SIZE;
                    projXSpeed = 0;
                    projYSpeed = ySpeed;
                    break;

                case 225:
                    projX = x + (TANK_SIZE+projSize)/2;
                    projY = y;
                    projXSpeed = xSpeed;
                    projYSpeed = ySpeed;
                    break;

                case 270:
                    projX = x;
                    projY = y + (TANK_SIZE-projSize)/2;
                    projXSpeed = xSpeed;
                    projYSpeed = 0;
                    break;

                case 315:
                    projX = x;
                    projY = y;
                    projXSpeed = xSpeed;
                    projYSpeed = ySpeed;
                    break;

                default:
                    projX = x;
                    projY = y;
                    projXSpeed = xSpeed;
                    projYSpeed = ySpeed;
                    break;
            }

            // Add new shot.
            shots.add(new Projectile(direction, projX, projY, xSpeed, ySpeed));
            soundManager.playShot();

            // Reset shot cooldown.
            shotCooldown = firingSpeed;
        }

        shotCooldown--;
    }

    // Event API
    // =========
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
                default:
                    System.out.println("Tank::update ==> Error: invalid KeyEvent type encountered.");
                    break;
            }
        }
    }
    
    public void goHome() {
        x = originX;
        y = originY;
    }
    
    public void powerUp(PowerUp p){
        switch(p.type){
            case life:
                this.lives++;
                break;
            case damage:
                if(firingSpeed >= 6)
                    firingSpeed -= 5;
                break;
            case speed:
                this.speed++;
                break;
        }
    }
}
