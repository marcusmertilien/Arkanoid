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
    private static final int BALL_SPEED = 1;                     // default ball speed

    // The class' static image assets
    private static final String SPRITE_PATH = GameEngine.BALL_ASSET_PATH + "ball.png";
    private static final BufferedImage BALL_ASSET;
    static {
        // Init images assets
        BufferedImage rawAsset = AssetLoader.load(SPRITE_PATH, 1);
        BALL_ASSET = AssetLoader.getScaledInstance(rawAsset, BALL_WIDTH, BALL_HEIGHT);
    }


    // Constructors
    // ============

    public Ball(int x, int y) {
        super(x, y, BALL_WIDTH, BALL_HEIGHT, 0, 0, BALL_SPEED);

        this.sprite = this.BALL_ASSET;
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

}
