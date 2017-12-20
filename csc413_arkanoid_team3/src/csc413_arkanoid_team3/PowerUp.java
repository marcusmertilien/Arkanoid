package csc413_arkanoid_team3;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.HashMap;

public class PowerUp extends Prop {

    // Class Constants
    // ===============

    public enum Types {
        LAZER, EXTEND, SLOW, CATCH, BREAK, DISRUPT, TWIN, NEWDISRUPT, PLAYER, REDUCE
    }

    private static final int POWERUP_SPRITE_WIDTH = 16;                // width of power up
    private static final int POWERUP_SPRITE_HEIGHT = 8;                // height of power up
    private static final int POWERUP_WIDTH = 2*POWERUP_SPRITE_WIDTH;   // visible width
    private static final int POWERUP_HEIGHT = 2*POWERUP_SPRITE_HEIGHT; // visible height
    private static final int ANIMATION_COOLDOWN = 64;                  // animation max time out.

    // The class' static image assets
    private static final String SPRITE_PATH = GameEngine.POWERUPS_ASSET_PATH + "powerups-sprite-sheet.png";
    private static final HashMap<Types, BufferedImage> ASSET_MAP;
    static {
        // Init images assets
        BufferedImage spriteSheet = AssetLoader.load(SPRITE_PATH, 2);
        ASSET_MAP = new HashMap<Types, BufferedImage>();

        for (int i = 0; i < Types.values().length; i++) {
            BufferedImage powerUp = spriteSheet.getSubimage(0, i*POWERUP_HEIGHT, spriteSheet.getWidth(), POWERUP_HEIGHT);
            ASSET_MAP.put(Types.values()[i], powerUp);
        }
    }


    // Class Fields
    // ============

    private int animationTimer = ANIMATION_COOLDOWN; // animation timer for roll effect
    private BufferedImage assetRow;                  // the animation asset row
    private int spriteAnimationX;                    // the current x positon of the animation
    public Types type;                               // the power up's type


    // Constructors
    // ============

    public PowerUp(int x, int y, Types type) {
        super(x, y, POWERUP_WIDTH, POWERUP_HEIGHT);
        this.type = type;
        this.assetRow = ASSET_MAP.get(this.type);
        this.sprite = this.assetRow.getSubimage(0, 0, POWERUP_WIDTH, POWERUP_HEIGHT);
    }

    public void update() {
        // Decrement timer.
        --animationTimer;

        if (animationTimer <= 0) {
            // Reset animation frame.
            animationTimer = ANIMATION_COOLDOWN;
            spriteAnimationX = 0;
            this.sprite = this.assetRow.getSubimage(spriteAnimationX, 0, POWERUP_WIDTH, POWERUP_HEIGHT);
        } else if (animationTimer%8 == 0) {
            // Move to next spriate.
            spriteAnimationX += POWERUP_WIDTH;
            this.sprite = this.assetRow.getSubimage(spriteAnimationX, 0, POWERUP_WIDTH, POWERUP_HEIGHT);
        }

        // Powerups move to the bottom of the screen.
        this.y++;
        
        
        
    }
}
