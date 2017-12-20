package csc413_arkanoid_team3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.HashMap;
import javax.imageio.ImageIO;


public class Block extends Prop {

    private static String FILE_NAME = "blocks.png";

    // Dimensional constants.
    private static int BLOCK_SPRITE_WIDTH = 16;             // width of stage area
    private static int BLOCK_SPRITE_HEIGHT = 8;             // height of stage area
    public static int BLOCK_WIDTH = 2*BLOCK_SPRITE_WIDTH;
    public static int BLOCK_HEIGHT = 2*BLOCK_SPRITE_HEIGHT;

    // Possible block types.
    public static enum Types {
        WHITE, YELLOW, PINK, BLUE, RED, GREEN, CYAN, ORANGE, SILVER, GOLD
    }

    private static HashMap<Types, Integer> pointMap;
    private static HashMap<Types, Integer> hitPointsMap;
    private static HashMap<Types, BufferedImage> assetMap;
    static {
        try {
            // Init point data
            // ===============
            pointMap = new HashMap<Types, Integer>();
            pointMap.put(Types.WHITE, 50);
            pointMap.put(Types.ORANGE, 60);
            pointMap.put(Types.CYAN, 70);
            pointMap.put(Types.GREEN, 80);
            pointMap.put(Types.RED, 90);
            pointMap.put(Types.BLUE, 100);
            pointMap.put(Types.PINK, 110);
            pointMap.put(Types.YELLOW, 120);
            pointMap.put(Types.SILVER, 50);
            pointMap.put(Types.GOLD, 0);

            hitPointsMap = new HashMap<Types, Integer>();
            hitPointsMap.put(Types.WHITE, 1);
            hitPointsMap.put(Types.ORANGE, 1);
            hitPointsMap.put(Types.CYAN, 1);
            hitPointsMap.put(Types.GREEN, 1);
            hitPointsMap.put(Types.RED, 1);
            hitPointsMap.put(Types.BLUE, 1);
            hitPointsMap.put(Types.PINK, 1);
            hitPointsMap.put(Types.YELLOW, 1);
            hitPointsMap.put(Types.SILVER, 2);
            hitPointsMap.put(Types.GOLD, -1); // Gold blocks are indestructible.

            // Init images assets
            // ==================
            ClassLoader cl = GameEngine.class.getClassLoader();
            BufferedImage spriteSheet;
            BufferedImage rawAsset, block;
            Image tempScaledImage;
            Graphics2D g2d;

            rawAsset = ImageIO.read(cl.getResource(GameEngine.GENERAL_ASSET_PATH + FILE_NAME));
            tempScaledImage = rawAsset.getScaledInstance(2*rawAsset.getWidth(), 2*rawAsset.getHeight(), Image.SCALE_SMOOTH);
            spriteSheet =  new BufferedImage(2*rawAsset.getWidth(), 2*rawAsset.getHeight(), Image.SCALE_SMOOTH);
            g2d = spriteSheet.createGraphics();
            g2d.drawImage(tempScaledImage, 0, 0, null);

            assetMap = new HashMap<Types, BufferedImage>();

            for (int i = 0; i < Types.values().length; i++) {
                block = spriteSheet.getSubimage(i*BLOCK_WIDTH, 0, BLOCK_WIDTH, BLOCK_HEIGHT);
                assetMap.put(Types.values()[i], block);
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    // Class fields
    // ============

    private BufferedImage bgSprite;
    public Types type;
    public int points;
    public int hitPoints;

    // TODO: each block might contain a powerup up that releases on destroy.
    // private PowerUp powerUp;


    // Constructors
    // ============

    public Block(int x, int y, Types type) {
        super(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);

        this.type = type;
        this.sprite = assetMap.get(this.type);
        this.points = pointMap.get(this.type);
        this.hitPoints = hitPointsMap.get(this.type);
        // this.powerUp = ...;
    }

    public int registerHit() {
        if (--this.hitPoints == 0) {
            this.hide();
            
            return this.points;
        }

        return 0;
    }
}
