package csc413_arkanoid_team3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.HashMap;
import javax.imageio.ImageIO;


public class Block extends Prop {

    // Class Constants
    // ===============

    // Dimensional constants.
    private static int BLOCK_SPRITE_WIDTH = 16;             // width of stage area
    private static int BLOCK_SPRITE_HEIGHT = 8;             // height of stage area
    public static int BLOCK_WIDTH = 2*BLOCK_SPRITE_WIDTH;   // visible block width
    public static int BLOCK_HEIGHT = 2*BLOCK_SPRITE_HEIGHT; // visible block height

    // Possible block types.
    public static enum Types {
        WHITE, YELLOW, PINK, BLUE, RED, GREEN, CYAN, ORANGE, SILVER, GOLD
    }

    // The class' static data assets
    private static HashMap<Types, Integer> pointsMap;
    private static HashMap<Types, Integer> healthMap;

    // The class' static image assets
    private static HashMap<Types, BufferedImage> assetMap;
    private static String SPRITE_PATH = GameEngine.GENERAL_ASSET_PATH + "blocks.png";
    static {
        // Init point data
        pointsMap = new HashMap<Types, Integer>(){{
            put(Types.WHITE, 50);
            put(Types.ORANGE, 60);
            put(Types.CYAN, 70);
            put(Types.GREEN, 80);
            put(Types.RED, 90);
            put(Types.BLUE, 100);
            put(Types.PINK, 110);
            put(Types.YELLOW, 120);
            put(Types.SILVER, 50);
            put(Types.GOLD, 0);
        }};

        // Init health data
        healthMap = new HashMap<Types, Integer>(){{
            put(Types.WHITE, 1);
            put(Types.ORANGE, 1);
            put(Types.CYAN, 1);
            put(Types.GREEN, 1);
            put(Types.RED, 1);
            put(Types.BLUE, 1);
            put(Types.PINK, 1);
            put(Types.YELLOW, 1);
            put(Types.SILVER, 2);
            put(Types.GOLD, 4);
        }};

        // Init images assets
        BufferedImage spriteSheet = AssetLoader.load(SPRITE_PATH, 2);
        assetMap = new HashMap<Types, BufferedImage>();

        for (int i = 0; i < Types.values().length; i++) {
            BufferedImage block = spriteSheet.getSubimage(i*BLOCK_WIDTH, 0, BLOCK_WIDTH, BLOCK_HEIGHT);
            assetMap.put(Types.values()[i], block);
        }
    }


    // Class fields
    // ============

    private BufferedImage bgSprite;
    public Types type;
    private int points;
    private int hitPoints;


    // Constructors
    // ============

    public Block(int x, int y, Types type) {
        super(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);

        this.type = type;
        this.sprite = assetMap.get(this.type);
        this.points = pointsMap.get(this.type);
        this.hitPoints = healthMap.get(this.type);
    }

    public int registerHit() {
        if (--this.hitPoints == 0) {
            this.hide();
            return this.points;
        }

        return 0;
    }
}
