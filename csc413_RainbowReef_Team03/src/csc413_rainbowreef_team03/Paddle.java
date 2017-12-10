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


public class Paddle extends GameObject implements Observer {

    private HashMap<Controls, Boolean> buttonStates = new HashMap<Controls, Boolean>();
    private HashMap<Integer, Controls> controlMap;

    private int speed = 2;
    protected int score = 0;
    protected int health = 100;
    protected int lives = 3;
    private int direction = 0;
    private int shotCooldown = 0;
    private int startingX =0;
    private int startingY =0;

    private static final int FIRING_SPEED = 30;
    private static int TANK_SIZE = 30;

    private SoundManager soundManager;

    // Constructors
    // ============
    public Paddle(int x, int y, int speed, int size, String spritePath, HashMap<Integer, Controls> controls) {
        super(x, y, 0, 0, speed, true, true);
        startingX = x;
        startingY = y;
        _initControls(controls);
        _initSprite(spritePath, size);
        _initSoundManager();
    }

    private void _initSprite(String spritePath, int size) {

        try {
            ClassLoader cl = Paddle.class.getClassLoader();
            BufferedImage rawAsset = ImageIO.read(cl.getResource(spritePath));

            // Create temp scaled image.
            Image tempScaledImage = rawAsset.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            sprite = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = sprite.createGraphics();

            // Now store the scaled version as our asset.
            g2d.drawImage(tempScaledImage, 0, 0, null);
            g2d.dispose();

            // Set new width and height.
            width = size;
            height = size;

        } catch (Exception e) {
            System.out.print("Error: GameEngine._initSprite:: No resources found\n");
        }
    }

    private void _initControls(HashMap<Integer, Controls> controls) {
        controlMap = controls;

        // Create controls mapping.
        for (Controls c : controlMap.values()) {
            buttonStates.put(c, false);
        }
    }

    private void _initSoundManager() {
        soundManager = SoundManager.getInstance();
    }


    // Public API
    // ==========
    public void update(ArrayList<Projectile> shots) {
        _updateShoot(shots);
        _updatePosition();
        _updateDirection();
    }

    private void _updatePosition() {
        // X-axis movement.
        if (buttonStates.get(Controls.LEFT)) {
            xSpeed = -speed;
            prevX = x;
            x += xSpeed;
        } else if (buttonStates.get(Controls.RIGHT)) {
            xSpeed = speed;
            prevX = x;
            x += xSpeed;
        } else {
            xSpeed = 0;
            prevX = x;
        }

        // Y-axis movement.
        if (buttonStates.get(Controls.UP)) {
            ySpeed = -speed;
            prevY = y;
            y += ySpeed;
        } else if (buttonStates.get(Controls.DOWN)) {
            ySpeed = speed;
            prevY = y;
            y += ySpeed;
        } else {
            ySpeed = 0;
            prevY = y;
        }
    }

    private void _updateDirection() {

        // Set tank direction based on input.
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

            shots.add(new Projectile(direction, projX, projY, xSpeed, ySpeed));
            soundManager.playShot();

            shotCooldown = FIRING_SPEED;

            if (DebugState.showShotDebugActive) {
                System.out.printf(
                    "new shot with player <%d, %d> and proj <%d, %d>\n",
                    this.x, this.y, projX, projY
                );
            }
        }

        shotCooldown--;
    }


    // Draw API
    // ========
    @Override
    public void draw(Graphics2D g2d) {
        if (this.isVisible()) {
            if (DebugState.showBoundsActive) {
                _debugDraw(g2d);
            } else {
                _draw(g2d);
            }
        }
    }

    private void _debugDraw(Graphics2D g2d) {
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(direction), (x+width/2), (y+height/2));
        g2d.setTransform(at);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x, y, width, height);
        g2d.drawImage(sprite, x+1, y+1, width-2, height-2, null);
    }

    private void _draw(Graphics2D g2d) {
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(direction), (x+width/2), (y+height/2));
        g2d.setTransform(at);
        g2d.drawImage(sprite, x, y, width, height, null);
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
        x = startingX;
        y = startingY;
    }
}
