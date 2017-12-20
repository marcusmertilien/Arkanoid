package csc413_arkanoid_team3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;


public class Ship extends Actor {

    // Class Constants
    // ===============

    private static final int BASE_SHIP_WIDTH = 32;
    private static final int BASE_SHIP_HEIGHT = 8;
    private static int SHIP_WIDTH = 2*BASE_SHIP_WIDTH;
    private static int SHIP_HEIGHT = 2*BASE_SHIP_HEIGHT;
    
    

    private static final int SPEED = 3;

    // The class' static image assets
    private static final String SPRITE_PATH = GameEngine.SHIP_PATH + "ship-sprite-map.png";
    private static  BufferedImage SHIP_ASSET;
    static {
        // Init images assets
        BufferedImage spriteSheet = AssetLoader.load(SPRITE_PATH, 1);
        BufferedImage rawAsset = spriteSheet.getSubimage(0, 64, BASE_SHIP_WIDTH, BASE_SHIP_HEIGHT);
        SHIP_ASSET = AssetLoader.getScaledInstance(rawAsset, SHIP_WIDTH, SHIP_HEIGHT);
    }


    // Constructors
    // ============

    public Ship(int x, int y) {
        super(x, y, SHIP_WIDTH, SHIP_HEIGHT, 0, 0, SPEED);
        sprite = this.SHIP_ASSET;
    }
    
    public int getSpeed(){
        return SPEED;
    }
    public int getshipWidth(){
        return SHIP_WIDTH;
    }
    public int getshipHeight(){
        return SHIP_HEIGHT;
    }
    public void setshipWidth(int w){
        SHIP_WIDTH=w;
    }
    public void setshipHeight(int h){
        SHIP_HEIGHT=h;
    }
    public void reduce(){
        BufferedImage spriteSheet = AssetLoader.load(SPRITE_PATH, 1);
        BufferedImage rawAsset = spriteSheet.getSubimage(0, 64, BASE_SHIP_WIDTH, BASE_SHIP_HEIGHT);
        SHIP_ASSET = AssetLoader.getScaledInstance(rawAsset, BASE_SHIP_WIDTH, BASE_SHIP_HEIGHT);
        sprite = SHIP_ASSET;
    }
    
}
