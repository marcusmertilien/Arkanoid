package csc413_tankgame_team03;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;


public class Stage {

    // Constants
    // =========

    private static String SPRITE_PATH = "background-sprite-sheet.png";
    private static int BG_WIDTH = 224;
    private static int BG_HEIGHT = 240;
    private static int SPACER_WIDTH = 8;
    private static int SPACE_HEIGHT = 20;


    // Class statics
    // =============

    // enum dictating the round the stage represents
    public static enum Rounds {
        ROUND_1, ROUND_2, ROUND_3, ROUND_4, ROUND_5
    }
    
    // Static hash map for stage bg assets
    private static HashMap<Rounds, BufferedImage> backgroundCollection;
    static {
        try {
            ClassLoader cl = Tank.class.getClassLoader();
            BufferedImage spriteMap = ImageIO.read(cl.getResource(GameEngine.STAGE_BG_PATH + SPRITE_PATH));
            
            backgroundCollection = new HashMap<Rounds, BufferedImage>();

            int roundCount = Rounds.values().length;
            for (int i = 0; i < roundCount; i++) {
                int x = i * BG_WIDTH;
                int y = 0;

                // Account for spacers in sprite sheet.
                if (i > 0) x += i*SPACER_WIDTH;

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
