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

    private static int BG_SPRITE_WIDTH = 224;      // width of stage area
    private static int BG_SPRITE_HEIGHT = 240;     // height of stage area
    private static int SPACER_WIDTH = 8;    // horizonatal spacer between stage bgs
    private static int SPACE_HEIGHT = 20;   // vertical spacer between bgs

    private static int STAGE_WIDTH = 2*BG_SPRITE_WIDTH;
    private static int STAGE_HEIGHT = 2*BG_SPRITE_HEIGHT;

    public static enum Rounds {
        ROUND_1, ROUND_2, ROUND_3, ROUND_4, ROUND_5
    }

    private static HashMap<String, Block.Types> map = new HashMap<String, Block.Types>(){{
        put("w", Block.Types.WHITE);
        put("y", Block.Types.YELLOW);
        put("p", Block.Types.PINK);
        put("b", Block.Types.BLUE);
        put("r", Block.Types.RED);
        put("g", Block.Types.GREEN);
        put("c", Block.Types.CYAN);
        put("o", Block.Types.ORANGE);
        put("+", Block.Types.SILVER);
        put("^", Block.Types.GOLD);
    }};

    private static String[][] round1map = {
        {"+", "+", "+", "+", "+", "+", "+", "+", "+", "+", "+", "+", "+"},
        {"r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r"},
        {"b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b"},
        {"o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o"},
        {"p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p"},
        {"g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "g"}
    };

    private static String[][] round2map = {
        {"p"},
        {"p", "c"},
        {"p", "c", "b"},
        {"p", "c", "b", "g"},
        {"p", "c", "b", "g", "r"},
        {"p", "c", "b", "g", "r", "p"},
        {"p", "c", "b", "g", "r", "p", "c"},
        {"p", "c", "b", "g", "r", "p", "c", "g"},
        {"p", "c", "b", "g", "r", "p", "c", "g", "b"},
        {"p", "c", "b", "g", "r", "p", "c", "g", "b", "r"},
        {"p", "c", "b", "g", "r", "p", "c", "g", "b", "r", "p"},
        {"p", "c", "b", "g", "r", "p", "c", "g", "b", "r", "p", "y"},
        {"p", "c", "b", "g", "r", "p", "c", "g", "b", "r", "p", "y", "p"},
        {"+", "+", "+", "+", "+", "+", "+", "+", "+", "+", "+", "+", "p"}
    };

    private static String[][] round3map = {
        {"w", "y", "p", "b", "r", "g", "c", "o", "+", "^", "w", "y", "p"},
        {"w", "y", "p", "b", "r", "g", "c", "o", "+", "^", "w", "y", "p"},
        {"w", "y", "p", "b", "r", "g", "c", "o", "+", "^", "w", "y", "p"},
        {"w", "y", "p", "b", "r", "g", "c", "o", "+", "^", "w", "y", "p"},
        {"w", "y", "p", "b", "r", "g", "c", "o", "+", "^", "w", "y", "p"},
        {"w", "y", "p", "b", "r", "g", "c", "o", "+", "^", "w", "y", "p"},
        {"w", "y", "p", "b", "r", "g", "c", "o", "+", "^", "w", "y", "p"},
        {"w", "y", "p", "b", "r", "g", "c", "o", "+", "^", "w", "y", "p"},
        {"w", "y", "p", "b", "r", "g", "c", "o", "+", "^", "w", "y", "p"},
        {"w", "y", "p", "b", "r", "g", "c", "o", "+", "^", "w", "y", "p"},
        {"w", "y", "p", "b", "r", "g", "c", "o", "+", "^", "w", "y", "p"}
    };

    private static String[][] round4map = {
        {"g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "g"}, {},
        {"w", "w", "w", "^", "^", "^", "^", "^", "^", "^", "^", "^", "^"}, {},
        {"r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r"}, {},
        {"^", "^", "^", "^", "^", "^", "^", "^", "^", "^", "w", "w", "w"}, {},
        {"p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p"}, {},
        {"b", "b", "b", "^", "^", "^", "^", "^", "^", "^", "^", "^", "^"}, {},
        {"b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b"}, {},
        {"^", "^", "^", "^", "^", "^", "^", "^", "^", "^", "b", "b", "b"},
    };

    private static HashMap<Rounds, String[][]> stageMap = new HashMap<Rounds, String[][]>(){{
        put(Rounds.ROUND_1, round1map);
        put(Rounds.ROUND_2, round2map);
        put(Rounds.ROUND_3, round3map);
        put(Rounds.ROUND_4, round4map);
    }};

    private static HashMap<Rounds, BufferedImage> backgroundCollection;
    static {
        try {
            ClassLoader cl = GameEngine.class.getClassLoader();
            BufferedImage spriteMap = ImageIO.read(cl.getResource(GameEngine.STAGE_BG_PATH + SPRITE_PATH));
            BufferedImage rawAsset, bgMap;
            Image tempScaledImage;
            Graphics2D g2d;
            
            backgroundCollection = new HashMap<Rounds, BufferedImage>();

            for (int i = 0; i < Rounds.values().length; i++) {
                int x = i * BG_SPRITE_WIDTH;
                int y = 0;

                // Account for spacers in sprite sheet.
                if (i > 0) x += i*SPACER_WIDTH;

                // Provide an image asset per enum type.
                rawAsset = spriteMap.getSubimage(x, y, BG_SPRITE_WIDTH, BG_SPRITE_HEIGHT);
                tempScaledImage = rawAsset.getScaledInstance(STAGE_WIDTH, STAGE_HEIGHT, Image.SCALE_SMOOTH);
                bgMap = new BufferedImage(STAGE_WIDTH, STAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                g2d = bgMap.createGraphics();
                g2d.drawImage(tempScaledImage, 0, 0, null);

                backgroundCollection.put(Rounds.values()[i], bgMap);
            }

        } catch (Exception e) {
            System.out.print("No resources found\n");
        }
    }


    // Class fields
    // ============

    private BufferedImage bgSprite;

    public Rounds round;
    public ArrayList<Block> blocks;


    // Constructors
    // ============

    public Stage(Rounds round) {
        this.round = round;
        this.bgSprite = backgroundCollection.get(this.round);

        // Test data will use a data collection for the setup.
        this.blocks = new ArrayList<Block>();
        String[][] stage = stageMap.get(this.round);

        for (int i = 0; i < stage.length; i++) {
            for (int j = 0; j < stage[i].length; j++) {
                int x = 16 + (j*Block.BLOCK_WIDTH);
                int y = 60 + (i*Block.BLOCK_HEIGHT);
                blocks.add(new Block(x, y, map.get(stage[i][j])));
            }
        }
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
        for (Block _b : blocks) _b.draw(g2d);
    }

    private void _drawPowerUps(Graphics2D g2d) {
        // TODO draw powerups
    }

}
