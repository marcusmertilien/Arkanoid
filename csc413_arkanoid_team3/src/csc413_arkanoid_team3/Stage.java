package csc413_arkanoid_team3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;


public class Stage {

    private static String SPRITE_PATH = "background-sprite-sheet.png";

    private static int BG_WIDTH = 224;      // width of stage area
    private static int BG_HEIGHT = 240;     // height of stage area
    private static int SPACER_WIDTH = 8;    // horizonatal spacer between stage bgs
    private static int SPACE_HEIGHT = 20;   // vertical spacer between bgs

    public static enum Rounds {
        ROUND_1, ROUND_2, ROUND_3, ROUND_4, ROUND_5
    }

    private static HashMap<Rounds, BufferedImage> backgroundCollection;
    static {
        try {
            ClassLoader cl = GameEngine.class.getClassLoader();
            BufferedImage spriteMap = ImageIO.read(cl.getResource(GameEngine.STAGE_BG_PATH + SPRITE_PATH));
            
            backgroundCollection = new HashMap<Rounds, BufferedImage>();

            for (int i = 0; i < Rounds.values().length; i++) {
                int x = i * BG_WIDTH;
                int y = 0;

                // Account for spacers in sprite sheet.
                if (i > 0) x += i*SPACER_WIDTH;

                // Provide an image asset per enum type.
                backgroundCollection.put(Rounds.values()[i], spriteMap.getSubimage(x, y, BG_WIDTH, BG_HEIGHT));
            }

        } catch (Exception e) {
            System.out.print("No resources found\n");
        }
    }


    // Class fields
    // ============

    private BufferedImage bgSprite;
    private Rounds round;


    // Constructors
    // ============

    public Stage(Rounds round) {
        this.round = round;
        this.bgSprite = backgroundCollection.get(this.round);
    }

    public void draw(Graphics2D g2d) {
        _drawBackground(g2d);
        _drawBlocks(g2d);
        _drawPowerUps(g2d);
    }

    private void _drawBackground(Graphics2D g2d) {
        g2d.drawImage(this.bgSprite, 0, 0, null);
    }

    private void _drawBlocks(Graphics2D g2d) {
        // TODO draw blocks
    }

    private void _drawPowerUps(Graphics2D g2d) {
        // TODO draw powerups
    }

}
