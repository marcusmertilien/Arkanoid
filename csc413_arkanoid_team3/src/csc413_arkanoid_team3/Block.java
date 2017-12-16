package csc413_arkanoid_team3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;


public class Block extends Prop {

    private static String FILE_NAME = "blocks.png";

    private static int BLOCK_SPRITE_WIDTH = 16;      // width of stage area
    private static int BLOCK_SPRITE_HEIGHT = 8;     // height of stage area

    public static int BLOCK_WIDTH = 2*BLOCK_SPRITE_WIDTH;
    public static int BLOCK_HEIGHT = 2*BLOCK_SPRITE_HEIGHT;

    public static enum Types {
        WHITE, YELLOW, PINK, BLUE, RED, GREEN, CYAN, ORANGE, SILVER, GOLD
    }

    private static HashMap<Types, BufferedImage> blockAssetCollection;
    static {
        try {
            ClassLoader cl = GameEngine.class.getClassLoader();
            BufferedImage spriteMap = ImageIO.read(cl.getResource(GameEngine.GENERAL_ASSET_PATH + FILE_NAME));
            BufferedImage rawAsset, block;
            Image tempScaledImage;
            Graphics2D g2d;
            
            blockAssetCollection = new HashMap<Types, BufferedImage>();

            for (int i = 0; i < Types.values().length; i++) {
                int x = i * BLOCK_SPRITE_WIDTH;
                int y = 0;

                // Provide an image asset per enum type.
                rawAsset = spriteMap.getSubimage(x, y, BLOCK_SPRITE_WIDTH, BLOCK_SPRITE_HEIGHT);
                tempScaledImage = rawAsset.getScaledInstance(BLOCK_WIDTH, BLOCK_HEIGHT, Image.SCALE_SMOOTH);
                block = new BufferedImage(BLOCK_WIDTH, BLOCK_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                g2d = block.createGraphics();
                g2d.drawImage(tempScaledImage, 0, 0, null);

                blockAssetCollection.put(Types.values()[i], block);
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    // Class fields
    // ============

    private BufferedImage bgSprite;
    private Types type;


    // Constructors
    // ============

    public Block(int x, int y, Types type) {
        super(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
        this.type = type;
        this.sprite = blockAssetCollection.get(this.type);
    }
}
