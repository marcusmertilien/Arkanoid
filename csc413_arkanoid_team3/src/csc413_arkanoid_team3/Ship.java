package csc413_arkanoid_team3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;


public class Ship extends Actor {

    private static String SPRITE_PATH = "ship-sprite-map.png";
    private static BufferedImage shipAsset;

    private static int BASE_SHIP_WIDTH = 32;
    private static int BASE_SHIP_HEIGHT = 8;

    private static int SHIP_WIDTH = 2*BASE_SHIP_WIDTH;
    private static int SHIP_HEIGHT = 2*BASE_SHIP_HEIGHT;

    private static int SPEED = 5;

    static {
        try {
            ClassLoader cl = GameEngine.class.getClassLoader();
            BufferedImage spriteMap = ImageIO.read(cl.getResource(GameEngine.SHIP_PATH + SPRITE_PATH));
            BufferedImage rawAsset, ship;
            Image tempScaledImage;
            Graphics2D g2d;

            rawAsset = spriteMap.getSubimage(0, 64, BASE_SHIP_WIDTH, BASE_SHIP_HEIGHT);
            tempScaledImage = rawAsset.getScaledInstance(SHIP_WIDTH, SHIP_HEIGHT, Image.SCALE_SMOOTH);
            shipAsset = new BufferedImage(SHIP_WIDTH, SHIP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            g2d = shipAsset.createGraphics();
            g2d.drawImage(tempScaledImage, 0, 0, null);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    public Ship(int x, int y) {
        super(x, y, SHIP_WIDTH, SHIP_HEIGHT, 0, 0, SPEED);
        sprite = this.shipAsset;
    }

}
