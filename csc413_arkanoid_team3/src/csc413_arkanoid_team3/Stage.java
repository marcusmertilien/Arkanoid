package csc413_arkanoid_team3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;


public class Stage {

    // Class Constants
    // ===============

    private static final int BG_SPRITE_WIDTH = 224;             // width of stage area
    private static final int BG_SPRITE_HEIGHT = 240;            // height of stage area
    private static final int SPACER_WIDTH = 8;                  // horizonatal spacer between stage bgs
    private static final int SPACE_HEIGHT = 20;                 // vertical spacer between bgs
    private static final int STAGE_WIDTH = 2*BG_SPRITE_WIDTH;   // visual stage width
    private static final int STAGE_HEIGHT = 2*BG_SPRITE_HEIGHT; // visual stage height

    // The rounds in the game
    public static enum Rounds {
        ROUND_1, ROUND_2, ROUND_3, ROUND_4, ROUND_5;
        private static Rounds[] vals = values();
        public Rounds next() {
            return vals[(this.ordinal()+1)%vals.length];
        }
    }

    // A hash map representing a relation between a string and a block type - used for level templating.
    private static final HashMap<String, Block.Types> map = new HashMap<String, Block.Types>(){{
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

    // A hash map representing the block layours of the stages.
    private static final HashMap<Rounds, String[][]> stageMap = new HashMap<Rounds, String[][]>(){{

        put(Rounds.ROUND_1, new String[][] {
            {"+", "+", "+", "+", "+", "+", "+", "+", "+", "+", "+", "+", "+"},
            {"r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r"},
            {"b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b"},
            {"o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o"},
            {"p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p"},
            {"g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "g"}
        });

        put(Rounds.ROUND_2, new String[][] {
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
        });
        put(Rounds.ROUND_3, new String[][] {
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
        });
        put(Rounds.ROUND_4, new String[][]{
            {"g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "g"}, {},
            {"w", "w", "w", "^", "^", "^", "^", "^", "^", "^", "^", "^", "^"}, {},
            {"r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r"}, {},
            {"^", "^", "^", "^", "^", "^", "^", "^", "^", "^", "w", "w", "w"}, {},
            {"p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p"}, {},
            {"b", "b", "b", "^", "^", "^", "^", "^", "^", "^", "^", "^", "^"}, {},
            {"b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b"}, {},
            {"^", "^", "^", "^", "^", "^", "^", "^", "^", "^", "b", "b", "b"},
        });
    }};

    // The class' static image assets
    private static final String SPRITE_PATH = GameEngine.STAGE_BG_PATH + "background-sprite-sheet.png";
    private static final HashMap<Rounds, BufferedImage> ASSET_MAP;
    static {
        // Init images assets
        BufferedImage spriteSheet = AssetLoader.load(SPRITE_PATH, 1);
        ASSET_MAP = new HashMap<Rounds, BufferedImage>();

        // For each round, crop one sprite from the sheet and store it.
        for (int i = 0; i < Rounds.values().length; i++) {
            int x = i * BG_SPRITE_WIDTH;
            int y = 0;

            // Account for spacers in sprite sheet.
            if (i > 0) x += i*SPACER_WIDTH;

            // Provide an image asset per enum type.
            BufferedImage rawAsset = spriteSheet.getSubimage(x, y, BG_SPRITE_WIDTH, BG_SPRITE_HEIGHT);
            BufferedImage stageAsset = AssetLoader.getScaledInstance(rawAsset, STAGE_WIDTH, STAGE_HEIGHT);
            ASSET_MAP.put(Rounds.values()[i], stageAsset);
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
        this.bgSprite = ASSET_MAP.get(this.round);

        // Test data will use a data collection for the setup.
        this.blocks = new ArrayList<Block>();
        String[][] stage = stageMap.get(this.round);

        // Construct block layout.
        for (int i = 0; i < stage.length; i++) {
            for (int j = 0; j < stage[i].length; j++) {
                int x = 16 + (j*Block.BLOCK_WIDTH);
                int y = 60 + (i*Block.BLOCK_HEIGHT);

                if (map.get(stage[i][j]) != null) {
                    blocks.add(new Block(x, y, map.get(stage[i][j])));
                }
            }
        }
    }


    // Public API
    // ==========

    public void draw(Graphics2D g2d) {
        _drawBackground(g2d);
        _drawBlocks(g2d);
    }

    private void _drawBackground(Graphics2D g2d) {
        g2d.drawImage(this.bgSprite, 0, 0, null);
    }

    private void _drawBlocks(Graphics2D g2d) {
        for (Block _b : blocks) _b.draw(g2d);
    }

}
