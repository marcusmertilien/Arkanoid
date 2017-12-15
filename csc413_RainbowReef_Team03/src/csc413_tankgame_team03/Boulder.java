
package csc413_tankgame_team03;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;


public class Boulder extends Prop {

    // Boulder types.
    public Type type;
    public enum Type {
        DEFAULT, BREAKABLE
    }

    // Static image assets.
    private static BufferedImage defaultBoulder;
    private static BufferedImage breakableBoulder;
    static {
        try {
            ClassLoader cl = Tank.class.getClassLoader();
            BufferedImage rawAsset;
            Image tempScaledImage;
            Graphics2D g2d;
            int tileSize = GameEngine.TILE_SIZE;

            // Open and set normal boulder asset.
            rawAsset = ImageIO.read(cl.getResource(GameEngine.ENV_ASSET_PATH + "boulder.png"));
            tempScaledImage = rawAsset.getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH);
            defaultBoulder = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
            g2d = defaultBoulder.createGraphics();
            g2d.drawImage(tempScaledImage, 0, 0, null);
            g2d.dispose();

            // Open and set breakable boulder asset.
            rawAsset = ImageIO.read(cl.getResource(GameEngine.ENV_ASSET_PATH + "breakableBoulder.png"));
            tempScaledImage = rawAsset.getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH);
            breakableBoulder = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
            g2d = breakableBoulder.createGraphics();
            g2d.drawImage(tempScaledImage, 0, 0, null);
            g2d.dispose();
        } catch (Exception e) {
            System.out.print("No resources found\n");
        }
    }

    // Constructors
    // ============
    public Boulder(int x, int y, Type boulderType) {
        super(x, y, GameEngine.TILE_SIZE, GameEngine.TILE_SIZE);
        this.type = boulderType;
        this.sprite = (this.type == Type.DEFAULT) ? defaultBoulder : breakableBoulder;
    }


}
