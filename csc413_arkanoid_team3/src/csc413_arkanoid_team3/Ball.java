package csc413_arkanoid_team3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;


public class Ball extends Actor {

    // Class Constants
    // ===============

    private static final int BALL_SPRITE_WIDTH = 4;              // sprite asset width
    private static final int BALL_SPRITE_HEIGHT = 4;             // sprite asset height
    private static final int BALL_WIDTH = 2*BALL_SPRITE_WIDTH;   // visible ball width
    private static final int BALL_HEIGHT = 2*BALL_SPRITE_HEIGHT; // visible ball height
    private static final int BOUNCES_PER_SPEEDUP = 5;            // speed up marker
    public static final int BALL_SPEED = 3;                      // default ball speed
    public static final int BALL_MAX_SPEED = 6;                  // the max ball speed


    // The class' static image assets
    private static final String SPRITE_PATH = GameEngine.BALL_ASSET_PATH + "ball.png";
    private static final BufferedImage BALL_ASSET;
    static {
        // Init images assets
        BufferedImage rawAsset = AssetLoader.load(SPRITE_PATH, 1);
        BALL_ASSET = AssetLoader.getScaledInstance(rawAsset, BALL_WIDTH, BALL_HEIGHT);
    }

    private int bounceCounter;


    // Constructors
    // ============

    public Ball(int x, int y) {
        super(x, y, BALL_WIDTH, BALL_HEIGHT, 0, 0, BALL_SPEED);

        this.sprite = this.BALL_ASSET;
        this.bounceCounter = 0;
    }


    // Public API
    // ==========

    public void update() {
        _updatePosition();
    }

    private void _updatePosition() {
        // Store previous location.
        previousX = x;
        previousY = y;

        // Update location.
        x += xSpeed;
        y += ySpeed;
    }
    
    
    public void resetLocationE(){
        x = previousX;
        y = previousY;
    }

    public void incrementBounce() {
        // Increase the ball speed per bounce count.
        if (
            bounceCounter++ % BOUNCES_PER_SPEEDUP == 0 &&
            Math.abs(ySpeed) <= BALL_MAX_SPEED
        ) {
            ySpeed = (ySpeed >=0) ? ySpeed + 1: ySpeed - 1;
        }
    }

}
