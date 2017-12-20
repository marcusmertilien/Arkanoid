package csc413_arkanoid_team3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;


public class Ball extends Actor {

    private static int BALL_SPRITE_WIDTH = 4;
    private static int BALL_SPRITE_HEIGHT = 4;
    private static int BALL_WIDTH = 2*BALL_SPRITE_WIDTH;
    private static int BALL_HEIGHT = 2*BALL_SPRITE_HEIGHT;
    public static int BALL_SPEED = 1;

    private static String SPRITE_PATH = GameEngine.BALL_ASSET_PATH + "ball.png";
    private static BufferedImage ballAsset;
    static {

        // Init images assets
        // ==================
        BufferedImage rawAsset = AssetLoader.load(SPRITE_PATH, 1);
        ballAsset = AssetLoader.getScaledInstance(rawAsset, BALL_WIDTH, BALL_HEIGHT);

    }


    // Constructors
    // ============

    public Ball(int x, int y) {
        super(x, y, BALL_WIDTH, BALL_HEIGHT, 0, 0, BALL_SPEED);
        sprite = this.ballAsset;
    }

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
