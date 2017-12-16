package csc413_arkanoid_team3;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.HashMap;

public class PowerUp extends Prop {

    public Types type;
    public enum Types {
        LAZER, EXTEND, SLOW, CATCH, BREAK, DISRUPT, T, N, P, R
    }

    private static int POWERUP_SPRITE_WIDTH = 16;             // width of stage area
    private static int POWERUP_SPRITE_HEIGHT = 8;             // height of stage area
    public static int POWERUP_WIDTH = 2*POWERUP_SPRITE_WIDTH;
    public static int POWERUP_HEIGHT = 2*POWERUP_SPRITE_HEIGHT;

    private static String FILE_NAME = "powerups-sprite-sheet.png";
    private static HashMap<Types, BufferedImage> assetMap;
    static {
        try {
            // Init images assets
            // ==================
            ClassLoader cl = GameEngine.class.getClassLoader();
            BufferedImage spriteSheet, rawAsset;
            Image tempScaledImage;
            Graphics2D g2d;

            rawAsset = ImageIO.read(cl.getResource(GameEngine.POWERUPS_ASSET_PATH + FILE_NAME));
            tempScaledImage = rawAsset.getScaledInstance(2*rawAsset.getWidth(), 2*rawAsset.getHeight(), BufferedImage.TYPE_INT_ARGB);
            spriteSheet =  new BufferedImage(2*rawAsset.getWidth(), 2*rawAsset.getHeight(), BufferedImage.TYPE_INT_ARGB);
            g2d = spriteSheet.createGraphics();
            g2d.drawImage(tempScaledImage, 0, 0, null);

            assetMap = new HashMap<Types, BufferedImage>();

            for (int i = 0; i < Types.values().length; i++) {
                BufferedImage powerUp = spriteSheet.getSubimage(0, i*POWERUP_HEIGHT, spriteSheet.getWidth(), POWERUP_HEIGHT);
                assetMap.put(Types.values()[i], powerUp);
            }

        } catch (Exception e) {
            System.out.print("No resources found\n");
        }
    }

    private static final int ANIMATION_COOLDOWN = 64;
    private int animationTimer = ANIMATION_COOLDOWN;
    private BufferedImage assetRow;
    int spriteX;

    public PowerUp(int x, int y, Types type) {
        super(x, y, POWERUP_WIDTH, POWERUP_HEIGHT);
        this.type = type;
        this.assetRow = assetMap.get(this.type);
        this.sprite = this.assetRow.getSubimage(0, 0, POWERUP_WIDTH, POWERUP_HEIGHT);
    }

    public void update() {
        // Decrement timer.
        --animationTimer;

        if (animationTimer <= 0) {
            // Reset animation frame.
            animationTimer = ANIMATION_COOLDOWN;
            spriteX = 0;
            this.sprite = this.assetRow.getSubimage(spriteX, 0, POWERUP_WIDTH, POWERUP_HEIGHT);
        } else if (animationTimer%8 == 0) {
            // Move to next spriate.
            spriteX += POWERUP_WIDTH;
            this.sprite = this.assetRow.getSubimage(spriteX, 0, POWERUP_WIDTH, POWERUP_HEIGHT);
        }


    }
}
