package csc413_arkanoid_team3;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.HashMap;

public class Enemy extends Prop {

    // Class Constants
    // ===============

    public enum Types {
        GREEN, RED, BLUE
    }

    private static final int ENEMY_WIDTH = 32;  // width of stage area
    private static final int ENEMY_HEIGHT = 32; // height of stage area
    private static final int ANIMATION_COOLDOWN = 96;  // animation max time out.

    // The class' static image assets
    private static final String SPRITE_PATH = GameEngine.GENERAL_ASSET_PATH + "enemies.png";
    private static final HashMap<Types, BufferedImage> ASSET_MAP;
    static {
        // Init images assets
        BufferedImage spriteSheet = AssetLoader.load(SPRITE_PATH, 1);
        ASSET_MAP = new HashMap<Types, BufferedImage>();

        // Build a map of block types to image assets.
        for (int i = 0; i < Types.values().length; i++) {
            BufferedImage Enemy = spriteSheet.getSubimage(0, i*ENEMY_HEIGHT, spriteSheet.getWidth(), ENEMY_HEIGHT);
            ASSET_MAP.put(Types.values()[i], Enemy);
        }
    }


    // Class Fields
    // ============

    private int animationTimer = ANIMATION_COOLDOWN; // animation timer for roll effect
    private BufferedImage assetRow;                  // the animation asset row
    private int spriteAnimationX;                    // the current x positon of the animation
    private Boolean isDestroyed;                     // if this is still a valid enemy
    public Types type;                               // the enemy's type


    // Constructors
    // ============

    public Enemy(int x, int y, Types type) {
        super(x, y, ENEMY_WIDTH, ENEMY_HEIGHT);

        this.type = type;
        this.assetRow = ASSET_MAP.get(this.type);
        this.sprite = this.assetRow.getSubimage(0, 0, ENEMY_WIDTH, ENEMY_HEIGHT);
        this.isDestroyed = false;
    }


    // Public API
    // ==========

    public void update() {
        if (!isDestroyed) {
            _updateLive();
        } else {
            _updateDead();
        }
    }

    public void _updateLive() {
        // Decrement timer.
        --animationTimer;

        if (animationTimer <= 0) {
            // Reset animation frame.
            animationTimer = ANIMATION_COOLDOWN;
            spriteAnimationX = 0;
            this.sprite = this.assetRow.getSubimage(spriteAnimationX, 0, ENEMY_WIDTH, ENEMY_HEIGHT);
        } else if (animationTimer%8 == 0) {
            // Move to next sprite.
            spriteAnimationX += ENEMY_WIDTH;
            this.sprite = this.assetRow.getSubimage(spriteAnimationX, 0, ENEMY_WIDTH, ENEMY_HEIGHT);
        }
    }

    public void _updateDead() {
        // Decrement timer.
        --animationTimer;

        if (animationTimer <= 0) {
            // Hide enemy for removal.
            this.hide();
        } else if (animationTimer%8 == 0) {
            // Move to next sprite.
            spriteAnimationX += ENEMY_WIDTH;
            this.sprite = this.assetRow.getSubimage(spriteAnimationX, 0, ENEMY_WIDTH, ENEMY_HEIGHT);
        }
    }

    public void registerHit() {
        isDestroyed = true;
        animationTimer = 6*8;
        spriteAnimationX = 12*ENEMY_WIDTH;
    }
}
